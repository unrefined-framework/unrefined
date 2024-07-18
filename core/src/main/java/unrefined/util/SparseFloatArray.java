/*
 * Copyright (C) 2006 The Android Open Source Project
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

import java.util.Iterator;

/**
 * SparseFloatArrays map integers to floats.  Unlike a normal array of floats,
 * there can be gaps in the indices.  It is intended to be more memory efficient
 * than using a HashMap to map Integers to Floats, both because it avoids
 * auto-boxing keys and values and its data structure doesn't rely on an extra entry object
 * for each mapping.
 *
 * <p>Note that this container keeps its mappings in an array data structure,
 * using a binary search to find keys.  The implementation is not intended to be appropriate for
 * data structures
 * that may contain large numbers of items.  It is generally slower than a traditional
 * HashMap, since lookups require a binary search and adds and removes require inserting
 * and deleting entries in the array.  For containers holding up to hundreds of items,
 * the performance difference is not significant, less than 50%.</p>
 *
 * <p>It is possible to iterate over the items in this container using
 * {@link #keyAt(int)} and {@link #valueAt(int)}. Iterating over the keys using
 * <code>keyAt(int)</code> with ascending values of the index will return the
 * keys in ascending order, or the values corresponding to the keys in ascending
 * order in the case of <code>valueAt(int)</code>.</p>
 */
public class SparseFloatArray implements Cloneable, Iterable<Float> {

    private int[] keys;
    private float[] values;
    private int size;

    /**
     * Creates a new SparseFloatArray containing no mappings.
     */
    public SparseFloatArray() {
        this(10);
    }

    /**
     * Creates a new SparseFloatArray containing no mappings that will not
     * require any additional memory allocation to store the specified
     * number of mappings.  If you supply an initial capacity of 0, the
     * sparse array will be initialized with a light-weight representation
     * not requiring any additional array allocations.
     */
    public SparseFloatArray(int initialCapacity) {
        if (initialCapacity == 0) {
            keys = EmptyArray.INT;
            values = EmptyArray.FLOAT;
        } else {
            initialCapacity = SparseArray.sparseIntArraySize(initialCapacity);
            keys = new int[initialCapacity];
            values = new float[initialCapacity];
        }
        size = 0;
    }

    @Override
    public SparseFloatArray clone() {
        SparseFloatArray clone;
        try {
            clone = (SparseFloatArray) super.clone();
        } catch (CloneNotSupportedException e) {
            clone = new SparseFloatArray();
        }
        clone.keys = keys.clone();
        clone.values = values.clone();
        return clone;
    }

    /**
     * Gets the float mapped from the specified key, or <code>0</code>
     * if no such mapping has been made.
     */
    public float get(int key) {
        return get(key, (float) 0);
    }

    /**
     * Gets the float mapped from the specified key, or the specified value
     * if no such mapping has been made.
     */
    public float get(int key, float valueIfKeyNotFound) {
        int i = Arrays.binarySearchUnchecked(keys, 0, size, key);

        if (i < 0) {
            return valueIfKeyNotFound;
        } else {
            return values[i];
        }
    }

    /**
     * Removes the mapping from the specified key, if there was any.
     */
    public void remove(int key) {
        int i = Arrays.binarySearchUnchecked(keys, 0, size, key);

        if (i >= 0) {
            removeAt(i);
        }
    }

    /**
     * Removes the mapping at the given index.
     */
    public void removeAt(int index) {
        System.arraycopy(keys, index + 1, keys, index, size - (index + 1));
        System.arraycopy(values, index + 1, values, index, size - (index + 1));
        size--;
    }

    /**
     * Remove a range of mappings as a batch.
     *
     * @param index Index to begin at
     * @param size Number of mappings to remove
     *
     * <p>For indices outside of the range <code>0...size()-1</code>,
     * the behavior is undefined.</p>
     */
    public void removeAtRange(int index, int size) {
        final int end = Math.min(this.size, index + size);
        for (int i = index; i < end; i++) {
            removeAt(i);
        }
    }

    /**
     * Adds a mapping from the specified key to the specified value,
     * replacing the previous mapping from the specified key if there
     * was one.
     */
    public void put(int key, float value) {
        int i = Arrays.binarySearchUnchecked(keys, 0, size, key);

        if (i >= 0) {
            values[i] = value;
        } else {
            i = ~i;

            if (size >= keys.length) {
                int n = SparseArray.sparseIntArraySize(size + 1);

                int[] nkeys = new int[n];
                float[] nvalues = new float[n];

                // Log.e("SparseArray", "grow " + keys.length + " to " + n);
                System.arraycopy(keys, 0, nkeys, 0, keys.length);
                System.arraycopy(values, 0, nvalues, 0, values.length);

                keys = nkeys;
                values = nvalues;
            }

            if (size - i != 0) {
                // Log.e("SparseArray", "move " + (size - i));
                System.arraycopy(keys, i, keys, i + 1, size - i);
                System.arraycopy(values, i, values, i + 1, size - i);
            }

            keys[i] = key;
            values[i] = value;
            size ++;
        }
    }

    /**
     * Returns the number of key-value mappings that this SparseFloatArray
     * currently stores.
     */
    public int size() {
        return size;
    }

    /**
     * Given an index in the range <code>0...size()-1</code>, returns
     * the key from the <code>index</code>th key-value mapping that this
     * SparseFloatArray stores.
     *
     * <p>The keys corresponding to indices in ascending order are guaranteed to
     * be in ascending order, e.g., <code>keyAt(0)</code> will return the
     * smallest key and <code>keyAt(size()-1)</code> will return the largest
     * key.</p>
     *
     * <p>For indices outside of the range <code>0...size()-1</code>,
     * an {@link ArrayIndexOutOfBoundsException} is thrown.</p>
     */
    public int keyAt(int index) {
        if (index >= size) {
            // The array might be slightly bigger than size, in which case, indexing won't fail.
            // Check if exception should be thrown outside of the critical path.
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return keys[index];
    }

    /**
     * Given an index in the range <code>0...size()-1</code>, returns
     * the value from the <code>index</code>th key-value mapping that this
     * SparseFloatArray stores.
     *
     * <p>The values corresponding to indices in ascending order are guaranteed
     * to be associated with keys in ascending order, e.g.,
     * <code>valueAt(0)</code> will return the value associated with the
     * smallest key and <code>valueAt(size()-1)</code> will return the value
     * associated with the largest key.</p>
     *
     * <p>For indices outside of the range <code>0...size()-1</code>,
     * an {@link ArrayIndexOutOfBoundsException} is thrown.</p>
     */
    public float valueAt(int index) {
        if (index >= size) {
            // The array might be slightly bigger than size, in which case, indexing won't fail.
            // Check if exception should be thrown outside of the critical path.
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return values[index];
    }

    /**
     * Given an index in the range <code>0...size()-1</code>, sets a new
     * value for the <code>index</code>th key-value mapping that this
     * SparseFloatArray stores.
     *
     * <p>For indices outside of the range <code>0...size()-1</code>,
     * an {@link ArrayIndexOutOfBoundsException} is thrown.</p>
     */
    public void setValueAt(int index, float value) {
        if (index >= size) {
            // The array might be slightly bigger than size, in which case, indexing won't fail.
            // Check if exception should be thrown outside of the critical path.
            throw new ArrayIndexOutOfBoundsException(index);
        }
        values[index] = value;
    }

    /**
     * Returns the index for which {@link #keyAt} would return the
     * specified key, or a negative number if the specified
     * key is not mapped.
     */
    public int indexOfKey(int key) {
        return Arrays.binarySearchUnchecked(keys, 0, size, key);
    }

    /**
     * Returns an index for which {@link #valueAt} would return the
     * specified key, or a negative number if no keys map to the
     * specified value.
     * Beware that this is a linear search, unlike lookups by key,
     * and that multiple keys can map to the same value and this will
     * find only one of them.
     */
    public int indexOfValue(float value) {
        for (int i = 0; i < size; i ++) {
            if (Float.compare(values[i], value) == 0) return i;
        }

        return -1;
    }

    /**
     * Removes all key-value mappings from this SparseFloatArray.
     */
    public void clear() {
        size = 0;
    }

    /**
     * Puts a key/value pair into the array, optimizing for the case where
     * the key is greater than all existing keys in the array.
     */
    public void append(int key, float value) {
        if (size != 0 && key <= keys[size - 1]) {
            put(key, value);
            return;
        }

        int pos = size;
        if (pos >= keys.length) {
            int n =  SparseArray.sparseIntArraySize(pos + 1);

            int[] nkeys = new int[n];
            float[] nvalues = new float[n];

            // Log.e("SparseArray", "grow " + keys.length + " to " + n);
            System.arraycopy(keys, 0, nkeys, 0, keys.length);
            System.arraycopy(values, 0, nvalues, 0, values.length);

            keys = nkeys;
            values = nvalues;
        }

        keys[pos] = key;
        values[pos] = value;
        size = pos + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SparseFloatArray)) return false;

        SparseFloatArray that = (SparseFloatArray) o;

        if (size != that.size) return false;
        for (int i = 0; i < size; i ++) {
            if (Float.compare(keys[i], that.keys[i]) != 0) return false;
            if (Float.compare(values[i], that.values[i]) != 0) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = size;
        for (int i = 0; i < size; i ++) {
            result = 31 * result + keys[i];
            result = 31 * result + Float.floatToIntBits(values[i]);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation composes a string by iterating over its mappings.
     */
    @Override
    public String toString() {
        if (size() <= 0) {
            return "{}";
        }

        StringBuilder buffer = new StringBuilder(size * 28);
        buffer.append('{');
        for (int i = 0; i< size; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            int key = keyAt(i);
            buffer.append(key);
            buffer.append('=');
            float value = valueAt(i);
            buffer.append(value);
        }
        buffer.append('}');
        return buffer.toString();
    }

    @Override
    public Iterator<Float> iterator() {
        return new Iterator<Float>() {
            private int index = -1;
            @Override
            public boolean hasNext() {
                return index + 1 < size();
            }
            @Override
            public Float next() {
                index ++;
                return get(index);
            }
        };
    }

}
