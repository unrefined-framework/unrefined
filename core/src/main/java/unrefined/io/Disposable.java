package unrefined.io;

public interface Disposable extends AutoCloseable {

    default void close() {
        dispose();
    }

    void dispose();
    boolean isDisposed();

}
