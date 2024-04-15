package unrefined.net;

import java.util.Objects;
import java.util.Set;

public abstract class NetworkInterface {

    public NetworkInterface of(String name) throws SocketException {
        return Net.getInstance().getNetworkInterface(name);
    }
    public NetworkInterface of(InetAddress address) throws SocketException {
        return Net.getInstance().getNetworkInterface(address);
    }
    public NetworkInterface get(int index) throws SocketException {
        return Net.getInstance().getNetworkInterface(index);
    }
    public Set<NetworkInterface> getAll() throws SocketException {
        return Net.getInstance().getNetworkInterfaces();
    }

    public static class Address {

        private final InetAddress address;
        private final Inet4Address broadcast;
        private final short networkPrefixLength;

        public Address(InetAddress address, Inet4Address broadcast, short networkPrefixLength) {
            this.address = Objects.requireNonNull(address);
            this.broadcast = Objects.requireNonNull(broadcast);
            this.networkPrefixLength = networkPrefixLength;
        }

        public InetAddress getInetAddress() {
            return address;
        }
        public Inet4Address getBroadcast() {
            return broadcast;
        }
        public short getNetworkPrefixLength() {
            return networkPrefixLength;
        }

    }

    public abstract String getName();
    public abstract Set<InetAddress> getInetAddresses();
    public abstract Set<Address> getAddresses();
    public abstract Set<NetworkInterface> getChildren();
    public abstract NetworkInterface getParent();
    public abstract int getIndex();
    public abstract String getDisplayName();
    public abstract boolean isUp() throws SocketException;
    public abstract boolean isLoopback() throws SocketException;
    public abstract boolean isPointToPoint() throws SocketException;
    public abstract boolean isMulticastSupported() throws SocketException;
    public abstract byte[] getHardwareAddress() throws SocketException;
    public abstract int getMTU() throws SocketException;
    public abstract boolean isVirtual();

}
