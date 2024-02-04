package unrefined.util.function;

@FunctionalInterface
public interface FloatFunctor<R> extends VarFunctor<R> {

    @Override
    default R actuate(Object... args) {
        return apply((float) args[0]);
    }

    R apply(float a);

}
