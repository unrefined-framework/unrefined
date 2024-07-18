package unrefined.nio;

import unrefined.util.ProducerThreadLocal;
import unrefined.util.concurrent.ConcurrentHashSet;
import unrefined.util.concurrent.Producer;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class StackBuffer implements Closeable {

    public static StackBuffer allocate(int size) {
        return new Heap(size);
    }

    public static StackBuffer allocateDirect(int size) {
        return new Direct(size);
    }

    public static StackBuffer ofThreadLocal(int size) {
        return new ThreadLocal(size);
    }

    public static StackBuffer ofDirectThreadLocal(int size) {
        return new DirectThreadLocal(size);
    }

    private static class Heap extends StackBuffer {
        private volatile ByteBuffer buffer;
        public Heap(int size) {
            buffer = ByteBuffer.allocate(size);
        }
        @Override
        protected ByteBuffer buffer() {
            if (buffer == null) throw new IllegalStateException("Already closed");
            return buffer;
        }
        @Override
        public void close() throws IOException {
            buffer = null;
        }
    }

    private static class Direct extends StackBuffer {
        private volatile ByteBuffer buffer;
        public Direct(int size) {
            buffer = ByteBuffer.allocateDirect(size);
        }
        @Override
        protected ByteBuffer buffer() {
            if (closed.get()) throw new IllegalStateException("Already closed");
            return buffer;
        }
        private final AtomicBoolean closed = new AtomicBoolean(false);
        @Override
        public void close() throws IOException {
            if (closed.compareAndSet(false, true)) {
                Allocator.getInstance().invokeCleaner(buffer);
                buffer = null;
            }
        }
    }

    private static class ThreadLocal extends StackBuffer {
        private volatile Producer<ByteBuffer> buffer;
        public ThreadLocal(int size) {
            buffer = new ProducerThreadLocal<>(() -> ByteBuffer.allocate(size))::get;
        }
        @Override
        protected ByteBuffer buffer() {
            if (buffer == null) throw new IllegalStateException("Already closed");
            return buffer.get();
        }
        @Override
        public void close() throws IOException {
            buffer = null;
        }
    }

    private static class DirectThreadLocal extends StackBuffer {
        private final Set<ByteBuffer> buffers = new ConcurrentHashSet<>();
        private volatile Producer<ByteBuffer> buffer;
        public DirectThreadLocal(int size) {
            buffer = new ProducerThreadLocal<>(() -> {
                ByteBuffer buffer = ByteBuffer.allocateDirect(size);
                buffers.add(buffer);
                return buffer;
            })::get;
        }
        @Override
        protected ByteBuffer buffer() {
            if (closed.get()) throw new IllegalStateException("Already closed");
            return buffer.get();
        }
        private final AtomicBoolean closed = new AtomicBoolean(false);
        @Override
        public void close() throws IOException {
            if (closed.compareAndSet(false, true)) {
                synchronized (buffers) {
                    for (ByteBuffer buffer : buffers) {
                        Allocator.getInstance().invokeCleaner(buffer);
                    }
                }
                buffers.clear();
                buffer = null;
            }
        }
    }

    protected abstract ByteBuffer buffer();

    private int index;

    private void ensureMemory(int size) {
        if (index + size >= buffer().capacity()) throw new StackOverflowError("Out of memory");
    }

    private void ensureIndex() {
        if (index < 0) throw new IllegalStateException("Illegal stack");
    }

    public void pushBoolean(boolean value) {
        ensureMemory(1);
        buffer().put(index, value ? (byte) 1 : (byte) 0);
        index += 1;
    }

    public void pushByte(byte value) {
        ensureMemory(1);
        buffer().put(index, value);
        index += 1;
    }

    public void pushChar(char value) {
        ensureMemory(2);
        buffer().putChar(index, value);
        index += 2;
    }

    public void pushDouble(double value) {
        ensureMemory(8);
        buffer().putDouble(index, value);
        index += 8;
    }

    public void pushFloat(float value) {
        ensureMemory(4);
        buffer().putFloat(index, value);
        index += 4;
    }

    public void pushShort(short value) {
        ensureMemory(2);
        buffer().putShort(index, value);
        index += 2;
    }

    public void pushInt(int value) {
        ensureMemory(4);
        buffer().putInt(index, value);
        index += 4;
    }

    public void pushLong(long value) {
        ensureMemory(8);
        buffer().putLong(index, value);
        index += 8;
    }

    public boolean popBoolean() {
        index -= 1;
        ensureIndex();
        return buffer().get(index) != 0;
    }

    public byte popByte() {
        index -= 1;
        ensureIndex();
        return buffer().get(index);
    }

    public char popChar() {
        index -= 2;
        ensureIndex();
        return buffer().getChar(index);
    }

    public double popDouble() {
        index -= 8;
        ensureIndex();
        return buffer().getDouble(index);
    }

    public float popFloat() {
        index -= 4;
        ensureIndex();
        return buffer().getFloat(index);
    }

    public short popShort() {
        index -= 2;
        ensureIndex();
        return buffer().getShort(index);
    }

    public int popInt() {
        index -= 4;
        ensureIndex();
        return buffer().getInt(index);
    }

    public long popLong() {
        index -= 8;
        ensureIndex();
        return buffer().getLong(index);
    }

    public ByteBuffer toByteBuffer() {
        return buffer().duplicate();
    }

    public boolean isDirect() {
        return buffer().isDirect();
    }

    public int size() {
        return buffer().capacity();
    }

}
