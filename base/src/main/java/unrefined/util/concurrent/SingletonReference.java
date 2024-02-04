package unrefined.util.concurrent;

import java.io.Serializable;
import java.util.Objects;

public class SingletonReference<V> implements Serializable {

    private static final long serialVersionUID = 4454797889195124053L;

    private volatile V instance = null;
    private final Producer<V> producer;
    private final Object lock = new Object();
    public SingletonReference(Producer<V> producer) {
        this.producer = Objects.requireNonNull(producer);
    }

    public V get() {
        if (instance == null) synchronized (lock) {
            if (instance == null) instance = producer.get();
        }
        return instance;
    }

}
