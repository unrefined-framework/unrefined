package unrefined.util.function;

@FunctionalInterface
public interface LongAssert extends LongFunctor<Boolean> {

    @Override
    default Boolean applyOfLong(long t) {
        return test(t);
    }

    boolean test(long t);

}
