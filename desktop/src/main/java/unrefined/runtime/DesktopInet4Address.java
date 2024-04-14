package unrefined.runtime;

import unrefined.net.Inet4Address;
import unrefined.net.NetworkInterface;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Objects;

public class DesktopInet4Address extends Inet4Address {

    private final java.net.Inet4Address address;

    public DesktopInet4Address(java.net.Inet4Address address) {
        this.address = Objects.requireNonNull(address);
    }

    public java.net.Inet4Address getInet4Address() {
        return address;
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
