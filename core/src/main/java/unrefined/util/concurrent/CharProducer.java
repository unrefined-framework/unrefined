package unrefined.util.concurrent;

@FunctionalInterface
public interface CharProducer extends Producer<Character> {

    @Override
    default Character call() throws Exception {
        return getAsChar();
    }

    @Override
    default Character get() {
        return getAsChar();
    }

    char getAsChar();

}
