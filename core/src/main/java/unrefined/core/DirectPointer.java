package unrefined.core;

import unrefined.math.FastMath;
import unrefined.nio.Allocator;
import unrefined.nio.Pointer;
import unrefined.nio.UnboundedPointerException;

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

    public DirectPointer(long address, long size, boolean hasMemory) {
        this.attachment = null;
        this.address = address;
        this.size = size;
        this.hasMemory = hasMemory;
        bounded = true;
    }

    protected DirectPointer(DirectPointer attachment, long address, long size) {
        this.attachment = attachment;
        this.address = address;
        this.size = size;
        this.hasMemory = false;
        bounded = true;
    }

    public DirectPointer(long address) {
        this.attachment = null;
        this.address = address;
        this.size = -1;
        this.hasMemory = false;
        bounded = false;
    }

    protected DirectPointer(DirectPointer attachment, long address) {
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
        return Allocator.getInstance().getByte(address + offset);
    }

    @Override
    public short getUnsignedByte(long offset) {
        return Allocator.getInstance().getUnsignedByte(address + offset);
    }

    @Override
    public short getShort(long offset) {
        return Allocator.getInstance().getShort(address + offset);
    }

    @Override
    public int getUnsignedShort(long offset) {
        return Allocator.getInstance().getUnsignedShort(address + offset);
    }

    @Override
    public char getChar(long offset) {
        return Allocator.getInstance().getChar(address + offset);
    }

    @Override
    public int getInt(long offset) {
        return Allocator.getInstance().getInt(address + offset);
    }

    @Override
    public long getUnsignedInt(long offset) {
        return Allocator.getInstance().getUnsignedInt(address + offset);
    }

    @Override
    public long getLong(long offset) {
        return Allocator.getInstance().getLong(address + offset);
    }

    @Override
    public BigInteger getUnsignedLong(long offset) {
        return Allocator.getInstance().getUnsignedLong(address + offset);
    }

    @Override
    public long getNativeInt(long offset) {
        return Allocator.getInstance().getNativeInt(address + offset);
    }

    @Override
    public long getNativeLong(long offset) {
        return Allocator.getInstance().getNativeLong(address + offset);
    }

    @Override
    public long getAddress(long offset) {
        return Allocator.getInstance().getAddress(address + offset);
    }

    @Override
    public float getFloat(long offset) {
        return Allocator.getInstance().getFloat(address + offset);
    }

    @Override
    public double getDouble(long offset) {
        return Allocator.getInstance().getDouble(address + offset);
    }

    @Override
    public void putByte(long offset, byte value) {
        Allocator.getInstance().putByte(address + offset, value);
    }

    @Override
    public void putByte(long offset, int value) {
        Allocator.getInstance().putByte(address + offset, value);
    }

    @Override
    public void putUnsignedByte(long offset, short value) {
        Allocator.getInstance().putUnsignedByte(address + offset, value);
    }

    @Override
    public void putUnsignedByte(long offset, int value) {
        Allocator.getInstance().putUnsignedByte(address + offset, value);
    }

    @Override
    public void putShort(long offset, short value) {
        Allocator.getInstance().putShort(address + offset, value);
    }

    @Override
    public void putShort(long offset, int value) {
        Allocator.getInstance().putShort(address + offset, value);
    }

    @Override
    public void putUnsignedShort(long offset, int value) {
        Allocator.getInstance().putUnsignedShort(address + offset, value);
    }

    @Override
    public void putInt(long offset, int value) {
        Allocator.getInstance().putInt(address + offset, value);
    }

    @Override
    public void putUnsignedInt(long offset, long value) {
        Allocator.getInstance().putUnsignedInt(address + offset, value);
    }

    @Override
    public void putLong(long offset, long value) {
        Allocator.getInstance().putLong(address + offset, value);
    }

    @Override
    public void putUnsignedLong(long offset, BigInteger value) {
        Allocator.getInstance().putUnsignedLong(address + offset, value);
    }

    @Override
    public void putFloat(long offset, float value) {
        Allocator.getInstance().putFloat(address + offset, value);
    }

    @Override
    public void putDouble(long offset, double value) {
        Allocator.getInstance().putDouble(address + offset, value);
    }

    @Override
    public void putNativeInt(long offset, long value) {
        Allocator.getInstance().putNativeInt(address + offset, value);
    }

    @Override
    public void putNativeLong(long offset, long value) {
        Allocator.getInstance().putNativeLong(address + offset, value);
    }

    @Override
    public void putAddress(long offset, long value) {
        Allocator.getInstance().putAddress(address + offset, value);
    }

    @Override
    public void getByteArray(long offset, byte[] array, int index, int length) {
        Allocator.getInstance().getByteArray(address + offset, array, index, length);
    }

    @Override
    public void getByteArray(long offset, byte[] array) {
        Allocator.getInstance().getByteArray(address + offset, array);
    }

    @Override
    public void putByteArray(long offset, byte[] array, int index, int length) {
        Allocator.getInstance().putByteArray(address + offset, array, index, length);
    }

    @Override
    public void putByteArray(long offset, byte[] array) {
        Allocator.getInstance().putByteArray(address + offset, array);
    }

    @Override
    public void getShortArray(long offset, short[] array, int index, int length) {
        Allocator.getInstance().getShortArray(address + offset, array, index, length);
    }

    @Override
    public void getShortArray(long offset, short[] array) {
        Allocator.getInstance().getShortArray(address + offset, array);
    }

    @Override
    public void putShortArray(long offset, short[] array, int index, int length) {
        Allocator.getInstance().putShortArray(address + offset, array, index, length);
    }

    @Override
    public void putShortArray(long offset, short[] array) {
        Allocator.getInstance().putShortArray(address + offset, array);
    }

    @Override
    public void getCharArray(long offset, char[] array, int index, int length) {
        Allocator.getInstance().getCharArray(address + offset, array, index, length);
    }

    @Override
    public void getCharArray(long offset, char[] array) {
        Allocator.getInstance().getCharArray(address + offset, array);
    }

    @Override
    public void putCharArray(long offset, char[] array, int index, int length) {
        Allocator.getInstance().putCharArray(address + offset, array, index, length);
    }

    @Override
    public void putCharArray(long offset, char[] array) {
        Allocator.getInstance().putCharArray(address + offset, array);
    }

    @Override
    public void getIntArray(long offset, int[] array, int index, int length) {
        Allocator.getInstance().getIntArray(address + offset, array, index, length);
    }

    @Override
    public void getIntArray(long offset, int[] array) {
        Allocator.getInstance().getIntArray(address + offset, array);
    }

    @Override
    public void putIntArray(long offset, int[] array, int index, int length) {
        Allocator.getInstance().putIntArray(address + offset, array, index, length);
    }

    @Override
    public void putIntArray(long offset, int[] array) {
        Allocator.getInstance().putIntArray(address + offset, array);
    }

    @Override
    public void getLongArray(long offset, long[] array, int index, int length) {
        Allocator.getInstance().getLongArray(address + offset, array, index, length);
    }

    @Override
    public void getLongArray(long offset, long[] array) {
        Allocator.getInstance().getLongArray(address + offset, array);
    }

    @Override
    public void putLongArray(long offset, long[] array, int index, int length) {
        Allocator.getInstance().putLongArray(address + offset, array, index, length);
    }

    @Override
    public void putLongArray(long offset, long[] array) {
        Allocator.getInstance().putLongArray(address + offset, array);
    }

    @Override
    public void getFloatArray(long offset, float[] array, int index, int length) {
        Allocator.getInstance().getFloatArray(address + offset, array, index, length);
    }

    @Override
    public void getFloatArray(long offset, float[] array) {
        Allocator.getInstance().getFloatArray(address + offset, array);
    }

    @Override
    public void putFloatArray(long offset, float[] array, int index, int length) {
        Allocator.getInstance().putFloatArray(address + offset, array, index, length);
    }

    @Override
    public void putFloatArray(long offset, float[] array) {
        Allocator.getInstance().putFloatArray(address + offset, array);
    }

    @Override
    public void getDoubleArray(long offset, double[] array, int index, int length) {
        Allocator.getInstance().getDoubleArray(address + offset, array, index, length);
    }

    @Override
    public void getDoubleArray(long offset, double[] array) {
        Allocator.getInstance().getDoubleArray(address + offset, array);
    }

    @Override
    public void putDoubleArray(long offset, double[] array, int index, int length) {
        Allocator.getInstance().putDoubleArray(address + offset, array, index, length);
    }

    @Override
    public void putDoubleArray(long offset, double[] array) {
        Allocator.getInstance().putDoubleArray(address + offset, array);
    }

    @Override
    public long getZeroTerminatedStringLength(long offset) {
        return Allocator.getInstance().getZeroTerminatedStringLength(address + offset);
    }

    @Override
    public long getZeroTerminatedStringLength(long offset, long maxLength) {
        return Allocator.getInstance().getZeroTerminatedWideCharStringLength(address + offset, maxLength);
    }

    @Override
    public long getZeroTerminatedWideCharStringLength(long offset) {
        return Allocator.getInstance().getZeroTerminatedWideCharStringLength(address + offset);
    }

    @Override
    public long getZeroTerminatedWideCharStringLength(long offset, long maxLength) {
        return Allocator.getInstance().getZeroTerminatedStringLength(address + offset, maxLength);
    }

    @Override
    public long getZeroTerminatedStringLength(long offset, Charset charset) {
        return Allocator.getInstance().getZeroTerminatedStringLength(address + offset, charset);
    }

    @Override
    public long getZeroTerminatedStringLength(long offset, long maxLength, Charset charset) {
        return Allocator.getInstance().getZeroTerminatedStringLength(address + offset, maxLength, charset);
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset) {
        return Allocator.getInstance().getZeroTerminatedByteArray(address + offset);
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset, int maxLength) {
        return Allocator.getInstance().getZeroTerminatedByteArray(address + offset, maxLength);
    }

    @Override
    public byte[] getZeroTerminatedWideCharByteArray(long offset) {
        return Allocator.getInstance().getZeroTerminatedWideCharByteArray(address + offset);
    }

    @Override
    public byte[] getZeroTerminatedWideCharByteArray(long offset, int maxLength) {
        return Allocator.getInstance().getZeroTerminatedWideCharByteArray(address + offset, maxLength);
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset, Charset charset) {
        return Allocator.getInstance().getZeroTerminatedByteArray(address + offset, charset);
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset, int maxLength, Charset charset) {
        return Allocator.getInstance().getZeroTerminatedByteArray(address + offset, maxLength, charset);
    }

    @Override
    public String getZeroTerminatedString(long offset) {
        return Allocator.getInstance().getZeroTerminatedString(address + offset);
    }

    @Override
    public String getZeroTerminatedString(long offset, int maxLength) {
        return Allocator.getInstance().getZeroTerminatedString(address + offset, maxLength);
    }

    @Override
    public String getZeroTerminatedWideCharString(long offset) {
        return Allocator.getInstance().getZeroTerminatedWideCharString(address + offset);
    }

    @Override
    public String getZeroTerminatedWideCharString(long offset, int maxLength) {
        return Allocator.getInstance().getZeroTerminatedWideCharString(address + offset, maxLength);
    }

    @Override
    public String getZeroTerminatedString(long offset, Charset charset) {
        return Allocator.getInstance().getZeroTerminatedString(address + offset, charset);
    }

    @Override
    public String getZeroTerminatedString(long offset, int maxLength, Charset charset) {
        return Allocator.getInstance().getZeroTerminatedString(address + offset, maxLength, charset);
    }

    @Override
    public void putZeroTerminatedByteArray(long offset, byte[] array) {
        Allocator.getInstance().putZeroTerminatedByteArray(address + offset, array);
    }

    @Override
    public void putZeroTerminatedByteArray(long offset, byte[] array, int index, int length) {
        Allocator.getInstance().putZeroTerminatedByteArray(address + offset, array, index, length);
    }

    @Override
    public void putZeroTerminatedWideCharByteArray(long offset, byte[] array) {
        Allocator.getInstance().putZeroTerminatedWideCharByteArray(address + offset, array);
    }

    @Override
    public void putZeroTerminatedWideCharByteArray(long offset, byte[] array, int index, int length) {
        Allocator.getInstance().putZeroTerminatedWideCharByteArray(address + offset, array, index, length);
    }

    @Override
    public void putZeroTerminatedByteArray(long offset, byte[] array, Charset charset) {
        Allocator.getInstance().putZeroTerminatedByteArray(address + offset, array, charset);
    }

    @Override
    public void putZeroTerminatedByteArray(long offset, byte[] array, int index, int length, Charset charset) {
        Allocator.getInstance().putZeroTerminatedByteArray(address + offset, array, index, length, charset);
    }

    @Override
    public void putZeroTerminatedString(long offset, String string) {
        Allocator.getInstance().putZeroTerminatedString(address + offset, string);
    }

    @Override
    public void putZeroTerminatedWideCharString(long offset, String string) {
        Allocator.getInstance().putZeroTerminatedWideCharString(address + offset, string);
    }

    @Override
    public void putZeroTerminatedString(long offset, String string, Charset charset) {
        Allocator.getInstance().putZeroTerminatedString(address + offset, string, charset);
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
            Allocator.getInstance().copyMemory(address + offset, dstPointer.address() + dstOffset, count);
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
        BigInteger address = FastMath.unsign(this.address);
        BigInteger size = address.add(FastMath.unsign(this.size));
        BigInteger checkIndex = address
                .add(FastMath.unsign(offset))
                .add(FastMath.unsign(length));
        if (bounded && checkIndex.compareTo(size) < 0) return;
        throw new IndexOutOfBoundsException("Index out of range: " + checkIndex);
    }

    @Override
    public boolean inBounds(long offset, long length) {
        BigInteger address = FastMath.unsign(this.address);
        BigInteger size = address.add(FastMath.unsign(this.size));
        BigInteger checkIndex = address
                .add(FastMath.unsign(offset))
                .add(FastMath.unsign(length));
        if (bounded) return checkIndex.compareTo(size) < 0;
        else return false;
    }

    @Override
    public void setMemory(long offset, long size, byte value) {
        Allocator.getInstance().setMemory(address + offset, size, value);
    }

    @Override
    public void setMemory(long offset, long size, int value) {
        Allocator.getInstance().setMemory(address + offset, size, value);
    }

    @Override
    public long indexOf(long offset, byte value) {
        return Allocator.getInstance().indexOf(address + offset, value);
    }

    @Override
    public long indexOf(long offset, int value) {
        return Allocator.getInstance().indexOf(address + offset, value);
    }

    @Override
    public long indexOf(long offset, byte value, long maxLength) {
        return Allocator.getInstance().indexOf(address + offset, value, maxLength);
    }

    @Override
    public long indexOf(long offset, int value, long maxLength) {
        return Allocator.getInstance().indexOf(address + offset, value, maxLength);
    }

    @Override
    public long indexOf(long offset, byte[] value) {
        return Allocator.getInstance().indexOf(address + offset, value);
    }

    @Override
    public long indexOf(long offset, byte[] value, int valueOffset, int valueLength) {
        return Allocator.getInstance().indexOf(address + offset, value, valueOffset, valueLength);
    }

    @Override
    public long indexOf(long offset, byte[] value, long maxLength) {
        return Allocator.getInstance().indexOf(address + offset, value, maxLength);
    }

    @Override
    public long indexOf(long offset, byte[] value, int valueOffset, int valueLength, long maxLength) {
        return Allocator.getInstance().indexOf(address + offset, value, valueOffset, valueLength, maxLength);
    }

    @Override
    public Pointer reallocate(long size) throws IOException {
        if (!hasMemory) throw new IOException("duplicate or slice");
        else {
            long address = Allocator.getInstance().reallocateMemory(this.address, size);
            if (address == 0) throw new IOException("Unable to allocate native memory, size: " + FastMath.unsign(size));
            else return new DirectPointer(address, size, true);
        }
    }

    @Override
    public boolean hasMemory() {
        return hasMemory;
    }

    @Override
    public boolean isNullPointer() {
        return address == 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Pointer)) return false;

        Pointer that = (Pointer) object;

        if (isNullPointer() && that.isNullPointer()) return true;
        else if (that.isDirect()) return address() == that.address();
        else return false;
    }

    @Override
    public int hashCode() {
        return (int) (address ^ (address >>> 32));
    }

    @Override
    public String toString() {
        return getClass().getName() + '@' + Integer.toHexString(hashCode())
                + '{' +
                "hasMemory=" + hasMemory +
                ", address=" + address +
                '}';
    }

    @Override
    public void close() throws IOException {
        if (hasMemory) Allocator.getInstance().freeMemory(address);
    }

}
