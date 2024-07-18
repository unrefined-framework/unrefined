package unrefined.nio;

import java.io.IOException;

public class IntArrayHandle extends Pointer.Handle implements Comparable<IntArrayHandle> {

    public static IntArrayHandle wrap(Pointer memory, int length) {
        return new IntArrayHandle(memory, length);
    }

    public static IntArrayHandle allocate(int length) throws IOException {
        return new IntArrayHandle(Pointer.allocate((long) length << 2), length);
    }

    public static IntArrayHandle allocateDirect(int length) throws IOException {
        return new IntArrayHandle(Pointer.allocateDirect((long) length << 2), length);
    }

    private final int length;

    protected IntArrayHandle(Pointer memory, int length) {
        super(memory);
        this.length = length;
    }

    public int length() {
        return length;
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, (long) length << 2);
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, (long) length << 2);
    }

    public void get(int[] array, int offset) {
        payload().getIntArray(0, array, offset, length);
    }

    public void get(int[] array) {
        payload().getIntArray(0, array, 0, length);
    }

    public void set(int[] array, int offset) {
        payload().putIntArray(0, array, offset, length);
    }

    public void set(int[] array) {
        payload().putIntArray(0, array, 0, length);
    }

    @Override
    public int compareTo(IntArrayHandle other) {
        return payload().compareTo(other.payload(), 0, (long) length << 2);
    }

}
