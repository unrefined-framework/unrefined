package unrefined.net;

public abstract class InetSocketAddress extends SocketAddress {

    public abstract int getPort();
    public abstract InetAddress getInetAddress();
    public abstract boolean isUnresolved();

}
