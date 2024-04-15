package unrefined.net;

import unrefined.nio.channels.DatagramSocketChannel;

import java.io.FileDescriptor;
import java.io.IOException;

public abstract class DatagramSocket extends MulticastSocket {

    public static DatagramSocket of() throws IOException {
        return Net.getInstance().createDatagramSocket();
    }
    public static DatagramSocket of(InetSocketAddress remote) throws IOException {
        return Net.getInstance().createDatagramSocket(remote);
    }
    public static DatagramSocket of(InetSocketAddress remote, InetSocketAddress local) throws IOException {
        return Net.getInstance().createDatagramSocket(remote, local);
    }

    @Override
    public abstract void bind(InetSocketAddress address) throws SocketException;
    public abstract void connect(InetAddress address, int port) throws IOException;
    public abstract void connect(InetSocketAddress address) throws IOException;
    public abstract void disconnect() throws IOException;
    public abstract boolean isBound();
    public abstract boolean isConnected();
    public abstract InetAddress getRemoteInetAddress();
    public abstract InetAddress getLocalInetAddress();
    public abstract int getRemotePort();
    public abstract int getLocalPort();
    public abstract InetSocketAddress getRemoteSocketAddress();
    @Override
    public abstract InetSocketAddress getLocalSocketAddress();
    public abstract void send(DatagramPacket packet) throws IOException;
    public abstract void receive(DatagramPacket packet) throws IOException;
    public abstract void setTimeout(int timeout) throws SocketException;
    public abstract int getTimeout() throws SocketException;
    public abstract void setSendBufferSize(int size) throws SocketException;
    public abstract int getSendBufferSize() throws SocketException;
    public abstract void setReceiveBufferSize(int size) throws SocketException;
    public abstract int getReceiveBufferSize() throws SocketException;
    public abstract void setReuseAddress(boolean on) throws SocketException;
    public abstract boolean isReuseAddress() throws SocketException;
    public abstract void setBroadcast(boolean on) throws SocketException;
    public abstract boolean isBroadcast() throws SocketException;
    public abstract void setTrafficClass(int tc) throws SocketException;
    public abstract int getTrafficClass() throws SocketException;
    public abstract boolean isOpen();
    public abstract DatagramSocketChannel getChannel();

    public abstract FileDescriptor getFD() throws IOException;

}
