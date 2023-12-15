package unrefined.util.concurrent;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface DoubleProducer extends Callable<Double> {

    @Override
    default Double call() throws Exception {
        return get();
    }

    double get();

}
