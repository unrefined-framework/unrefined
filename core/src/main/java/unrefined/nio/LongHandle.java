package unrefined.nio;

import java.io.IOException;
import java.math.BigInteger;

public class LongHandle extends Pointer.Handle implements Comparable<LongHandle> {

    public static LongHandle wrap(Pointer memory) {
        return new LongHandle(memory);
    }

    public static LongHandle allocate() throws IOException {
        return new LongHandle(Pointer.allocate(8));
    }

    public static LongHandle allocateDirect() throws IOException {
        return new LongHandle(Pointer.allocateDirect(8));
    }

    protected LongHandle(Pointer memory) {
        super(memory);
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, 8);
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, 8);
    }

    public long get() {
        return payload().getLong(0);
    }

    public BigInteger getUnsigned() {
        return payload().getUnsignedLong(0);
    }

    public void set(long newValue) {
        payload().putLong(0, newValue);
    }

    public void setUnsigned(BigInteger newValue) {
        payload().putUnsignedLong(0, newValue);
    }

    @Override
    public int compareTo(LongHandle other) {
        return payload().compareTo(other.payload(), 0, 8);
    }

}
