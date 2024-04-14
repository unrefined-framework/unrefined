package unrefined.util.concurrent;

@FunctionalInterface
public interface IntProducer extends Producer<Integer> {

    @Override
    default Integer call() throws Exception {
        return getAsInt();
    }

    @Override
    default Integer get() {
        return getAsInt();
    }

    int getAsInt();

}
