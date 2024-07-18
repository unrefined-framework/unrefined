package unrefined.nio;

import java.io.IOException;

public class FloatHandle extends Pointer.Handle implements Comparable<FloatHandle> {

    public static FloatHandle wrap(Pointer memory) {
        return new FloatHandle(memory);
    }

    public static FloatHandle allocate() throws IOException {
        return new FloatHandle(Pointer.allocate(4));
    }

    public static FloatHandle allocateDirect() throws IOException {
        return new FloatHandle(Pointer.allocateDirect(4));
    }

    protected FloatHandle(Pointer memory) {
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

    public float get() {
        return payload().getFloat(0);
    }

    public void set(float newValue) {
        payload().putFloat(0, newValue);
    }

    @Override
    public int compareTo(FloatHandle other) {
        return payload().compareTo(other.payload(), 0, 4);
    }

}
