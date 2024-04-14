package unrefined.runtime;

import unrefined.desktop.NetSupport;
import unrefined.net.ClientSocket;
import unrefined.net.DatagramPacket;
import unrefined.net.DatagramSocket;
import unrefined.net.Inet6Address;
import unrefined.net.InetAddress;
import unrefined.net.InetSocketAddress;
import unrefined.net.Net;
import unrefined.net.NetworkInterface;
import unrefined.net.ServerSocket;
import unrefined.net.SocketException;
import unrefined.net.UnknownHostException;
import unrefined.nio.channels.ClientSocketChannel;
import unrefined.nio.channels.DatagramSocketChannel;
import unrefined.nio.channels.Pipe;
import unrefined.nio.channels.Selector;
import unrefined.nio.channels.ServerSocketChannel;
import unrefined.util.UnexpectedError;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class DesktopNet extends Net {

    @Override
    public InetAddress createInetAddress(String host, byte[] address) throws UnknownHostException {
        try {
            return NetSupport.toUnrefinedInetAddress(java.net.InetAddress.getByAddress(host, address));
        } catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public InetAddress createInetAddress(byte[] address) throws UnknownHostException {
        try {
            return NetSupport.toUnrefinedInetAddress(java.net.InetAddress.getByAddress(address));
        } catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public InetAddress createInetAddress(String host) throws UnknownHostException {
        try {
            return NetSupport.toUnrefinedInetAddress(java.net.InetAddress.getByName(host));
        } catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public Set<InetAddress> getInetAddresses(String host) throws UnknownHostException {
        try {
            Set<InetAddress> addresses = new HashSet<>();
            for (java.net.InetAddress address : java.net.InetAddress.getAllByName(host)) {
                addresses.add(NetSupport.toUnrefinedInetAddress(address));
            }
            return Collections.unmodifiableSet(addresses);
        } catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public InetAddress getLoopback() {
        return NetSupport.toUnrefinedInetAddress(java.net.InetAddress.getLoopbackAddress());
    }

    @Override
    public InetAddress getLocalHost() throws UnknownHostException {
        try {
            return NetSupport.toUnrefinedInetAddress(java.net.InetAddress.getLocalHost());
        } catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public Inet6Address createInet6Address(String host, byte[] address, NetworkInterface nif) throws UnknownHostException {
        try {
            return new DesktopInet6Address(java.net.Inet6Address.getByAddress(host, address, ((DesktopNetworkInterface) nif).getNetworkInterface()));
        } catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public Inet6Address createInet6Address(String host, byte[] address, int scope) throws UnknownHostException {
        try {
            return new DesktopInet6Address(java.net.Inet6Address.getByAddress(host, address, scope));
        } catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public InetSocketAddress createSocketAddress(int port) {
        return new DesktopInetSocketAddress(new java.net.InetSocketAddress(port));
    }

    @Override
    public InetSocketAddress createSocketAddress(InetAddress address, int port) {
        return new DesktopInetSocketAddress(new java.net.InetSocketAddress(address == null ? null : NetSupport.toInetAddress(address), port));
    }

    @Override
    public InetSocketAddress createSocketAddress(String host, int port) {
        return new DesktopInetSocketAddress(new java.net.InetSocketAddress(host, port));
    }

    @Override
    public InetSocketAddress createUnresolvedSocketAddress(String host, int port) {
        return new DesktopInetSocketAddress(java.net.InetSocketAddress.createUnresolved(host, port));
    }

    @Override
    public ClientSocket createClientSocket() throws IOException {
        return new DesktopClientSocket(new Socket());
    }

    @Override
    public ClientSocket createClientSocket(InetSocketAddress remote) throws IOException {
        try {
            return new DesktopClientSocket(remote == null ? new Socket() :
                    new Socket(((DesktopInetSocketAddress) remote).getSocketAddress().getAddress(), remote.getPort()));
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public ClientSocket createClientSocket(InetSocketAddress remote, InetSocketAddress local) throws IOException {
        try {
            if (remote == null && local == null) return new DesktopClientSocket(new Socket());
            else if (remote == null) return new DesktopClientSocket(new Socket(((DesktopInetSocketAddress) local).getSocketAddress().getAddress(), local.getPort()));
            else if (local == null) return new DesktopClientSocket(new Socket(((DesktopInetSocketAddress) remote).getSocketAddress().getAddress(), remote.getPort()));
            else return new DesktopClientSocket(new Socket(((DesktopInetSocketAddress) remote).getSocketAddress().getAddress(), remote.getPort(),
                        ((DesktopInetSocketAddress) local).getSocketAddress().getAddress(), local.getPort()));
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public ServerSocket createServerSocket() throws IOException {
        try {
            return new DesktopServerSocket(new java.net.ServerSocket());
        }
        catch (IOException e) {
            throw new UnexpectedError(e);
        }
    }

    @Override
    public ServerSocket createServerSocket(InetSocketAddress local) throws IOException {
        try {
            if (local == null) return new DesktopServerSocket(new java.net.ServerSocket());
            else {
                java.net.InetSocketAddress address = ((DesktopInetSocketAddress) local).getSocketAddress();
                return new DesktopServerSocket(new java.net.ServerSocket(address.getPort(), 50, address.getAddress()));
            }
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public ServerSocket createServerSocket(InetSocketAddress local, int backlog) throws IOException {
        try {
            if (local == null) return new DesktopServerSocket(new java.net.ServerSocket());
            else {
                java.net.InetSocketAddress address = ((DesktopInetSocketAddress) local).getSocketAddress();
                return new DesktopServerSocket(new java.net.ServerSocket(address.getPort(), backlog, address.getAddress()));
            }
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public DatagramSocket createDatagramSocket() throws IOException {
        try {
            return new DesktopDatagramSocket(new MulticastSocket());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public DatagramSocket createDatagramSocket(InetSocketAddress remote) throws IOException {
        try {
            if (remote == null) return new DesktopDatagramSocket(new MulticastSocket());
            else {
                MulticastSocket socket = new MulticastSocket();
                socket.connect(((DesktopInetSocketAddress) remote).getSocketAddress());
                return new DesktopDatagramSocket(socket);
            }
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public DatagramSocket createDatagramSocket(InetSocketAddress remote, InetSocketAddress local) throws IOException {
        try {
            if (remote == null && local == null) return new DesktopDatagramSocket(new MulticastSocket());
            else if (remote == null) return new DesktopDatagramSocket(new MulticastSocket(((DesktopInetSocketAddress) local).getSocketAddress()));
            else if (local == null) {
                MulticastSocket socket = new MulticastSocket();
                socket.connect(((DesktopInetSocketAddress) remote).getSocketAddress());
                return new DesktopDatagramSocket(socket);
            }
            else {
                MulticastSocket socket = new MulticastSocket(((DesktopInetSocketAddress) local).getSocketAddress());
                socket.connect(((DesktopInetSocketAddress) remote).getSocketAddress());
                return new DesktopDatagramSocket(socket);
            }
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public DatagramPacket createDatagramPacket(byte[] buffer, int offset, int length) {
        return new DesktopDatagramPacket(new java.net.DatagramPacket(buffer, offset, length));
    }

    @Override
    public DatagramPacket createDatagramPacket(byte[] buffer, int offset, int length, InetSocketAddress address) {
        return new DesktopDatagramPacket(new java.net.DatagramPacket(buffer, offset, length, ((DesktopInetSocketAddress) address).getSocketAddress()));
    }

    @Override
    public ClientSocketChannel openClientSocketChannel() throws IOException {
        try {
            return new DesktopClientSocketChannel(SocketChannel.open());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public ClientSocketChannel openClientSocketChannel(InetSocketAddress remote) throws IOException {
        try {
            return new DesktopClientSocketChannel(SocketChannel.open(((DesktopInetSocketAddress) remote).getSocketAddress()));
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public ClientSocketChannel openClientSocketChannel(InetSocketAddress remote, InetSocketAddress local) throws IOException {
        try {
            SocketChannel channel = SocketChannel.open();
            channel.bind(((DesktopInetSocketAddress) local).getSocketAddress());
            channel.connect(((DesktopInetSocketAddress) remote).getSocketAddress());
            return new DesktopClientSocketChannel(channel);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public ServerSocketChannel openServerSocketChannel() throws IOException {
        try {
            return new DesktopServerSocketChannel(java.nio.channels.ServerSocketChannel.open());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public ServerSocketChannel openServerSocketChannel(InetSocketAddress local) throws IOException {
        try {
            java.nio.channels.ServerSocketChannel channel = java.nio.channels.ServerSocketChannel.open();
            channel.bind(((DesktopInetSocketAddress) local).getSocketAddress());
            return new DesktopServerSocketChannel(channel);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public DatagramSocketChannel openDatagramSocketChannel() throws IOException {
        try {
            return new DesktopDatagramSocketChannel(DatagramChannel.open());
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public DatagramSocketChannel openDatagramSocketChannel(InetSocketAddress remote) throws IOException {
        try {
            DatagramChannel channel = DatagramChannel.open();
            channel.connect(((DesktopInetSocketAddress) remote).getSocketAddress());
            return new DesktopDatagramSocketChannel(channel);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public DatagramSocketChannel openDatagramSocketChannel(InetSocketAddress remote, InetSocketAddress local) throws IOException {
        try {
            DatagramChannel channel = DatagramChannel.open();
            channel.bind(((DesktopInetSocketAddress) local).getSocketAddress());
            channel.connect(((DesktopInetSocketAddress) remote).getSocketAddress());
            return new DesktopDatagramSocketChannel(channel);
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public NetworkInterface getNetworkInterface(String name) throws SocketException {
        try {
            return new DesktopNetworkInterface(java.net.NetworkInterface.getByName(name));
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public NetworkInterface getNetworkInterface(int index) throws SocketException {
        try {
            return new DesktopNetworkInterface(java.net.NetworkInterface.getByIndex(index));
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public NetworkInterface getNetworkInterface(InetAddress address) throws SocketException {
        try {
            return new DesktopNetworkInterface(java.net.NetworkInterface.getByInetAddress(NetSupport.toInetAddress(address)));
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public Set<NetworkInterface> getNetworkInterfaces() throws SocketException {
        try {
            Set<NetworkInterface> interfaces = new HashSet<>();
            for (Enumeration<java.net.NetworkInterface> enumeration = java.net.NetworkInterface.getNetworkInterfaces();
                 enumeration.hasMoreElements();) {
                interfaces.add(new DesktopNetworkInterface(enumeration.nextElement()));
            }
            return Collections.unmodifiableSet(interfaces);
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public URLConnection openConnection(URL url) throws IOException {
        return url.openConnection();
    }

    @Override
    public Pipe openPipe() throws IOException {
        return new DesktopPipe(java.nio.channels.Pipe.open());
    }

    @Override
    public Selector openSelector() throws IOException {
        return new DesktopSelector(java.nio.channels.Selector.open());
    }

}
