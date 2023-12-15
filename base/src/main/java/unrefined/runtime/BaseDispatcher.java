package unrefined.runtime;

import unrefined.util.signal.Dispatcher;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link Dispatcher} implementation with a worker thread.
 * {@link #start()} and {@link #stop()} are convenience methods to
 * start (and stop) an arbitrary thread which executes {@link #run()} in its
 * context.
 *
 * @see Dispatcher
 */
public class BaseDispatcher extends Dispatcher {

    /**
     * The thread used in {@link #start()} and {@link #stop}.
     */
    private Thread workerThread = null;

    /**
     * The worker thread name.
     */
    private final String workerThreadName;

    /**
     * This ID is used to generate thread names.
     */
    private final static AtomicInteger nextSerialNumber = new AtomicInteger(0);
    private static int serialNumber() {
        return nextSerialNumber.getAndIncrement();
    }

    protected boolean isDispatchThread() {
        return Thread.currentThread() == workerThread;
    }

    @Override
    protected void preActuation() {
    }

    @Override
    protected void postActuation() {
    }

    @Override
    protected boolean hasQueueThread() {
        return true;
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
        workerThreadName = name;
    }

    /**
     * Creates a new {@link Thread} which runs {@link #run()}. Does nothing if
     * there already is a running thread.
     */
    public final synchronized void start() {
        if (workerThread == null) {
            workerThread = new Thread(this::run, workerThreadName);
            workerThread.setDaemon(true);
            workerThread.start();
        }
    }

    protected void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                waitFor();
                switchContext();
            }
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Stops the current {@link Thread} created by {@link #start()}. Does
     * nothing if there is no running thread.
     */
    public final synchronized void stop() {
        if (workerThread != null) {
            workerThread.interrupt();
            workerThread = null;
        }
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
