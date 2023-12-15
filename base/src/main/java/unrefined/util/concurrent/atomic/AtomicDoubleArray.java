/*
 * Originally written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 *
 * Modified by Karstian Lee in order to support other array types except int, long and reference
 */

package unrefined.util.concurrent.atomic;

import java.util.concurrent.atomic.AtomicLongArray;

/**
 * An {@code double} array in which elements may be updated atomically.
 * See the {@link java.lang.invoke.VarHandle} specification for descriptions of the
 * properties of atomic accesses.
 *
 * @author Doug Lea
 * @author Karstian Lee
 */
public class AtomicDoubleArray implements java.io.Serializable {

    private static final long serialVersionUID = 3163525109562157012L;

    /**
     * Making this non-final is the lesser evil according to Effective
     * Java 2nd Edition Item 76: Write readObject methods defensively.
     */
    private transient AtomicLongArray array;

    /**
     * Creates a new AtomicDoubleArray of the given length, with all
     * elements initially zero.
     *
     * @param length the length of the array
     */
    public AtomicDoubleArray(int length) {
        array = new AtomicLongArray(length);
    }

    /**
     * Creates a new AtomicDoubleArray with the same length as, and
     * all elements copied from, the given array.
     *
     * @param array the array to copy elements from
     * @throws NullPointerException if array is null
     */
    public AtomicDoubleArray(double[] array) {
        int length = array.length;
        long[] longArray = new long[length];
        for (int i = 0; i < length; i ++) {
            longArray[i] = Double.doubleToRawLongBits(array[i]);
        }
        this.array = new AtomicLongArray(longArray);
    }

    /**
     * Returns the length of the array.
     *
     * @return the length of the array
     */
    public final int length() {
        return array.length();
    }

    /**
     * Returns the current value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     *
     * @param i the index
     * @return the current value
     */
    public final double get(int i) {
        return Double.longBitsToDouble(array.get(i));
    }

    /**
     * Sets the element at index {@code i} to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setVolatile}.
     *
     * @param i the index
     * @param newValue the new value
     */
    public final void set(int i, double newValue) {
        array.set(i, Double.doubleToRawLongBits(newValue));
    }

    /**
     * Sets the element at index {@code i} to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setRelease}.
     *
     * @param i the index
     * @param newValue the new value
     */
    public final void lazySet(int i, double newValue) {
        array.lazySet(i, Double.doubleToRawLongBits(newValue));
    }

    /**
     * Atomically sets the element at index {@code i} to {@code
     * newValue} and returns the old value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndSet}.
     *
     * @param i the index
     * @param newValue the new value
     * @return the previous value
     */
    public final double getAndSet(int i, double newValue) {
        return Double.longBitsToDouble(array.getAndSet(i, Double.doubleToRawLongBits(newValue)));
    }

    /**
     * Atomically sets the element at index {@code i} to {@code
     * newValue} if the element's current value {@code == expectedValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#compareAndSet}.
     *
     * @param i the index
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public final boolean compareAndSet(int i, double expectedValue, double newValue) {
        return array.compareAndSet(i, Double.doubleToRawLongBits(expectedValue), Double.doubleToRawLongBits(newValue));
    }

    /**
     * Possibly atomically sets the element at index {@code i} to
     * {@code newValue} if the element's current value {@code == expectedValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#weakCompareAndSet}.
     *
     * <p>May <a
     * href="http://download.oracle.com/javase/7/docs/api/java/util/concurrent/atomic/package-summary.html#Spurious">
     * fail spuriously</a> and does not provide ordering guarantees, so is only rarely an appropriate
     * alternative to {@code compareAndSet}.
     *
     * @param i the index
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful
     */
    public final boolean weakCompareAndSet(int i, double expectedValue, double newValue) {
        return array.weakCompareAndSet(i, Double.doubleToRawLongBits(expectedValue), Double.doubleToRawLongBits(newValue));
    }

    /**
     * Atomically increments the value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(i, 1.0)}.
     *
     * @param i the index
     * @return the previous value
     */
    public final double getAndIncrement(int i) {
        return getAndAdd(i, 1.0);
    }

    /**
     * Atomically decrements the value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(i, -1.0)}.
     *
     * @param i the index
     * @return the previous value
     */
    public final double getAndDecrement(int i) {
        return getAndAdd(i, -1.0);
    }

    /**
     * Atomically adds the given value to the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * @param i the index
     * @param delta the value to add
     * @return the previous value
     */
    public final double getAndAdd(int i, double delta) {
        while (true) {
            long current = array.get(i);
            double currentVal = Double.longBitsToDouble(current);
            double nextVal = delta + currentVal;
            long next = Double.doubleToRawLongBits(nextVal);
            if (array.compareAndSet(i, current, next)) {
                return currentVal;
            }
        }
    }

    /**
     * Atomically increments the value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(i, 1.0)}.
     *
     * @param i the index
     * @return the updated value
     */
    public final double incrementAndGet(int i) {
        return addAndGet(i, 1.0);
    }

    /**
     * Atomically decrements the value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(i, -1.0)}.
     *
     * @param i the index
     * @return the updated value
     */
    public final double decrementAndGet(int i) {
        return addAndGet(i, -1.0);
    }

    /**
     * Atomically adds the given value to the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * @param i the index
     * @param delta the value to add
     * @return the updated value
     */
    public final double addAndGet(int i, double delta) {
        while (true) {
            long current = array.get(i);
            double currentVal = Double.longBitsToDouble(current);
            double nextVal = delta + currentVal;
            long next = Double.doubleToRawLongBits(nextVal);
            if (array.compareAndSet(i, current, next)) {
                return nextVal;
            }
        }
    }

    /**
     * Returns the String representation of the current values of array.
     * @return the String representation of the current values of array
     */
    public String toString() {
        int iMax = array.length() - 1;
        if (iMax == -1) return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i ++) {
            b.append(Double.longBitsToDouble(array.get(i)));
            if (i == iMax) return b.append(']').toString();
            else b.append(',').append(' ');
        }
    }

    /**
     * Saves the state to a stream (that is, serializes it).
     *
     * @serialData The length of the array is emitted (int), followed by all of its elements (each a
     * {@code double}) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();

        // Write out array length
        int length = length();
        out.writeInt(length);

        // Write out all elements in the proper order.
        for (int i = 0; i < length; i ++) {
            out.writeDouble(Double.longBitsToDouble(array.get(i)));
        }
    }

    /** 
     * Reconstitutes the instance from a stream (that is, deserializes it). 
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();

        int length = in.readInt();
        
        long[] longArray = new long[length];
        for (int i = 0; i < length; i ++) {
            longArray[i] = Double.doubleToRawLongBits(in.readDouble());
        }
        array = new AtomicLongArray(longArray);
    }

}
