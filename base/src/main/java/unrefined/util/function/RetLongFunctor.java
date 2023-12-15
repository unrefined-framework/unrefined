package unrefined.util.function;

@FunctionalInterface
public interface RetLongFunctor<T> extends VarFunctor<Long> {

    @SuppressWarnings("unchecked")
    @Override
    default Long actuate(Object... args) {
        return apply((T) args[0]);
    }

    long apply(T t);

}
