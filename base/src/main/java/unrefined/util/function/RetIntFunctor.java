package unrefined.util.function;

@FunctionalInterface
public interface RetIntFunctor<T> extends VarFunctor<Integer> {

    @SuppressWarnings("unchecked")
    @Override
    default Integer actuate(Object... args) {
        return apply((T) args[0]);
    }

    int apply(T t);

}
