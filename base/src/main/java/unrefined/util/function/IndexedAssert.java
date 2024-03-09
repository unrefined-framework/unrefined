package unrefined.util.function;

@FunctionalInterface
public interface IndexedAssert<T> extends BiAssert<Integer, T> {

    @Override
    default boolean test(Integer i, T t) {
        return testIndexed(i, t);
    }

    boolean testIndexed(int i, T t);

}
