package unrefined.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.WeakHashMap;

public class WeakHashSet<E> extends AbstractSet<E> {

    private static final Object DUMMY = new Object();

    private final WeakHashMap<E, Object> backMap;

    public WeakHashSet() {
        backMap = new WeakHashMap<>();
    }

    public WeakHashSet(int initialCapacity) {
        backMap = new WeakHashMap<>(initialCapacity);
    }

    public WeakHashSet(int initialCapacity, float loadFactor) {
        backMap = new WeakHashMap<>(initialCapacity, loadFactor);
    }

    public WeakHashSet(Collection<E> c) {
        backMap = new WeakHashMap<>(Math.max(c.size(), 12));
        addAll(c);
    }

    @Override
    public int size() {
        return backMap.size();
    }

    @Override
    public boolean isEmpty() {
        return backMap.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return backMap.keySet().iterator();
    }

    @Override
    public boolean add(E o) {
        return backMap.put(o, DUMMY) == null;
    }

    @Override
    public boolean remove(Object o) {
        return backMap.remove(o) == DUMMY;
    }

    @Override
    public boolean contains(Object o) {
        return backMap.containsKey(o);
    }

    @Override
    public void clear() {
        backMap.clear();
    }

    @Override
    public Object[] toArray() {
        return backMap.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return backMap.keySet().toArray(a);
    }

}
