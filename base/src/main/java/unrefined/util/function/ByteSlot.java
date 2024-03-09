package unrefined.util.function;

@FunctionalInterface
public interface ByteSlot extends Slot<Byte> {

    @Override
    default void accept(Byte t) {
        acceptAsByte(t);
    }

    void acceptAsByte(byte t);

}
