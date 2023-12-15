package unrefined.util.concurrent;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface LongProducer extends Callable<Long> {

    @Override
    default Long call() throws Exception {
        return get();
    }

    long get();

}
