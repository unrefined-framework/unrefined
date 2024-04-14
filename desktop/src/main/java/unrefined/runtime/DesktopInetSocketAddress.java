package unrefined.runtime;

import unrefined.desktop.NetSupport;
import unrefined.net.InetAddress;
import unrefined.net.InetSocketAddress;

import java.util.Objects;

public class DesktopInetSocketAddress extends InetSocketAddress {

    private final java.net.InetSocketAddress address;
    private final InetAddress inetAddress;
    public DesktopInetSocketAddress(java.net.InetSocketAddress address) {
        this.address = Objects.requireNonNull(address);
        java.net.InetAddress inetAddress = address.getAddress();
        this.inetAddress = inetAddress == null ? null : NetSupport.toUnrefinedInetAddress(inetAddress);
    }

    public java.net.InetSocketAddress getSocketAddress() {
        return address;
    }

    @Override
    public int getPort() {
        return address.getPort();
    }

    @Override
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    @Override
    public boolean isUnresolved() {
        return address.isUnresolved();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopInetSocketAddress that = (DesktopInetSocketAddress) object;

        return address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "port=" + getPort() +
                ", unresolved=" + isUnresolved() +
                (isUnresolved() ? "" : ", address=" + inetAddress) +
                '}';
    }

}
