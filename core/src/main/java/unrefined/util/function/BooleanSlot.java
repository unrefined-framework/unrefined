package unrefined.util.function;

@FunctionalInterface
public interface BooleanSlot extends Slot<Boolean> {

    @Override
    default void accept(Boolean t) {
        acceptAsBoolean(t);
    }

    void acceptAsBoolean(boolean t);

}
