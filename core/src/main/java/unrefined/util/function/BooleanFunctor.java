package unrefined.util.function;

@FunctionalInterface
public interface BooleanFunctor<R> extends Functor<Boolean, R> {

    @Override
    default R apply(Boolean t) {
        return applyOfBoolean(t);
    }

    R applyOfBoolean(boolean t);

}
