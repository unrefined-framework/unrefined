package unrefined.runtime;

import unrefined.net.Inet6Address;
import unrefined.net.NetworkInterface;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Objects;

public class DesktopInet6Address extends Inet6Address {

    private final java.net.Inet6Address address;
    private final NetworkInterface scopedInterface;

    public DesktopInet6Address(java.net.Inet6Address address) {
        this.address = Objects.requireNonNull(address);
        java.net.NetworkInterface networkInterface = address.getScopedInterface();
        this.scopedInterface = networkInterface == null ? null : new DesktopNetworkInterface(networkInterface);
    }

    public java.net.Inet6Address getInet6Address() {
        return address;
    }

    @Override
    public int getScope() {
        return address.getScopeId();
    }

    @Override
    public NetworkInterface getScopedNetworkInterface() {
        return scopedInterface;
    }

    @Override
    public boolean isIPv4CompatibleIPv6Address() {
        return address.isIPv4CompatibleAddress();
    }

    @Override
    public boolean isIPv4Address() {
        return !address.isIPv4CompatibleAddress();
    }

    @Override
    public boolean isMulticastAddress() {
        return address.isMulticastAddress();
    }

    @Override
    public boolean isAnyLocalAddress() {
        return address.isAnyLocalAddress();
    }

    @Override
    public boolean isLoopbackAddress() {
        return address.isLoopbackAddress();
    }

    @Override
    public boolean isLinkLocalAddress() {
        return address.isLinkLocalAddress();
    }

    @Override
    public boolean isSiteLocalAddress() {
        return address.isSiteLocalAddress();
    }

    @Override
    public boolean isMulticastGlobal() {
        return address.isMCGlobal();
    }

    @Override
    public boolean isMulticastNodeLocal() {
        return address.isMCNodeLocal();
    }

    @Override
    public boolean isMulticastLinkLocal() {
        return address.isMCLinkLocal();
    }

    @Override
    public boolean isMulticastSiteLocal() {
        return address.isMCSiteLocal();
    }

    @Override
    public boolean isMulticastOrganizationLocal() {
        return address.isMCOrgLocal();
    }

    @Override
    public boolean isReachable(int timeout) throws IOException {
        try {
            return address.isReachable(timeout);
        }
        catch (SocketException e) {
            throw new unrefined.net.SocketException(e.getMessage());
        }
        catch (UnknownHostException e) {
            throw new unrefined.net.UnknownHostException(e.getMessage());
        }
    }

    @Override
    public boolean isReachable(NetworkInterface nif, int ttl, int timeout) throws IOException {
        try {
            return address.isReachable(nif == null ? null : ((DesktopNetworkInterface) nif).getNetworkInterface(), ttl, timeout);
        }
        catch (SocketException e) {
            throw new unrefined.net.SocketException(e.getMessage());
        }
        catch (UnknownHostException e) {
            throw new unrefined.net.UnknownHostException(e.getMessage());
        }
    }

    @Override
    public String getHostName() {
        return address.getHostName();
    }

    @Override
    public String getCanonicalHostName() {
        return address.getCanonicalHostName();
    }

    @Override
    public byte[] getAddress() {
        return address.getAddress();
    }

    @Override
    public String getHostAddress() {
        return address.getHostAddress();
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "hostName=" + getHostName() +
                ", hostAddress=" + getHostAddress() +
                '}';
    }

}
