package unrefined.util.concurrent;

@FunctionalInterface
public interface FloatProducer extends Producer<Float> {

    @Override
    default Float call() throws Exception {
        return getAsFloat();
    }

    @Override
    default Float get() {
        return getAsFloat();
    }

    float getAsFloat();

}
