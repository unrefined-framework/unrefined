package unrefined.runtime;

import unrefined.desktop.NetSupport;
import unrefined.net.ClientSocket;
import unrefined.net.InetSocketAddress;
import unrefined.net.SocketException;
import unrefined.nio.channels.ClientSocketChannel;
import unrefined.nio.channels.SelectableChannel;
import unrefined.nio.channels.SelectionKey;
import unrefined.nio.channels.Selector;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public class DesktopClientSocketChannel extends ClientSocketChannel {

    private final SocketChannel channel;
    private final ClientSocket socket;

    public DesktopClientSocketChannel(SocketChannel channel) {
        this.channel = Objects.requireNonNull(channel);
        Socket socket = channel.socket();
        this.socket = socket == null ? null : new DesktopClientSocket(socket);
    }

    public SocketChannel getChannel() {
        return channel;
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

    private volatile boolean inputShutdown = false, outputShutdown = false;

    @Override
    public synchronized void shutdownInput() throws IOException {
        try {
            channel.shutdownInput();
            inputShutdown = true;
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public synchronized void shutdownOutput() throws IOException {
        try {
            channel.shutdownOutput();
            outputShutdown = true;
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public boolean isInputShutdown() {
        return inputShutdown;
    }

    @Override
    public boolean isOutputShutdown() {
        return outputShutdown;
    }

    @Override
    public ClientSocket getSocket() {
        return socket;
    }

    @Override
    public boolean isConnected() {
        return channel.isConnected();
    }

    @Override
    public boolean isConnectionPending() {
        return channel.isConnectionPending();
    }

    @Override
    public boolean connect(InetSocketAddress remote) throws IOException {
        try {
            return channel.connect(remote == null ? null : ((DesktopInetSocketAddress) remote).getSocketAddress());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public boolean finishConnect() throws IOException {
        try {
            return channel.finishConnect();
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
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
    public InetSocketAddress getRemoteSocketAddress() throws IOException {
        try {
            java.net.InetSocketAddress address = (java.net.InetSocketAddress) channel.getRemoteAddress();
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
    public int read(ByteBuffer dst) throws IOException {
        try {
            return channel.read(dst);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        try {
            return channel.read(dsts, offset, length);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        try {
            return channel.write(src);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        try {
            return channel.write(srcs, offset, length);
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

        DesktopClientSocketChannel that = (DesktopClientSocketChannel) object;

        return channel.equals(that.channel);
    }

    @Override
    public int hashCode() {
        return channel.hashCode();
    }

    @Override
    public String toString() {
        if (isConnected()) {
            try {
                return getClass().getName()
                        + '{' +
                        "connected=true" +
                        ", remote=" + getRemoteSocketAddress() +
                        ", local=" + getLocalSocketAddress() +
                        ", inputShutdown=" + isInputShutdown() +
                        ", outputShutdown=" + isOutputShutdown() +
                        '}';
            } catch (IOException e) {
                return getClass().getName()
                        + '{' +
                        "connected=true" +
                        ", remote=null" +
                        ", local=null" +
                        ", inputShutdown=" + isInputShutdown() +
                        ", outputShutdown=" + isOutputShutdown() +
                        '}';
            }
        }
        else {
            return getClass().getName()
                    + '{' +
                    "connected=false" +
                    ", connectionPending=" + isConnectionPending() +
                    '}';
        }
    }

}
