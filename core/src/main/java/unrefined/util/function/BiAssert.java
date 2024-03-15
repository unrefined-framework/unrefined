package unrefined.util.function;

@FunctionalInterface
public interface BiAssert<T, U> extends BiFunctor<T, U, Boolean> {

    @Override
    default Boolean apply(T t, U u) {
        return test(t, u);
    }

    boolean test(T t, U u);

}
