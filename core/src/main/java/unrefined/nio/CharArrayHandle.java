package unrefined.nio;

import java.io.IOException;

public class CharArrayHandle extends Pointer.Handle implements Comparable<CharArrayHandle> {

    public static CharArrayHandle wrap(Pointer memory, int length) {
        return new CharArrayHandle(memory, length);
    }

    public static CharArrayHandle allocate(int length) throws IOException {
        return new CharArrayHandle(Pointer.allocate((long) length << 1), length);
    }

    public static CharArrayHandle allocateDirect(int length) throws IOException {
        return new CharArrayHandle(Pointer.allocateDirect((long) length << 1), length);
    }

    private final int length;

    protected CharArrayHandle(Pointer memory, int length) {
        super(memory);
        this.length = length;
    }

    public int length() {
        return length;
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, (long) length << 1);
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, (long) length << 1);
    }

    public void get(char[] array, int offset) {
        payload().getCharArray(0, array, offset, length);
    }

    public void get(char[] array) {
        payload().getCharArray(0, array, 0, length);
    }

    public void set(char[] array, int offset) {
        payload().putCharArray(0, array, offset, length);
    }

    public void set(char[] array) {
        payload().putCharArray(0, array, 0, length);
    }

    @Override
    public int compareTo(CharArrayHandle other) {
        return payload().compareTo(other.payload(), 0, (long) length << 1);
    }

}
