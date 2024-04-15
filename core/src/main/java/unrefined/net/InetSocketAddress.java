package unrefined.net;

public abstract class InetSocketAddress extends SocketAddress {

    public static InetSocketAddress of(int port) {
        return Net.getInstance().createInetSocketAddress(port);
    }
    public static InetSocketAddress of(InetAddress address, int port) {
        return Net.getInstance().createInetSocketAddress(address, port);
    }
    public static InetSocketAddress of(String host, int port) {
        return Net.getInstance().createInetSocketAddress(host, port);
    }
    public static InetSocketAddress ofUnresolved(String host, int port) {
        return Net.getInstance().createUnresolvedInetSocketAddress(host, port);
    }

    public abstract int getPort();
    public abstract InetAddress getInetAddress();
    public abstract boolean isUnresolved();

}
