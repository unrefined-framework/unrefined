package unrefined.util;

import unrefined.context.Environment;
import unrefined.util.concurrent.Producer;
import unrefined.util.concurrent.worker.Worker;
import unrefined.util.signal.Dispatcher;

import java.util.Map;
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

    public Thread current() {
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

    public abstract void park(boolean absolute, long time);
    public void park(long timestamp) {
        park(true, timestamp);
    }
    public void park(long timeout, TimeUnit timeUnit) {
        park(false, timeUnit.toMillis(timeout));
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

    public Thread createThread(String name, boolean daemon, int priority, Runnable runnable) {
        Thread thread = new Thread(runnable, name);
        thread.setDaemon(daemon);
        thread.setPriority(priority);
        return thread;
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

    public ThreadLocalStack createThreadLocalStack(int size) {
        return new ThreadLocalStack(size);
    }

    public ThreadLocalStack createThreadLocalStack() {
        return new ThreadLocalStack();
    }

}
