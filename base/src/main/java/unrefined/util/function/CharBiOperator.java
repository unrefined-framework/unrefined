package unrefined.util.function;

@FunctionalInterface
public interface CharBiOperator extends BiOperator<Character> {

    @Override
    default Character apply(Character a, Character b) {
        return applyAsChar(a, b);
    }

    char applyAsChar(char a, char b);

}
