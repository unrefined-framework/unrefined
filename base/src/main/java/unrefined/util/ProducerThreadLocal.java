package unrefined.util;

import unrefined.util.concurrent.Producer;

import java.util.Objects;

public class ProducerThreadLocal<T> extends ThreadLocal<T> {

    private final Producer<? extends T> producer;

    public ProducerThreadLocal(Producer<? extends T> producer) {
        this.producer = Objects.requireNonNull(producer);
    }

    @Override
    protected T initialValue() {
        return producer.get();
    }

}
