package unrefined.util.function;

@FunctionalInterface
public interface FloatBiOperator extends BiOperator<Float> {

    @Override
    default Float apply(Float a, Float b) {
        return applyAsFloat(a, b);
    }

    float applyAsFloat(float a, float b);

}
