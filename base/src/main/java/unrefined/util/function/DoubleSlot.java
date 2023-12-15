package unrefined.util.function;

@FunctionalInterface
public interface DoubleSlot extends VarSlot {

    @Override
    default void actuate(Object... args) {
        accept((double) args[0]);
    }

    void accept(double value);

}
