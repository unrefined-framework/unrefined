package unrefined.nio;

import java.io.IOException;

public class DoubleArrayHandle extends Pointer.Handle implements Comparable<DoubleArrayHandle> {

    public static DoubleArrayHandle wrap(Pointer memory, int length) {
        return new DoubleArrayHandle(memory, length);
    }

    public static DoubleArrayHandle allocate(int length) throws IOException {
        return new DoubleArrayHandle(Pointer.allocate((long) length << 3), length);
    }

    public static DoubleArrayHandle allocateDirect(int length) throws IOException {
        return new DoubleArrayHandle(Pointer.allocateDirect((long) length << 3), length);
    }

    private final int length;

    protected DoubleArrayHandle(Pointer memory, int length) {
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

    public void get(double[] array, int offset) {
        payload().getDoubleArray(0, array, offset, length);
    }

    public void get(double[] array) {
        payload().getDoubleArray(0, array, 0, length);
    }

    public void set(double[] array, int offset) {
        payload().putDoubleArray(0, array, offset, length);
    }

    public void set(double[] array) {
        payload().putDoubleArray(0, array, 0, length);
    }

    @Override
    public int compareTo(DoubleArrayHandle other) {
        return payload().compareTo(other.payload(), 0, (long) length << 3);
    }

}
