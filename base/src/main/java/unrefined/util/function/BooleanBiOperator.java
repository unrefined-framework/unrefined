package unrefined.util.function;

@FunctionalInterface
public interface BooleanBiOperator extends BiOperator<Boolean> {

    @Override
    default Boolean apply(Boolean a, Boolean b) {
        return applyAsBoolean(a, b);
    }

    default boolean test(boolean t, boolean u) {
        return applyAsBoolean(t, u);
    }

    boolean applyAsBoolean(boolean a, boolean b);

}
