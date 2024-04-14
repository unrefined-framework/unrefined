package unrefined.nio.channels;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;

public abstract class Selector implements Closeable {

    public abstract Set<SelectionKey> keys();
    public abstract Set<SelectionKey> selectedKeys();

    public abstract int selectNow() throws IOException;
    public abstract int select(long timeout) throws IOException;
    public abstract int select() throws IOException;
    public abstract Selector wakeup();

    public abstract void close() throws IOException;
    public abstract boolean isOpen();

}
