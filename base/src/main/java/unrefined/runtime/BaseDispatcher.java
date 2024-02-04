package unrefined.runtime;

import unrefined.util.signal.Dispatcher;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link Dispatcher} implementation with a single-thread {@link ScheduledExecutorService}.
 *
 * @see Dispatcher
 */
public class BaseDispatcher extends Dispatcher {

    private volatile Thread workerThread = null;
    private final ScheduledExecutorService executor;

    /**
     * This ID is used to generate thread names.
     */
    private final static AtomicInteger nextSerialNumber = new AtomicInteger(0);
    private static int serialNumber() {
        return nextSerialNumber.getAndIncrement();
    }

    @Override
    public boolean isDispatchThread(Thread thread) {
        return thread == workerThread;
    }

    @Override
    public void invokeLater(Runnable runnable) {
        executor.execute(runnable);
    }

    /**
     * Creates a new dispatcher. The associated thread specified to run as a daemon.
     */
    public BaseDispatcher() {
        this("Dispatcher-" + serialNumber());
    }

    /**
     * Creates a new dispatcher whose associated thread has the specified name.
     * The associated thread specified to run as a daemon.
     *
     * @param name the name of the associated thread
     */
    public BaseDispatcher(String name) {
        Objects.requireNonNull(name);
        executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, name);
            thread.setDaemon(true);
            workerThread = thread;
            return thread;
        });
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "thread=" + workerThread +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        BaseDispatcher that = (BaseDispatcher) object;

        return Objects.equals(workerThread, that.workerThread);
    }

    @Override
    public int hashCode() {
        return workerThread != null ? workerThread.hashCode() : 0;
    }

}
