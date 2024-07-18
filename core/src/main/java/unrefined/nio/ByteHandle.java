package unrefined.nio;

import java.io.IOException;

public class ByteHandle extends Pointer.Handle implements Comparable<ByteHandle> {

    public static ByteHandle wrap(Pointer memory) {
        return new ByteHandle(memory);
    }

    public static ByteHandle allocate() throws IOException {
        return new ByteHandle(Pointer.allocate(1));
    }

    public static ByteHandle allocateDirect() throws IOException {
        return new ByteHandle(Pointer.allocateDirect(1));
    }

    protected ByteHandle(Pointer memory) {
        super(memory);
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, 1);
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, 1);
    }

    public byte get() {
        return payload().getByte(0);
    }

    public short getUnsigned() {
        return payload().getUnsignedByte(0);
    }

    public void set(byte newValue) {
        payload().putByte(0, newValue);
    }

    public void set(int newValue) {
        payload().putByte(0, newValue);
    }

    public void setUnsigned(short newValue) {
        payload().putUnsignedByte(0, newValue);
    }

    @Override
    public int compareTo(ByteHandle other) {
        return payload().compareTo(other.payload(), 0, 1);
    }

}
