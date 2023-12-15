package unrefined.util.function;

@FunctionalInterface
public interface RetIntBiFunctor<T, U> extends VarFunctor<Integer> {

    @SuppressWarnings("unchecked")
    @Override
    default Integer actuate(Object... args) {
        return apply((T) args[0], (U) args[1]);
    }

    int apply(T t, U u);

}
