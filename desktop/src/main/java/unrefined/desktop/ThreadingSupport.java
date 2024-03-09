package unrefined.desktop;

import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public final class ThreadingSupport {

    private ThreadingSupport() {
        throw new NotInstantiableError(ThreadingSupport.class);
    }

    private static final Method ofVirtualMethod;
    private static final Method isVirtualMethod;
    private static final boolean virtualThreadSupported;
    static {
        Method method;
        try {
            method = Thread.class.getDeclaredMethod("ofVirtual");
        }
        catch (NoSuchMethodException e) {
            method = null;
        }
        ofVirtualMethod = method;
        if (method == null) isVirtualMethod = null;
        else {
            try {
                method = Thread.class.getDeclaredMethod("isVirtual");
            }
            catch (NoSuchMethodException e) {
                method = null;
            }
            isVirtualMethod = method;
        }
        virtualThreadSupported = ofVirtualMethod != null && isVirtualMethod != null;
    }

    public static boolean isVirtualThreadSupported() {
        return virtualThreadSupported;
    }

    public static Thread createVirtualThread(Runnable task, String name, Thread.UncaughtExceptionHandler exceptionHandler) {
        if (!virtualThreadSupported) throw new UnsupportedOperationException("Virtual thread not supported");
        try {
            Object builder = ReflectionSupport.invokeObjectMethod(null, ofVirtualMethod);
            if (name != null) builder = ReflectionSupport.invokeObjectMethod(builder, builder.getClass().getMethod("name", String.class), name);
            Thread thread = (Thread) ReflectionSupport.invokeObjectMethod(builder, builder.getClass().getMethod("unstarted", Runnable.class), task);
            if (exceptionHandler != null) thread.setUncaughtExceptionHandler(exceptionHandler);
            return thread;
        } catch (InvocationTargetException | NoSuchMethodException e) {
            throw new UnexpectedError(e);
        }
    }

    public static boolean isVirtual(Thread thread) {
        Objects.requireNonNull(thread);
        if (!virtualThreadSupported) return false;
        try {
            return ReflectionSupport.invokeBooleanMethod(thread, isVirtualMethod);
        } catch (InvocationTargetException e) {
            throw new UnexpectedError(e);
        }
    }

}
