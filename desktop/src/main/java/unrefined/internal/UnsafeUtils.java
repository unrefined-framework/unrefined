package unrefined.internal;

import sun.misc.Unsafe;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.lang.reflect.Field;

public final class UnsafeUtils {

    private UnsafeUtils() {
        throw new NotInstantiableError(UnsafeUtils.class);
    }

    private static final Unsafe UNSAFE;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new UnexpectedError(e);
        }
    }

    public static Unsafe getUnsafe() {
        return UNSAFE;
    }

}
