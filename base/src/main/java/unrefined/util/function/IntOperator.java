package unrefined.util.function;

@FunctionalInterface
public interface IntOperator extends VarFunctor<Integer> {

    @Override
    default Integer actuate(Object... args) {
        return apply((int) args[0]);
    }

    int apply(int a);

}
