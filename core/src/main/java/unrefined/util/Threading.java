package unrefined.util;

import unrefined.context.Environment;
import unrefined.util.concurrent.Producer;
import unrefined.util.concurrent.worker.Worker;
import unrefined.util.signal.Dispatcher;

import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class Threading {

    private static volatile Threading INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Threading getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.threading", Threading.class);
        }
        return INSTANCE;
    }

    /**
     * The minimum priority that a thread can have.
     */
    public static final int MIN_PRIORITY = Thread.MIN_PRIORITY;

    /**
     * The default priority that is assigned to a thread.
     */
    public static final int NORM_PRIORITY = Thread.NORM_PRIORITY;

    /**
     * The maximum priority that a thread can have.
     */
    public static final int MAX_PRIORITY = Thread.MAX_PRIORITY;
    
    public PlatformThreadBuilder buildPlatformThread() {
        if (!isPlatformThreadSupported()) throw new UnsupportedOperationException("Platform thread not supported");
        return new PlatformThreadBuilder();
    }

    public static class PlatformThreadBuilder {
        private PlatformThreadBuilder() {
        }
        private ThreadGroup group = null;
        private String name = null;
        private boolean daemon = false;
        private int priority = -1;
        private Thread.UncaughtExceptionHandler exceptionHandler = null;
        public PlatformThreadBuilder group(ThreadGroup group) {
            this.group = group;
            return this;
        }
        public PlatformThreadBuilder name(String name) {
            this.name = name;
            return this;
        }
        public PlatformThreadBuilder daemon(boolean daemon) {
            this.daemon = daemon;
            return this;
        }
        public PlatformThreadBuilder priority(int priority) {
            this.priority = priority;
            return this;
        }
        public PlatformThreadBuilder onUncaughtException(Thread.UncaughtExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }
        public Thread unstarted(Runnable task) {
            return Threading.getInstance().createPlatformThread(group, task, name, daemon, priority, exceptionHandler);
        }
        public Thread start(Runnable task) {
            Thread thread = Threading.getInstance().createPlatformThread(group, task, name, daemon, priority, exceptionHandler);
            thread.start();
            return thread;
        }
        public ThreadFactory factory() {
            return this::unstarted;
        }
    }

    public VirtualThreadBuilder buildVirtualThread() {
        if (!isVirtualThreadSupported()) throw new UnsupportedOperationException("Virtual thread not supported");
        return new VirtualThreadBuilder();
    }

    public static class VirtualThreadBuilder {
        private VirtualThreadBuilder() {
        }
        private String name = null;
        private Thread.UncaughtExceptionHandler exceptionHandler = null;
        public VirtualThreadBuilder name(String name) {
            this.name = name;
            return this;
        }
        public VirtualThreadBuilder onUncaughtException(Thread.UncaughtExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }
        public Thread unstarted(Runnable task) {
            return Threading.getInstance().createVirtualThread(task, name, exceptionHandler);
        }
        public Thread start(Runnable task) {
            Thread thread = Threading.getInstance().createVirtualThread(task, name, exceptionHandler);
            thread.start();
            return thread;
        }
        public ThreadFactory factory() {
            return this::unstarted;
        }
    }
    
    public abstract boolean isPlatformThreadSupported();
    public abstract boolean isVirtualThreadSupported();

    public ThreadGroup createThreadGroup(String name) {
        return new ThreadGroup(name);
    }

    public ThreadGroup createThreadGroup(ThreadGroup parent, String name) {
        return new ThreadGroup(parent, name);
    }
    
    public abstract Thread createPlatformThread(ThreadGroup group, Runnable task, String name, boolean daemon, int priority, Thread.UncaughtExceptionHandler exceptionHandler);
    public Thread createPlatformThread(ThreadGroup group, Runnable task, String name, boolean daemon, int priority) {
        return createPlatformThread(group, task, name, daemon, priority, null);
    }
    public Thread createPlatformThread(Runnable task, String name, boolean daemon, int priority) {
        return createPlatformThread(null, task, name, daemon, priority);
    }
    public Thread createPlatformThread(Runnable task, String name, boolean daemon) {
        return createPlatformThread(task, name, daemon, -1);
    }
    public Thread createPlatformThread(Runnable task, boolean daemon) {
        return createPlatformThread(task, null, daemon);
    }
    public Thread createPlatformThread(Runnable task) {
        return createPlatformThread(task, false);
    }
    public abstract Thread createVirtualThread(Runnable task, String name, Thread.UncaughtExceptionHandler exceptionHandler);
    public Thread createVirtualThread(Runnable task, String name) {
        return createVirtualThread(task, name, null);
    }
    public Thread createVirtualThread(Runnable task) {
        return createVirtualThread(task, null);
    }
    
    public abstract boolean isVirtualThread(Thread thread);
    public abstract boolean isPlatformThread(Thread thread);

    public Thread currentThread() {
        return Thread.currentThread();
    }

    public void yield() {
        Thread.yield();
    }

    public boolean interrupted() {
        return Thread.interrupted();
    }

    public int activeThreadCount() {
        return Thread.activeCount();
    }

    public Map<Thread, StackTraceElement[]> stackTraces() {
        return Thread.getAllStackTraces();
    }

    public boolean isSynchronizing(Object object) {
        return Thread.holdsLock(object);
    }


    public abstract void park(Object blocker);
    public void park() {
        park(null);
    }
    public abstract void parkUntil(Object blocker, long timestamp);
    public void parkUntil(long timestamp) {
        parkUntil(null, timestamp);
    }
    public abstract void park(Object blocker, long timeout, TimeUnit timeUnit);
    public void park(long timeout, TimeUnit timeUnit) {
        park(null, timeout, timeUnit);
    }
    public void park(Object blocker, long timeout) {
        park(blocker, timeout, null);
    }
    public void park(long timeout) {
        park(null, timeout, null);
    }
    public abstract void unpark(Thread thread);
    public void unpark() {
        unpark(Thread.currentThread());
    }

    public abstract boolean isMainThread(Thread thread);
    public boolean isMainThread() {
        return isMainThread(Thread.currentThread());
    }

    public boolean isEventThread(Thread thread) {
        return Dispatcher.defaultInstance().isDispatchThread(thread);
    }

    public boolean isEventThread() {
        return Dispatcher.defaultInstance().isDispatchThread();
    }

    public abstract Worker createWorker(String name, Class<?> clazz);
    public Worker createWorker(Class<?> clazz) {
        return createWorker(null, clazz);
    }

    public <T> ThreadLocal<T> createThreadLocal() {
        return new ThreadLocal<>();
    }

    public <T> ThreadLocal<T> createThreadLocal(Producer<? extends T> producer) {
        return new ProducerThreadLocal<>(producer);
    }

    public ThreadLocalBuffer createThreadLocalBuffer() {
        return new ThreadLocalBuffer();
    }

}
