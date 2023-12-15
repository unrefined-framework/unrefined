package unrefined.util;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public final class EmptyArray {

    private EmptyArray() {
        throw new NotInstantiableError(EmptyArray.class);
    }

    public static final boolean[] BOOLEAN = new boolean[0];
    public static final byte[] BYTE = new byte[0];
    public static final char[] CHAR = new char[0];
    public static final short[] SHORT = new short[0];
    public static final int[] INT = new int[0];
    public static final long[] LONG = new long[0];
    public static final float[] FLOAT = new float[0];
    public static final double[] DOUBLE = new double[0];
    public static final Object[] OBJECT = new Object[0];

    private static final Map<Class<?>, Object> CACHE = new HashMap<>();
    static {
        CACHE.put(boolean.class, BOOLEAN);
        CACHE.put(byte.class, BYTE);
        CACHE.put(char.class, CHAR);
        CACHE.put(short.class, SHORT);
        CACHE.put(int.class, INT);
        CACHE.put(long.class, LONG);
        CACHE.put(float.class, FLOAT);
        CACHE.put(double.class, DOUBLE);
        CACHE.put(Object.class, OBJECT);
    }

    @SuppressWarnings("unchecked")
    public static <T> T ofArrayClass(Class<T> clazz) {
        if (!clazz.isArray()) throw new IllegalArgumentException("Not an array class");
        Class<?> componentClass = clazz.getComponentType();
        if (!CACHE.containsKey(componentClass)) synchronized (CACHE) {
            if (!CACHE.containsKey(componentClass))
                CACHE.put(componentClass, Array.newInstance(componentClass, 0));
        }
        return (T) CACHE.get(componentClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] ofComponentClass(Class<T> clazz) {
        if (!CACHE.containsKey(clazz)) synchronized (CACHE) {
            if (!CACHE.containsKey(clazz)) CACHE.put(clazz, Array.newInstance(clazz, 0));
        }
        return (T[]) CACHE.get(clazz);
    }

}
