package unrefined.nio;

import unrefined.internal.NumberUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;

/**
 * Object-oriented wrapper for memory-operation methods in {@link Allocator}.
 * {@code ByteOrder} is adapted internally: convert to native order while writing from java side to native side,
 * or convert to java order while reading from native side to java side.
 */
public class DirectPointer extends Pointer {

    private final long address;
    private final long size;
    private final boolean bounded;

    private final DirectPointer attachment;
    private final boolean hasMemory;

    public DirectPointer(Allocator allocator, long address, long size, boolean hasMemory) {
        super(allocator);
        this.attachment = null;
        this.address = address;
        this.size = size;
        this.hasMemory = hasMemory;
        bounded = true;
    }

    protected DirectPointer(DirectPointer attachment, long address, long size) {
        super(attachment.getAllocator());
        this.attachment = attachment;
        this.address = address;
        this.size = size;
        this.hasMemory = false;
        bounded = true;
    }

    public DirectPointer(Allocator allocator, long address) {
        super(allocator);
        this.attachment = null;
        this.address = address;
        this.size = -1;
        this.hasMemory = false;
        bounded = false;
    }

    protected DirectPointer(DirectPointer attachment, long address) {
        super(attachment.getAllocator());
        this.attachment = attachment;
        this.address = address;
        this.size = -1;
        this.hasMemory = false;
        bounded = false;
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    public long address() {
        return address;
    }

    @Override
    public long size() throws UnboundedPointerException {
        if (bounded) return size;
        else throw new UnboundedPointerException();
    }

    @Override
    public boolean isBounded() {
        return bounded;
    }

    @Override
    public boolean hasArrays() {
        return false;
    }

    @Override
    public byte[][] arrays() {
        return null;
    }

    @Override
    public long arraysOffset() {
        return -1;
    }

    @Override
    public long arraysLength() {
        return -1;
    }

    @Override
    public byte getByte(long offset) {
        return getAllocator().getByte(address + offset);
    }

    @Override
    public short getUnsignedByte(long offset) {
        return getAllocator().getUnsignedByte(address + offset);
    }

    @Override
    public short getShort(long offset) {
        return getAllocator().getShort(address + offset);
    }

    @Override
    public int getUnsignedShort(long offset) {
        return getAllocator().getUnsignedShort(address + offset);
    }

    @Override
    public char getChar(long offset) {
        return getAllocator().getChar(address + offset);
    }

    @Override
    public int getInt(long offset) {
        return getAllocator().getInt(address + offset);
    }

    @Override
    public long getUnsignedInt(long offset) {
        return getAllocator().getUnsignedInt(address + offset);
    }

    @Override
    public long getLong(long offset) {
        return getAllocator().getLong(address + offset);
    }

    @Override
    public BigInteger getUnsignedLong(long offset) {
        return getAllocator().getUnsignedLong(address + offset);
    }

    @Override
    public long getNativeInt(long offset) {
        return getAllocator().getNativeInt(address + offset);
    }

    @Override
    public long getNativeLong(long offset) {
        return getAllocator().getNativeLong(address + offset);
    }

    @Override
    public long getAddress(long offset) {
        return getAllocator().getAddress(address + offset);
    }

    @Override
    public float getFloat(long offset) {
        return getAllocator().getFloat(address + offset);
    }

    @Override
    public double getDouble(long offset) {
        return getAllocator().getDouble(address + offset);
    }

    @Override
    public void putByte(long offset, byte value) {
        getAllocator().putByte(address + offset, value);
    }

    @Override
    public void putByte(long offset, int value) {
        getAllocator().putByte(address + offset, value);
    }

    @Override
    public void putUnsignedByte(long offset, short value) {
        getAllocator().putUnsignedByte(address + offset, value);
    }

    @Override
    public void putUnsignedByte(long offset, int value) {
        getAllocator().putUnsignedByte(address + offset, value);
    }

    @Override
    public void putShort(long offset, short value) {
        getAllocator().putShort(address + offset, value);
    }

    @Override
    public void putShort(long offset, int value) {
        getAllocator().putShort(address + offset, value);
    }

    @Override
    public void putUnsignedShort(long offset, int value) {
        getAllocator().putUnsignedShort(address + offset, value);
    }

    @Override
    public void putInt(long offset, int value) {
        getAllocator().putInt(address + offset, value);
    }

    @Override
    public void putUnsignedInt(long offset, long value) {
        getAllocator().putUnsignedInt(address + offset, value);
    }

    @Override
    public void putLong(long offset, long value) {
        getAllocator().putLong(address + offset, value);
    }

    @Override
    public void putUnsignedLong(long offset, BigInteger value) {
        getAllocator().putUnsignedLong(address + offset, value);
    }

    @Override
    public void putFloat(long offset, float value) {
        getAllocator().putFloat(address + offset, value);
    }

    @Override
    public void putDouble(long offset, double value) {
        getAllocator().putDouble(address + offset, value);
    }

    @Override
    public void putNativeInt(long offset, long value) {
        getAllocator().putNativeInt(address + offset, value);
    }

    @Override
    public void putNativeLong(long offset, long value) {
        getAllocator().putNativeLong(address + offset, value);
    }

    @Override
    public void putAddress(long offset, long value) {
        getAllocator().putAddress(address + offset, value);
    }

    @Override
    public void getByteArray(long offset, byte[] array, int index, int length) {
        getAllocator().getByteArray(address + offset, array, index, length);
    }

    @Override
    public void getByteArray(long offset, byte[] array) {
        getAllocator().getByteArray(address + offset, array);
    }

    @Override
    public void putByteArray(long offset, byte[] array, int index, int length) {
        getAllocator().putByteArray(address + offset, array, index, length);
    }

    @Override
    public void putByteArray(long offset, byte[] array) {
        getAllocator().putByteArray(address + offset, array);
    }

    @Override
    public void getShortArray(long offset, short[] array, int index, int length) {
        getAllocator().getShortArray(address + offset, array, index, length);
    }

    @Override
    public void getShortArray(long offset, short[] array) {
        getAllocator().getShortArray(address + offset, array);
    }

    @Override
    public void putShortArray(long offset, short[] array, int index, int length) {
        getAllocator().putShortArray(address + offset, array, index, length);
    }

    @Override
    public void putShortArray(long offset, short[] array) {
        getAllocator().putShortArray(address + offset, array);
    }

    @Override
    public void getCharArray(long offset, char[] array, int index, int length) {
        getAllocator().getCharArray(address + offset, array, index, length);
    }

    @Override
    public void getCharArray(long offset, char[] array) {
        getAllocator().getCharArray(address + offset, array);
    }

    @Override
    public void putCharArray(long offset, char[] array, int index, int length) {
        getAllocator().putCharArray(address + offset, array, index, length);
    }

    @Override
    public void putCharArray(long offset, char[] array) {
        getAllocator().putCharArray(address + offset, array);
    }

    @Override
    public void getIntArray(long offset, int[] array, int index, int length) {
        getAllocator().getIntArray(address + offset, array, index, length);
    }

    @Override
    public void getIntArray(long offset, int[] array) {
        getAllocator().getIntArray(address + offset, array);
    }

    @Override
    public void putIntArray(long offset, int[] array, int index, int length) {
        getAllocator().putIntArray(address + offset, array, index, length);
    }

    @Override
    public void putIntArray(long offset, int[] array) {
        getAllocator().putIntArray(address + offset, array);
    }

    @Override
    public void getLongArray(long offset, long[] array, int index, int length) {
        getAllocator().getLongArray(address + offset, array, index, length);
    }

    @Override
    public void getLongArray(long offset, long[] array) {
        getAllocator().getLongArray(address + offset, array);
    }

    @Override
    public void putLongArray(long offset, long[] array, int index, int length) {
        getAllocator().putLongArray(address + offset, array, index, length);
    }

    @Override
    public void putLongArray(long offset, long[] array) {
        getAllocator().putLongArray(address + offset, array);
    }

    @Override
    public void getFloatArray(long offset, float[] array, int index, int length) {
        getAllocator().getFloatArray(address + offset, array, index, length);
    }

    @Override
    public void getFloatArray(long offset, float[] array) {
        getAllocator().getFloatArray(address + offset, array);
    }

    @Override
    public void putFloatArray(long offset, float[] array, int index, int length) {
        getAllocator().putFloatArray(address + offset, array, index, length);
    }

    @Override
    public void putFloatArray(long offset, float[] array) {
        getAllocator().putFloatArray(address + offset, array);
    }

    @Override
    public void getDoubleArray(long offset, double[] array, int index, int length) {
        getAllocator().getDoubleArray(address + offset, array, index, length);
    }

    @Override
    public void getDoubleArray(long offset, double[] array) {
        getAllocator().getDoubleArray(address + offset, array);
    }

    @Override
    public void putDoubleArray(long offset, double[] array, int index, int length) {
        getAllocator().putDoubleArray(address + offset, array, index, length);
    }

    @Override
    public void putDoubleArray(long offset, double[] array) {
        getAllocator().putDoubleArray(address + offset, array);
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset) {
        return getAllocator().getZeroTerminatedByteArray(address + offset);
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset, int maxLength) {
        return getAllocator().getZeroTerminatedByteArray(address + offset, maxLength);
    }

    @Override
    public String getZeroTerminatedString(long offset, Charset charset) {
        return getAllocator().getZeroTerminatedString(address + offset, charset);
    }

    @Override
    public String getZeroTerminatedString(long offset, int maxLength, Charset charset) {
        return getAllocator().getZeroTerminatedString(address + offset, maxLength, charset);
    }

    @Override
    public void putZeroTerminatedByteArray(long offset, byte[] array) {
        getAllocator().putZeroTerminatedByteArray(address + offset, array);
    }

    @Override
    public void putZeroTerminatedByteArray(long offset, byte[] array, int index, int length) {
        getAllocator().putZeroTerminatedByteArray(address + offset, array, index, length);
    }

    @Override
    public void putZeroTerminatedString(long offset, String string, Charset charset) {
        getAllocator().putZeroTerminatedString(address + offset, string, charset);
    }

    @Override
    public Pointer slice(long offset) {
        if (bounded) return new DirectPointer(this, address + offset, size - offset);
        else return new DirectPointer(this, address + offset);
    }

    @Override
    public Pointer slice(long offset, long size) {
        return new DirectPointer(this, address + offset, size);
    }

    @Override
    public Pointer duplicate() {
        if (bounded) return new DirectPointer(this, address, size);
        else return new DirectPointer(this, address);
    }

    @Override
    public Pointer attachment() {
        return attachment;
    }

    // TODO performance optimization
    @Override
    public void transferTo(long offset, Pointer dstPointer, long dstOffset, long count) {
        if (bounded) checkBounds(offset, count);
        if (dstPointer.isBounded()) dstPointer.checkBounds(dstOffset, count);
        if (dstPointer instanceof DirectPointer) {
            getAllocator().copyMemory(address + offset, dstPointer.address() + dstOffset, count);
        }
        else if (count < 0) {
            for (long i = 0; i < Long.MAX_VALUE; i ++) {
                dstPointer.putByte(dstOffset + i, getByte(offset + i));
            }
            for (long i = Long.MIN_VALUE; i <= count; i ++) {
                dstPointer.putByte(dstOffset + i, getByte(offset + i));
            }
        }
        else {
            for (long i = 0; i < count; i ++) {
                dstPointer.putByte(dstOffset + i, getByte(offset + i));
            }
        }
    }

    @Override
    public void checkBounds(long offset, long length) throws IndexOutOfBoundsException {
        BigInteger address = NumberUtils.toUnsignedBigInteger(this.address);
        BigInteger size = address.add(NumberUtils.toUnsignedBigInteger(this.size));
        BigInteger checkIndex = address
                .add(NumberUtils.toUnsignedBigInteger(offset))
                .add(NumberUtils.toUnsignedBigInteger(length));
        if (bounded && checkIndex.compareTo(size) < 0) return;
        throw new IndexOutOfBoundsException("Index out of range: " + checkIndex);
    }

    @Override
    public boolean inBounds(long offset, long length) {
        BigInteger address = NumberUtils.toUnsignedBigInteger(this.address);
        BigInteger size = address.add(NumberUtils.toUnsignedBigInteger(this.size));
        BigInteger checkIndex = address
                .add(NumberUtils.toUnsignedBigInteger(offset))
                .add(NumberUtils.toUnsignedBigInteger(length));
        if (bounded) return checkIndex.compareTo(size) < 0;
        else return false;
    }

    @Override
    public void setMemory(long offset, long size, byte value) {
        getAllocator().setMemory(address + offset, size, value);
    }

    @Override
    public void setMemory(long offset, long size, int value) {
        getAllocator().setMemory(address + offset, size, value);
    }

    @Override
    public long indexOf(long offset, byte value) {
        return getAllocator().indexOf(address + offset, value);
    }

    @Override
    public long indexOf(long offset, int value) {
        return getAllocator().indexOf(address + offset, value);
    }

    @Override
    public long indexOf(long offset, byte value, long maxLength) {
        return getAllocator().indexOf(address + offset, value, maxLength);
    }

    @Override
    public long indexOf(long offset, int value, long maxLength) {
        return getAllocator().indexOf(address + offset, value, maxLength);
    }

    @Override
    public Pointer reallocate(long size) throws IOException {
        if (!hasMemory) throw new IOException("duplicate or slice");
        else {
            long address = getAllocator().reallocateMemory(this.address, size);
            if (address == 0) throw new IOException("Unable to allocate native memory, size: " + NumberUtils.toUnsignedBigInteger(size));
            else return new DirectPointer(getAllocator(), address, size, true);
        }
    }

    @Override
    public boolean hasMemory() {
        return hasMemory;
    }

    @Override
    public void close() throws IOException {
        if (hasMemory) getAllocator().freeMemory(address);
    }

}
