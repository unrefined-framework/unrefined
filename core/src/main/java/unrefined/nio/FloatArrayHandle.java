package unrefined.nio;

import java.io.IOException;

public class FloatArrayHandle extends Pointer.Handle implements Comparable<FloatArrayHandle> {

    public static FloatArrayHandle wrap(Pointer memory, int length) {
        return new FloatArrayHandle(memory, length);
    }

    public static FloatArrayHandle allocate(int length) throws IOException {
        return new FloatArrayHandle(Pointer.allocate((long) length << 2), length);
    }

    public static FloatArrayHandle allocateDirect(int length) throws IOException {
        return new FloatArrayHandle(Pointer.allocateDirect((long) length << 2), length);
    }

    private final int length;

    protected FloatArrayHandle(Pointer memory, int length) {
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

    public void get(float[] array, int offset) {
        payload().getFloatArray(0, array, offset, length);
    }

    public void get(float[] array) {
        payload().getFloatArray(0, array, 0, length);
    }

    public void set(float[] array, int offset) {
        payload().putFloatArray(0, array, offset, length);
    }

    public void set(float[] array) {
        payload().putFloatArray(0, array, 0, length);
    }

    @Override
    public int compareTo(FloatArrayHandle other) {
        return payload().compareTo(other.payload(), 0, (long) length << 2);
    }

}
