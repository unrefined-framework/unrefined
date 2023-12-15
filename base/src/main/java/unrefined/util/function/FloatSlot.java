package unrefined.util.function;

@FunctionalInterface
public interface FloatSlot extends VarSlot {

    @Override
    default void actuate(Object... args) {
        accept((float) args[0]);
    }

    void accept(float value);

}
