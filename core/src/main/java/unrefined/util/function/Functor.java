package unrefined.util.function;

@FunctionalInterface
public interface Functor<T, R> extends VarFunctor<R> {

    @SuppressWarnings("unchecked")
    @Override
    default R actuate(Object... args) {
        return apply((T) args[0]);
    }

    R apply(T t);

}
