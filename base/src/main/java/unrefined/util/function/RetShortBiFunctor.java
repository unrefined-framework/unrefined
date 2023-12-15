package unrefined.util.function;

@FunctionalInterface
public interface RetShortBiFunctor<T, U> extends VarFunctor<Short> {

    @SuppressWarnings("unchecked")
    @Override
    default Short actuate(Object... args) {
        return apply((T) args[0], (U) args[1]);
    }

    short apply(T t, U u);

}
