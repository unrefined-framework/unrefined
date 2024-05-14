package unrefined.runtime;

import android.os.Looper;
import unrefined.math.FastMath;
import unrefined.util.Threading;
import unrefined.util.concurrent.worker.Worker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class AndroidThreading extends Threading {

    @Override
    public boolean isPlatformThreadSupported() {
        return true;
    }

    @Override
    public boolean isVirtualThreadSupported() {
        return false;
    }

    @Override
    public Thread createPlatformThread(ThreadGroup group, Runnable task, String name, boolean daemon, int priority, Thread.UncaughtExceptionHandler exceptionHandler) {
        Thread thread;
        if (name == null) thread = new Thread(group, task);
        else thread = new Thread(group, task, name);
        thread.setPriority(priority < 0 ? NORM_PRIORITY : FastMath.clamp(priority, MIN_PRIORITY, MAX_PRIORITY));
        thread.setDaemon(daemon);
        if (exceptionHandler != null) thread.setUncaughtExceptionHandler(exceptionHandler);
        return thread;
    }

    @Override
    public Thread createVirtualThread(Runnable task, String name, Thread.UncaughtExceptionHandler exceptionHandler) {
        throw new UnsupportedOperationException("Virtual thread not supported");
    }

    @Override
    public boolean isVirtualThread(Thread thread) {
        return false;
    }

    @Override
    public boolean isPlatformThread(Thread thread) {
        return true;
    }

    @Override
    public void park(Object blocker) {
        if (blocker == null) LockSupport.park();
        else LockSupport.park(blocker);
    }

    @Override
    public void parkUntil(Object blocker, long timestamp) {
        if (blocker == null) LockSupport.parkUntil(timestamp);
        else LockSupport.parkUntil(blocker, timestamp);
    }

    @Override
    public void park(Object blocker, long timeout, TimeUnit timeUnit) {
        if (timeUnit == null) timeUnit = TimeUnit.MILLISECONDS;
        if (blocker == null) LockSupport.parkNanos(timeUnit.toNanos(timeout));
        else LockSupport.parkNanos(blocker, timeUnit.toNanos(timeout));
    }

    @Override
    public void unpark(Thread thread) {
        LockSupport.unpark(Objects.requireNonNull(thread));
    }

    @Override
    public boolean isMainThread(Thread thread) {
        return Looper.getMainLooper().getThread() == thread;
    }

    @Override
    public Worker createWorker(String name, Class<?> clazz) {
        return new BaseWorker(name, clazz);
    }

}
