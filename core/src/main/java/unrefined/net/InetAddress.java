package unrefined.net;

import java.io.IOException;

public abstract class InetAddress {

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
