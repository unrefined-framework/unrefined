package unrefined.util;

import unrefined.context.Environment;

import java.nio.ByteBuffer;

public final class ThreadStack {

    private ThreadStack() {
        throw new NotInstantiableError(ThreadStack.class);
    }

    private static final ThreadLocal<IndexBuffer> CACHE = new ProducerThreadLocal<>(IndexBuffer::new);
    private static final int STACK_SIZE = Environment.properties().parseIntProperty("unrefined.util.thread.stack", 1024 * 1024);
    private static final class IndexBuffer {
        private int index;
        private final ByteBuffer buffer = ByteBuffer.allocate(STACK_SIZE);
        private void ensureMemory(int size) {
            if (index + size >= buffer.capacity()) throw new StackOverflowError("Out of memory");
        }
        private void ensureIndex() {
            if (index < 0) throw new IllegalStateException("Illegal stack");
        }
        public void pushBoolean(boolean value) {
            ensureMemory(1);
            buffer.put(index, value ? (byte) 1 : (byte) 0);
            index += 1;
        }
        public void pushByte(byte value) {
            ensureMemory(1);
            buffer.put(index, value);
            index += 1;
        }
        public void pushChar(char value) {
            ensureMemory(2);
            buffer.putChar(index, value);
            index += 2;
        }
        public void pushDouble(double value) {
            ensureMemory(8);
            buffer.putDouble(index, value);
            index += 8;
        }
        public void pushFloat(float value) {
            ensureMemory(4);
            buffer.putFloat(index, value);
            index += 4;
        }
        public void pushShort(short value) {
            ensureMemory(2);
            buffer.putShort(index, value);
            index += 2;
        }
        public void pushInt(int value) {
            ensureMemory(4);
            buffer.putInt(index, value);
            index += 4;
        }
        public void pushLong(long value) {
            ensureMemory(8);
            buffer.putLong(index, value);
            index += 8;
        }
        public boolean popBoolean() {
            index -= 1;
            ensureIndex();
            return buffer.get(index) != 0;
        }
        public byte popByte() {
            index -= 1;
            ensureIndex();
            return buffer.get(index);
        }
        public char popChar() {
            index -= 2;
            ensureIndex();
            return buffer.getChar(index);
        }
        public double popDouble() {
            index -= 8;
            ensureIndex();
            return buffer.getDouble(index);
        }
        public float popFloat() {
            index -= 4;
            ensureIndex();
            return buffer.getFloat(index);
        }
        public short popShort() {
            index -= 2;
            ensureIndex();
            return buffer.getShort(index);
        }
        public int popInt() {
            index -= 4;
            ensureIndex();
            return buffer.getInt(index);
        }
        public long popLong() {
            index -= 8;
            ensureIndex();
            return buffer.getLong(index);
        }
    }

    public static void pushBoolean(boolean value) {
        CACHE.get().pushBoolean(value);
    }
    public static void pushByte(byte value) {
        CACHE.get().pushByte(value);
    }
    public static void pushChar(char value) {
        CACHE.get().pushChar(value);
    }
    public static void pushDouble(double value) {
        CACHE.get().pushDouble(value);
    }
    public static void pushFloat(float value) {
        CACHE.get().pushFloat(value);
    }
    public static void pushShort(short value) {
        CACHE.get().pushShort(value);
    }
    public static void pushInt(int value) {
        CACHE.get().pushInt(value);
    }
    public static void pushLong(long value) {
        CACHE.get().pushLong(value);
    }
    public static boolean popBoolean() {
        return CACHE.get().popBoolean();
    }
    public static byte popByte() {
        return CACHE.get().popByte();
    }
    public static char popChar() {
        return CACHE.get().popChar();
    }
    public static double popDouble() {
        return CACHE.get().popDouble();
    }
    public static float popFloat() {
        return CACHE.get().popFloat();
    }
    public static short popShort() {
        return CACHE.get().popShort();
    }
    public static int popInt() {
        return CACHE.get().popInt();
    }
    public static long popLong() {
        return CACHE.get().popLong();
    }

}
