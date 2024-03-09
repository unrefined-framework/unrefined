package unrefined.util.function;

@FunctionalInterface
public interface IntFunctor<R> extends Functor<Integer, R> {

    @Override
    default R apply(Integer t) {
        return applyOfInt(t);
    }

    R applyOfInt(int t);

}
