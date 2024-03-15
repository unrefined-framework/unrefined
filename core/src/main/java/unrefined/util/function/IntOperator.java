package unrefined.util.function;

@FunctionalInterface
public interface IntOperator extends Operator<Integer> {

    @Override
    default Integer apply(Integer a) {
        return applyAsInt(a);
    }

    int applyAsInt(int a);

}
