package unrefined.util.function;

@FunctionalInterface
public interface RetBooleanFunctor<T> extends VarFunctor<Boolean> {

    @SuppressWarnings("unchecked")
    @Override
    default Boolean actuate(Object... args) {
        return apply((T) args[0]);
    }

    boolean apply(T t);

}
