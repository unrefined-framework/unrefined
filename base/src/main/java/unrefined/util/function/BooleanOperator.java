package unrefined.util.function;

@FunctionalInterface
public interface BooleanOperator extends VarFunctor<Boolean> {

    @Override
    default Boolean actuate(Object... args) {
        return apply((boolean) args[0]);
    }

    boolean apply(boolean a);

}
