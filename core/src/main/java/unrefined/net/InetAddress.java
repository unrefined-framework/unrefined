package unrefined.net;

import java.io.IOException;
import java.util.Set;

public abstract class InetAddress {

    public static InetAddress of(String host, byte[] address) throws UnknownHostException {
        return Net.getInstance().createInetAddress(host, address);
    }
    public static InetAddress of(byte[] address) throws UnknownHostException {
        return Net.getInstance().createInetAddress(address);
    }
    public static InetAddress get(String host) throws UnknownHostException {
        return Net.getInstance().getInetAddress(host);
    }
    public static Set<InetAddress> getAll(String host) throws UnknownHostException {
        return Net.getInstance().getInetAddresses(host);
    }
    public static InetAddress getLoopback() {
        return Net.getInstance().getLoopback();
    }
    public static InetAddress getLocalHost() throws UnknownHostException {
        return Net.getInstance().getLocalHost();
    }

    public abstract boolean isMulticastAddress();
    public abstract boolean isAnyLocalAddress();
    public abstract boolean isLoopbackAddress();
    public abstract boolean isLinkLocalAddress();
    public abstract boolean isSiteLocalAddress();
    public abstract boolean isMulticastGlobal();
    public abstract boolean isMulticastNodeLocal();
    public abstract boolean isMulticastLinkLocal();
    public abstract boolean isMulticastSiteLocal();
    public abstract boolean isMulticastOrganizationLocal();
    public abstract boolean isReachable(int timeout) throws IOException;
    public abstract boolean isReachable(NetworkInterface nif, int ttl, int timeout) throws IOException;
    public abstract String getHostName();
    public abstract String getCanonicalHostName();
    public abstract byte[] getAddress();
    public abstract String getHostAddress();

}
