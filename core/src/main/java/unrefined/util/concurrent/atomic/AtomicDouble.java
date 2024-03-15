/*
 * Originally written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 *
 * Modified by Karstian Lee in order to support other data types except int, long, boolean and reference
 */

package unrefined.util.concurrent.atomic;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/**
 * An {@code double} value that may be updated atomically.  See the
 * {@link java.lang.invoke.VarHandle} specification for descriptions of the properties
 * of atomic accesses. An {@code AtomicDouble} is used in
 * applications such as atomically incremented counters, and cannot be
 * used as a replacement for an {@link Double}. However,
 * this class does extend {@code Number} to allow uniform access by
 * tools and utilities that deal with numerically-based classes.
 *
 * @author Doug Lea
 * @author Karstian Lee
 */
public class AtomicDouble extends Number implements java.io.Serializable {

    private static final long serialVersionUID = -6417175974846496720L;
    
    private transient volatile long value;

    private static final AtomicLongFieldUpdater<AtomicDouble> UPDATER =
            AtomicLongFieldUpdater.newUpdater(AtomicDouble.class, "value");

    public AtomicDouble(double initialValue) {
        value = Double.doubleToRawLongBits(initialValue);
    }

    /**
     * Creates a new AtomicDouble with initial value {@code 0.0}.
     */
    public AtomicDouble() {
    }

    /**
     * Returns the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     *
     * @return the current value
     */
    public final double get() {
        return Double.longBitsToDouble(value);
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setVolatile}.
     *
     * @param newValue the new value
     */
    public final void set(double newValue) {
        value = Double.doubleToRawLongBits(newValue);
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setRelease}.
     *
     * @param newValue the new value
     */
    public final void lazySet(double newValue) {
        UPDATER.lazySet(this, Double.doubleToRawLongBits(newValue));
    }

    /**
     * Atomically sets the value to {@code newValue} and returns the old value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndSet}.
     *
     * @param newValue the new value
     * @return the previous value
     */
    public final double getAndSet(double newValue) {
        return Double.longBitsToDouble(UPDATER.getAndSet(this, Double.doubleToRawLongBits(newValue)));
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
    public final boolean compareAndSet(double expectedValue, double newValue) {
        return UPDATER.compareAndSet(this, Double.doubleToRawLongBits(expectedValue), Double.doubleToRawLongBits(newValue));
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
    public final boolean weakCompareAndSet(double expectedValue, double newValue) {
        return UPDATER.weakCompareAndSet(this, Double.doubleToRawLongBits(expectedValue), Double.doubleToRawLongBits(newValue));
    }

    /**
     * Atomically increments the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(1.0)}.
     *
     * @return the previous value
     */
    public final double getAndIncrement() {
        return getAndAdd(1.0);
    }

    /**
     * Atomically decrements the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(-1.0)}.
     *
     * @return the previous value
     */
    public final double getAndDecrement() {
        return getAndAdd(-1.0);
    }

    /**
     * Atomically adds the given value to the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * @param delta the value to add
     * @return the previous value
     */
    public final double getAndAdd(double delta) {
        while (true) {
            long current = value;
            double currentVal = Double.longBitsToDouble(current);
            double nextVal = delta + currentVal;
            long next = Double.doubleToRawLongBits(nextVal);
            if (UPDATER.compareAndSet(this, current, next)) {
                return currentVal;
            }
        }
    }

    /**
     * Atomically increments the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(1.0)}.
     *
     * @return the updated value
     */
    public final double incrementAndGet() {
        return addAndGet(1.0);
    }

    /**
     * Atomically decrements the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(-1.0)}.
     *
     * @return the updated value
     */
    public final double decrementAndGet() {
        return addAndGet(-1.0);
    }

    /**
     * Atomically adds the given value to the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * @param delta the value to add
     * @return the updated value
     */
    public final double addAndGet(double delta) {
        while (true) {
            long current = value;
            double currentVal = Double.longBitsToDouble(current);
            double nextVal = delta + currentVal;
            long next = Double.doubleToRawLongBits(nextVal);
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
        return Double.toString(get());
    }

    /**
     * Returns the current value of this {@code AtomicDouble} as an
     * {@code int},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     */
    public int intValue() {
        return (int) get();
    }

    /**
     * Returns the current value of this {@code AtomicDouble} as a
     * {@code long} after a widening primitive conversion,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     */
    public long longValue() {
        return (long) get();
    }

    /**
     * Returns the current value of this {@code AtomicDouble} as a
     * {@code float} after a widening primitive conversion,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     */
    public float floatValue() {
        return (float) get();
    }

    /**
     * Returns the current value of this {@code AtomicDouble} as a
     * {@code double} after a widening primitive conversion,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     *
     * Equivalent to {@link #get()}.
     */
    public double doubleValue() {
        return get();
    }

    /**
     * Saves the state to a stream (that is, serializes it).
     *
     * @serialData The current value is emitted (a {@code double}).
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();

        out.writeDouble(get());
    }

    /**
     * Reconstitutes the instance from a stream (that is, deserializes it).
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();

        set(in.readDouble());
    }

}
