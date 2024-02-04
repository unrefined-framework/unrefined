package unrefined.runtime;

import unrefined.util.Threading;
import unrefined.util.concurrent.worker.Worker;

import java.util.Objects;

import static unrefined.desktop.UnsafeSupport.UNSAFE;

public class DesktopThreading extends Threading {

    @Override
    public void park(boolean absolute, long time) {
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
