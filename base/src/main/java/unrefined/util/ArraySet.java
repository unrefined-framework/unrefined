/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package unrefined.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import unrefined.util.ArrayMap.MapCollections;

/**
 * ArraySet is a generic set data structure that is designed to be more memory efficient than a
 * traditional {@link java.util.HashSet}.  The design is very similar to
 * {@link ArrayMap}, with all of the caveats described there.  This implementation is
 * separate from ArrayMap, however, so the Object array contains only one item for each
 * entry in the set (instead of a pair for a mapping).
 *
 * <p>Note that this implementation is not intended to be appropriate for data structures
 * that may contain large numbers of items.  It is generally slower than a traditional
 * HashSet, since lookups require a binary search and adds and removes require inserting
 * and deleting entries in the array.  For containers holding up to hundreds of items,
 * the performance difference is not significant, less than 50%.</p>
 *
 * <p>Because this container is intended to better balance memory use, unlike most other
 * standard Java containers it will shrink its array as items are removed from it.  Currently
 * you have no control over this shrinking -- if you set a capacity and then remove an
 * item, it may reduce the capacity to better match the current size.  In the future an
 * explicit call to set the capacity should turn off this aggressive shrinking behavior.</p>
 *
 * <p>This structure is <b>NOT</b> thread-safe.</p>
 */
public final class ArraySet<E> implements Collection<E>, Set<E> {

    //private static final boolean DEBUG = false;
    //private static final String TAG = "ArraySet";

    /**
     * The minimum amount by which the capacity of a ArraySet will increase.
     * This is tuned to be relatively space-efficient.
     */
    private static final int BASE_SIZE = 4;

    /**
     * Maximum number of entries to have in array caches.
     */
    private static final int CACHE_SIZE = 10;

    /**
     * Caches of small array objects to avoid spamming garbage.  The cache
     * Object[] variable is a pointer to a linked list of array objects.
     * The first entry in the array is a pointer to the next array in the
     * list; the second entry is a pointer to the int[] hash code array for it.
     */
    private static Object[] baseCache;
    private static int baseCacheSize;
    private static Object[] twiceBaseCache;
    private static int twiceBaseCacheSize;
    /**
     * Separate locks for each cache since each can be accessed independently of the other without
     * risk of a deadlock.
     */
    private static final Object baseCacheLock = new Object();
    private static final Object twiceBaseCacheLock = new Object();

    private final boolean identityHashCode;
    private int[] hashes;
    private Object[] array;
    private int size;
    private MapCollections<E, E> collections;

    private int binarySearch(int[] hashes, int hash) {
        try {
            return FastArray.binarySearchUnchecked(hashes, 0, size, hash);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ConcurrentModificationException();
        }
    }

    private int indexOf(Object key, int hash) {
        final int N = size;

        // Important fast case: if nothing is in here, nothing to look for.
        if (N == 0) {
            return ~0;
        }

        int index = binarySearch(hashes, hash);

        // If the hash code wasn't found, then we have no entry for this key.
        if (index < 0) {
            return index;
        }

        // If the key at the returned index matches, that's what we want.
        if (key.equals(array[index])) {
            return index;
        }

        // Search for a matching key after the index.
        int end;
        for (end = index + 1; end < N && hashes[end] == hash; end++) {
            if (key.equals(array[end])) return end;
        }

        // Search for a matching key before the index.
        for (int i = index - 1; i >= 0 && hashes[i] == hash; i--) {
            if (key.equals(array[i])) return i;
        }

        // Key not found -- return negative value indicating where a
        // new entry for this key should go.  We use the end of the
        // hash chain to reduce the number of array entries that will
        // need to be copied when inserting.
        return ~end;
    }

    private int indexOfNull() {
        final int N = size;

        // Important fast case: if nothing is in here, nothing to look for.
        if (N == 0) {
            return ~0;
        }

        int index = binarySearch(hashes, 0);

        // If the hash code wasn't found, then we have no entry for this key.
        if (index < 0) {
            return index;
        }

        // If the key at the returned index matches, that's what we want.
        if (null == array[index]) {
            return index;
        }

        // Search for a matching key after the index.
        int end;
        for (end = index + 1; end < N && hashes[end] == 0; end++) {
            if (null == array[end]) return end;
        }

        // Search for a matching key before the index.
        for (int i = index - 1; i >= 0 && hashes[i] == 0; i--) {
            if (null == array[i]) return i;
        }

        // Key not found -- return negative value indicating where a
        // new entry for this key should go.  We use the end of the
        // hash chain to reduce the number of array entries that will
        // need to be copied when inserting.
        return ~end;
    }

    private void allocArrays(final int size) {
        if (size == (BASE_SIZE * 2)) {
            synchronized (twiceBaseCacheLock) {
                if (twiceBaseCache != null) {
                    final Object[] array = twiceBaseCache;
                    try {
                        this.array = array;
                        twiceBaseCache = (Object[]) array[0];
                        hashes = (int[]) array[1];
                        if (hashes != null) {
                            array[0] = array[1] = null;
                            twiceBaseCacheSize--;
                            //if (DEBUG) {
                            //    Log.d(TAG, "Retrieving 2x cache " + mHashes + " now have "
                            //            + sTwiceBaseCacheSize + " entries");
                            //}
                            return;
                        }
                    } catch (ClassCastException e) {
                    }
                    // Whoops!  Someone trampled the array (probably due to not protecting
                    // their access with a lock).  Our cache is corrupt; report and give up.
                    //Slog.wtf(TAG, "Found corrupt ArraySet cache: [0]=" + array[0]
                    //        + " [1]=" + array[1]);
                    System.err.println("ArraySet: Found corrupt ArraySet cache: [0]=" + array[0] + " [1]=" + array[1]);
                    twiceBaseCache = null;
                    twiceBaseCacheSize = 0;
                }
            }
        } else if (size == BASE_SIZE) {
            synchronized (baseCacheLock) {
                if (baseCache != null) {
                    final Object[] array = baseCache;
                    try {
                        this.array = array;
                        baseCache = (Object[]) array[0];
                        hashes = (int[]) array[1];
                        if (hashes != null) {
                            array[0] = array[1] = null;
                            baseCacheSize--;
                            //if (DEBUG) {
                            //    Log.d(TAG, "Retrieving 1x cache " + mHashes + " now have "
                            //            + sBaseCacheSize + " entries");
                            //}
                            return;
                        }
                    } catch (ClassCastException e) {
                    }
                    // Whoops!  Someone trampled the array (probably due to not protecting
                    // their access with a lock).  Our cache is corrupt; report and give up.
                    //Slog.wtf(TAG, "Found corrupt ArraySet cache: [0]=" + array[0]
                    //        + " [1]=" + array[1]);
                    System.err.println("ArraySet: Found corrupt ArraySet cache: [0]=" + array[0] + " [1]=" + array[1]);
                    baseCache = null;
                    baseCacheSize = 0;
                }
            }
        }

        hashes = new int[size];
        array = new Object[size];
    }

    /**
     * Make sure <b>NOT</b> to call this method with arrays that can still be modified. In other
     * words, don't pass mHashes or mArray in directly.
     */
    private static void freeArrays(final int[] hashes, final Object[] array, final int size) {
        if (hashes.length == (BASE_SIZE * 2)) {
            synchronized (twiceBaseCacheLock) {
                if (twiceBaseCacheSize < CACHE_SIZE) {
                    array[0] = twiceBaseCache;
                    array[1] = hashes;
                    for (int i = size - 1; i >= 2; i--) {
                        array[i] = null;
                    }
                    twiceBaseCache = array;
                    twiceBaseCacheSize++;
                    //if (DEBUG) {
                    //    Log.d(TAG, "Storing 2x cache " + array + " now have " + sTwiceBaseCacheSize
                    //            + " entries");
                    //}
                }
            }
        } else if (hashes.length == BASE_SIZE) {
            synchronized (baseCacheLock) {
                if (baseCacheSize < CACHE_SIZE) {
                    array[0] = baseCache;
                    array[1] = hashes;
                    for (int i = size - 1; i >= 2; i--) {
                        array[i] = null;
                    }
                    baseCache = array;
                    baseCacheSize++;
                    //if (DEBUG) {
                    //    Log.d(TAG, "Storing 1x cache " + array + " now have "
                    //            + sBaseCacheSize + " entries");
                    //}
                }
            }
        }
    }

    /**
     * Create a new empty ArraySet.  The default capacity of an array map is 0, and
     * will grow once items are added to it.
     */
    public ArraySet() {
        this(0, false);
    }

    /**
     * Create a new ArraySet with a given initial capacity.
     */
    public ArraySet(int capacity) {
        this(capacity, false);
    }

    private ArraySet(int capacity, boolean identityHashCode) {
        this.identityHashCode = identityHashCode;
        if (capacity == 0) {
            hashes = EmptyArray.INT;
            array = EmptyArray.OBJECT;
        } else {
            allocArrays(capacity);
        }
        size = 0;
    }

    /**
     * Create a new ArraySet with the mappings from the given ArraySet.
     */
    public ArraySet(ArraySet<E> set) {
        this();
        if (set != null) {
            addAll(set);
        }
    }

    /**
     * Create a new ArraySet with items from the given collection.
     */
    public ArraySet(Collection<? extends E> set) {
        this();
        if (set != null) {
            addAll(set);
        }
    }

    /**
     * Create a new ArraySet with items from the given array
     */
    public ArraySet(E[] array) {
        this();
        if (array != null) {
            for (E value : array) {
                add(value);
            }
        }
    }

    /**
     * Make the array map empty.  All storage is released.
     */
    @Override
    public void clear() {
        if (size != 0) {
            final int[] ohashes = hashes;
            final Object[] oarray = array;
            final int osize = size;
            hashes = EmptyArray.INT;
            array = EmptyArray.OBJECT;
            size = 0;
            freeArrays(ohashes, oarray, osize);
        }
        if (size != 0) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Ensure the array map can hold at least <var>minimumCapacity</var>
     * items.
     */
    public void ensureCapacity(int minimumCapacity) {
        final int oSize = size;
        if (hashes.length < minimumCapacity) {
            final int[] ohashes = hashes;
            final Object[] oarray = array;
            allocArrays(minimumCapacity);
            if (size > 0) {
                System.arraycopy(ohashes, 0, hashes, 0, size);
                System.arraycopy(oarray, 0, array, 0, size);
            }
            freeArrays(ohashes, oarray, size);
        }
        if (size != oSize) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Check whether a value exists in the set.
     *
     * @param key The value to search for.
     * @return Returns true if the value exists, else false.
     */
    @Override
    public boolean contains(Object key) {
        return indexOf(key) >= 0;
    }

    /**
     * Returns the index of a value in the set.
     *
     * @param key The value to search for.
     * @return Returns the index of the value if it exists, else a negative integer.
     */
    public int indexOf(Object key) {
        return key == null ? indexOfNull()
                : indexOf(key, identityHashCode ? System.identityHashCode(key) : key.hashCode());
    }

    /**
     * Return the value at the given index in the array.
     *
     * <p>For indices outside of the range <code>0...size()-1</code>, an
     * {@link ArrayIndexOutOfBoundsException} is thrown.</p>
     *
     * @param index The desired index, must be between 0 and {@link #size()}-1.
     * @return Returns the value stored at the given index.
     */
    public E valueAt(int index) {
        if (index >= size) {
            // The array might be slightly bigger than mSize, in which case, indexing won't fail.
            // Check if exception should be thrown outside of the critical path.
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return valueAtUnchecked(index);
    }

    /**
     * Returns the value at the given index in the array without checking that the index is within
     * bounds. This allows testing values at the end of the internal array, outside of the
     * [0, mSize) bounds.
     */
    private E valueAtUnchecked(int index) {
        return (E) array[index];
    }

    /**
     * Return true if the array map contains no items.
     */
    @Override
    public boolean isEmpty() {
        return size <= 0;
    }

    /**
     * Adds the specified object to this set. The set is not modified if it
     * already contains the object.
     *
     * @param value the object to add.
     * @return {@code true} if this set is modified, {@code false} otherwise.
     */
    @Override
    public boolean add(E value) {
        final int oSize = size;
        final int hash;
        int index;
        if (value == null) {
            hash = 0;
            index = indexOfNull();
        } else {
            hash = identityHashCode ? System.identityHashCode(value) : value.hashCode();
            index = indexOf(value, hash);
        }
        if (index >= 0) {
            return false;
        }

        index = ~index;
        if (oSize >= hashes.length) {
            final int n = oSize >= (BASE_SIZE * 2) ? (oSize + (oSize >> 1))
                    : (oSize >= BASE_SIZE ? (BASE_SIZE * 2) : BASE_SIZE);

            //if (DEBUG) Log.d(TAG, "add: grow from " + mHashes.length + " to " + n);

            final int[] ohashes = hashes;
            final Object[] oarray = array;
            allocArrays(n);

            if (oSize != size) {
                throw new ConcurrentModificationException();
            }

            if (hashes.length > 0) {
                //if (DEBUG) Log.d(TAG, "add: copy 0-" + oSize + " to 0");
                System.arraycopy(ohashes, 0, hashes, 0, ohashes.length);
                System.arraycopy(oarray, 0, array, 0, oarray.length);
            }

            freeArrays(ohashes, oarray, oSize);
        }

        if (index < oSize) {
            //if (DEBUG) {
            //    Log.d(TAG, "add: move " + index + "-" + (oSize - index) + " to " + (index + 1));
            //}
            System.arraycopy(hashes, index, hashes, index + 1, oSize - index);
            System.arraycopy(array, index, array, index + 1, oSize - index);
        }

        if (oSize != size || index >= hashes.length) {
            throw new ConcurrentModificationException();
        }

        hashes[index] = hash;
        array[index] = value;
        size++;
        return true;
    }

    /**
     * Perform a {@link #add(Object)} of all values in <var>array</var>
     * @param array The array whose contents are to be retrieved.
     */
    public void addAll(ArraySet<? extends E> array) {
        final int N = array.size;
        ensureCapacity(size + N);
        if (size == 0) {
            if (N > 0) {
                System.arraycopy(array.hashes, 0, hashes, 0, N);
                System.arraycopy(array.array, 0, this.array, 0, N);
                if (0 != size) {
                    throw new ConcurrentModificationException();
                }
                size = N;
            }
        } else {
            for (int i = 0; i < N; i++) {
                add(array.valueAt(i));
            }
        }
    }

    /**
     * Removes the specified object from this set.
     *
     * @param object the object to remove.
     * @return {@code true} if this set was modified, {@code false} otherwise.
     */
    @Override
    public boolean remove(Object object) {
        final int index = indexOf(object);
        if (index >= 0) {
            removeAt(index);
            return true;
        }
        return false;
    }

    /** Returns true if the array size should be decreased. */
    private boolean shouldShrink() {
        return hashes.length > (BASE_SIZE * 2) && size < hashes.length / 3;
    }

    /**
     * Returns the new size the array should have. Is only valid if {@link #shouldShrink} returns
     * true.
     */
    private int getNewShrunkenSize() {
        // We don't allow it to shrink smaller than (BASE_SIZE*2) to avoid flapping between that
        // and BASE_SIZE.
        return size > (BASE_SIZE * 2) ? (size + (size >> 1)) : (BASE_SIZE * 2);
    }

    /**
     * Remove the key/value mapping at the given index.
     *
     * <p>For indices outside of the range <code>0...size()-1</code>, an
     * {@link ArrayIndexOutOfBoundsException} is thrown.</p>
     *
     * @param index The desired index, must be between 0 and {@link #size()}-1.
     * @return Returns the value that was stored at this index.
     */
    public E removeAt(int index) {
        if (index >= size) {
            // The array might be slightly bigger than mSize, in which case, indexing won't fail.
            // Check if exception should be thrown outside of the critical path.
            throw new ArrayIndexOutOfBoundsException(index);
        }
        final int oSize = size;
        final Object old = array[index];
        if (oSize <= 1) {
            // Now empty.
            //if (DEBUG) Log.d(TAG, "remove: shrink from " + mHashes.length + " to 0");
            clear();
        } else {
            final int nSize = oSize - 1;
            if (shouldShrink()) {
                // Shrunk enough to reduce size of arrays.
                final int n = getNewShrunkenSize();

                //if (DEBUG) Log.d(TAG, "remove: shrink from " + mHashes.length + " to " + n);

                final int[] ohashes = hashes;
                final Object[] oarray = array;
                allocArrays(n);

                if (index > 0) {
                    //if (DEBUG) Log.d(TAG, "remove: copy from 0-" + index + " to 0");
                    System.arraycopy(ohashes, 0, hashes, 0, index);
                    System.arraycopy(oarray, 0, array, 0, index);
                }
                if (index < nSize) {
                    //if (DEBUG) {
                    //    Log.d(TAG, "remove: copy from " + (index + 1) + "-" + nSize
                    //            + " to " + index);
                    //}
                    System.arraycopy(ohashes, index + 1, hashes, index, nSize - index);
                    System.arraycopy(oarray, index + 1, array, index, nSize - index);
                }
            } else {
                if (index < nSize) {
                    //if (DEBUG) {
                    //    Log.d(TAG, "remove: move " + (index + 1) + "-" + nSize + " to " + index);
                    //}
                    System.arraycopy(hashes, index + 1, hashes, index, nSize - index);
                    System.arraycopy(array, index + 1, array, index, nSize - index);
                }
                array[nSize] = null;
            }
            if (oSize != size) {
                throw new ConcurrentModificationException();
            }
            size = nSize;
        }
        return (E) old;
    }

    /**
     * Perform a {@link #remove(Object)} of all values in <var>array</var>
     * @param array The array whose contents are to be removed.
     */
    public boolean removeAll(ArraySet<? extends E> array) {
        // TODO: If array is sufficiently large, a marking approach might be beneficial. In a first
        //       pass, use the property that the sets are sorted by hash to make this linear passes
        //       (except for hash collisions, which means worst case still n*m), then do one
        //       collection pass into a new array. This avoids binary searches and excessive memcpy.
        final int N = array.size;

        // Note: ArraySet does not make thread-safety guarantees. So instead of OR-ing together all
        //       the single results, compare size before and after.
        final int originalSize = size;
        for (int i = 0; i < N; i++) {
            remove(array.valueAt(i));
        }
        return originalSize != size;
    }

    /**
     * Return the number of items in this array map.
     */
    @Override
    public int size() {
        return size;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        System.arraycopy(array, 0, result, 0, size);
        return result;
    }

    @Override
    public <T> T[] toArray(T[] array) {
        if (array.length < size) {
            @SuppressWarnings("unchecked") T[] newArray =
                    (T[]) Array.newInstance(array.getClass().getComponentType(), size);
            array = newArray;
        }
        System.arraycopy(this.array, 0, array, 0, size);
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns false if the object is not a set, or
     * if the sets have different sizes.  Otherwise, for each value in this
     * set, it checks to make sure the value also exists in the other set.
     * If any value doesn't exist, the method returns false; otherwise, it
     * returns true.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Set) {
            Set<?> set = (Set<?>) o;
            if (size() != set.size()) {
                return false;
            }

            try {
                for (int i = 0; i < size; i++) {
                    E mine = valueAt(i);
                    if (!set.contains(mine)) {
                        return false;
                    }
                }
            } catch (NullPointerException ignored) {
                return false;
            } catch (ClassCastException ignored) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int[] hashes = this.hashes;
        int result = 0;
        for (int i = 0, s = size; i < s; i++) {
            result += hashes[i];
        }
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation composes a string by iterating over its values. If
     * this set contains itself as a value, the string "(this Set)"
     * will appear in its place.
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }

        StringBuilder buffer = new StringBuilder(size * 14);
        buffer.append('{');
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            Object value = valueAt(i);
            if (value != this) {
                buffer.append(value);
            } else {
                buffer.append("(this Set)");
            }
        }
        buffer.append('}');
        return buffer.toString();
    }

    // ------------------------------------------------------------------------
    // Interop with traditional Java containers.  Not as efficient as using
    // specialized collection APIs.
    // ------------------------------------------------------------------------

    private MapCollections<E, E> getCollection() {
        if (collections == null) {
            collections = new MapCollections<E, E>() {
                @Override
                protected int colGetSize() {
                    return size;
                }

                @Override
                protected Object colGetEntry(int index, int offset) {
                    return array[index];
                }

                @Override
                protected int colIndexOfKey(Object key) {
                    return indexOf(key);
                }

                @Override
                protected int colIndexOfValue(Object value) {
                    return indexOf(value);
                }

                @Override
                protected Map<E, E> colGetMap() {
                    throw new UnsupportedOperationException("not a map");
                }

                @Override
                protected void colPut(E key, E value) {
                    add(key);
                }

                @Override
                protected E colSetValue(int index, E value) {
                    throw new UnsupportedOperationException("not a map");
                }

                @Override
                protected void colRemoveAt(int index) {
                    removeAt(index);
                }

                @Override
                protected void colClear() {
                    clear();
                }
            };
        }
        return collections;
    }

    /**
     * Return an {@link Iterator} over all values in the set.
     *
     * <p><b>Note:</b> this is a fairly inefficient way to access the array contents, it
     * requires generating a number of temporary objects and allocates additional state
     * information associated with the container that will remain for the life of the container.</p>
     */
    @Override
    public Iterator<E> iterator() {
        return getCollection().getKeySet().iterator();
    }

    /**
     * Determine if the array set contains all of the values in the given collection.
     * @param collection The collection whose contents are to be checked against.
     * @return Returns true if this array set contains a value for every entry
     * in <var>collection</var>, else returns false.
     */
    @Override
    public boolean containsAll(Collection<?> collection) {
        Iterator<?> it = collection.iterator();
        while (it.hasNext()) {
            if (!contains(it.next())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Perform an {@link #add(Object)} of all values in <var>collection</var>
     * @param collection The collection whose contents are to be retrieved.
     */
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        ensureCapacity(size + collection.size());
        boolean added = false;
        for (E value : collection) {
            added |= add(value);
        }
        return added;
    }

    /**
     * Remove all values in the array set that exist in the given collection.
     * @param collection The collection whose contents are to be used to remove values.
     * @return Returns true if any values were removed from the array set, else false.
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean removed = false;
        for (Object value : collection) {
            removed |= remove(value);
        }
        return removed;
    }

    /**
     * Remove all values in the array set that do <b>not</b> exist in the given collection.
     * @param collection The collection whose contents are to be used to determine which
     * values to keep.
     * @return Returns true if any values were removed from the array set, else false.
     */
    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean removed = false;
        for (int i = size - 1; i >= 0; i--) {
            if (!collection.contains(array[i])) {
                removeAt(i);
                removed = true;
            }
        }
        return removed;
    }

}
