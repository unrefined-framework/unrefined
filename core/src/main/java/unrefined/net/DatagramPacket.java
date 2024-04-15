package unrefined.net;

public abstract class DatagramPacket {

    public static DatagramPacket of(byte[] buffer, int offset, int length) {
        return Net.getInstance().createDatagramPacket(buffer, offset, length);
    }
    public static DatagramPacket of(byte[] buffer) {
        return Net.getInstance().createDatagramPacket(buffer);
    }
    public static DatagramPacket of(byte[] buffer, int offset, int length, InetSocketAddress address) {
        return Net.getInstance().createDatagramPacket(buffer, offset, length, address);
    }
    public static DatagramPacket of(byte[] buffer, InetSocketAddress address) {
        return Net.getInstance().createDatagramPacket(buffer, address);
    }

    public abstract void setInetAddress(InetAddress address);
    public abstract InetAddress getInetAddress();
    public abstract int getPort();
    public abstract byte[] getData();
    public abstract int getOffset();
    public abstract int getLength();
    public abstract void setData(byte[] buffer, int offset, int length);
    public void setData(byte[] buffer) {
        setData(buffer, 0, buffer.length);
    }
    public abstract void setPort(int port);
    public abstract void setSocketAddress(InetSocketAddress address);
    public abstract InetSocketAddress getSocketAddress();
    public abstract void setLength(int length);

}
