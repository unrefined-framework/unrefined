package unrefined.util.concurrent;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface ShortProducer extends Callable<Short> {

    @Override
    default Short call() throws Exception {
        return getAsShort();
    }

    default Short get() {
        return getAsShort();
    }

    short getAsShort();

}
