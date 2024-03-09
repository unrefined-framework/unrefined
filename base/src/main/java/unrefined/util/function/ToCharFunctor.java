package unrefined.util.function;

@FunctionalInterface
public interface ToCharFunctor<T> extends Functor<T, Character> {

    @Override
    default Character apply(T t) {
        return applyAsChar(t);
    }

    char applyAsChar(T t);

}
