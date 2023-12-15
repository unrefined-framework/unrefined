package unrefined.util.function;

@FunctionalInterface
public interface CharOperator extends VarFunctor<Character> {

    @Override
    default Character actuate(Object... args) {
        return apply((char) args[0]);
    }

    char apply(char a);

}
