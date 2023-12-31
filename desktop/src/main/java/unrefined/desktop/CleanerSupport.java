package unrefined.desktop;

import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class CleanerSupport {

    private CleanerSupport() {
        throw new NotInstantiableError(CleanerSupport.class);
    }

    private static final Method createMethod;
    private static final Method cleanMethod;
    private static final Method registerMethod;
    static {
        Method method;
        try {
            method = Class.forName("java.lang.ref.Cleaner").getDeclaredMethod("register", Object.class, Runnable.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            method = null;
        }
        registerMethod = method;
        if (registerMethod == null) {
            try {
                method = Class.forName("sun.misc.Cleaner").getDeclaredMethod("create", Object.class, Runnable.class);
            } catch (NoSuchMethodException | ClassNotFoundException ignored) {
            }
            createMethod = method;
            try {
                method = Class.forName("java.lang.ref.Cleaner.Cleanable").getDeclaredMethod("clean");
            } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            }
            cleanMethod = method;
        }
        else {
            try {
                method = Class.forName("java.lang.ref.Cleaner").getDeclaredMethod("create");
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                method = null;
            }
            createMethod = method;
            try {
                method = Class.forName("sun.misc.Cleaner").getDeclaredMethod("clean");
            } catch (NoSuchMethodException | ClassNotFoundException ex) {
                method = null;
            }
            cleanMethod = method;
        }
    }

    private static final Object cleanerLock = new Object();
    private static volatile Object cleaner;

    private static Object getCleaner(Object object, Runnable cleanProc) {
        if (cleaner == null) synchronized (cleanerLock) {
            if (cleaner == null) {
                try {
                    if (registerMethod == null) cleaner = createMethod.invoke(null, object, cleanProc);
                    else cleaner = registerMethod.invoke(createMethod.invoke(null), object, cleanProc);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new UnexpectedError(e);
                }
            }
        }
        return cleaner;
    }

    public static Runnable register(Object object, Runnable cleanProc) {
        Object cleaner = getCleaner(object, cleanProc);
        return () -> {
            try {
                cleanMethod.invoke(cleaner);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new UnexpectedError(e);
            }
        };
    }

}
