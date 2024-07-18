package unrefined.nio;

import java.io.IOException;

public class BooleanHandle extends Pointer.Handle implements Comparable<BooleanHandle> {

    public static BooleanHandle wrap(Pointer memory) {
        return new BooleanHandle(memory);
    }

    public static BooleanHandle allocate() throws IOException {
        return new BooleanHandle(Pointer.allocate(1));
    }

    public static BooleanHandle allocateDirect() throws IOException {
        return new BooleanHandle(Pointer.allocateDirect(1));
    }

    protected BooleanHandle(Pointer memory) {
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

    public boolean get() {
        return payload().getBoolean(0);
    }

    public void set(boolean newValue) {
        payload().putBoolean(0, newValue);
    }

    @Override
    public int compareTo(BooleanHandle other) {
        return payload().compareTo(other.payload(), 0, 1);
    }

}
