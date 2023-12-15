package unrefined.util.function;

@FunctionalInterface
public interface LongOperator extends VarFunctor<Long> {

    @Override
    default Long actuate(Object... args) {
        return apply((long) args[0]);
    }

    long apply(long a);

}
