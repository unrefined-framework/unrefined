package unrefined.util.function;

@FunctionalInterface
public interface ToLongFunctor<T> extends Functor<T, Long> {

    @Override
    default Long apply(T t) {
        return applyAsLong(t);
    }

    long applyAsLong(T t);

}
