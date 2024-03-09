package unrefined.util.function;

@FunctionalInterface
public interface ToByteFunctor<T> extends Functor<T, Byte> {

    @Override
    default Byte apply(T t) {
        return applyAsByte(t);
    }

    byte applyAsByte(T t);

}
