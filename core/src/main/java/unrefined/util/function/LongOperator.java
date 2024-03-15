package unrefined.util.function;

@FunctionalInterface
public interface LongOperator extends Operator<Long> {

    @Override
    default Long apply(Long a) {
        return applyAsLong(a);
    }

    long applyAsLong(long a);

}
