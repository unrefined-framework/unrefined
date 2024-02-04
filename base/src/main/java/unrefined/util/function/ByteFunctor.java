package unrefined.util.function;

@FunctionalInterface
public interface ByteFunctor<R> extends VarFunctor<R> {

    @Override
    default R actuate(Object... args) {
        return apply((byte) args[0]);
    }

    R apply(byte a);

}
