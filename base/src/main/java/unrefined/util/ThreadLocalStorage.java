package unrefined.util;

import unrefined.util.concurrent.Producer;

public final class ThreadLocalStorage {
    
    private ThreadLocalStorage() {
        throw new NotInstantiableError(ThreadLocalStorage.class);
    }
    
    public static <T> ThreadLocal<T> allocate() {
        return new ThreadLocal<>();
    }
    
    public static <T> ThreadLocal<T> allocateWithInitial(Producer<? extends T> producer) {
        return new ProducerThreadLocal<>(producer);
    }

    public static ThreadLocalBuffer allocateBuffer() {
        return new ThreadLocalBuffer();
    }

    public static ThreadLocalStack allocateStack(int size) {
        return new ThreadLocalStack(size);
    }

    public static ThreadLocalStack allocateStack() {
        return new ThreadLocalStack();
    }

}
