package unrefined.internal;

import sun.misc.Unsafe;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.lang.reflect.Field;
import java.nio.Buffer;

public final class BufferUtils {

    private BufferUtils() {
        throw new NotInstantiableError(BufferUtils.class);
    }

    private static final Unsafe UNSAFE = UnsafeUtils.getUnsafe();

    private final static Field BUFFER_ADDRESS_FIELD;
    static {
        try {
            BUFFER_ADDRESS_FIELD = Buffer.class.getDeclaredField("address");
        } catch (NoSuchFieldException e) {
            throw new UnexpectedError(e);
        }
    }

    public static long getDirectBufferAddress(Buffer buffer) {
        return buffer.isDirect() ? UNSAFE.getLong(buffer, UNSAFE.objectFieldOffset(BUFFER_ADDRESS_FIELD)) : 0;
    }

}
