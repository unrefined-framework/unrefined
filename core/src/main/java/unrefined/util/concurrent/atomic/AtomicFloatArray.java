/*
 * Originally written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 *
 * Modified by Karstian Lee in order to support other array types except int, long and reference
 */

package unrefined.util.concurrent.atomic;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * An {@code float} array in which elements may be updated atomically.
 * See the {@link java.lang.invoke.VarHandle} specification for descriptions of the
 * properties of atomic accesses.
 *
 * @author Doug Lea
 * @author Karstian Lee
 */
public class AtomicFloatArray implements java.io.Serializable {

    private static final long serialVersionUID = 8133021301133524798L;

    /**
     * Making this non-final is the lesser evil according to Effective
     * Java 2nd Edition Item 76: Write readObject methods defensively.
     */
    private transient AtomicIntegerArray array;

    /**
     * Creates a new AtomicFloatArray of the given length, with all
     * elements initially zero.
     *
     * @param length the length of the array
     */
    public AtomicFloatArray(int length) {
        array = new AtomicIntegerArray(length);
    }

    /**
     * Creates a new AtomicFloatArray with the same length as, and
     * all elements copied from, the given array.
     *
     * @param array the array to copy elements from
     * @throws NullPointerException if array is null
     */
    public AtomicFloatArray(float[] array) {
        int length = array.length;
        int[] intArray = new int[length];
        for (int i = 0; i < length; i ++) {
            intArray[i] = Float.floatToRawIntBits(array[i]);
        }
        this.array = new AtomicIntegerArray(intArray);
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
    public final float get(int i) {
        return Float.intBitsToFloat(array.get(i));
    }

    /**
     * Sets the element at index {@code i} to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setVolatile}.
     *
     * @param i the index
     * @param newValue the new value
     */
    public final void set(int i, float newValue) {
        array.set(i, Float.floatToRawIntBits(newValue));
    }

    /**
     * Sets the element at index {@code i} to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setRelease}.
     *
     * @param i the index
     * @param newValue the new value
     */
    public final void lazySet(int i, float newValue) {
        array.lazySet(i, Float.floatToRawIntBits(newValue));
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
    public final float getAndSet(int i, float newValue) {
        return Float.intBitsToFloat(array.getAndSet(i, Float.floatToRawIntBits(newValue)));
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
    public final boolean compareAndSet(int i, float expectedValue, float newValue) {
        return array.compareAndSet(i, Float.floatToRawIntBits(expectedValue), Float.floatToRawIntBits(newValue));
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
    public final boolean weakCompareAndSet(int i, float expectedValue, float newValue) {
        return array.weakCompareAndSet(i, Float.floatToRawIntBits(expectedValue), Float.floatToRawIntBits(newValue));
    }

    /**
     * Atomically increments the value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(i, 1.0f)}.
     *
     * @param i the index
     * @return the previous value
     */
    public final float getAndIncrement(int i) {
        return getAndAdd(i, 1.0f);
    }

    /**
     * Atomically decrements the value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(i, -1.0f)}.
     *
     * @param i the index
     * @return the previous value
     */
    public final float getAndDecrement(int i) {
        return getAndAdd(i, -1.0f);
    }

    /**
     * Atomically adds the given value to the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * @param i the index
     * @param delta the value to add
     * @return the previous value
     */
    public final float getAndAdd(int i, float delta) {
        while (true) {
            int current = array.get(i);
            float currentVal = Float.intBitsToFloat(current);
            float nextVal = delta + currentVal;
            int next = Float.floatToRawIntBits(nextVal);
            if (array.compareAndSet(i, current, next)) {
                return currentVal;
            }
        }
    }

    /**
     * Atomically increments the value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(i, 1.0f)}.
     *
     * @param i the index
     * @return the updated value
     */
    public final float incrementAndGet(int i) {
        return addAndGet(i, 1.0f);
    }

    /**
     * Atomically decrements the value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(i, -1.0f)}.
     *
     * @param i the index
     * @return the updated value
     */
    public final float decrementAndGet(int i) {
        return addAndGet(i, -1.0f);
    }

    /**
     * Atomically adds the given value to the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * @param i the index
     * @param delta the value to add
     * @return the updated value
     */
    public final float addAndGet(int i, float delta) {
        while (true) {
            int current = array.get(i);
            float currentVal = Float.intBitsToFloat(current);
            float nextVal = delta + currentVal;
            int next = Float.floatToRawIntBits(nextVal);
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
            b.append(Float.intBitsToFloat(array.get(i)));
            if (i == iMax) return b.append(']').toString();
            else b.append(',').append(' ');
        }
    }

    /**
     * Saves the state to a stream (that is, serializes it).
     *
     * @serialData The length of the array is emitted (int), followed by all of its elements (each a
     * {@code float}) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();

        // Write out array length
        int length = length();
        out.writeInt(length);

        // Write out all elements in the proper order.
        for (int i = 0; i < length; i ++) {
            out.writeFloat(Float.intBitsToFloat(array.get(i)));
        }
    }

    /** 
     * Reconstitutes the instance from a stream (that is, deserializes it). 
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();

        int length = in.readInt();
        
        int[] intArray = new int[length];
        for (int i = 0; i < length; i ++) {
            intArray[i] = Float.floatToRawIntBits(in.readFloat());
        }
        array = new AtomicIntegerArray(intArray);
    }

}
