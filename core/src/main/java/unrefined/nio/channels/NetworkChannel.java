package unrefined.nio.channels;

import unrefined.net.InetSocketAddress;

import java.io.IOException;
import java.nio.channels.Channel;

public interface NetworkChannel extends Channel {

    void bind(InetSocketAddress local) throws IOException;
    InetSocketAddress getLocalSocketAddress() throws IOException;

}
