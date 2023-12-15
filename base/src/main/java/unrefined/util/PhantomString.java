package unrefined.util;

import java.util.Arrays;
import java.util.Objects;

public class PhantomString implements CharSequence {

    private final char[] array;
    private final int offset, length;

    public PhantomString(char[] array, int offset, int length) {
        Objects.requireNonNull(array);
        if (offset < 0 || offset + length > array.length) throw new ArrayIndexOutOfBoundsException();
        this.array = array;
        this.offset = offset;
        this.length = length;
    }

    public PhantomString(char[] array) {
        this(array, 0, array.length);
    }

    public char[] array() {
        return array;
    }

    public int offset() {
        return offset;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public char charAt(int index) {
        return array[offset + index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        char[] array = Arrays.copyOfRange(this.array, offset + start, offset + end);
        return new PhantomString(array, 0, array.length);
    }

    @Override
    public String toString() {
        return new String(array, offset, length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhantomString that = (PhantomString) o;

        if (length != that.length) return false;
        for (int i = 0; i < length; i ++) {
            if (array[offset + i] != that.array[that.offset + i]) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(array);
        result = 31 * result + offset;
        result = 31 * result + length;
        return result;
    }

}
