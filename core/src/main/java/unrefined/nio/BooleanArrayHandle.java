package unrefined.nio;

import java.io.IOException;

public class BooleanArrayHandle extends Pointer.Handle implements Comparable<BooleanArrayHandle> {

    public static BooleanArrayHandle wrap(Pointer memory, int length) {
        return new BooleanArrayHandle(memory, length);
    }

    public static BooleanArrayHandle allocate(int length) throws IOException {
        return new BooleanArrayHandle(Pointer.allocate(length), length);
    }

    public static BooleanArrayHandle allocateDirect(int length) throws IOException {
        return new BooleanArrayHandle(Pointer.allocateDirect(length), length);
    }

    private final int length;

    protected BooleanArrayHandle(Pointer memory, int length) {
        super(memory);
        this.length = length;
    }

    public int length() {
        return length;
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, length);
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, length);
    }

    public void get(boolean[] array, int index) {
        payload().getBooleanArray(0, array, index, length);
    }

    public void get(boolean[] array) {
        payload().getBooleanArray(0, array, 0, length);
    }

    public void set(boolean[] array, int index) {
        payload().putBooleanArray(0, array, index, length);
    }

    public void set(boolean[] array) {
        payload().putBooleanArray(0, array, 0, length);
    }

    @Override
    public int compareTo(BooleanArrayHandle other) {
        return payload().compareTo(other.payload(), 0, length);
    }

}
