package unrefined.text;

import unrefined.util.UnexpectedError;

import java.text.CharacterIterator;
import java.util.Objects;

public class CharArrayCharacterIterator implements CharacterIterator {

    private final char[] array;
    private final int offset, length;
    private int index = 0;

    public CharArrayCharacterIterator(char[] array, int offset, int length) {
        Objects.requireNonNull(array);
        if (offset < 0 || offset + length > array.length) throw new ArrayIndexOutOfBoundsException();
        this.array = array;
        this.offset = offset;
        this.length = length;
    }

    public CharArrayCharacterIterator(char[] array) {
        this(array, 0, array.length);
    }

    @Override
    public char first() {
        return array[offset];
    }

    @Override
    public char last() {
        return array[offset + length - 1];
    }

    @Override
    public char current() {
        return array[offset + index];
    }

    @Override
    public char next() {
        index ++;
        if (index == length) return DONE;
        else if (index > length) throw new IndexOutOfBoundsException();
        else return array[offset + index];
    }

    @Override
    public char previous() {
        index --;
        if (index == -1) return DONE;
        else if (index < -1) throw new IndexOutOfBoundsException();
        else return array[offset + index];
    }

    @Override
    public char setIndex(int position) {
        index = position;
        if (index == -1 || index == length) return DONE;
        else if (index < -1 || index > length) throw new IndexOutOfBoundsException();
        else return array[index];
    }

    @Override
    public int getBeginIndex() {
        return 0;
    }

    @Override
    public int getEndIndex() {
        return length;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnexpectedError(e);
        }
    }

}
