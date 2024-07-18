package unrefined.nio;

import unrefined.util.foreign.Foreign;

import java.io.IOException;
import java.math.BigInteger;

public class NativeIntHandle extends Pointer.Handle implements Comparable<NativeIntHandle> {

    public static NativeIntHandle wrap(Pointer memory) {
        return new NativeIntHandle(memory);
    }

    public static NativeIntHandle allocate() throws IOException {
        return new NativeIntHandle(Pointer.allocate(Foreign.getInstance().nativeIntSize()));
    }

    public static NativeIntHandle allocateDirect() throws IOException {
        return new NativeIntHandle(Pointer.allocateDirect(Foreign.getInstance().nativeIntSize()));
    }

    protected NativeIntHandle(Pointer memory) {
        super(memory);
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, Foreign.getInstance().nativeIntSize());
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, Foreign.getInstance().nativeIntSize());
    }

    public long get() {
        return payload().getNativeInt(0);
    }

    public BigInteger getUnsigned() {
        return payload().getUnsignedNativeInt(0);
    }

    public void set(long newValue) {
        payload().putNativeInt(0, newValue);
    }

    public void setUnsigned(BigInteger newValue) {
        payload().putUnsignedNativeInt(0, newValue);
    }

    @Override
    public int compareTo(NativeIntHandle other) {
        return payload().compareTo(other.payload(), 0, Foreign.getInstance().nativeIntSize());
    }

}
