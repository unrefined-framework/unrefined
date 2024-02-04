package unrefined.runtime;

import unrefined.desktop.ForeignSupport;
import unrefined.desktop.UnsafeSupport;
import unrefined.desktop.OSInfo;
import unrefined.math.FastMath;
import unrefined.nio.Allocator;
import unrefined.nio.Pointer;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

import static unrefined.desktop.ForeignSupport.MEMORY_IO;
import static unrefined.desktop.UnsafeSupport.UNSAFE;

// TODO performance optimization
public class DesktopAllocator extends Allocator {

    @Override
    public ByteBuffer wrapBytes(long address, int capacity) {
        return MEMORY_IO.newDirectByteBuffer(address, capacity).order(ByteOrder.nativeOrder());
    }

    private static final long ARRAY_MAX = Integer.MAX_VALUE * 8L;

    private static long rangeCheck(Object array, long offset) {
        Class<?> clazz = array.getClass();
        if (clazz.isArray() && clazz.getComponentType().isPrimitive()) {
            if (offset < 0) throw new ArrayIndexOutOfBoundsException("Array index out of range: " + FastMath.unsign(offset));
            else if (offset >= ARRAY_MAX || offset / UNSAFE.arrayIndexScale(clazz) >= Integer.MAX_VALUE)
                throw new ArrayIndexOutOfBoundsException("Array index out of range: " + FastMath.unsign(offset));
            else return UNSAFE.arrayBaseOffset(clazz) + offset;
        }
        else throw new ArrayStoreException("not a primitive array");
    }

    @Override
    public boolean getBoolean(Object array, long offset) {
        return UNSAFE.getBoolean(array, rangeCheck(array, offset));
    }

    @Override
    public byte getByte(Object array, long offset) {
        return UNSAFE.getByte(array, rangeCheck(array, offset));
    }

    @Override
    public char getChar(Object array, long offset) {
        return UNSAFE.getChar(array, rangeCheck(array, offset));
    }

    @Override
    public short getShort(Object array, long offset) {
        return UNSAFE.getShort(array, rangeCheck(array, offset));
    }

    @Override
    public int getInt(Object array, long offset) {
        return UNSAFE.getInt(array, rangeCheck(array, offset));
    }

    @Override
    public long getLong(Object array, long offset) {
        return UNSAFE.getLong(array, rangeCheck(array, offset));
    }

    @Override
    public float getFloat(Object array, long offset) {
        return UNSAFE.getFloat(array, rangeCheck(array, offset));
    }

    @Override
    public double getDouble(Object array, long offset) {
        return UNSAFE.getDouble(array, rangeCheck(array, offset));
    }

    @Override
    public int compareMemory(long srcAddress, long srcOffset, long dstAddress, long dstOffset, long length) {
        long compared = ForeignSupport.memcmp(srcAddress + srcOffset, dstAddress + dstOffset, length);
        return compared == 0 ? 0 : (compared < 0 ? -1 : 1);
    }

    private static void rangeCheck(Class<?> clazz, long offset, long length) {
        if (clazz.isArray() && clazz.getComponentType().isPrimitive()) {
            if (offset < 0) throw new ArrayIndexOutOfBoundsException("Array index out of range: " + FastMath.unsign(offset));
            else if (length < 0) throw new ArrayIndexOutOfBoundsException("Array index out of range: " +
                    FastMath.unsign(length).add(BigInteger.valueOf(offset)));
            else {
                long range = offset + length;
                if (range < 0) throw new ArrayIndexOutOfBoundsException("Array index out of range: " + FastMath.unsign(range));
                else if (range >= ARRAY_MAX || range / UNSAFE.arrayIndexScale(clazz) >= Integer.MAX_VALUE)
                    throw new ArrayIndexOutOfBoundsException("Array index out of range: " + FastMath.unsign(offset));
            }
        }
        else throw new ArrayStoreException("not a primitive array");
    }

    @Override
    public int compareMemory(Object srcArray, long srcOffset, Object dstArray, long dstOffset, long length) {
        Class<?> srcType = srcArray.getClass();
        Class<?> dstType = dstArray.getClass();
        rangeCheck(srcType, srcOffset, length);
        rangeCheck(dstType, dstOffset, length);
        for (long i = 0; i < length; i ++) {
            byte oa = UNSAFE.getByte(srcArray, UNSAFE.arrayBaseOffset(srcType) + srcOffset + i);
            byte ob = UNSAFE.getByte(dstArray, UNSAFE.arrayBaseOffset(dstType) + dstOffset + i);
            if (oa != ob) {
                return FastMath.compareUnsigned(oa, ob);
            }
        }
        return 0;
    }

    @Override
    public int compareMemory(long srcAddress, long srcOffset, Object dstArray, long dstOffset, long length) {
        Class<?> dstType = dstArray.getClass();
        rangeCheck(dstType, dstOffset, length);
        for (long i = 0; i < length; i ++) {
            byte oa = UNSAFE.getByte(srcAddress + srcOffset + i);
            byte ob = UNSAFE.getByte(dstArray, UNSAFE.arrayBaseOffset(dstType) + dstOffset + i);
            if (oa != ob) {
                return FastMath.compareUnsigned(oa, ob);
            }
        }
        return 0;
    }

    @Override
    public int compareMemory(Object srcArray, long srcOffset, long dstAddress, long dstOffset, long length) {
        Class<?> srcType = srcArray.getClass();
        rangeCheck(srcType, srcOffset, length);
        for (long i = 0; i < length; i ++) {
            byte oa = UNSAFE.getByte(srcArray, UNSAFE.arrayBaseOffset(srcType) + srcOffset + i);
            byte ob = UNSAFE.getByte(dstAddress + dstOffset + i);
            if (oa != ob) {
                return FastMath.compareUnsigned(oa, ob);
            }
        }
        return 0;
    }

    @Override
    public void putByteArray(long address, byte[] array, int offset, int length) {
        MEMORY_IO.putByteArray(address, array, offset, length);
    }

    @Override
    public void getByteArray(long address, byte[] array, int offset, int length) {
        MEMORY_IO.getByteArray(address, array, offset, length);
    }

    @Override
    public void putShortArray(long address, short[] array, int offset, int length) {
        MEMORY_IO.putShortArray(address, array, offset, length);
    }

    @Override
    public void getShortArray(long address, short[] array, int offset, int length) {
        MEMORY_IO.getShortArray(address, array, offset, length);
    }

    @Override
    public void putCharArray(long address, char[] array, int offset, int length) {
        MEMORY_IO.putCharArray(address, array, offset, length);
    }

    @Override
    public void getCharArray(long address, char[] array, int offset, int length) {
        MEMORY_IO.getCharArray(address, array, offset, length);
    }

    @Override
    public void putIntArray(long address, int[] array, int offset, int length) {
        MEMORY_IO.putIntArray(address, array, offset, length);
    }

    @Override
    public void getIntArray(long address, int[] array, int offset, int length) {
        MEMORY_IO.getIntArray(address, array, offset, length);
    }

    @Override
    public void putLongArray(long address, long[] array, int offset, int length) {
        MEMORY_IO.putLongArray(address, array, offset, length);
    }

    @Override
    public void getLongArray(long address, long[] array, int offset, int length) {
        MEMORY_IO.getLongArray(address, array, offset, length);
    }

    @Override
    public void putFloatArray(long address, float[] array, int offset, int length) {
        MEMORY_IO.putFloatArray(address, array, offset, length);
    }

    @Override
    public void getFloatArray(long address, float[] array, int offset, int length) {
        MEMORY_IO.getFloatArray(address, array, offset, length);
    }

    @Override
    public void putDoubleArray(long address, double[] array, int offset, int length) {
        MEMORY_IO.putDoubleArray(address, array, offset, length);
    }

    @Override
    public void getDoubleArray(long address, double[] array, int offset, int length) {
        MEMORY_IO.getDoubleArray(address, array, offset, length);
    }

    @Override
    public byte getByte(long address) {
        return MEMORY_IO.getByte(address);
    }

    @Override
    public short getShort(long address) {
        return MEMORY_IO.getShort(address);
    }

    @Override
    public int getInt(long address) {
        return MEMORY_IO.getInt(address);
    }

    @Override
    public long getLong(long address) {
        return MEMORY_IO.getLong(address);
    }

    @Override
    public float getFloat(long address) {
        return MEMORY_IO.getFloat(address);
    }

    @Override
    public double getDouble(long address) {
        return MEMORY_IO.getDouble(address);
    }

    @Override
    public void putByte(long address, byte value) {
        MEMORY_IO.putByte(address, value);
    }

    @Override
    public void putShort(long address, short value) {
        MEMORY_IO.putShort(address, value);
    }

    @Override
    public void putInt(long address, int value) {
        MEMORY_IO.putInt(address, value);
    }

    @Override
    public void putLong(long address, long value) {
        MEMORY_IO.putLong(address, value);
    }

    @Override
    public void putFloat(long address, float value) {
        MEMORY_IO.putFloat(address, value);
    }

    @Override
    public void putDouble(long address, double value) {
        MEMORY_IO.putDouble(address, value);
    }

    @Override
    public void copyMemory(long srcAddress, long dstAddress, long size) {
        MEMORY_IO.copyMemory(srcAddress, dstAddress, size);
    }

    @Override
    public void setMemory(long address, long size, byte value) {
        MEMORY_IO.setMemory(address, size, value);
    }

    @Override
    public long searchMemory(long address, byte value, long size) {
        return MEMORY_IO.memchr(address, value, size);
    }

    @Override
    public long searchMemory(long address, byte[] value, int valueOffset, int valueLength, long size) {
        if (valueLength == 1) return MEMORY_IO.memchr(address, value[valueOffset], size);
        else {
            byte[] expected = Arrays.copyOfRange(value, valueOffset, valueOffset + valueLength);
            byte[] buffer = new byte[valueLength];
            if (size < 0) {
                for (long searched = 0; searched < Long.MAX_VALUE; searched ++) {
                    MEMORY_IO.getByteArray(address + searched, buffer, valueOffset, valueLength);
                    if (Arrays.equals(expected, buffer)) return address + searched;
                }
                for (long searched = Long.MIN_VALUE; searched < Allocator.UINT64_MAX; searched ++) {
                    MEMORY_IO.getByteArray(address + searched, buffer, valueOffset, valueLength);
                    if (Arrays.equals(expected, buffer)) return address + searched;
                }
            }
            else {
                for (long searched = 0; searched < size; searched ++) {
                    MEMORY_IO.getByteArray(address + searched, buffer, valueOffset, valueLength);
                    if (Arrays.equals(expected, buffer)) return address + searched;
                }
            }
            return 0;
        }
    }

    @Override
    public long searchMemory(long address, byte[] value, long size) {
        if (value.length == 1) return MEMORY_IO.memchr(address, value[0], size);
        else {
            byte[] buffer = new byte[value.length];
            if (size < 0) {
                for (long searched = 0; searched < Long.MAX_VALUE; searched ++) {
                    MEMORY_IO.getByteArray(address + searched, buffer, 0, value.length);
                    if (Arrays.equals(value, buffer)) return address + searched;
                }
                for (long searched = Long.MIN_VALUE; searched < Allocator.UINT64_MAX; searched ++) {
                    MEMORY_IO.getByteArray(address + searched, buffer, 0, value.length);
                    if (Arrays.equals(value, buffer)) return address + searched;
                }
            }
            else {
                for (long searched = 0; searched < size; searched ++) {
                    MEMORY_IO.getByteArray(address + searched, buffer, 0, value.length);
                    if (Arrays.equals(value, buffer)) return address + searched;
                }
            }
            return 0;
        }
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
    public long getZeroTerminatedWideCharStringLength(long address) {
        return ForeignSupport.wcslen(address);
    }

    @Override
    public long getZeroTerminatedWideCharStringLength(long address, long maxLength) {
        return (ForeignSupport.wmemchr(address, '\0', maxLength) - address) / ForeignSupport.wideCharSize();
    }

    @Override
    public long getZeroTerminatedStringLength(long address, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        byte[] terminator = "\0".getBytes(charset);
        int size = terminator.length;
        if (size == 1 && terminator[0] == '\0') return MEMORY_IO.getStringLength(address);
        else if (OSInfo.isWideCharStringCompatible(terminator)) return ForeignSupport.wcslen(address);
        else {
            long length = 0;
            byte[] buffer = new byte[size];
            while (true) {
                MEMORY_IO.getByteArray(address, buffer, 0, size);
                if (Arrays.equals(terminator, buffer)) return length;
                else length ++;
                address += size;
            }
        }
    }

    @Override
    public long getZeroTerminatedStringLength(long address, long maxLength, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        byte[] terminator = "\0".getBytes(charset);
        int size = terminator.length;
        if (size == 1 && terminator[0] == '\0') return MEMORY_IO.memchr(address, '\0', maxLength) - address;
        else if (OSInfo.isWideCharStringCompatible(terminator)) return (ForeignSupport.wmemchr(address, '\0', maxLength) - address) / ForeignSupport.wideCharSize();
        else {
            long length = 0;
            byte[] buffer = new byte[size];
            while (true) {
                MEMORY_IO.getByteArray(address, buffer, 0, size);
                if (Arrays.equals(terminator, buffer)) return length;
                else length ++;
                address += size;
            }
        }
    }

    @Override
    public String getZeroTerminatedString(long address, int maxLength, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        byte[] terminator = "\0".getBytes(charset);
        int size = terminator.length;
        if (size == 1 && terminator[0] == '\0') return new String(getZeroTerminatedByteArray(address, maxLength), charset);
        else if (OSInfo.isWideCharStringCompatible(terminator)) return new String(getZeroTerminatedWideCharByteArray(address, maxLength), charset);
        else {
            StringBuilder builder = new StringBuilder();
            byte[] buffer = new byte[size];
            while (true) {
                MEMORY_IO.getByteArray(address, buffer, 0, size);
                if (Arrays.equals(terminator, buffer)) return builder.toString();
                else builder.append(new String(buffer, charset));
                address += size;
            }
        }
    }

    @Override
    public long getDirectBufferAddress(Buffer buffer) {
        return MEMORY_IO.getDirectBufferAddress(buffer);
    }

    @Override
    public void invokeCleaner(ByteBuffer buffer) throws NullPointerException, IllegalArgumentException {
        UnsafeSupport.invokeCleaner(buffer);
    }

}
