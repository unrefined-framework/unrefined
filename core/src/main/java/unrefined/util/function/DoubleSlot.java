package unrefined.util.function;

@FunctionalInterface
public interface DoubleSlot extends Slot<Double> {

    @Override
    default void accept(Double t) {
        acceptAsDouble(t);
    }

    void acceptAsDouble(double value);

}
