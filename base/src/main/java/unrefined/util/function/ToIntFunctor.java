package unrefined.util.function;

@FunctionalInterface
public interface ToIntFunctor<T> extends Functor<T, Integer> {

    @Override
    default Integer apply(T t) {
        return applyAsInt(t);
    }

    int applyAsInt(T t);

}
