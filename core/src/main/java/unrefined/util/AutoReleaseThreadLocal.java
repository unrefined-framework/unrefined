package unrefined.util;

import unrefined.app.Runtime;
import unrefined.util.concurrent.Producer;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class AutoReleaseThreadLocal<T> extends ProducerThreadLocal<WeakReference<T>> {

    private final Producer<? extends T> producer;
    public AutoReleaseThreadLocal(Producer<? extends T> producer) {
        super(() -> new WeakReference<>(producer.get()));
        this.producer = Objects.requireNonNull(producer);
    }

    @Override
    public WeakReference<T> get() {
        T object = super.get().get();
        try (QuietCloseable ignored = Runtime.getInstance().reachabilityScope(object)) {
            if (object == null) {
                object = producer.get();
                set(new WeakReference<>(object));
            }
            return super.get();
        }
    }

    public T getValue() {
        return get().get();
    }

}
