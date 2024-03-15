package unrefined.util.concurrent.atomic;

public class AtomicShortArray implements java.io.Serializable {

    private static final long serialVersionUID = 1783453164840728416L;

    private static final Atomic ATOMIC = Atomic.getInstance();
    private final short[] array;

    /**
     * Creates a new AtomicShortArray of the given length, with all
     * elements initially zero.
     *
     * @param length the length of the array
     */
    public AtomicShortArray(int length) {
        array = new short[length];
    }

    /**
     * Creates a new AtomicShortArray with the same length as, and
     * all elements copied from, the given array.
     *
     * @param array the array to copy elements from
     * @throws NullPointerException if array is null
     */
    public AtomicShortArray(short[] array) {
        // Visibility guaranteed by final field guarantees
        this.array = array.clone();
    }

    /**
     * Returns the length of the array.
     *
     * @return the length of the array
     */
    public final int length() {
        return array.length;
    }

    /**
     * Returns the current value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getVolatile}.
     *
     * @param i the index
     * @return the current value
     */
    public final short get(int i) {
        return ATOMIC.getArrayShortElementVolatile(array, i);
    }

    /**
     * Sets the element at index {@code i} to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setVolatile}.
     *
     * @param i the index
     * @param newValue the new value
     */
    public final void set(int i, short newValue) {
        ATOMIC.setArrayShortElementVolatile(array, i, newValue);
    }

    /**
     * Sets the element at index {@code i} to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setRelease}.
     *
     * @param i the index
     * @param newValue the new value
     */
    public final void lazySet(int i, short newValue) {
        ATOMIC.lazySetArrayShortElement(array, i, newValue);
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
    public final short getAndSet(int i, short newValue) {
        return ATOMIC.getAndSetArrayShortElement(array, i, newValue);
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
    public final boolean compareAndSet(int i, short expectedValue, short newValue) {
        return ATOMIC.compareAndSetArrayShortElement(array, i, expectedValue, newValue);
    }

    /**
     * Possibly atomically sets the element at index {@code i} to
     * {@code newValue} if the element's current value {@code == expectedValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#weakCompareAndSetPlain}.
     *
     * @param i the index
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful
     */
    public final boolean weakCompareAndSet(int i, short expectedValue, short newValue) {
        return ATOMIC.weakCompareAndSetArrayShortElement(array, i, expectedValue, newValue);
    }

    /**
     * Atomically increments the value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(i, 1)}.
     *
     * @param i the index
     * @return the previous value
     */
    public final short getAndIncrement(int i) {
        return ATOMIC.getAndIncrementArrayShortElement(array, i);
    }

    /**
     * Atomically decrements the value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code getAndAdd(i, -1)}.
     *
     * @param i the index
     * @return the previous value
     */
    public final short getAndDecrement(int i) {
        return ATOMIC.getAndDecrementArrayShortElement(array, i);
    }

    /**
     * Atomically adds the given value to the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * @param i the index
     * @param delta the value to add
     * @return the previous value
     */
    public final short getAndAdd(int i, short delta) {
        return ATOMIC.getAndAddArrayShortElement(array, i, delta);
    }

    /**
     * Atomically increments the value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(i, 1)}.
     *
     * @param i the index
     * @return the updated value
     */
    public final short incrementAndGet(int i) {
        return ATOMIC.incrementAndGetArrayShortElement(array, i);
    }

    /**
     * Atomically decrements the value of the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * <p>Equivalent to {@code addAndGet(i, -1)}.
     *
     * @param i the index
     * @return the updated value
     */
    public final short decrementAndGet(int i) {
        return ATOMIC.decrementAndGetArrayShortElement(array, i);
    }

    /**
     * Atomically adds the given value to the element at index {@code i},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#getAndAdd}.
     *
     * @param i the index
     * @param delta the value to add
     * @return the updated value
     */
    public final short addAndGet(int i, short delta) {
        return ATOMIC.addAndGetArrayShortElement(array, i, delta);
    }

    /**
     * Returns the String representation of the current values of array.
     * @return the String representation of the current values of array
     */
    public String toString() {
        int iMax = array.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(get(i));
            if (i == iMax)
                return b.append(']').toString();
            b.append(',').append(' ');
        }
    }

}
