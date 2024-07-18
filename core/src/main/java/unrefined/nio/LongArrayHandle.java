package unrefined.nio;

import java.io.IOException;

public class LongArrayHandle extends Pointer.Handle implements Comparable<LongArrayHandle> {

    public static LongArrayHandle wrap(Pointer memory, int length) {
        return new LongArrayHandle(memory, length);
    }

    public static LongArrayHandle allocate(int length) throws IOException {
        return new LongArrayHandle(Pointer.allocate((long) length << 3), length);
    }

    public static LongArrayHandle allocateDirect(int length) throws IOException {
        return new LongArrayHandle(Pointer.allocateDirect((long) length << 3), length);
    }

    private final int length;

    protected LongArrayHandle(Pointer memory, int length) {
        super(memory);
        this.length = length;
    }

    public int length() {
        return length;
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, (long) length << 3);
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, (long) length << 3);
    }

    public void get(long[] array, int offset) {
        payload().getLongArray(0, array, offset, length);
    }

    public void get(long[] array) {
        payload().getLongArray(0, array, 0, length);
    }

    public void set(long[] array, int offset) {
        payload().putLongArray(0, array, offset, length);
    }

    public void set(long[] array) {
        payload().putLongArray(0, array, 0, length);
    }

    @Override
    public int compareTo(LongArrayHandle other) {
        return payload().compareTo(other.payload(), 0, (long) length << 3);
    }

}
