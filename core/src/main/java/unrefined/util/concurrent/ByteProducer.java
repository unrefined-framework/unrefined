package unrefined.util.concurrent;

@FunctionalInterface
public interface ByteProducer extends Producer<Byte> {

    @Override
    default Byte call() throws Exception {
        return getAsByte();
    }

    @Override
    default Byte get() {
        return getAsByte();
    }

    byte getAsByte();

}
