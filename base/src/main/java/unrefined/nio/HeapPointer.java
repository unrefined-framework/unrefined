package unrefined.nio;

import unrefined.math.FastMath;
import unrefined.util.foreign.Foreign;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;

import static unrefined.nio.Allocator.SIZE_MAX;

public class HeapPointer extends Pointer {

    private interface NativeTypeAdapter {
        long get(long offset);
        void put(long offset, long value);
    }

    private final NativeTypeAdapter NATIVE_TYPE_ADAPTER_64 = new NativeTypeAdapter() {
        @Override
        public long get(long offset) {
            return getLong(offset);
        }
        @Override
        public void put(long offset, long value) {
            putLong(offset, value);
        }
    };

    private final NativeTypeAdapter NATIVE_TYPE_ADAPTER_32 = new NativeTypeAdapter() {
        @Override
        public long get(long offset) {
            return (long) getInt(offset) & 0xFFFFFFFFL;
        }
        @Override
        public void put(long offset, long value) {
            putInt(offset, (int) value);
        }
    };

    private final NativeTypeAdapter NATIVE_INT_ADAPTER = Foreign.getInstance().nativeIntSize() == 8 ? NATIVE_TYPE_ADAPTER_64 : NATIVE_TYPE_ADAPTER_32;
    private final NativeTypeAdapter NATIVE_LONG_ADAPTER = Foreign.getInstance().nativeLongSize() == 8 ? NATIVE_TYPE_ADAPTER_64 : NATIVE_TYPE_ADAPTER_32;
    private final NativeTypeAdapter ADDRESS_ADAPTER = Foreign.getInstance().addressSize() == 8 ? NATIVE_TYPE_ADAPTER_64 : NATIVE_TYPE_ADAPTER_32;

    private static final long MAX_2D_ARRAY_SIZE = (long) Integer.MAX_VALUE * Integer.MAX_VALUE;

    private final byte[][] arrays;
    private final long size;
    private final long arraysOffset;
    private final long arraysLength;

    private final HeapPointer attachment;

    private final boolean hasMemory;

    public HeapPointer(long size) throws IOException {
        if (size < 0 || size > MAX_2D_ARRAY_SIZE) throw new IOException(new OutOfMemoryError("Requested array size too large"));
        else if (size <= Integer.MAX_VALUE) arrays = new byte[1][(int) size];
        else {
            int divide = (int) (size / Integer.MAX_VALUE);
            int reminder = (int) (size % Integer.MAX_VALUE);
            arrays = new byte[divide + (reminder > 0 ? 1 : 0)][];
            for (int i = 1; i < arrays.length; i ++) {
                arrays[i] = new byte[Integer.MAX_VALUE];
            }
            arrays[0] = new byte[reminder];
        }
        this.attachment = null;
        this.size = size;
        this.arraysOffset = 0;
        this.arraysLength = size;
        hasMemory = true;
    }

    public HeapPointer(byte[] array, int offset, int length) {
        this.attachment = null;
        this.arrays = new byte[][] { array };
        this.arraysOffset = offset;
        this.arraysLength = array.length;
        this.size = length;
        hasMemory = false;
    }

    protected HeapPointer(HeapPointer attachment, long arraysLength, byte[][] arrays, long arraysOffset, long size) {
        this.attachment = attachment;
        this.arrays = arrays;
        this.size = size;
        this.arraysOffset = arraysOffset;
        this.arraysLength = arraysLength;
        hasMemory = false;
    }

    @Override
    public boolean isDirect() {
        return false;
    }

    @Override
    public long address() {
        return 0;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public boolean isBounded() {
        return true;
    }

    @Override
    public boolean hasArrays() {
        return true;
    }

    @Override
    public byte[][] arrays() {
        return arrays;
    }

    @Override
    public long arraysOffset() {
        return arraysOffset;
    }

    @Override
    public long arraysLength() {
        return arraysLength;
    }

    private byte getByte0(long offset) {
        return arrays[(int) (offset / Integer.MAX_VALUE)][(int) (offset % Integer.MAX_VALUE)];
    }

    @Override
    public byte getByte(long offset) {
        if (offset < 0 || offset >= size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else return getByte0(offset + arraysOffset);
    }

    @Override
    public short getUnsignedByte(long offset) {
        return (short) Byte.toUnsignedInt(getByte(offset));
    }

    @Override
    public short getShort(long offset) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else if (offset + 1 >= size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset + 1));
        else {
            offset += arraysOffset;
            return (short) (((getByte0(offset) & 0xFF) << 8) + (getByte0(offset + 1) & 0xFF));
        }
    }

    @Override
    public int getUnsignedShort(long offset) {
        return Short.toUnsignedInt(getShort(offset));
    }

    @Override
    public char getChar(long offset) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else if (offset + 1 >= size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset + 1));
        else {
            offset += arraysOffset;
            return (char) (((getByte0(offset) & 0xFF) << 8) + (getByte0(offset + 1) & 0xFF));
        }
    }

    private int getInt0(long offset) {
        return ((getByte0(offset) & 0xFF) << 24) + ((getByte0(offset + 1) & 0xFF) << 16) + ((getByte0(offset + 2) & 0xFF) << 8) + (getByte0(offset + 3) & 0xFF);
    }

    @Override
    public int getInt(long offset) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else if (offset + 3 >= size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset + 3));
        else {
            offset += arraysOffset;
            return getInt0(offset);
        }
    }

    @Override
    public long getUnsignedInt(long offset) {
        return Integer.toUnsignedLong(getInt(offset));
    }

    @Override
    public long getLong(long offset) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else if (offset + 7 >= size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset + 7));
        else {
            offset += arraysOffset;
            return (((long) getInt0(offset) & 0xFFFFFFFFL) << 32) + (getInt0(offset + 4) & 0xFFFFFFFFL);
        }
    }

    @Override
    public BigInteger getUnsignedLong(long offset) {
        return FastMath.unsign(getLong(offset));
    }

    @Override
    public long getNativeInt(long offset) {
        return NATIVE_INT_ADAPTER.get(offset);
    }

    @Override
    public long getNativeLong(long offset) {
        return NATIVE_LONG_ADAPTER.get(offset);
    }

    @Override
    public long getAddress(long offset) {
        return ADDRESS_ADAPTER.get(offset);
    }

    @Override
    public float getFloat(long offset) {
        return Float.intBitsToFloat(getInt(offset));
    }

    @Override
    public double getDouble(long offset) {
        return Double.longBitsToDouble(getLong(offset));
    }

    private void putByte0(long offset, byte value) {
        arrays[(int) (offset / Integer.MAX_VALUE)][(int) (offset % Integer.MAX_VALUE)] = value;
    }

    @Override
    public void putByte(long offset, byte value) {
        if (offset < 0 || offset >= size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else putByte0(offset + arraysOffset, value);
    }

    @Override
    public void putByte(long offset, int value) {
        putByte(offset, (byte) value);
    }

    @Override
    public void putUnsignedByte(long offset, short value) {
        putByte(offset, (byte) value);
    }

    @Override
    public void putUnsignedByte(long offset, int value) {
        putByte(offset, (byte) value);
    }

    @Override
    public void putShort(long offset, short value) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else if (offset + 1 >= size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset + 1));
        else {
            offset += arraysOffset;
            putByte0(offset, (byte) (value >> 8));
            putByte0(offset + 1, (byte) value);
        }
    }

    @Override
    public void putShort(long offset, int value) {
        putShort(offset, (short) value);
    }

    @Override
    public void putUnsignedShort(long offset, int value) {
        putShort(offset, (short) value);
    }

    private void putInt0(long offset, int value) {
        putByte0(offset, (byte) (value >> 24));
        putByte0(offset + 1, (byte) (value >> 16));
        putByte0(offset + 2, (byte) (value >> 8));
        putByte0(offset + 3, (byte) value);
    }

    @Override
    public void putInt(long offset, int value) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else if (offset + 3 >= size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset + 1));
        else {
            offset += arraysOffset;
            putInt0(offset, value);
        }
    }

    @Override
    public void putUnsignedInt(long offset, long value) {
        putInt(offset, (int) value);
    }

    @Override
    public void putLong(long offset, long value) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else if (offset + 7 >= size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset + 1));
        else {
            offset += arraysOffset;
            putInt0(offset, (int) (value >> 32));
            putInt0(offset + 4, (int) value);
        }
    }

    @Override
    public void putUnsignedLong(long offset, BigInteger value) {
        putLong(offset, value.longValue());
    }

    @Override
    public void putNativeInt(long offset, long value) {
        NATIVE_INT_ADAPTER.put(offset, value);
    }

    @Override
    public void putNativeLong(long offset, long value) {
        NATIVE_LONG_ADAPTER.put(offset, value);
    }

    @Override
    public void putAddress(long offset, long value) {
        ADDRESS_ADAPTER.put(offset, value);
    }

    @Override
    public void putFloat(long offset, float value) {
        putInt(offset, Float.floatToIntBits(value));
    }

    @Override
    public void putDouble(long offset, double value) {
        putLong(offset, Double.doubleToLongBits(value));
    }

    @Override
    public void getByteArray(long offset, byte[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + length;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        else {
            offset += arraysOffset;
            int startArray = (int) (offset / Integer.MAX_VALUE);
            int startIndex = (int) (offset % Integer.MAX_VALUE);
            int endArray = (int) (size / Integer.MAX_VALUE);
            if (startArray == endArray) System.arraycopy(arrays[startArray], startIndex, array, index, length);
            else {
                int endRange = (int) (size % Integer.MAX_VALUE);
                int startRange = length - endRange;
                System.arraycopy(arrays[startArray], startIndex, array, index, startRange);
                System.arraycopy(arrays[endArray], 0, array, index + startRange, endRange);
            }
        }
    }

    @Override
    public void getByteArray(long offset, byte[] array) {
        getByteArray(offset, array, 0, array.length);
    }

    @Override
    public void putByteArray(long offset, byte[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + length;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        else {
            offset += arraysOffset;
            int startArray = (int) (offset / Integer.MAX_VALUE);
            int startIndex = (int) (offset % Integer.MAX_VALUE);
            int endArray = (int) (size / Integer.MAX_VALUE);
            if (startArray == endArray) System.arraycopy(array, index, arrays[startArray], startIndex, length);
            else {
                int endRange = (int) (size % Integer.MAX_VALUE);
                int startRange = length - endRange;
                System.arraycopy(array, index, arrays[startArray], startIndex, startRange);
                System.arraycopy(array, index + startRange, arrays[endArray], 0, endRange);
            }
        }
    }

    @Override
    public void putByteArray(long offset, byte[] array) {
        putByteArray(offset, array, 0, array.length);
    }

    @Override
    public void getShortArray(long offset, short[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 1;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 1;
            array[i] = (short) (((getByte0(offset + l) & 0xFF) << 8) + (getByte0(offset + l + 1)) & 0xFF);
        }
    }

    @Override
    public void getShortArray(long offset, short[] array) {
        getShortArray(offset, array, 0, array.length);
    }

    @Override
    public void putShortArray(long offset, short[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 1;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 1;
            putByte0(offset + l, (byte) (array[i] >> 8));
            putByte0(offset + l + 1, (byte) array[i]);
        }
    }

    @Override
    public void putShortArray(long offset, short[] array) {
        putShortArray(offset, array, 0, array.length);
    }

    @Override
    public void getCharArray(long offset, char[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 1;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 1;
            array[i] = (char) (((getByte0(offset + l) & 0xFF) << 8) + (getByte0(offset + l + 1)) & 0xFF);
        }
    }

    @Override
    public void getCharArray(long offset, char[] array) {
        getCharArray(offset, array, 0, array.length);
    }

    @Override
    public void putCharArray(long offset, char[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 1;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 1;
            putByte0(offset + l, (byte) (array[i] >> 8));
            putByte0(offset + l + 1, (byte) array[i]);
        }
    }

    @Override
    public void putCharArray(long offset, char[] array) {
        putCharArray(offset, array, 0, array.length);
    }

    @Override
    public void getIntArray(long offset, int[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 2;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 2;
            array[i] = getInt0(offset + l);
        }
    }

    @Override
    public void getIntArray(long offset, int[] array) {
        getIntArray(offset, array, 0, array.length);
    }

    @Override
    public void putIntArray(long offset, int[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 2;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 2;
            putInt0(offset + l, array[i]);
        }
    }

    @Override
    public void putIntArray(long offset, int[] array) {
        putIntArray(offset, array, 0, array.length);
    }

    @Override
    public void getLongArray(long offset, long[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 3;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 3;
            array[i] = (((long) getInt0(offset + l) & 0xFFFFFFFFL) << 32) + (getInt0(offset + l + 4) & 0xFFFFFFFFL);
        }
    }

    @Override
    public void getLongArray(long offset, long[] array) {
        getLongArray(offset, array, 0, array.length);
    }

    @Override
    public void putLongArray(long offset, long[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 3;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 3;
            putInt0(offset + l, (int) (array[i] >> 32));
            putInt0(offset + l + 4, (int) array[i]);
        }
    }

    @Override
    public void putLongArray(long offset, long[] array) {
        putLongArray(offset, array, 0, array.length);
    }

    @Override
    public void getFloatArray(long offset, float[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 2;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 2;
            array[i] = Float.intBitsToFloat(getInt0(offset + l));
        }
    }

    @Override
    public void getFloatArray(long offset, float[] array) {
        getFloatArray(offset, array, 0, array.length);
    }

    @Override
    public void putFloatArray(long offset, float[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 2;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 2;
            putInt0(offset + l, Float.floatToIntBits(array[i]));
        }
    }

    @Override
    public void putFloatArray(long offset, float[] array) {
        putFloatArray(offset, array, 0, array.length);
    }

    @Override
    public void getDoubleArray(long offset, double[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 3;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 3;
            array[i] = Double.longBitsToDouble((((long) getInt0(offset + l) & 0xFFFFFFFFL) << 32) + (getInt0(offset + l + 4) & 0xFFFFFFFFL));
        }
    }

    @Override
    public void getDoubleArray(long offset, double[] array) {
        getDoubleArray(offset, array, 0, array.length);
    }

    @Override
    public void putDoubleArray(long offset, double[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 3;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arraysOffset;
        long l;
        long value;
        for (int i = index; i < length; i ++) {
            l = (long) i << 3;
            value = Double.doubleToLongBits(array[i]);
            putInt0(offset + l, (int) (value >> 32));
            putInt0(offset + l + 4, (int) value);
        }
    }

    @Override
    public void putDoubleArray(long offset, double[] array) {
        putDoubleArray(offset, array, 0, array.length);
    }

    @Override
    public long getZeroTerminatedStringLength(long offset) {
        return getZeroTerminatedStringLength(offset, null);
    }

    @Override
    public long getZeroTerminatedStringLength(long offset, long maxLength) {
        return getZeroTerminatedStringLength(offset, maxLength, null);
    }

    @Override
    public long getZeroTerminatedWideCharStringLength(long offset, long maxLength) {
        return getZeroTerminatedStringLength(offset, maxLength, Foreign.getInstance().wideCharset());
    }

    @Override
    public long getZeroTerminatedWideCharStringLength(long offset) {
        return getZeroTerminatedStringLength(offset, Foreign.getInstance().wideCharset());
    }

    @Override
    public long getZeroTerminatedStringLength(long offset, Charset charset) {
        return getZeroTerminatedStringLength(offset, SIZE_MAX);
    }

    @Override
    public long getZeroTerminatedStringLength(long offset, long maxLength, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        byte[] terminator = "\0".getBytes(charset);
        int size = terminator.length;
        if (size == 1 && terminator[0] == '\0') return indexOf(offset, '\0', maxLength);
        long length = 0;
        byte[] buffer = new byte[size];
        while (true) {
            getByteArray(offset, buffer, 0, size);
            if (Arrays.equals(terminator, buffer)) return length;
            else length ++;
            offset += size;
        }
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset) {
        return getZeroTerminatedByteArray(offset, Integer.MAX_VALUE);
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset, int maxLength) {
        return getZeroTerminatedByteArray(offset, maxLength, null);
    }

    @Override
    public byte[] getZeroTerminatedWideCharByteArray(long offset) {
        return getZeroTerminatedWideCharByteArray(offset, Integer.MAX_VALUE);
    }

    @Override
    public byte[] getZeroTerminatedWideCharByteArray(long offset, int maxLength) {
        return getZeroTerminatedByteArray(offset, maxLength, Foreign.getInstance().wideCharset());
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset, Charset charset) {
        return getZeroTerminatedByteArray(offset, Integer.MAX_VALUE, charset);
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset, int maxLength, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        long stringLength = getZeroTerminatedStringLength(offset, maxLength, charset) * "\0".getBytes(charset).length;
        if (stringLength < 0 || stringLength > Integer.MAX_VALUE) stringLength = Integer.MAX_VALUE;
        byte[] array = new byte[(int) stringLength];
        getByteArray(offset, array);
        return array;
    }

    @Override
    public String getZeroTerminatedString(long offset) {
        return getZeroTerminatedString(offset, Integer.MAX_VALUE);
    }

    @Override
    public String getZeroTerminatedString(long offset, int maxLength) {
        return getZeroTerminatedString(offset, maxLength, null);
    }

    @Override
    public String getZeroTerminatedWideCharString(long offset) {
        return getZeroTerminatedWideCharString(offset, Integer.MAX_VALUE);
    }

    @Override
    public String getZeroTerminatedWideCharString(long offset, int maxLength) {
        return getZeroTerminatedString(offset, maxLength, Foreign.getInstance().wideCharset());
    }

    @Override
    public String getZeroTerminatedString(long offset, Charset charset) {
        return getZeroTerminatedString(offset, Integer.MAX_VALUE, charset);
    }

    @Override
    public String getZeroTerminatedString(long offset, int maxLength, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        long stringLength = getZeroTerminatedStringLength(offset, maxLength, charset) * "\0".getBytes(charset).length;
        if (stringLength < 0 || stringLength > Integer.MAX_VALUE) stringLength = Integer.MAX_VALUE;
        byte[] array = new byte[(int) stringLength];
        getByteArray(offset, array);
        return new String(array, charset);
    }

    @Override
    public void putZeroTerminatedByteArray(long offset, byte[] array) {
        putZeroTerminatedByteArray(offset, array, null);
    }

    @Override
    public void putZeroTerminatedByteArray(long offset, byte[] array, int index, int length) {
        putZeroTerminatedByteArray(offset, array, index, length, null);
    }

    @Override
    public void putZeroTerminatedWideCharByteArray(long offset, byte[] array) {
        putZeroTerminatedByteArray(offset, array, Foreign.getInstance().wideCharset());
    }

    @Override
    public void putZeroTerminatedWideCharByteArray(long offset, byte[] array, int index, int length) {
        putZeroTerminatedByteArray(offset, array, index, length, Foreign.getInstance().wideCharset());
    }

    @Override
    public void putZeroTerminatedByteArray(long offset, byte[] array, Charset charset) {
        putZeroTerminatedByteArray(offset, array, 0, array.length, charset);
    }

    @Override
    public void putZeroTerminatedByteArray(long offset, byte[] array, int index, int length, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        putByteArray(offset, array, index, length);
        putByteArray(offset + length, "\0".getBytes(charset));
    }

    @Override
    public void putZeroTerminatedString(long offset, String string) {
        putZeroTerminatedByteArray(offset, string.getBytes(Charset.defaultCharset()));
    }

    @Override
    public void putZeroTerminatedWideCharString(long offset, String string) {
        putZeroTerminatedWideCharByteArray(offset, string.getBytes(Foreign.getInstance().wideCharset()));
    }

    @Override
    public void putZeroTerminatedString(long offset, String string, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        putZeroTerminatedByteArray(offset, string.getBytes(charset), charset);
    }

    @Override
    public Pointer slice(long offset) {
        return slice(offset, arraysLength - offset);
    }

    @Override
    public Pointer slice(long offset, long size) {
        return new HeapPointer(this, arraysLength, arrays, offset, size);
    }

    @Override
    public Pointer duplicate() {
        return new HeapPointer(this, arraysLength, arrays, arraysOffset, size);
    }

    @Override
    public Pointer attachment() {
        return attachment;
    }

    @Override
    public void transferTo(long offset, Pointer dstPointer, long dstOffset, long count) {
        if (dstPointer.isBounded()) dstPointer.checkBounds(dstOffset, count);
        if (offset < 0 || offset >= this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            BigInteger size = FastMath.unsign(count).add(BigInteger.valueOf(offset));
            if (size.compareTo(BigInteger.valueOf(this.size)) > 0) throw new IndexOutOfBoundsException("Index out of range: " + size);
            offset += arraysOffset;
            int startArray = (int) (offset / Integer.MAX_VALUE);
            int startIndex = (int) (offset % Integer.MAX_VALUE);
            int endArray = (int) (size.longValue() / Integer.MAX_VALUE);
            int endIndex = (int) (size.longValue() % Integer.MAX_VALUE);
            if (startArray == endArray) {
                dstPointer.putByteArray(dstOffset, arrays[startArray], startIndex, (int) count);
            }
            else {
                int startRange = Integer.MAX_VALUE - startIndex;
                dstPointer.putByteArray(dstOffset, arrays[startArray], startIndex, startRange);
                dstOffset += startRange;
                for (int array = startArray + 1; array < endArray; array ++) {
                    dstPointer.putByteArray(dstOffset, arrays[array], 0, Integer.MAX_VALUE);
                    dstOffset += Integer.MAX_VALUE;
                }
                dstPointer.putByteArray(dstOffset, arrays[endArray], 0, endIndex + 1);
            }
        }
    }

    @Override
    public void checkBounds(long offset, long length) throws IndexOutOfBoundsException {
        if (offset < 0 || length < 0) {
            throw new IndexOutOfBoundsException("Index out of range: " +
                    FastMath.unsign(offset).add(FastMath.unsign(length)));
        }
        else {
            long size = offset + length;
            if (size < 0 || size > this.size) {
                throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
            }
        }
    }

    @Override
    public boolean inBounds(long offset, long length) {
        if (offset < 0 || length < 0) return false;
        long size = offset + length;
        return size >= 0 && size <= this.size;
    }

    @Override
    public void setMemory(long offset, long size, byte value) {
        offset += arraysOffset;
        for (long i = 0; i < size; i ++) {
            putByte0(offset + i, value);
        }
    }

    @Override
    public void setMemory(long offset, long size, int value) {
        setMemory(offset, size, (byte) value);
    }

    @Override
    public long indexOf(long offset, byte value) {
        return indexOf(offset, value, size - offset);
    }

    @Override
    public long indexOf(long offset, int value) {
        return indexOf(offset, (byte) value);
    }

    @Override
    public long indexOf(long offset, byte value, long maxLength) {
        if (offset < 0 || offset >= this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            BigInteger size = BigInteger.valueOf(maxLength).add(BigInteger.valueOf(offset));
            if (size.compareTo(BigInteger.valueOf(this.size)) > 0) throw new IndexOutOfBoundsException("Index out of range: " + size);
            offset += arraysOffset;
            int startArray = (int) (offset / Integer.MAX_VALUE);
            int startIndex = (int) (offset % Integer.MAX_VALUE);
            int endArray = (int) (size.longValue() / Integer.MAX_VALUE);
            int endIndex = (int) (size.longValue() % Integer.MAX_VALUE);
            if (startArray == endArray) {
                for (int i = startIndex; i <= endIndex; i ++) {
                    if (arrays[startArray][i] == value) return (long) startArray * Integer.MAX_VALUE + i;
                }
            }
            else {
                for (int i = startIndex; i < Integer.MAX_VALUE; i ++) {
                    if (arrays[startArray][i] == value) return (long) startArray * Integer.MAX_VALUE + i;
                }
                for (int array = startArray + 1; array < endArray; array ++) {
                    for (int i = 0; i < Integer.MAX_VALUE; i ++) {
                        if (arrays[array][i] == value) return (long) array * Integer.MAX_VALUE + i;
                    }
                }
                for (int i = 0; i <= endIndex; i ++) {
                    if (arrays[endArray][i] == value) return (long) endArray * Integer.MAX_VALUE + i;
                }
            }
            return -1;
        }
    }

    @Override
    public long indexOf(long offset, int value, long maxLength) {
        return indexOf(offset, (byte) value, maxLength);
    }

    @Override
    public long indexOf(long offset, byte[] value) {
        return indexOf(offset, value, 0, value.length);
    }

    @Override
    public long indexOf(long offset, byte[] value, int valueOffset, int valueLength) {
        return indexOf(offset, value, valueOffset, valueLength, SIZE_MAX);
    }

    @Override
    public long indexOf(long offset, byte[] value, long maxLength) {
        return indexOf(offset, value, 0, value.length, maxLength);
    }

    @Override
    public long indexOf(long offset, byte[] value, int valueOffset, int valueLength, long maxLength) {
        if (value.length == 1) return indexOf(offset, value[0]);
        else {
            if (maxLength < 0) throw new IndexOutOfBoundsException("Index out of range: " + maxLength);
            else if (maxLength > MAX_2D_ARRAY_SIZE) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(maxLength));
            byte[] buffer = new byte[valueLength];
            for (long searched = 0; searched < size; searched ++) {
                getByteArray(searched, buffer, valueOffset, valueLength);
                if (Arrays.equals(value, buffer)) return searched;
            }
            return 0;
        }
    }

    @Override
    public Pointer reallocate(long size) throws IOException {
        try {
            HeapPointer pointer = new HeapPointer(size);
            transferTo(0, pointer, 0, Math.min(size, this.size));
            return pointer;
        }
        catch (OutOfMemoryError e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean hasMemory() {
        return hasMemory;
    }

    @Override
    public boolean isNullPointer() {
        return arraysLength == 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Pointer)) return false;

        Pointer that = (Pointer) object;

        if (isNullPointer() && that.isNullPointer()) return true;
        else if (that.hasArrays()) {
            return arrays() == that.arrays() && size() == that.size() && arraysOffset() == that.arraysOffset();
        }
        else return false;
    }

    @Override
    public int hashCode() {
        int result = arrays.hashCode();
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (int) (arraysOffset ^ (arraysOffset >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName() + '@' + Integer.toHexString(hashCode())
                + '{' +
                "hasMemory=" + hasMemory +
                ", arrays=" + arrays +
                '}';
    }

    @Override
    public void close() throws IOException {
    }

}
