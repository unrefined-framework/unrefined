package unrefined.util.concurrent;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface Producer<V> extends Callable<V> {

    @Override
    default V call() throws Exception {
        return get();
    }

    V get();

}
