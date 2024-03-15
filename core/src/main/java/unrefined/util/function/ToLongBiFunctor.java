package unrefined.util.function;

@FunctionalInterface
public interface ToLongBiFunctor<T, U> extends BiFunctor<T, U, Long> {

    @Override
    default Long apply(T t, U u) {
        return applyAsLong(t, u);
    }

    long applyAsLong(T t, U u);

}
