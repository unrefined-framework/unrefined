package unrefined.net;

import unrefined.nio.channels.ClientSocketChannel;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class ClientSocket extends Socket {

    public static ClientSocket of() throws IOException {
        return Net.getInstance().createClientSocket();
    }
    public static ClientSocket of(InetSocketAddress remote) throws IOException {
        return Net.getInstance().createClientSocket(remote);
    }
    public static ClientSocket of(InetSocketAddress remote, InetSocketAddress local) throws IOException {
        return Net.getInstance().createClientSocket(remote, local);
    }

    public abstract void connect(InetSocketAddress remote) throws IOException;
    public abstract void connect(InetSocketAddress remote, int timeout) throws IOException;
    @Override
    public abstract void bind(InetSocketAddress local) throws IOException;
    public abstract InetAddress getRemoteInetAddress();
    public abstract InetAddress getLocalInetAddress();
    public abstract int getRemotePort();
    public abstract int getLocalPort();
    public abstract InetSocketAddress getRemoteSocketAddress();
    @Override
    public abstract InetSocketAddress getLocalSocketAddress();
    public abstract ClientSocketChannel getChannel();
    public abstract InputStream getInputStream() throws IOException;
    public abstract OutputStream getOutputStream() throws IOException;
    public abstract void setNoDelay(boolean on) throws SocketException;
    public abstract boolean isNoDelay() throws SocketException;
    public abstract void setLinger(boolean on, int linger) throws SocketException;
    public abstract int getLinger() throws SocketException;
    public abstract void setOOBInline(boolean on) throws SocketException;
    public abstract boolean isOOBInline() throws SocketException;
    public abstract void setTimeout(int timeout) throws SocketException;
    public abstract int getTimeout() throws SocketException;
    public abstract void setSendBufferSize(int size) throws SocketException;
    public abstract int getSendBufferSize() throws SocketException;
    public abstract void setReceiveBufferSize(int size) throws SocketException;
    public abstract int getReceiveBufferSize() throws SocketException;
    public abstract void setKeepAlive(boolean on) throws SocketException;
    public abstract boolean isKeepAlive() throws SocketException;
    public abstract void setTrafficClass(int tc) throws SocketException;
    public abstract int getTrafficClass() throws SocketException;
    public abstract void setReuseAddress(boolean on) throws SocketException;
    public abstract boolean isReuseAddress() throws SocketException;

    public abstract void sendUrgentData(int data) throws IOException;

    public abstract void shutdownInput() throws IOException;
    public abstract void shutdownOutput() throws IOException;

    public abstract boolean isConnected();
    public abstract boolean isBound();
    public abstract boolean isOpen();
    public abstract boolean isInputShutdown();
    public abstract boolean isOutputShutdown();

    public abstract FileDescriptor getFD() throws IOException;

}
