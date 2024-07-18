package unrefined.nio;

import unrefined.util.foreign.Foreign;

import java.io.IOException;

public class NativeIntArrayHandle extends Pointer.Handle implements Comparable<NativeIntArrayHandle> {

    public static NativeIntArrayHandle wrap(Pointer memory, int length) {
        return new NativeIntArrayHandle(memory, length);
    }

    public static NativeIntArrayHandle allocate(int length) throws IOException {
        return new NativeIntArrayHandle(Pointer.allocate((long) length * Foreign.getInstance().nativeIntSize()), length);
    }

    public static NativeIntArrayHandle allocateDirect(int length) throws IOException {
        return new NativeIntArrayHandle(Pointer.allocateDirect((long) length * Foreign.getInstance().nativeIntSize()), length);
    }

    private final int length;

    protected NativeIntArrayHandle(Pointer memory, int length) {
        super(memory);
        this.length = length;
    }

    public int length() {
        return length;
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, (long) length * Foreign.getInstance().nativeIntSize());
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, (long) length * Foreign.getInstance().nativeIntSize());
    }

    public void get(long[] array, int offset) {
        payload().getNativeIntArray(0, array, offset, length);
    }

    public void get(long[] array) {
        payload().getNativeIntArray(0, array, 0, length);
    }

    public void set(long[] array, int offset) {
        payload().putNativeIntArray(0, array, offset, length);
    }

    public void set(long[] array) {
        payload().putNativeIntArray(0, array, 0, length);
    }

    @Override
    public int compareTo(NativeIntArrayHandle other) {
        return payload().compareTo(other.payload(), 0, (long) length * Foreign.getInstance().nativeIntSize());
    }

}
