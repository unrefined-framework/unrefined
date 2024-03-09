package unrefined.util.function;

@FunctionalInterface
public interface ShortBiOperator extends BiOperator<Short> {

    @Override
    default Short apply(Short a, Short b) {
        return applyAsShort(a, b);
    }

    short applyAsShort(short a, short b);

}
