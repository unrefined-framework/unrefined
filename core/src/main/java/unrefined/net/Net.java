package unrefined.net;

import unrefined.context.Environment;
import unrefined.nio.channels.ClientSocketChannel;
import unrefined.nio.channels.DatagramSocketChannel;
import unrefined.nio.channels.Pipe;
import unrefined.nio.channels.Selector;
import unrefined.nio.channels.ServerSocketChannel;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

public abstract class Net {

    private static volatile Net INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Net getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.net", Net.class);
        }
        return INSTANCE;
    }

    public abstract InetAddress createInetAddress(String host, byte[] address) throws UnknownHostException;
    public abstract InetAddress createInetAddress(byte[] address) throws UnknownHostException;
    public abstract InetAddress createInetAddress(String host) throws UnknownHostException;
    public abstract Set<InetAddress> getInetAddresses(String host) throws UnknownHostException;
    public abstract InetAddress getLoopback();
    public abstract InetAddress getLocalHost() throws UnknownHostException;
    public abstract Inet6Address createInet6Address(String host, byte[] address, NetworkInterface nif) throws UnknownHostException;
    public abstract Inet6Address createInet6Address(String host, byte[] address, int scope) throws UnknownHostException;

    public abstract InetSocketAddress createSocketAddress(int port);
    public abstract InetSocketAddress createSocketAddress(InetAddress address, int port);
    public abstract InetSocketAddress createSocketAddress(String host, int port);
    public abstract InetSocketAddress createUnresolvedSocketAddress(String host, int port);

    public abstract ClientSocket createClientSocket() throws IOException;
    public abstract ClientSocket createClientSocket(InetSocketAddress remote) throws IOException;
    public abstract ClientSocket createClientSocket(InetSocketAddress remote, InetSocketAddress local) throws IOException;
    
    public abstract ServerSocket createServerSocket() throws IOException;
    public abstract ServerSocket createServerSocket(InetSocketAddress local) throws IOException;
    public abstract ServerSocket createServerSocket(InetSocketAddress local, int backlog) throws IOException;

    public abstract DatagramSocket createDatagramSocket() throws IOException;
    public abstract DatagramSocket createDatagramSocket(InetSocketAddress remote) throws IOException;
    public abstract DatagramSocket createDatagramSocket(InetSocketAddress remote, InetSocketAddress local) throws IOException;

    public abstract DatagramPacket createDatagramPacket(byte[] buffer, int offset, int length);
    public DatagramPacket createDatagramPacket(byte[] buffer) {
        return createDatagramPacket(buffer, 0, buffer.length);
    }
    public abstract DatagramPacket createDatagramPacket(byte[] buffer, int offset, int length, InetSocketAddress address);
    public DatagramPacket createDatagramPacket(byte[] buffer, InetSocketAddress address) {
        return createDatagramPacket(buffer, 0, buffer.length, address);
    }

    public abstract ClientSocketChannel openClientSocketChannel() throws IOException;
    public abstract ClientSocketChannel openClientSocketChannel(InetSocketAddress remote) throws IOException;
    public abstract ClientSocketChannel openClientSocketChannel(InetSocketAddress remote, InetSocketAddress local) throws IOException;
    
    public abstract ServerSocketChannel openServerSocketChannel() throws IOException;
    public abstract ServerSocketChannel openServerSocketChannel(InetSocketAddress local) throws IOException;
    
    public abstract DatagramSocketChannel openDatagramSocketChannel() throws IOException;
    public abstract DatagramSocketChannel openDatagramSocketChannel(InetSocketAddress remote) throws IOException;
    public abstract DatagramSocketChannel openDatagramSocketChannel(InetSocketAddress remote, InetSocketAddress local) throws IOException;

    public abstract NetworkInterface getNetworkInterface(String name) throws SocketException;
    public abstract NetworkInterface getNetworkInterface(int index) throws SocketException;
    public abstract NetworkInterface getNetworkInterface(InetAddress address) throws SocketException;
    public abstract Set<NetworkInterface> getNetworkInterfaces() throws SocketException;

    public abstract URLConnection openConnection(URL url) throws IOException;

    public abstract Pipe openPipe() throws IOException;
    public abstract Selector openSelector() throws IOException;

    public FileDescriptor getFD(ClientSocket socket) {
        try {
            return socket.getFD();
        } catch (IOException e) {
            return null;
        }
    }
    public FileDescriptor getFD(ServerSocket socket) {
        try {
            return socket.getFD();
        } catch (IOException e) {
            return null;
        }
    }
    public FileDescriptor getFD(DatagramSocket socket) {
        try {
            return socket.getFD();
        } catch (IOException e) {
            return null;
        }
    }
    public FileDescriptor getFD(ClientSocketChannel channel) {
        try {
            return channel.getFD();
        } catch (IOException e) {
            return null;
        }
    }
    public FileDescriptor getFD(ServerSocketChannel channel) {
        try {
            return channel.getFD();
        } catch (IOException e) {
            return null;
        }
    }
    public FileDescriptor getFD(DatagramSocketChannel channel) {
        try {
            return channel.getFD();
        } catch (IOException e) {
            return null;
        }
    }
    public FileDescriptor getFD(Pipe.SourceChannel channel) {
        try {
            return channel.getFD();
        } catch (IOException e) {
            return null;
        }
    }
    public FileDescriptor getFD(Pipe.SinkChannel channel) {
        try {
            return channel.getFD();
        } catch (IOException e) {
            return null;
        }
    }

}
