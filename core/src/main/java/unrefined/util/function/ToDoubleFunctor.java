package unrefined.util.function;

@FunctionalInterface
public interface ToDoubleFunctor<T> extends Functor<T, Double> {

    @Override
    default Double apply(T t) {
        return applyAsDouble(t);
    }

    double applyAsDouble(T t);

}
