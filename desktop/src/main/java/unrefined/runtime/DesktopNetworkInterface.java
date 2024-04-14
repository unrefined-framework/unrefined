package unrefined.runtime;

import unrefined.desktop.NetSupport;
import unrefined.net.InetAddress;
import unrefined.net.NetworkInterface;
import unrefined.net.SocketException;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DesktopNetworkInterface extends NetworkInterface {

    private final java.net.NetworkInterface networkInterface;
    private final Set<NetworkInterface> children;
    private final NetworkInterface parent;
    private final Set<InetAddress> inetAddresses;
    private final Set<Address> addresses;

    public DesktopNetworkInterface(java.net.NetworkInterface networkInterface) {
        this.networkInterface = Objects.requireNonNull(networkInterface);
        java.net.NetworkInterface parent = networkInterface.getParent();
        this.parent = parent == null ? null : new DesktopNetworkInterface(parent);
        Set<NetworkInterface> children = new HashSet<>();
        for (Enumeration<java.net.NetworkInterface> enumeration = networkInterface.getSubInterfaces(); enumeration.hasMoreElements();) {
            children.add(new DesktopNetworkInterface(enumeration.nextElement()));
        }
        this.children = Collections.unmodifiableSet(children);
        Set<InetAddress> inetAddresses = new HashSet<>();
        for (Enumeration<java.net.InetAddress> enumeration = networkInterface.getInetAddresses(); enumeration.hasMoreElements();) {
            inetAddresses.add(NetSupport.toUnrefinedInetAddress(enumeration.nextElement()));
        }
        this.inetAddresses = Collections.unmodifiableSet(inetAddresses);
        Set<Address> addresses = new HashSet<>();
        for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
            addresses.add(new Address(NetSupport.toUnrefinedInetAddress(address.getAddress()),
                    new DesktopInet4Address((Inet4Address) address.getBroadcast()), address.getNetworkPrefixLength()));
        }
        this.addresses = Collections.unmodifiableSet(addresses);
    }

    public java.net.NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    @Override
    public String getName() {
        return networkInterface.getName();
    }

    @Override
    public Set<InetAddress> getInetAddresses() {
        return inetAddresses;
    }

    @Override
    public Set<Address> getAddresses() {
        return addresses;
    }

    @Override
    public Set<NetworkInterface> getChildren() {
        return children;
    }

    @Override
    public NetworkInterface getParent() {
        return parent;
    }

    @Override
    public int getIndex() {
        return networkInterface.getIndex();
    }

    @Override
    public String getDisplayName() {
        return networkInterface.getDisplayName();
    }

    @Override
    public boolean isUp() throws SocketException {
        try {
            return networkInterface.isUp();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public boolean isLoopback() throws SocketException {
        try {
            return networkInterface.isLoopback();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public boolean isPointToPoint() throws SocketException {
        try {
            return networkInterface.isPointToPoint();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public boolean isMulticastSupported() throws SocketException {
        try {
            return networkInterface.supportsMulticast();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public byte[] getHardwareAddress() throws SocketException {
        try {
            return networkInterface.getHardwareAddress();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public int getMTU() throws SocketException {
        try {
            return networkInterface.getMTU();
        } catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
    }

    @Override
    public boolean isVirtual() {
        return networkInterface.isVirtual();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopNetworkInterface that = (DesktopNetworkInterface) object;

        return networkInterface.equals(that.networkInterface);
    }

    @Override
    public int hashCode() {
        return networkInterface.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "name=" + getName() +
                ", displayName=" + getDisplayName() +
                '}';
    }

}
