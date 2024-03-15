package unrefined.util.function;

@FunctionalInterface
public interface DoubleFunctor<R> extends Functor<Double, R> {

    @Override
    default R apply(Double t) {
        return applyOfDouble(t);
    }

    R applyOfDouble(double t);

}
