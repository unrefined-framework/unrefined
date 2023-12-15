package unrefined.util;

import unrefined.util.concurrent.Producer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ThreadCache {

    private ThreadCache() {
        throw new NotInstantiableError(ThreadCache.class);
    }

    private static final Map<Class<?>, ThreadLocal<Object>> CACHE = new ConcurrentHashMap<>();

    public static boolean contains(Class<?> clazz) {
        return CACHE.containsKey(clazz);
    }

    public static <T> void register(Class<T> clazz, Producer<T> producer) {
        CACHE.put(clazz, new ProducerThreadLocal<>(producer));
    }

    public static void unregister(Class<?> clazz) {
        CACHE.remove(clazz);
    }

    public static void clear() {
        CACHE.clear();
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz) {
        return ((ThreadLocal<T>) CACHE.get(clazz)).get();
    }

}
