package unrefined.util.function;

@FunctionalInterface
public interface RetFloatFunctor<T> extends VarFunctor<Float> {

    @SuppressWarnings("unchecked")
    @Override
    default Float actuate(Object... args) {
        return apply((T) args[0]);
    }

    float apply(T t);

}
