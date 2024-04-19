package unrefined.util;

import unrefined.util.concurrent.Producer;
import unrefined.util.function.Assert;

import java.lang.ref.WeakReference;

public class AutoReleaseReference<T> {

    private WeakReference<T> holder;

    public AutoReleaseReference(T initialValue) {
        holder = new WeakReference<>(initialValue);
    }

    public AutoReleaseReference() {
        holder = new WeakReference<>(null);
    }

    public void set(T value) {
        holder = new WeakReference<>(value);
    }

    public T setIfAndGet(Assert<T> predicate, T newValue) {
        T value = get();
        if (predicate.test(value)) set(newValue);
        return newValue;
    }

    public T setIfAndGet(Assert<T> predicate, Producer<T> producer) {
        T value = get();
        if (predicate.test(value)) {
            T newValue = producer.get();
            set(newValue);
            return newValue;
        }
        return value;
    }

    public T setIfReleasedAndGet(T newValue) {
        T value = get();
        if (value == null) set(newValue);
        return newValue;
    }

    public T setIfReleasedAndGet(Producer<T> producer) {
        T value = get();
        if (value == null) {
            T newValue = producer.get();
            set(newValue);
            return newValue;
        }
        return value;
    }

    public T get() {
        return holder.get();
    }

}
