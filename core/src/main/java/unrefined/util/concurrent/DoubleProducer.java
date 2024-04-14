package unrefined.util.concurrent;

@FunctionalInterface
public interface DoubleProducer extends Producer<Double> {

    @Override
    default Double call() throws Exception {
        return getAsDouble();
    }

    @Override
    default Double get() {
        return getAsDouble();
    }

    double getAsDouble();

}
