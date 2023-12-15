package unrefined.util.function;

@FunctionalInterface
public interface FloatBiOperator extends VarFunctor<Float> {

    @Override
    default Float actuate(Object... args) {
        return apply((float) args[0], (float) args[1]);
    }

    float apply(float a, float b);

}
