package unrefined.util.function;

@FunctionalInterface
public interface BiSlot<T, U> extends VarSlot {

    @SuppressWarnings("unchecked")
    @Override
    default void actuate(Object... args) {
        accept((T) args[0], (U) args[1]);
    }

    void accept(T t, U u);

}
