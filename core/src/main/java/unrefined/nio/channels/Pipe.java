package unrefined.nio.channels;

import unrefined.net.Net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.WritableByteChannel;

public abstract class Pipe {

    public static Pipe open() throws IOException {
        return Net.getInstance().openPipe();
    }

    public abstract static class SourceChannel extends SelectableChannel
            implements ReadableByteChannel, ScatteringByteChannel {
        @Override
        public final int getValidOperations() {
            return SelectionKey.Operation.READ;
        }
        public abstract FileDescriptor getFD() throws IOException;
    }

    public abstract static class SinkChannel extends SelectableChannel
            implements WritableByteChannel, GatheringByteChannel {
        @Override
        public final int getValidOperations() {
            return SelectionKey.Operation.WRITE;
        }
        public abstract FileDescriptor getFD() throws IOException;
    }

    public abstract SourceChannel source();
    public abstract SinkChannel sink();

}
