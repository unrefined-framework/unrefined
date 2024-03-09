package unrefined.util.function;

@FunctionalInterface
public interface ByteFunctor<R> extends Functor<Byte, R> {

    @Override
    default R apply(Byte t) {
        return applyOfByte(t);
    }

    R applyOfByte(byte t);

}
