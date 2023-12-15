package unrefined.util.function;

@FunctionalInterface
public interface ByteOperator extends VarFunctor<Byte> {

    @Override
    default Byte actuate(Object... args) {
        return apply((byte) args[0]);
    }

    byte apply(byte a);

}
