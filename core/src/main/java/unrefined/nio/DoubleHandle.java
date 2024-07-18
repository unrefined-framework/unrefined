package unrefined.nio;

import java.io.IOException;

public class DoubleHandle extends Pointer.Handle implements Comparable<DoubleHandle> {

    public static DoubleHandle wrap(Pointer memory) {
        return new DoubleHandle(memory);
    }

    public static DoubleHandle allocate() throws IOException {
        return new DoubleHandle(Pointer.allocate(8));
    }

    public static DoubleHandle allocateDirect() throws IOException {
        return new DoubleHandle(Pointer.allocateDirect(8));
    }

    protected DoubleHandle(Pointer memory) {
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

    public double get() {
        return payload().getDouble(0);
    }

    public void set(double newValue) {
        payload().putDouble(0, newValue);
    }

    @Override
    public int compareTo(DoubleHandle other) {
        return payload().compareTo(other.payload(), 0, 8);
    }

}
