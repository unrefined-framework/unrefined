package unrefined.util.function;

@FunctionalInterface
public interface CharFunctor<R> extends Functor<Character, R> {

    @Override
    default R apply(Character t) {
        return applyOfChar(t);
    }

    R applyOfChar(char t);

}
