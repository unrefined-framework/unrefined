package unrefined.util.function;

@FunctionalInterface
public interface ShortOperator extends Operator<Short> {

    @Override
    default Short apply(Short a) {
        return applyAsShort(a);
    }

    short applyAsShort(short a);

}
