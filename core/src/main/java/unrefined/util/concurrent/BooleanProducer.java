package unrefined.util.concurrent;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface BooleanProducer extends Callable<Boolean> {

    @Override
    default Boolean call() throws Exception {
        return getAsBoolean();
    }

    default Boolean get() {
        return getAsBoolean();
    }

    boolean getAsBoolean();

}
