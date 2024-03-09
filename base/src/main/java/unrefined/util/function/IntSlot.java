package unrefined.util.function;

@FunctionalInterface
public interface IntSlot extends Slot<Integer> {

    @Override
    default void accept(Integer t) {
        acceptAsInt(t);
    }

    void acceptAsInt(int t);

}
