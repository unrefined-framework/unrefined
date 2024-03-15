package unrefined.util.function;

@FunctionalInterface
public interface Assert<T> extends Functor<T, Boolean> {

    @Override
    default Boolean apply(T t) {
        return test(t);
    }

    boolean test(T t);

}
