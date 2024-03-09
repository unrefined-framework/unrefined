package unrefined.util.function;

@FunctionalInterface
public interface LongFunctor<R> extends Functor<Long, R> {

    @Override
    default R apply(Long t) {
        return applyOfLong(t);
    }

    R applyOfLong(long t);

}
