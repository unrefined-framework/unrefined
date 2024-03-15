package unrefined.util.function;

@FunctionalInterface
public interface DoubleAssert extends DoubleFunctor<Boolean> {

    @Override
    default Boolean applyOfDouble(double t) {
        return test(t);
    }

    boolean test(double t);

}
