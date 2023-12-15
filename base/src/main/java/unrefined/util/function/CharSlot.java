package unrefined.util.function;

@FunctionalInterface
public interface CharSlot extends VarSlot {

    @Override
    default void actuate(Object... args) {
        accept((char) args[0]);
    }

    void accept(char value);

}
