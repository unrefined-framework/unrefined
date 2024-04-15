package unrefined.net;

public abstract class Inet6Address extends InetAddress {

    public static Inet6Address of(String host, byte[] address, NetworkInterface nif) throws UnknownHostException {
        return Net.getInstance().createInet6Address(host, address, nif);
    }
    public static Inet6Address of(String host, byte[] address, int scope) throws UnknownHostException {
        return Net.getInstance().createInet6Address(host, address, scope);
    }

    public abstract int getScope();
    public abstract NetworkInterface getScopedNetworkInterface();
    public abstract boolean isIPv4CompatibleIPv6Address();
    public abstract boolean isIPv4Address();

}
