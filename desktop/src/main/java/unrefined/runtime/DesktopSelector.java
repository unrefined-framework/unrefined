package unrefined.runtime;

import unrefined.nio.channels.SelectionKey;
import unrefined.nio.channels.Selector;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DesktopSelector extends Selector {

    private final java.nio.channels.Selector selector;
    private final Set<SelectionKey> keys;
    private final Set<SelectionKey> selectedKeys;

    public DesktopSelector(java.nio.channels.Selector selector) {
        this.selector = selector;
        Set<SelectionKey> keys = new HashSet<>();
        for (java.nio.channels.SelectionKey key : selector.keys()) {
            keys.add(new DesktopSelectionKey(key));
        }
        this.keys = Collections.unmodifiableSet(keys);
        Set<java.nio.channels.SelectionKey> selectedKeys = selector.selectedKeys();
        this.selectedKeys = new Set<SelectionKey>() {
            @Override
            public int size() {
                return selectedKeys.size();
            }
            @Override
            public boolean isEmpty() {
                return selectedKeys.isEmpty();
            }
            @Override
            public boolean contains(Object o) {
                return selectedKeys.contains(((DesktopSelectionKey) o).getSelectionKey());
            }
            @Override
            public Iterator<SelectionKey> iterator() {
                Iterator<java.nio.channels.SelectionKey> iterator = selectedKeys.iterator();
                return new Iterator<SelectionKey>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }
                    @Override
                    public SelectionKey next() {
                        return new DesktopSelectionKey(iterator.next());
                    }
                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
            @Override
            public Object[] toArray() {
                int size = size();
                java.nio.channels.SelectionKey[] src = selectedKeys.toArray(new java.nio.channels.SelectionKey[size]);
                Object[] dst = new Object[size];
                for (int i = 0; i < size(); i ++) {
                    dst[i] = new DesktopSelectionKey(src[i]);
                }
                return dst;
            }
            @SuppressWarnings("unchecked")
            private <T> T[] prepareArray(T[] a, int size) {
                if (a.length < size) {
                    return (T[]) java.lang.reflect.Array
                            .newInstance(a.getClass().getComponentType(), size);
                }
                if (a.length > size) {
                    a[size] = null;
                }
                return a;
            }
            @SuppressWarnings("unchecked")
            @Override
            public <T> T[] toArray(T[] a) {
                int size = size();
                java.nio.channels.SelectionKey[] src = selectedKeys.toArray(new java.nio.channels.SelectionKey[size]);
                T[] dst = prepareArray(a, size);
                for (int i = 0; i < size(); i ++) {
                    dst[i] = (T) new DesktopSelectionKey(src[i]);
                }
                return dst;
            }
            @Override
            public boolean add(SelectionKey selectionKey) {
                throw new UnsupportedOperationException();
            }
            @Override
            public boolean remove(Object o) {
                return selectedKeys.remove(((DesktopSelectionKey) o).getSelectionKey());
            }
            private Set<java.nio.channels.SelectionKey> unbox(Collection<?> c) {
                Set<java.nio.channels.SelectionKey> unboxed = new HashSet<>();
                for (Object o : c) {
                    unboxed.add(((DesktopSelectionKey) o).getSelectionKey());
                }
                return unboxed;
            }
            @Override
            public boolean containsAll(Collection<?> c) {
                return selectedKeys.containsAll(unbox(c));
            }
            @Override
            public boolean addAll(Collection<? extends SelectionKey> c) {
                throw new UnsupportedOperationException();
            }
            @Override
            public boolean retainAll(Collection<?> c) {
                return selectedKeys.retainAll(unbox(c));
            }
            @Override
            public boolean removeAll(Collection<?> c) {
                return selectedKeys.removeAll(unbox(c));
            }
            @Override
            public void clear() {
                selectedKeys.clear();
            }
            @Override
            public int hashCode() {
                return selectedKeys.hashCode();
            }
        };
    }

    public java.nio.channels.Selector getSelector() {
        return selector;
    }

    @Override
    public Set<SelectionKey> keys() {
        return keys;
    }

    @Override
    public Set<SelectionKey> selectedKeys() {
        return selectedKeys;
    }

    @Override
    public int selectNow() throws IOException {
        return selector.selectNow();
    }

    @Override
    public int select(long timeout, TimeUnit timeUnit) throws IOException {
        if (timeUnit == null) timeUnit = TimeUnit.MILLISECONDS;
        return selector.select(timeUnit.toMillis(timeout));
    }

    @Override
    public int select() throws IOException {
        return selector.select();
    }

    @Override
    public Selector wakeup() {
        selector.wakeup();
        return this;
    }

    @Override
    public void close() throws IOException {
        selector.close();
    }

    @Override
    public boolean isOpen() {
        return selector.isOpen();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopSelector that = (DesktopSelector) object;

        return selector.equals(that.selector);
    }

    @Override
    public int hashCode() {
        return selector.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode()) +
                + '{' +
                "selectedKeys=" + selectedKeys +
                '}';
    }

}
