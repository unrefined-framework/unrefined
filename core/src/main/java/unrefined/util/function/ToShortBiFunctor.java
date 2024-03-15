package unrefined.util.function;

@FunctionalInterface
public interface ToShortBiFunctor<T, U> extends BiFunctor<T, U, Short> {

    @Override
    default Short apply(T t, U u) {
        return applyAsShort(t, u);
    }

    short applyAsShort(T t, U u);

}
