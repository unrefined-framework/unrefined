package unrefined.util.function;

@FunctionalInterface
public interface ShortBiOperator extends VarFunctor<Short> {

    @Override
    default Short actuate(Object... args) {
        return apply((short) args[0], (short) args[1]);
    }

    short apply(short a, short b);

}
