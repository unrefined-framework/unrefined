package unrefined.util.function;

@FunctionalInterface
public interface CharFunctor<R> extends VarFunctor<R> {

    @Override
    default R actuate(Object... args) {
        return apply((char) args[0]);
    }

    R apply(char a);

}
