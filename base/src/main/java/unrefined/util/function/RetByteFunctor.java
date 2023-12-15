package unrefined.util.function;

@FunctionalInterface
public interface RetByteFunctor<T> extends VarFunctor<Byte> {

    @SuppressWarnings("unchecked")
    @Override
    default Byte actuate(Object... args) {
        return apply((T) args[0]);
    }

    byte apply(T t);

}
