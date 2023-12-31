package unrefined.util;

import unrefined.util.concurrent.Producer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadLocalBuffer {

    private final Map<Class<?>, ThreadLocal<Object>> CACHE = new ConcurrentHashMap<>();

    public boolean contains(Class<?> clazz) {
        return CACHE.containsKey(clazz);
    }

    public <T> void register(Class<T> clazz, Producer<T> producer) {
        CACHE.put(clazz, new ProducerThreadLocal<>(producer));
    }

    public void unregister(Class<?> clazz) {
        CACHE.remove(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        ThreadLocal<T> threadLocal = (ThreadLocal<T>) CACHE.get(clazz);
        return threadLocal == null ? null : threadLocal.get();
    }

    public void clear() {
        CACHE.clear();
    }

}
