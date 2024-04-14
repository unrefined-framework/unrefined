package unrefined.net;

import java.io.Closeable;
import java.io.IOException;

public abstract class Socket implements Closeable {

    public abstract void bind(InetSocketAddress local) throws IOException;
    public abstract InetSocketAddress getLocalSocketAddress() throws IOException;

}
