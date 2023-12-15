package unrefined.util.concurrent;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface CharProducer extends Callable<Character> {

    @Override
    default Character call() throws Exception {
        return get();
    }

    char get();

}
