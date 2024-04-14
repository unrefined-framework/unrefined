package unrefined.runtime;

import unrefined.desktop.NetSupport;
import unrefined.net.ClientSocket;
import unrefined.net.InetAddress;
import unrefined.net.InetSocketAddress;
import unrefined.net.SocketException;
import unrefined.nio.channels.ClientSocketChannel;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class DesktopClientSocket extends ClientSocket {

    private final Socket socket;
    private final ClientSocketChannel channel;

    public DesktopClientSocket(Socket socket) {
        this.socket = Objects.requireNonNull(socket);
        this.channel = socket.getChannel() == null ? null : new DesktopClientSocketChannel(socket.getChannel());
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void connect(InetSocketAddress remote) throws IOException {
        try {
            socket.connect(remote == null ? null : ((DesktopInetSocketAddress) remote).getSocketAddress());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public void connect(InetSocketAddress remote, int timeout) throws IOException {
        try {
            socket.connect(remote == null ? null : ((DesktopInetSocketAddress) remote).getSocketAddress(), timeout);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public void bind(InetSocketAddress local) throws IOException {
        try {
            socket.bind(local == null ? null : ((DesktopInetSocketAddress) local).getSocketAddress());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public InetAddress getRemoteInetAddress() {
        java.net.InetAddress address = socket.getInetAddress();
        return address == null ? null : NetSupport.toUnrefinedInetAddress(address);
    }

    @Override
    public InetAddress getLocalInetAddress() {
        java.net.InetAddress address = socket.getLocalAddress();
        return address == null ? null : NetSupport.toUnrefinedInetAddress(address);
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
        java.net.InetSocketAddress address = (java.net.InetSocketAddress) socket.getRemoteSocketAddress();
        return address == null ? null : new DesktopInetSocketAddress(address);
    }

    @Override
    public InetSocketAddress getLocalSocketAddress() {
        java.net.InetSocketAddress address = (java.net.InetSocketAddress) socket.getLocalSocketAddress();
        return address == null ? null : new DesktopInetSocketAddress(address);
    }

    @Override
    public ClientSocketChannel getChannel() {
        return channel;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            return socket.getInputStream();
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        try {
            return socket.getOutputStream();
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public void setNoDelay(boolean on) throws SocketException {
        try {
            socket.setTcpNoDelay(on);
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public boolean isNoDelay() throws SocketException {
        try {
            return socket.getTcpNoDelay();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void setLinger(boolean on, int linger) throws SocketException {
        try {
            socket.setSoLinger(on, linger);
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public int getLinger() throws SocketException {
        try {
            return socket.getSoLinger();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void setOOBInline(boolean on) throws SocketException {
        try {
            socket.setOOBInline(on);
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public boolean isOOBInline() throws SocketException {
        try {
            return socket.getOOBInline();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void setTimeout(int timeout) throws SocketException {
        try {
            socket.setSoTimeout(timeout);
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public int getTimeout() throws SocketException {
        try {
            return socket.getSoTimeout();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void setSendBufferSize(int size) throws SocketException {
        try {
            socket.setSendBufferSize(size);
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public int getSendBufferSize() throws SocketException {
        try {
            return socket.getSendBufferSize();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void setReceiveBufferSize(int size) throws SocketException {
        try {
            socket.setReceiveBufferSize(size);
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public int getReceiveBufferSize() throws SocketException {
        try {
            return socket.getReceiveBufferSize();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void setKeepAlive(boolean on) throws SocketException {
        try {
            socket.setKeepAlive(on);
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public boolean isKeepAlive() throws SocketException {
        try {
            return socket.getKeepAlive();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void setTrafficClass(int tc) throws SocketException {
        try {
            socket.setTrafficClass(tc);
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public int getTrafficClass() throws SocketException {
        try {
            return socket.getTrafficClass();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void setReuseAddress(boolean on) throws SocketException {
        try {
            socket.setReuseAddress(on);
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public boolean isReuseAddress() throws SocketException {
        try {
            return socket.getReuseAddress();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public void sendUrgentData(int data) throws IOException {
        try {
            socket.sendUrgentData(data);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public void shutdownInput() throws IOException {
        try {
            socket.shutdownInput();
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public void shutdownOutput() throws IOException {
        try {
            socket.shutdownOutput();
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
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
    public boolean isInputShutdown() {
        return socket.isInputShutdown();
    }

    @Override
    public boolean isOutputShutdown() {
        return socket.isOutputShutdown();
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

        DesktopClientSocket that = (DesktopClientSocket) object;

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
                    ", inputShutdown=" + isInputShutdown() +
                    ", outputShutdown" + isOutputShutdown() +
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
