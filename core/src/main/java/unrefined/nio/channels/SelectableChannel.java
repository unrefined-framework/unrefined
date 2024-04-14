package unrefined.nio.channels;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.InterruptibleChannel;

public abstract class SelectableChannel implements InterruptibleChannel {

    public abstract int getValidOperations();
    public abstract boolean isRegistered();
    public abstract SelectionKey keyFor(Selector selector);
    public abstract SelectionKey register(Selector selector, int operations, Object attachment) throws ClosedChannelException;
    public abstract SelectionKey register(Selector selector, int operations) throws ClosedChannelException;
    public abstract SelectableChannel configureBlocking(boolean block) throws IOException;
    public abstract boolean isBlocking();
    public abstract Object getBlockingLock();

}
