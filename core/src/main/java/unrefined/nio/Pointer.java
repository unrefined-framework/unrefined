package unrefined.nio;

import unrefined.math.FastMath;
import unrefined.util.Duplicatable;
import unrefined.util.foreign.Foreign;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * A wrapper class which represents a block of memory.
 * 
 * <p>This class provides operations on a block of memory.
 * {@code Pointer} instances will either represent direct
 * memory (that is, a fixed address in the process address space,
 * directly accessible by native code), or backed by at least one
 * Java <code>byte</code> array.
 * See {@link #isDirect()}, {@link #hasArrays()} for more information.
 */
public abstract class Pointer implements Comparable<Pointer>, Closeable, Duplicatable {

    public static final Pointer NULL = Pointer.wrap(0);

    /**
     * Wraps a Java {@code byte} array in a {@link Pointer} instance.
     *
     * @param array the {@code array} to wrap in a {@code Pointer} instance.
     *
     * @return a {@code Pointer} instance.
     */
    public static Pointer wrap(byte[] array) {
        return Allocator.getInstance().wrapPointer(array);
    }

    /**
     * Wraps a Java {@code byte} array in a {@link Pointer} instance.
     *
     * @param array the {@code array} to wrap in a {@code Pointer} instance.
     * @param offset the offset of the array.
     * @param length the region length of the array.
     *
     * @return a {@code Pointer} instance.
     */
    public static Pointer wrap(byte[] array, int offset, int length) {
        return Allocator.getInstance().wrapPointer(array, offset, length);
    }

    /**
     * Wraps a native address in a {@link Pointer} instance.
     *
     * @param address the {@code address} to wrap in a {@code Pointer} instance.
     *
     * @return a {@code Pointer} instance.
     */
    public static Pointer wrap(long address) {
        return Allocator.getInstance().wrapPointer(address);
    }

    /**
     * Wraps a native address in a {@link Pointer} instance.
     *
     * @param address the {@code address} to wrap in a Pointer instance.
     * @param size the size of the native memory region.
     *
     * @return a {@code Pointer} instance.
     */
    public static Pointer wrap(long address, long size) {
        return Allocator.getInstance().wrapPointer(address, size);
    }

    /**
     * Wraps an existing ByteBuffer in a {@link Pointer} implementation so it can
     * be used as a parameter to native functions.
     *
     * <p>Wrapping a ByteBuffer is only neccessary if the native function parameter
     * was declared as a {@code Pointer}.  The if the method will always be used
     * with {@code ByteBuffer} parameters, then the parameter type can just be declared
     * as {@code ByteBuffer} and the conversion will be performed automatically.
     *
     * @param buffer the {@code ByteBuffer} to wrap.
     *
     * @return a {@code Pointer} instance that will proxy all accesses to the ByteBuffer contents.
     */
    public static Pointer wrap(ByteBuffer buffer) {
        return Allocator.getInstance().wrapPointer(buffer);
    }

    public static Pointer allocate(long size) throws IOException {
        return Allocator.getInstance().allocatePointer(size, false);
    }

    public static Pointer allocate(String string) throws IOException {
        return allocate(string, null);
    }

    public static Pointer allocate(String string, Charset charset) throws IOException {
        if (charset == null) charset = Charset.defaultCharset();
        byte[] bytes = string.getBytes(charset);
        byte[] terminator = "\0".getBytes(charset);
        Pointer pointer = allocate(bytes.length + terminator.length);
        pointer.putByteArray(bytes.length, terminator);
        pointer.putByteArray(0, bytes);
        return pointer;
    }

    public static Pointer allocateDirect(long size) throws IOException {
        return Allocator.getInstance().allocatePointer(size, true);
    }

    public static Pointer allocateDirect(String string) throws IOException {
        return allocateDirect(string, null);
    }

    public static Pointer allocateDirect(String string, Charset charset) throws IOException {
        if (charset == null) charset = Charset.defaultCharset();
        byte[] bytes = string.getBytes(charset);
        byte[] terminator = "\0".getBytes(charset);
        Pointer pointer = allocateDirect(bytes.length + terminator.length);
        pointer.putByteArray(bytes.length, terminator);
        pointer.putByteArray(0, bytes);
        return pointer;
    }

    /**
     * Indicates whether this memory object represents a native memory address.
     *
     * <p>Memory objects can be either direct (representing native memory), or
     * non-direct (representing java heap memory).
     *
     * @return true if, and only if, this memory object represents a native address.
     */
    public abstract boolean isDirect();

    /**
     * Gets the native address of this memory object (optional operation).
     *
     * @return the native address of this memory object.  If this object is not
     * a native memory address, an address of zero is returned.
     */
    public abstract long address();

    @Override
    public String toString() {
        if (isDirect()) {
            try {
                long size = size();
                return getClass().getName()
                        + '{' +
                        "address=" + address() +
                        ", size=" + size +
                        ", hasMemory=" + hasMemory() +
                        '}';
            }
            catch (UnboundedPointerException e) {
                return getClass().getName()
                        + '{' +
                        "address=" + address() +
                        ", hasMemory=" + hasMemory() +
                        '}';
            }
        }
        else {
            try {
                long size = size();
                return getClass().getName() + "@" + Integer.toHexString(hashCode())
                        + '{' +
                        "size=" + size +
                        ", hasMemory=" + hasMemory() +
                        '}';
            }
            catch (UnboundedPointerException e) {
                return getClass().getName() + "@" + Integer.toHexString(hashCode())
                        + '{' +
                        "hasMemory=" + hasMemory() +
                        '}';
            }
        }
    }

    /**
     * Gets the size of this memory object in bytes (optional operation).
     *
     * @return the size of the memory area this {@code Pointer} points to.  If
     * the size is unknown, throws an {@link UnboundedPointerException}.
     */
    public abstract long size() throws UnboundedPointerException;

    /**
     * Indicates whether the memory region size of this <code>Pointer</code> instance is known.
     *
     * @return true if, and only if, the memory region size of this object is known.
     */
    public abstract boolean isBounded();

    /**
     * Indicates whether this <code>Pointer</code> instance is backed by arrays.
     *
     * @return true if, and only if, this memory object is backed by an array
     */
    public abstract boolean hasArrays();

    /**
     * Returns the arrays that back this pointer.
     *
     * @return The arrays that back this pointer.
     * @throws UnsupportedOperationException if this pointer does not have a backing array.
     */
    public abstract byte[][] arrays();

    /**
     * Returns the offset within this pointer's backing arrays of the first element.
     *
     * @throws UnsupportedOperationException if this pointer does not have a backing array
     * @return The offset of the first element on the backing array
     */
    public abstract long arraysOffset();

    /**
     * Returns the length of this pointer's backing arrays that is used by this pointer.
     *
     * @throws UnsupportedOperationException if this pointer does not have a backing array
     * @return The length of the backing array used
     */
    public abstract long arraysLength();

    /**
     * Reads an {@code byte} (8 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the {@code byte} value at the offset.
     */
    public abstract byte getByte(long offset);

    /**
     * Reads a unsigned {@code byte} (8-bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the unsigned {@code byte} value at the offset.
     */
    public abstract short getUnsignedByte(long offset);

    /**
     * Reads a {@code short} (16 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the {@code short} value at the offset.
     */
    public abstract short getShort(long offset);

    /**
     * Reads a unsigned {@code short} (16 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the unsigned {@code short} value at the offset.
     */
    public abstract int getUnsignedShort(long offset);

    /**
     * Reads a {@code char} (16 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the {@code char} value at the offset.
     */
    public abstract char getChar(long offset);

    /**
     * Reads an {@code int} (32 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the {@code int} value contained in the memory at the offset.
     */
    public abstract int getInt(long offset);

    /**
     * Reads a unsigned {@code int} (32 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the unsigned {@code int} value contained in the memory at the offset.
     */
    public abstract long getUnsignedInt(long offset);

    /**
     * Reads a {@code long} (64 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the {@code long} value at the offset.
     */
    public abstract long getLong(long offset);

    /**
     * Reads a unsigned {@code long} (64 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the unsigned {@code long} value at the offset.
     */
    public abstract BigInteger getUnsignedLong(long offset);

    /**
     * Reads a native {@code int} (32-bit or 64-bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the native {@code int} value at the offset.
     */
    public abstract long getNativeInt(long offset);

    /**
     * Reads a native {@code long} (32-bit or 64-bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the native {@code long} value at the offset.
     */
    public abstract long getNativeLong(long offset);

    /**
     * Reads a native memory address value at the given offset.
     * <p>A native address can be either 32 or 64 bits in size, depending
     * on the cpu architecture.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the native address value contained in the memory at the offset
     */
    public abstract long getAddress(long offset);

    /**
     * Reads a {@code float} (32 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the {@code float} value at the offset.
     */
    public abstract float getFloat(long offset);

    /**
     * Reads a {@code double} (64 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the {@code double} value at the offset.
     */
    public abstract double getDouble(long offset);

    /**
     * Writes a {@code byte} (8 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the {@code byte} value to be written.
     */
    public abstract void putByte(long offset, byte value);

    /**
     * Writes a {@code byte} (8 bit) value at the given offset.
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the {@code byte} value to be written.
     */
    public abstract void putByte(long offset, int value);

    /**
     * Writes a unsigned {@code byte} (8 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the unsigned {@code byte} value to be written.
     */
    public abstract void putUnsignedByte(long offset, short value);

    /**
     * Writes a unsigned {@code byte} (8 bit) value at the given offset.
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the unsigned {@code byte} value to be written.
     */
    public abstract void putUnsignedByte(long offset, int value);

    /**
     * Writes a {@code short} (16 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the {@code short} value to be written.
     */
    public abstract void putShort(long offset, short value);

    /**
     * Writes a {@code short} (16 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the {@code short} value to be written.
     */
    public abstract void putShort(long offset, int value);

    /**
     * Writes a unsigned {@code short} (16 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the unsigned {@code short} value to be written.
     */
    public abstract void putUnsignedShort(long offset, int value);

    /**
     * Writes an {@code int} (32 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the {@code int} value to be written.
     */
    public abstract void putInt(long offset, int value);

    /**
     * Writes a unsigned {@code int} (32 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the unsigned {@code int} value to be written.
     */
    public abstract void putUnsignedInt(long offset, long value);

    /**
     * Writes a {@code long} value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the {@code long} value to be written.
     */
    public abstract void putLong(long offset, long value);

    /**
     * Writes a unsigned {@code long} value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the unsigned {@code long} value to be written.
     */
    public abstract void putUnsignedLong(long offset, BigInteger value);

    /**
     * Writes a native {@code int} value at the given offset.
     *
     * <p>A native {@code int} can be either 32 or 64 bits in size, depending
     * on the cpu architecture, and the C ABI in use.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the native {@code int} value to be written.
     */
    public abstract void putNativeInt(long offset, long value);

    /**
     * Writes a native {@code long} value at the given offset.
     *
     * <p>A native {@code long} can be either 32 or 64 bits in size, depending
     * on the cpu architecture, and the C ABI in use.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the native {@code long} value to be written.
     */
    public abstract void putNativeLong(long offset, long value);

    /**
     * Writes a native memory address value at the given offset.
     * <p>A native address can be either 32 or 64 bits in size, depending
     * on the cpu architecture.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value The native address value to be written.
     */
    public abstract void putAddress(long offset, long value);

    /**
     * Writes a {@code float} (32 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the {@code float} value to be written.
     */
    public abstract void putFloat(long offset, float value);

    /**
     * Writes a {@code double} (64 bit) value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the {@code double} value to be written.
     */
    public abstract void putDouble(long offset, double value);

    /**
     * Bulk get method for multiple {@code byte} values.
     *
     * <p>This method reads multiple {@code byte} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array the array into which values are to be stored.
     * @param index the start index in the {@code array} array to begin storing the values.
     * @param length the number of values to be read.
     */
    public abstract void getByteArray(long offset, byte[] array, int index, int length);

    /**
     * Bulk get method for multiple {@code byte} values.
     *
     * <p>This method reads multiple {@code byte} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array the array into which values are to be stored.
     */
    public abstract void getByteArray(long offset, byte[] array);

    /**
     * Bulk put method for multiple {@code byte} values.
     *
     * <p>This method writes multiple {@code byte} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     * @param index the start index in the {@code array} array to begin reading values.
     * @param length the number of values to be written.
     */
    public abstract void putByteArray(long offset, byte[] array, int index, int length);

    /**
     * Bulk put method for multiple {@code byte} values.
     *
     * <p>This method writes multiple {@code byte} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     */
    public abstract void putByteArray(long offset, byte[] array);

    /**
     * Bulk get method for multiple {@code short} values.
     *
     * <p>This method reads multiple {@code short} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     * @param index the start index in the {@code array} array to begin storing the values.
     * @param length the number of values to be read.
     */
    public abstract void getShortArray(long offset, short[] array, int index, int length);

    /**
     * Bulk get method for multiple {@code short} values.
     *
     * <p>This method reads multiple {@code short} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     */
    public abstract void getShortArray(long offset, short[] array);

    /**
     * Bulk put method for multiple {@code short} values.
     *
     * <p>This method writes multiple {@code short} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     * @param index the start index in the {@code array} array to begin reading values.
     * @param length the number of values to be written.
     */
    public abstract void putShortArray(long offset, short[] array, int index, int length);

    /**
     * Bulk put method for multiple {@code short} values.
     *
     * <p>This method writes multiple {@code short} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     */
    public abstract void putShortArray(long offset, short[] array);

    /**
     * Bulk get method for multiple {@code char} values.
     *
     * <p>This method reads multiple {@code char} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     * @param index the start index in the {@code array} array to begin storing the values.
     * @param length the number of values to be read.
     */
    public abstract void getCharArray(long offset, char[] array, int index, int length);

    /**
     * Bulk get method for multiple {@code char} values.
     *
     * <p>This method reads multiple {@code char} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     */
    public abstract void getCharArray(long offset, char[] array);

    /**
     * Bulk put method for multiple {@code char} values.
     *
     * <p>This method writes multiple {@code char} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     * @param index the start index in the {@code array} array to begin reading values.
     * @param length the number of values to be written.
     */
    public abstract void putCharArray(long offset, char[] array, int index, int length);

    /**
     * Bulk put method for multiple {@code char} values.
     *
     * <p>This method writes multiple {@code char} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     */
    public abstract void putCharArray(long offset, char[] array);

    /**
     * Bulk get method for multiple {@code int} values.
     *
     * <p>This method reads multiple {@code int} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     * @param index the start index in the {@code array} array to begin storing the values.
     * @param length the number of values to be read.
     */
    public abstract void getIntArray(long offset, int[] array, int index, int length);

    /**
     * Bulk get method for multiple {@code int} values.
     *
     * <p>This method reads multiple {@code int} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     */
    public abstract void getIntArray(long offset, int[] array);

    /**
     * Bulk put method for multiple {@code int} values.
     *
     * <p>This method writes multiple {@code int} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     * @param index the start index in the {@code array} array to begin reading values.
     * @param length the number of values to be written.
     */
    public abstract void putIntArray(long offset, int[] array, int index, int length);

    /**
     * Bulk put method for multiple {@code int} values.
     *
     * <p>This method writes multiple {@code int} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     */
    public abstract void putIntArray(long offset, int[] array);

    /**
     * Bulk get method for multiple {@code long} values.
     *
     * <p>This method reads multiple {@code long} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     * @param index the start index in the {@code array} array to begin storing the values.
     * @param length the number of values to be read.
     */
    public abstract void getLongArray(long offset, long[] array, int index, int length);

    /**
     * Bulk get method for multiple {@code long} values.
     *
     * <p>This method reads multiple {@code long} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     */
    public abstract void getLongArray(long offset, long[] array);

    /**
     * Bulk put method for multiple {@code long} values.
     *
     * <p>This method writes multiple {@code long} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     * @param index the start index in the {@code array} array to begin reading values.
     * @param length the number of values to be written.
     */
    public abstract void putLongArray(long offset, long[] array, int index, int length);

    /**
     * Bulk put method for multiple {@code long} values.
     *
     * <p>This method writes multiple {@code long} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     */
    public abstract void putLongArray(long offset, long[] array);

    /**
     * Bulk get method for multiple {@code float} values.
     *
     * <p>This method reads multiple {@code float} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     * @param index the start index in the {@code array} array to begin storing the values.
     * @param length the number of values to be read.
     */
    public abstract void getFloatArray(long offset, float[] array, int index, int length);

    /**
     * Bulk get method for multiple {@code float} values.
     *
     * <p>This method reads multiple {@code float} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     */
    public abstract void getFloatArray(long offset, float[] array);

    /**
     * Bulk put method for multiple {@code float} values.
     *
     * <p>This method writes multiple {@code float} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     * @param index the start index in the {@code array} array to begin reading values.
     * @param length the number of values to be written.
     */
    public abstract void putFloatArray(long offset, float[] array, int index, int length);

    /**
     * Bulk put method for multiple {@code float} values.
     *
     * <p>This method writes multiple {@code float} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     */
    public abstract void putFloatArray(long offset, float[] array);

    /**
     * Bulk get method for multiple {@code double} values.
     *
     * <p>This method reads multiple {@code double} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     * @param index the start index in the {@code array} array to begin storing the values.
     * @param length the number of values to be read.
     */
    public abstract void getDoubleArray(long offset, double[] array, int index, int length);

    /**
     * Bulk get method for multiple {@code double} values.
     *
     * <p>This method reads multiple {@code double} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     */
    public abstract void getDoubleArray(long offset, double[] array);

    /**
     * Bulk put method for multiple {@code double} values.
     *
     * <p>This method writes multiple {@code double} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     * @param index the start index in the {@code array} array to begin reading values.
     * @param length the number of values to be written.
     */
    public abstract void putDoubleArray(long offset, double[] array, int index, int length);

    /**
     * Bulk put method for multiple {@code double} values.
     *
     * <p>This method writes multiple {@code double} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     */
    public abstract void putDoubleArray(long offset, double[] array);

    /**
     * Reads an unbounded {@code Pointer} value at the given offset.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the {@code Pointer} value read from memory.
     */
    public Pointer getPointer(long offset) {
        return wrap(getAddress(offset));
    }

    /**
     * Reads a bounded {@code Pointer} value at the given offset.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @param size the maximum size of the memory location the returned {@code Pointer} represents.
     * @return the {@code Pointer} value read from memory.
     */
    public Pointer getPointer(long offset, long size) {
        return wrap(getAddress(offset), size);
    }

    /**
     * Writes a {@code Pointer} value at the given offset.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param value the {@code Pointer} value to be written to memory.
     */
    public void putPointer(long offset, Pointer value) {
        putAddress(offset, value.address());
    }

    public abstract long getZeroTerminatedStringLength(long offset);

    public abstract long getZeroTerminatedStringLength(long offset, long maxLength);

    public abstract long getZeroTerminatedWideCharStringLength(long offset);

    public abstract long getZeroTerminatedWideCharStringLength(long offset, long maxLength);


    public abstract long getZeroTerminatedStringLength(long offset, Charset charset);

    public abstract long getZeroTerminatedStringLength(long offset, long maxLength, Charset charset);


    public abstract byte[] getZeroTerminatedByteArray(long offset);

    public abstract byte[] getZeroTerminatedByteArray(long offset, int maxLength);

    public abstract byte[] getZeroTerminatedWideCharByteArray(long offset);

    public abstract byte[] getZeroTerminatedWideCharByteArray(long offset, int maxLength);

    public abstract byte[] getZeroTerminatedByteArray(long offset, Charset charset);

    public abstract byte[] getZeroTerminatedByteArray(long offset, int maxLength, Charset charset);

    /**
     * Reads an {@code String} value at the given offset.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the {@code String} value read from memory.
     */
    public abstract String getZeroTerminatedString(long offset);

    /**
     * Reads a {@code String} value at the given offset, using a specific {@code Charset}
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @param maxLength the maximum size of memory to search for a '\0' character.
     * @return the {@code String} value read from memory.
     */
    public abstract String getZeroTerminatedString(long offset, int maxLength);

    /**
     * Reads an {@code String} value at the given offset.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the {@code String} value read from memory.
     */
    public abstract String getZeroTerminatedWideCharString(long offset);

    /**
     * Reads a {@code String} value at the given offset, using a specific {@code Charset}
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @param maxLength the maximum size of memory to search for a '\0' character.
     * @return the {@code String} value read from memory.
     */
    public abstract String getZeroTerminatedWideCharString(long offset, int maxLength);

    /**
     * Reads an {@code String} value at the given offset.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @return the {@code String} value read from memory.
     */
    public abstract String getZeroTerminatedString(long offset, Charset charset);

    /**
     * Reads a {@code String} value at the given offset, using a specific {@code Charset}
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the value will be read.
     * @param maxLength the maximum size of memory to search for a '\0' character.
     * @param charset the {@code Charset} to use to decode the string.
     * @return the {@code String} value read from memory.
     */
    public abstract String getZeroTerminatedString(long offset, int maxLength, Charset charset);

    public abstract void putZeroTerminatedByteArray(long offset, byte[] array);

    public abstract void putZeroTerminatedByteArray(long offset, byte[] array, int index, int length);

    public abstract void putZeroTerminatedWideCharByteArray(long offset, byte[] array);

    public abstract void putZeroTerminatedWideCharByteArray(long offset, byte[] array, int index, int length);

    public abstract void putZeroTerminatedByteArray(long offset, byte[] array, Charset charset);

    public abstract void putZeroTerminatedByteArray(long offset, byte[] array, int index, int length, Charset charset);

    /**
     * Writes a {@code String} value at the given offset, using the default {@code Charset}
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param string the string to be written.
     */
    public abstract void putZeroTerminatedString(long offset, String string);

    /**
     * Writes a {@code String} value at the given offset, using the wide {@code Charset}
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param string the string to be written.
     */
    public abstract void putZeroTerminatedWideCharString(long offset, String string);

    /**
     * Writes a {@code String} value at the given offset, using a specific {@code Charset}
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the value will be written.
     * @param string the string to be written.
     * @param charset the {@code Charset} to use to decode the string.
     */
    public abstract void putZeroTerminatedString(long offset, String string, Charset charset);

    /**
     * Creates a new unbounded {@code Pointer} representing a sub-region of the memory
     * referred to by this {@code Pointer}.
     *
     * @param offset the offset from the start of the memory this {@code Pointer}
     * represents at which the new {@code Pointer} will start.
     * @return a {@code Pointer} instance representing the new sub-region.
     */
    public abstract Pointer slice(long offset);

    /**
     * Creates a new bounded {@code Pointer} representing a sub-region of the memory
     * referred to by this {@code Pointer}.
     *
     * @param offset the offset from the start of the memory this {@code Pointer}
     * represents at which the new {@code Pointer} will start.
     * @param size the maximum size of the memory sub-region.
     *
     * @return a {@code Pointer} instance representing the new sub-region.
     */
    public abstract Pointer slice(long offset, long size);

    public abstract Pointer duplicate();

    public abstract Pointer attachment();

    public boolean hasAttachment() {
        return attachment() != null;
    }

    /**
     * Bulk data transfer from one memory location to another.
     *
     * @param offset the offset from the start of the memory location this {@code Pointer} represents to begin copying from.
     * @param dstPointer the destination memory location to transfer data to.
     * @param dstOffset the offset from the start of the memory location the destination {@code Pointer} represents to begin copying to.
     * @param count the number of bytes to transfer.
     */
    public abstract void transferTo(long offset, Pointer dstPointer, long dstOffset, long count);

    /**
     * Bulk data transfer from one memory location to another.
     *
     * @param dstPointer the destination memory location to transfer data to.
     * @param count the number of bytes to transfer.
     */
    public void transferTo(Pointer dstPointer, long count) {
        transferTo(0, dstPointer, 0, count);
    }

    /**
     * Bulk data transfer from one memory location to another.
     *
     * @param offset the offset from the start of the memory location this {@code Pointer} represents to begin copying to.
     * @param srcPointer the destination memory location to transfer data from.
     * @param srcOffset the offset from the start of the memory location the destination {@code Pointer} represents to begin copying from.
     * @param count the number of bytes to transfer.
     */
    public void transferFrom(long offset, Pointer srcPointer, long srcOffset, long count) {
        srcPointer.transferTo(srcOffset, this, offset, count);
    }

    /**
     * Bulk data transfer from one memory location to another.
     *
     * @param srcPointer the destination memory location to transfer data from.
     * @param count the number of bytes to transfer.
     */
    public void transferFrom(Pointer srcPointer, long count) {
        srcPointer.transferTo(0, this, 0, count);
    }

    /**
     * Checks that the memory region is within the bounds of this memory object
     *
     * @param offset the starting point within this memory region.
     * @param length the length of the memory region in bytes
     * @throws IndexOutOfBoundsException if the memory region is not within the bounds.
     */
    public abstract void checkBounds(long offset, long length) throws IndexOutOfBoundsException;

    /**
     * Checks that the memory region is within the bounds of this memory object
     *
     * @param offset the starting point within this memory region.
     * @param length the length of the memory region in bytes
     * @throws IndexOutOfBoundsException if the memory region is not within the bounds.
     */
    public abstract boolean inBounds(long offset, long length);

    /**
     * Sets the value of each byte in the memory area represented by this {@code Pointer}.
     * to a specified value.
     *
     * @param offset the offset from the start of the memory location this {@code Pointer} represents to begin writing to.
     * @param size the number of bytes to set to the value.
     * @param value the value to set each byte to.
     */
    public abstract void setMemory(long offset, long size, byte value);

    /**
     * Sets the value of each byte in the memory area represented by this {@code Pointer}.
     * to a specified value.
     *
     * @param offset the offset from the start of the memory location this {@code Pointer} represents to begin writing to.
     * @param size the number of bytes to set to the value.
     * @param value the value to set each byte to.
     */
    public abstract void setMemory(long offset, long size, int value);

    /**
     * Returns the location of a byte value within the memory area represented by this {@code Pointer}.
     *
     * @param offset the offset from the start of the memory location this {@code Pointer} represents to begin searching.
     * @param value the {@code byte} value to locate.
     * @return the offset from the start of the search area (i.e. relative to the offset parameter), or -1 if not found.
     */
    public abstract long indexOf(long offset, byte value);

    /**
     * Returns the location of a byte value within the memory area represented by this {@code Pointer}.
     *
     * @param offset the offset from the start of the memory location this {@code Pointer} represents to begin searching.
     * @param value the {@code byte} value to locate.
     * @return the offset from the start of the search area (i.e. relative to the offset parameter), or -1 if not found.
     */
    public abstract long indexOf(long offset, int value);

    /**
     * Returns the location of a byte value within the memory area represented by this {@code Pointer}.
     *
     * @param offset the offset from the start of the memory location this {@code Pointer} represents to begin searching.
     * @param value the {@code byte} value to locate.
     * @param maxLength the maximum number of bytes to search for the desired value.
     * @return the offset from the start of the search area (i.e. relative to the offset parameter), or -1 if not found.
     */
    public abstract long indexOf(long offset, byte value, long maxLength);

    /**
     * Returns the location of a byte value within the memory area represented by this {@code Pointer}.
     *
     * @param offset the offset from the start of the memory location this {@code Pointer} represents to begin searching.
     * @param value the {@code byte} value to locate.
     * @param maxLength the maximum number of bytes to search for the desired value.
     * @return the offset from the start of the search area (i.e. relative to the offset parameter), or -1 if not found.
     */
    public abstract long indexOf(long offset, int value, long maxLength);

    public abstract long indexOf(long offset, byte[] value);

    public abstract long indexOf(long offset, byte[] value, int valueOffset, int valueLength);

    public abstract long indexOf(long offset, byte[] value, long maxLength);

    public abstract long indexOf(long offset, byte[] value, int valueOffset, int valueLength, long maxLength);

    /**
     * Bulk get method for multiple unbounded {@code Pointer} values.
     *
     * <p>This method reads multiple {@code Pointer} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     * @param index the start index in the {@code array} array to begin storing the values.
     * @param length the number of values to be read.
     */
    public void getPointerArray(long offset, Pointer[] array, int index, int length) {
        int pointerSize = Foreign.getInstance().addressSize();
        for (int i = 0; i < length; i ++) {
            array[index + i] = getPointer(offset + (long) i * pointerSize);
        }
    }

    /**
     * Bulk get method for multiple unbounded {@code Pointer} values.
     *
     * <p>This method reads multiple {@code Pointer} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     */
    public void getPointerArray(long offset, Pointer[] array) {
        getPointerArray(offset, array, 0, array.length);
    }

    /**
     * Bulk get method for multiple bounded {@code Pointer} values.
     *
     * <p>This method reads multiple {@code Pointer} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     * @param size the maximum size of the memory location each Pointer represents.
     * @param index the start index in the {@code array} to begin storing the values.
     * @param length the number of values to be read.
     */
    public void getPointerArray(long offset, Pointer[] array, long size, int index, int length) {
        int pointerSize = Foreign.getInstance().addressSize();
        for (int i = 0; i < length; i ++) {
            array[index + i] = getPointer(offset + (long) i * pointerSize, size);
        }
    }

    /**
     * Bulk get method for multiple bounded {@code Pointer} values.
     *
     * <p>This method reads multiple {@code Pointer} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     * @param size the maximum size of the memory location each Pointer represents.
     */
    public void getPointerArray(long offset, Pointer[] array, long size) {
        getPointerArray(offset, array, size, 0, array.length);
    }

    /**
     * Bulk get method for multiple bounded {@code Pointer} values.
     *
     * <p>This method reads multiple {@code Pointer} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     * @param arrayIndex the start index in the {@code array} to begin storing the values.
     * @param size the array of maximum size of the memory location corresponding Pointer represents.
     * @param sizeIndex the start index in the {@code size} array to begin read the sizes.
     * @param length the number of values to be read.
     */
    public void getPointerArray(long offset, Pointer[] array, int arrayIndex, long[] size, int sizeIndex, int length) {
        int pointerSize = Foreign.getInstance().addressSize();
        for (int i = 0; i < length; i ++) {
            array[arrayIndex + i] = getPointer(offset + (long) i * pointerSize, size[sizeIndex + i]);
        }
    }

    /**
     * Bulk get method for multiple bounded {@code Pointer} values.
     *
     * <p>This method reads multiple {@code Pointer} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     * @param size the array of maximum size of the memory location corresponding Pointer represents.
     * @param index the start index in the {@code array} to begin storing the values.
     * @param length the number of values to be read.
     */
    public void getPointerArray(long offset, Pointer[] array, long[] size, int index, int length) {
        getPointerArray(offset, array, index, size, index, length);
    }

    /**
     * Bulk get method for multiple bounded {@code Pointer} values.
     *
     * <p>This method reads multiple {@code Pointer} values from consecutive addresses,
     * beginning at the given offset, and stores them in an array.
     *
     * @param offset The offset from the start of the memory this {@code Pointer} represents at which the first value will be read.
     * @param array The array into which values are to be stored.
     * @param size the array of maximum size of the memory location corresponding Pointer represents.
     */
    public void getPointerArray(long offset, Pointer[] array, long[] size) {
        getPointerArray(offset, array, size, 0, array.length);
    }

    /**
     * Bulk put method for multiple {@code Pointer} values.
     *
     * <p>This method writes multiple {@code Pointer} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     * @param index the start index in the {@code array} array to begin reading values.
     * @param length the number of values to be written.
     */
    public void putPointerArray(long offset, Pointer[] array, int index, int length) {
        int pointerSize = Foreign.getInstance().addressSize();
        for (int i = 0; i < length; i ++) {
            putPointer(offset + (long) i * pointerSize, array[index + i]);
        }
    }

    /**
     * Bulk put method for multiple {@code Pointer} values.
     *
     * <p>This method writes multiple {@code Pointer} values to consecutive addresses,
     * beginning at the given offset, from an array.
     *
     * @param offset the offset from the start of the memory this {@code Pointer} represents at which the first value will be written.
     * @param array the array to get values from.
     */
    public void putPointerArray(long offset, Pointer[] array) {
        putPointerArray(offset, array, 0, array.length);
    }

    public abstract Pointer reallocate(long size) throws IOException;

    public abstract boolean hasMemory();

    public abstract boolean isNullPointer();

    @Override
    public int compareTo(Pointer other) {
        try {
            if (isNullPointer()) return other.isNullPointer() ? 0 : -1;
            else if (other.isNullPointer()) return isNullPointer() ? 0 : 1;
            else if (isBounded() && other.isBounded()) {
                long size = size();
                long otherSize = other.size();
                long length;
                if (size > 0) length = otherSize < 0 ? size : Math.min(size, otherSize);
                else length = otherSize >= 0 ? otherSize : Math.min(size, otherSize);

                if (length >= 0) {
                    for (long i = 0; i < length; i ++) {
                        byte oa = getByte(i);
                        byte ob = other.getByte(i);
                        if (oa != ob) return FastMath.compareUnsigned(oa, ob);
                    }
                }
                else {
                    byte oa, ob;
                    for (long i = 0; i < Long.MAX_VALUE; i ++) {
                        oa = getByte(i);
                        ob = other.getByte(i);
                        if (oa != ob) return FastMath.compareUnsigned(oa, ob);
                    }
                    oa = getByte(Long.MAX_VALUE);
                    ob = other.getByte(Long.MAX_VALUE);
                    if (oa != ob) return FastMath.compareUnsigned(oa, ob);
                    for (long i = Long.MIN_VALUE; i < length; i ++) {
                        oa = getByte(i);
                        ob = other.getByte(i);
                        if (oa != ob) return FastMath.compareUnsigned(oa, ob);
                    }
                }
                return FastMath.compareUnsigned(size, otherSize);
            }
            else if (isBounded()) return compare(other, 0, size());
            else return compare(other, 0, other.size());
        } catch (UnboundedPointerException e) {
            throw new IllegalStateException(e);
        }
    }

    private int compare(Pointer other, long offset, long length) {
        // FIXME: performance optimization
        if (length > 0) {
            for (long i = 0; i < length; i ++) {
                byte oa = getByte(offset + i);
                byte ob = other.getByte(offset + i);
                if (oa != ob) return FastMath.compareUnsigned(oa, ob);
            }
            return 0;
        }
        else {
            byte oa, ob;
            for (long i = 0; i < Long.MAX_VALUE; i ++) {
                oa = getByte(offset + i);
                ob = other.getByte(offset + i);
                if (oa != ob) return FastMath.compareUnsigned(oa, ob);
            }
            oa = getByte(offset + Long.MAX_VALUE);
            ob = other.getByte(offset + Long.MAX_VALUE);
            if (oa != ob) return FastMath.compareUnsigned(oa, ob);
            for (long i = Long.MIN_VALUE; i < length; i ++) {
                oa = getByte(offset + i);
                ob = other.getByte(offset + i);
                if (oa != ob) return FastMath.compareUnsigned(oa, ob);
            }
            return 0;
        }
    }

    public int compareTo(Pointer other, long offset, long length) {
        if (length == 0) return 0;
        else if (isNullPointer() && other.isNullPointer()) return 0;
        else if (isDirect() && other.isDirect()) return Allocator.getInstance().compareMemory(address(), other.address(), length);
        else return compare(other, offset, length);
    }

}
