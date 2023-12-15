package unrefined.util.concurrent;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface BooleanProducer extends Callable<Boolean> {

    @Override
    default Boolean call() throws Exception {
        return get();
    }

    boolean get();

}
