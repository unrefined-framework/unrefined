package unrefined.nio;

import java.io.IOException;

public class ByteArrayHandle extends Pointer.Handle implements Comparable<ByteArrayHandle> {

    public static ByteArrayHandle wrap(Pointer memory, int length) {
        return new ByteArrayHandle(memory, length);
    }

    public static ByteArrayHandle allocate(int length) throws IOException {
        return new ByteArrayHandle(Pointer.allocate(length), length);
    }

    public static ByteArrayHandle allocateDirect(int length) throws IOException {
        return new ByteArrayHandle(Pointer.allocateDirect(length), length);
    }

    private final int length;

    protected ByteArrayHandle(Pointer memory, int length) {
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

    public void get(byte[] array, int index) {
        payload().getByteArray(0, array, index, length);
    }

    public void get(byte[] array) {
        payload().getByteArray(0, array, 0, length);
    }

    public void set(byte[] array, int index) {
        payload().putByteArray(0, array, index, length);
    }

    public void set(byte[] array) {
        payload().putByteArray(0, array, 0, length);
    }

    @Override
    public int compareTo(ByteArrayHandle other) {
        return payload().compareTo(other.payload(), 0, length);
    }

}
