package unrefined.util.function;

@FunctionalInterface
public interface BiOperator<T> extends BiFunctor<T, T, T> {

    @Override
    T apply(T a, T b);

}
