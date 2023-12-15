package unrefined.util.function;

@FunctionalInterface
public interface DoubleOperator extends VarFunctor<Double> {

    @Override
    default Double actuate(Object... args) {
        return apply((double) args[0]);
    }

    double apply(double a);

}
