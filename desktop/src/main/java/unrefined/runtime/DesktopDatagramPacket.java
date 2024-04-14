package unrefined.runtime;

import unrefined.desktop.NetSupport;
import unrefined.net.DatagramPacket;
import unrefined.net.InetAddress;
import unrefined.net.InetSocketAddress;
import unrefined.util.FastArray;

import java.util.Objects;

public class DesktopDatagramPacket extends DatagramPacket {

    private final java.net.DatagramPacket packet;

    public DesktopDatagramPacket(java.net.DatagramPacket packet) {
        this.packet = Objects.requireNonNull(packet);
    }

    public java.net.DatagramPacket getPacket() {
        return packet;
    }

    @Override
    public void setInetAddress(InetAddress address) {
        packet.setAddress(address == null ? null : NetSupport.toInetAddress(address));
    }

    @Override
    public InetAddress getInetAddress() {
        return NetSupport.toUnrefinedInetAddress(packet.getAddress());
    }

    @Override
    public int getPort() {
        return packet.getPort();
    }

    @Override
    public byte[] getData() {
        return packet.getData();
    }

    @Override
    public int getOffset() {
        return packet.getOffset();
    }

    @Override
    public int getLength() {
        return packet.getLength();
    }

    @Override
    public void setData(byte[] buffer, int offset, int length) {
        packet.setData(buffer, offset, length);
    }

    @Override
    public void setPort(int port) {
        packet.setPort(port);
    }

    @Override
    public void setSocketAddress(InetSocketAddress address) {
        packet.setSocketAddress(((DesktopInetSocketAddress) address).getSocketAddress());
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return new DesktopInetSocketAddress((java.net.InetSocketAddress) packet.getSocketAddress());
    }

    @Override
    public void setLength(int length) {
        packet.setLength(length);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopDatagramPacket that = (DesktopDatagramPacket) object;

        if (getOffset() != that.getOffset()) return false;
        else if (getLength() != that.getLength()) return false;
        else return FastArray.equals(getData(), getOffset(), getOffset() + getLength(), that.getData(), that.getOffset(), that.getOffset() + that.getLength());
    }

    @Override
    public int hashCode() {
        return FastArray.hashCode(getData(), getOffset(), getLength());
    }

    @Override
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode()) +
                + '{' +
                "address=" + getSocketAddress() +
                '}';
    }

}
