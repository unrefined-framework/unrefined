package unrefined.util.function;

@FunctionalInterface
public interface Slot<T> extends VarSlot {

    @SuppressWarnings("unchecked")
    @Override
    default void actuate(Object... args) {
        accept((T) args[0]);
    }

    void accept(T t);

}
