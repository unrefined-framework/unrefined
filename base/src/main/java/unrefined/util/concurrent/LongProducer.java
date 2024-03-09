package unrefined.util.concurrent;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface LongProducer extends Callable<Long> {

    @Override
    default Long call() throws Exception {
        return getAsLong();
    }

    default Long get() {
        return getAsLong();
    }

    long getAsLong();

}
