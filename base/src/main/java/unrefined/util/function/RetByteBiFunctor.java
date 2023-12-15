package unrefined.util.function;

@FunctionalInterface
public interface RetByteBiFunctor<T, U> extends VarFunctor<Byte> {

    @SuppressWarnings("unchecked")
    @Override
    default Byte actuate(Object... args) {
        return apply((T) args[0], (U) args[1]);
    }

    byte apply(T t, U u);

}
