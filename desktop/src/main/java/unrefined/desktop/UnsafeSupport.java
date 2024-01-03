package unrefined.desktop;

import sun.misc.Unsafe;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

    private static final boolean BIG_ENDIAN = ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder());

    private static byte compareAndExchangeByte(Object o, long offset,
                                              byte expected,
                                              byte x) {
        long wordOffset = offset & ~3;
        int shift = (int) (offset & 3) << 3;
        if (BIG_ENDIAN) {
            shift = 24 - shift;
        }
        int mask           = 0xFF << shift;
        int maskedExpected = (expected & 0xFF) << shift;
        int maskedX        = (x & 0xFF) << shift;
        int fullWord;
        do {
            fullWord = UNSAFE.getIntVolatile(o, wordOffset);
            if ((fullWord & mask) != maskedExpected)
                return (byte) ((fullWord & mask) >> shift);
        } while (!UNSAFE.compareAndSwapInt(o, wordOffset,
                fullWord, (fullWord & ~mask) | maskedX));
        return expected;
    }

    public static boolean compareAndSwapByte(Object o, long offset, byte expected, byte x) {
        return compareAndExchangeByte(o, offset, expected, x) == expected;
    }

    public static boolean compareAndSwapBoolean(Object o, long offset, boolean expected, boolean x) {
        return compareAndSwapByte(o, offset, expected ? (byte) 1 : (byte) 0, x ? (byte) 1 : (byte) 0);
    }

    public static byte getAndSetByte(Object o, long offset, byte newValue) {
        byte v;
        do {
            v = UNSAFE.getByteVolatile(o, offset);
        } while (!compareAndSwapByte(o, offset, v, newValue));
        return v;
    }

    public static boolean getAndSetBoolean(Object o, long offset, boolean newValue) {
        return getAndSetByte(o, offset, newValue ? (byte) 1 : (byte) 0) != 0;
    }

    public static short compareAndExchangeShort(Object o, long offset,
                                                short expected,
                                                short x) {
        if ((offset & 3) == 3) {
            throw new IllegalArgumentException("Update spans the word, not supported");
        }
        long wordOffset = offset & ~3;
        int shift = (int) (offset & 3) << 3;
        if (BIG_ENDIAN) {
            shift = 16 - shift;
        }
        int mask           = 0xFFFF << shift;
        int maskedExpected = (expected & 0xFFFF) << shift;
        int maskedX        = (x & 0xFFFF) << shift;
        int fullWord;
        do {
            fullWord = UNSAFE.getIntVolatile(o, wordOffset);
            if ((fullWord & mask) != maskedExpected) {
                return (short) ((fullWord & mask) >> shift);
            }
        } while (!UNSAFE.compareAndSwapInt(o, wordOffset,
                fullWord, (fullWord & ~mask) | maskedX));
        return expected;
    }

    public static boolean compareAndSwapShort(Object o, long offset, short expected, short x) {
        return compareAndExchangeShort(o, offset, expected, x) == expected;
    }

    public static boolean compareAndSwapChar(Object o, long offset, char expected, char x) {
        return compareAndSwapShort(o, offset, (short) expected, (short) x);
    }

    public static short getAndSetShort(Object o, long offset, short newValue) {
        short v;
        do {
            v = UNSAFE.getShortVolatile(o, offset);
        } while (!compareAndSwapShort(o, offset, v, newValue));
        return v;
    }

    public static char getAndSetChar(Object o, long offset, char newValue) {
        return (char) getAndSetShort(o, offset, (short) newValue);
    }

    public static float getAndSetFloat(Object o, long offset, float newValue) {
        return Float.intBitsToFloat(UNSAFE.getAndSetInt(o, offset, Float.floatToRawIntBits(newValue)));
    }

    public static double getAndSetDouble(Object o, long offset, double newValue) {
        return Double.longBitsToDouble(UNSAFE.getAndSetLong(o, offset, Double.doubleToRawLongBits(newValue)));
    }

    public static void putOrderedByte(Object o, long offset, byte x) {
        long wordOffset = offset & ~3;
        int shift = (int) (offset & 3) << 3;
        if (BIG_ENDIAN) {
            shift = 24 - shift;
        }
        int mask           = 0xFF << shift;
        int maskedX        = (x & 0xFF) << shift;
        int fullWord = UNSAFE.getIntVolatile(o, wordOffset);
        UNSAFE.putOrderedInt(o, offset, (fullWord & ~mask) | maskedX);
    }

    public static void putOrderedBoolean(Object o, long offset, boolean x) {
        putOrderedByte(o, offset, x ? (byte) 1 : (byte) 0);
    }

    public static void putOrderedShort(Object o, long offset, short x) {
        if ((offset & 3) == 3) {
            throw new IllegalArgumentException("Update spans the word, not supported");
        }
        long wordOffset = offset & ~3;
        int shift = (int) (offset & 3) << 3;
        if (BIG_ENDIAN) {
            shift = 16 - shift;
        }
        int mask           = 0xFFFF << shift;
        int maskedX        = (x & 0xFFFF) << shift;
        int fullWord = UNSAFE.getIntVolatile(o, wordOffset);
        UNSAFE.putOrderedInt(o, offset, (fullWord & ~mask) | maskedX);
    }

    public static void putOrderedChar(Object o, long offset, char x) {
        putOrderedShort(o, offset, (short) x);
    }

    public static void putOrderedFloat(Object o, long offset, float x) {
        UNSAFE.putOrderedInt(o, offset, Float.floatToRawIntBits(x));
    }

    public static void putOrderedDouble(Object o, long offset, double x) {
        UNSAFE.putOrderedLong(o, offset, Double.doubleToRawLongBits(x));
    }

    public static boolean compareAndSwapFloat(Object o, long offset, float expected, float x) {
        return UNSAFE.compareAndSwapInt(o, offset, Float.floatToRawIntBits(expected), Float.floatToRawIntBits(x));
    }

    public static boolean compareAndSwapDouble(Object o, long offset, double expected, double x) {
        return UNSAFE.compareAndSwapLong(o, offset, Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(x));
    }

    public static byte getAndAddByte(Object o, long offset, byte delta) {
        byte v;
        do {
            v = UNSAFE.getByteVolatile(o, offset);
        } while (!compareAndSwapByte(o, offset, v, (byte) (v + delta)));
        return v;
    }

    public static short getAndAddShort(Object o, long offset, short delta) {
        short v;
        do {
            v = UNSAFE.getShortVolatile(o, offset);
        } while (!compareAndSwapShort(o, offset, v, (short) (v + delta)));
        return v;
    }

    public static float getAndAddFloat(Object o, long offset, float delta) {
        int expectedBits;
        float v;
        do {
            // Load and CAS with the raw bits to avoid issues with NaNs and
            // possible bit conversion from signaling NaNs to quiet NaNs that
            // may result in the loop not terminating.
            expectedBits = UNSAFE.getIntVolatile(o, offset);
            v = Float.intBitsToFloat(expectedBits);
        } while (!UNSAFE.compareAndSwapInt(o, offset,
                expectedBits, Float.floatToRawIntBits(v + delta)));
        return v;
    }

    public static double getAndAddDouble(Object o, long offset, double delta) {
        long expectedBits;
        double v;
        do {
            // Load and CAS with the raw bits to avoid issues with NaNs and
            // possible bit conversion from signaling NaNs to quiet NaNs that
            // may result in the loop not terminating.
            expectedBits = UNSAFE.getLongVolatile(o, offset);
            v = Double.longBitsToDouble(expectedBits);
        } while (!UNSAFE.compareAndSwapLong(o, offset,
                expectedBits, Double.doubleToRawLongBits(v + delta)));
        return v;
    }

}
