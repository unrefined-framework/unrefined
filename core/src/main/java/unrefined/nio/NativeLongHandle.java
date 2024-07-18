package unrefined.nio;

import unrefined.util.foreign.Foreign;

import java.io.IOException;
import java.math.BigInteger;

public class NativeLongHandle extends Pointer.Handle implements Comparable<NativeLongHandle> {

    public static NativeLongHandle wrap(Pointer memory) {
        return new NativeLongHandle(memory);
    }

    public static NativeLongHandle allocate() throws IOException {
        return new NativeLongHandle(Pointer.allocate(Foreign.getInstance().nativeLongSize()));
    }

    public static NativeLongHandle allocateDirect() throws IOException {
        return new NativeLongHandle(Pointer.allocateDirect(Foreign.getInstance().nativeLongSize()));
    }

    protected NativeLongHandle(Pointer memory) {
        super(memory);
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, Foreign.getInstance().nativeLongSize());
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, Foreign.getInstance().nativeLongSize());
    }

    public long get() {
        return payload().getNativeLong(0);
    }

    public BigInteger getUnsigned() {
        return payload().getUnsignedNativeLong(0);
    }

    public void set(long newValue) {
        payload().putNativeLong(0, newValue);
    }

    public void setUnsigned(BigInteger newValue) {
        payload().putUnsignedNativeLong(0, newValue);
    }

    @Override
    public int compareTo(NativeLongHandle other) {
        return payload().compareTo(other.payload(), 0, Foreign.getInstance().nativeLongSize());
    }

}
