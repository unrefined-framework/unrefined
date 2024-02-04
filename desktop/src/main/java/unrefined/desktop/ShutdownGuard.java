package unrefined.desktop;

import unrefined.util.NotInstantiableError;
import unrefined.util.concurrent.ConcurrentHashSet;

import java.util.Set;

public final class ShutdownGuard {

    private ShutdownGuard() {
        throw new NotInstantiableError(ShutdownGuard.class);
    }

    public static void register(Runnable runnable) {
        SHUTDOWN_HOOKS.add(runnable);
    }

    public static void unregister(Runnable runnable) {
        SHUTDOWN_HOOKS.remove(runnable);
    }

    private static final Set<Runnable> SHUTDOWN_HOOKS = new ConcurrentHashSet<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            synchronized (SHUTDOWN_HOOKS) {
                for (Runnable runnable : SHUTDOWN_HOOKS) {
                    runnable.run();
                }
            }
        }, "Unrefined Shutdown Hook"));
    }

}
