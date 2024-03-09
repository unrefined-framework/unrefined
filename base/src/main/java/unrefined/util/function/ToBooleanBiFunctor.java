package unrefined.util.function;

@FunctionalInterface
public interface ToBooleanBiFunctor<T, U> extends BiAssert<T, U> {

    @Override
    default Boolean apply(T t, U u) {
        return applyAsBoolean(t, u);
    }

    @Override
    default boolean test(T t, U u) {
        return applyAsBoolean(t, u);
    }

    boolean applyAsBoolean(T t, U u);

}
