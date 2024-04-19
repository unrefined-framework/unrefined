/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package unrefined.util;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * <code>GrowableArray</code> is a variable size contiguous indexable array of Objects. The size of
 * the container is the number of Objects it contains. The capacity of the container
 * is the number of Objects it can hold.
 * <p>
 * Objects may be inserted at any position up to the size of the container, thus
 * increasing the size of the container. Objects at any position in the container may
 * be removed, thus shrinking the size of the container. Objects at any position in
 * the container may be replaced, which does not affect the container's size.
 * <p>
 * The capacity of a <code>GrowableArray</code> may be specified when the container is created. If the
 * capacity of the container is exceeded, the capacity is increased (doubled by
 * default).
 */
public class GrowableArray<E> implements Cloneable, Iterable<E> {

    /**
     * A counter for changes to the list.
     */
    protected int modCount;

    /**
     * The number of elements or the size of the GrowableArray.
     */
    private int size;

    /**
     * The elements of the GrowableArray.
     */
    private E[] elements;

    /**
     * How many elements should be added to the GrowableArray when it is detected that
     * it needs to grow to accommodate extra entries. If this value is zero or
     * negative the size will be doubled if an increase is needed.
     */
    private int capacityIncrement;

    /**
     * Constructs a new GrowableArray using the default capacity.
     */
    public GrowableArray() {
        this(10, 0);
    }

    /**
     * Constructs a new GrowableArray using the specified capacity.
     * 
     * @param capacity
     *            the initial capacity of the new GrowableArray.
     * @throws IllegalArgumentException
     *             if {@code capacity} is negative.
     */
    public GrowableArray(int capacity) {
        this(capacity, 0);
    }

    /**
     * Constructs a new GrowableArray using the specified capacity and capacity
     * increment.
     * 
     * @param capacity
     *            the initial capacity of the new GrowableArray.
     * @param capacityIncrement
     *            the amount to increase the capacity when this GrowableArray is full.
     * @throws IllegalArgumentException
     *             if {@code capacity} is negative.
     */
    public GrowableArray(int capacity, int capacityIncrement) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity < 0");
        }
        elements = newElementArray(capacity);
        size = 0;
        this.capacityIncrement = capacityIncrement;
    }

    @SuppressWarnings("unchecked")
    private E[] newElementArray(int size) {
        return (E[]) new Object[size];
    }

    /**
     * Adds the specified object into this GrowableArray at the specified index. The
     * object is inserted before any element with the same or a higher index
     * increasing their index by 1. If the index is equal to the size of this
     * GrowableArray, the object is added at the end.
     * 
     * @param index
     *            the index at which to insert the element.
     * @param e
     *            the object to insert in this GrowableArray.
     * @throws ArrayIndexOutOfBoundsException
     *                if {@code index < 0 || index > size()}.
     * @see #add
     * @see #size
     */
    public void add(int index, E e) {
        if (0 <= index && index <= size) {
            if (size == elements.length) {
                growByOne();
            }
            int count = size - index;
            if (count > 0) {
                System.arraycopy(elements, index, elements,
                        index + 1, count);
            }
            elements[index] = e;
            size++;
            modCount++;
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    /**
     * Adds the specified object at the end of this GrowableArray.
     * 
     * @param e
     *            the object to add to the GrowableArray.
     * @return {@code true}
     */
    public boolean add(E e) {
        if (size == elements.length) {
            growByOne();
        }
        elements[size++] = e;
        modCount++;
        return true;
    }

    /**
     * Returns the number of elements this GrowableArray can hold without growing.
     * 
     * @return the capacity of this GrowableArray.
     * @see #ensureCapacity
     * @see #size
     */
    public int capacity() {
        return elements.length;
    }

    /**
     * Removes all elements from this GrowableArray, leaving the size zero and the
     * capacity unchanged.
     * 
     * @see #isEmpty
     * @see #size
     */
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        modCount++;
        size = 0;
    }

    /**
     * Returns a new GrowableArray with the same elements, size, capacity and capacity
     * increment as this GrowableArray.
     * 
     * @return a shallow copy of this GrowableArray.
     * @see Cloneable
     */
    @Override
    @SuppressWarnings("unchecked")
    public GrowableArray<E> clone() {
        GrowableArray<E> clone;
        try {
            clone = (GrowableArray<E>) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            clone = new GrowableArray<>();
            clone.capacityIncrement = capacityIncrement;
        }
        clone.elements = elements.clone();
        return clone;
    }

    /**
     * Searches this GrowableArray for the specified object.
     * 
     * @param e
     *            the object to look for in this GrowableArray.
     * @return {@code true} if object is an element of this GrowableArray,
     *         {@code false} otherwise.
     * @see #indexOf(Object)
     * @see #indexOf(Object, int)
     * @see Object#equals
     */
    public boolean contains(E e) {
        return indexOf(e, 0) != -1;
    }

    /**
     * Ensures that this GrowableArray can hold the specified number of elements
     * without growing.
     * 
     * @param minimumCapacity
     *            the minimum number of elements that this GrowableArray will hold
     *            before growing.
     * @see #capacity
     */
    public void ensureCapacity(int minimumCapacity) {
        if (elements.length < minimumCapacity) {
            int next = (capacityIncrement <= 0 ? elements.length
                    : capacityIncrement)
                    + elements.length;
            grow(Math.max(minimumCapacity, next));
        }
    }

    /**
     * Returns the first element in this GrowableArray.
     * 
     * @return the element at the first position.
     * @throws NoSuchElementException
     *                if this GrowableArray is empty.
     * @see #get
     * @see #getLast
     * @see #size
     */
    public E getFirst() {
        if (size > 0) {
            return elements[0];
        }
        throw new NoSuchElementException();
    }

    /**
     * Returns the element at the specified index in this GrowableArray.
     * 
     * @param index
     *            the index of the element to return in this GrowableArray.
     * @return the element at the specified index.
     * @throws ArrayIndexOutOfBoundsException
     *                if {@code index < 0 || index >= size()}.
     * @see #size
     */
    public E get(int index) {
        if (index < size) {
            return elements[index];
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    private void grow(int newCapacity) {
        E[] newData = newElementArray(newCapacity);
        // Assumes elementCount is <= newCapacity
        //assert size <= newCapacity;
        System.arraycopy(elements, 0, newData, 0, size);
        elements = newData;
    }

    private void growByOne() {
        int adding = 0;
        if (capacityIncrement <= 0) {
            if ((adding = elements.length) == 0) {
                adding = 1;
            }
        } else {
            adding = capacityIncrement;
        }

        E[] newData = newElementArray(elements.length + adding);
        System.arraycopy(elements, 0, newData, 0, size);
        elements = newData;
    }

    /**
     * Searches in this GrowableArray for the index of the specified object. The search
     * for the object starts at the beginning and moves towards the end of this
     * GrowableArray.
     * 
     * @param e
     *            the object to find in this GrowableArray.
     * @return the index in this GrowableArray of the specified element, -1 if the
     *         element isn't found.
     * @see #contains
     * @see #lastIndexOf(Object)
     * @see #lastIndexOf(Object, int)
     */
    public int indexOf(E e) {
        return indexOf(e, 0);
    }

    /**
     * Searches in this GrowableArray for the index of the specified object. The search
     * for the object starts at the specified index and moves towards the end
     * of this GrowableArray.
     * 
     * @param e
     *            the object to find in this GrowableArray.
     * @param index
     *            the index at which to start searching.
     * @return the index in this GrowableArray of the specified element, -1 if the
     *         element isn't found.
     * @throws ArrayIndexOutOfBoundsException
     *                if {@code index < 0}.
     * @see #contains
     * @see #lastIndexOf(Object)
     * @see #lastIndexOf(Object, int)
     */
    public int indexOf(E e, int index) {
        if (e != null) {
            for (int i = index; i < size; i++) {
                if (e.equals(elements[i])) {
                    return i;
                }
            }
        } else {
            for (int i = index; i < size; i++) {
                if (elements[i] == null) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns if this GrowableArray has no elements, a size of zero.
     * 
     * @return {@code true} if this GrowableArray has no elements, {@code false}
     *         otherwise.
     * @see #size
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the last element in this GrowableArray.
     * 
     * @return the element at the last position.
     * @throws NoSuchElementException
     *                if this GrowableArray is empty.
     * @see #get
     * @see #getFirst
     * @see #size
     */
    public E getLast() {
        try {
            return elements[size - 1];
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
    }

    /**
     * Searches in this GrowableArray for the index of the specified object. The search
     * for the object starts at the end and moves towards the start of this
     * GrowableArray.
     * 
     * @param e
     *            the object to find in this GrowableArray.
     * @return the index in this GrowableArray of the specified element, -1 if the
     *         element isn't found.
     * @see #contains
     * @see #indexOf(Object)
     * @see #indexOf(Object, int)
     */
    public int lastIndexOf(E e) {
        return lastIndexOf(e, size - 1);
    }

    /**
     * Searches in this GrowableArray for the index of the specified object. The search
     * for the object starts at the specified index and moves towards the
     * start of this GrowableArray.
     * 
     * @param e
     *            the object to find in this GrowableArray.
     * @param index
     *            the index at which to start searching.
     * @return the index in this GrowableArray of the specified element, -1 if the
     *         element isn't found.
     * @throws ArrayIndexOutOfBoundsException
     *                if {@code index >= size()}.
     * @see #contains
     * @see #indexOf(Object)
     * @see #indexOf(Object, int)
     */
    public int lastIndexOf(E e, int index) {
        if (index < size) {
            if (e != null) {
                for (int i = index; i >= 0; i--) {
                    if (e.equals(elements[i])) {
                        return i;
                    }
                }
            } else {
                for (int i = index; i >= 0; i--) {
                    if (elements[i] == null) {
                        return i;
                    }
                }
            }
            return -1;
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    /**
     * Removes the object at the specified index from this GrowableArray. All
     * elements with an index bigger than {@code index} have their index
     * decreased by 1.
     * 
     * @param index
     *            the index of the object to remove.
     * @return the removed object.
     * @throws IndexOutOfBoundsException
     *                if {@code index < 0 || index >= size()}.
     */
    public E removeAt(int index) {
        if (index < size) {
            E result = elements[index];
            size--;
            int size = this.size - index;
            if (size > 0) {
                System.arraycopy(elements, index + 1, elements,
                        index, size);
            }
            elements[this.size] = null;
            modCount++;
            return result;
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    /**
     * Removes the first occurrence, starting at the beginning and moving
     * towards the end, of the specified object from this GrowableArray. All elements
     * with an index bigger than the element that gets removed have their index
     * decreased by 1.
     * 
     * @param e
     *            the object to remove from this GrowableArray.
     * @return {@code true} if the specified object was found, {@code false}
     *         otherwise.
     * @see #clear
     * @see #removeAt
     * @see #size
     */
    public boolean remove(E e) {
        int index;
        if ((index = indexOf(e, 0)) == -1) {
            return false;
        }
        if (0 <= index && index < size) {
            size--;
            int size = this.size - index;
            if (size > 0) {
                System.arraycopy(elements, index + 1, elements,
                        index, size);
            }
            elements[this.size] = null;
            modCount++;
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return true;
    }

    /**
     * Removes the Objects in the specified range from the start to the end, but not
     * including, end index. All elements with an index bigger than or equal to
     * {@code end} have their index decreased by {@code end - start}.
     * 
     * @param start
     *            the index at which to start removing.
     * @param end
     *            the index one past the end of the range to remove.
     * @throws IndexOutOfBoundsException
     *                if {@code start < 0, start > end} or
     *                {@code end > size()}.
     */
    public void removeAtRange(int start, int end) {
        if (start >= 0 && start <= end && end <= size) {
            if (start == end) {
                return;
            }
            if (end != size) {
                System.arraycopy(elements, end, elements, start,
                        size - end);
                int newCount = size - (end - start);
                Arrays.fill(elements, newCount, size, null);
                size = newCount;
            } else {
                Arrays.fill(elements, start, size, null);
                size = start;
            }
            modCount++;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Replaces the element at the specified index in this GrowableArray with the
     * specified object.
     * 
     * @param index
     *            the index at which to put the specified object.
     * @param e
     *            the object to add to this GrowableArray.
     * @return the previous element at the index.
     * @throws ArrayIndexOutOfBoundsException
     *                if {@code index < 0 || index >= size()}.
     * @see #size
     */
    public E set(int index, E e) {
        if (index < size) {
            E result = elements[index];
            elements[index] = e;
            return result;
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    /**
     * Sets the size of this GrowableArray to the specified size. If there are more
     * than length elements in this GrowableArray, the elements at end are lost. If
     * there are less than length elements in the GrowableArray, the additional
     * elements contain null.
     * 
     * @param length
     *            the new size of this GrowableArray.
     * @see #size
     */
    public void setSize(int length) {
        if (length == size) {
            return;
        }
        ensureCapacity(length);
        if (size > length) {
            Arrays.fill(elements, length, size, null);
        }
        size = length;
        modCount++;
    }

    /**
     * Returns the number of elements in this GrowableArray.
     * 
     * @return the number of elements in this GrowableArray.
     * @see #size
     * @see #getLast
     */
    public int size() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrowableArray)) return false;

        GrowableArray<?> that = (GrowableArray<?>) o;

        int size = size();
        if (size != that.size()) return false;

        for (int i = 0; i < size; i ++) {
            if (!Objects.equals(get(i), that.get(i))) {
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
            result = 31 * result + Objects.hashCode(get(i));
        }
        return result;
    }

    public E[] array() {
        return elements;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        int length = size - 1;
        StringBuilder buffer = new StringBuilder(size * 16);
        buffer.append('[');
        for (int i = 0; i < length; i++) {
            if (elements[i] == this) {
                buffer.append("(this Collection)");
            } else {
                buffer.append(elements[i]);
            }
            buffer.append(", ");
        }
        if (elements[length] == this) {
            buffer.append("(this Collection)");
        } else {
            buffer.append(elements[length]);
        }
        buffer.append(']');
        return buffer.toString();
    }

    /**
     * Attempts to copy elements contained by this GrowableArray into the
     * corresponding elements of the supplied {@code E} array.
     *
     * @param srcOffset starting position in this GrowableArray.
     * @param dst
     *            the {@code E} array into which the elements of this
     *            GrowableArray are copied.
     * @param dstOffset starting position in the destination array.
     * @param size the number of array elements to be copied.
     * @throws IndexOutOfBoundsException
     *             if {@code dst} is not big enough or {@code size} too large.
     */
    public void copyTo(int srcOffset, E[] dst, int dstOffset, int size) {
        if (size > this.size) throw new ArrayIndexOutOfBoundsException(size);
        System.arraycopy(elements, srcOffset, dst, dstOffset, size);
    }

    /**
     * Attempts to copy elements contained by this GrowableArray into the
     * corresponding elements of the supplied {@code E} array.
     *
     * @param srcOffset starting position in this GrowableArray.
     * @param dst
     *            the {@code E} array into which the elements of this
     *            GrowableArray are copied.
     * @param dstOffset starting position in the destination array.
     * @throws IndexOutOfBoundsException
     *             if {@code dst} is not big enough.
     */
    public void copyTo(int srcOffset, E[] dst, int dstOffset) {
        System.arraycopy(elements, srcOffset, dst, dstOffset, size);
    }

    /**
     * Attempts to copy elements contained by this GrowableArray into the
     * corresponding elements of the supplied {@code E} array.
     *
     * @param dst
     *            the {@code E} array into which the elements of this
     *            GrowableArray are copied.
     * @throws IndexOutOfBoundsException
     *             if {@code elements} is not big enough.
     * @see #clone
     */
    public void copyTo(E[] dst) {
        System.arraycopy(elements, 0, dst, 0, size);
    }

    /**
     * Attempts to copy elements contained by the corresponding elements
     * of the supplied {@code E} array into this GrowableArray.
     *
     * @param src
     *            the {@code E} array from which the elements copied into this
     *            GrowableArray.
     * @param srcOffset starting position in the source array.
     * @param dstOffset starting position in this GrowableArray.
     * @param size the number of array elements to be copied.
     * @throws IndexOutOfBoundsException
     *             if {@code src} is not big enough or {@code size} too large.
     */
    public void copyFrom(E[] src, int srcOffset, int dstOffset, int size) {
        if (size > this.size) throw new ArrayIndexOutOfBoundsException(size);
        System.arraycopy(src, srcOffset, elements, dstOffset, size);
    }

    /**
     * Attempts to copy elements contained by the corresponding elements
     * of the supplied {@code E} array into this GrowableArray.
     *
     * @param src
     *            the {@code E} array from which the elements copied into this
     *            GrowableArray.
     * @param srcOffset starting position in the source array.
     * @param dstOffset starting position in this GrowableArray.
     * @throws IndexOutOfBoundsException
     *             if {@code dst} is not big enough.
     */
    public void copyFrom(E[] src, int srcOffset, int dstOffset) {
        System.arraycopy(src, srcOffset, elements, dstOffset, size);
    }

    /**
     * Attempts to copy elements contained by the corresponding elements
     * of the supplied {@code E} array into this GrowableArray.
     *
     * @param src
     *            the {@code E} array from which the elements copied into this
     *            GrowableArray.
     * @throws IndexOutOfBoundsException
     *             if {@code src} is not big enough.
     * @see #clone
     */
    public void copyFrom(E[] src) {
        System.arraycopy(src, 0, elements, 0, size);
    }

    /**
     * Sets the capacity of this GrowableArray to be the same as the size.
     * 
     * @see #capacity
     * @see #ensureCapacity
     * @see #size
     */
    public void trimToSize() {
        if (elements.length != size) {
            grow(size);
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int numLeft = size();
            private int expectedModCount = modCount;
            private int lastPosition = -1;
            public boolean hasNext() {
                return numLeft > 0;
            }
            public E next() {
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException();
                }
                try {
                    int index = size() - numLeft;
                    E result = get(index);
                    lastPosition = index;
                    numLeft--;
                    return result;
                } catch (IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }
            public void remove() {
                if (lastPosition == -1) {
                    throw new IllegalStateException();
                }
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException();
                }
                try {
                    if (lastPosition == size() - numLeft) {
                        numLeft--; // we're removing after a call to previous()
                    }
                    GrowableArray.this.removeAt(lastPosition);
                } catch (IndexOutOfBoundsException e) {
                    throw new ConcurrentModificationException();
                }
                expectedModCount = modCount;
                lastPosition = -1;
            }
        };
    }

}
