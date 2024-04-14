package unrefined.runtime;

import unrefined.desktop.NetSupport;
import unrefined.net.InetSocketAddress;
import unrefined.net.ServerSocket;
import unrefined.net.SocketException;
import unrefined.net.UnknownHostException;
import unrefined.nio.channels.ClientSocketChannel;
import unrefined.nio.channels.SelectableChannel;
import unrefined.nio.channels.SelectionKey;
import unrefined.nio.channels.Selector;
import unrefined.nio.channels.ServerSocketChannel;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public class DesktopServerSocketChannel extends ServerSocketChannel {

    private final java.nio.channels.ServerSocketChannel channel;
    private final ServerSocket socket;

    public DesktopServerSocketChannel(java.nio.channels.ServerSocketChannel channel) {
        this.channel = Objects.requireNonNull(channel);
        java.net.ServerSocket socket = channel.socket();
        this.socket = socket == null ? null : new DesktopServerSocket(socket);
    }

    public java.nio.channels.ServerSocketChannel getChannel() {
        return channel;
    }

    @Override
    public boolean isRegistered() {
        return channel.isRegistered();
    }

    @Override
    public SelectionKey keyFor(Selector selector) {
        java.nio.channels.SelectionKey selectionKey = channel.keyFor(selector == null ? null : ((DesktopSelector) selector).getSelector());
        return selectionKey == null ? null : new DesktopSelectionKey(selectionKey, this);
    }

    @Override
    public SelectionKey register(Selector selector, int operations, Object attachment) throws ClosedChannelException {
        java.nio.channels.SelectionKey selectionKey = channel.register(selector == null ? null : ((DesktopSelector) selector).getSelector(), operations, attachment);
        return selectionKey == null ? null : new DesktopSelectionKey(selectionKey, this);
    }

    @Override
    public SelectionKey register(Selector selector, int operations) throws ClosedChannelException {
        java.nio.channels.SelectionKey selectionKey = channel.register(selector == null ? null : ((DesktopSelector) selector).getSelector(), operations);
        return selectionKey == null ? null : new DesktopSelectionKey(selectionKey, this);
    }

    @Override
    public SelectableChannel configureBlocking(boolean block) throws IOException {
        try {
            channel.configureBlocking(block);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
        return this;
    }

    @Override
    public boolean isBlocking() {
        return channel.isBlocking();
    }

    @Override
    public Object getBlockingLock() {
        return channel.blockingLock();
    }

    @Override
    public void bind(InetSocketAddress local) throws IOException {
        try {
            channel.bind(local == null ? null : ((DesktopInetSocketAddress) local).getSocketAddress());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public void bind(InetSocketAddress local, int backlog) throws IOException {
        try {
            channel.bind(local == null ? null : ((DesktopInetSocketAddress) local).getSocketAddress(), backlog);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public ServerSocket getSocket() {
        return socket;
    }

    @Override
    public ClientSocketChannel accept() throws IOException {
        SocketChannel channel = this.channel.accept();
        return channel == null ? null : new DesktopClientSocketChannel(channel);
    }

    @Override
    public InetSocketAddress getLocalSocketAddress() throws IOException {
        try {
            java.net.InetSocketAddress address = (java.net.InetSocketAddress) channel.getLocalAddress();
            return address == null ? null : new DesktopInetSocketAddress(address);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public FileDescriptor getFD() throws IOException {
        return NetSupport.getFD(channel);
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        try {
            channel.close();
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

        DesktopServerSocketChannel that = (DesktopServerSocketChannel) object;

        return channel.equals(that.channel);
    }

    @Override
    public int hashCode() {
        return channel.hashCode();
    }

    @Override
    public String toString() {
        try {
            return getClass().getName()
                    + '{' +
                    "local=" + getLocalSocketAddress() +
                    '}';
        } catch (IOException e) {
            return getClass().getName()
                    + '{' +
                    "local=null" +
                    '}';
        }
    }

}
