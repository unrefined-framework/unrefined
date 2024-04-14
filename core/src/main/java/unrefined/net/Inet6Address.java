package unrefined.net;

public abstract class Inet6Address extends InetAddress {

    public abstract int getScope();
    public abstract NetworkInterface getScopedNetworkInterface();
    public abstract boolean isIPv4CompatibleIPv6Address();
    public abstract boolean isIPv4Address();

}
