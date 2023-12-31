package unrefined.desktop;

import sun.misc.Unsafe;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

@SuppressWarnings({"DiscouragedPrivateApi", "BlockedPrivateApi"})
public final class UnsafeSupport {

    private UnsafeSupport() {
        throw new NotInstantiableError(UnsafeSupport.class);
    }

    public static final Unsafe UNSAFE;
    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new UnexpectedError(e);
        }
    }

    public static final MethodHandles.Lookup IMPL_LOOKUP;
    static {
        try {
            Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            if (ReflectionSupport.trySetAccessible(field)) IMPL_LOOKUP = (MethodHandles.Lookup) field.get(null);
            else {
                IMPL_LOOKUP = (MethodHandles.Lookup) UNSAFE.getObject(MethodHandles.Lookup.class, UNSAFE.staticFieldOffset(field));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new UnexpectedError(e);
        }
    }

    private static final Method invokeCleanerMethod;
    static {
        Method method;
        try {
            method = Unsafe.class.getDeclaredMethod("invokeCleaner", ByteBuffer.class);
        } catch (NoSuchMethodException e) {
            method = null;
        }
        invokeCleanerMethod = method;
    }

    private static Object invoke(Object obj, Method method) throws InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);
        return method.invoke(obj);
    }

    public static void invokeCleaner(ByteBuffer directBuffer) {
        if (invokeCleanerMethod == null) {
            if (!directBuffer.isDirect()) throw new IllegalArgumentException("buffer is non-direct");
            try {
                if (invoke(directBuffer, directBuffer.getClass().getMethod("attachment")) != null)
                    throw new IllegalArgumentException("duplicate or slice");
                Object cleaner = invoke(directBuffer, directBuffer.getClass().getMethod("cleaner"));
                if (cleaner != null) invoke(cleaner, cleaner.getClass().getMethod("clean"));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new UnexpectedError(e);
            }
        }
        else {
            try {
                invokeCleanerMethod.invoke(UNSAFE, directBuffer);
            } catch (InvocationTargetException | IllegalAccessException ignored) {
            }
        }
    }

}
