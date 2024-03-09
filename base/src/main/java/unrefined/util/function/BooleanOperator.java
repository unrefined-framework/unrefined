package unrefined.util.function;

@FunctionalInterface
public interface BooleanOperator extends Operator<Boolean> {

    @Override
    default Boolean apply(Boolean a) {
        return applyAsBoolean(a);
    }

    default boolean test(boolean t) {
        return applyAsBoolean(t);
    }

    boolean applyAsBoolean(boolean a);

}
