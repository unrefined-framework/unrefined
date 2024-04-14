package unrefined.net;

public abstract class DatagramPacket {

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
