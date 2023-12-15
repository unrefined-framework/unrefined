package unrefined.util.function;

@FunctionalInterface
public interface RetShortFunctor<T> extends VarFunctor<Short> {

    @SuppressWarnings("unchecked")
    @Override
    default Short actuate(Object... args) {
        return apply((T) args[0]);
    }

    short apply(T t);

}
