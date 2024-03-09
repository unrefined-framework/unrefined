package unrefined.util.function;

@FunctionalInterface
public interface FloatAssert extends FloatFunctor<Boolean> {

    @Override
    default Boolean applyOfFloat(float t) {
        return test(t);
    }

    boolean test(float t);

}
