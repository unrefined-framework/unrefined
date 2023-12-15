package unrefined.util.function;

@FunctionalInterface
public interface CharBiOperator extends VarFunctor<Character> {

    @Override
    default Character actuate(Object... args) {
        return apply((char) args[0], (char) args[1]);
    }

    char apply(char a, char b);

}
