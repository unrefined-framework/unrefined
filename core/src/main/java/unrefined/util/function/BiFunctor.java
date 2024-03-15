package unrefined.util.function;

@FunctionalInterface
public interface BiFunctor<T, U, R> extends VarFunctor<R> {

    @SuppressWarnings("unchecked")
    @Override
    default R actuate(Object... args) {
        return apply((T) args[0], (U) args[1]);
    }

    R apply(T t, U u);

}
