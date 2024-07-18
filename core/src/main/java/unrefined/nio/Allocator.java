package unrefined.nio;

import unrefined.context.Environment;
import unrefined.core.DirectPointer;
import unrefined.core.HeapPointer;
import unrefined.math.FastMath;
import unrefined.util.Arrays;
import unrefined.util.foreign.Foreign;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Provides facilities to directly access memory from Java.
 */
public abstract class Allocator {

    private static volatile Allocator INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Allocator getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.allocator", Allocator.class);
        }
        return INSTANCE;
    }

    public static final long       NULL                      = 0L;
    public static final byte       INT8_MIN                  = Byte.MIN_VALUE;
    public static final byte       INT8_MAX                  = Byte.MAX_VALUE;
    public static final byte       UINT8_MAX                 = -1;
    public static final short      UINT8_MAX_UNSIGNED        = (short) Byte.toUnsignedInt(UINT8_MAX);
    public static final short      INT16_MIN                 = Short.MIN_VALUE;
    public static final short      INT16_MAX                 = Short.MAX_VALUE;
    public static final short      UINT16_MAX                = -1;
    public static final int        UINT16_MAX_UNSIGNED       = Short.toUnsignedInt(UINT16_MAX);
    public static final int        INT32_MIN                 = Integer.MIN_VALUE;
    public static final int        INT32_MAX                 = Integer.MAX_VALUE;
    public static final int        UINT32_MAX                = 0xFFFFFF;
    public static final long       UINT32_MAX_UNSIGNED       = Integer.toUnsignedLong(UINT32_MAX);
    public static final long       INT64_MIN                 = Long.MIN_VALUE;
    public static final long       INT64_MAX                 = Long.MAX_VALUE;
    public static final long       UINT64_MAX                = 0xFFFFFFFFL;
    public static final BigInteger UINT64_MAX_UNSIGNED       = FastMath.unsign(UINT64_MAX);
    public static final long       NATIVE_INT_MAX            = Foreign.getInstance().nativeIntSize() == 8 ? Long.MAX_VALUE : Integer.MAX_VALUE;
    public static final long       NATIVE_INT_MIN            = Foreign.getInstance().nativeIntSize() == 8 ? Long.MIN_VALUE : Integer.MAX_VALUE;
    public static final long       NATIVE_UINT_MAX           = Foreign.getInstance().nativeIntSize() == 8 ? UINT64_MAX : UINT32_MAX;
    public static final BigInteger NATIVE_UINT_MAX_UNSIGNED  = Foreign.getInstance().nativeIntSize() == 8 ? UINT64_MAX_UNSIGNED : BigInteger.valueOf(UINT32_MAX_UNSIGNED);
    public static final long       NATIVE_LONG_MAX           = Foreign.getInstance().nativeLongSize() == 8 ? Long.MAX_VALUE : Integer.MAX_VALUE;
    public static final long       NATIVE_LONG_MIN           = Foreign.getInstance().nativeLongSize() == 8 ? Long.MIN_VALUE : Integer.MAX_VALUE;
    public static final long       NATIVE_ULONG_MAX          = Foreign.getInstance().nativeLongSize() == 8 ? UINT64_MAX : UINT32_MAX;
    public static final BigInteger NATIVE_ULONG_MAX_UNSIGNED = Foreign.getInstance().nativeLongSize() == 8 ? UINT64_MAX_UNSIGNED : BigInteger.valueOf(UINT32_MAX_UNSIGNED);
    public static final long       SIZE_MAX                  = Foreign.getInstance().addressSize() == 8 ? UINT64_MAX : UINT32_MAX;
    public static final BigInteger SIZE_MAX_UNSIGNED         = Foreign.getInstance().addressSize() == 8 ? UINT64_MAX_UNSIGNED :
            BigInteger.valueOf(UINT32_MAX_UNSIGNED);

    private interface NativeTypeAdapter {
        long get(long address);
        long get(ByteBuffer buffer);
        long get(ByteBuffer buffer, int index);
        long get(Object array, long offset);
        void put(long address, long value);
        void put(ByteBuffer buffer, long value);
        void put(ByteBuffer buffer, int index, long value);
        void put(Object array, long offset, long value);
    }

    private final NativeTypeAdapter NATIVE_TYPE_ADAPTER_64 = new NativeTypeAdapter() {
        @Override
        public long get(long address) {
            return getLong(address);
        }
        @Override
        public long get(ByteBuffer buffer) {
            return buffer.getLong();
        }
        @Override
        public long get(ByteBuffer buffer, int index) {
            return buffer.getLong(index);
        }
        @Override
        public long get(Object array, long offset) {
            return getLong(array, offset);
        }
        @Override
        public void put(long address, long value) {
            putLong(address, value);
        }
        @Override
        public void put(ByteBuffer buffer, long value) {
            buffer.putLong(value);
        }
        @Override
        public void put(ByteBuffer buffer, int index, long value) {
            buffer.putLong(index, value);
        }
        @Override
        public void put(Object array, long offset, long value) {
            putLong(array, offset, value);
        }
    };

    private final NativeTypeAdapter NATIVE_TYPE_ADAPTER_32 = new NativeTypeAdapter() {
        @Override
        public long get(long address) {
            return (long) getInt(address) & 0xFFFFFFFFL;
        }
        @Override
        public long get(ByteBuffer buffer) {
            return (long) buffer.getInt() & 0xFFFFFFFFL;
        }
        @Override
        public long get(ByteBuffer buffer, int index) {
            return (long) buffer.getInt(index) & 0xFFFFFFFFL;
        }
        @Override
        public long get(Object array, long offset) {
            return (long) getInt(array, offset) & 0xFFFFFFFFL;
        }
        @Override
        public void put(long address, long value) {
            putInt(address, (int) value);
        }
        @Override
        public void put(ByteBuffer buffer, long value) {
            buffer.putInt((int) value);
        }
        @Override
        public void put(ByteBuffer buffer, int index, long value) {
            buffer.putInt(index, (int) value);
        }
        @Override
        public void put(Object array, long offset, long value) {
            putInt(array, offset, (int) value);
        }
    };

    private final NativeTypeAdapter NATIVE_INT_ADAPTER = Foreign.getInstance().nativeIntSize() == 8 ? NATIVE_TYPE_ADAPTER_64 : NATIVE_TYPE_ADAPTER_32;
    private final NativeTypeAdapter NATIVE_LONG_ADAPTER = Foreign.getInstance().nativeLongSize() == 8 ? NATIVE_TYPE_ADAPTER_64 : NATIVE_TYPE_ADAPTER_32;
    private final NativeTypeAdapter ADDRESS_ADAPTER = Foreign.getInstance().addressSize() == 8 ? NATIVE_TYPE_ADAPTER_64 : NATIVE_TYPE_ADAPTER_32;

    /**
     * Reads a {@code boolean} from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A {@code boolean} containing the value.
     */
    public abstract boolean getBoolean(long address);

    /**
     * Reads a {@code boolean} from a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to get the value from.
     * @return A {@code boolean} containing the value.
     */
    public abstract boolean getBoolean(ByteBuffer buffer);

    /**
     * Reads a {@code boolean} from a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to get the value from.
     * @return A {@code boolean} containing the value.
     */
    public abstract boolean getBoolean(ByteBuffer buffer, int index);

    /**
     * Reads a {@code byte} from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A {@code byte} containing the value.
     */
    public abstract byte getByte(long address);

    /**
     * Reads an 8-bit unsigned integer from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A {@code short} containing the value.
     */
    public short getUnsignedByte(long address) {
        return (short) Byte.toUnsignedInt(getByte(address));
    }

    /**
     * Reads a {@code short} from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A {@code short} containing the value.
     */
    public abstract short getShort(long address);

    /**
     * Reads a 16-bit unsigned integer from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return An {@code int} containing the value.
     */
    public int getUnsignedShort(long address) {
        return Short.toUnsignedInt(getShort(address));
    }

    /**
     * Reads a {@code char} from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A {@code char} containing the value.
     */
    public char getChar(long address) {
        return (char) getShort(address);
    }

    /**
     * Reads an {@code int} from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return An {@code int} containing the value.
     */
    public abstract int getInt(long address);

    /**
     * Reads a 32-bit unsigned integer from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A {@code long} containing the value.
     */
    public long getUnsignedInt(long address) {
        return Integer.toUnsignedLong(getInt(address));
    }

    /**
     * Reads a {@code long} from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A {@code long} containing the value.
     */
    public abstract long getLong(long address);

    /**
     * Reads a 64-bit unsigned integer from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A {@link BigInteger} containing the value.
     */
    public BigInteger getUnsignedLong(long address) {
        return FastMath.unsign(getLong(address));
    }

    /**
     * Reads a native {@code int} (32-bit or 64-bit) from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A {@code long} containing the value.
     */
    public long getNativeInt(long address) {
        return NATIVE_INT_ADAPTER.get(address);
    }

    /**
     * Reads an unsigned native {@code int} (32-bit or 64-bit) from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A {@link BigInteger} containing the value.
     */
    public BigInteger getUnsignedNativeInt(long address) {
        return FastMath.unsign(NATIVE_INT_ADAPTER.get(address));
    }

    /**
     * Reads a native {@code int} (32-bit or 64-bit) from a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to get the value from.
     * @return A {@code long} containing the value.
     */
    public long getNativeInt(ByteBuffer buffer) {
        return NATIVE_INT_ADAPTER.get(buffer);
    }

    /**
     * Reads an unsigned native {@code int} (32-bit or 64-bit) from a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to get the value from.
     * @return A {@link BigInteger} containing the value.
     */
    public BigInteger getUnsignedNativeInt(ByteBuffer buffer) {
        return FastMath.unsign(NATIVE_INT_ADAPTER.get(buffer));
    }

    /**
     * Reads a native {@code int} (32-bit or 64-bit) from a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to get the value from.
     * @param index The index from which the {@code byte} will be read.
     * @return A {@code long} containing the value.
     */
    public long getNativeInt(ByteBuffer buffer, int index) {
        return NATIVE_INT_ADAPTER.get(buffer, index);
    }

    /**
     * Reads an unsigned native {@code int} (32-bit or 64-bit) from a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to get the value from.
     * @param index The index from which the {@code byte} will be read.
     * @return A {@link BigInteger} containing the value.
     */
    public BigInteger getUnsignedNativeInt(ByteBuffer buffer, int index) {
        return FastMath.unsign(NATIVE_INT_ADAPTER.get(buffer, index));
    }

    /**
     * Reads a native {@code long} (32-bit or 64-bit) from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A {@code long} containing the value.
     */
    public long getNativeLong(long address) {
        return NATIVE_LONG_ADAPTER.get(address);
    }

    /**
     * Reads an unsigned native {@code long} (32-bit or 64-bit) from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A {@link BigInteger} containing the value.
     */
    public BigInteger getUnsignedNativeLong(long address) {
        return FastMath.unsign(NATIVE_LONG_ADAPTER.get(address));
    }

    /**
     * Reads a native {@code long} (32-bit or 64-bit) from a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to get the value from.
     * @return A {@code long} containing the value.
     */
    public long getNativeLong(ByteBuffer buffer) {
        return NATIVE_LONG_ADAPTER.get(buffer);
    }

    /**
     * Reads an unsigned native {@code long} (32-bit or 64-bit) from a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to get the value from.
     * @return A {@link BigInteger} containing the value.
     */
    public BigInteger getUnsignedNativeLong(ByteBuffer buffer) {
        return FastMath.unsign(NATIVE_LONG_ADAPTER.get(buffer));
    }

    /**
     * Reads a native {@code long} (32-bit or 64-bit) from a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to get the value from.
     * @param index The index from which the {@code byte} will be read.
     * @return A {@code long} containing the value.
     */
    public long getNativeLong(ByteBuffer buffer, int index) {
        return NATIVE_LONG_ADAPTER.get(buffer, index);
    }

    /**
     * Reads an unsigned native {@code long} (32-bit or 64-bit) from a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to get the value from.
     * @param index The index from which the {@code byte} will be read.
     * @return A {@link BigInteger} containing the value.
     */
    public BigInteger getUnsignedNativeLong(ByteBuffer buffer, int index) {
        return FastMath.unsign(NATIVE_LONG_ADAPTER.get(buffer, index));
    }

    /**
     * Reads a {@code float} from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A float containing the value.
     */
    public abstract float getFloat(long address);

    /**
     * Reads a {@code double} from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A double containing the value.
     */
    public abstract double getDouble(long address);

    /**
     * Reads a native memory address (32-bit or 64-bit) from a native memory location.
     *
     * @param address The memory location to get the value from.
     * @return A {@code long} containing the value.
     */
    public long getAddress(long address) {
        return ADDRESS_ADAPTER.get(address);
    }

    /**
     * Reads a native memory address (32-bit or 64-bit) from a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to get the value from.
     * @return A {@code long} containing the value.
     */
    public long getAddress(ByteBuffer buffer) {
        return ADDRESS_ADAPTER.get(buffer);
    }

    /**
     * Reads a native memory address (32-bit or 64-bit) from a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to get the value from.
     * @param index The index from which the {@code byte} will be read.
     * @return A {@code long} containing the value.
     */
    public long getAddress(ByteBuffer buffer, int index) {
        return ADDRESS_ADAPTER.get(buffer, index);
    }

    /**
     * Writes a {@code boolean} to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public abstract void putBoolean(long address, boolean value);

    /**
     * Writes a {@code boolean} to a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to put the value.
     * @param value The value to write to memory.
     */
    public abstract void putBoolean(ByteBuffer buffer, boolean value);

    /**
     * Writes a {@code boolean} to a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to put the value.
     * @param index The index from which the {@code boolean} will be written.
     * @param value The value to write to memory.
     */
    public abstract void putBoolean(ByteBuffer buffer, int index, boolean value);

    /**
     * Writes a {@code byte} to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public abstract void putByte(long address, byte value);

    /**
     * Writes a {@code byte} to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public void putByte(long address, int value) {
        putByte(address, (byte) value);
    }

    /**
     * Writes an 8-bit unsigned integer to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public void putUnsignedByte(long address, short value) {
        putByte(address, (byte) value);
    }

    /**
     * Writes an 8-bit unsigned integer to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public void putUnsignedByte(long address, int value) {
        putByte(address, (byte) value);
    }

    /**
     * Writes a {@code short} to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public abstract void putShort(long address, short value);

    /**
     * Writes a {@code short} to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public void putShort(long address, int value) {
        putShort(address, (short) value);
    }

    /**
     * Writes a 16-bit unsigned integer to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public void putUnsignedShort(long address, int value) {
        putShort(address, (short) value);
    }

    /**
     * Writes a {@code char} to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public void putChar(long address, char value) {
        putShort(address, (short) value);
    }

    /**
     * Writes an {@code int} to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public abstract void putInt(long address, int value);

    /**
     * Writes a 32-bit unsigned integer to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public void putUnsignedInt(long address, long value) {
        putInt(address, (int) value);
    }

    /**
     * Writes a {@code long} to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public abstract void putLong(long address, long value);

    /**
     * Writes a 64-bit unsigned integer to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public void putUnsignedLong(long address, BigInteger value) {
        putLong(address, value.longValue());
    }

    /**
     * Writes a native {@code int} (32-bit or 64-bit) to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public void putNativeInt(long address, long value) {
        NATIVE_INT_ADAPTER.put(address, value);
    }

    /**
     * Writes an unsigned native {@code int} (32-bit or 64-bit) to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public void putUnsignedNativeInt(long address, BigInteger value) {
        NATIVE_INT_ADAPTER.put(address, value.longValue());
    }

    /**
     * Writes a native {@code int} (32-bit or 64-bit) to a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to put the value.
     * @param value The value to write to memory.
     */
    public void putNativeInt(ByteBuffer buffer, long value) {
        NATIVE_INT_ADAPTER.put(buffer, value);
    }

    /**
     * Writes an unsigned native {@code int} (32-bit or 64-bit) to a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to put the value.
     * @param value The value to write to memory.
     */
    public void putUnsignedNativeInt(ByteBuffer buffer, BigInteger value) {
        NATIVE_INT_ADAPTER.put(buffer, value.longValue());
    }

    /**
     * Writes a native {@code int} (32-bit or 64-bit) to a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to put the value.
     * @param index The index from which the {@code byte} will be written.
     * @param value The value to write to memory.
     */
    public void putNativeInt(ByteBuffer buffer, int index, long value) {
        NATIVE_INT_ADAPTER.put(buffer, index, value);
    }

    /**
     * Writes an unsigned native {@code int} (32-bit or 64-bit) to a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to put the value.
     * @param index The index from which the {@code byte} will be written.
     * @param value The value to write to memory.
     */
    public void putUnsignedNativeInt(ByteBuffer buffer, int index, BigInteger value) {
        NATIVE_INT_ADAPTER.put(buffer, index, value.longValue());
    }

    /**
     * Writes a native {@code long} (32-bit or 64-bit) to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public void putNativeLong(long address, long value) {
        NATIVE_LONG_ADAPTER.put(address, value);
    }

    /**
     * Writes an unsigned native {@code long} (32-bit or 64-bit) to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public void putUnsignedNativeLong(long address, BigInteger value) {
        NATIVE_LONG_ADAPTER.put(address, value.longValue());
    }

    /**
     * Writes a native {@code long} (32-bit or 64-bit) to a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to put the value.
     * @param value The value to write to memory.
     */
    public void putNativeLong(ByteBuffer buffer, long value) {
        NATIVE_LONG_ADAPTER.put(buffer, value);
    }

    /**
     * Writes an unsigned native {@code long} (32-bit or 64-bit) to a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to put the value.
     * @param value The value to write to memory.
     */
    public void putUnsignedNativeLong(ByteBuffer buffer, BigInteger value) {
        NATIVE_LONG_ADAPTER.put(buffer, value.longValue());
    }

    /**
     * Writes a native {@code long} (32-bit or 64-bit) to a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to put the value.
     * @param index The index from which the {@code byte} will be written.
     * @param value The value to write to memory.
     */
    public void putNativeLong(ByteBuffer buffer, int index, long value) {
        NATIVE_LONG_ADAPTER.put(buffer, index, value);
    }

    /**
     * Writes an unsigned native {@code long} (32-bit or 64-bit) to a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to put the value.
     * @param index The index from which the {@code byte} will be written.
     * @param value The value to write to memory.
     */
    public void putUnsignedNativeLong(ByteBuffer buffer, int index, BigInteger value) {
        NATIVE_LONG_ADAPTER.put(buffer, index, value.longValue());
    }

    /**
     * Writes a {@code float} to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public abstract void putFloat(long address, float value);

    /**
     * Writes a {@code double} to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public abstract void putDouble(long address, double value);

    /**
     * Writes a native memory address (32-bit or 64-bit) to a native memory location.
     *
     * @param address The memory location to put the value.
     * @param value The value to write to memory.
     */
    public void putAddress(long address, long value) {
        ADDRESS_ADAPTER.put(address, value);
    }

    /**
     * Writes a native memory address (32-bit or 64-bit) to a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to put the value.
     * @param value The value to write to memory.
     */
    public void putAddress(ByteBuffer buffer, long value) {
        ADDRESS_ADAPTER.put(buffer, value);
    }

    /**
     * Writes a native memory address (32-bit or 64-bit) to a {@link ByteBuffer}.
     *
     * @param buffer The {@code ByteBuffer} to put the value.
     * @param index The index from which the {@code byte} will be written.
     * @param value The value to write to memory.
     */
    public void putAddress(ByteBuffer buffer, int index, long value) {
        ADDRESS_ADAPTER.put(buffer, index, value);
    }

    /**
     * Copies contents of a native memory location to another native memory location.
     *
     * @param srcAddress The source memory address.
     * @param dstAddress The destination memory address.
     * @param size The number of bytes to copy.
     */
    public abstract void copyMemory(long srcAddress, long dstAddress, long size);

    /**
     * Sets a region of native memory to a specific {@code byte} value.
     *
     * @param address The address of start of the native memory.
     * @param size The number of bytes to set.
     * @param value The value to set the native memory to.
     */
    public abstract void setMemory(long address, long size, byte value);

    /**
     * Sets a region of native memory to a specific {@code byte} value.
     *
     * @param address The address of start of the native memory.
     * @param size The number of bytes to set.
     * @param value The value to set the native memory to.
     */
    public void setMemory(long address, long size, int value) {
        setMemory(address, size, (byte) value);
    }

     /**
     * Gets the address of a {@code byte} value in a native memory region.
     *
     * @param address The native memory address to start searching.
     * @param value The value to search for.
     * @param size The size of the native memory region being searched.
     * @return The address of the value, or 0 (zero) if not found.
     */
     public abstract long searchMemory(long address, byte value, long size);

     /**
     * Gets the address of a {@code byte} value in a native memory region.
     *
     * @param address The native memory address to start searching.
     * @param value The value to search for.
     * @return The address of the value, or 0 (zero) if not found.
     */
     public long searchMemory(long address, byte value) {
         return searchMemory(address, value, SIZE_MAX);
     }

     /**
     * Gets the address of a {@code byte} value in a native memory region.
     *
     * @param address The native memory address to start searching.
     * @param value The value to search for.
     * @param size The size of the native memory region being searched.
     * @return The address of the value, or 0 (zero) if not found.
     */
     public long searchMemory(long address, int value, long size) {
         return searchMemory(address, (byte) value, size);
     }

     /**
     * Gets the address of a {@code byte} value in a native memory region.
     *
     * @param address The native memory address to start searching.
     * @param value The value to search for.
     * @return The address of the value, or 0 (zero) if not found.
     */
     public long searchMemory(long address, int value) {
         return searchMemory(address, (byte) value);
     }

    /**
     * Gets the address of a {@code byte} array in a native memory region.
     *
     * @param address The native memory address to start searching.
     * @param value The value array to search for.
     * @param valueOffset The value array offset.
     * @param valueLength The value array length.
     * @param size The size of the native memory region being searched.
     * @return The address of the value, or 0 (zero) if not found.
     */
    public abstract long searchMemory(long address, byte[] value, int valueOffset, int valueLength, long size);

    /**
     * Gets the address of a {@code byte} value array in a native memory region.
     *
     * @param address The native memory address to start searching.
     * @param value The value array to search for.
     * @param size The size of the native memory region being searched.
     * @return The address of the value, or 0 (zero) if not found.
     */
    public long searchMemory(long address, byte[] value, long size) {
        return searchMemory(address, value, 0, value.length, size);
    }

    /**
     * Gets the address of a {@code byte} value array in a native memory region.
     *
     * @param address The native memory address to start searching.
     * @param value The value array to search for.
     * @param valueOffset The value array offset.
     * @param valueLength The value array length.
     * @return The address of the value, or 0 (zero) if not found.
     */
    public long searchMemory(long address, byte[] value, int valueOffset, int valueLength) {
        return searchMemory(address, value, valueOffset, valueLength, SIZE_MAX);
    }

    /**
     * Gets the address of a {@code byte} value array in a native memory region.
     *
     * @param address The native memory address to start searching.
     * @param value The value array to search for.
     * @return The address of the value, or 0 (zero) if not found.
     */
    public long searchMemory(long address, byte[] value) {
        return searchMemory(address, value, 0, value.length, SIZE_MAX);
    }

    /**
     * Writes a {@code boolean} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying from.
     * @param length The number of array elements to copy.
     */
    public void putBooleanArray(long address, boolean[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            putBoolean(address + i, array[offset + i]);
        }
    }

    /**
     * Writes a {@code boolean} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     */
    public void putBooleanArray(long address, boolean[] array) {
        putBooleanArray(address, array, 0, array.length);
    }

    /**
     * Writes a {@code byte} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying from.
     * @param length The number of array elements to copy.
     */
    public void putByteArray(long address, byte[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            putByte(address + i, array[offset + i]);
        }
    }

    /**
     * Writes a {@code byte} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     */
    public void putByteArray(long address, byte[] array) {
        putByteArray(address, array, 0, array.length);
    }

    /**
     * Reads a {@code boolean} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying to.
     * @param length The number of array elements to copy.
     */
    public void getBooleanArray(long address, boolean[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            array[offset + i] = getBoolean(address + i);
        }
    }

    /**
     * Reads a {@code boolean} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     */
    public void getBooleanArray(long address, boolean[] array) {
        getBooleanArray(address, array, 0, array.length);
    }

    /**
     * Reads a {@code byte} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying to.
     * @param length The number of array elements to copy.
     */
    public void getByteArray(long address, byte[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            array[offset + i] = getByte(address + i);
        }
    }

    /**
     * Reads a {@code byte} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     */
    public void getByteArray(long address, byte[] array) {
        getByteArray(address, array, 0, array.length);
    }

    /**
     * Writes a {@code short} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying from.
     * @param length The number of array elements to copy.
     */
    public void putShortArray(long address, short[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            putShort(address + (long) i << 1, array[offset + i]);
        }
    }

    /**
     * Writes a {@code short} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     */
    public void putShortArray(long address, short[] array) {
        putShortArray(address, array, 0, array.length);
    }

    /**
     * Reads a {@code short} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying to.
     * @param length The number of array elements to copy.
     */
    public void getShortArray(long address, short[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            array[offset + i] = getShort(address + (long) i << 1);
        }
    }

    /**
     * Reads a {@code short} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     */
    public void getShortArray(long address, short[] array) {
        getShortArray(address, array, 0, array.length);
    }

    /**
     * Writes a {@code char} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying from.
     * @param length The number of array elements to copy.
     */
    public void putCharArray(long address, char[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            putChar(address + (long) i << 1, array[offset + i]);
        }
    }

    /**
     * Writes a {@code char} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     */
    public void putCharArray(long address, char[] array) {
        putCharArray(address, array, 0, array.length);
    }

    /**
     * Reads a {@code char} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying to.
     * @param length The number of array elements to copy.
     */
    public void getCharArray(long address, char[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            array[offset + i] = getChar(address + (long) i << 1);
        }
    }

    /**
     * Reads a {@code char} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     */
    public void getCharArray(long address, char[] array) {
        getCharArray(address, array, 0, array.length);
    }

    /**
     * Writes an {@code int} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying from.
     * @param length The number of array elements to copy.
     */
    public void putIntArray(long address, int[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            putInt(address + (long) i << 2, array[offset + i]);
        }
    }

    /**
     * Writes an {@code int} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     */
    public void putIntArray(long address, int[] array) {
        putIntArray(address, array, 0, array.length);
    }

    /**
     * Reads an {@code int} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying to.
     * @param length The number of array elements to copy.
     */
    public void getIntArray(long address, int[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            array[offset + i] = getInt(address + (long) i << 2);
        }
    }

    /**
     * Reads an {@code int} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     */
    public void getIntArray(long address, int[] array) {
        getIntArray(address, array, 0, array.length);
    }

    /**
     * Writes a {@code long} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying from.
     * @param length The number of array elements to copy.
     */
    public void putLongArray(long address, long[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            putLong(address + (long) i << 3, array[offset + i]);
        }
    }

    /**
     * Writes a {@code long} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     */
    public void putLongArray(long address, long[] array) {
        putLongArray(address, array, 0, array.length);
    }

    /**
     * Reads a {@code long} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying to.
     * @param length The number of array elements to copy.
     */
    public void getLongArray(long address, long[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            array[offset + i] = getLong(address + (long) i << 3);
        }
    }

    /**
     * Reads a {@code long} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     */
    public void getLongArray(long address, long[] array) {
        getLongArray(address, array, 0, array.length);
    }

    /**
     * Writes a {@code float} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying from.
     * @param length The number of array elements to copy.
     */
    public void putFloatArray(long address, float[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            putFloat(address + (long) i << 2, array[offset + i]);
        }
    }

    /**
     * Writes a {@code float} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     */
    public void putFloatArray(long address, float[] array) {
        putFloatArray(address, array, 0, array.length);
    }

    /**
     * Reads a {@code float} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying to.
     * @param length The number of array elements to copy.
     */
    public void getFloatArray(long address, float[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            array[offset + i] = getFloat(address + (long) i << 2);
        }
    }

    /**
     * Reads a {@code float} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     */
    public void getFloatArray(long address, float[] array) {
        getFloatArray(address, array, 0, array.length);
    }

    /**
     * Writes a {@code double} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying from.
     * @param length The number of array elements to copy.
     */
    public void putDoubleArray(long address, double[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            putDouble(address + (long) i << 3, array[offset + i]);
        }
    }

    /**
     * Writes a {@code double} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     */
    public void putDoubleArray(long address, double[] array) {
        putDoubleArray(address, array, 0, array.length);
    }

    /**
     * Reads a {@code double} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying to.
     * @param length The number of array elements to copy.
     */
    public void getDoubleArray(long address, double[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        for (int i = 0; i < length; i ++) {
            array[offset + i] = getDouble(address + (long) i << 3);
        }
    }

    /**
     * Reads a {@code double} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     */
    public void getDoubleArray(long address, double[] array) {
        getDoubleArray(address, array, 0, array.length);
    }

    /**
     * Writes an {@code address} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying from.
     * @param length The number of array elements to copy.
     */
    public void putAddressArray(long address, long[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        int addressSize = Foreign.getInstance().addressSize();
        for (int i = 0; i < length; i ++) {
            putAddress(address + (long) i * addressSize, array[offset + i]);
        }
    }

    /**
     * Writes an {@code address} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     */
    public void putAddressArray(long address, long[] array) {
        putAddressArray(address, array, 0, array.length);
    }

    /**
     * Reads an {@code address} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying to.
     * @param length The number of array elements to copy.
     */
    public void getAddressArray(long address, long[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        int addressSize = Foreign.getInstance().addressSize();
        for (int i = 0; i < length; i ++) {
            array[offset + i] = getAddress(address + (long) i * addressSize);
        }
    }

    /**
     * Reads an {@code address} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     */
    public void getAddressArray(long address, long[] array) {
        getAddressArray(address, array, 0, array.length);
    }

    /**
     * Writes a native {@code int} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying from.
     * @param length The number of array elements to copy.
     */
    public void putNativeIntArray(long address, long[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        int nativeIntSize = Foreign.getInstance().nativeIntSize();
        for (int i = 0; i < length; i ++) {
            putNativeInt(address + (long) i * nativeIntSize, array[offset + i]);
        }
    }

    /**
     * Writes a native {@code int} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     */
    public void putNativeIntArray(long address, long[] array) {
        putNativeIntArray(address, array, 0, array.length);
    }

    /**
     * Reads a native {@code int} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying to.
     * @param length The number of array elements to copy.
     */
    public void getNativeIntArray(long address, long[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        int nativeIntSize = Foreign.getInstance().nativeIntSize();
        for (int i = 0; i < length; i ++) {
            array[offset + i] = getNativeInt(address + (long) i * nativeIntSize);
        }
    }

    /**
     * Reads a native {@code int} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     */
    public void getNativeIntArray(long address, long[] array) {
        getNativeIntArray(address, array, 0, array.length);
    }

    /**
     * Writes a native {@code long} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying from.
     * @param length The number of array elements to copy.
     */
    public void putNativeLongArray(long address, long[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        int nativeLongSize = Foreign.getInstance().nativeLongSize();
        for (int i = 0; i < length; i ++) {
            putNativeLong(address + (long) i * nativeLongSize, array[offset + i]);
        }
    }

    /**
     * Writes a native {@code long} array to native memory.
     *
     * @param address The native memory address to copy the array to.
     * @param array The array to copy.
     */
    public void putNativeLongArray(long address, long[] array) {
        putNativeLongArray(address, array, 0, array.length);
    }

    /**
     * Reads a native {@code long} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     * @param offset The offset within the array to start copying to.
     * @param length The number of array elements to copy.
     */
    public void getNativeLongArray(long address, long[] array, int offset, int length) {
        if (offset < 0) throw new ArrayIndexOutOfBoundsException(offset);
        else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
        int size = offset + length;
        if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        int nativeLongSize = Foreign.getInstance().nativeLongSize();
        for (int i = 0; i < length; i ++) {
            array[offset + i] = getNativeLong(address + (long) i * nativeLongSize);
        }
    }

    /**
     * Reads a native {@code long} array from native memory.
     *
     * @param address The native memory address to copy the array from.
     * @param array The array to copy.
     */
    public void getNativeLongArray(long address, long[] array) {
        getNativeLongArray(address, array, 0, array.length);
    }

    /**
     * Allocates native memory.
     *
     * @param size The number of bytes of memory to allocate.
     * @return The native address of the allocated memory.
     */
    public abstract long allocateMemory(long size);

    /**
     * Re-allocates native memory.
     *
     * @param size The number of bytes of memory to re-allocate.
     * @return The native address of the re-allocated memory.
     */
    public abstract long reallocateMemory(long address, long size);

    /**
     * Releases memory allocated via {@link #allocateMemory} back to the system.
     *
     * @param address The address of the memory to release.
     */
    public abstract void freeMemory(long address);

    /**
     * Gets the length of a native string depends on the default charset.
     *
     * @param address The native address of the string.
     * @return The length of the string, in characters.
     */
    public long getZeroTerminatedStringLength(long address) {
        return getZeroTerminatedStringLength(address, SIZE_MAX, null);
    }

    /**
     * Gets the length of a native wide char string depends on the default charset.
     *
     * @param address The native address of the string.
     * @param maxLength The limit of the memory area to scan for a zero byte.
     * @return The length of the string, in characters.
     */
    public long getZeroTerminatedWideCharStringLength(long address, long maxLength) {
        return getZeroTerminatedStringLength(address, maxLength, Foreign.getInstance().wideCharset());
    }

    /**
     * Gets the length of a native wide char string depends on the default charset.
     *
     * @param address The native address of the string.
     * @return The length of the string, in characters.
     */
    public long getZeroTerminatedWideCharStringLength(long address) {
        return getZeroTerminatedStringLength(address, SIZE_MAX, Foreign.getInstance().wideCharset());
    }

    /**
     * Gets the length of a native string depends on the default charset.
     *
     * @param address The native address of the string.
     * @param maxLength The limit of the memory area to scan for a zero byte.
     * @return The length of the string, in characters.
     */
    public long getZeroTerminatedStringLength(long address, long maxLength) {
        return getZeroTerminatedStringLength(address, maxLength, null);
    }

    /**
     * Gets the length of a native string depends on the charset.
     *
     * @param address The native address of the string.
     * @param charset The charset of the string.
     * @return The length of the string, in characters.
     */
    public long getZeroTerminatedStringLength(long address, Charset charset) {
        return getZeroTerminatedStringLength(address, SIZE_MAX, charset);
    }

    /**
     * Gets the length of a native string depends on the charset.
     *
     * @param address The native address of the string.
     * @param charset The charset of the string.
     * @param maxLength The limit of the memory area to scan for the zero terminator.
     * @return The length of the string, in characters.
     */
    public abstract long getZeroTerminatedStringLength(long address, long maxLength, Charset charset);

    /**
     * Reads a {@code byte} array from native memory depends on the default charset,
     * stopping when a '\0' {@code byte} is found.
     *
     * @param address The address to read the data from.
     * @return The {@code byte} array containing a copy of the native data. Any '\0'
     * {@code byte} is stripped from the end.
     */
    public byte[] getZeroTerminatedByteArray(long address) {
        return getZeroTerminatedByteArray(address, Arrays.ARRAY_LENGTH_MAX);
    }

    /**
     * Reads a {@code byte} array from native memory depends on the default charset,
     * stopping when a '\0' {@code byte} is found, or the maximum length is reached.
     *
     * @param address The address to read the data from.
     * @param maxLength The limit of the memory area to scan for a zero byte.
     * @return The {@code byte} array containing a copy of the native data.  Any '\0'
     * {@code byte} is stripped from the end.
     */
    public byte[] getZeroTerminatedByteArray(long address, int maxLength) {
        return getZeroTerminatedByteArray(address, maxLength, null);
    }

    /**
     * Reads a {@code byte} array from native memory depends on the wide charset,
     * stopping when a '\0' {@code byte} is found.
     *
     * @param address The address to read the data from.
     * @return The {@code byte} array containing a copy of the native data. Any '\0'
     * {@code byte} is stripped from the end.
     */
    public byte[] getZeroTerminatedWideCharByteArray(long address) {
        return getZeroTerminatedWideCharByteArray(address, Arrays.ARRAY_LENGTH_MAX);
    }

    /**
     * Reads a {@code byte} array from native memory depends on the wide charset,
     * stopping when a '\0' {@code byte} is found, or the maximum length is reached.
     *
     * @param address The address to read the data from.
     * @param maxLength The limit of the memory area to scan for a zero byte.
     * @return The {@code byte} array containing a copy of the native data.  Any '\0'
     * {@code byte} is stripped from the end.
     */
    public byte[] getZeroTerminatedWideCharByteArray(long address, int maxLength) {
        return getZeroTerminatedByteArray(address, maxLength, Foreign.getInstance().wideCharset());
    }

    /**
     * Reads a {@code byte} array from native memory depends on the specific charset,
     * stopping when a '\0' character is found.
     *
     * @param address The address to read the data from.
     * @param charset The charset to decode the data.
     * @return The {@code byte} array containing a copy of the native data.  Any '\0'
     * {@code byte} is stripped from the end.
     */
    public byte[] getZeroTerminatedByteArray(long address, Charset charset) {
        return getZeroTerminatedByteArray(address, Arrays.ARRAY_LENGTH_MAX, charset);
    }

    /**
     * Reads a {@code byte} array from native memory with the specific charset,
     * stopping when a '\0' character is found, or the maximum length is reached.
     *
     * @param address The address to read the data from.
     * @param maxLength The limit of the memory area to scan for a zero byte.
     * @param charset The charset to decode the data.
     * @return The {@code byte} array containing a copy of the native data.  Any '\0'
     * {@code byte} is stripped from the end.
     */
    public byte[] getZeroTerminatedByteArray(long address, int maxLength, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        long stringLength = getZeroTerminatedStringLength(address, maxLength, charset) * "\0".getBytes(charset).length;
        if (stringLength < 0 || stringLength > Arrays.ARRAY_LENGTH_MAX) stringLength = Arrays.ARRAY_LENGTH_MAX;
        byte[] array = new byte[(int) stringLength];
        getByteArray(address, array);
        return array;
    }

    /**
     * Reads a {@link String} from native memory depends on the default charset,
     * stopping when a '\0' {@code byte} is found.
     *
     * @param address The address to read the data from.
     * @return The {@link String} containing a copy of the native data. Any '\0'
     * {@code byte} is stripped from the end.
     */
    public String getZeroTerminatedString(long address) {
        return getZeroTerminatedString(address, Arrays.ARRAY_LENGTH_MAX);
    }

    /**
     * Reads a {@link String} from native memory depends on the default charset,
     * stopping when a '\0' {@code byte} is found, or the maximum length is reached.
     *
     * @param address The address to read the data from.
     * @param maxLength The limit of the memory area to scan for a zero byte.
     * @return The {@link String} containing a copy of the native data.  Any '\0'
     * {@code byte} is stripped from the end.
     */
    public String getZeroTerminatedString(long address, int maxLength) {
        return getZeroTerminatedString(address, maxLength, null);
    }

    /**
     * Reads a {@link String} from native memory depends on the wide charset,
     * stopping when a '\0' {@code byte} is found.
     *
     * @param address The address to read the data from.
     * @return The {@link String} containing a copy of the native data. Any '\0'
     * {@code byte} is stripped from the end.
     */
    public String getZeroTerminatedWideCharString(long address) {
        return getZeroTerminatedWideCharString(address, Arrays.ARRAY_LENGTH_MAX);
    }

    /**
     * Reads a {@link String} from native memory depends on the wide charset,
     * stopping when a '\0' {@code byte} is found, or the maximum length is reached.
     *
     * @param address The address to read the data from.
     * @param maxLength The limit of the memory area to scan for a zero byte.
     * @return The {@link String} containing a copy of the native data.  Any '\0'
     * {@code byte} is stripped from the end.
     */
    public String getZeroTerminatedWideCharString(long address, int maxLength) {
        return getZeroTerminatedString(address, maxLength, Foreign.getInstance().wideCharset());
    }

    /**
     * Reads a {@link String} from native memory depends on the specific charset,
     * stopping when a '\0' character is found.
     *
     * @param address The address to read the data from.
     * @param charset The charset to decode the data.
     * @return The {@link String} containing a copy of the native data.  Any '\0'
     * {@code byte} is stripped from the end.
     */
    public String getZeroTerminatedString(long address, Charset charset) {
        return getZeroTerminatedString(address, Arrays.ARRAY_LENGTH_MAX, charset);
    }

    /**
     * Reads a {@link String} from native memory with the specific charset,
     * stopping when a '\0' character is found, or the maximum length is reached.
     *
     * @param address The address to read the data from.
     * @param maxLength The limit of the memory area to scan for a zero byte.
     * @param charset The charset to decode the data.
     * @return The {@link String} containing a copy of the native data.  Any '\0'
     * {@code byte} is stripped from the end.
     */
    public String getZeroTerminatedString(long address, int maxLength, Charset charset) {
        return new String(getZeroTerminatedByteArray(address, maxLength, charset), charset == null ? Charset.defaultCharset() : charset);
    }

    /**
     * Copies a {@code byte} array to native memory and appends a '\0' terminating character
     * depends on the default charset.
     * <b>Note</b> A total of length + (character size) bytes is written to native memory.
     *
     * @param address The address to copy to.
     * @param array The {@code byte} array to copy to native memory
     * @param offset The offset within the {@code byte} array to begin copying from
     * @param length The number of bytes to copy to native memory
     */
    public void putZeroTerminatedByteArray(long address, byte[] array, int offset, int length) {
        putZeroTerminatedByteArray(address, array, offset, length, null);
    }

    /**
     * Copies a {@code byte} array to native memory and appends a '\0' terminating character
     * depends on the default charset.
     * <b>Note</b> A total of length + (character size) bytes is written to native memory.
     *
     * @param address The address to copy to.
     * @param array The {@code byte} array to copy to native memory
     */
    public void putZeroTerminatedByteArray(long address, byte[] array) {
        putZeroTerminatedByteArray(address, array, 0, array.length);
    }

    /**
     * Copies a {@code byte} array to native memory and appends a '\0' terminating character
     * depends on the wide charset.
     * <b>Note</b> A total of length + (character size) bytes is written to native memory.
     *
     * @param address The address to copy to.
     * @param array The {@code byte} array to copy to native memory
     * @param offset The offset within the {@code byte} array to begin copying from
     * @param length The number of bytes to copy to native memory
     */
    public void putZeroTerminatedWideCharByteArray(long address, byte[] array, int offset, int length) {
        putZeroTerminatedByteArray(address, array, offset, length, Foreign.getInstance().wideCharset());
    }

    /**
     * Copies a {@code byte} array to native memory and appends a '\0' terminating character
     * depends on the wide charset.
     * <b>Note</b> A total of length + (character size) bytes is written to native memory.
     *
     * @param address The address to copy to.
     * @param array The {@code byte} array to copy to native memory
     */
    public void putZeroTerminatedWideCharByteArray(long address, byte[] array) {
        putZeroTerminatedByteArray(address, array, Foreign.getInstance().wideCharset());
    }

    /**
     * Copies a {@code byte} array to native memory and appends a '\0' terminating character
     * depends on the specific charset.
     * <b>Note</b> A total of length + (character size) bytes is written to native memory.
     *
     * @param address The address to copy to.
     * @param array The {@code byte} array to copy to native memory
     * @param offset The offset within the {@code byte} array to begin copying from
     * @param length The number of bytes to copy to native memory
     */
    public void putZeroTerminatedByteArray(long address, byte[] array, int offset, int length, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        byte[] terminator = "\0".getBytes(charset);
        putByteArray(address + length, terminator);
        putByteArray(address, array, offset, length);
    }

    /**
     * Copies a {@code byte} array to native memory and appends a '\0' terminating character
     * depends on the specific charset.
     * <b>Note</b> A total of length + (character size) bytes is written to native memory.
     *
     * @param address The address to copy to.
     * @param array The {@code byte} array to copy to native memory
     */
    public void putZeroTerminatedByteArray(long address, byte[] array, Charset charset) {
        putZeroTerminatedByteArray(address, array, 0, array.length, charset);
    }

    /**
     * Copies a {@link String} to native memory and appends a '\0' terminating character
     * depends on the default charset.
     * <b>Note</b> A total of length + (character size) bytes is written to native memory.
     *
     * @param address The address to copy to.
     * @param string The {@link String} to copy to native memory.
     */
    public void putZeroTerminatedString(long address, String string) {
        putZeroTerminatedString(address, string, Charset.defaultCharset());
    }

    /**
     * Copies a {@link String} to native memory and appends a '\0' terminating character
     * depends on the wide charset.
     * <b>Note</b> A total of length + (character size) bytes is written to native memory.
     *
     * @param address The address to copy to.
     * @param string The {@link String} to copy to native memory.
     */
    public void putZeroTerminatedWideCharString(long address, String string) {
        putZeroTerminatedString(address, string, Foreign.getInstance().wideCharset());
    }

    /**
     * Copies a {@link String} to native memory and appends a '\0' terminating character
     * depends on the specific charset.
     * <b>Note</b> A total of length + (character size) bytes is written to native memory.
     *
     * @param address The address to copy to.
     * @param string The {@link String} to copy to native memory.
     * @param charset The charset to encode the string.
     */
    public void putZeroTerminatedString(long address, String string, Charset charset) {
        byte[] bytes = string.getBytes(charset);
        byte[] terminator = "\0".getBytes(charset);
        putByteArray(address + bytes.length, terminator);
        putByteArray(address, bytes);
    }

    /**
     * Finds the location of a {@code byte} value in a native memory region.
     *
     * @param address The native memory address to start searching from.
     * @param value The value to search for.
     * @return The offset from the memory address of the value, if found, else -1 (minus one).
     */
    public long indexOf(long address, byte value) {
        long location = searchMemory(address, value);
        return location != 0 ? location - address : -1;
    }

    /**
     * Finds the location of a {@code byte} value in a native memory region.
     *
     * @param address The native memory address to start searching from.
     * @param value The value to search for.
     * @return The offset from the memory address of the value, if found, else -1 (minus one).
     */
    public long indexOf(long address, int value) {
        return indexOf(address, (byte) value);
    }

    /**
     * Finds the location of a {@code byte} value in a native memory region.
     *
     * @param address The native memory address to start searching from.
     * @param value The value to search for.
     * @param maxLength The maximum number of bytes to search.
     * @return The offset from the memory address of the value, if found, else -1 (minus one).
     */
    public long indexOf(long address, byte value, long maxLength) {
        long location = searchMemory(address, value, maxLength);
        return location != 0 ? location - address : -1;
    }

    /**
     * Finds the location of a {@code byte} value in a native memory region.
     *
     * @param address The native memory address to start searching from.
     * @param value The value to search for.
     * @param maxLength The maximum number of bytes to search.
     * @return The offset from the memory address of the value, if found, else -1 (minus one).
     */
    public long indexOf(long address, int value, long maxLength) {
        return indexOf(address, (byte) value, maxLength);
    }

    /**
     * Finds the location of a {@code byte} array in a native memory region.
     *
     * @param address The native memory address to start searching from.
     * @param value The value to search for.
     * @return The offset from the memory address of the value, if found, else -1 (minus one).
     */
    public long indexOf(long address, byte[] value) {
        long location = searchMemory(address, value);
        return location != 0 ? location - address : -1;
    }

    /**
     * Finds the location of a {@code byte} array in a native memory region.
     *
     * @param address The native memory address to start searching from.
     * @param value The value to search for.
     * @return The offset from the memory address of the value, if found, else -1 (minus one).
     */
    public long indexOf(long address, byte[] value, int valueOffset, int valueLength) {
        long location = searchMemory(address, value, valueOffset, valueLength);
        return location != 0 ? location - address : -1;
    }

    /**
     * Finds the location of a {@code byte} array in a native memory region.
     *
     * @param address The native memory address to start searching from.
     * @param value The value to search for.
     * @param maxLength The maximum number of bytes to search.
     * @return The offset from the memory address of the value, if found, else -1 (minus one).
     */
    public long indexOf(long address, byte[] value, long maxLength) {
        long location = searchMemory(address, value, maxLength);
        return location != 0 ? location - address : -1;
    }

    /**
     * Finds the location of a {@code byte} array in a native memory region.
     *
     * @param address The native memory address to start searching from.
     * @param value The value to search for.
     * @param maxLength The maximum number of bytes to search.
     * @return The offset from the memory address of the value, if found, else -1 (minus one).
     */
    public long indexOf(long address, byte[] value, int valueOffset, int valueLength, long maxLength) {
        long location = searchMemory(address, value, valueOffset, valueLength, maxLength);
        return location != 0 ? location - address : -1;
    }

    /**
     * Gets the native memory address of a direct {@link Buffer}
     *
     * @param buffer A direct {@code Buffer} to get the address of.
     * @return The native memory address of the buffer contents, or NULL if not a direct buffer.
     */
    public abstract long getDirectBufferAddress(Buffer buffer);

    /**
     * Invokes a direct {@link ByteBuffer}'s cleaner to free its off-heap memory, if any.
     *
     * @param buffer the direct {@code Buffer} to invoke cleaner
     * @throws NullPointerException if {@code buffer} is null
     * @throws IllegalArgumentException if {@code buffer} is non-direct,
     * or is a {@code slice}, or is a {@code duplicate}
     */
    public abstract void invokeCleaner(ByteBuffer buffer) throws NullPointerException, IllegalArgumentException;

    public ByteBuffer allocateBytes(int capacity, boolean direct) {
        return (direct ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity));
    }

    public ByteBuffer wrapBytes(byte[] array, int offset, int length) {
        return ByteBuffer.wrap(array, offset, length);
    }

    public ByteBuffer wrapBytes(byte[] array) {
        return ByteBuffer.wrap(array);
    }

    public abstract ByteBuffer wrapBytes(long address, int capacity);

    public ByteBuffer reallocateBytes(ByteBuffer buffer, int capacity) {
        if (buffer.isDirect()) return wrapBytes(getDirectBufferAddress(buffer.position(capacity)), capacity);
        else return Buffers.clear(ByteBuffer.allocate(capacity).order(buffer.order()).put(Buffers.clear(buffer)));
    }

    public MappedByteBuffer mapBytes(FileChannel channel, FileChannel.MapMode mode, long position, long size) throws IOException {
        return channel.map(mode, position, size);
    }

    public CharBuffer allocateChars(int capacity) {
        return CharBuffer.allocate(capacity);
    }

    public CharBuffer wrapChars(char[] array, int offset, int length) {
        return CharBuffer.wrap(array, offset, length);
    }

    public CharBuffer wrapChars(char[] array) {
        return CharBuffer.wrap(array);
    }

    public CharBuffer wrapChars(CharSequence csq, int start, int end) {
        return CharBuffer.wrap(csq, start, end);
    }

    public CharBuffer wrapChars(CharSequence csq) {
        return CharBuffer.wrap(csq);
    }

    public CharBuffer reallocateChars(CharBuffer buffer, int capacity) {
        if (buffer.isDirect()) return wrapBytes(getDirectBufferAddress(buffer.position(capacity)), capacity << 1).asCharBuffer();
        else return Buffers.clear(CharBuffer.allocate(capacity).put(Buffers.clear(buffer)));
    }

    public ShortBuffer allocateShorts(int capacity) {
        return ShortBuffer.allocate(capacity);
    }

    public ShortBuffer wrapShorts(short[] array, int offset, int length) {
        return ShortBuffer.wrap(array, offset, length);
    }

    public ShortBuffer wrapShorts(short[] array) {
        return ShortBuffer.wrap(array);
    }

    public ShortBuffer reallocateShorts(ShortBuffer buffer, int capacity) {
        if (buffer.isDirect()) return wrapBytes(getDirectBufferAddress(buffer.position(capacity)), capacity << 1).asShortBuffer();
        else return Buffers.clear(ShortBuffer.allocate(capacity).put(Buffers.clear(buffer)));
    }

    public IntBuffer allocateInts(int capacity) {
        return IntBuffer.allocate(capacity);
    }

    public IntBuffer wrapInts(int[] array, int offset, int length) {
        return IntBuffer.wrap(array, offset, length);
    }

    public IntBuffer wrapInts(int[] array) {
        return IntBuffer.wrap(array);
    }

    public IntBuffer reallocateInts(IntBuffer buffer, int capacity) {
        if (buffer.isDirect()) return wrapBytes(getDirectBufferAddress(buffer.position(capacity)), capacity << 2).asIntBuffer();
        else return Buffers.clear(IntBuffer.allocate(capacity).put(Buffers.clear(buffer)));
    }

    public LongBuffer allocateLongs(int capacity) {
        return LongBuffer.allocate(capacity);
    }

    public LongBuffer wrapLongs(long[] array, int offset, int length) {
        return LongBuffer.wrap(array, offset, length);
    }

    public LongBuffer wrapLongs(long[] array) {
        return LongBuffer.wrap(array);
    }

    public LongBuffer reallocateLongs(LongBuffer buffer, int capacity) {
        if (buffer.isDirect()) return wrapBytes(getDirectBufferAddress(buffer.position(capacity)), capacity << 3).asLongBuffer();
        else return Buffers.clear(LongBuffer.allocate(capacity).put(Buffers.clear(buffer)));
    }

    public FloatBuffer allocateFloats(int capacity) {
        return FloatBuffer.allocate(capacity);
    }

    public FloatBuffer wrapFloats(float[] array, int offset, int length) {
        return FloatBuffer.wrap(array, offset, length);
    }

    public FloatBuffer wrapFloats(float[] array) {
        return FloatBuffer.wrap(array);
    }

    public FloatBuffer reallocateFloats(FloatBuffer buffer, int capacity) {
        if (buffer.isDirect()) return wrapBytes(getDirectBufferAddress(buffer.position(capacity)), capacity << 2).asFloatBuffer();
        else return Buffers.clear(FloatBuffer.allocate(capacity).put(Buffers.clear(buffer)));
    }

    public DoubleBuffer allocateDoubles(int capacity) {
        return DoubleBuffer.allocate(capacity);
    }

    public DoubleBuffer wrapDoubles(double[] array, int offset, int length) {
        return DoubleBuffer.wrap(array, offset, length);
    }

    public DoubleBuffer wrapDoubles(double[] array) {
        return DoubleBuffer.wrap(array);
    }

    public DoubleBuffer reallocateDoubles(DoubleBuffer buffer, int capacity) {
        if (buffer.isDirect()) return wrapBytes(getDirectBufferAddress(buffer.position(capacity)), capacity << 3).asDoubleBuffer();
        else return Buffers.clear(DoubleBuffer.allocate(capacity).put(Buffers.clear(buffer)));
    }

    public boolean isValid(Buffer buffer) {
        return !buffer.isDirect() || getDirectBufferAddress(buffer) != 0L;
    }

    public Pointer wrapPointer(long address) {
        return new DirectPointer(address);
    }

    public Pointer wrapPointer(long address, long size) {
        return new DirectPointer(address, size, false);
    }

    public Pointer wrapPointer(ByteBuffer buffer) {
        return buffer.isDirect() ? wrapPointer(getAddress(buffer), buffer.capacity()) : wrapPointer(buffer.array(), buffer.arrayOffset(), buffer.capacity());
    }

    public Pointer wrapPointer(byte[] array, int offset, int length) {
        return new HeapPointer(array, offset, length);
    }

    public Pointer wrapPointer(byte[] array) {
        return wrapPointer(array, 0, array.length);
    }

    private static DirectPointer allocateDirectPointer(Allocator allocator, long size) throws IOException {
        if (size == 0) return (DirectPointer) Pointer.NULL;
        long address = allocator.allocateMemory(size);
        if (address == 0) throw new IOException("Unable to allocate native memory, size: " + FastMath.unsign(size));
        else return new DirectPointer(address, size, true);
    }

    public Pointer allocatePointer(long size, boolean direct) throws IOException {
        return direct ? allocateDirectPointer(this, size) : new HeapPointer(size);
    }

    public Pointer reallocatePointer(Pointer pointer, long size) throws IOException {
        return pointer.reallocate(size);
    }

    /**
     * Frees a direct {@link Pointer}'s off-heap memory, if any.
     *
     * @param pointer the direct {@code Pointer} to free memory
     * @throws NullPointerException if {@code pointer} is null
     * @throws IllegalArgumentException if {@code pointer} is non-direct,
     * or is a {@code slice}, or is a {@code duplicate}
     */
    public void freePointer(Pointer pointer) throws IOException {
        pointer.close();
    }

    public abstract void copyMemory(Object srcArray, long srcOffset, Object dstArray, long dstOffset, long bytes);
    public abstract void setMemory(Object array, long offset, long bytes, byte value);
    public void setMemory(Object array, long offset, long bytes, int value) {
        setMemory(array, offset, bytes, (byte) value);
    }

    public abstract boolean getBoolean(Object array, long offset);
    public abstract byte getByte(Object array, long offset);
    public abstract char getChar(Object array, long offset);
    public abstract short getShort(Object array, long offset);
    public abstract int getInt(Object array, long offset);
    public abstract long getLong(Object array, long offset);
    public abstract float getFloat(Object array, long offset);
    public abstract double getDouble(Object array, long offset);
    public void getBooleanArray(Object srcArray, long srcOffset, boolean[] dstArray, int dstOffset, int length) {
        for (int i = 0; i < length; i ++) {
            dstArray[dstOffset + i] = getByte(srcArray, srcOffset + i) != 0;
        }
    }
    public void getByteArray(Object srcArray, long srcOffset, byte[] dstArray, int dstOffset, int length) {
        for (int i = 0; i < length; i ++) {
            dstArray[dstOffset + i] = getByte(srcArray, srcOffset + i);
        }
    }
    public void getCharArray(Object srcArray, long srcOffset, char[] dstArray, int dstOffset, int length) {
        for (int i = 0; i < length; i ++) {
            dstArray[dstOffset + i] = getChar(srcArray, srcOffset + (long) i << 1);
        }
    }
    public void getShortArray(Object srcArray, long srcOffset, short[] dstArray, int dstOffset, int length) {
        for (int i = 0; i < length; i ++) {
            dstArray[dstOffset + i] = getShort(srcArray, srcOffset + (long) i << 1);
        }
    }
    public void getIntArray(Object srcArray, long srcOffset, int[] dstArray, int dstOffset, int length) {
        for (int i = 0; i < length; i ++) {
            dstArray[dstOffset + i] = getInt(srcArray, srcOffset + (long) i << 2);
        }
    }
    public void getLongArray(Object srcArray, long srcOffset, long[] dstArray, int dstOffset, int length) {
        for (int i = 0; i < length; i ++) {
            dstArray[dstOffset + i] = getLong(srcArray, srcOffset + (long) i << 4);
        }
    }
    public void getFloatArray(Object srcArray, long srcOffset, float[] dstArray, int dstOffset, int length) {
        for (int i = 0; i < length; i ++) {
            dstArray[dstOffset + i] = getFloat(srcArray, srcOffset + (long) i << 2);
        }
    }
    public void getDoubleArray(Object srcArray, long srcOffset, double[] dstArray, int dstOffset, int length) {
        for (int i = 0; i < length; i ++) {
            dstArray[dstOffset + i] = getDouble(srcArray, srcOffset + (long) i << 4);
        }
    }
    public void getNativeIntArray(Object srcArray, long srcOffset, long[] dstArray, int dstOffset, int length) {
        int nativeIntSize = Foreign.getInstance().nativeIntSize();
        for (int i = 0; i < length; i ++) {
            dstArray[dstOffset + i] = getNativeInt(srcArray, srcOffset + (long) i * nativeIntSize);
        }
    }
    public void getNativeLongArray(Object srcArray, long srcOffset, long[] dstArray, int dstOffset, int length) {
        int nativeLongSize = Foreign.getInstance().nativeLongSize();
        for (int i = 0; i < length; i ++) {
            dstArray[dstOffset + i] = getNativeLong(srcArray, srcOffset + (long) i * nativeLongSize);
        }
    }
    public void getAddressArray(Object srcArray, long srcOffset, long[] dstArray, int dstOffset, int length) {
        int addressSize = Foreign.getInstance().addressSize();
        for (int i = 0; i < length; i ++) {
            dstArray[dstOffset + i] = getAddress(srcArray, srcOffset + (long) i * addressSize);
        }
    }
    public long getNativeInt(Object array, long offset) {
        return NATIVE_INT_ADAPTER.get(array, offset);
    }
    public long getNativeLong(Object array, long offset) {
        return NATIVE_LONG_ADAPTER.get(array, offset);
    }
    public long getAddress(Object array, long offset) {
        return ADDRESS_ADAPTER.get(array, offset);
    }
    public short getUnsignedByte(Object array, long offset) {
        return (short) Byte.toUnsignedInt(getByte(array, offset));
    }
    public int getUnsignedShort(Object array, long offset) {
        return Short.toUnsignedInt(getShort(array, offset));
    }
    public long getUnsignedInt(Object array, long offset) {
        return Integer.toUnsignedLong(getInt(array, offset));
    }
    public BigInteger getUnsignedLong(Object array, long offset) {
        return FastMath.unsign(getLong(array, offset));
    }
    public BigInteger getUnsignedNativeInt(Object array, long offset) {
        return FastMath.unsign(NATIVE_INT_ADAPTER.get(array, offset));
    }
    public BigInteger getUnsignedNativeLong(Object array, long offset) {
        return FastMath.unsign(NATIVE_LONG_ADAPTER.get(array, offset));
    }

    public abstract void putBoolean(Object array, long offset, boolean value);
    public abstract void putByte(Object array, long offset, byte value);
    public void putByte(Object array, long offset, short value) {
        putByte(array, offset, (byte) value);
    }
    public void putByte(Object array, long offset, int value) {
        putByte(array, offset, (byte) value);
    }
    public abstract void putChar(Object array, long offset, char value);
    public abstract void putShort(Object array, long offset, short value);
    public void putShort(Object array, long offset, int value) {
        putShort(array, offset, (short) value);
    }
    public abstract void putInt(Object array, long offset, int value);
    public abstract void putLong(Object array, long offset, long value);
    public abstract void putFloat(Object array, long offset, float value);
    public abstract void putDouble(Object array, long offset, double value);
    public void putBooleanArray(Object dstArray, long dstOffset, boolean[] srcArray, int srcOffset, int length) {
        for (int i = 0; i < length; i ++) {
            putByte(dstArray, dstOffset + i, srcArray[srcOffset + i] ? 1 : 0);
        }
    }
    public void putByteArray(Object dstArray, long dstOffset, byte[] srcArray, int srcOffset, int length) {
        for (int i = 0; i < length; i ++) {
            putByte(dstArray, dstOffset + i, srcArray[srcOffset + i]);
        }
    }
    public void putCharArray(Object dstArray, long dstOffset, char[] srcArray, int srcOffset, int length) {
        for (int i = 0; i < length; i ++) {
            putChar(dstArray, dstOffset + (long) i << 1, srcArray[srcOffset + i]);
        }
    }
    public void putShortArray(Object dstArray, long dstOffset, short[] srcArray, int srcOffset, int length) {
        for (int i = 0; i < length; i ++) {
            putShort(dstArray, dstOffset + (long) i << 1, srcArray[srcOffset + i]);
        }
    }
    public void putIntArray(Object dstArray, long dstOffset, int[] srcArray, int srcOffset, int length) {
        for (int i = 0; i < length; i ++) {
            putInt(dstArray, dstOffset + (long) i << 2, srcArray[srcOffset + i]);
        }
    }
    public void putLongArray(Object dstArray, long dstOffset, long[] srcArray, int srcOffset, int length) {
        for (int i = 0; i < length; i ++) {
            putLong(dstArray, dstOffset + (long) i << 3, srcArray[srcOffset + i]);
        }
    }
    public void putFloatArray(Object dstArray, long dstOffset, float[] srcArray, int srcOffset, int length) {
        for (int i = 0; i < length; i ++) {
            putFloat(dstArray, dstOffset + (long) i << 2, srcArray[srcOffset + i]);
        }
    }
    public void putDoubleArray(Object dstArray, long dstOffset, double[] srcArray, int srcOffset, int length) {
        for (int i = 0; i < length; i ++) {
            putDouble(dstArray, dstOffset + (long) i << 3, srcArray[srcOffset + i]);
        }
    }
    public void putNativeIntArray(Object dstArray, long dstOffset, long[] srcArray, int srcOffset, int length) {
        int nativeIntSize = Foreign.getInstance().nativeIntSize();
        for (int i = 0; i < length; i ++) {
            putNativeInt(dstArray, dstOffset + (long) i * nativeIntSize, srcArray[srcOffset + i]);
        }
    }
    public void putNativeLongArray(Object dstArray, long dstOffset, long[] srcArray, int srcOffset, int length) {
        int nativeLongSize = Foreign.getInstance().nativeLongSize();
        for (int i = 0; i < length; i ++) {
            putNativeLong(dstArray, dstOffset + (long) i * nativeLongSize, srcArray[srcOffset + i]);
        }
    }
    public void putAddressArray(Object dstArray, long dstOffset, long[] srcArray, int srcOffset, int length) {
        int addressSize = Foreign.getInstance().addressSize();
        for (int i = 0; i < length; i ++) {
            putAddress(dstArray, dstOffset + (long) i * addressSize, srcArray[srcOffset + i]);
        }
    }
    public void putNativeInt(Object array, long offset, long value) {
        NATIVE_INT_ADAPTER.put(array, offset, value);
    }
    public void putNativeLong(Object array, long offset, long value) {
        NATIVE_LONG_ADAPTER.put(array, offset, value);
    }
    public void putAddress(Object array, long offset, long value) {
        ADDRESS_ADAPTER.put(array, offset, value);
    }
    public void putUnsignedByte(Object array, long offset, short value) {
        putByte(array, offset, (byte) value);
    }
    public void putUnsignedByte(Object array, long offset, int value) {
        putByte(array, offset, (byte) value);
    }
    public void putUnsignedShort(Object array, long offset, int value) {
        putShort(array, offset, (short) value);
    }
    public void putUnsignedInt(Object array, long offset, long value) {
        putInt(array, offset, (int) value);
    }
    public void putUnsignedLong(Object array, long offset, BigInteger value) {
        putLong(array, offset, value.longValue());
    }
    public void putUnsignedNativeInt(Object array, long offset, BigInteger value) {
        NATIVE_INT_ADAPTER.put(array, offset, value.longValue());
    }
    public void putUnsignedNativeLong(Object array, long offset, BigInteger value) {
        NATIVE_LONG_ADAPTER.put(array, offset, value.longValue());
    }

    public abstract int compareMemory(long srcAddress, long srcOffset, long dstAddress, long dstOffset, long length);
    public abstract int compareMemory(Object srcArray, long srcOffset, Object dstArray, long dstOffset, long length);
    public abstract int compareMemory(long srcAddress, long srcOffset, Object dstArray, long dstOffset, long length);
    public abstract int compareMemory(Object srcArray, long srcOffset, long dstAddress, long dstOffset, long length);

    public int compareMemory(long srcAddress, long dstAddress, long length) {
        return compareMemory(srcAddress, 0, dstAddress, 0, length);
    }

    public int compareMemory(Object srcArray, long srcOffset, long dstAddress, long length) {
        return compareMemory(srcArray, srcOffset, dstAddress, 0, length);
    }

    public int compareMemory(long srcAddress, Object dstArray, long dstOffset, long length) {
        return compareMemory(srcAddress, 0, dstArray, dstOffset, length);
    }

}
