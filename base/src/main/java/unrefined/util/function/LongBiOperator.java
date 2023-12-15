package unrefined.util.function;

@FunctionalInterface
public interface LongBiOperator extends VarFunctor<Long> {

    @Override
    default Long actuate(Object... args) {
        return apply((long) args[0], (long) args[1]);
    }

    long apply(long a, long b);

}
