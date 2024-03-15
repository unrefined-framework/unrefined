package unrefined.util.function;

@FunctionalInterface
public interface ByteOperator extends Operator<Byte> {

    @Override
    default Byte apply(Byte a) {
        return applyAsByte(a);
    }

    byte applyAsByte(byte a);

}
