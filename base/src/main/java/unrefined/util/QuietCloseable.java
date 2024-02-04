package unrefined.util;

public interface QuietCloseable extends AutoCloseable {

    @Override
    void close();

}
