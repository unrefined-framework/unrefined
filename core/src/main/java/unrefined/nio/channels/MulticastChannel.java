package unrefined.nio.channels;

import unrefined.net.InetAddress;
import unrefined.net.NetworkInterface;

import java.io.IOException;

public interface MulticastChannel extends NetworkChannel {

    MembershipKey join(InetAddress group, NetworkInterface nif) throws IOException;
    MembershipKey join(InetAddress group, NetworkInterface nif, InetAddress source) throws IOException;

    void setTTL(int ttl) throws IOException;
    int getTTL() throws IOException;
    void setNetworkInterface(NetworkInterface nif) throws IOException;
    NetworkInterface getNetworkInterface() throws IOException;
    void setLoopbackMode(boolean on) throws IOException;
    boolean isLoopbackMode() throws IOException;

}
