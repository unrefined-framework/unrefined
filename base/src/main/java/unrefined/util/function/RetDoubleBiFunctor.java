package unrefined.util.function;

@FunctionalInterface
public interface RetDoubleBiFunctor<T, U> extends VarFunctor<Double> {

    @SuppressWarnings("unchecked")
    @Override
    default Double actuate(Object... args) {
        return apply((T) args[0], (U) args[1]);
    }

    double apply(T t, U u);

}
