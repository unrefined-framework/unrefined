package unrefined.util.function;

@FunctionalInterface
public interface ShortAssert extends ShortFunctor<Boolean> {

    @Override
    default Boolean applyOfShort(short t) {
        return test(t);
    }

    boolean test(short t);

}
