package unrefined.util.function;

@FunctionalInterface
public interface ToIntBiFunctor<T, U> extends BiFunctor<T, U, Integer> {

    @Override
    default Integer apply(T t, U u) {
        return applyAsInt(t, u);
    }

    int applyAsInt(T t, U u);

}
