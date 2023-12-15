package unrefined.util.function;

@FunctionalInterface
public interface RetCharFunctor<T> extends VarFunctor<Character> {

    @SuppressWarnings("unchecked")
    @Override
    default Character actuate(Object... args) {
        return apply((T) args[0]);
    }

    char apply(T t);

}
