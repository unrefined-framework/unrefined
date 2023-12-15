package unrefined.util.function;

@FunctionalInterface
public interface IntSlot extends VarSlot {

    @Override
    default void actuate(Object... args) {
        accept((int) args[0]);
    }

    void accept(int value);

}
