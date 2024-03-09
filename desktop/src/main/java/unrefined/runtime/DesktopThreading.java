package unrefined.runtime;

import unrefined.desktop.ThreadingSupport;
import unrefined.math.FastMath;
import unrefined.util.Threading;
import unrefined.util.concurrent.worker.Worker;

import java.util.Objects;

import static unrefined.desktop.UnsafeSupport.UNSAFE;

public class DesktopThreading extends Threading {

    @Override
    public boolean isPlatformThreadSupported() {
        return true;
    }

    @Override
    public boolean isVirtualThreadSupported() {
        return ThreadingSupport.isVirtualThreadSupported();
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
        return ThreadingSupport.createVirtualThread(task, name, exceptionHandler);
    }

    @Override
    public boolean isVirtualThread(Thread thread) {
        return ThreadingSupport.isVirtual(thread);
    }

    @Override
    public boolean isPlatformThread(Thread thread) {
        return !ThreadingSupport.isVirtual(thread);
    }

    @Override
    public void park(long time, boolean absolute) {
        UNSAFE.park(absolute, time);
    }

    @Override
    public void unpark(Thread thread) {
        UNSAFE.unpark(Objects.requireNonNull(thread));
    }

    @Override
    public boolean isMainThread(Thread thread) {
        return thread.getId() == 1;
    }

    @Override
    public Worker createWorker(String name, Class<?> clazz) {
        return new DesktopWorker(name, clazz);
    }

}
