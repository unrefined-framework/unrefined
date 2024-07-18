package unrefined.nio;

import java.io.IOException;

public class IntHandle extends Pointer.Handle implements Comparable<IntHandle> {

    public static IntHandle wrap(Pointer memory) {
        return new IntHandle(memory);
    }

    public static IntHandle allocate() throws IOException {
        return new IntHandle(Pointer.allocate(4));
    }

    public static IntHandle allocateDirect() throws IOException {
        return new IntHandle(Pointer.allocateDirect(4));
    }

    protected IntHandle(Pointer memory) {
        super(memory);
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, 4);
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, 4);
    }

    public int get() {
        return payload().getInt(0);
    }

    public long getUnsigned() {
        return payload().getUnsignedInt(0);
    }

    public void set(int newValue) {
        payload().putInt(0, newValue);
    }

    public void setUnsigned(long newValue) {
        payload().putUnsignedInt(0, newValue);
    }

    @Override
    public int compareTo(IntHandle other) {
        return payload().compareTo(other.payload(), 0, 4);
    }

}
