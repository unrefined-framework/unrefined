package unrefined.runtime;

import unrefined.desktop.NetSupport;
import unrefined.net.DatagramSocket;
import unrefined.net.InetAddress;
import unrefined.net.InetSocketAddress;
import unrefined.net.NetworkInterface;
import unrefined.net.SocketException;
import unrefined.net.UnknownHostException;
import unrefined.nio.channels.DatagramSocketChannel;
import unrefined.nio.channels.MembershipKey;
import unrefined.nio.channels.SelectableChannel;
import unrefined.nio.channels.SelectionKey;
import unrefined.nio.channels.Selector;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.MulticastSocket;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;

public class DesktopDatagramSocketChannel extends DatagramSocketChannel {

    private final DatagramChannel channel;
    private final DesktopDatagramSocket socket;

    public DesktopDatagramSocketChannel(DatagramChannel channel) {
        this.channel = channel;
        java.net.MulticastSocket socket = (MulticastSocket) channel.socket();
        this.socket = socket == null ? null : new DesktopDatagramSocket(socket);
    }

    public DatagramChannel getChannel() {
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

    @Override
    public DatagramSocket getSocket() {
        return socket;
    }

    @Override
    public boolean isConnected() {
        return channel.isConnected();
    }

    @Override
    public void connect(InetSocketAddress remote) throws IOException {
        try {
            channel.connect(remote == null ? null : ((DesktopInetSocketAddress) remote).getSocketAddress());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public void disconnect() throws IOException {
        try {
            channel.disconnect();
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
    public InetSocketAddress receive(ByteBuffer dst) throws IOException {
        try {
            java.net.InetSocketAddress address = (java.net.InetSocketAddress) channel.receive(dst);
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
    public int send(ByteBuffer src, InetSocketAddress target) throws IOException {
        try {
            return channel.send(src, target == null ? null : ((DesktopInetSocketAddress) target).getSocketAddress());
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
    public long read(ByteBuffer[] dsts) throws IOException {
        try {
            return channel.read(dsts);
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
    public long write(ByteBuffer[] srcs) throws IOException {
        try {
            return channel.write(srcs);
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
    public FileDescriptor getFD() throws IOException {
        return NetSupport.getFD(channel);
    }

    @Override
    public MembershipKey join(InetAddress group, NetworkInterface nif) throws IOException {
        try {
            return new DesktopMembershipKey(channel.join(group == null ? null : NetSupport.toInetAddress(group),
                    nif == null ? null : ((DesktopNetworkInterface) nif).getNetworkInterface()));
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public MembershipKey join(InetAddress group, NetworkInterface nif, InetAddress source) throws IOException {
        try {
            return new DesktopMembershipKey(channel.join(group == null ? null : NetSupport.toInetAddress(group),
                    nif == null ? null : ((DesktopNetworkInterface) nif).getNetworkInterface(),
                    source == null ? null : NetSupport.toInetAddress(source)));
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public void setTTL(int ttl) throws IOException {
        try {
            channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, ttl);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public int getTTL() throws IOException {
        try {
            return channel.getOption(StandardSocketOptions.IP_MULTICAST_TTL);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public void setNetworkInterface(NetworkInterface nif) throws IOException {
        try {
            channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nif == null ? null : ((DesktopNetworkInterface) nif).getNetworkInterface());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public NetworkInterface getNetworkInterface() throws IOException {
        try {
            return new DesktopNetworkInterface(channel.getOption(StandardSocketOptions.IP_MULTICAST_IF));
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public void setLoopbackMode(boolean on) throws IOException {
        try {
            channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, on);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public boolean isLoopbackMode() throws IOException {
        try {
            return channel.getOption(StandardSocketOptions.IP_MULTICAST_LOOP);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
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

        DesktopDatagramSocketChannel that = (DesktopDatagramSocketChannel) object;

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
                        '}';
            } catch (IOException e) {
                return getClass().getName()
                        + '{' +
                        "connected=true" +
                        ", remote=null" +
                        ", local=null" +
                        '}';
            }
        }
        else {
            return getClass().getName()
                    + '{' +
                    "connected=false" +
                    '}';
        }
    }

}
