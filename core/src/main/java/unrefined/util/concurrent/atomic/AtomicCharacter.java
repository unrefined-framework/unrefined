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
 * An {@code char} value that may be updated atomically.  See the
 * {@link java.lang.invoke.VarHandle} specification for descriptions of the properties
 * of atomic accesses. An {@code AtomicCharacter} cannot be
 * used as a replacement for an {@link Character}.
 *
 * @author Doug Lea
 * @author Karstian Lee
 */
public class AtomicCharacter implements java.io.Serializable {

    private static final long serialVersionUID = -800587072034922649L;

    private transient volatile int value;

    private static final AtomicIntegerFieldUpdater<AtomicCharacter> UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(AtomicCharacter.class, "value");

    public AtomicCharacter(char initialValue) {
        value = initialValue;
    }

    /**
     * Creates a new AtomicCharacter with initial value {@code '\0'}.
     */
    public AtomicCharacter() {
    }

    /**
     * Returns the current value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     *
     * @return the current value
     */
    public final char get() {
        return (char) value;
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setVolatile}.
     *
     * @param newValue the new value
     */
    public final void set(char newValue) {
        value = newValue;
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setRelease}.
     *
     * @param newValue the new value
     */
    public final void lazySet(char newValue) {
        UPDATER.lazySet(this, newValue);
    }

    /**
     * Atomically sets the value to {@code newValue} and returns the old value,
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndSet}.
     *
     * @param newValue the new value
     * @return the previous value
     */
    public final char getAndSet(char newValue) {
        return (char) UPDATER.getAndSet(this, newValue);
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
    public final boolean compareAndSet(char expectedValue, char newValue) {
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
    public final boolean weakCompareAndSet(char expectedValue, char newValue) {
        return UPDATER.weakCompareAndSet(this, expectedValue, newValue);
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value
     */
    public String toString() {
        return Character.toString(get());
    }

    /**
     * Saves the state to a stream (that is, serializes it).
     *
     * @serialData The current value is emitted (a {@code char}).
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();

        out.writeChar(get());
    }

    /**
     * Reconstitutes the instance from a stream (that is, deserializes it).
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();

        set(in.readChar());
    }

}
