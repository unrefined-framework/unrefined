package unrefined.util.function;

@FunctionalInterface
public interface ToByteBiFunctor<T, U> extends BiFunctor<T, U, Byte> {

    @Override
    default Byte apply(T t, U u) {
        return applyAsByte(t, u);
    }

    byte applyAsByte(T t, U u);

}
