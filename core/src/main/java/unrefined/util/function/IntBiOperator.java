package unrefined.util.function;

@FunctionalInterface
public interface IntBiOperator extends BiOperator<Integer> {

    @Override
    default Integer apply(Integer a, Integer b) {
        return applyAsInt(a, b);
    }

    int applyAsInt(int a, int b);

}
