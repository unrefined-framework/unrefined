package unrefined.util.concurrent;

@FunctionalInterface
public interface BooleanProducer extends Producer<Boolean> {

    @Override
    default Boolean call() throws Exception {
        return getAsBoolean();
    }

    @Override
    default Boolean get() {
        return getAsBoolean();
    }

    boolean getAsBoolean();

}
