package unrefined.util.function;

@FunctionalInterface
public interface RetLongBiFunctor<T, U> extends VarFunctor<Long> {

    @SuppressWarnings("unchecked")
    @Override
    default Long actuate(Object... args) {
        return apply((T) args[0], (U) args[1]);
    }

    long apply(T t, U u);

}
