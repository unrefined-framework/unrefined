package unrefined.util.concurrent;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

public class ConcurrentWeakHashSet<E> extends AbstractSet<E> implements Serializable {

    private static final long serialVersionUID = 8761417982496142107L;

    private static final Boolean PRESENT = true;

    private final ConcurrentWeakHashMap<E, Object> backMap;

    public ConcurrentWeakHashSet() {
        backMap = new ConcurrentWeakHashMap<>();
    }

    public ConcurrentWeakHashSet(int initialCapacity) {
        backMap = new ConcurrentWeakHashMap<>(initialCapacity);
    }

    public ConcurrentWeakHashSet(int initialCapacity, float loadFactor) {
        backMap = new ConcurrentWeakHashMap<>(initialCapacity, loadFactor);
    }

    public ConcurrentWeakHashSet(int initialCapacity, float loadFactor, int concurrencyLevel) {
        backMap = new ConcurrentWeakHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
    }

    public ConcurrentWeakHashSet(Collection<E> c) {
        backMap = new ConcurrentWeakHashMap<>(Math.max(c.size(), 12));
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
        return backMap.put(o, PRESENT) == null;
    }

    @Override
    public boolean remove(Object o) {
        return PRESENT.equals(backMap.remove(o));
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
