package unrefined.util.function;

@FunctionalInterface
public interface ShortSlot extends VarSlot {

    @Override
    default void actuate(Object... args) {
        accept((short) args[0]);
    }

    void accept(short value);

}
