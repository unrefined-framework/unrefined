package unrefined.util.function;

@FunctionalInterface
public interface FloatOperator extends Operator<Float> {

    @Override
    default Float apply(Float a) {
        return applyAsFloat(a);
    }

    float applyAsFloat(float a);

}
