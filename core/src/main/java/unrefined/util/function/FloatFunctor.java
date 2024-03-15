package unrefined.util.function;

@FunctionalInterface
public interface FloatFunctor<R> extends Functor<Float, R> {

    @Override
    default R apply(Float t) {
        return applyOfFloat(t);
    }

    R applyOfFloat(float t);

}
