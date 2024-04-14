package unrefined.runtime;

import unrefined.desktop.NetSupport;
import unrefined.net.InetAddress;
import unrefined.net.NetworkInterface;
import unrefined.net.SocketException;
import unrefined.net.UnknownHostException;
import unrefined.nio.channels.MembershipKey;
import unrefined.nio.channels.DatagramSocketChannel;
import unrefined.nio.channels.MulticastChannel;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.util.Objects;

public class DesktopMembershipKey extends MembershipKey {

    private final java.nio.channels.MembershipKey membershipKey;
    private final DatagramSocketChannel channel;
    private final InetAddress group;
    private final NetworkInterface nif;
    private final InetAddress source;

    public DesktopMembershipKey(java.nio.channels.MembershipKey membershipKey) {
        this.membershipKey = Objects.requireNonNull(membershipKey);
        DatagramChannel channel = (DatagramChannel) membershipKey.channel();
        this.channel = channel == null ? null : new DesktopDatagramSocketChannel(channel);
        java.net.InetAddress address = membershipKey.group();
        this.group = address == null ? null : NetSupport.toUnrefinedInetAddress(address);
        address = membershipKey.sourceAddress();
        this.source = address == null ? null : NetSupport.toUnrefinedInetAddress(address);
        java.net.NetworkInterface nif = membershipKey.networkInterface();
        this.nif = nif == null ? null : new DesktopNetworkInterface(nif);
    }

    public java.nio.channels.MembershipKey getMembershipKey() {
        return membershipKey;
    }

    @Override
    public boolean isValid() {
        return membershipKey.isValid();
    }

    @Override
    public void drop() {
        membershipKey.drop();
    }

    @Override
    public void block(InetAddress source) throws IOException {
        try {
            membershipKey.block(NetSupport.toInetAddress(source));
        }
        catch (java.net.SocketException e) {
            throw new SocketException(e.getMessage());
        }
        catch (java.net.UnknownHostException e) {
            throw new UnknownHostException(e.getMessage());
        }
    }

    @Override
    public void unblock(InetAddress source) {
        membershipKey.unblock(NetSupport.toInetAddress(source));
    }

    @Override
    public MulticastChannel getChannel() {
        return channel;
    }

    @Override
    public InetAddress getGroup() {
        return group;
    }

    @Override
    public NetworkInterface getNetworkInterface() {
        return nif;
    }

    @Override
    public InetAddress getSource() {
        return source;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopMembershipKey that = (DesktopMembershipKey) object;

        return membershipKey.equals(that.membershipKey);
    }

    @Override
    public int hashCode() {
        return membershipKey.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "interface=" + nif +
                ", group=" + group +
                ", source=" + source +
                '}';
    }

}
