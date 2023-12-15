package unrefined.util.function;

@FunctionalInterface
public interface ByteSlot extends VarSlot {

    @Override
    default void actuate(Object... args) {
        accept((byte) args[0]);
    }

    void accept(byte value);

}
