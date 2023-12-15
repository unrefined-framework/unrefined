package unrefined.util.function;

@FunctionalInterface
public interface LongSlot extends VarSlot {

    @Override
    default void actuate(Object... args) {
        accept((long) args[0]);
    }

    void accept(long value);

}
