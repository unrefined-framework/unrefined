package unrefined.core;

import unrefined.math.FastMath;
import unrefined.nio.Pointer;
import unrefined.util.Arrays;
import unrefined.util.foreign.Foreign;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static unrefined.nio.Allocator.SIZE_MAX;

public class HeapPointer extends Pointer {

    private interface NativeTypeAdapter {
        long get(Pointer pointer, long offset);
        void put(Pointer pointer, long offset, long value);
    }

    private static final NativeTypeAdapter NATIVE_TYPE_ADAPTER_64 = new NativeTypeAdapter() {
        @Override
        public long get(Pointer pointer, long offset) {
            return pointer.getLong(offset);
        }
        @Override
        public void put(Pointer pointer, long offset, long value) {
            pointer.putLong(offset, value);
        }
    };

    private static final NativeTypeAdapter NATIVE_TYPE_ADAPTER_32 = new NativeTypeAdapter() {
        @Override
        public long get(Pointer pointer, long offset) {
            return (long) pointer.getInt(offset) & 0xFFFFFFFFL;
        }
        @Override
        public void put(Pointer pointer, long offset, long value) {
            pointer.putInt(offset, (int) value);
        }
    };

    private static final NativeTypeAdapter NATIVE_INT_ADAPTER = Foreign.getInstance().nativeIntSize() == 8 ? NATIVE_TYPE_ADAPTER_64 : NATIVE_TYPE_ADAPTER_32;
    private static final NativeTypeAdapter NATIVE_LONG_ADAPTER = Foreign.getInstance().nativeLongSize() == 8 ? NATIVE_TYPE_ADAPTER_64 : NATIVE_TYPE_ADAPTER_32;
    private static final NativeTypeAdapter ADDRESS_ADAPTER = Foreign.getInstance().addressSize() == 8 ? NATIVE_TYPE_ADAPTER_64 : NATIVE_TYPE_ADAPTER_32;

    private volatile byte[][] array;
    private volatile ByteBuffer[] buffers;
    private final long size;
    private final long arrayOffset;
    private final long arrayLength;

    private final HeapPointer attachment;

    private final boolean hasMemory;

    public HeapPointer(long size) throws IOException {
        if (size < 0 || size > Arrays.ARRAY_2D_LENGTH_MAX) throw new IOException(new OutOfMemoryError("Requested array size too large"));
        else if (size <= Arrays.ARRAY_LENGTH_MAX) array = new byte[1][(int) size];
        else {
            int divide = (int) (size / Arrays.ARRAY_LENGTH_MAX);
            int reminder = (int) (size % Arrays.ARRAY_LENGTH_MAX);
            array = new byte[divide + (reminder > 0 ? 1 : 0)][];
            buffers = new ByteBuffer[array.length];
            for (int i = 0; i < array.length - 1; i ++) {
                array[i] = new byte[Arrays.ARRAY_LENGTH_MAX];
                buffers[i] = ByteBuffer.wrap(array[i]);
            }
            array[array.length - 1] = new byte[reminder > 0 ? reminder : Arrays.ARRAY_LENGTH_MAX];
            buffers[buffers.length - 1] = ByteBuffer.wrap(array[array.length - 1]);
        }
        this.attachment = null;
        this.size = size;
        this.arrayOffset = 0;
        this.arrayLength = size;
        hasMemory = true;
    }

    public HeapPointer(byte[] array, int offset, int length) {
        this.attachment = null;
        this.array = new byte[][] { array };
        this.buffers = new ByteBuffer[] { ByteBuffer.wrap(array, offset, length) };
        this.arrayOffset = offset;
        this.arrayLength = array.length;
        this.size = length;
        hasMemory = false;
    }

    public HeapPointer(byte[] array) {
        this(array, 0, array.length);
    }

    protected HeapPointer(HeapPointer attachment, long arraysLength, byte[][] array, long arrayOffset, long size) {
        this.attachment = attachment;
        this.array = array;
        int startIndex = (int) (arrayOffset / Arrays.ARRAY_LENGTH_MAX);
        int startOffset = (int) (arrayOffset % Arrays.ARRAY_LENGTH_MAX);
        int endIndex = (int) ((arrayOffset + size) / Arrays.ARRAY_LENGTH_MAX) - 1;
        int endLength = (int) ((arrayOffset + size) % Arrays.ARRAY_LENGTH_MAX);
        this.buffers = new ByteBuffer[endIndex + 2];
        buffers[startIndex] = ByteBuffer.wrap(array[startIndex], startOffset, array[startIndex].length - startOffset);
        for (int i = startIndex + 1; i < endIndex; i ++) {
            buffers[i] = ByteBuffer.wrap(array[i]);
        }
        if (endIndex > startIndex) buffers[endIndex] = ByteBuffer.wrap(array[endIndex], 0, endLength);
        this.size = size;
        this.arrayOffset = arrayOffset;
        this.arrayLength = arraysLength;
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
    public boolean hasArray() {
        return true;
    }

    @Override
    public Object array() {
        return array;
    }

    @Override
    public ByteBuffer[] toByteBuffers() {
        int i = 0;
        for (; i < buffers.length; i ++) {
            if (buffers[i] != null) break;
        }
        i ++;
        ByteBuffer[] byteBuffers = new ByteBuffer[buffers.length - i];
        for (int j = 0; j < byteBuffers.length; j ++) {
            byteBuffers[j] = buffers[j + i].duplicate();
        }
        return byteBuffers;
    }

    @Override
    public long arrayOffset() {
        return arrayOffset;
    }

    @Override
    public long arrayLength() {
        return arrayLength;
    }

    @Override
    public boolean getBoolean(long offset) {
        return getByte0(offset) != 0;
    }

    private byte getByte0(long offset) {
        return array[(int) (offset / Arrays.ARRAY_LENGTH_MAX)][(int) (offset % Arrays.ARRAY_LENGTH_MAX)];
    }

    @Override
    public byte getByte(long offset) {
        if (offset < 0 || offset >= size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else return getByte0(offset + arrayOffset);
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
            offset += arrayOffset;
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
            offset += arrayOffset;
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
            offset += arrayOffset;
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
            offset += arrayOffset;
            return (((long) getInt0(offset) & 0xFFFFFFFFL) << 32) + (getInt0(offset + 4) & 0xFFFFFFFFL);
        }
    }

    @Override
    public BigInteger getUnsignedLong(long offset) {
        return FastMath.unsign(getLong(offset));
    }

    @Override
    public long getNativeInt(long offset) {
        return NATIVE_INT_ADAPTER.get(this, offset);
    }

    @Override
    public BigInteger getUnsignedNativeInt(long offset) {
        return FastMath.unsign(NATIVE_INT_ADAPTER.get(this, offset));
    }

    @Override
    public long getNativeLong(long offset) {
        return NATIVE_LONG_ADAPTER.get(this, offset);
    }

    @Override
    public BigInteger getUnsignedNativeLong(long offset) {
        return FastMath.unsign(NATIVE_LONG_ADAPTER.get(this, offset));
    }

    @Override
    public long getAddress(long offset) {
        return ADDRESS_ADAPTER.get(this, offset);
    }

    @Override
    public float getFloat(long offset) {
        return Float.intBitsToFloat(getInt(offset));
    }

    @Override
    public double getDouble(long offset) {
        return Double.longBitsToDouble(getLong(offset));
    }

    @Override
    public void putBoolean(long offset, boolean value) {
        putByte0(offset, (byte) (value ? 1 : 0));
    }

    private void putByte0(long offset, byte value) {
        array[(int) (offset / Arrays.ARRAY_LENGTH_MAX)][(int) (offset % Arrays.ARRAY_LENGTH_MAX)] = value;
    }

    @Override
    public void putByte(long offset, byte value) {
        if (offset < 0 || offset >= size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else putByte0(offset + arrayOffset, value);
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
            offset += arrayOffset;
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

    @Override
    public void putChar(long offset, char value) {
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
            offset += arrayOffset;
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
            offset += arrayOffset;
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
        NATIVE_INT_ADAPTER.put(this, offset, value);
    }

    @Override
    public void putUnsignedNativeInt(long offset, BigInteger value) {
        NATIVE_INT_ADAPTER.put(this, offset, value.longValue());
    }

    @Override
    public void putNativeLong(long offset, long value) {
        NATIVE_LONG_ADAPTER.put(this, offset, value);
    }

    @Override
    public void putUnsignedNativeLong(long offset, BigInteger value) {
        NATIVE_LONG_ADAPTER.put(this, offset, value.longValue());
    }

    @Override
    public void putAddress(long offset, long value) {
        ADDRESS_ADAPTER.put(this, offset, value);
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
    public void getBooleanArray(long offset, boolean[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + length;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arrayOffset;
        for (int i = index; i < length; i ++) {
            putByte0(offset + i, (byte) (array[i] ? 1 : 0));
        }
    }

    @Override
    public void getBooleanArray(long offset, boolean[] array) {
        getBooleanArray(offset, array, 0, array.length);
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
            offset += arrayOffset;
            int startArray = (int) (offset / Arrays.ARRAY_LENGTH_MAX);
            int startIndex = (int) (offset % Arrays.ARRAY_LENGTH_MAX);
            int endArray = (int) (size / Arrays.ARRAY_LENGTH_MAX);
            if (startArray == endArray) System.arraycopy(this.array[startArray], startIndex, array, index, length);
            else {
                int endRange = (int) (size % Arrays.ARRAY_LENGTH_MAX);
                int startRange = length - endRange;
                System.arraycopy(this.array[startArray], startIndex, array, index, startRange);
                System.arraycopy(this.array[endArray], 0, array, index + startRange, endRange);
            }
        }
    }

    @Override
    public void getByteArray(long offset, byte[] array) {
        getByteArray(offset, array, 0, array.length);
    }

    @Override
    public void putBooleanArray(long offset, boolean[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        long size = offset + length;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arrayOffset;
        for (int i = index; i < length; i ++) {
            putByte0(offset + i, (byte) (array[i] ? 1 : 0));
        }
    }

    @Override
    public void putBooleanArray(long offset, boolean[] array) {
        putBooleanArray(offset, array, 0, array.length);
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
            offset += arrayOffset;
            int startArray = (int) (offset / Arrays.ARRAY_LENGTH_MAX);
            int startIndex = (int) (offset % Arrays.ARRAY_LENGTH_MAX);
            int endArray = (int) (size / Arrays.ARRAY_LENGTH_MAX);
            if (startArray == endArray) System.arraycopy(array, index, this.array[startArray], startIndex, length);
            else {
                int endRange = (int) (size % Arrays.ARRAY_LENGTH_MAX);
                int startRange = length - endRange;
                System.arraycopy(array, index, this.array[startArray], startIndex, startRange);
                System.arraycopy(array, index + startRange, this.array[endArray], 0, endRange);
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
        offset += arrayOffset;
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
        offset += arrayOffset;
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
        offset += arrayOffset;
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
        offset += arrayOffset;
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
        offset += arrayOffset;
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
        offset += arrayOffset;
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
        offset += arrayOffset;
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
        offset += arrayOffset;
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
        offset += arrayOffset;
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
        offset += arrayOffset;
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
        offset += arrayOffset;
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
        offset += arrayOffset;
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
    public void getNativeIntArray(long offset, long[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        int nativeIntSize = Foreign.getInstance().nativeIntSize();
        long size = offset + (long) length * nativeIntSize;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arrayOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i * nativeIntSize;
            array[i] = NATIVE_INT_ADAPTER.get(this, offset + l);
        }
    }

    @Override
    public void getNativeIntArray(long offset, long[] array) {
        getNativeIntArray(offset, array, 0, array.length);
    }

    @Override
    public void putNativeIntArray(long offset, long[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        int nativeIntSize = Foreign.getInstance().nativeIntSize();
        long size = offset + (long) length * nativeIntSize;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arrayOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i * nativeIntSize;
            NATIVE_INT_ADAPTER.put(this, offset + l, array[i]);
        }
    }

    @Override
    public void putNativeIntArray(long offset, long[] array) {
        putNativeIntArray(offset, array, 0, array.length);
    }

    @Override
    public void getNativeLongArray(long offset, long[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        int nativeLongSize = Foreign.getInstance().nativeLongSize();
        long size = offset + (long) length * nativeLongSize;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arrayOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i * nativeLongSize;
            NATIVE_INT_ADAPTER.put(this, offset + l, array[i]);
        }
    }

    @Override
    public void getNativeLongArray(long offset, long[] array) {
        getNativeLongArray(offset, array, 0, array.length);
    }

    @Override
    public void putNativeLongArray(long offset, long[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        int nativeLongSize = Foreign.getInstance().nativeLongSize();
        long size = offset + (long) length * nativeLongSize;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arrayOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i * nativeLongSize;
            array[i] = NATIVE_LONG_ADAPTER.get(this, offset + l);
        }
    }

    @Override
    public void putNativeLongArray(long offset, long[] array) {
        putNativeLongArray(offset, array, 0, array.length);
    }

    @Override
    public void getAddressArray(long offset, long[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        int addressSize = Foreign.getInstance().addressSize();
        long size = offset + (long) length * addressSize;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arrayOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i * addressSize;
            NATIVE_INT_ADAPTER.put(this, offset + l, array[i]);
        }
    }

    @Override
    public void getAddressArray(long offset, long[] array) {
        getAddressArray(offset, array, 0, array.length);
    }

    @Override
    public void putAddressArray(long offset, long[] array, int index, int length) {
        if (offset < 0) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(offset));
        else {
            if (index < 0) throw new ArrayIndexOutOfBoundsException(index);
            else if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            int size = index + length;
            if (size < 0 || size > array.length) throw new ArrayIndexOutOfBoundsException(size);
        }
        int addressSize = Foreign.getInstance().addressSize();
        long size = offset + (long) length * addressSize;
        if (size < 0 || size > this.size) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(size));
        offset += arrayOffset;
        long l;
        for (int i = index; i < length; i ++) {
            l = (long) i * addressSize;
            array[i] = ADDRESS_ADAPTER.get(this, offset + l);
        }
    }

    @Override
    public void putAddressArray(long offset, long[] array) {
        putAddressArray(offset, array, 0, array.length);
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
            if (java.util.Arrays.equals(terminator, buffer)) return length;
            else length ++;
            offset += size;
        }
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset) {
        return getZeroTerminatedByteArray(offset, Arrays.ARRAY_LENGTH_MAX);
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset, int maxLength) {
        return getZeroTerminatedByteArray(offset, maxLength, null);
    }

    @Override
    public byte[] getZeroTerminatedWideCharByteArray(long offset) {
        return getZeroTerminatedWideCharByteArray(offset, Arrays.ARRAY_LENGTH_MAX);
    }

    @Override
    public byte[] getZeroTerminatedWideCharByteArray(long offset, int maxLength) {
        return getZeroTerminatedByteArray(offset, maxLength, Foreign.getInstance().wideCharset());
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset, Charset charset) {
        return getZeroTerminatedByteArray(offset, Arrays.ARRAY_LENGTH_MAX, charset);
    }

    @Override
    public byte[] getZeroTerminatedByteArray(long offset, int maxLength, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        long stringLength = getZeroTerminatedStringLength(offset, maxLength, charset) * "\0".getBytes(charset).length;
        if (stringLength < 0 || stringLength > Arrays.ARRAY_LENGTH_MAX) stringLength = Arrays.ARRAY_LENGTH_MAX;
        byte[] array = new byte[(int) stringLength];
        getByteArray(offset, array);
        return array;
    }

    @Override
    public String getZeroTerminatedString(long offset) {
        return getZeroTerminatedString(offset, Arrays.ARRAY_LENGTH_MAX);
    }

    @Override
    public String getZeroTerminatedString(long offset, int maxLength) {
        return getZeroTerminatedString(offset, maxLength, null);
    }

    @Override
    public String getZeroTerminatedWideCharString(long offset) {
        return getZeroTerminatedWideCharString(offset, Arrays.ARRAY_LENGTH_MAX);
    }

    @Override
    public String getZeroTerminatedWideCharString(long offset, int maxLength) {
        return getZeroTerminatedString(offset, maxLength, Foreign.getInstance().wideCharset());
    }

    @Override
    public String getZeroTerminatedString(long offset, Charset charset) {
        return getZeroTerminatedString(offset, Arrays.ARRAY_LENGTH_MAX, charset);
    }

    @Override
    public String getZeroTerminatedString(long offset, int maxLength, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        long stringLength = getZeroTerminatedStringLength(offset, maxLength, charset) * "\0".getBytes(charset).length;
        if (stringLength < 0 || stringLength > Arrays.ARRAY_LENGTH_MAX) stringLength = Arrays.ARRAY_LENGTH_MAX;
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
        return slice(offset, arrayLength - offset);
    }

    @Override
    public Pointer slice(long offset, long size) {
        return new HeapPointer(this, arrayLength, array, arrayOffset + offset, size);
    }

    @Override
    public Pointer duplicate() {
        return new HeapPointer(this, arrayLength, array, arrayOffset, size);
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
            offset += arrayOffset;
            int startArray = (int) (offset / Arrays.ARRAY_LENGTH_MAX);
            int startIndex = (int) (offset % Arrays.ARRAY_LENGTH_MAX);
            int endArray = (int) (size.longValue() / Arrays.ARRAY_LENGTH_MAX);
            int endIndex = (int) (size.longValue() % Arrays.ARRAY_LENGTH_MAX);
            if (startArray == endArray) {
                dstPointer.putByteArray(dstOffset, array[startArray], startIndex, (int) count);
            }
            else {
                int startRange = Arrays.ARRAY_LENGTH_MAX - startIndex;
                dstPointer.putByteArray(dstOffset, array[startArray], startIndex, startRange);
                dstOffset += startRange;
                for (int array = startArray + 1; array < endArray; array ++) {
                    dstPointer.putByteArray(dstOffset, this.array[array], 0, Arrays.ARRAY_LENGTH_MAX);
                    dstOffset += Arrays.ARRAY_LENGTH_MAX;
                }
                dstPointer.putByteArray(dstOffset, array[endArray], 0, endIndex + 1);
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
        offset += arrayOffset;
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
            offset += arrayOffset;
            int startArray = (int) (offset / Arrays.ARRAY_LENGTH_MAX);
            int startIndex = (int) (offset % Arrays.ARRAY_LENGTH_MAX);
            int endArray = (int) (size.longValue() / Arrays.ARRAY_LENGTH_MAX);
            int endIndex = (int) (size.longValue() % Arrays.ARRAY_LENGTH_MAX);
            if (startArray == endArray) {
                for (int i = startIndex; i <= endIndex; i ++) {
                    if (array[startArray][i] == value) return (long) startArray * Arrays.ARRAY_LENGTH_MAX + i;
                }
            }
            else {
                for (int i = startIndex; i < Arrays.ARRAY_LENGTH_MAX; i ++) {
                    if (array[startArray][i] == value) return (long) startArray * Arrays.ARRAY_LENGTH_MAX + i;
                }
                for (int array = startArray + 1; array < endArray; array ++) {
                    for (int i = 0; i < Arrays.ARRAY_LENGTH_MAX; i ++) {
                        if (this.array[array][i] == value) return (long) array * Arrays.ARRAY_LENGTH_MAX + i;
                    }
                }
                for (int i = 0; i <= endIndex; i ++) {
                    if (array[endArray][i] == value) return (long) endArray * Arrays.ARRAY_LENGTH_MAX + i;
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
            else if (maxLength > Arrays.ARRAY_2D_LENGTH_MAX) throw new IndexOutOfBoundsException("Index out of range: " + FastMath.unsign(maxLength));
            byte[] buffer = new byte[valueLength];
            for (long searched = 0; searched < size; searched ++) {
                getByteArray(searched, buffer, valueOffset, valueLength);
                if (java.util.Arrays.equals(value, buffer)) return searched;
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
        return size == 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Pointer)) return false;

        Pointer that = (Pointer) object;

        if (isNullPointer() && that.isNullPointer()) return true;
        else if (that.hasArray()) {
            return array() == that.array() && size() == that.size() && arrayOffset() == that.arrayOffset();
        }
        else return false;
    }

    @Override
    public int hashCode() {
        int result = array.hashCode();
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (int) (arrayOffset ^ (arrayOffset >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName() + '@' + Integer.toHexString(hashCode())
                + '{' +
                "hasMemory=" + hasMemory +
                ", array=" + array.toString() +
                ", arrayOffset=" + arrayOffset +
                '}';
    }

    @Override
    public void close() throws IOException {
        array = null;
    }

}
