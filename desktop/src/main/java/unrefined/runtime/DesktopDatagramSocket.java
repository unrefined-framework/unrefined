package unrefined.runtime;

import unrefined.desktop.NetSupport;
import unrefined.net.DatagramPacket;
import unrefined.net.DatagramSocket;
import unrefined.net.InetAddress;
import unrefined.net.InetSocketAddress;
import unrefined.net.NetworkInterface;
import unrefined.net.SocketException;
import unrefined.net.UnknownHostException;
import unrefined.nio.channels.DatagramSocketChannel;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.util.Objects;

public class DesktopDatagramSocket extends DatagramSocket {

    private final java.net.MulticastSocket socket;
    private final DatagramSocketChannel channel;

    public DesktopDatagramSocket(java.net.MulticastSocket socket) {
        this.socket = Objects.requireNonNull(socket);
        DatagramChannel channel = socket.getChannel();
        this.channel = channel == null ? null : new DesktopDatagramSocketChannel(channel);
    }

    public java.net.MulticastSocket getSocket() {
        return socket;
    }

    @Override
    public void bind(InetSocketAddress address) throws SocketException {
        try {
            socket.bind(address == null ? null : ((DesktopInetSocketAddress) address).getSocketAddress());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void connect(InetAddress address, int port) throws IOException {
        try {
            socket.connect(address == null ? null : NetSupport.toInetAddress(address), port);
        }
        catch (UncheckedIOException e) {
            IOException ioe = e.getCause();
            if (ioe instanceof java.net.SocketException) throw new SocketException(ioe.getMessage());
            else if (ioe instanceof java.net.UnknownHostException) throw new UnknownHostException(ioe.getMessage());
            else throw ioe;
        }
    }

    @Override
    public void connect(InetSocketAddress address) throws IOException {
        try {
            socket.connect(address == null ? null : ((DesktopInetSocketAddress) address).getSocketAddress());
        }
        catch (UncheckedIOException e) {
            IOException ioe = e.getCause();
            if (ioe instanceof java.net.SocketException) throw new SocketException(ioe.getMessage());
            else if (ioe instanceof java.net.UnknownHostException) throw new UnknownHostException(ioe.getMessage());
            else throw ioe;
        }
    }

    @Override
    public void disconnect() throws IOException {
        try {
            socket.disconnect();
        }
        catch (UncheckedIOException e) {
            IOException ioe = e.getCause();
            if (ioe instanceof java.net.SocketException) throw new SocketException(ioe.getMessage());
            else if (ioe instanceof java.net.UnknownHostException) throw new UnknownHostException(ioe.getMessage());
            else throw ioe;
        }
    }

    @Override
    public boolean isBound() {
        return socket.isBound();
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    public InetAddress getRemoteInetAddress() {
        return NetSupport.toUnrefinedInetAddress(socket.getInetAddress());
    }

    @Override
    public InetAddress getLocalInetAddress() {
        return NetSupport.toUnrefinedInetAddress(socket.getLocalAddress());
    }

    @Override
    public int getRemotePort() {
        return socket.getPort();
    }

    @Override
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress() {
        return new DesktopInetSocketAddress((java.net.InetSocketAddress) socket.getRemoteSocketAddress());
    }

    @Override
    public InetSocketAddress getLocalSocketAddress() {
        return new DesktopInetSocketAddress((java.net.InetSocketAddress) socket.getLocalSocketAddress());
    }

    @Override
    public void send(DatagramPacket packet) throws IOException {
        try {
            socket.send(((DesktopDatagramPacket) packet).getPacket());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public void receive(DatagramPacket packet) throws IOException {
        try {
            socket.receive(((DesktopDatagramPacket) packet).getPacket());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
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
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void setSendBufferSize(int size) throws SocketException {
        try {
            socket.setSendBufferSize(size);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public int getSendBufferSize() throws SocketException {
        try {
            return socket.getSendBufferSize();
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
    public void setBroadcast(boolean on) throws SocketException {
        try {
            socket.setBroadcast(on);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public boolean isBroadcast() throws SocketException {
        try {
            return socket.getBroadcast();
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void setTrafficClass(int tc) throws SocketException {
        try {
            socket.setTrafficClass(tc);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public int getTrafficClass() throws SocketException {
        try {
            return socket.getTrafficClass();
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public boolean isOpen() {
        return !socket.isClosed();
    }

    @Override
    public DatagramSocketChannel getChannel() {
        return channel;
    }

    @Override
    public FileDescriptor getFD() throws IOException {
        return NetSupport.getFD(socket);
    }

    @Override
    public void joinGroup(InetSocketAddress group, NetworkInterface nif) throws IOException {
        try {
            socket.joinGroup(group == null ? null : ((DesktopInetSocketAddress) group).getSocketAddress(),
                    nif == null ? null : ((DesktopNetworkInterface) nif).getNetworkInterface());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public void leaveGroup(InetSocketAddress group, NetworkInterface nif) throws IOException {
        try {
            socket.leaveGroup(group == null ? null : ((DesktopInetSocketAddress) group).getSocketAddress(),
                    nif == null ? null : ((DesktopNetworkInterface) nif).getNetworkInterface());
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
            socket.setTimeToLive(ttl);
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
            return socket.getTimeToLive();
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
        if (!isOpen()) throw new ClosedChannelException();
        try {
            socket.setNetworkInterface(((DesktopNetworkInterface) nif).getNetworkInterface());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public NetworkInterface getNetworkInterface() throws IOException {
        if (!isOpen()) throw new ClosedChannelException();
        try {
            return new DesktopNetworkInterface(socket.getNetworkInterface());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void setLoopbackMode(boolean on) throws IOException {
        if (!isOpen()) throw new ClosedChannelException();
        try {
            socket.setLoopbackMode(on);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public boolean isLoopbackMode() throws IOException {
        if (!isOpen()) throw new ClosedChannelException();
        try {
            return socket.getLoopbackMode();
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopDatagramSocket that = (DesktopDatagramSocket) object;

        return socket.equals(that.socket);
    }

    @Override
    public int hashCode() {
        return socket.hashCode();
    }

    @Override
    public String toString() {
        if (isConnected()) {
            return getClass().getName()
                    + '{' +
                    "connected=true" +
                    ", remote=" + getRemoteSocketAddress() +
                    ", local=" + getLocalSocketAddress() +
                    '}';
        }
        else {
            return getClass().getName()
                    + '{' +
                    "connected=false" +
                    '}';
        }
    }

}
