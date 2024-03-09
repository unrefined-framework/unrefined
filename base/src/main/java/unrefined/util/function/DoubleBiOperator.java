package unrefined.util.function;

@FunctionalInterface
public interface DoubleBiOperator extends BiOperator<Double> {

    @Override
    default Double apply(Double a, Double b) {
        return applyAsDouble(a, b);
    }

    double applyAsDouble(double a, double b);

}
