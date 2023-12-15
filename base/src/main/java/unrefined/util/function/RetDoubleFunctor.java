package unrefined.util.function;

@FunctionalInterface
public interface RetDoubleFunctor<T> extends VarFunctor<Double> {

    @SuppressWarnings("unchecked")
    @Override
    default Double actuate(Object... args) {
        return apply((T) args[0]);
    }

    double apply(T t);

}
