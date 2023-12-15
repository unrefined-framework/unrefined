package unrefined.util.function;

@FunctionalInterface
public interface BooleanBiOperator extends VarFunctor<Boolean> {

    @Override
    default Boolean actuate(Object... args) {
        return apply((boolean) args[0], (boolean) args[1]);
    }

    boolean apply(boolean a, boolean b);

}
