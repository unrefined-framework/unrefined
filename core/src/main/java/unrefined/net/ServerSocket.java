package unrefined.net;

import unrefined.nio.channels.ServerSocketChannel;

import java.io.FileDescriptor;
import java.io.IOException;

public abstract class ServerSocket extends Socket {

    @Override
    public abstract void bind(InetSocketAddress endpoint) throws IOException;
    public abstract void bind(InetSocketAddress endpoint, int backlog) throws IOException;
    public abstract InetAddress getLocalInetAddress();
    public abstract int getLocalPort();
    @Override
    public abstract InetSocketAddress getLocalSocketAddress();
    public abstract ClientSocket accept() throws IOException;
    public abstract ServerSocketChannel getChannel();
    public abstract boolean isBound();
    public abstract boolean isOpen();
    public abstract void setTimeout(int timeout) throws SocketException;
    public abstract int getTimeout() throws SocketException;
    public abstract void setReuseAddress(boolean on) throws SocketException;
    public abstract boolean isReuseAddress() throws SocketException;
    public abstract void setReceiveBufferSize(int size) throws SocketException;
    public abstract int getReceiveBufferSize() throws SocketException;

    public abstract FileDescriptor getFD() throws IOException;

}
