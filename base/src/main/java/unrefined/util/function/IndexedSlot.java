package unrefined.util.function;

@FunctionalInterface
public interface IndexedSlot<T> extends BiSlot<Integer, T> {

    @Override
    default void accept(Integer i, T t) {
        acceptIndexed(i, t);
    }

    void acceptIndexed(int i, T t);

}
