package unrefined.util.function;

@FunctionalInterface
public interface RetFloatBiFunctor<T, U> extends VarFunctor<Float> {

    @SuppressWarnings("unchecked")
    @Override
    default Float actuate(Object... args) {
        return apply((T) args[0], (U) args[1]);
    }

    float apply(T t, U u);

}
