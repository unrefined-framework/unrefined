package unrefined.nio;

import unrefined.util.foreign.Foreign;

import java.io.IOException;

public class AddressArrayHandle extends Pointer.Handle implements Comparable<AddressArrayHandle> {

    public static AddressArrayHandle wrap(Pointer memory, int length) {
        return new AddressArrayHandle(memory, length);
    }

    public static AddressArrayHandle allocate(int length) throws IOException {
        return new AddressArrayHandle(Pointer.allocate((long) length * Foreign.getInstance().addressSize()), length);
    }

    public static AddressArrayHandle allocateDirect(int length) throws IOException {
        return new AddressArrayHandle(Pointer.allocateDirect((long) length * Foreign.getInstance().addressSize()), length);
    }

    private final int length;

    protected AddressArrayHandle(Pointer memory, int length) {
        super(memory);
        this.length = length;
    }

    public int length() {
        return length;
    }

    @Override
    public void set(Pointer memory, long offset) {
        payload().transferFrom(0, memory, offset, (long) length * Foreign.getInstance().addressSize());
    }

    @Override
    public void get(Pointer memory, long offset) {
        payload().transferTo(0, memory, offset, (long) length * Foreign.getInstance().addressSize());
    }

    public void get(long[] array, int offset) {
        payload().getAddressArray(0, array, offset, length);
    }

    public void get(long[] array) {
        payload().getAddressArray(0, array, 0, length);
    }

    public void set(long[] array, int offset) {
        payload().putAddressArray(0, array, offset, length);
    }

    public void set(long[] array) {
        payload().putAddressArray(0, array, 0, length);
    }

    @Override
    public int compareTo(AddressArrayHandle other) {
        return payload().compareTo(other.payload(), 0, (long) length * Foreign.getInstance().addressSize());
    }

}
