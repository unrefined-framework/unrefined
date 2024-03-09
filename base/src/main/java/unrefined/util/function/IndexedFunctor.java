package unrefined.util.function;

@FunctionalInterface
public interface IndexedFunctor<T, R> extends BiFunctor<Integer, T, R> {

    @Override
    default R apply(Integer i, T t) {
        return applyIndexed(i, t);
    }

    R applyIndexed(int i, T t);

}
