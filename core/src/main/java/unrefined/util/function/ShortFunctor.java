package unrefined.util.function;

@FunctionalInterface
public interface ShortFunctor<R> extends Functor<Short, R> {

    @Override
    default R apply(Short t) {
        return applyOfShort(t);
    }

    R applyOfShort(short t);

}
