package unrefined.util.concurrent;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface CharProducer extends Callable<Character> {

    @Override
    default Character call() throws Exception {
        return getAsChar();
    }

    default Character get() {
        return getAsChar();
    }

    char getAsChar();

}
