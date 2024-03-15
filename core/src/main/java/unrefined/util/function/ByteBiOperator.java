package unrefined.util.function;

@FunctionalInterface
public interface ByteBiOperator extends BiOperator<Byte> {

    @Override
    default Byte apply(Byte a, Byte b) {
        return applyAsByte(a, b);
    }

    byte applyAsByte(byte a, byte b);

}
