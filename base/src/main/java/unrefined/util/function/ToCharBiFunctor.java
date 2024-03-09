package unrefined.util.function;

@FunctionalInterface
public interface ToCharBiFunctor<T, U> extends BiFunctor<T, U, Character> {

    @Override
    default Character apply(T t, U u) {
        return applyAsChar(t, u);
    }

    char applyAsChar(T t, U u);

}
