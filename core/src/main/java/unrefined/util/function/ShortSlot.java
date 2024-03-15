package unrefined.util.function;

@FunctionalInterface
public interface ShortSlot extends Slot<Short> {

    @Override
    default void accept(Short t) {
        acceptAsShort(t);
    }

    void acceptAsShort(short t);

}
