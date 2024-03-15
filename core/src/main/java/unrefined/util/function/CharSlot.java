package unrefined.util.function;

@FunctionalInterface
public interface CharSlot extends Slot<Character> {

    @Override
    default void accept(Character t) {
        acceptAsChar(t);
    }

    void acceptAsChar(char value);

}
