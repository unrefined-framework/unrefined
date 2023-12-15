package unrefined.internal;

import sun.misc.Unsafe;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public final class MethodLookupUtils {

    private MethodLookupUtils() {
        throw new NotInstantiableError(MethodLookupUtils.class);
    }

    private static final MethodHandles.Lookup IMPL_LOOKUP;

    static {
        Unsafe unsafe = UnsafeUtils.getUnsafe();
        try {
            Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            if (field.trySetAccessible()) IMPL_LOOKUP = (MethodHandles.Lookup) field.get(null);
            else {
                IMPL_LOOKUP = (MethodHandles.Lookup) unsafe.getObject(MethodHandles.Lookup.class, unsafe.staticFieldOffset(field));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new UnexpectedError(e);
        }
    }

    public static MethodHandles.Lookup getImplLookup() {
        return IMPL_LOOKUP;
    }

}
