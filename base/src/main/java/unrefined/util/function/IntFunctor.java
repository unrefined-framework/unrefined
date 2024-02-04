package unrefined.util.function;

@FunctionalInterface
public interface IntFunctor<R> extends VarFunctor<R> {

    @Override
    default R actuate(Object... args) {
        return apply((int) args[0]);
    }

    R apply(int a);

}
