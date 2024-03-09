package unrefined.util.function;

@FunctionalInterface
public interface CharOperator extends Operator<Character> {

    @Override
    default Character apply(Character a) {
        return applyAsChar(a);
    }

    char applyAsChar(char a);

}
