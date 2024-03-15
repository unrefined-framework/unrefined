package unrefined.util.function;

@FunctionalInterface
public interface IntAssert extends IntFunctor<Boolean> {

    @Override
    default Boolean applyOfInt(int t) {
        return test(t);
    }

    boolean test(int t);

}
