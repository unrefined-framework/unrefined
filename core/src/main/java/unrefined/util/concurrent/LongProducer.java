package unrefined.util.concurrent;

@FunctionalInterface
public interface LongProducer extends Producer<Long> {

    @Override
    default Long call() throws Exception {
        return getAsLong();
    }

    @Override
    default Long get() {
        return getAsLong();
    }

    long getAsLong();

}
