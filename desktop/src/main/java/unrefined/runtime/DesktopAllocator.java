package unrefined.runtime;

import unrefined.desktop.ForeignSupport;
import unrefined.desktop.UnsafeSupport;
import unrefined.internal.OperatingSystem;
import unrefined.nio.Allocator;

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
        else if (OperatingSystem.isWideCharStringCompatible(terminator)) return ForeignSupport.wcslen(address);
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
        else if (OperatingSystem.isWideCharStringCompatible(terminator)) return (ForeignSupport.wmemchr(address, '\0', maxLength) - address) / ForeignSupport.wideCharSize();
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
        else if (OperatingSystem.isWideCharStringCompatible(terminator)) return new String(getZeroTerminatedWideCharByteArray(address, maxLength), charset);
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
