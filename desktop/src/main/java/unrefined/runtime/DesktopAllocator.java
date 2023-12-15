package unrefined.runtime;

import sun.misc.Unsafe;
import unrefined.internal.BufferUtils;
import unrefined.internal.CLibraryUtils;
import unrefined.internal.MemoryLayoutUtils;
import unrefined.internal.NativeStringUtils;
import unrefined.internal.UnsafeUtils;
import unrefined.nio.Allocator;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

// TODO performance optimization
public class DesktopAllocator extends Allocator {

    @Override
    public void putByteArray(long address, byte[] array, int offset, int length) {
        MemorySegment.copy(array, offset, MemorySegment.ofAddress(address), ValueLayout.JAVA_BYTE, 0, length);
    }

    @Override
    public void getByteArray(long address, byte[] array, int offset, int length) {
        MemorySegment.copy(MemorySegment.ofAddress(address), ValueLayout.JAVA_BYTE, 0, array, offset, length);
    }

    @Override
    public void putShortArray(long address, short[] array, int offset, int length) {
        MemorySegment.copy(array, offset, MemorySegment.ofAddress(address), ValueLayout.JAVA_SHORT, 0, length);
    }

    @Override
    public void getShortArray(long address, short[] array, int offset, int length) {
        MemorySegment.copy(MemorySegment.ofAddress(address), ValueLayout.JAVA_SHORT, 0, array, offset, length);
    }

    @Override
    public void putCharArray(long address, char[] array, int offset, int length) {
        MemorySegment.copy(array, offset, MemorySegment.ofAddress(address), ValueLayout.JAVA_CHAR, 0, length);
    }

    @Override
    public void getCharArray(long address, char[] array, int offset, int length) {
        MemorySegment.copy(MemorySegment.ofAddress(address), ValueLayout.JAVA_CHAR, 0, array, offset, length);
    }

    @Override
    public void putIntArray(long address, int[] array, int offset, int length) {
        MemorySegment.copy(array, offset, MemorySegment.ofAddress(address), ValueLayout.JAVA_INT, 0, length);
    }

    @Override
    public void getIntArray(long address, int[] array, int offset, int length) {
        MemorySegment.copy(MemorySegment.ofAddress(address), ValueLayout.JAVA_INT, 0, array, offset, length);
    }

    @Override
    public void putLongArray(long address, long[] array, int offset, int length) {
        MemorySegment.copy(array, offset, MemorySegment.ofAddress(address), ValueLayout.JAVA_LONG, 0, length);
    }

    @Override
    public void getLongArray(long address, long[] array, int offset, int length) {
        MemorySegment.copy(MemorySegment.ofAddress(address), ValueLayout.JAVA_LONG, 0, array, offset, length);
    }

    @Override
    public void putFloatArray(long address, float[] array, int offset, int length) {
        MemorySegment.copy(array, offset, MemorySegment.ofAddress(address), ValueLayout.JAVA_FLOAT, 0, length);
    }

    @Override
    public void getFloatArray(long address, float[] array, int offset, int length) {
        MemorySegment.copy(MemorySegment.ofAddress(address), ValueLayout.JAVA_FLOAT, 0, array, offset, length);
    }

    @Override
    public void putDoubleArray(long address, double[] array, int offset, int length) {
        MemorySegment.copy(array, offset, MemorySegment.ofAddress(address), ValueLayout.JAVA_DOUBLE, 0, length);
    }

    @Override
    public void getDoubleArray(long address, double[] array, int offset, int length) {
        MemorySegment.copy(MemorySegment.ofAddress(address), ValueLayout.JAVA_DOUBLE, 0, array, offset, length);
    }

    private static final Unsafe UNSAFE = UnsafeUtils.getUnsafe();

    @Override
    public byte getByte(long address) {
        return UNSAFE.getByte(address);
    }

    @Override
    public short getShort(long address) {
        return UNSAFE.getShort(address);
    }

    @Override
    public int getInt(long address) {
        return UNSAFE.getInt(address);
    }

    @Override
    public long getLong(long address) {
        return UNSAFE.getLong(address);
    }

    @Override
    public float getFloat(long address) {
        return UNSAFE.getFloat(address);
    }

    @Override
    public double getDouble(long address) {
        return UNSAFE.getDouble(address);
    }

    @Override
    public void putByte(long address, byte value) {
        UNSAFE.putByte(address, value);
    }

    @Override
    public void putShort(long address, short value) {
        UNSAFE.putShort(address, value);
    }

    @Override
    public void putInt(long address, int value) {
        UNSAFE.putInt(address, value);
    }

    @Override
    public void putLong(long address, long value) {
        UNSAFE.putLong(address, value);
    }

    @Override
    public void putFloat(long address, float value) {
        UNSAFE.putFloat(address, value);
    }

    @Override
    public void putDouble(long address, double value) {
        UNSAFE.putDouble(address, value);
    }

    @Override
    public void copyMemory(long srcAddress, long dstAddress, long size) {
        UNSAFE.copyMemory(srcAddress, dstAddress, size);
    }

    @Override
    public void setMemory(long address, long size, byte value) {
        UNSAFE.setMemory(address, size, value);
    }

    @Override
    public long searchMemory(long address, byte value, long size) {
        return CLibraryUtils.memchr(MemorySegment.ofAddress(address), value, size);
    }

    @Override
    public long allocateMemory(long size) {
        return UNSAFE.allocateMemory(size);
    }

    @Override
    public long reallocateMemory(long address, long size) {
        return UNSAFE.reallocateMemory(address, size);
    }

    @Override
    public void freeMemory(long address) {
        UNSAFE.freeMemory(address);
    }

    @Override
    public long getZeroTerminatedStringLength(long address) {
        return NativeStringUtils.LENGTH_CALCULATOR.strlen(MemorySegment.ofAddress(address));
    }

    @Override
    public long getZeroTerminatedStringLength(long address, long maxLength) {
        return NativeStringUtils.LENGTH_CALCULATOR.strnlen_s(MemorySegment.ofAddress(address), maxLength);
    }

    @Override
    public String getZeroTerminatedString(long address, Charset charset) {
        return NativeStringUtils.getString(MemorySegment.ofAddress(address), charset);
    }

    @Override
    protected ByteBuffer createDirectByteBuffer(long address, long capacity) {
        return MemorySegment.ofAddress(address).reinterpret(capacity).asByteBuffer().order(ByteOrder.nativeOrder());
    }

    @Override
    public long getDirectBufferAddress(Buffer buffer) {
        return BufferUtils.getDirectBufferAddress(buffer);
    }

    @Override
    public void invokeCleaner(ByteBuffer buffer) throws NullPointerException, IllegalArgumentException {
        UNSAFE.invokeCleaner(buffer);
    }

    @Override
    public int nativeIntSize() {
        return MemoryLayoutUtils.NATIVE_INT_SIZE;
    }

    @Override
    public int nativeLongSize() {
        return MemoryLayoutUtils.NATIVE_LONG_SIZE;
    }

    @Override
    public int addressSize() {
        return MemoryLayoutUtils.ADDRESS_SIZE;
    }

    @Override
    public int memoryPageSize() {
        return UNSAFE.pageSize();
    }

}
