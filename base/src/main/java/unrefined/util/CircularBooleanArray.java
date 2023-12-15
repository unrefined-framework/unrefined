/*
 * Copyright 2018 The Android Open Source Project
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

/**
 * CircularBooleanArray is a circular boolean array data structure that provides O(1) random read, O(1)
 * prepend and O(1) append. The CircularBooleanArray automatically grows its capacity when number of
 * added booleans is over its capacity.
 */
public class CircularBooleanArray {

    private boolean[] elements;
    private int head;
    private int tail;
    private int capacityBitmask;

    private void doubleCapacity() {
        int n = elements.length;
        int r = n - head;
        int newCapacity = n << 1;
        if (newCapacity < 0) {
            throw new RuntimeException("Max array capacity exceeded");
        }
        boolean[] a = new boolean[newCapacity];
        System.arraycopy(elements, head, a, 0, r);
        System.arraycopy(elements, 0, a, r, head);
        elements = a;
        head = 0;
        tail = n;
        capacityBitmask = newCapacity - 1;
    }

    /**
     * Creates a circular array with default capacity.
     */
    public CircularBooleanArray() {
        this(8);
    }

    /**
     * Creates a circular array with capacity for at least {@code minCapacity}
     * elements.
     *
     * @param minCapacity the minimum capacity, between 1 and 2^30 inclusive
     */
    public CircularBooleanArray(int minCapacity) {
        if (minCapacity < 1) {
            throw new IllegalArgumentException("capacity must be >= 1");
        }
        if (minCapacity > (2 << 29)) {
            throw new IllegalArgumentException("capacity must be <= 2^30");
        }

        // If minCapacity isn't a power of 2, round up to the next highest
        // power of 2.
        final int arrayCapacity;
        if (Integer.bitCount(minCapacity) != 1) {
            arrayCapacity = Integer.highestOneBit(minCapacity - 1) << 1;
        } else {
            arrayCapacity = minCapacity;
        }

        capacityBitmask = arrayCapacity - 1;
        elements = new boolean[arrayCapacity];
    }

    /**
     * Add a boolean in front of the CircularBooleanArray.
     * @param e  Boolean to add.
     */
    public void addFirst(boolean e) {
        head = (head - 1) & capacityBitmask;
        elements[head] = e;
        if (head == tail) {
            doubleCapacity();
        }
    }

    /**
     * Add a boolean at end of the CircularBooleanArray.
     * @param e  Boolean to add.
     */
    public void addLast(boolean e) {
        elements[tail] = e;
        tail = (tail + 1) & capacityBitmask;
        if (tail == head) {
            doubleCapacity();
        }
    }

    /**
     * Remove first boolean from front of the CircularBooleanArray and return it.
     * @return  The boolean removed.
     * @throws ArrayIndexOutOfBoundsException if CircularBooleanArray is empty.
     */
    public boolean popFirst() {
        if (head == tail) throw new ArrayIndexOutOfBoundsException();
        boolean result = elements[head];
        head = (head + 1) & capacityBitmask;
        return result;
    }

    /**
     * Remove last boolean from end of the CircularBooleanArray and return it.
     * @return  The boolean removed.
     * @throws ArrayIndexOutOfBoundsException if CircularBooleanArray is empty.
     */
    public boolean popLast() {
        if (head == tail) throw new ArrayIndexOutOfBoundsException();
        int t = (tail - 1) & capacityBitmask;
        boolean result = elements[t];
        tail = t;
        return result;
    }

    /**
     * Remove all booleans from the CircularBooleanArray.
     */
    public void clear() {
        tail = head;
    }

    /**
     * Remove multiple booleans from front of the CircularBooleanArray, ignore when numOfElements
     * is less than or equals to 0.
     * @param numOfElements  Number of booleans to remove.
     * @throws ArrayIndexOutOfBoundsException if numOfElements is larger than
     *         {@link #size()}
     */
    public void removeFromStart(int numOfElements) {
        if (numOfElements <= 0) {
            return;
        }
        if (numOfElements > size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        head = (head + numOfElements) & capacityBitmask;
    }

    /**
     * Remove multiple elements from end of the CircularBooleanArray, ignore when numOfElements
     * is less than or equals to 0.
     * @param numOfElements  Number of booleans to remove.
     * @throws ArrayIndexOutOfBoundsException if numOfElements is larger than
     *         {@link #size()}
     */
    public void removeFromEnd(int numOfElements) {
        if (numOfElements <= 0) {
            return;
        }
        if (numOfElements > size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        tail = (tail - numOfElements) & capacityBitmask;
    }

    /**
     * Get first boolean of the CircularBooleanArray.
     * @return The first boolean.
     * @throws ArrayIndexOutOfBoundsException if CircularBooleanArray is empty.
     */
    public boolean getFirst() {
        if (head == tail) throw new ArrayIndexOutOfBoundsException();
        return elements[head];
    }

    /**
     * Get last boolean of the CircularBooleanArray.
     * @return The last boolean.
     * @throws ArrayIndexOutOfBoundsException if CircularBooleanArray is empty.
     */
    public boolean getLast() {
        if (head == tail) throw new ArrayIndexOutOfBoundsException();
        return elements[(tail - 1) & capacityBitmask];
    }

    /**
     * Get nth (0 <= n <= size()-1) boolean of the CircularBooleanArray.
     * @param n  The zero based element index in the CircularBooleanArray.
     * @return The nth boolean.
     * @throws ArrayIndexOutOfBoundsException if n < 0 or n >= size().
     */
    public boolean get(int n) {
        if (n < 0 || n >= size()) throw new ArrayIndexOutOfBoundsException();
        return elements[(head + n) & capacityBitmask];
    }

    /**
     * Get number of booleans in the CircularBooleanArray.
     * @return Number of booleans in the CircularBooleanArray.
     */
    public int size() {
        return (tail - head) & capacityBitmask;
    }

    /**
     * Return true if size() is 0.
     * @return true if size() is 0.
     */
    public boolean isEmpty() {
        return head == tail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CircularBooleanArray)) return false;

        CircularBooleanArray that = (CircularBooleanArray) o;

        int size = size();
        if (size != that.size()) return false;

        for (int i = 0; i < size; i ++) {
            if (get(head + i) != that.get(that.head + i)) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int size = size();
        int result = size;
        for (int i = 0; i < size; i ++) {
            result = 31 * result + (get(head + i) ? 1231 : 1237);
        }
        return result;
    }

    @Override
    public String toString() {
        int size = size();
        if (size <= 0) return "[]";

        StringBuilder buffer = new StringBuilder(size * 28);
        buffer.append('[');
        for (int i = 0; i < size; i ++) {
            if (i > 0) buffer.append(", ");
            buffer.append(get(head + i));
        }
        buffer.append(']');
        return buffer.toString();
    }

}
