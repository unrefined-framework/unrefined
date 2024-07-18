package unrefined.nio;

import unrefined.util.NotInstantiableError;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public final class Buffers {
    
    private Buffers() {
        throw new NotInstantiableError(Buffers.class);
    }
    
    public static <T extends Buffer> T rewind(T buffer) {
        ((Buffer) buffer).rewind();
        return buffer;
    }

    public static <T extends Buffer> T flip(T buffer) {
        ((Buffer) buffer).flip();
        return buffer;
    }

    public static <T extends Buffer> T mark(T buffer) {
        ((Buffer) buffer).mark();
        return buffer;
    }

    public static <T extends Buffer> T position(T buffer, int newPosition) {
        ((Buffer) buffer).position(newPosition);
        return buffer;
    }

    public static <T extends Buffer> T limit(T buffer, int newLimit) {
        ((Buffer) buffer).limit(newLimit);
        return buffer;
    }

    public static <T extends Buffer> T clear(T buffer) {
        ((Buffer) buffer).clear();
        return buffer;
    }

    public static <T extends Buffer> T reset(T buffer) {
        ((Buffer) buffer).reset();
        return buffer;
    }

    public static ByteBuffer slice(ByteBuffer buffer, int index, int length) {
        return limit(position(buffer.duplicate(), index), index + length).slice();
    }

    public static CharBuffer slice(CharBuffer buffer, int index, int length) {
        return limit(position(buffer.duplicate(), index), index + length).slice();
    }

    public static ShortBuffer slice(ShortBuffer buffer, int index, int length) {
        return limit(position(buffer.duplicate(), index), index + length).slice();
    }

    public static IntBuffer slice(IntBuffer buffer, int index, int length) {
        return limit(position(buffer.duplicate(), index), index + length).slice();
    }

    public static LongBuffer slice(LongBuffer buffer, int index, int length) {
        return limit(position(buffer.duplicate(), index), index + length).slice();
    }

    public static FloatBuffer slice(FloatBuffer buffer, int index, int length) {
        return limit(position(buffer.duplicate(), index), index + length).slice();
    }

    public static DoubleBuffer slice(DoubleBuffer buffer, int index, int length) {
        return limit(position(buffer.duplicate(), index), index + length).slice();
    }
    
}
