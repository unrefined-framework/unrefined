package unrefined.nio;

import java.io.IOException;

public class ShortHandle extends Pointer.Handle implements Comparable<ShortHandle> {

    public static ShortHandle wrap(Pointer memory) {
        return new ShortHandle(memory);
    }

    public static ShortHandle allocate() throws IOException {
        return new ShortHandle(Pointer.allocate(2));
    }

    public static ShortHandle allocateDirect() throws IOException {
        return new ShortHandle(Pointer.allocateDirect(2));
    }

    protected ShortHandle(Pointer memory) {
        super(memory);
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, 2);
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, 2);
    }

    public short get() {
        return payload().getShort(0);
    }

    public int getUnsigned() {
        return payload().getUnsignedShort(0);
    }

    public void set(short newValue) {
        payload().putShort(0, newValue);
    }

    public void set(int newValue) {
        payload().putShort(0, newValue);
    }

    public void setUnsigned(int newValue) {
        payload().putUnsignedShort(0, newValue);
    }

    @Override
    public int compareTo(ShortHandle other) {
        return payload().compareTo(other.payload(), 0, 2);
    }

}
