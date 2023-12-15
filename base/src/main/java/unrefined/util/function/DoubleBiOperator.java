package unrefined.util.function;

@FunctionalInterface
public interface DoubleBiOperator extends VarFunctor<Double> {

    @Override
    default Double actuate(Object... args) {
        return apply((double) args[0], (double) args[1]);
    }

    double apply(double a, double b);

}
