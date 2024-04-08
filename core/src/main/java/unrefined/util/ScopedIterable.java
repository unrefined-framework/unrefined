package unrefined.util;

import java.util.Iterator;
import java.util.Objects;

public interface ScopedIterable<T> extends Iterable<T>, AutoCloseable {

    static <T> ScopedIterable<T> wrap(Iterable<T> iterable, AutoCloseable closeProc) {
        Objects.requireNonNull(iterable);
        return new ScopedIterable<T>() {
            @Override
            public void close() throws Exception {
                if (closeProc != null) closeProc.close();
            }
            @Override
            public Iterator<T> iterator() {
                return iterable.iterator();
            }
        };
    }

}
