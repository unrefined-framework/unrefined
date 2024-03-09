package unrefined.util.function;

@FunctionalInterface
public interface ToFloatBiFunctor<T, U> extends BiFunctor<T, U, Float> {

    @Override
    default Float apply(T t, U u) {
        return applyAsFloat(t, u);
    }

    float applyAsFloat(T t, U u);

}
