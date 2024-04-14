package unrefined.nio.channels;

import unrefined.net.DatagramSocket;
import unrefined.net.InetSocketAddress;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public abstract class DatagramSocketChannel extends SelectableChannel
        implements ByteChannel, ScatteringByteChannel, GatheringByteChannel, MulticastChannel {

    @Override
    public final int getValidOperations() {
        return SelectionKey.Operation.READ | SelectionKey.Operation.WRITE;
    }

    public abstract void bind(InetSocketAddress local) throws IOException;
    public abstract DatagramSocket getSocket();
    public abstract boolean isConnected();
    public abstract void connect(InetSocketAddress remote) throws IOException;
    public abstract void disconnect() throws IOException;
    public abstract InetSocketAddress getRemoteSocketAddress() throws IOException;
    public abstract InetSocketAddress receive(ByteBuffer dst) throws IOException;
    public abstract int send(ByteBuffer src, InetSocketAddress target) throws IOException;
    public abstract int read(ByteBuffer dst) throws IOException;
    public abstract long read(ByteBuffer[] dsts, int offset, int length) throws IOException;
    public abstract long read(ByteBuffer[] dsts) throws IOException;
    public abstract int write(ByteBuffer src) throws IOException;
    public abstract long write(ByteBuffer[] srcs, int offset, int length) throws IOException;
    public abstract long write(ByteBuffer[] srcs) throws IOException;
    @Override
    public abstract InetSocketAddress getLocalSocketAddress() throws IOException;

    public abstract FileDescriptor getFD() throws IOException;

}
