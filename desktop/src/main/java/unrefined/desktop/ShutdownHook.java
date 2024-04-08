package unrefined.desktop;

import unrefined.util.NotInstantiableError;
import unrefined.util.concurrent.ConcurrentHashSet;

import java.util.Set;

public final class ShutdownHook {

    private ShutdownHook() {
        throw new NotInstantiableError(ShutdownHook.class);
    }

    public static void register(Runnable runnable) {
        SHUTDOWN_HOOKS.add(runnable);
    }

    public static void unregister(Runnable runnable) {
        SHUTDOWN_HOOKS.remove(runnable);
    }

    public static boolean isShutdownThread(Thread thread) {
        return thread == SHUTDOWN_THREAD;
    }

    public static boolean isShutdownThread() {
        return isShutdownThread(Thread.currentThread());
    }

    private static final Set<Runnable> SHUTDOWN_HOOKS = new ConcurrentHashSet<>();
    private static final Thread SHUTDOWN_THREAD = new Thread(() -> {
        synchronized (SHUTDOWN_HOOKS) {
            for (Runnable hook : SHUTDOWN_HOOKS) {
                hook.run();
            }
        }
    }, "Unrefined Shutdown Hook");

    static {
        Runtime.getRuntime().addShutdownHook(SHUTDOWN_THREAD);
    }

}
