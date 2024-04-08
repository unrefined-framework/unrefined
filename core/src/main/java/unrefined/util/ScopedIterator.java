package unrefined.util;

import java.util.Iterator;
import java.util.Objects;

public interface ScopedIterator<E> extends Iterator<E>, AutoCloseable {

    static <E> ScopedIterator<E> wrap(Iterator<E> iterator, AutoCloseable closeProc) {
        Objects.requireNonNull(iterator);
        return new ScopedIterator<E>() {
            @Override
            public void close() throws Exception {
                if (closeProc != null) closeProc.close();
            }
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
            @Override
            public E next() {
                return iterator.next();
            }
        };
    }

}
