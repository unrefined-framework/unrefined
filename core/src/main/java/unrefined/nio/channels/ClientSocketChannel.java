package unrefined.nio.channels;

import unrefined.net.ClientSocket;
import unrefined.net.InetSocketAddress;
import unrefined.net.Net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public abstract class ClientSocketChannel extends SelectableChannel
        implements ByteChannel, ScatteringByteChannel, GatheringByteChannel, NetworkChannel {

    public static ClientSocketChannel open() throws IOException {
        return Net.getInstance().openClientSocketChannel();
    }
    public static ClientSocketChannel open(InetSocketAddress remote) throws IOException {
        return Net.getInstance().openClientSocketChannel(remote);
    }
    public static ClientSocketChannel open(InetSocketAddress remote, InetSocketAddress local) throws IOException {
        return Net.getInstance().openClientSocketChannel(remote, local);
    }

    @Override
    public final int getValidOperations() {
        return SelectionKey.Operation.READ | SelectionKey.Operation.WRITE | SelectionKey.Operation.CONNECT;
    }

    @Override
    public abstract void bind(InetSocketAddress local) throws IOException;
    public abstract void shutdownInput() throws IOException;
    public abstract void shutdownOutput() throws IOException;
    public abstract boolean isInputShutdown();
    public abstract boolean isOutputShutdown();
    public abstract ClientSocket getSocket();
    public abstract boolean isConnected();
    public abstract boolean isConnectionPending();
    public abstract boolean connect(InetSocketAddress remote) throws IOException;
    public abstract boolean finishConnect() throws IOException;
    @Override
    public abstract InetSocketAddress getLocalSocketAddress() throws IOException;
    public abstract InetSocketAddress getRemoteSocketAddress() throws IOException;
    @Override
    public abstract int read(ByteBuffer dst) throws IOException;
    @Override
    public abstract long read(ByteBuffer[] dsts, int offset, int length) throws IOException;
    public final long read(ByteBuffer[] dsts) throws IOException {
        return read(dsts, 0, dsts.length);
    }
    @Override
    public abstract int write(ByteBuffer src) throws IOException;
    @Override
    public abstract long write(ByteBuffer[] srcs, int offset, int length) throws IOException;
    @Override
    public final long write(ByteBuffer[] srcs) throws IOException {
        return write(srcs, 0, srcs.length);
    }

    public abstract FileDescriptor getFD() throws IOException;

}
