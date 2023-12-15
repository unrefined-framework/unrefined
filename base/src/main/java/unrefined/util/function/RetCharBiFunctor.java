package unrefined.util.function;

@FunctionalInterface
public interface RetCharBiFunctor<T, U> extends VarFunctor<Character> {

    @SuppressWarnings("unchecked")
    @Override
    default Character actuate(Object... args) {
        return apply((T) args[0], (U) args[1]);
    }

    char apply(T t, U u);

}
