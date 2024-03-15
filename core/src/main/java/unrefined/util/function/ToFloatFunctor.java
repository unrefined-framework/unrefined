package unrefined.util.function;

@FunctionalInterface
public interface ToFloatFunctor<T> extends Functor<T, Float> {

    @Override
    default Float apply(T t) {
        return applyAsFloat(t);
    }

    float applyAsFloat(T t);

}
