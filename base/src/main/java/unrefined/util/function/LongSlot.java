package unrefined.util.function;

@FunctionalInterface
public interface LongSlot extends Slot<Long> {

    @Override
    default void accept(Long t) {
        acceptAsLong(t);
    }

    void acceptAsLong(long t);

}
