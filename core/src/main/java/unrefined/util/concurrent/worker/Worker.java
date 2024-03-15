package unrefined.util.concurrent.worker;

import unrefined.util.Threading;
import unrefined.util.function.Slot;
import unrefined.util.signal.Signal;

import java.util.Objects;

public abstract class Worker {

    public static Worker of(Class<?> clazz) {
        return Threading.getInstance().createWorker(clazz);
    }

    public static Worker of(String name, Class<?> clazz) {
        return Threading.getInstance().createWorker(name, clazz);
    }

    private final Class<?> clazz;

    public Worker(Class<?> clazz) {
        this.clazz = Objects.requireNonNull(clazz);
    }

    public Class<?> getWorkerClass() {
        return clazz;
    }

    public abstract void post(Object message) throws IllegalStateException;
    public abstract String getName();
    public abstract void terminate();
    public abstract boolean isTerminated();

    private final Signal<Slot<Object>> onMessage = Signal.ofSlot();

    public Signal<Slot<Object>> onMessage() {
        return onMessage;
    }

    public abstract boolean isWorkerThread(Thread thread);
    public boolean isWorkerThread() {
        return isWorkerThread(Thread.currentThread());
    }

    private final Signal<Slot<Throwable>> onException = Signal.ofSlot();

    public Signal<Slot<Throwable>> onException() {
        return onException;
    }

}
