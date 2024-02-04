package unrefined.app;

import unrefined.context.Environment;
import unrefined.util.NotInstantiableError;
import unrefined.util.QuietCloseable;
import unrefined.util.signal.Signal;

import java.io.File;
import java.lang.ref.WeakReference;

public abstract class Runtime {

    private static volatile Runtime INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Runtime getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.runtime", Runtime.class);
        }
        return INSTANCE;
    }

    public static final class Flag {
        private Flag() {
            throw new NotInstantiableError(Flag.class);
        }
        public static final int BASE  = 0;
        public static final int MEDIA = 1;
        public static final int ALL   = removeUnusedBits(0xFFFFFFFF);
        public static int removeUnusedBits(int flags) {
            return flags << 31 >>> 31;
        }
        public static String toString(int flags) {
            flags = removeUnusedBits(flags);
            StringBuilder builder = new StringBuilder("[BASE");
            if ((flags & MEDIA) != 0) builder.append(", MEDIA");
            builder.append("]");
            return builder.toString();
        }
    }

    private static final java.lang.Runtime RUNTIME = java.lang.Runtime.getRuntime();

    public int availableProcessors() {
        return RUNTIME.availableProcessors();
    }

    private final Signal<Runnable> onShutdown = Signal.ofRunnable();

    public Signal<Runnable> onShutdown() {
        return onShutdown;
    }

    public void exit(int status) {
        RUNTIME.exit(status);
    }

    public void halt(int status) {
        RUNTIME.halt(status);
    }

    public long managedMemory() {
        return RUNTIME.totalMemory() - RUNTIME.freeMemory();
    }

    public long committedHeap() {
        return RUNTIME.totalMemory();
    }
    public long freeHeap() {
        return RUNTIME.freeMemory();
    }
    public long maximumHeap() {
        return RUNTIME.maxMemory();
    }

    /**
     * Gets the size in bytes of a native memory page (whatever that is).
     * This value will always be a power of two.
     */
    public abstract int memoryPageSize();

    public abstract long processId();

    /**
     * This method guarantees that garbage collection is
     * done unlike <code>{@link System#gc()}</code>
     */
    public void collect() {
        Object object = new Object();
        WeakReference<Object> reference = new WeakReference<>(object);
        try {
            object = null;
        }
        finally {
            while (reference.get() != null) RUNTIME.gc();
        }
    }

    public boolean weakCollect() {
        Object object = new Object();
        WeakReference<Object> reference = new WeakReference<>(object);
        try {
            object = null;
        }
        finally {
            RUNTIME.gc();
        }
        return reference.get() == null;
    }

    /**
     * This method guarantees that garbage collection is
     * done after the JVM shutdown is initialized
     */
    public abstract void collectOnExit();

    public int identityHashCode(Object object) {
        return System.identityHashCode(object);
    }

    public abstract int arrayIndexScale(Class<?> clazz);

    public File getCurrentDirectory() {
        return new File(System.getProperty("user.dir"));
    }

    public void setCurrentDirectory(File file) {
        System.setProperty("user.dir", file.getAbsolutePath());
    }

    public abstract void sneakyThrows(Throwable throwable);
    public abstract long sizeOf(Object object);
    public long sizeOf(boolean x) {
        return 4;
    }
    public long sizeOf(byte x) {
        return 1;
    }
    public long sizeOf(short x) {
        return 2;
    }
    public long sizeOf(char x) {
        return 2;
    }
    public long sizeOf(int x) {
        return 4;
    }
    public long sizeOf(long x) {
        return 8;
    }
    public long sizeOf(float x) {
        return 4;
    }
    public long sizeOf(double x) {
        return 8;
    }
    public abstract long sizeOfType(Class<?> clazz);
    public abstract int referenceSize();

    private static final class ReferenceHolder implements QuietCloseable {
        public volatile Object object;
        public ReferenceHolder(Object object) {
            this.object = object;
        }
        @Override
        public void close() {
            object = null;
        }
    }

    private static final ThreadLocal<WeakReference<QuietCloseable>> FENCE = new ThreadLocal<>();
    public QuietCloseable reachabilityScope(Object object) {
        FENCE.set(new WeakReference<>(new ReferenceHolder(object)));
        return FENCE.get().get();
    }

}
