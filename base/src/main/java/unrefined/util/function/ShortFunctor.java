package unrefined.util.function;

@FunctionalInterface
public interface ShortFunctor<R> extends VarFunctor<R> {

    @Override
    default R actuate(Object... args) {
        return apply((short) args[0]);
    }

    R apply(short a);

}
