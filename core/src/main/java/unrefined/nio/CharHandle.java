package unrefined.nio;

import java.io.IOException;

public class CharHandle extends Pointer.Handle implements Comparable<CharHandle> {

    public static CharHandle wrap(Pointer memory) {
        return new CharHandle(memory);
    }

    public static CharHandle allocate() throws IOException {
        return new CharHandle(Pointer.allocate(2));
    }

    public static CharHandle allocateDirect() throws IOException {
        return new CharHandle(Pointer.allocateDirect(2));
    }

    protected CharHandle(Pointer memory) {
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

    public char get() {
        return payload().getChar(0);
    }

    public void set(char newValue) {
        payload().putChar(0, newValue);
    }

    @Override
    public int compareTo(CharHandle other) {
        return payload().compareTo(other.payload(), 0, 2);
    }

}
