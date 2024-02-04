package unrefined.util.function;

@FunctionalInterface
public interface DoubleFunctor<R> extends VarFunctor<R> {

    @Override
    default R actuate(Object... args) {
        return apply((double) args[0]);
    }

    R apply(double a);

}
