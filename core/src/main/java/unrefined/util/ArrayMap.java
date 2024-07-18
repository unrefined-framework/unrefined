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
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * ArrayMap is a generic key->value mapping data structure that is
 * designed to be more memory efficient than a traditional {@link java.util.HashMap}.
 * It keeps its mappings in an array data structure -- an integer array of hash
 * codes for each item, and an Object array of the key/value pairs.  This allows it to
 * avoid having to create an extra object for every entry put in to the map, and it
 * also tries to control the growth of the size of these arrays more aggressively
 * (since growing them only requires copying the entries in the array, not rebuilding
 * a hash map).
 *
 * <p>Note that this implementation is not intended to be appropriate for data structures
 * that may contain large numbers of items.  It is generally slower than a traditional
 * HashMap, since lookups require a binary search and adds and removes require inserting
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
public final class ArrayMap<K, V> implements Map<K, V> {

    //private static final boolean DEBUG = false;
    //private static final String TAG = "ArrayMap";

    /**
     * Attempt to spot concurrent modifications to this data structure.
     *
     * It's best-effort, but any time we can throw something more diagnostic than an
     * ArrayIndexOutOfBoundsException deep in the ArrayMap internals it's going to
     * save a lot of development time.
     *
     * Good times to look for CME include after any allocArrays() call and at the end of
     * functions that change mSize (put/remove/clear).
     */
    private static final boolean CONCURRENT_MODIFICATION_EXCEPTIONS = true;

    /**
     * The minimum amount by which the capacity of a ArrayMap will increase.
     * This is tuned to be relatively space-efficient.
     */
    private static final int BASE_SIZE = 4;

    /**
     * Maximum number of entries to have in array caches.
     */
    private static final int CACHE_SIZE = 10;

    /**
     * Special hash array value that indicates the container is immutable.
     */
    private static final int[] EMPTY_IMMUTABLE_INTS = new int[0];

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
    private MapCollections<K, V> collections;

    private static int binarySearchHashes(int[] hashes, int N, int hash) {
        try {
            return Arrays.binarySearchUnchecked(hashes, 0, N, hash);
        } catch (ArrayIndexOutOfBoundsException e) {
            if (CONCURRENT_MODIFICATION_EXCEPTIONS) {
                throw new ConcurrentModificationException();
            } else {
                throw e; // the cache is poisoned at this point, there's not much we can do
            }
        }
    }

    private int indexOf(Object key, int hash) {
        final int N = size;

        // Important fast case: if nothing is in here, nothing to look for.
        if (N == 0) {
            return ~0;
        }

        int index = binarySearchHashes(hashes, N, hash);

        // If the hash code wasn't found, then we have no entry for this key.
        if (index < 0) {
            return index;
        }

        // If the key at the returned index matches, that's what we want.
        if (key.equals(array[index<<1])) {
            return index;
        }

        // Search for a matching key after the index.
        int end;
        for (end = index + 1; end < N && hashes[end] == hash; end++) {
            if (key.equals(array[end << 1])) return end;
        }

        // Search for a matching key before the index.
        for (int i = index - 1; i >= 0 && hashes[i] == hash; i--) {
            if (key.equals(array[i << 1])) return i;
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

        int index = binarySearchHashes(hashes, N, 0);

        // If the hash code wasn't found, then we have no entry for this key.
        if (index < 0) {
            return index;
        }

        // If the key at the returned index matches, that's what we want.
        if (null == array[index<<1]) {
            return index;
        }

        // Search for a matching key after the index.
        int end;
        for (end = index + 1; end < N && hashes[end] == 0; end++) {
            if (null == array[end << 1]) return end;
        }

        // Search for a matching key before the index.
        for (int i = index - 1; i >= 0 && hashes[i] == 0; i--) {
            if (null == array[i << 1]) return i;
        }

        // Key not found -- return negative value indicating where a
        // new entry for this key should go.  We use the end of the
        // hash chain to reduce the number of array entries that will
        // need to be copied when inserting.
        return ~end;
    }

    private void allocArrays(final int size) {
        if (hashes == EMPTY_IMMUTABLE_INTS) {
            throw new UnsupportedOperationException("ArrayMap is immutable");
        }
        if (size == (BASE_SIZE*2)) {
            synchronized (twiceBaseCacheLock) {
                if (twiceBaseCache != null) {
                    final Object[] array = twiceBaseCache;
                    this.array = array;
                    try {
                        twiceBaseCache = (Object[]) array[0];
                        hashes = (int[]) array[1];
                        if (hashes != null) {
                            array[0] = array[1] = null;
                            twiceBaseCacheSize--;
                            //if (DEBUG) {
                            //    Log.d(TAG, "Retrieving 2x cache " + mHashes
                            //            + " now have " + mTwiceBaseCacheSize + " entries");
                            //}
                            return;
                        }
                    } catch (ClassCastException e) {
                    }
                    // Whoops!  Someone trampled the array (probably due to not protecting
                    // their access with a lock).  Our cache is corrupt; report and give up.
                    //Slog.wtf(TAG, "Found corrupt ArrayMap cache: [0]=" + array[0]
                    //        + " [1]=" + array[1]);
                    System.err.println("ArrayMap: Found corrupt ArrayMap cache: [0]=" + array[0] + " [1]=" + array[1]);
                    twiceBaseCache = null;
                    twiceBaseCacheSize = 0;
                }
            }
        } else if (size == BASE_SIZE) {
            synchronized (baseCacheLock) {
                if (baseCache != null) {
                    final Object[] array = baseCache;
                    this.array = array;
                    try {
                        baseCache = (Object[]) array[0];
                        hashes = (int[]) array[1];
                        if (hashes != null) {
                            array[0] = array[1] = null;
                            baseCacheSize--;
                            //if (DEBUG) {
                            //    Log.d(TAG, "Retrieving 1x cache " + mHashes
                            //            + " now have " + mBaseCacheSize + " entries");
                            //}
                            return;
                        }
                    } catch (ClassCastException e) {
                    }
                    // Whoops!  Someone trampled the array (probably due to not protecting
                    // their access with a lock).  Our cache is corrupt; report and give up.
                    //Slog.wtf(TAG, "Found corrupt ArrayMap cache: [0]=" + array[0]
                    //        + " [1]=" + array[1]);
                    System.err.println("ArrayMap: Found corrupt ArrayMap cache: [0]=" + array[0] + " [1]=" + array[1]);
                    baseCache = null;
                    baseCacheSize = 0;
                }
            }
        }

        hashes = new int[size];
        array = new Object[size<<1];
    }

    /**
     * Make sure <b>NOT</b> to call this method with arrays that can still be modified. In other
     * words, don't pass mHashes or mArray in directly.
     */
    private static void freeArrays(final int[] hashes, final Object[] array, final int size) {
        if (hashes.length == (BASE_SIZE*2)) {
            synchronized (twiceBaseCacheLock) {
                if (twiceBaseCacheSize < CACHE_SIZE) {
                    array[0] = twiceBaseCache;
                    array[1] = hashes;
                    for (int i=(size<<1)-1; i>=2; i--) {
                        array[i] = null;
                    }
                    twiceBaseCache = array;
                    twiceBaseCacheSize++;
                    //if (DEBUG) Log.d(TAG, "Storing 2x cache " + array
                    //        + " now have " + mTwiceBaseCacheSize + " entries");
                }
            }
        } else if (hashes.length == BASE_SIZE) {
            synchronized (baseCacheLock) {
                if (baseCacheSize < CACHE_SIZE) {
                    array[0] = baseCache;
                    array[1] = hashes;
                    for (int i=(size<<1)-1; i>=2; i--) {
                        array[i] = null;
                    }
                    baseCache = array;
                    baseCacheSize++;
                    //if (DEBUG) Log.d(TAG, "Storing 1x cache " + array
                    //        + " now have " + mBaseCacheSize + " entries");
                }
            }
        }
    }

    /**
     * Create a new empty ArrayMap.  The default capacity of an array map is 0, and
     * will grow once items are added to it.
     */
    public ArrayMap() {
        this(0, false);
    }

    /**
     * Create a new ArrayMap with a given initial capacity.
     */
    public ArrayMap(int capacity) {
        this(capacity, false);
    }

    private ArrayMap(int capacity, boolean identityHashCode) {
        this.identityHashCode = identityHashCode;

        // If this is immutable, use the sentinal EMPTY_IMMUTABLE_INTS
        // instance instead of the usual EmptyArray.INT. The reference
        // is checked later to see if the array is allowed to grow.
        if (capacity < 0) {
            hashes = EMPTY_IMMUTABLE_INTS;
            array = EmptyArray.OBJECT;
        } else if (capacity == 0) {
            hashes = EmptyArray.INT;
            array = EmptyArray.OBJECT;
        } else {
            allocArrays(capacity);
        }
        size = 0;
    }

    /**
     * Create a new ArrayMap with the mappings from the given ArrayMap.
     */
    public ArrayMap(ArrayMap<K, V> map) {
        this();
        if (map != null) {
            putAll(map);
        }
    }

    /**
     * Make the array map empty.  All storage is released.
     */
    @Override
    public void clear() {
        if (size > 0) {
            final int[] ohashes = hashes;
            final Object[] oarray = array;
            final int osize = size;
            hashes = EmptyArray.INT;
            array = EmptyArray.OBJECT;
            size = 0;
            freeArrays(ohashes, oarray, osize);
        }
        if (CONCURRENT_MODIFICATION_EXCEPTIONS && size > 0) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Ensure the array map can hold at least <var>minimumCapacity</var>
     * items.
     */
    public void ensureCapacity(int minimumCapacity) {
        final int osize = size;
        if (hashes.length < minimumCapacity) {
            final int[] ohashes = hashes;
            final Object[] oarray = array;
            allocArrays(minimumCapacity);
            if (size > 0) {
                System.arraycopy(ohashes, 0, hashes, 0, osize);
                System.arraycopy(oarray, 0, array, 0, osize<<1);
            }
            freeArrays(ohashes, oarray, osize);
        }
        if (CONCURRENT_MODIFICATION_EXCEPTIONS && size != osize) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Check whether a key exists in the array.
     *
     * @param key The key to search for.
     * @return Returns true if the key exists, else false.
     */
    @Override
    public boolean containsKey(Object key) {
        return indexOfKey(key) >= 0;
    }

    /**
     * Returns the index of a key in the set.
     *
     * @param key The key to search for.
     * @return Returns the index of the key if it exists, else a negative integer.
     */
    public int indexOfKey(Object key) {
        return key == null ? indexOfNull()
                : indexOf(key, identityHashCode ? System.identityHashCode(key) : key.hashCode());
    }

    /**
     * Returns an index for which {@link #valueAt} would return the
     * specified value, or a negative number if no keys map to the
     * specified value.
     * Beware that this is a linear search, unlike lookups by key,
     * and that multiple keys can map to the same value and this will
     * find only one of them.
     */
    public int indexOfValue(Object value) {
        final int N = size *2;
        final Object[] array = this.array;
        if (value == null) {
            for (int i=1; i<N; i+=2) {
                if (array[i] == null) {
                    return i>>1;
                }
            }
        } else {
            for (int i=1; i<N; i+=2) {
                if (value.equals(array[i])) {
                    return i>>1;
                }
            }
        }
        return -1;
    }

    /**
     * Check whether a value exists in the array.  This requires a linear search
     * through the entire array.
     *
     * @param value The value to search for.
     * @return Returns true if the value exists, else false.
     */
    @Override
    public boolean containsValue(Object value) {
        return indexOfValue(value) >= 0;
    }

    /**
     * Retrieve a value from the array.
     * @param key The key of the value to retrieve.
     * @return Returns the value associated with the given key,
     * or null if there is no such key.
     */
    @Override
    public V get(Object key) {
        final int index = indexOfKey(key);
        return index >= 0 ? (V) array[(index<<1)+1] : null;
    }

    /**
     * Return the key at the given index in the array.
     *
     * <p>For indices outside of the range <code>0...size()-1</code>, an
     * {@link ArrayIndexOutOfBoundsException} is thrown.</p>
     *
     * @param index The desired index, must be between 0 and {@link #size()}-1.
     * @return Returns the key stored at the given index.
     */
    public K keyAt(int index) {
        if (index >= size) {
            // The array might be slightly bigger than mSize, in which case, indexing won't fail.
            // Check if exception should be thrown outside of the critical path.
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return (K) array[index << 1];
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
    public V valueAt(int index) {
        if (index >= size) {
            // The array might be slightly bigger than mSize, in which case, indexing won't fail.
            // Check if exception should be thrown outside of the critical path.
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return (V) array[(index << 1) + 1];
    }

    /**
     * Set the value at a given index in the array.
     *
     * <p>For indices outside of the range <code>0...size()-1</code>, an
     * {@link ArrayIndexOutOfBoundsException} is thrown.</p>
     *
     * @param index The desired index, must be between 0 and {@link #size()}-1.
     * @param value The new value to store at this index.
     * @return Returns the previous value at the given index.
     */
    public V setValueAt(int index, V value) {
        if (index >= size) {
            // The array might be slightly bigger than mSize, in which case, indexing won't fail.
            // Check if exception should be thrown outside of the critical path.
            throw new ArrayIndexOutOfBoundsException(index);
        }
        index = (index << 1) + 1;
        V old = (V) array[index];
        array[index] = value;
        return old;
    }

    /**
     * Return true if the array map contains no items.
     */
    @Override
    public boolean isEmpty() {
        return size <= 0;
    }

    /**
     * Add a new value to the array map.
     * @param key The key under which to store the value.  If
     * this key already exists in the array, its value will be replaced.
     * @param value The value to store for the given key.
     * @return Returns the old value that was stored for the given key, or null if there
     * was no such key.
     */
    @Override
    public V put(K key, V value) {
        final int osize = size;
        final int hash;
        int index;
        if (key == null) {
            hash = 0;
            index = indexOfNull();
        } else {
            hash = identityHashCode ? System.identityHashCode(key) : key.hashCode();
            index = indexOf(key, hash);
        }
        if (index >= 0) {
            index = (index<<1) + 1;
            final V old = (V) array[index];
            array[index] = value;
            return old;
        }

        index = ~index;
        if (osize >= hashes.length) {
            final int n = osize >= (BASE_SIZE*2) ? (osize+(osize>>1))
                    : (osize >= BASE_SIZE ? (BASE_SIZE*2) : BASE_SIZE);

            //if (DEBUG) Log.d(TAG, "put: grow from " + mHashes.length + " to " + n);

            final int[] ohashes = hashes;
            final Object[] oarray = array;
            allocArrays(n);

            if (CONCURRENT_MODIFICATION_EXCEPTIONS && osize != size) {
                throw new ConcurrentModificationException();
            }

            if (hashes.length > 0) {
                //if (DEBUG) Log.d(TAG, "put: copy 0-" + osize + " to 0");
                System.arraycopy(ohashes, 0, hashes, 0, ohashes.length);
                System.arraycopy(oarray, 0, array, 0, oarray.length);
            }

            freeArrays(ohashes, oarray, osize);
        }

        if (index < osize) {
            //if (DEBUG) Log.d(TAG, "put: move " + index + "-" + (osize-index)
            //        + " to " + (index+1));
            System.arraycopy(hashes, index, hashes, index + 1, osize - index);
            System.arraycopy(array, index << 1, array, (index + 1) << 1, (size - index) << 1);
        }

        if (CONCURRENT_MODIFICATION_EXCEPTIONS) {
            if (osize != size || index >= hashes.length) {
                throw new ConcurrentModificationException();
            }
        }
        hashes[index] = hash;
        array[index<<1] = key;
        array[(index<<1)+1] = value;
        size++;
        return null;
    }

    /**
     * Perform a {@link #put(Object, Object)} of all key/value pairs in <var>array</var>
     * @param array The array whose contents are to be retrieved.
     */
    public void putAll(ArrayMap<? extends K, ? extends V> array) {
        final int N = array.size;
        ensureCapacity(size + N);
        if (size == 0) {
            if (N > 0) {
                System.arraycopy(array.hashes, 0, hashes, 0, N);
                System.arraycopy(array.array, 0, this.array, 0, N<<1);
                size = N;
            }
        } else {
            for (int i=0; i<N; i++) {
                put(array.keyAt(i), array.valueAt(i));
            }
        }
    }

    /**
     * Remove an existing key from the array map.
     * @param key The key of the mapping to remove.
     * @return Returns the value that was stored under the key, or null if there
     * was no such key.
     */
    @Override
    public V remove(Object key) {
        final int index = indexOfKey(key);
        if (index >= 0) {
            return removeAt(index);
        }

        return null;
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
    public V removeAt(int index) {
        if (index >= size) {
            // The array might be slightly bigger than mSize, in which case, indexing won't fail.
            // Check if exception should be thrown outside of the critical path.
            throw new ArrayIndexOutOfBoundsException(index);
        }

        final Object old = array[(index << 1) + 1];
        final int osize = size;
        final int nsize;
        if (osize <= 1) {
            // Now empty.
            //if (DEBUG) Log.d(TAG, "remove: shrink from " + mHashes.length + " to 0");
            final int[] ohashes = hashes;
            final Object[] oarray = array;
            hashes = EmptyArray.INT;
            array = EmptyArray.OBJECT;
            freeArrays(ohashes, oarray, osize);
            nsize = 0;
        } else {
            nsize = osize - 1;
            if (hashes.length > (BASE_SIZE*2) && size < hashes.length/3) {
                // Shrunk enough to reduce size of arrays.  We don't allow it to
                // shrink smaller than (BASE_SIZE*2) to avoid flapping between
                // that and BASE_SIZE.
                final int n = osize > (BASE_SIZE*2) ? (osize + (osize>>1)) : (BASE_SIZE*2);

                //if (DEBUG) Log.d(TAG, "remove: shrink from " + mHashes.length + " to " + n);

                final int[] ohashes = hashes;
                final Object[] oarray = array;
                allocArrays(n);

                if (CONCURRENT_MODIFICATION_EXCEPTIONS && osize != size) {
                    throw new ConcurrentModificationException();
                }

                if (index > 0) {
                    //if (DEBUG) Log.d(TAG, "remove: copy from 0-" + index + " to 0");
                    System.arraycopy(ohashes, 0, hashes, 0, index);
                    System.arraycopy(oarray, 0, array, 0, index << 1);
                }
                if (index < nsize) {
                    //if (DEBUG) Log.d(TAG, "remove: copy from " + (index+1) + "-" + nsize
                    //        + " to " + index);
                    System.arraycopy(ohashes, index + 1, hashes, index, nsize - index);
                    System.arraycopy(oarray, (index + 1) << 1, array, index << 1,
                            (nsize - index) << 1);
                }
            } else {
                if (index < nsize) {
                    //if (DEBUG) Log.d(TAG, "remove: move " + (index+1) + "-" + nsize
                    //        + " to " + index);
                    System.arraycopy(hashes, index + 1, hashes, index, nsize - index);
                    System.arraycopy(array, (index + 1) << 1, array, index << 1,
                            (nsize - index) << 1);
                }
                array[nsize << 1] = null;
                array[(nsize << 1) + 1] = null;
            }
        }
        if (CONCURRENT_MODIFICATION_EXCEPTIONS && osize != size) {
            throw new ConcurrentModificationException();
        }
        size = nsize;
        return (V)old;
    }

    /**
     * Return the number of items in this array map.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns false if the object is not a map, or
     * if the maps have different sizes. Otherwise, for each key in this map,
     * values of both maps are compared. If the values for any key are not
     * equal, the method returns false, otherwise it returns true.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) o;
            if (size() != map.size()) {
                return false;
            }

            try {
                for (int i = 0; i< size; i++) {
                    K key = keyAt(i);
                    V mine = valueAt(i);
                    Object theirs = map.get(key);
                    if (mine == null) {
                        if (theirs != null || !map.containsKey(key)) {
                            return false;
                        }
                    } else if (!mine.equals(theirs)) {
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
        final Object[] array = this.array;
        int result = 0;
        for (int i = 0, v = 1, s = size; i < s; i++, v+=2) {
            Object value = array[v];
            result += hashes[i] ^ (value == null ? 0 : value.hashCode());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation composes a string by iterating over its mappings. If
     * this map contains itself as a key or a value, the string "(this Map)"
     * will appear in its place.
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }

        StringBuilder buffer = new StringBuilder(size * 28);
        buffer.append('{');
        for (int i = 0; i< size; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            Object key = keyAt(i);
            if (key != this) {
                buffer.append(key);
            } else {
                buffer.append("(this Map)");
            }
            buffer.append('=');
            Object value = valueAt(i);
            if (value != this) {
                buffer.append(value);
            } else {
                buffer.append("(this Map)");
            }
        }
        buffer.append('}');
        return buffer.toString();
    }

    // ------------------------------------------------------------------------
    // Interop with traditional Java containers.  Not as efficient as using
    // specialized collection APIs.
    // ------------------------------------------------------------------------

    private MapCollections<K, V> getCollection() {
        if (collections == null) {
            collections = new MapCollections<K, V>() {
                @Override
                protected int colGetSize() {
                    return size;
                }

                @Override
                protected Object colGetEntry(int index, int offset) {
                    return array[(index<<1) + offset];
                }

                @Override
                protected int colIndexOfKey(Object key) {
                    return indexOfKey(key);
                }

                @Override
                protected int colIndexOfValue(Object value) {
                    return indexOfValue(value);
                }

                @Override
                protected Map<K, V> colGetMap() {
                    return ArrayMap.this;
                }

                @Override
                protected void colPut(K key, V value) {
                    put(key, value);
                }

                @Override
                protected V colSetValue(int index, V value) {
                    return setValueAt(index, value);
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
     * Determine if the array map contains all of the keys in the given collection.
     * @param collection The collection whose contents are to be checked against.
     * @return Returns true if this array map contains a key for every entry
     * in <var>collection</var>, else returns false.
     */
    public boolean containsAll(Collection<?> collection) {
        return MapCollections.containsAllHelper(this, collection);
    }

    /**
     * Perform a {@link #put(Object, Object)} of all key/value pairs in <var>map</var>
     * @param map The map whose contents are to be retrieved.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        ensureCapacity(size + map.size());
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Remove all keys in the array map that exist in the given collection.
     * @param collection The collection whose contents are to be used to remove keys.
     * @return Returns true if any keys were removed from the array map, else false.
     */
    public boolean removeAll(Collection<?> collection) {
        return MapCollections.removeAllHelper(this, collection);
    }

    /**
     * Remove all keys in the array map that do <b>not</b> exist in the given collection.
     * @param collection The collection whose contents are to be used to determine which
     * keys to keep.
     * @return Returns true if any keys were removed from the array map, else false.
     */
    public boolean retainAll(Collection<?> collection) {
        return MapCollections.retainAllHelper(this, collection);
    }

    /**
     * Return a {@link Set} for iterating over and interacting with all mappings
     * in the array map.
     *
     * <p><b>Note:</b> this is a very inefficient way to access the array contents, it
     * requires generating a number of temporary objects and allocates additional state
     * information associated with the container that will remain for the life of the container.</p>
     *
     * <p><b>Note:</b></p> the semantics of this
     * Set are subtly different than that of a {@link java.util.HashMap}: most important,
     * the {@link Entry Map.Entry} object returned by its iterator is a single
     * object that exists for the entire iterator, so you can <b>not</b> hold on to it
     * after calling {@link java.util.Iterator#next() Iterator.next}.</p>
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return getCollection().getEntrySet();
    }

    /**
     * Return a {@link Set} for iterating over and interacting with all keys
     * in the array map.
     *
     * <p><b>Note:</b> this is a fairly inefficient way to access the array contents, it
     * requires generating a number of temporary objects and allocates additional state
     * information associated with the container that will remain for the life of the container.</p>
     */
    @Override
    public Set<K> keySet() {
        return getCollection().getKeySet();
    }

    /**
     * Return a {@link Collection} for iterating over and interacting with all values
     * in the array map.
     *
     * <p><b>Note:</b> this is a fairly inefficient way to access the array contents, it
     * requires generating a number of temporary objects and allocates additional state
     * information associated with the container that will remain for the life of the container.</p>
     */
    @Override
    public Collection<V> values() {
        return getCollection().getValues();
    }

    /**
     * Helper for writing standard Java collection interfaces to a data
     * structure like {@link ArrayMap}.
     */
    abstract static class MapCollections<K, V> {

        EntrySet entrySet;
        KeySet keySet;
        ValuesCollection values;

        final class ArrayIterator<T> implements Iterator<T> {
            final int mOffset;
            int mSize;
            int mIndex;
            boolean mCanRemove = false;

            ArrayIterator(int offset) {
                mOffset = offset;
                mSize = colGetSize();
            }

            @Override
            public boolean hasNext() {
                return mIndex < mSize;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                Object res = colGetEntry(mIndex, mOffset);
                mIndex++;
                mCanRemove = true;
                return (T)res;
            }

            @Override
            public void remove() {
                if (!mCanRemove) {
                    throw new IllegalStateException();
                }
                mIndex--;
                mSize--;
                mCanRemove = false;
                colRemoveAt(mIndex);
            }
        }

        final class MapIterator implements Iterator<Entry<K, V>>, Entry<K, V> {
            int mEnd;
            int mIndex;
            boolean mEntryValid = false;

            MapIterator() {
                mEnd = colGetSize() - 1;
                mIndex = -1;
            }

            @Override
            public boolean hasNext() {
                return mIndex < mEnd;
            }

            @Override
            public Entry<K, V> next() {
                if (!hasNext()) throw new NoSuchElementException();
                mIndex++;
                mEntryValid = true;
                return this;
            }

            @Override
            public void remove() {
                if (!mEntryValid) {
                    throw new IllegalStateException();
                }
                colRemoveAt(mIndex);
                mIndex--;
                mEnd--;
                mEntryValid = false;
            }

            @Override
            public K getKey() {
                if (!mEntryValid) {
                    throw new IllegalStateException(
                            "This container does not support retaining Map.Entry objects");
                }
                return (K)colGetEntry(mIndex, 0);
            }

            @Override
            public V getValue() {
                if (!mEntryValid) {
                    throw new IllegalStateException(
                            "This container does not support retaining Map.Entry objects");
                }
                return (V)colGetEntry(mIndex, 1);
            }

            @Override
            public V setValue(V object) {
                if (!mEntryValid) {
                    throw new IllegalStateException(
                            "This container does not support retaining Map.Entry objects");
                }
                return colSetValue(mIndex, object);
            }

            @Override
            public final boolean equals(Object o) {
                if (!mEntryValid) {
                    throw new IllegalStateException(
                            "This container does not support retaining Map.Entry objects");
                }
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                Entry<?, ?> e = (Entry<?, ?>) o;
                return Objects.equals(e.getKey(), colGetEntry(mIndex, 0))
                        && Objects.equals(e.getValue(), colGetEntry(mIndex, 1));
            }

            @Override
            public final int hashCode() {
                if (!mEntryValid) {
                    throw new IllegalStateException(
                            "This container does not support retaining Map.Entry objects");
                }
                final Object key = colGetEntry(mIndex, 0);
                final Object value = colGetEntry(mIndex, 1);
                return (key == null ? 0 : key.hashCode()) ^
                        (value == null ? 0 : value.hashCode());
            }

            @Override
            public final String toString() {
                return getKey() + "=" + getValue();
            }
        }

        final class EntrySet implements Set<Entry<K, V>> {
            @Override
            public boolean add(Entry<K, V> object) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends Entry<K, V>> collection) {
                int oldSize = colGetSize();
                for (Entry<K, V> entry : collection) {
                    colPut(entry.getKey(), entry.getValue());
                }
                return oldSize != colGetSize();
            }

            @Override
            public void clear() {
                colClear();
            }

            @Override
            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                Entry<?, ?> e = (Entry<?, ?>) o;
                int index = colIndexOfKey(e.getKey());
                if (index < 0) {
                    return false;
                }
                Object foundVal = colGetEntry(index, 1);
                return Objects.equals(foundVal, e.getValue());
            }

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

            @Override
            public boolean isEmpty() {
                return colGetSize() == 0;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new MapIterator();
            }

            @Override
            public boolean remove(Object object) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int size() {
                return colGetSize();
            }

            @Override
            public Object[] toArray() {
                throw new UnsupportedOperationException();
            }

            @Override
            public <T> T[] toArray(T[] array) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean equals(Object object) {
                return equalsSetHelper(this, object);
            }

            @Override
            public int hashCode() {
                int result = 0;
                for (int i=colGetSize()-1; i>=0; i--) {
                    final Object key = colGetEntry(i, 0);
                    final Object value = colGetEntry(i, 1);
                    result += ( (key == null ? 0 : key.hashCode()) ^
                            (value == null ? 0 : value.hashCode()) );
                }
                return result;
            }
        };

        final class KeySet implements Set<K> {

            @Override
            public boolean add(K object) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends K> collection) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                colClear();
            }

            @Override
            public boolean contains(Object object) {
                return colIndexOfKey(object) >= 0;
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return containsAllHelper(colGetMap(), collection);
            }

            @Override
            public boolean isEmpty() {
                return colGetSize() == 0;
            }

            @Override
            public Iterator<K> iterator() {
                return new ArrayIterator<K>(0);
            }

            @Override
            public boolean remove(Object object) {
                int index = colIndexOfKey(object);
                if (index >= 0) {
                    colRemoveAt(index);
                    return true;
                }
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return removeAllHelper(colGetMap(), collection);
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return retainAllHelper(colGetMap(), collection);
            }

            @Override
            public int size() {
                return colGetSize();
            }

            @Override
            public Object[] toArray() {
                return toArrayHelper(0);
            }

            @Override
            public <T> T[] toArray(T[] array) {
                return toArrayHelper(array, 0);
            }

            @Override
            public boolean equals(Object object) {
                return equalsSetHelper(this, object);
            }

            @Override
            public int hashCode() {
                int result = 0;
                for (int i=colGetSize()-1; i>=0; i--) {
                    Object obj = colGetEntry(i, 0);
                    result += obj == null ? 0 : obj.hashCode();
                }
                return result;
            }
        };

        final class ValuesCollection implements Collection<V> {

            @Override
            public boolean add(V object) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends V> collection) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                colClear();
            }

            @Override
            public boolean contains(Object object) {
                return colIndexOfValue(object) >= 0;
            }

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

            @Override
            public boolean isEmpty() {
                return colGetSize() == 0;
            }

            @Override
            public Iterator<V> iterator() {
                return new ArrayIterator<V>(1);
            }

            @Override
            public boolean remove(Object object) {
                int index = colIndexOfValue(object);
                if (index >= 0) {
                    colRemoveAt(index);
                    return true;
                }
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                int N = colGetSize();
                boolean changed = false;
                for (int i=0; i<N; i++) {
                    Object cur = colGetEntry(i, 1);
                    if (collection.contains(cur)) {
                        colRemoveAt(i);
                        i--;
                        N--;
                        changed = true;
                    }
                }
                return changed;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                int N = colGetSize();
                boolean changed = false;
                for (int i=0; i<N; i++) {
                    Object cur = colGetEntry(i, 1);
                    if (!collection.contains(cur)) {
                        colRemoveAt(i);
                        i--;
                        N--;
                        changed = true;
                    }
                }
                return changed;
            }

            @Override
            public int size() {
                return colGetSize();
            }

            @Override
            public Object[] toArray() {
                return toArrayHelper(1);
            }

            @Override
            public <T> T[] toArray(T[] array) {
                return toArrayHelper(array, 1);
            }
        };

        public static <K, V> boolean containsAllHelper(Map<K, V> map, Collection<?> collection) {
            Iterator<?> it = collection.iterator();
            while (it.hasNext()) {
                if (!map.containsKey(it.next())) {
                    return false;
                }
            }
            return true;
        }

        public static <K, V> boolean removeAllHelper(Map<K, V> map, Collection<?> collection) {
            int oldSize = map.size();
            Iterator<?> it = collection.iterator();
            while (it.hasNext()) {
                map.remove(it.next());
            }
            return oldSize != map.size();
        }

        public static <K, V> boolean retainAllHelper(Map<K, V> map, Collection<?> collection) {
            int oldSize = map.size();
            Iterator<K> it = map.keySet().iterator();
            while (it.hasNext()) {
                if (!collection.contains(it.next())) {
                    it.remove();
                }
            }
            return oldSize != map.size();
        }

        public Object[] toArrayHelper(int offset) {
            final int N = colGetSize();
            Object[] result = new Object[N];
            for (int i=0; i<N; i++) {
                result[i] = colGetEntry(i, offset);
            }
            return result;
        }

        public <T> T[] toArrayHelper(T[] array, int offset) {
            final int N  = colGetSize();
            if (array.length < N) {
                @SuppressWarnings("unchecked") T[] newArray
                    = (T[]) Array.newInstance(array.getClass().getComponentType(), N);
                array = newArray;
            }
            for (int i=0; i<N; i++) {
                array[i] = (T)colGetEntry(i, offset);
            }
            if (array.length > N) {
                array[N] = null;
            }
            return array;
        }

        public static <T> boolean equalsSetHelper(Set<T> set, Object object) {
            if (set == object) {
                return true;
            }
            if (object instanceof Set) {
                Set<?> s = (Set<?>) object;

                try {
                    return set.size() == s.size() && set.containsAll(s);
                } catch (NullPointerException ignored) {
                    return false;
                } catch (ClassCastException ignored) {
                    return false;
                }
            }
            return false;
        }

        public Set<Entry<K, V>> getEntrySet() {
            if (entrySet == null) {
                entrySet = new EntrySet();
            }
            return entrySet;
        }

        public Set<K> getKeySet() {
            if (keySet == null) {
                keySet = new KeySet();
            }
            return keySet;
        }

        public Collection<V> getValues() {
            if (values == null) {
                values = new ValuesCollection();
            }
            return values;
        }

        protected abstract int colGetSize();
        protected abstract Object colGetEntry(int index, int offset);
        protected abstract int colIndexOfKey(Object key);
        protected abstract int colIndexOfValue(Object key);
        protected abstract Map<K, V> colGetMap();
        protected abstract void colPut(K key, V value);
        protected abstract V colSetValue(int index, V value);
        protected abstract void colRemoveAt(int index);
        protected abstract void colClear();

    }

}
