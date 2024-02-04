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
import java.util.Objects;

/**
 * CircularArray is a generic circular array data structure that provides O(1) random read, O(1)
 * prepend and O(1) append. The CircularArray automatically grows its capacity when number of added
 * items is over its capacity.
 */
public class CircularArray<E> implements Cloneable, Iterable<E> {

    private E[] elements;
    private int head;
    private int tail;
    private int capacityBitmask;

    @SuppressWarnings("unchecked")
    private void doubleCapacity() {
        int n = elements.length;
        int r = n - head;
        int newCapacity = n << 1;
        if (newCapacity < 0) {
            throw new RuntimeException("Max array capacity exceeded");
        }
        Object[] a = new Object[newCapacity];
        System.arraycopy(elements, head, a, 0, r);
        System.arraycopy(elements, 0, a, r, head);
        elements = (E[]) a;
        head = 0;
        tail = n;
        capacityBitmask = newCapacity - 1;
    }

    /**
     * Creates a circular array with default capacity.
     */
    public CircularArray() {
        this(8);
    }

    /**
     * Creates a circular array with capacity for at least {@code minCapacity}
     * elements.
     *
     * @param minCapacity the minimum capacity, between 1 and 2^30 inclusive
     */
    @SuppressWarnings("unchecked")
    public CircularArray(int minCapacity) {
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
        elements = (E[]) new Object[arrayCapacity];
    }

    @SuppressWarnings("unchecked")
    @Override
    public CircularArray<E> clone() {
        CircularArray<E> clone;
        try {
            clone = (CircularArray<E>) super.clone();
        }
        catch (CloneNotSupportedException e) {
            clone = new CircularArray<>();
            clone.head = head;
            clone.tail = tail;
            clone.capacityBitmask = capacityBitmask;
        }
        clone.elements = elements.clone();
        return clone;
    }

    /**
     * Add an element in front of the CircularArray.
     * @param e  Element to add.
     */
    public void addFirst(E e) {
        head = (head - 1) & capacityBitmask;
        elements[head] = e;
        if (head == tail) {
            doubleCapacity();
        }
    }

    /**
     * Add an element at end of the CircularArray.
     * @param e  Element to add.
     */
    public void addLast(E e) {
        elements[tail] = e;
        tail = (tail + 1) & capacityBitmask;
        if (tail == head) {
            doubleCapacity();
        }
    }

    /**
     * Remove first element from front of the CircularArray and return it.
     * @return  The element removed.
     * @throws ArrayIndexOutOfBoundsException if CircularArray is empty.
     */
    public E popFirst() {
        if (head == tail) {
            throw new ArrayIndexOutOfBoundsException();
        }
        E result = elements[head];
        elements[head] = null;
        head = (head + 1) & capacityBitmask;
        return result;
    }

    /**
     * Remove last element from end of the CircularArray and return it.
     * @return  The element removed.
     * @throws ArrayIndexOutOfBoundsException if CircularArray is empty.
     */
    public E popLast() {
        if (head == tail) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int t = (tail - 1) & capacityBitmask;
        E result = elements[t];
        elements[t] = null;
        tail = t;
        return result;
    }

    /**
     * Remove all elements from the CircularArray.
     */
    public void clear() {
        removeFromStart(size());
    }

    /**
     * Remove multiple elements from front of the CircularArray, ignore when numOfElements
     * is less than or equals to 0.
     * @param numOfElements  Number of elements to remove.
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
        int end = elements.length;
        if (numOfElements < end - head) {
            end = head + numOfElements;
        }
        for (int i = head; i < end; i++) {
            elements[i] = null;
        }
        int removed = (end - head);
        numOfElements -= removed;
        head = (head + removed) & capacityBitmask;
        if (numOfElements > 0) {
            // head wrapped to 0
            for (int i = 0; i < numOfElements; i ++) {
                elements[i] = null;
            }
            head = numOfElements;
        }
    }

    /**
     * Remove multiple elements from end of the CircularArray, ignore when numOfElements
     * is less than or equals to 0.
     * @param numOfElements  Number of elements to remove.
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
        int start = 0;
        if (numOfElements < tail) {
            start = tail - numOfElements;
        }
        for (int i = start; i < tail; i++) {
            elements[i] = null;
        }
        int removed = (tail - start);
        numOfElements -= removed;
        tail = tail - removed;
        if (numOfElements > 0) {
            // tail wrapped to elements.length
            tail = elements.length;
            int newTail = tail - numOfElements;
            for (int i = newTail; i < tail; i ++) {
                elements[i] = null;
            }
            tail = newTail;
        }
    }

    /**
     * Get first element of the CircularArray.
     * @return The first element.
     * @throws ArrayIndexOutOfBoundsException if CircularArray is empty.
     */
    public E getFirst() {
        if (head == tail) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return elements[head];
    }

    /**
     * Get last element of the CircularArray.
     * @return The last element.
     * @throws ArrayIndexOutOfBoundsException if CircularArray is empty.
     */
    public E getLast() {
        if (head == tail) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return elements[(tail - 1) & capacityBitmask];
    }

    /**
     * Get nth (0 <= n <= size()-1) element of the CircularArray.
     * @param n  The zero based element index in the CircularArray.
     * @return The nth element.
     * @throws ArrayIndexOutOfBoundsException if n < 0 or n >= size().
     */
    public E get(int n) {
        if (n < 0 || n >= size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return elements[(head + n) & capacityBitmask];
    }

    /**
     * Get number of elements in the CircularArray.
     * @return Number of elements in the CircularArray.
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
        if (!(o instanceof CircularArray)) return false;

        CircularArray<?> that = (CircularArray<?>) o;

        int size = size();
        if (size != that.size()) return false;

        for (int i = 0; i < size; i ++) {
            if (!Objects.equals(get(head + i), that.get(that.head + i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int size = size();
        int result = size;
        for (int i = 0; i < size; i ++) {
            E element = get(head + i);
            result = 31 * result + Objects.hashCode(element);
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
            E element = get(head + i);
            buffer.append(element == this ? "(this Collection)" : element);
        }
        buffer.append(']');
        return buffer.toString();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int index = -1;
            @Override
            public boolean hasNext() {
                return index + 1 < size();
            }
            @Override
            public E next() {
                index ++;
                return get(index);
            }
        };
    }

}
