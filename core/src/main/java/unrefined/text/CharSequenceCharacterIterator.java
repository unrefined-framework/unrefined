package unrefined.text;

import unrefined.util.UnexpectedError;

import java.text.CharacterIterator;
import java.util.Objects;

public class CharSequenceCharacterIterator implements CharacterIterator {

    private final CharSequence sequence;
    private final int start, end, length;
    private int index = 0;

    public CharSequenceCharacterIterator(CharSequence sequence, int start, int end) {
        Objects.requireNonNull(sequence);
        if (start < 0 || end > sequence.length()) throw new ArrayIndexOutOfBoundsException();
        this.sequence = sequence;
        this.start = start;
        this.end = end;
        this.length = end - start;
    }

    public CharSequenceCharacterIterator(CharSequence sequence) {
        this(sequence, 0, sequence.length());
    }

    @Override
    public char first() {
        return sequence.charAt(start);
    }

    @Override
    public char last() {
        return sequence.charAt(end - 1);
    }

    @Override
    public char current() {
        return sequence.charAt(start + index);
    }

    @Override
    public char next() {
        index ++;
        if (index == length) return DONE;
        else if (index > length) throw new IndexOutOfBoundsException();
        else return sequence.charAt(start + index);
    }

    @Override
    public char previous() {
        index --;
        if (index == -1) return DONE;
        else if (index < -1) throw new IndexOutOfBoundsException();
        else return sequence.charAt(start + index);
    }

    @Override
    public char setIndex(int position) {
        index = position;
        if (index == -1 || index == length) return DONE;
        else if (index < -1 || index > length) throw new IndexOutOfBoundsException();
        else return sequence.charAt(index);
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
