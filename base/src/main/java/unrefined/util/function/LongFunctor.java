package unrefined.util.function;

@FunctionalInterface
public interface LongFunctor<R> extends VarFunctor<R> {

    @Override
    default R actuate(Object... args) {
        return apply((long) args[0]);
    }

    R apply(long a);

}
