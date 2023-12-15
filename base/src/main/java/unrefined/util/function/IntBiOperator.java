package unrefined.util.function;

@FunctionalInterface
public interface IntBiOperator extends VarFunctor<Integer> {

    @Override
    default Integer actuate(Object... args) {
        return apply((int) args[0], (int) args[1]);
    }

    int apply(int a, int b);

}
