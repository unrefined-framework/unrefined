package unrefined.util.function;

@FunctionalInterface
public interface BooleanFunctor<R> extends VarFunctor<R> {

    @Override
    default R actuate(Object... args) {
        return apply((boolean) args[0]);
    }

    R apply(boolean a);

}
