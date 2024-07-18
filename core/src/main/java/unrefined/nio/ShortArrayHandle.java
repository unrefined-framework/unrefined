package unrefined.nio;

import java.io.IOException;

public class ShortArrayHandle extends Pointer.Handle implements Comparable<ShortArrayHandle> {

    public static ShortArrayHandle wrap(Pointer memory, int length) {
        return new ShortArrayHandle(memory, length);
    }

    public static ShortArrayHandle allocate(int length) throws IOException {
        return new ShortArrayHandle(Pointer.allocate((long) length << 1), length);
    }

    public static ShortArrayHandle allocateDirect(int length) throws IOException {
        return new ShortArrayHandle(Pointer.allocateDirect((long) length << 1), length);
    }

    private final int length;

    protected ShortArrayHandle(Pointer memory, int length) {
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

    public void get(short[] array, int offset) {
        payload().getShortArray(0, array, offset, length);
    }

    public void get(short[] array) {
        payload().getShortArray(0, array, 0, length);
    }

    public void set(short[] array, int offset) {
        payload().putShortArray(0, array, offset, length);
    }

    public void set(short[] array) {
        payload().putShortArray(0, array, 0, length);
    }

    @Override
    public int compareTo(ShortArrayHandle other) {
        return payload().compareTo(other.payload(), 0, (long) length << 1);
    }

}
