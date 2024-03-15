package unrefined.util.function;

@FunctionalInterface
public interface FloatSlot extends Slot<Float> {

    @Override
    default void accept(Float t) {
        acceptAsFloat(t);
    }

    void acceptAsFloat(float t);

}
