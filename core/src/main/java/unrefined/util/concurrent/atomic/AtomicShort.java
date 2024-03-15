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
 * An {@code short} value that may be updated atomically.  See the
 * {@link java.lang.invoke.VarHandle} specification for descriptions of the properties
 * of atomic accesses. An {@code AtomicShort} is used in
 * applications such as atomically incremented counters, and cannot be
 * used as a replacement for an {@link Short}. However,
 * this class does extend {@code Number} to allow uniform access by
 * tools and utilities that deal with numerically-based classes.
 *
 * @author Doug Lea
 * @author Karstian Lee
 */
public class AtomicShort extends Number implements java.io.Serializable {

    private static final long serialVersionUID = -5648921859780178716L;

    private transient volatile int value;

    private static final AtomicIntegerFieldUpdater<AtomicShort> UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(AtomicShort.class, "value");

    public AtomicShort(short initialValue) {
        value = initialValue;
    }

    /**
     * Creates a new AtomicShort with initial value {@code 0}.
     */
    public AtomicShort() {
    }

    /**
     * Returns the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     *
     * @return the current value
     */
    public final short get() {
        return (short) value;
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setVolatile}.
     *
     * @param newValue the new value
     */
    public final void set(short newValue) {
        value = newValue;
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setRelease}.
     *
     * @param newValue the new value
     */
    public final void lazySet(short newValue) {
        UPDATER.lazySet(this, newValue);
    }

    /**
     * Atomically sets the value to {@code newValue} and returns the old value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndSet}.
     *
     * @param newValue the new value
     * @return the previous value
     */
    public final short getAndSet(short newValue) {
        return (short) UPDATER.getAndSet(this, newValue);
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
    public final boolean compareAndSet(short expectedValue, short newValue) {
        return UPDATER.compareAndSet(this, expectedValue, newValue);
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
    public final boolean weakCompareAndSet(short expectedValue, short newValue) {
        return UPDATER.weakCompareAndSet(this, expectedValue, newValue);
    }

    /**
     * Atomically increments the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(1)}.
     *
     * @return the previous value
     */
    public final short getAndIncrement() {
        return getAndAdd((short) 1);
    }

    /**
     * Atomically decrements the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(-1)}.
     *
     * @return the previous value
     */
    public final short getAndDecrement() {
        return getAndAdd((short) -1);
    }

    /**
     * Atomically adds the given value to the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * @param delta the value to add
     * @return the previous value
     */
    public final short getAndAdd(short delta) {
        while (true) {
            int current = value;
            short currentVal = (short) current;
            short nextVal = (short) (delta + currentVal);
            int next = nextVal;
            if (UPDATER.compareAndSet(this, current, next)) {
                return currentVal;
            }
        }
    }

    /**
     * Atomically increments the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(1)}.
     *
     * @return the updated value
     */
    public final short incrementAndGet() {
        return addAndGet((short) 1);
    }

    /**
     * Atomically decrements the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(-1)}.
     *
     * @return the updated value
     */
    public final short decrementAndGet() {
        return addAndGet((short) -1);
    }

    /**
     * Atomically adds the given value to the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * @param delta the value to add
     * @return the updated value
     */
    public final short addAndGet(short delta) {
        while (true) {
            int current = value;
            short currentVal = (short) current;
            short nextVal = (short) (delta + currentVal);
            int next = nextVal;
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
        return Short.toString(get());
    }

    /**
     * Returns the current value of this {@code AtomicShort} as an
     * {@code int},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     */
    public int intValue() {
        return get();
    }

    /**
     * Returns the current value of this {@code AtomicShort} as a
     * {@code long} after a widening primitive conversion,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     */
    public long longValue() {
        return get();
    }

    /**
     * Returns the current value of this {@code AtomicShort} as a
     * {@code float} after a widening primitive conversion,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     */
    public float floatValue() {
        return get();
    }

    /**
     * Returns the current value of this {@code AtomicShort} as a
     * {@code double} after a widening primitive conversion,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     */
    public double doubleValue() {
        return get();
    }

    /**
     * Saves the state to a stream (that is, serializes it).
     *
     * @serialData The current value is emitted (a {@code short}).
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();

        out.writeShort(get());
    }

    /** Reconstitutes the instance from a stream (that is, deserializes it). */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();

        set(in.readShort());
    }

}
