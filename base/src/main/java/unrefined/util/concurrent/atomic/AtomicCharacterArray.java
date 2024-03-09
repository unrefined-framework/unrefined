package unrefined.util.concurrent.atomic;

public class AtomicCharacterArray implements java.io.Serializable {

    private static final long serialVersionUID = 532069563836178550L;

    private static final Atomic ATOMIC = Atomic.getInstance();
    private final char[] array;

    /**
     * Creates a new AtomicCharacterArray of the given length, with all
     * elements initially zero.
     *
     * @param length the length of the array
     */
    public AtomicCharacterArray(int length) {
        array = new char[length];
    }

    /**
     * Creates a new AtomicCharacterArray with the same length as, and
     * all elements copied from, the given array.
     *
     * @param array the array to copy elements from
     * @throws NullPointerException if array is null
     */
    public AtomicCharacterArray(char[] array) {
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
    public final char get(int i) {
        return ATOMIC.getArrayCharElementVolatile(array, i);
    }

    /**
     * Sets the element at index {@code i} to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setVolatile}.
     *
     * @param i the index
     * @param newValue the new value
     */
    public final void set(int i, char newValue) {
        ATOMIC.setArrayCharElementVolatile(array, i, newValue);
    }

    /**
     * Sets the element at index {@code i} to {@code newValue},
     * with memory effects as specified by {@link java.lang.invoke.VarHandle#setRelease}.
     *
     * @param i the index
     * @param newValue the new value
     */
    public final void lazySet(int i, char newValue) {
        ATOMIC.lazySetArrayCharElement(array, i, newValue);
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
    public final char getAndSet(int i, char newValue) {
        return ATOMIC.getAndSetArrayCharElement(array, i, newValue);
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
    public final boolean compareAndSet(int i, char expectedValue, char newValue) {
        return ATOMIC.compareAndSetArrayCharElement(array, i, expectedValue, newValue);
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
    public final boolean weakCompareAndSet(int i, char expectedValue, char newValue) {
        return ATOMIC.weakCompareAndSetArrayCharElement(array, i, expectedValue, newValue);
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
