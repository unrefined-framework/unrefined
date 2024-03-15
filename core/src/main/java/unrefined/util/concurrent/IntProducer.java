package unrefined.util.concurrent;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface IntProducer extends Callable<Integer> {

    @Override
    default Integer call() throws Exception {
        return getAsInt();
    }

    default Integer get() {
        return getAsInt();
    }

    int getAsInt();

}
