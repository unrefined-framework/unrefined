package unrefined.nio.channels;

import unrefined.net.Net;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class Selector implements Closeable {

    public static Selector open() throws IOException {
        return Net.getInstance().openSelector();
    }

    public abstract Set<SelectionKey> keys();
    public abstract Set<SelectionKey> selectedKeys();

    public abstract int selectNow() throws IOException;
    public int select(long timeout) throws IOException {
        return select(timeout, null);
    }
    public abstract int select(long timeout, TimeUnit timeUnit) throws IOException;
    public abstract int select() throws IOException;
    public abstract Selector wakeup();

    public abstract void close() throws IOException;
    public abstract boolean isOpen();

}
