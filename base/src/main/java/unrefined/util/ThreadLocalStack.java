package unrefined.util;

import unrefined.context.Environment;

import java.nio.ByteBuffer;

public final class ThreadLocalStack {

    private static final int STACK_SIZE = Environment.properties.parseIntProperty("unrefined.util.thread.stack", 1024 * 1024);

    private final int size;

    private final ThreadLocal<IndexBuffer> STACK_CACHE;

    public ThreadLocalStack(int size) {
        if (size <= 0) throw new IllegalArgumentException("size <= 0");
        this.size = size;
        STACK_CACHE = new ProducerThreadLocal<>(() -> new IndexBuffer(size));
    }

    public ThreadLocalStack() {
        this(STACK_SIZE);
    }

    public int size() {
        return size;
    }

    private static final class IndexBuffer {
        private final ByteBuffer buffer;
        public IndexBuffer(int size) {
            buffer = ByteBuffer.allocate(size);
        }
        private int index;
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

    public void pushBoolean(boolean value) {
        STACK_CACHE.get().pushBoolean(value);
    }
    public void pushByte(byte value) {
        STACK_CACHE.get().pushByte(value);
    }
    public void pushChar(char value) {
        STACK_CACHE.get().pushChar(value);
    }
    public void pushDouble(double value) {
        STACK_CACHE.get().pushDouble(value);
    }
    public void pushFloat(float value) {
        STACK_CACHE.get().pushFloat(value);
    }
    public void pushShort(short value) {
        STACK_CACHE.get().pushShort(value);
    }
    public void pushInt(int value) {
        STACK_CACHE.get().pushInt(value);
    }
    public void pushLong(long value) {
        STACK_CACHE.get().pushLong(value);
    }
    public boolean popBoolean() {
        return STACK_CACHE.get().popBoolean();
    }
    public byte popByte() {
        return STACK_CACHE.get().popByte();
    }
    public char popChar() {
        return STACK_CACHE.get().popChar();
    }
    public double popDouble() {
        return STACK_CACHE.get().popDouble();
    }
    public float popFloat() {
        return STACK_CACHE.get().popFloat();
    }
    public short popShort() {
        return STACK_CACHE.get().popShort();
    }
    public int popInt() {
        return STACK_CACHE.get().popInt();
    }
    public long popLong() {
        return STACK_CACHE.get().popLong();
    }

}
