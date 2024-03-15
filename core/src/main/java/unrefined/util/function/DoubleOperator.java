package unrefined.util.function;

@FunctionalInterface
public interface DoubleOperator extends Operator<Double> {

    @Override
    default Double apply(Double a) {
        return applyAsDouble(a);
    }

    double applyAsDouble(double a);

}
