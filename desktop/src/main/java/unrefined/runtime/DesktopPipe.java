package unrefined.runtime;

import unrefined.desktop.NetSupport;
import unrefined.nio.channels.Pipe;
import unrefined.nio.channels.SelectableChannel;
import unrefined.nio.channels.SelectionKey;
import unrefined.nio.channels.Selector;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Objects;

public class DesktopPipe extends Pipe {

    private final java.nio.channels.Pipe pipe;
    private final SourceChannel source;
    private final SinkChannel sink;

    public DesktopPipe(java.nio.channels.Pipe pipe) {
        this.pipe = Objects.requireNonNull(pipe);
        this.source = new SourceChannel(pipe.source());
        this.sink = new SinkChannel(pipe.sink());
    }

    public java.nio.channels.Pipe getPipe() {
        return pipe;
    }

    private static class SourceChannel extends Pipe.SourceChannel {
        private final java.nio.channels.Pipe.SourceChannel channel;
        public SourceChannel(java.nio.channels.Pipe.SourceChannel channel) {
            this.channel = Objects.requireNonNull(channel);
        }
        @Override
        public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
            return channel.read(dsts, offset, length);
        }
        @Override
        public long read(ByteBuffer[] dsts) throws IOException {
            return channel.read(dsts);
        }
        @Override
        public int read(ByteBuffer dst) throws IOException {
            return channel.read(dst);
        }
        @Override
        public boolean isRegistered() {
            return channel.isRegistered();
        }
        @Override
        public SelectionKey keyFor(Selector selector) {
            return new DesktopSelectionKey(channel.keyFor(((DesktopSelector) selector).getSelector()));
        }
        @Override
        public SelectionKey register(Selector selector, int operations, Object attachment) throws ClosedChannelException {
            return new DesktopSelectionKey(channel.register(((DesktopSelector) selector).getSelector(), operations, attachment));
        }
        @Override
        public SelectionKey register(Selector selector, int operations) throws ClosedChannelException {
            return new DesktopSelectionKey(channel.register(((DesktopSelector) selector).getSelector(), operations));
        }
        @Override
        public SelectableChannel configureBlocking(boolean block) throws IOException {
            channel.configureBlocking(block);
            return this;
        }
        @Override
        public boolean isBlocking() {
            return channel.isBlocking();
        }
        @Override
        public Object getBlockingLock() {
            return channel.blockingLock();
        }
        @Override
        public boolean isOpen() {
            return channel.isOpen();
        }
        @Override
        public void close() throws IOException {
            channel.close();
        }
        @Override
        public FileDescriptor getFD() throws IOException {
            return NetSupport.getFD(channel);
        }
    }

    private static class SinkChannel extends Pipe.SinkChannel {
        private final java.nio.channels.Pipe.SinkChannel channel;
        public SinkChannel(java.nio.channels.Pipe.SinkChannel channel) {
            this.channel = Objects.requireNonNull(channel);
        }
        @Override
        public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
            return channel.write(srcs, offset, length);
        }
        @Override
        public long write(ByteBuffer[] srcs) throws IOException {
            return channel.write(srcs);
        }
        @Override
        public int write(ByteBuffer src) throws IOException {
            return channel.write(src);
        }
        @Override
        public boolean isRegistered() {
            return channel.isRegistered();
        }
        @Override
        public SelectionKey keyFor(Selector selector) {
            return new DesktopSelectionKey(channel.keyFor(((DesktopSelector) selector).getSelector()));
        }
        @Override
        public SelectionKey register(Selector selector, int operations, Object attachment) throws ClosedChannelException {
            return new DesktopSelectionKey(channel.register(((DesktopSelector) selector).getSelector(), operations, attachment));
        }
        @Override
        public SelectionKey register(Selector selector, int operations) throws ClosedChannelException {
            return new DesktopSelectionKey(channel.register(((DesktopSelector) selector).getSelector(), operations));
        }
        @Override
        public SelectableChannel configureBlocking(boolean block) throws IOException {
            channel.configureBlocking(block);
            return this;
        }
        @Override
        public boolean isBlocking() {
            return channel.isBlocking();
        }
        @Override
        public Object getBlockingLock() {
            return channel.blockingLock();
        }
        @Override
        public boolean isOpen() {
            return channel.isOpen();
        }
        @Override
        public void close() throws IOException {
            channel.close();
        }
        @Override
        public FileDescriptor getFD() throws IOException {
            return NetSupport.getFD(channel);
        }
    }

    @Override
    public Pipe.SourceChannel source() {
        return source;
    }

    @Override
    public Pipe.SinkChannel sink() {
        return sink;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopPipe that = (DesktopPipe) object;

        return pipe.equals(that.pipe);
    }

    @Override
    public int hashCode() {
        return pipe.hashCode();
    }

}
