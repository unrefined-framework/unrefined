package unrefined.app;

import unrefined.context.Container;
import unrefined.context.ContainerListener;
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
        public static final int BASE     = 0;
        public static final int GRAPHICS = 1;
        public static final int AUDIO    = 1 << 1;
        public static final int MEDIA    = GRAPHICS | AUDIO;
        public static final int ALL      = removeUnusedBits(0xFFFFFFFF);
        public static int removeUnusedBits(int flags) {
            return flags << 30 >>> 30;
        }
        public static String toString(int flags) {
            flags = removeUnusedBits(flags);
            StringBuilder builder = new StringBuilder("[BASE");
            if ((flags & GRAPHICS) != 0) builder.append(", GRAPHICS");
            if ((flags & AUDIO) != 0) builder.append(", AUDIO");
            builder.append("]");
            return builder.toString();
        }
    }

    public abstract Container createContainer(ContainerListener containerListener);
    public Container createContainer() {
        return createContainer(null);
    }

    private volatile String APP_VENDOR;
    private final Object APP_VENDOR_LOCK = new Object();
    public String getApplicationVendor() {
        if (APP_VENDOR == null) synchronized (APP_VENDOR_LOCK) {
            if (APP_VENDOR == null) APP_VENDOR = Environment.properties.getProperty("unrefined.app.vendor");
        }
        return APP_VENDOR;
    }
    private volatile String APP_NAME;
    private final Object APP_NAME_LOCK = new Object();
    public String getApplicationName() {
        if (APP_NAME == null) synchronized (APP_NAME_LOCK) {
            if (APP_NAME == null) APP_NAME = Environment.properties.getProperty("unrefined.app.name");
        }
        return APP_NAME;
    }
    private volatile String APP_VERSION_NAME;
    private final Object APP_VERSION_NAME_LOCK = new Object();
    public String getApplicationVersionName() {
        if (APP_VERSION_NAME == null) synchronized (APP_VERSION_NAME_LOCK) {
            if (APP_VERSION_NAME == null) APP_VERSION_NAME = Environment.properties.getProperty("unrefined.app.version.name");
        }
        return APP_VERSION_NAME;
    }
    private volatile String APP_VERSION_CODE;
    private final Object APP_VERSION_CODE_LOCK = new Object();
    public String getApplicationVersionCode() {
        if (APP_VERSION_CODE == null) synchronized (APP_VERSION_CODE_LOCK) {
            if (APP_VERSION_CODE == null) APP_VERSION_CODE = Environment.properties.getProperty("unrefined.app.version.code");
        }
        return APP_VERSION_CODE;
    }
    private volatile String APP_PACKAGE;
    private final Object APP_PACKAGE_LOCK = new Object();
    public String getApplicationPackage() {
        if (APP_PACKAGE == null) synchronized (APP_PACKAGE_LOCK) {
            if (APP_PACKAGE == null) APP_PACKAGE = Environment.properties.getProperty("unrefined.app.package");
        }
        return APP_PACKAGE;
    }
    private volatile String APP_IMPLEMENTER;
    private final Object APP_IMPLEMENTER_LOCK = new Object();
    public String getApplicationImplementer() {
        if (APP_IMPLEMENTER == null) synchronized (APP_IMPLEMENTER_LOCK) {
            if (APP_IMPLEMENTER == null) APP_IMPLEMENTER = Environment.properties.getProperty("unrefined.app.implementer");
        }
        return APP_IMPLEMENTER;
    }

    public abstract Preferences getPreferences(String name);
    public abstract boolean deletePreferences(String name);

    private static final java.lang.Runtime RUNTIME = java.lang.Runtime.getRuntime();

    public int availableProcessors() {
        return RUNTIME.availableProcessors();
    }

    private final Signal<Runnable> onShutdown = Signal.ofRunnable();

    public Signal<Runnable> onShutdown() {
        return onShutdown;
    }

    public abstract boolean isShutdownThread(Thread thread);
    public boolean isShutdownThread() {
        return isShutdownThread(Thread.currentThread());
    }

    public void exit(int status) {
        RUNTIME.exit(status);
    }

    public void halt(int status) {
        RUNTIME.halt(status);
    }

    public long usedHeap() {
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

    public void suggestCollect() {
        RUNTIME.gc();
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

    public abstract File getConfigDirectory();
    public abstract File getCacheDirectory();
    public abstract File getFilesDirectory(String type);
    public abstract File getHomeDirectory();
    public abstract File getTempDirectory();
    public abstract File getCurrentDirectory();

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

    public final Environment system = Environment.system;
    public final Environment properties = Environment.properties;
    public final Environment global = Environment.global;
    public final Environment threadLocal = Environment.threadLocal;

}
