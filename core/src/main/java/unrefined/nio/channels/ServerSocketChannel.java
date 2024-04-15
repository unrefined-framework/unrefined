package unrefined.nio.channels;

import unrefined.net.InetSocketAddress;
import unrefined.net.Net;
import unrefined.net.ServerSocket;

import java.io.FileDescriptor;
import java.io.IOException;

public abstract class ServerSocketChannel extends SelectableChannel implements NetworkChannel {

    public static ServerSocketChannel open() throws IOException {
        return Net.getInstance().openServerSocketChannel();
    }
    public static ServerSocketChannel open(InetSocketAddress local) throws IOException {
        return Net.getInstance().openServerSocketChannel(local);
    }

    @Override
    public final int getValidOperations() {
        return SelectionKey.Operation.ACCEPT;
    }

    public abstract void bind(InetSocketAddress local) throws IOException;
    public abstract void bind(InetSocketAddress local, int backlog) throws IOException;
    public abstract ServerSocket getSocket();
    public abstract ClientSocketChannel accept() throws IOException;
    @Override
    public abstract InetSocketAddress getLocalSocketAddress() throws IOException;

    public abstract FileDescriptor getFD() throws IOException;

}
