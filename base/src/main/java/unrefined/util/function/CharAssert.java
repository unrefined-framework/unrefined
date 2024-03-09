package unrefined.util.function;

@FunctionalInterface
public interface CharAssert extends CharFunctor<Boolean> {

    @Override
    default Boolean applyOfChar(char t) {
        return test(t);
    }

    boolean test(char t);

}
