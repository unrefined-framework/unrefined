package unrefined.util.function;

@FunctionalInterface
public interface ToShortFunctor<T> extends Functor<T, Short> {

    @Override
    default Short apply(T t) {
        return applyAsShort(t);
    }

    short applyAsShort(T t);

}
