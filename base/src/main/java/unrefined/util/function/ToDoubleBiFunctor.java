package unrefined.util.function;

@FunctionalInterface
public interface ToDoubleBiFunctor<T, U> extends BiFunctor<T, U, Double> {

    @Override
    default Double apply(T t, U u) {
        return applyAsDouble(t, u);
    }

    double applyAsDouble(T t, U u);

}
