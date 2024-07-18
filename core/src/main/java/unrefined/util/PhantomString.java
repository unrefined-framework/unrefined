package unrefined.util;

import java.util.Arrays;
import java.util.Objects;

public class PhantomString implements CharSequence {

    private final char[] array;
    private final CharSequence sequence;
    private final int offset, length;

    public PhantomString(char[] array, int offset, int length) {
        Objects.requireNonNull(array);
        if (offset < 0 || offset + length > array.length) throw new ArrayIndexOutOfBoundsException();
        this.array = array;
        this.sequence = null;
        this.offset = offset;
        this.length = length;
    }

    public PhantomString(CharSequence sequence, int offset, int length) {
        Objects.requireNonNull(sequence);
        if (offset < 0 || offset + length > sequence.length()) throw new StringIndexOutOfBoundsException();
        if (sequence instanceof PhantomString) {
            this.array = ((PhantomString) sequence).array;
            this.sequence = ((PhantomString) sequence).sequence;
        }
        else {
            this.array = null;
            this.sequence = sequence;
        }
        this.offset = offset;
        this.length = length;
    }

    public PhantomString(char[] array) {
        this(array, 0, array.length);
    }

    public PhantomString(CharSequence array) {
        this(array, 0, array.length());
    }

    public char[] array() {
        return array;
    }

    public CharSequence sequence() {
        return sequence;
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
        if (sequence == null) return array[offset + index];
        else return sequence.charAt(offset + index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (sequence == null) return new PhantomString(array, offset + start, offset + end);
        else return new PhantomString(sequence, offset + start, offset + end);
    }

    @Override
    public String toString() {
        if (sequence == null) return new String(array, offset, length);
        else return CharSequences.toString(sequence, offset, length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhantomString that = (PhantomString) o;

        if (length != that.length) return false;
        if (array == null) {
            for (int i = 0; i < length; i ++) {
                if (sequence.charAt(offset + i) != that.sequence.charAt(that.offset + i)) return false;
            }
        }
        else {
            for (int i = 0; i < length; i ++) {
                if (array[offset + i] != that.array[that.offset + i]) return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = array == null ? CharSequences.hashCode(sequence) : Arrays.hashCode(array);
        result = 31 * result + offset;
        result = 31 * result + length;
        return result;
    }

}
