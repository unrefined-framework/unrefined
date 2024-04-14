package unrefined.util.concurrent;

@FunctionalInterface
public interface ShortProducer extends Producer<Short> {

    @Override
    default Short call() throws Exception {
        return getAsShort();
    }

    @Override
    default Short get() {
        return getAsShort();
    }

    short getAsShort();

}
