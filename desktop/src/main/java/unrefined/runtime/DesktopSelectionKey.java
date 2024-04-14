package unrefined.runtime;

import unrefined.desktop.NetSupport;
import unrefined.nio.channels.SelectableChannel;
import unrefined.nio.channels.SelectionKey;
import unrefined.nio.channels.Selector;

import java.util.Objects;

public class DesktopSelectionKey extends SelectionKey {

    private final java.nio.channels.SelectionKey selectionKey;
    private final SelectableChannel channel;
    private final Selector selector;

    public DesktopSelectionKey(java.nio.channels.SelectionKey selectionKey, SelectableChannel channel) {
        this.selectionKey = Objects.requireNonNull(selectionKey);
        this.channel = channel == null ? NetSupport.toUnrefinedSelectableChannel(selectionKey.channel()) : Objects.requireNonNull(channel);
        this.selector = new DesktopSelector(selectionKey.selector());
    }

    public DesktopSelectionKey(java.nio.channels.SelectionKey selectionKey) {
        this(selectionKey, null);
    }

    public java.nio.channels.SelectionKey getSelectionKey() {
        return selectionKey;
    }

    @Override
    public SelectableChannel getChannel() {
        return channel;
    }

    @Override
    public Selector getSelector() {
        return selector;
    }

    @Override
    public boolean isValid() {
        return selectionKey.isValid();
    }

    @Override
    public void cancel() {
        selectionKey.cancel();
    }

    @Override
    public int getInterestOperations() {
        return selectionKey.interestOps();
    }

    @Override
    public void setInterestOperations(int operations) {
        selectionKey.interestOps(operations);
    }

    @Override
    public int getReadyOperations() {
        return selectionKey.readyOps();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopSelectionKey that = (DesktopSelectionKey) object;

        return selectionKey.equals(that.selectionKey);
    }

    @Override
    public int hashCode() {
        return selectionKey.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "selectionKey=" + selectionKey +
                ", channel=" + channel +
                ", selector=" + selector +
                '}';
    }

}
