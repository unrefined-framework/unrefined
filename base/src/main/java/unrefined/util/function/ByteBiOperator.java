package unrefined.util.function;

@FunctionalInterface
public interface ByteBiOperator extends VarFunctor<Byte> {

    @Override
    default Byte actuate(Object... args) {
        return apply((byte) args[0], (byte) args[1]);
    }

    byte apply(byte a, byte b);

}
