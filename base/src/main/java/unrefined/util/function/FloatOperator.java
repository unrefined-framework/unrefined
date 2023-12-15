package unrefined.util.function;

@FunctionalInterface
public interface FloatOperator extends VarFunctor<Float> {

    @Override
    default Float actuate(Object... args) {
        return apply((float) args[0]);
    }

    float apply(float a);

}
