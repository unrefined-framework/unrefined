package unrefined.nio;

import unrefined.internal.NumberUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;

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

    private final NativeTypeAdapter NATIVE_INT_ADAPTER = getAllocator().nativeIntSize() == 8 ? NATIVE_TYPE_ADAPTER_64 : NATIVE_TYPE_ADAPTER_32;
    private final NativeTypeAdapter NATIVE_LONG_ADAPTER = getAllocator().nativeLongSize() == 8 ? NATIVE_TYPE_ADAPTER_64 : NATIVE_TYPE_ADAPTER_32;
    private final NativeTypeAdapter ADDRESS_ADAPTER = getAllocator().addressSize() == 8 ? NATIVE_TYPE_ADAPTER_64 : NATIVE_TYPE_ADAPTER_32;

    private static final long MAX_2D_ARRAY_SIZE = (long) Integer.MAX_VALUE * Integer.MAX_VALUE;

    private final byte[][] arrays;
    private final long size;
    private final long arraysOffset;
    private final long arraysLength;

    private final HeapPointer attachment;

    private final boolean hasMemory;

    public HeapPointer(Allocator allocator, long size) throws IOException {
        super(allocator);
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

    public HeapPointer(Allocator allocator, byte[] array, int offset, int length) {
        super(allocator);
        this.attachment = null;
        this.arrays = new byte[][] { array };
        this.arraysOffset = offset;
        this.arraysLength = array.length;
        this.size = length;
        hasMemory = false;
    }

    protected HeapPointer(HeapPointer attachment, long arraysLength, byte[][] arrays, long arraysOffset, long size) {
        super(attachment.getAllocator());
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
        if (offset < 0 || offset >= size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else return getByte0(offset + arraysOffset);
    }

    @Override
    public short getUnsignedByte(long offset) {
        return (short) Byte.toUnsignedInt(getByte(offset));
    }

    @Override
    public short getShort(long offset) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else if (offset + 1 >= size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset + 1));
        else {
            offset += arraysOffset;
            return (short) ((getByte0(offset) << 8) + (getByte0(offset + 1)));
        }
    }

    @Override
    public int getUnsignedShort(long offset) {
        return Short.toUnsignedInt(getShort(offset));
    }

    @Override
    public char getChar(long offset) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else if (offset + 1 >= size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset + 1));
        else {
            offset += arraysOffset;
            return (char) ((getByte0(offset) << 8) + (getByte0(offset + 1)));
        }
    }

    private int getInt0(long offset) {
        return (getByte0(offset) << 24) + (getByte0(offset + 1) << 16) + (getByte0(offset + 2) << 8) + (getByte0(offset + 3));
    }

    @Override
    public int getInt(long offset) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else if (offset + 3 >= size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset + 3));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else if (offset + 7 >= size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset + 7));
        else {
            offset += arraysOffset;
            return ((long) getInt0(offset) << 32) + getInt0(offset + 4);
        }
    }

    @Override
    public BigInteger getUnsignedLong(long offset) {
        return NumberUtils.toUnsignedBigInteger(getLong(offset));
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
        if (offset < 0 || offset >= size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else if (offset + 1 >= size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset + 1));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else if (offset + 3 >= size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset + 1));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else if (offset + 7 >= size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset + 1));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + length;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + length;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 1;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 1;
            array[i] = (short) ((getByte0(offset + l) << 8) + (getByte0(offset + l + 1)));
        }
    }

    @Override
    public void getShortArray(long offset, short[] array) {
        getShortArray(offset, array, 0, array.length);
    }

    @Override
    public void putShortArray(long offset, short[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 1;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 1;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 1;
            array[i] = (char) ((getByte0(offset + l) << 8) + (getByte0(offset + l + 1)));
        }
    }

    @Override
    public void getCharArray(long offset, char[] array) {
        getCharArray(offset, array, 0, array.length);
    }

    @Override
    public void putCharArray(long offset, char[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 1;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 2;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 2;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 3;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 3;
            array[i] = ((long) getInt0(offset + l) << 32) + getInt0(offset + l + 4);
        }
    }

    @Override
    public void getLongArray(long offset, long[] array) {
        getLongArray(offset, array, 0, array.length);
    }

    @Override
    public void putLongArray(long offset, long[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 3;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 2;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 2;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
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
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 3;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
        offset += arraysOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i << 3;
            array[i] = Double.longBitsToDouble(((long) getInt0(offset + l) << 32) + getInt0(offset + l + 4));
        }
    }

    @Override
    public void getDoubleArray(long offset, double[] array) {
        getDoubleArray(offset, array, 0, array.length);
    }

    @Override
    public void putDoubleArray(long offset, double[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + (long) length << 3;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
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
    public byte[] getZeroTerminatedByteArray(long offset) {
        return getZeroTerminatedByteArray(offset, Integer.MAX_VALUE);
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset, int maxLength) {
        long terminator = indexOf(offset + arraysOffset, '\0', maxLength);
        if (terminator == -1) return null;
        else {
            byte[] array = new byte[(int) terminator];
            getByteArray(offset, array);
            return array;
        }
    }

    @Override
    public String getZeroTerminatedString(long offset, Charset charset) {
        return new String(getZeroTerminatedByteArray(offset), charset);
    }

    @Override
    public String getZeroTerminatedString(long offset, int maxLength, Charset charset) {
        return new String(getZeroTerminatedByteArray(offset, maxLength), charset);
    }

    @Override
    public void putZeroTerminatedByteArray(long offset, byte[] array) {
        putZeroTerminatedByteArray(offset, array, 0, array.length);
    }

    @Override
    public void putZeroTerminatedByteArray(long offset, byte[] array, int index, int length) {
        putByte(offset + length + 1, '\0');
        putByteArray(offset, array, index, length);
    }

    @Override
    public void putZeroTerminatedString(long offset, String string, Charset charset) {
        putZeroTerminatedByteArray(offset, string.getBytes(charset));
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
        if (offset < 0 || offset >= this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
        else {
            BigInteger size = NumberUtils.toUnsignedBigInteger(count).add(BigInteger.valueOf(offset));
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
                    NumberUtils.toUnsignedBigInteger(offset).add(NumberUtils.toUnsignedBigInteger(length)));
        }
        else {
            long size = offset + length;
            if (size < 0 || size > this.size) {
                throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(size));
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
        if (offset < 0 || offset >= this.size) throw new IndexOutOfBoundsException("Index out of range: " + NumberUtils.toUnsignedBigInteger(offset));
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
    public Pointer reallocate(long size) throws IOException {
        try {
            HeapPointer pointer = new HeapPointer(getAllocator(), size);
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
    public void close() throws IOException {
    }

}
