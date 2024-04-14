package unrefined.net;

import java.io.IOException;

public abstract class MulticastSocket extends Socket {

    public abstract void joinGroup(InetSocketAddress group, NetworkInterface nif) throws IOException;
    public abstract void leaveGroup(InetSocketAddress group, NetworkInterface nif) throws IOException;

    public abstract void setTTL(int ttl) throws IOException;
    public abstract int getTTL() throws IOException;
    public abstract void setNetworkInterface(NetworkInterface nif) throws IOException;
    public abstract NetworkInterface getNetworkInterface() throws IOException;
    public abstract void setLoopbackMode(boolean on) throws IOException;
    public abstract boolean isLoopbackMode() throws IOException;

}
