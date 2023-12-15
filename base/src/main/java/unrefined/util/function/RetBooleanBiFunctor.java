package unrefined.util.function;

@FunctionalInterface
public interface RetBooleanBiFunctor<T, U> extends VarFunctor<Boolean> {

    @SuppressWarnings("unchecked")
    @Override
    default Boolean actuate(Object... args) {
        return apply((T) args[0], (U) args[1]);
    }

    boolean apply(T t, U u);

}
