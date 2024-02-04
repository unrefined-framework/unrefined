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

/**
 * CircularCharArray is a circular character array data structure that provides O(1) random read, O(1)
 * prepend and O(1) append. The CircularCharArray automatically grows its capacity when number of
 * added characters is over its capacity.
 */
public class CircularCharArray implements Cloneable, Iterable<Character> {

    private char[] elements;
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
        char[] a = new char[newCapacity];
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
    public CircularCharArray() {
        this(8);
    }

    /**
     * Creates a circular array with capacity for at least {@code minCapacity}
     * elements.
     *
     * @param minCapacity the minimum capacity, between 1 and 2^30 inclusive
     */
    public CircularCharArray(int minCapacity) {
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
        elements = new char[arrayCapacity];
    }

    @Override
    public CircularCharArray clone() {
        CircularCharArray clone;
        try {
            clone = (CircularCharArray) super.clone();
        }
        catch (CloneNotSupportedException e) {
            clone = new CircularCharArray();
            clone.head = head;
            clone.tail = tail;
            clone.capacityBitmask = capacityBitmask;
        }
        clone.elements = elements.clone();
        return clone;
    }

    /**
     * Add a character in front of the CircularCharArray.
     * @param e  Character to add.
     */
    public void addFirst(char e) {
        head = (head - 1) & capacityBitmask;
        elements[head] = e;
        if (head == tail) {
            doubleCapacity();
        }
    }

    /**
     * Add a character at end of the CircularCharArray.
     * @param e  Character to add.
     */
    public void addLast(char e) {
        elements[tail] = e;
        tail = (tail + 1) & capacityBitmask;
        if (tail == head) {
            doubleCapacity();
        }
    }

    /**
     * Remove first character from front of the CircularCharArray and return it.
     * @return  The character removed.
     * @throws ArrayIndexOutOfBoundsException if CircularCharArray is empty.
     */
    public char popFirst() {
        if (head == tail) throw new ArrayIndexOutOfBoundsException();
        char result = elements[head];
        head = (head + 1) & capacityBitmask;
        return result;
    }

    /**
     * Remove last character from end of the CircularCharArray and return it.
     * @return  The character removed.
     * @throws ArrayIndexOutOfBoundsException if CircularCharArray is empty.
     */
    public char popLast() {
        if (head == tail) throw new ArrayIndexOutOfBoundsException();
        int t = (tail - 1) & capacityBitmask;
        char result = elements[t];
        tail = t;
        return result;
    }

    /**
     * Remove all characters from the CircularCharArray.
     */
    public void clear() {
        tail = head;
    }

    /**
     * Remove multiple characters from front of the CircularCharArray, ignore when numOfElements
     * is less than or equals to 0.
     * @param numOfElements  Number of characters to remove.
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
     * Remove multiple elements from end of the CircularCharArray, ignore when numOfElements
     * is less than or equals to 0.
     * @param numOfElements  Number of characters to remove.
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
     * Get first character of the CircularCharArray.
     * @return The first character.
     * @throws ArrayIndexOutOfBoundsException if CircularCharArray is empty.
     */
    public char getFirst() {
        if (head == tail) throw new ArrayIndexOutOfBoundsException();
        return elements[head];
    }

    /**
     * Get last char of the CircularCharArray.
     * @return The last char.
     * @throws ArrayIndexOutOfBoundsException if CircularCharArray is empty.
     */
    public char getLast() {
        if (head == tail) throw new ArrayIndexOutOfBoundsException();
        return elements[(tail - 1) & capacityBitmask];
    }

    /**
     * Get nth (0 <= n <= size()-1) character of the CircularCharArray.
     * @param n  The zero based element index in the CircularCharArray.
     * @return The nth character.
     * @throws ArrayIndexOutOfBoundsException if n < 0 or n >= size().
     */
    public char get(int n) {
        if (n < 0 || n >= size()) throw new ArrayIndexOutOfBoundsException();
        return elements[(head + n) & capacityBitmask];
    }

    /**
     * Get number of characters in the CircularCharArray.
     * @return Number of characters in the CircularCharArray.
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
        if (!(o instanceof CircularCharArray)) return false;

        CircularCharArray that = (CircularCharArray) o;

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
    public Iterator<Character> iterator() {
        return new Iterator<Character>() {
            private int index = -1;
            @Override
            public boolean hasNext() {
                return index + 1 < size();
            }
            @Override
            public Character next() {
                index ++;
                return get(index);
            }
        };
    }

}
