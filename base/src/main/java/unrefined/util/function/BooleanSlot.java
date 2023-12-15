package unrefined.util.function;

@FunctionalInterface
public interface BooleanSlot extends VarSlot {

    @Override
    default void actuate(Object... args) {
        accept((boolean) args[0]);
    }

    void accept(boolean value);

}
