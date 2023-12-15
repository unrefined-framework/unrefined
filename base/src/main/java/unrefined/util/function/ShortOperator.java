package unrefined.util.function;

@FunctionalInterface
public interface ShortOperator extends VarFunctor<Short> {

    @Override
    default Short actuate(Object... args) {
        return apply((short) args[0]);
    }

    short apply(short a);

}
