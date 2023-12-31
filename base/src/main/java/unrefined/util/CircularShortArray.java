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

import java.util.Iterator;
import java.util.RandomAccess;

/**
 * CircularShortArray is a circular short array data structure that provides O(1) random read, O(1)
 * prepend and O(1) append. The CircularShortArray automatically grows its capacity when number of
 * added shorts is over its capacity.
 */
public class CircularShortArray implements Cloneable, Iterable<Short>, RandomAccess {

    private short[] elements;
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
        short[] a = new short[newCapacity];
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
    public CircularShortArray() {
        this(8);
    }

    /**
     * Creates a circular array with capacity for at least {@code minCapacity}
     * elements.
     *
     * @param minCapacity the minimum capacity, between 1 and 2^30 inclusive
     */
    public CircularShortArray(int minCapacity) {
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
        elements = new short[arrayCapacity];
    }

    @Override
    public CircularShortArray clone() {
        CircularShortArray clone;
        try {
            clone = (CircularShortArray) super.clone();
        }
        catch (CloneNotSupportedException e) {
            clone = new CircularShortArray();
            clone.head = head;
            clone.tail = tail;
            clone.capacityBitmask = capacityBitmask;
        }
        clone.elements = elements.clone();
        return clone;
    }

    /**
     * Add a short in front of the CircularShortArray.
     * @param e  Short to add.
     */
    public void addFirst(short e) {
        head = (head - 1) & capacityBitmask;
        elements[head] = e;
        if (head == tail) {
            doubleCapacity();
        }
    }

    /**
     * Add a short at end of the CircularShortArray.
     * @param e  Short to add.
     */
    public void addLast(short e) {
        elements[tail] = e;
        tail = (tail + 1) & capacityBitmask;
        if (tail == head) {
            doubleCapacity();
        }
    }

    /**
     * Remove first short from front of the CircularShortArray and return it.
     * @return  The short removed.
     * @throws ArrayIndexOutOfBoundsException if CircularShortArray is empty.
     */
    public short popFirst() {
        if (head == tail) throw new ArrayIndexOutOfBoundsException();
        short result = elements[head];
        head = (head + 1) & capacityBitmask;
        return result;
    }

    /**
     * Remove last short from end of the CircularShortArray and return it.
     * @return  The short removed.
     * @throws ArrayIndexOutOfBoundsException if CircularShortArray is empty.
     */
    public short popLast() {
        if (head == tail) throw new ArrayIndexOutOfBoundsException();
        int t = (tail - 1) & capacityBitmask;
        short result = elements[t];
        tail = t;
        return result;
    }

    /**
     * Remove all shorts from the CircularShortArray.
     */
    public void clear() {
        tail = head;
    }

    /**
     * Remove multiple shorts from front of the CircularShortArray, ignore when numOfElements
     * is less than or equals to 0.
     * @param numOfElements  Number of shorts to remove.
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
     * Remove multiple elements from end of the CircularShortArray, ignore when numOfElements
     * is less than or equals to 0.
     * @param numOfElements  Number of shorts to remove.
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
     * Get first short of the CircularShortArray.
     * @return The first short.
     * @throws ArrayIndexOutOfBoundsException if CircularShortArray is empty.
     */
    public short getFirst() {
        if (head == tail) throw new ArrayIndexOutOfBoundsException();
        return elements[head];
    }

    /**
     * Get last short of the CircularShortArray.
     * @return The last short.
     * @throws ArrayIndexOutOfBoundsException if CircularShortArray is empty.
     */
    public short getLast() {
        if (head == tail) throw new ArrayIndexOutOfBoundsException();
        return elements[(tail - 1) & capacityBitmask];
    }

    /**
     * Get nth (0 <= n <= size()-1) short of the CircularShortArray.
     * @param n  The zero based element index in the CircularShortArray.
     * @return The nth short.
     * @throws ArrayIndexOutOfBoundsException if n < 0 or n >= size().
     */
    public short get(int n) {
        if (n < 0 || n >= size()) throw new ArrayIndexOutOfBoundsException();
        return elements[(head + n) & capacityBitmask];
    }

    /**
     * Get number of shorts in the CircularShortArray.
     * @return Number of shorts in the CircularShortArray.
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
        if (!(o instanceof CircularShortArray)) return false;

        CircularShortArray that = (CircularShortArray) o;

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
            result = 31 * result + get(head + i);
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

    @Override
    public Iterator<Short> iterator() {
        return new Iterator<Short>() {
            private int index = -1;
            @Override
            public boolean hasNext() {
                return index + 1 < size();
            }
            @Override
            public Short next() {
                index ++;
                return get(index);
            }
        };
    }

}
