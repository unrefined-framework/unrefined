package unrefined.util.concurrent;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface ByteProducer extends Callable<Byte> {

    @Override
    default Byte call() throws Exception {
        return getAsByte();
    }

    default Byte get() {
        return getAsByte();
    }

    byte getAsByte();

}
