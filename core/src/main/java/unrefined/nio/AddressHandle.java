package unrefined.nio;

import unrefined.util.foreign.Foreign;

import java.io.IOException;

public class AddressHandle extends Pointer.Handle implements Comparable<AddressHandle> {

    public static AddressHandle wrap(Pointer memory) {
        return new AddressHandle(memory);
    }

    public static AddressHandle allocate() throws IOException {
        return new AddressHandle(Pointer.allocate(Foreign.getInstance().addressSize()));
    }

    public static AddressHandle allocateDirect() throws IOException {
        return new AddressHandle(Pointer.allocateDirect(Foreign.getInstance().addressSize()));
    }

    protected AddressHandle(Pointer memory) {
        super(memory);
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, Foreign.getInstance().addressSize());
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, Foreign.getInstance().addressSize());
    }

    public long get() {
        return payload().getAddress(0);
    }

    public void set(long newValue) {
        payload().putAddress(0, newValue);
    }

    @Override
    public int compareTo(AddressHandle other) {
        return payload().compareTo(other.payload(), 0, Foreign.getInstance().addressSize());
    }

}
