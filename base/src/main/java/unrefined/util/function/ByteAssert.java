package unrefined.util.function;

@FunctionalInterface
public interface ByteAssert extends ByteFunctor<Boolean> {

    @Override
    default Boolean applyOfByte(byte t) {
        return test(t);
    }

    boolean test(byte t);

}
