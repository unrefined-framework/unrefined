package unrefined.runtime;

import unrefined.util.concurrent.worker.Worker;
import unrefined.util.reflect.Reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class BaseWorker extends Worker {

    /**
     * This ID is used to generate thread names.
     */
    private static final AtomicInteger nextSerialNumber = new AtomicInteger();
    private static int serialNumber() {
        return nextSerialNumber.getAndIncrement();
    }

    private volatile Thread thread = null;
    private final ExecutorService executor;
    private final String name;
    private final Method main;

    public BaseWorker(String name, Class<?> clazz) throws IllegalArgumentException {
        super(clazz);
        try {
            main = clazz.getDeclaredMethod("main", Worker.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("public static void main(Worker) required");
        }
        if (main.getReturnType() != void.class) throw new IllegalArgumentException("Illegal return type; expected void");
        int modifiers = main.getModifiers();
        if (!Modifier.isStatic(modifiers)) throw new IllegalArgumentException("Illegal method modifier; expected static");
        if (!Modifier.isPublic(modifiers)) throw new IllegalArgumentException("Illegal method modifier; expected public");
        this.name = name == null ? "DesktopWorker-" + serialNumber() : name;
        executor = Executors.newSingleThreadExecutor(runnable -> {
            if (thread == null) {
                thread = new Thread(runnable);
                thread.setUncaughtExceptionHandler((t, e) -> BaseWorker.this.onException().emit(e));
                thread.setDaemon(true);
                thread.setName(BaseWorker.this.name);
            }
            return thread;
        });
        executor.execute(() -> {
            try {
                Reflection.getInstance().invokeVoidMethod(null, main, BaseWorker.this);
            } catch (InvocationTargetException e) {
                BaseWorker.this.onException().emit(e.getTargetException());
            }
        });
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public boolean isWorkerThread(Thread thread) {
        return thread != null && thread == this.thread;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void terminate() {
        executor.shutdownNow();
    }

    @Override
    public boolean isTerminated() {
        return executor.isTerminated();
    }

    @Override
    public void post(Object message) throws IllegalStateException {
        if (executor.isTerminated()) throw new IllegalStateException("Worker is terminated");
        executor.execute(() -> onMessage().emit(message));
    }

    @Override
    public String toString() {
        return getClass().getName() + '@' + Integer.toHexString(hashCode())
                + '{' +
                "name='" + name + '\'' +
                '}';
    }

}
