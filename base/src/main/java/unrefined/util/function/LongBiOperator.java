package unrefined.util.function;

@FunctionalInterface
public interface LongBiOperator extends BiOperator<Long> {

    @Override
    default Long apply(Long a, Long b) {
        return applyAsLong(a, b);
    }

    long applyAsLong(long a, long b);

}
