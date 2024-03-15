/*
 * Originally written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 *
 * Modified by Karstian Lee in order to support other data types except int, long, boolean and reference
 */

package unrefined.util.concurrent.atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * An {@code float} value that may be updated atomically.  See the
 * {@link java.lang.invoke.VarHandle} specification for descriptions of the properties
 * of atomic accesses. An {@code AtomicFloat} is used in
 * applications such as atomically incremented counters, and cannot be
 * used as a replacement for an {@link Float}. However,
 * this class does extend {@code Number} to allow uniform access by
 * tools and utilities that deal with numerically-based classes.
 *
 * @author Doug Lea
 * @author Karstian Lee
 */
public class AtomicFloat extends Number implements java.io.Serializable {

    private static final long serialVersionUID = 8290483888370477251L;
    
    private transient volatile int value;

    private static final AtomicIntegerFieldUpdater<AtomicFloat> UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(AtomicFloat.class, "value");

    public AtomicFloat(float initialValue) {
        value = Float.floatToRawIntBits(initialValue);
    }

    /**
     * Creates a new AtomicFloat with initial value {@code 0.0f}.
     */
    public AtomicFloat() {
    }

    /**
     * Returns the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     *
     * @return the current value
     */
    public final float get() {
        return Float.intBitsToFloat(value);
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setVolatile}.
     *
     * @param newValue the new value
     */
    public final void set(float newValue) {
        value = Float.floatToRawIntBits(newValue);
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setRelease}.
     *
     * @param newValue the new value
     */
    public final void lazySet(float newValue) {
        UPDATER.lazySet(this, Float.floatToRawIntBits(newValue));
    }

    /**
     * Atomically sets the value to {@code newValue} and returns the old value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndSet}.
     *
     * @param newValue the new value
     * @return the previous value
     */
    public final float getAndSet(float newValue) {
        return Float.intBitsToFloat(UPDATER.getAndSet(this, Float.floatToRawIntBits(newValue)));
    }

    /**
     * Atomically sets the value to {@code newValue}
     * if the current value {@code == expectedValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#compareAndSet}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public final boolean compareAndSet(float expectedValue, float newValue) {
        return UPDATER.compareAndSet(this, Float.floatToRawIntBits(expectedValue), Float.floatToRawIntBits(newValue));
    }

    /**
     * Possibly atomically sets the value to {@code newValue}
     * if the current value {@code == expectedValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#weakCompareAndSet}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful
     */
    public final boolean weakCompareAndSet(float expectedValue, float newValue) {
        return UPDATER.weakCompareAndSet(this, Float.floatToRawIntBits(expectedValue), Float.floatToRawIntBits(newValue));
    }

    /**
     * Atomically increments the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(1.0f)}.
     *
     * @return the previous value
     */
    public final float getAndIncrement() {
        return getAndAdd(1.0f);
    }

    /**
     * Atomically decrements the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(-1.0f)}.
     *
     * @return the previous value
     */
    public final float getAndDecrement() {
        return getAndAdd(-1.0f);
    }

    /**
     * Atomically adds the given value to the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * @param delta the value to add
     * @return the previous value
     */
    public final float getAndAdd(float delta) {
        while (true) {
            int current = value;
            float currentVal = Float.intBitsToFloat(current);
            float nextVal = delta + currentVal;
            int next = Float.floatToRawIntBits(nextVal);
            if (UPDATER.compareAndSet(this, current, next)) {
                return currentVal;
            }
        }
    }

    /**
     * Atomically increments the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(1.0f)}.
     *
     * @return the updated value
     */
    public final float incrementAndGet() {
        return addAndGet(1.0f);
    }

    /**
     * Atomically decrements the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(-1.0f)}.
     *
     * @return the updated value
     */
    public final float decrementAndGet() {
        return addAndGet(-1.0f);
    }

    /**
     * Atomically adds the given value to the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * @param delta the value to add
     * @return the updated value
     */
    public final float addAndGet(float delta) {
        while (true) {
            int current = value;
            float currentVal = Float.intBitsToFloat(current);
            float nextVal = delta + currentVal;
            int next = Float.floatToRawIntBits(nextVal);
            if (UPDATER.compareAndSet(this, current, next)) {
                return nextVal;
            }
        }
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value
     */
    public String toString() {
        return Float.toString(get());
    }

    /**
     * Returns the current value of this {@code AtomicFloat} as an
     * {@code int},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     */
    public int intValue() {
        return (int) get();
    }

    /**
     * Returns the current value of this {@code AtomicFloat} as a
     * {@code long} after a widening primitive conversion,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     */
    public long longValue() {
        return (long) get();
    }

    /**
     * Returns the current value of this {@code AtomicFloat} as a
     * {@code float} after a widening primitive conversion,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     *
     * Equivalent to {@link #get()}.
     */
    public float floatValue() {
        return get();
    }

    /**
     * Returns the current value of this {@code AtomicFloat} as a
     * {@code double} after a widening primitive conversion,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     */
    public double doubleValue() {
        return get();
    }

    /**
     * Saves the state to a stream (that is, serializes it).
     *
     * @serialData The current value is emitted (a {@code float}).
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();

        out.writeFloat(get());
    }

    /**
     * Reconstitutes the instance from a stream (that is, deserializes it).
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();

        set(in.readFloat());
    }

}
