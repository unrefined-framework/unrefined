package unrefined.util.function;

@FunctionalInterface
public interface Operator<T> extends Functor<T, T> {

    @Override
    T apply(T a);

}
