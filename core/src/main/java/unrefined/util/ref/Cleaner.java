package unrefined.util.ref;

import unrefined.context.Environment;

public abstract class Cleaner {

    private static volatile Cleaner INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Cleaner getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.cleaner", Cleaner.class);
        }
        return INSTANCE;
    }

    /**
     * Registers an object and a cleaning action to run when the object
     * becomes phantom reachable.
     *
     * @param object   the object to monitor
     * @param cleanProc a {@code Runnable} to invoke when the object becomes phantom reachable
     * @return a {@code Runnable} instance which cleans the object
     */
    public abstract Runnable register(Object object, Runnable cleanProc);

    public void register(AutoCloseable closeable) {
        register(closeable, () -> {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        });
    }

}
