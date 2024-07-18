package unrefined.nio;

import unrefined.util.foreign.Foreign;

import java.io.IOException;

public class NativeLongArrayHandle extends Pointer.Handle implements Comparable<NativeLongArrayHandle> {

    public static NativeLongArrayHandle wrap(Pointer memory, int length) {
        return new NativeLongArrayHandle(memory, length);
    }

    public static NativeLongArrayHandle allocate(int length) throws IOException {
        return new NativeLongArrayHandle(Pointer.allocate((long) length * Foreign.getInstance().nativeLongSize()), length);
    }

    public static NativeLongArrayHandle allocateDirect(int length) throws IOException {
        return new NativeLongArrayHandle(Pointer.allocateDirect((long) length * Foreign.getInstance().nativeLongSize()), length);
    }

    private final int length;

    protected NativeLongArrayHandle(Pointer memory, int length) {
        super(memory);
        this.length = length;
    }

    public int length() {
        return length;
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, (long) length * Foreign.getInstance().nativeLongSize());
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, (long) length * Foreign.getInstance().nativeLongSize());
    }

    public void get(long[] array, int offset) {
        payload().getNativeLongArray(0, array, offset, length);
    }

    public void get(long[] array) {
        payload().getNativeLongArray(0, array, 0, length);
    }

    public void set(long[] array, int offset) {
        payload().putNativeLongArray(0, array, offset, length);
    }

    public void set(long[] array) {
        payload().putNativeLongArray(0, array, 0, length);
    }

    @Override
    public int compareTo(NativeLongArrayHandle other) {
        return payload().compareTo(other.payload(), 0, (long) length * Foreign.getInstance().nativeLongSize());
    }

}
