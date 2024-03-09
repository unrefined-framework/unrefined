package unrefined.util.function;

@FunctionalInterface
public interface BooleanAssert extends BooleanOperator, BooleanFunctor<Boolean> {

    @Override
    default Boolean apply(Boolean a) {
        return test(a);
    }

    @Override
    default boolean applyAsBoolean(boolean a) {
        return test(a);
    }

    @Override
    default Boolean applyOfBoolean(boolean t) {
        return test(t);
    }

    boolean test(boolean t);

}
