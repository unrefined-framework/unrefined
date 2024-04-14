package unrefined.nio.channels;

import unrefined.net.InetAddress;
import unrefined.net.NetworkInterface;

import java.io.IOException;

public abstract class MembershipKey {

    public abstract boolean isValid();
    public abstract void drop();
    public abstract void block(InetAddress source) throws IOException;
    public abstract void unblock(InetAddress source);
    public abstract MulticastChannel getChannel();
    public abstract InetAddress getGroup();
    public abstract NetworkInterface getNetworkInterface();
    public abstract InetAddress getSource();

}
