package unrefined.util.function;

@FunctionalInterface
public interface ToBooleanFunctor<T> extends Assert<T> {

    @Override
    default Boolean apply(T t) {
        return applyAsBoolean(t);
    }

    @Override
    default boolean test(T t) {
        return applyAsBoolean(t);
    }

    boolean applyAsBoolean(T t);

}
