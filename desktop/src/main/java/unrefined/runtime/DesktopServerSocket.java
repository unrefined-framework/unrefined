package unrefined.runtime;

import unrefined.desktop.NetSupport;
import unrefined.net.ClientSocket;
import unrefined.net.InetAddress;
import unrefined.net.InetSocketAddress;
import unrefined.net.ServerSocket;
import unrefined.net.SocketException;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;
import java.util.Objects;

public class DesktopServerSocket extends ServerSocket {

    private final java.net.ServerSocket socket;
    private final unrefined.nio.channels.ServerSocketChannel channel;

    public DesktopServerSocket(java.net.ServerSocket socket) {
        this.socket = Objects.requireNonNull(socket);
        ServerSocketChannel channel = socket.getChannel();
        this.channel = channel == null ? null : new DesktopServerSocketChannel(channel);
    }

    public java.net.ServerSocket getServerSocket() {
        return socket;
    }

    @Override
    public void bind(InetSocketAddress endpoint) throws IOException {
        socket.bind(endpoint == null ? null : ((DesktopInetSocketAddress) endpoint).getSocketAddress());
    }

    @Override
    public void bind(InetSocketAddress endpoint, int backlog) throws IOException {
        socket.bind(endpoint == null ? null : ((DesktopInetSocketAddress) endpoint).getSocketAddress(), backlog);
    }

    @Override
    public InetAddress getLocalInetAddress() {
        java.net.InetAddress address = socket.getInetAddress();
        return address == null ? null : NetSupport.toUnrefinedInetAddress(address);
    }

    @Override
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    @Override
    public InetSocketAddress getLocalSocketAddress() {
        java.net.InetSocketAddress address = (java.net.InetSocketAddress) socket.getLocalSocketAddress();
        return address == null ? null : new DesktopInetSocketAddress(address);
    }

    @Override
    public ClientSocket accept() throws IOException {
        try {
            return new DesktopClientSocket(socket.accept());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public unrefined.nio.channels.ServerSocketChannel getChannel() {
        return channel;
    }

    @Override
    public boolean isBound() {
        return socket.isBound();
    }

    @Override
    public boolean isOpen() {
        return !socket.isClosed();
    }

    @Override
    public void setTimeout(int timeout) throws SocketException {
        try {
            socket.setSoTimeout(timeout);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public int getTimeout() throws SocketException {
        try {
            return socket.getSoTimeout();
        }
        catch (IOException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void setReuseAddress(boolean on) throws SocketException {
        try {
            socket.setReuseAddress(on);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public boolean isReuseAddress() throws SocketException {
        try {
            return socket.getReuseAddress();
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void setReceiveBufferSize(int size) throws SocketException {
        try {
            socket.setReceiveBufferSize(size);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public int getReceiveBufferSize() throws SocketException {
        try {
            return socket.getReceiveBufferSize();
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public FileDescriptor getFD() throws IOException {
        return NetSupport.getFD(socket);
    }

    @Override
    public void close() throws IOException {
        try {
            socket.close();
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopServerSocket that = (DesktopServerSocket) object;

        return socket.equals(that.socket);
    }

    @Override
    public int hashCode() {
        return socket.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "local=" + getLocalSocketAddress() +
                '}';
    }

}
