package unrefined.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public final class FastCollections {
    
    private FastCollections() {
        throw new NotInstantiableError(FastCollections.class);
    }

    static class UnmodifiableCollection<E> implements Collection<E>, Serializable {
        private static final long serialVersionUID = -2820611409762499033L;
        final Collection<? extends E> c;
        UnmodifiableCollection(Collection<? extends E> c) {
            if (c==null)
                throw new NullPointerException();
            this.c = c;
        }
        public int size()                   {return c.size();}
        public boolean isEmpty()            {return c.isEmpty();}
        public boolean contains(Object o)   {return c.contains(o);}
        public Object[] toArray()           {return c.toArray();}
        public <T> T[] toArray(T[] a)       {return c.toArray(a);}
        public String toString()            {return c.toString();}
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private final Iterator<? extends E> i = c.iterator();
                public boolean hasNext() {return i.hasNext();}
                public E next()          {return i.next();}
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }
        public boolean containsAll(Collection<?> coll) {
            return c.containsAll(coll);
        }
        public boolean addAll(Collection<? extends E> coll) {
            throw new UnsupportedOperationException();
        }
        public boolean removeAll(Collection<?> coll) {
            throw new UnsupportedOperationException();
        }
        public boolean retainAll(Collection<?> coll) {
            throw new UnsupportedOperationException();
        }
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }

    static class UnmodifiableSet<E> extends UnmodifiableCollection<E>
            implements Set<E>, Serializable {
        private static final long serialVersionUID = 54317293930279817L;
        UnmodifiableSet(Set<? extends E> s)     {super(s);}
        public boolean equals(Object o) {return o == this || c.equals(o);}
        public int hashCode()           {return c.hashCode();}
    }

    static class UnmodifiableSortedSet<E>
            extends UnmodifiableSet<E>
            implements SortedSet<E>, Serializable {
        private static final long serialVersionUID = 4604843297409628636L;
        private final SortedSet<E> ss;
        UnmodifiableSortedSet(SortedSet<E> s) {super(s); ss = s;}
        public Comparator<? super E> comparator() {return ss.comparator();}
        public SortedSet<E> subSet(E fromElement, E toElement) {
            return new UnmodifiableSortedSet<>(ss.subSet(fromElement,toElement));
        }
        public SortedSet<E> headSet(E toElement) {
            return new UnmodifiableSortedSet<>(ss.headSet(toElement));
        }
        public SortedSet<E> tailSet(E fromElement) {
            return new UnmodifiableSortedSet<>(ss.tailSet(fromElement));
        }
        public E first()                   {return ss.first();}
        public E last()                    {return ss.last();}
    }

    static class UnmodifiableNavigableSet<E>
            extends UnmodifiableSortedSet<E>
            implements NavigableSet<E>, Serializable {
        private static final long serialVersionUID = -5943116506494591760L;
        private static class EmptyNavigableSet<E> extends UnmodifiableNavigableSet<E>
                implements Serializable {
            private static final long serialVersionUID = -8042433137709000363L;

            public EmptyNavigableSet() {
                super(new TreeSet<E>());
            }
            private Object readResolve()        { return EMPTY_NAVIGABLE_SET; }
        }
        static final NavigableSet<?> EMPTY_NAVIGABLE_SET = new UnmodifiableNavigableSet.EmptyNavigableSet<>();
        private final NavigableSet<E> ns;
        UnmodifiableNavigableSet(NavigableSet<E> s)         {super(s); ns = s;}
        public E lower(E e)                             { return ns.lower(e); }
        public E floor(E e)                             { return ns.floor(e); }
        public E ceiling(E e)                         { return ns.ceiling(e); }
        public E higher(E e)                           { return ns.higher(e); }
        public E pollFirst()     { throw new UnsupportedOperationException(); }
        public E pollLast()      { throw new UnsupportedOperationException(); }
        public NavigableSet<E> descendingSet()
        { return new UnmodifiableNavigableSet<>(ns.descendingSet()); }
        public Iterator<E> descendingIterator()
        { return descendingSet().iterator(); }
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            return new UnmodifiableNavigableSet<>(
                    ns.subSet(fromElement, fromInclusive, toElement, toInclusive));
        }
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            return new UnmodifiableNavigableSet<>(
                    ns.headSet(toElement, inclusive));
        }
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            return new UnmodifiableNavigableSet<>(
                    ns.tailSet(fromElement, inclusive));
        }
    }

    static class UnmodifiableList<E> extends UnmodifiableCollection<E>
            implements List<E> {
        private static final long serialVersionUID = -2921884170611261656L;
        final List<? extends E> list;
        UnmodifiableList(List<? extends E> list) {
            super(list);
            this.list = list;
        }
        public boolean equals(Object o) {return o == this || list.equals(o);}
        public int hashCode()           {return list.hashCode();}

        public E get(int index) {return list.get(index);}
        public E set(int index, E element) {
            throw new UnsupportedOperationException();
        }
        public void add(int index, E element) {
            throw new UnsupportedOperationException();
        }
        public E remove(int index) {
            throw new UnsupportedOperationException();
        }
        public int indexOf(Object o)            {return list.indexOf(o);}
        public int lastIndexOf(Object o)        {return list.lastIndexOf(o);}
        public boolean addAll(int index, Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void sort(Comparator<? super E> c) {
            throw new UnsupportedOperationException();
        }
        public ListIterator<E> listIterator()   {return listIterator(0);}
        public ListIterator<E> listIterator(final int index) {
            return new ListIterator<E>() {
                private final ListIterator<? extends E> i
                        = list.listIterator(index);
                public boolean hasNext()     {return i.hasNext();}
                public E next()              {return i.next();}
                public boolean hasPrevious() {return i.hasPrevious();}
                public E previous()          {return i.previous();}
                public int nextIndex()       {return i.nextIndex();}
                public int previousIndex()   {return i.previousIndex();}
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                public void set(E e) {
                    throw new UnsupportedOperationException();
                }
                public void add(E e) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        public List<E> subList(int fromIndex, int toIndex) {
            return new UnmodifiableList<>(list.subList(fromIndex, toIndex));
        }
        /**
         * UnmodifiableRandomAccessList instances are serialized as
         * UnmodifiableList instances to allow them to be deserialized
         * in pre-1.4 JREs (which do not have UnmodifiableRandomAccessList).
         * This method inverts the transformation.  As a beneficial
         * side-effect, it also grafts the RandomAccess marker onto
         * UnmodifiableList instances that were serialized in pre-1.4 JREs.
         *
         * Note: Unfortunately, UnmodifiableRandomAccessList instances
         * serialized in 1.4.1 and deserialized in 1.4 will become
         * UnmodifiableList instances, as this method was missing in 1.4.
         */
        private Object readResolve() {
            return (list instanceof RandomAccess
                    ? new UnmodifiableRandomAccessList<>(list)
                    : this);
        }
    }

    /**
     * @serial include
     */
    static class UnmodifiableRandomAccessList<E> extends UnmodifiableList<E>
            implements RandomAccess
    {
        private static final long serialVersionUID = -2542308836966382001L;
        UnmodifiableRandomAccessList(List<? extends E> list) {
            super(list);
        }
        public List<E> subList(int fromIndex, int toIndex) {
            return new UnmodifiableRandomAccessList<>(
                    list.subList(fromIndex, toIndex));
        }
        /**
         * Allows instances to be deserialized in pre-1.4 JREs (which do
         * not have UnmodifiableRandomAccessList).  UnmodifiableList has
         * a readResolve method that inverts this transformation upon
         * deserialization.
         */
        private Object writeReplace() {
            return new UnmodifiableList<>(list);
        }
    }

    private static class UnmodifiableMap<K,V> implements Map<K,V>, Serializable {
        private static final long serialVersionUID = -6708287212828314978L;
        private final Map<? extends K, ? extends V> m;
        UnmodifiableMap(Map<? extends K, ? extends V> m) {
            if (m==null)
                throw new NullPointerException();
            this.m = m;
        }
        public int size()                        {return m.size();}
        public boolean isEmpty()                 {return m.isEmpty();}
        public boolean containsKey(Object key)   {return m.containsKey(key);}
        public boolean containsValue(Object val) {return m.containsValue(val);}
        public V get(Object key)                 {return m.get(key);}
        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }
        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }
        public void putAll(Map<? extends K, ? extends V> m) {
            throw new UnsupportedOperationException();
        }
        public void clear() {
            throw new UnsupportedOperationException();
        }
        private transient Set<K> keySet;
        private transient Set<Map.Entry<K,V>> entrySet;
        private transient Collection<V> values;
        public Set<K> keySet() {
            if (keySet==null)
                keySet = Collections.unmodifiableSet(m.keySet());
            return keySet;
        }
        public Set<Map.Entry<K,V>> entrySet() {
            if (entrySet==null)
                entrySet = new UnmodifiableMap.UnmodifiableEntrySet<>(m.entrySet());
            return entrySet;
        }
        public Collection<V> values() {
            if (values==null)
                values = Collections.unmodifiableCollection(m.values());
            return values;
        }
        public boolean equals(Object o) {return o == this || m.equals(o);}
        public int hashCode()           {return m.hashCode();}
        public String toString()        {return m.toString();}
        // Override default methods in Map
        @Override
        public V putIfAbsent(K key, V value) {
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }
        @Override
        public V replace(K key, V value) {
            throw new UnsupportedOperationException();
        }
        /**
         * We need this class in addition to UnmodifiableSet as
         * Map.Entries themselves permit modification of the backing Map
         * via their setValue operation.  This class is subtle: there are
         * many possible attacks that must be thwarted.
         *
         * @serial include
         */
        static class UnmodifiableEntrySet<K,V>
                extends UnmodifiableSet<Entry<K,V>> {
            private static final long serialVersionUID = 7854390611657943733L;
            @SuppressWarnings({"unchecked", "rawtypes"})
            UnmodifiableEntrySet(Set<? extends Map.Entry<? extends K, ? extends V>> s) {
                // Need to cast to raw in order to work around a limitation in the type system
                super((Set)s);
            }
            public Iterator<Map.Entry<K,V>> iterator() {
                return new Iterator<Map.Entry<K,V>>() {
                    private final Iterator<? extends Map.Entry<? extends K, ? extends V>> i = c.iterator();
                    public boolean hasNext() {
                        return i.hasNext();
                    }
                    public Map.Entry<K,V> next() {
                        return new UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry<>(i.next());
                    }
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    // Android-note: Oversight of Iterator.forEachRemaining().
                    // This seems pretty inconsistent. Unlike other subclasses,
                    // we aren't delegating to the subclass iterator here.
                    // Seems like an oversight. http://b/110351017
                };
            }
            @SuppressWarnings("unchecked")
            public Object[] toArray() {
                Object[] a = c.toArray();
                for (int i=0; i<a.length; i++)
                    a[i] = new UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry<>((Map.Entry<? extends K, ? extends V>)a[i]);
                return a;
            }
            @SuppressWarnings("unchecked")
            public <T> T[] toArray(T[] a) {
                // We don't pass a to c.toArray, to avoid window of
                // vulnerability wherein an unscrupulous multithreaded client
                // could get his hands on raw (unwrapped) Entries from c.
                Object[] arr = c.toArray(a.length==0 ? a : Arrays.copyOf(a, 0));
                for (int i=0; i<arr.length; i++)
                    arr[i] = new UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry<>((Map.Entry<? extends K, ? extends V>)arr[i]);
                if (arr.length > a.length)
                    return (T[])arr;
                System.arraycopy(arr, 0, a, 0, arr.length);
                if (a.length > arr.length)
                    a[arr.length] = null;
                return a;
            }
            /**
             * This method is overridden to protect the backing set against
             * an object with a nefarious equals function that senses
             * that the equality-candidate is Map.Entry and calls its
             * setValue method.
             */
            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                return c.contains(
                        new UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry<>((Map.Entry<?,?>) o));
            }
            /**
             * The next two methods are overridden to protect against
             * an unscrupulous List whose contains(Object o) method senses
             * when o is a Map.Entry, and calls o.setValue.
             */
            public boolean containsAll(Collection<?> coll) {
                for (Object e : coll) {
                    if (!contains(e)) // Invokes safe contains() above
                        return false;
                }
                return true;
            }
            public boolean equals(Object o) {
                if (o == this)
                    return true;

                if (!(o instanceof Set))
                    return false;
                Set<?> s = (Set<?>) o;
                if (s.size() != c.size())
                    return false;
                return containsAll(s); // Invokes safe containsAll() above
            }
            /**
             * This "wrapper class" serves two purposes: it prevents
             * the client from modifying the backing Map, by short-circuiting
             * the setValue method, and it protects the backing Map against
             * an ill-behaved Map.Entry that attempts to modify another
             * Map Entry when asked to perform an equality check.
             */
            private static class UnmodifiableEntry<K,V> implements Map.Entry<K,V> {
                private Map.Entry<? extends K, ? extends V> e;
                UnmodifiableEntry(Map.Entry<? extends K, ? extends V> e)
                {this.e = Objects.requireNonNull(e);}
                public K getKey()        {return e.getKey();}
                public V getValue()      {return e.getValue();}
                public V setValue(V value) {
                    throw new UnsupportedOperationException();
                }
                public int hashCode()    {return e.hashCode();}
                public boolean equals(Object o) {
                    if (this == o)
                        return true;
                    if (!(o instanceof Map.Entry))
                        return false;
                    Map.Entry<?,?> t = (Map.Entry<?,?>)o;
                    return Objects.equals(e.getKey(),   t.getKey()) &&
                            Objects.equals(e.getValue(), t.getValue());
                }
                public String toString() {return e.toString();}
            }
        }
    }
    /**
     * @serial include
     */
    static class UnmodifiableSortedMap<K,V>
            extends UnmodifiableMap<K,V>
            implements SortedMap<K,V>, Serializable {
        private static final long serialVersionUID = -8806743815996713206L;
        private final SortedMap<K, ? extends V> sm;
        UnmodifiableSortedMap(SortedMap<K, ? extends V> m) {super(m); sm = m; }
        public Comparator<? super K> comparator()   { return sm.comparator(); }
        public SortedMap<K,V> subMap(K fromKey, K toKey)
        { return new UnmodifiableSortedMap<>(sm.subMap(fromKey, toKey)); }
        public SortedMap<K,V> headMap(K toKey)
        { return new UnmodifiableSortedMap<>(sm.headMap(toKey)); }
        public SortedMap<K,V> tailMap(K fromKey)
        { return new UnmodifiableSortedMap<>(sm.tailMap(fromKey)); }
        public K firstKey()                           { return sm.firstKey(); }
        public K lastKey()                             { return sm.lastKey(); }
    }

    /**
     * @serial include
     */
    static class UnmodifiableNavigableMap<K,V>
            extends UnmodifiableSortedMap<K,V>
            implements NavigableMap<K,V>, Serializable {
        private static final long serialVersionUID = -4858195264774772197L;
        private static class EmptyNavigableMap<K,V> extends UnmodifiableNavigableMap<K,V>
                implements Serializable {
            private static final long serialVersionUID = -2239321462712562324L;
            EmptyNavigableMap()                       { super(new TreeMap<K,V>()); }
            @Override
            public NavigableSet<K> navigableKeySet()
            { return (NavigableSet<K>) UnmodifiableNavigableSet.EmptyNavigableSet.EMPTY_NAVIGABLE_SET; }
            private Object readResolve()        { return EMPTY_NAVIGABLE_MAP; }
        }
        static final Map<?,?> EMPTY_NAVIGABLE_MAP = new UnmodifiableNavigableMap.EmptyNavigableMap<>();
        /**
         * The instance we wrap and protect.
         */
        private final NavigableMap<K, ? extends V> nm;
        UnmodifiableNavigableMap(NavigableMap<K, ? extends V> m)
        {super(m); nm = m;}
        public K lowerKey(K key)                   { return nm.lowerKey(key); }
        public K floorKey(K key)                   { return nm.floorKey(key); }
        public K ceilingKey(K key)               { return nm.ceilingKey(key); }
        public K higherKey(K key)                 { return nm.higherKey(key); }
        @SuppressWarnings("unchecked")
        public Entry<K, V> lowerEntry(K key) {
            Entry<K,V> lower = (Entry<K, V>) nm.lowerEntry(key);
            return (null != lower)
                    ? new UnmodifiableEntrySet.UnmodifiableEntry<>(lower)
                    : null;
        }
        @SuppressWarnings("unchecked")
        public Entry<K, V> floorEntry(K key) {
            Entry<K,V> floor = (Entry<K, V>) nm.floorEntry(key);
            return (null != floor)
                    ? new UnmodifiableEntrySet.UnmodifiableEntry<>(floor)
                    : null;
        }
        @SuppressWarnings("unchecked")
        public Entry<K, V> ceilingEntry(K key) {
            Entry<K,V> ceiling = (Entry<K, V>) nm.ceilingEntry(key);
            return (null != ceiling)
                    ? new UnmodifiableEntrySet.UnmodifiableEntry<>(ceiling)
                    : null;
        }
        @SuppressWarnings("unchecked")
        public Entry<K, V> higherEntry(K key) {
            Entry<K,V> higher = (Entry<K, V>) nm.higherEntry(key);
            return (null != higher)
                    ? new UnmodifiableEntrySet.UnmodifiableEntry<>(higher)
                    : null;
        }
        @SuppressWarnings("unchecked")
        public Entry<K, V> firstEntry() {
            Entry<K,V> first = (Entry<K, V>) nm.firstEntry();
            return (null != first)
                    ? new UnmodifiableEntrySet.UnmodifiableEntry<>(first)
                    : null;
        }
        @SuppressWarnings("unchecked")
        public Entry<K, V> lastEntry() {
            Entry<K,V> last = (Entry<K, V>) nm.lastEntry();
            return (null != last)
                    ? new UnmodifiableEntrySet.UnmodifiableEntry<>(last)
                    : null;
        }
        public Entry<K, V> pollFirstEntry()
        { throw new UnsupportedOperationException(); }
        public Entry<K, V> pollLastEntry()
        { throw new UnsupportedOperationException(); }
        public NavigableMap<K, V> descendingMap()
        { return unmodifiableNavigableMap(nm.descendingMap()); }
        public NavigableSet<K> navigableKeySet()
        { return unmodifiableNavigableSet(nm.navigableKeySet()); }
        public NavigableSet<K> descendingKeySet()
        { return unmodifiableNavigableSet(nm.descendingKeySet()); }
        public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            return unmodifiableNavigableMap(
                    nm.subMap(fromKey, fromInclusive, toKey, toInclusive));
        }
        public NavigableMap<K, V> headMap(K toKey, boolean inclusive)
        { return unmodifiableNavigableMap(nm.headMap(toKey, inclusive)); }
        public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive)
        { return unmodifiableNavigableMap(nm.tailMap(fromKey, inclusive)); }
    }

    static class SynchronizedCollection<E> implements Collection<E>, Serializable {

        private static final long serialVersionUID = 2342945022991684597L;

        final Collection<E> c;  // Backing Collection
        final Object mutex;     // Object on which to synchronize

        SynchronizedCollection(Collection<E> c) {
            this.c = Objects.requireNonNull(c);
            mutex = this;
        }

        SynchronizedCollection(Collection<E> c, Object mutex) {
            this.c = Objects.requireNonNull(c);
            this.mutex = Objects.requireNonNull(mutex);
        }

        public int size() {
            synchronized (mutex) {return c.size();}
        }
        public boolean isEmpty() {
            synchronized (mutex) {return c.isEmpty();}
        }
        public boolean contains(Object o) {
            synchronized (mutex) {return c.contains(o);}
        }
        public Object[] toArray() {
            synchronized (mutex) {return c.toArray();}
        }
        public <T> T[] toArray(T[] a) {
            synchronized (mutex) {return c.toArray(a);}
        }

        public Iterator<E> iterator() {
            return c.iterator(); // Must be manually synched by user!
        }

        public boolean add(E e) {
            synchronized (mutex) {return c.add(e);}
        }
        public boolean remove(Object o) {
            synchronized (mutex) {return c.remove(o);}
        }

        public boolean containsAll(Collection<?> coll) {
            synchronized (mutex) {return c.containsAll(coll);}
        }
        public boolean addAll(Collection<? extends E> coll) {
            synchronized (mutex) {return c.addAll(coll);}
        }
        public boolean removeAll(Collection<?> coll) {
            synchronized (mutex) {return c.removeAll(coll);}
        }
        public boolean retainAll(Collection<?> coll) {
            synchronized (mutex) {return c.retainAll(coll);}
        }
        public void clear() {
            synchronized (mutex) {c.clear();}
        }
        public String toString() {
            synchronized (mutex) {return c.toString();}
        }
        private void writeObject(ObjectOutputStream s) throws IOException {
            synchronized (mutex) {s.defaultWriteObject();}
        }
    }

    static class SynchronizedSet<E>
            extends SynchronizedCollection<E>
            implements Set<E> {

        private static final long serialVersionUID = -3308597891521937196L;

        SynchronizedSet(Set<E> s) {
            super(s);
        }
        SynchronizedSet(Set<E> s, Object mutex) {
            super(s, mutex);
        }

        public boolean equals(Object o) {
            if (this == o)
                return true;
            synchronized (mutex) {return c.equals(o);}
        }
        public int hashCode() {
            synchronized (mutex) {return c.hashCode();}
        }
    }

    static class SynchronizedSortedSet<E>
            extends SynchronizedSet<E>
            implements SortedSet<E>
    {

        private static final long serialVersionUID = 4597910752392747900L;
        private final SortedSet<E> ss;

        SynchronizedSortedSet(SortedSet<E> s) {
            super(s);
            ss = s;
        }
        SynchronizedSortedSet(SortedSet<E> s, Object mutex) {
            super(s, mutex);
            ss = s;
        }

        public Comparator<? super E> comparator() {
            synchronized (mutex) {return ss.comparator();}
        }

        public SortedSet<E> subSet(E fromElement, E toElement) {
            synchronized (mutex) {
                return new SynchronizedSortedSet<>(
                        ss.subSet(fromElement, toElement), mutex);
            }
        }
        public SortedSet<E> headSet(E toElement) {
            synchronized (mutex) {
                return new SynchronizedSortedSet<>(ss.headSet(toElement), mutex);
            }
        }
        public SortedSet<E> tailSet(E fromElement) {
            synchronized (mutex) {
                return new SynchronizedSortedSet<>(ss.tailSet(fromElement),mutex);
            }
        }

        public E first() {
            synchronized (mutex) {return ss.first();}
        }
        public E last() {
            synchronized (mutex) {return ss.last();}
        }
    }

    static class SynchronizedNavigableSet<E>
        extends SynchronizedSortedSet<E>
        implements NavigableSet<E>
    {

        private static final long serialVersionUID = -1859065113094364030L;

        private final NavigableSet<E> ns;

        SynchronizedNavigableSet(NavigableSet<E> s) {
            super(s);
            ns = s;
        }

        SynchronizedNavigableSet(NavigableSet<E> s, Object mutex) {
            super(s, mutex);
            ns = s;
        }
        public E lower(E e)      { synchronized (mutex) {return ns.lower(e);} }
        public E floor(E e)      { synchronized (mutex) {return ns.floor(e);} }
        public E ceiling(E e)  { synchronized (mutex) {return ns.ceiling(e);} }
        public E higher(E e)    { synchronized (mutex) {return ns.higher(e);} }
        public E pollFirst()  { synchronized (mutex) {return ns.pollFirst();} }
        public E pollLast()    { synchronized (mutex) {return ns.pollLast();} }

        public NavigableSet<E> descendingSet() {
            synchronized (mutex) {
                return new SynchronizedNavigableSet<>(ns.descendingSet(), mutex);
            }
        }

        public Iterator<E> descendingIterator()
                 { synchronized (mutex) { return descendingSet().iterator(); } }

        public NavigableSet<E> subSet(E fromElement, E toElement) {
            synchronized (mutex) {
                return new SynchronizedNavigableSet<>(ns.subSet(fromElement, true, toElement, false), mutex);
            }
        }
        public NavigableSet<E> headSet(E toElement) {
            synchronized (mutex) {
                return new SynchronizedNavigableSet<>(ns.headSet(toElement, false), mutex);
            }
        }
        public NavigableSet<E> tailSet(E fromElement) {
            synchronized (mutex) {
                return new SynchronizedNavigableSet<>(ns.tailSet(fromElement, true), mutex);
            }
        }

        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            synchronized (mutex) {
                return new SynchronizedNavigableSet<>(ns.subSet(fromElement, fromInclusive, toElement, toInclusive), mutex);
            }
        }

        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            synchronized (mutex) {
                return new SynchronizedNavigableSet<>(ns.headSet(toElement, inclusive), mutex);
            }
        }

        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            synchronized (mutex) {
                return new SynchronizedNavigableSet<>(ns.tailSet(fromElement, inclusive), mutex);
            }
        }
    }

    static class SynchronizedList<E>
        extends SynchronizedCollection<E>
        implements List<E> {

        private static final long serialVersionUID = -3650654074474565685L;
        final List<E> list;

        SynchronizedList(List<E> list) {
            super(list);
            this.list = list;
        }
        SynchronizedList(List<E> list, Object mutex) {
            super(list, mutex);
            this.list = list;
        }

        public boolean equals(Object o) {
            if (this == o)
                return true;
            synchronized (mutex) {return list.equals(o);}
        }
        public int hashCode() {
            synchronized (mutex) {return list.hashCode();}
        }

        public E get(int index) {
            synchronized (mutex) {return list.get(index);}
        }
        public E set(int index, E element) {
            synchronized (mutex) {return list.set(index, element);}
        }
        public void add(int index, E element) {
            synchronized (mutex) {list.add(index, element);}
        }
        public E remove(int index) {
            synchronized (mutex) {return list.remove(index);}
        }

        public int indexOf(Object o) {
            synchronized (mutex) {return list.indexOf(o);}
        }
        public int lastIndexOf(Object o) {
            synchronized (mutex) {return list.lastIndexOf(o);}
        }

        public boolean addAll(int index, Collection<? extends E> c) {
            synchronized (mutex) {return list.addAll(index, c);}
        }

        public ListIterator<E> listIterator() {
            return list.listIterator(); // Must be manually synched by user
        }

        public ListIterator<E> listIterator(int index) {
            return list.listIterator(index); // Must be manually synched by user
        }

        public List<E> subList(int fromIndex, int toIndex) {
            synchronized (mutex) {
                return new SynchronizedList<>(list.subList(fromIndex, toIndex),
                                            mutex);
            }
        }

        /**
         * SynchronizedRandomAccessList instances are serialized as
         * SynchronizedList instances to allow them to be deserialized
         * in pre-1.4 JREs (which do not have SynchronizedRandomAccessList).
         * This method inverts the transformation.  As a beneficial
         * side-effect, it also grafts the RandomAccess marker onto
         * SynchronizedList instances that were serialized in pre-1.4 JREs.
         *
         * Note: Unfortunately, SynchronizedRandomAccessList instances
         * serialized in 1.4.1 and deserialized in 1.4 will become
         * SynchronizedList instances, as this method was missing in 1.4.
         */
        private Object readResolve() {
            return (list instanceof RandomAccess
                    ? new SynchronizedRandomAccessList<>(list)
                    : this);
        }
    }

    static class SynchronizedRandomAccessList<E>
        extends SynchronizedList<E>
        implements RandomAccess {

        SynchronizedRandomAccessList(List<E> list) {
            super(list);
        }

        SynchronizedRandomAccessList(List<E> list, Object mutex) {
            super(list, mutex);
        }

        public List<E> subList(int fromIndex, int toIndex) {
            synchronized (mutex) {
                return new SynchronizedRandomAccessList<>(
                    list.subList(fromIndex, toIndex), mutex);
            }
        }

        private static final long serialVersionUID = 1530674583602358482L;

        /**
         * Allows instances to be deserialized in pre-1.4 JREs (which do
         * not have SynchronizedRandomAccessList).  SynchronizedList has
         * a readResolve method that inverts this transformation upon
         * deserialization.
         */
        private Object writeReplace() {
            return new SynchronizedList<>(list);
        }
    }

    private static class SynchronizedMap<K,V>
        implements Map<K,V>, Serializable {

        private static final long serialVersionUID = 4883431028901719881L;
        private final Map<K,V> m;     // Backing Map
        final Object      mutex;        // Object on which to synchronize

        SynchronizedMap(Map<K,V> m) {
            this.m = Objects.requireNonNull(m);
            mutex = this;
        }

        SynchronizedMap(Map<K,V> m, Object mutex) {
            this.m = m;
            this.mutex = mutex;
        }

        public int size() {
            synchronized (mutex) {return m.size();}
        }
        public boolean isEmpty() {
            synchronized (mutex) {return m.isEmpty();}
        }
        public boolean containsKey(Object key) {
            synchronized (mutex) {return m.containsKey(key);}
        }
        public boolean containsValue(Object value) {
            synchronized (mutex) {return m.containsValue(value);}
        }
        public V get(Object key) {
            synchronized (mutex) {return m.get(key);}
        }

        public V put(K key, V value) {
            synchronized (mutex) {return m.put(key, value);}
        }
        public V remove(Object key) {
            synchronized (mutex) {return m.remove(key);}
        }
        public void putAll(Map<? extends K, ? extends V> map) {
            synchronized (mutex) {m.putAll(map);}
        }
        public void clear() {
            synchronized (mutex) {m.clear();}
        }

        private transient Set<K> keySet;
        private transient Set<Map.Entry<K,V>> entrySet;
        private transient Collection<V> values;

        public Set<K> keySet() {
            synchronized (mutex) {
                if (keySet==null)
                    keySet = new SynchronizedSet<>(m.keySet(), mutex);
                return keySet;
            }
        }

        public Set<Map.Entry<K,V>> entrySet() {
            synchronized (mutex) {
                if (entrySet==null)
                    entrySet = new SynchronizedSet<>(m.entrySet(), mutex);
                return entrySet;
            }
        }

        public Collection<V> values() {
            synchronized (mutex) {
                if (values==null)
                    values = new SynchronizedCollection<>(m.values(), mutex);
                return values;
            }
        }

        public boolean equals(Object o) {
            if (this == o)
                return true;
            synchronized (mutex) {return m.equals(o);}
        }
        public int hashCode() {
            synchronized (mutex) {return m.hashCode();}
        }
        public String toString() {
            synchronized (mutex) {return m.toString();}
        }
        private void writeObject(ObjectOutputStream s) throws IOException {
            synchronized (mutex) {s.defaultWriteObject();}
        }
    }

    static class SynchronizedSortedMap<K,V>
        extends SynchronizedMap<K,V>
        implements SortedMap<K,V>
    {

        private static final long serialVersionUID = -8883687955475681753L;
        private final SortedMap<K,V> sm;

        SynchronizedSortedMap(SortedMap<K,V> m) {
            super(m);
            sm = m;
        }
        SynchronizedSortedMap(SortedMap<K,V> m, Object mutex) {
            super(m, mutex);
            sm = m;
        }

        public Comparator<? super K> comparator() {
            synchronized (mutex) {return sm.comparator();}
        }

        public SortedMap<K,V> subMap(K fromKey, K toKey) {
            synchronized (mutex) {
                return new SynchronizedSortedMap<>(
                    sm.subMap(fromKey, toKey), mutex);
            }
        }
        public SortedMap<K,V> headMap(K toKey) {
            synchronized (mutex) {
                return new SynchronizedSortedMap<>(sm.headMap(toKey), mutex);
            }
        }
        public SortedMap<K,V> tailMap(K fromKey) {
            synchronized (mutex) {
               return new SynchronizedSortedMap<>(sm.tailMap(fromKey),mutex);
            }
        }

        public K firstKey() {
            synchronized (mutex) {return sm.firstKey();}
        }
        public K lastKey() {
            synchronized (mutex) {return sm.lastKey();}
        }
    }

    static class SynchronizedNavigableMap<K,V>
        extends SynchronizedSortedMap<K,V>
        implements NavigableMap<K,V>
    {

        private static final long serialVersionUID = 6197242966078130854L;
        private final NavigableMap<K,V> nm;

        SynchronizedNavigableMap(NavigableMap<K,V> m) {
            super(m);
            nm = m;
        }
        SynchronizedNavigableMap(NavigableMap<K,V> m, Object mutex) {
            super(m, mutex);
            nm = m;
        }

        public Entry<K, V> lowerEntry(K key)
                        { synchronized (mutex) { return nm.lowerEntry(key); } }
        public K lowerKey(K key)
                          { synchronized (mutex) { return nm.lowerKey(key); } }
        public Entry<K, V> floorEntry(K key)
                        { synchronized (mutex) { return nm.floorEntry(key); } }
        public K floorKey(K key)
                          { synchronized (mutex) { return nm.floorKey(key); } }
        public Entry<K, V> ceilingEntry(K key)
                      { synchronized (mutex) { return nm.ceilingEntry(key); } }
        public K ceilingKey(K key)
                        { synchronized (mutex) { return nm.ceilingKey(key); } }
        public Entry<K, V> higherEntry(K key)
                       { synchronized (mutex) { return nm.higherEntry(key); } }
        public K higherKey(K key)
                         { synchronized (mutex) { return nm.higherKey(key); } }
        public Entry<K, V> firstEntry()
                           { synchronized (mutex) { return nm.firstEntry(); } }
        public Entry<K, V> lastEntry()
                            { synchronized (mutex) { return nm.lastEntry(); } }
        public Entry<K, V> pollFirstEntry()
                       { synchronized (mutex) { return nm.pollFirstEntry(); } }
        public Entry<K, V> pollLastEntry()
                        { synchronized (mutex) { return nm.pollLastEntry(); } }

        public NavigableMap<K, V> descendingMap() {
            synchronized (mutex) {
                return
                    new SynchronizedNavigableMap<>(nm.descendingMap(), mutex);
            }
        }

        public NavigableSet<K> keySet() {
            return navigableKeySet();
        }

        public NavigableSet<K> navigableKeySet() {
            synchronized (mutex) {
                return new SynchronizedNavigableSet<>(nm.navigableKeySet(), mutex);
            }
        }

        public NavigableSet<K> descendingKeySet() {
            synchronized (mutex) {
                return new SynchronizedNavigableSet<>(nm.descendingKeySet(), mutex);
            }
        }


        public SortedMap<K,V> subMap(K fromKey, K toKey) {
            synchronized (mutex) {
                return new SynchronizedNavigableMap<>(
                    nm.subMap(fromKey, true, toKey, false), mutex);
            }
        }
        public SortedMap<K,V> headMap(K toKey) {
            synchronized (mutex) {
                return new SynchronizedNavigableMap<>(nm.headMap(toKey, false), mutex);
            }
        }
        public SortedMap<K,V> tailMap(K fromKey) {
            synchronized (mutex) {
        return new SynchronizedNavigableMap<>(nm.tailMap(fromKey, true),mutex);
            }
        }

        public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            synchronized (mutex) {
                return new SynchronizedNavigableMap<>(
                    nm.subMap(fromKey, fromInclusive, toKey, toInclusive), mutex);
            }
        }

        public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
            synchronized (mutex) {
                return new SynchronizedNavigableMap<>(
                        nm.headMap(toKey, inclusive), mutex);
            }
        }

        public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
            synchronized (mutex) {
                return new SynchronizedNavigableMap<>(
                    nm.tailMap(fromKey, inclusive), mutex);
            }
        }
    }

    static class CheckedCollection<E> implements Collection<E>, Serializable {
        private static final long serialVersionUID = 1578914078182001775L;

        final Collection<E> c;
        final Class<E> type;

        @SuppressWarnings("unchecked")
        E typeCheck(Object o) {
            if (o != null && !type.isInstance(o))
                throw new ClassCastException(badElementMsg(o));
            return (E) o;
        }

        private String badElementMsg(Object o) {
            return "Attempt to insert " + o.getClass() +
                " element into collection with element type " + type;
        }

        CheckedCollection(Collection<E> c, Class<E> type) {
            this.c = Objects.requireNonNull(c, "c");
            this.type = Objects.requireNonNull(type, "type");
        }

        public int size()                 { return c.size(); }
        public boolean isEmpty()          { return c.isEmpty(); }
        public boolean contains(Object o) { return c.contains(o); }
        public Object[] toArray()         { return c.toArray(); }
        public <T> T[] toArray(T[] a)     { return c.toArray(a); }
        public String toString()          { return c.toString(); }
        public boolean remove(Object o)   { return c.remove(o); }
        public void clear()               {        c.clear(); }

        public boolean containsAll(Collection<?> coll) {
            return c.containsAll(coll);
        }
        public boolean removeAll(Collection<?> coll) {
            return c.removeAll(coll);
        }
        public boolean retainAll(Collection<?> coll) {
            return c.retainAll(coll);
        }

        public Iterator<E> iterator() {
            // JDK-6363904 - unwrapped iterator could be typecast to
            // ListIterator with unsafe set()
            final Iterator<E> it = c.iterator();
            return new Iterator<E>() {
                public boolean hasNext() { return it.hasNext(); }
                public E next()          { return it.next(); }
                public void remove()     {        it.remove(); }};
            // Android-note: Oversight of Iterator.forEachRemaining().
            // http://b/110351017
        }

        public boolean add(E e)          { return c.add(typeCheck(e)); }

        @SuppressWarnings("unchecked")
        static <T> T[] zeroLengthArray(Class<T> type) {
            return (T[]) Array.newInstance(type, 0);
        }

        private E[] zeroLengthElementArray; // Lazily initialized

        private E[] zeroLengthElementArray() {
            return zeroLengthElementArray != null ? zeroLengthElementArray :
                (zeroLengthElementArray = zeroLengthArray(type));
        }

        @SuppressWarnings("unchecked")
        Collection<E> checkedCopyOf(Collection<? extends E> coll) {
            Object[] a;
            try {
                E[] z = zeroLengthElementArray();
                a = coll.toArray(z);
                // Defend against coll violating the toArray contract
                if (a.getClass() != z.getClass())
                    a = Arrays.copyOf(a, a.length, z.getClass());
            } catch (ArrayStoreException ignore) {
                // To get better and consistent diagnostics,
                // we call typeCheck explicitly on each element.
                // We call clone() to defend against coll retaining a
                // reference to the returned array and storing a bad
                // element into it after it has been type checked.
                a = coll.toArray().clone();
                for (Object o : a)
                    typeCheck(o);
            }
            // A slight abuse of the type system, but safe here.
            return (Collection<E>) Arrays.asList(a);
        }

        public boolean addAll(Collection<? extends E> coll) {
            // Doing things this way insulates us from concurrent changes
            // in the contents of coll and provides all-or-nothing
            // semantics (which we wouldn't get if we type-checked each
            // element as we added it)
            return c.addAll(checkedCopyOf(coll));
        }
    }

    static class CheckedQueue<E>
        extends CheckedCollection<E>
        implements Queue<E>, Serializable
    {
        private static final long serialVersionUID = 1433151992604707767L;
        final Queue<E> queue;

        CheckedQueue(Queue<E> queue, Class<E> elementType) {
            super(queue, elementType);
            this.queue = queue;
        }

        public E element()              {return queue.element();}
        public boolean equals(Object o) {return o == this || c.equals(o);}
        public int hashCode()           {return c.hashCode();}
        public E peek()                 {return queue.peek();}
        public E poll()                 {return queue.poll();}
        public E remove()               {return queue.remove();}
        public boolean offer(E e)       {return queue.offer(typeCheck(e));}
    }

    static class CheckedSet<E> extends CheckedCollection<E>
                                 implements Set<E>, Serializable
    {
        private static final long serialVersionUID = 4694047833775013803L;

        CheckedSet(Set<E> s, Class<E> elementType) { super(s, elementType); }

        public boolean equals(Object o) { return o == this || c.equals(o); }
        public int hashCode()           { return c.hashCode(); }
    }

    static class CheckedSortedSet<E> extends CheckedSet<E>
        implements SortedSet<E>, Serializable
    {

        private static final long serialVersionUID = -1037193489704631435L;
        private final SortedSet<E> ss;

        CheckedSortedSet(SortedSet<E> s, Class<E> type) {
            super(s, type);
            ss = s;
        }

        public Comparator<? super E> comparator() { return ss.comparator(); }
        public E first()                   { return ss.first(); }
        public E last()                    { return ss.last(); }

        public SortedSet<E> subSet(E fromElement, E toElement) {
            return checkedSortedSet(ss.subSet(fromElement,toElement), type);
        }
        public SortedSet<E> headSet(E toElement) {
            return checkedSortedSet(ss.headSet(toElement), type);
        }
        public SortedSet<E> tailSet(E fromElement) {
            return checkedSortedSet(ss.tailSet(fromElement), type);
        }
    }

    static class CheckedNavigableSet<E> extends CheckedSortedSet<E>
        implements NavigableSet<E>, Serializable
    {

        private static final long serialVersionUID = -1921979680076071352L;
        private final NavigableSet<E> ns;

        CheckedNavigableSet(NavigableSet<E> s, Class<E> type) {
            super(s, type);
            ns = s;
        }

        public E lower(E e)                             { return ns.lower(e); }
        public E floor(E e)                             { return ns.floor(e); }
        public E ceiling(E e)                         { return ns.ceiling(e); }
        public E higher(E e)                           { return ns.higher(e); }
        public E pollFirst()                         { return ns.pollFirst(); }
        public E pollLast()                            {return ns.pollLast(); }
        public NavigableSet<E> descendingSet()
                      { return checkedNavigableSet(ns.descendingSet(), type); }
        public Iterator<E> descendingIterator()
            {return checkedNavigableSet(ns.descendingSet(), type).iterator(); }

        public NavigableSet<E> subSet(E fromElement, E toElement) {
            return checkedNavigableSet(ns.subSet(fromElement, true, toElement, false), type);
        }
        public NavigableSet<E> headSet(E toElement) {
            return checkedNavigableSet(ns.headSet(toElement, false), type);
        }
        public NavigableSet<E> tailSet(E fromElement) {
            return checkedNavigableSet(ns.tailSet(fromElement, true), type);
        }

        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            return checkedNavigableSet(ns.subSet(fromElement, fromInclusive, toElement, toInclusive), type);
        }

        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            return checkedNavigableSet(ns.headSet(toElement, inclusive), type);
        }

        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            return checkedNavigableSet(ns.tailSet(fromElement, inclusive), type);
        }
    }

    private static class CheckedMap<K,V>
        implements Map<K,V>, Serializable
    {

        private static final long serialVersionUID = -3086846190258820373L;

        private final Map<K, V> m;
        final Class<K> keyType;
        final Class<V> valueType;

        private void typeCheck(Object key, Object value) {
            if (key != null && !keyType.isInstance(key))
                throw new ClassCastException(badKeyMsg(key));

            if (value != null && !valueType.isInstance(value))
                throw new ClassCastException(badValueMsg(value));
        }

        private String badKeyMsg(Object key) {
            return "Attempt to insert " + key.getClass() +
                    " key into map with key type " + keyType;
        }

        private String badValueMsg(Object value) {
            return "Attempt to insert " + value.getClass() +
                    " value into map with value type " + valueType;
        }

        CheckedMap(Map<K, V> m, Class<K> keyType, Class<V> valueType) {
            this.m = Objects.requireNonNull(m);
            this.keyType = Objects.requireNonNull(keyType);
            this.valueType = Objects.requireNonNull(valueType);
        }

        public int size()                      { return m.size(); }
        public boolean isEmpty()               { return m.isEmpty(); }
        public boolean containsKey(Object key) { return m.containsKey(key); }
        public boolean containsValue(Object v) { return m.containsValue(v); }
        public V get(Object key)               { return m.get(key); }
        public V remove(Object key)            { return m.remove(key); }
        public void clear()                    { m.clear(); }
        public Set<K> keySet()                 { return m.keySet(); }
        public Collection<V> values()          { return m.values(); }
        public boolean equals(Object o)        { return o == this || m.equals(o); }
        public int hashCode()                  { return m.hashCode(); }
        public String toString()               { return m.toString(); }

        public V put(K key, V value) {
            typeCheck(key, value);
            return m.put(key, value);
        }

        @SuppressWarnings("unchecked")
        public void putAll(Map<? extends K, ? extends V> t) {
            // Satisfy the following goals:
            // - good diagnostics in case of type mismatch
            // - all-or-nothing semantics
            // - protection from malicious t
            // - correct behavior if t is a concurrent map
            Object[] entries = t.entrySet().toArray();
            List<Map.Entry<K,V>> checked = new ArrayList<>(entries.length);
            for (Object o : entries) {
                Map.Entry<?,?> e = (Map.Entry<?,?>) o;
                Object k = e.getKey();
                Object v = e.getValue();
                typeCheck(k, v);
                checked.add(
                        new AbstractMap.SimpleImmutableEntry<>((K)k, (V)v));
            }
            for (Map.Entry<K,V> e : checked)
                m.put(e.getKey(), e.getValue());
        }

        private transient Set<Map.Entry<K,V>> entrySet;

        public Set<Map.Entry<K,V>> entrySet() {
            if (entrySet==null)
                entrySet = new CheckedEntrySet<>(m.entrySet(), valueType);
            return entrySet;
        }

        /**
         * We need this class in addition to CheckedSet as Map.Entry permits
         * modification of the backing Map via the setValue operation.  This
         * class is subtle: there are many possible attacks that must be
         * thwarted.
         *
         * @serial exclude
         */
        static class CheckedEntrySet<K,V> implements Set<Map.Entry<K,V>> {
            private final Set<Map.Entry<K,V>> s;
            private final Class<V> valueType;

            CheckedEntrySet(Set<Map.Entry<K, V>> s, Class<V> valueType) {
                this.s = s;
                this.valueType = valueType;
            }

            public int size()        { return s.size(); }
            public boolean isEmpty() { return s.isEmpty(); }
            public String toString() { return s.toString(); }
            public int hashCode()    { return s.hashCode(); }
            public void clear()      {        s.clear(); }

            public boolean add(Map.Entry<K, V> e) {
                throw new UnsupportedOperationException();
            }
            public boolean addAll(Collection<? extends Map.Entry<K, V>> coll) {
                throw new UnsupportedOperationException();
            }

            public Iterator<Map.Entry<K,V>> iterator() {
                final Iterator<Map.Entry<K, V>> i = s.iterator();
                final Class<V> valueType = this.valueType;

                return new Iterator<Map.Entry<K,V>>() {
                    public boolean hasNext() { return i.hasNext(); }
                    public void remove()     { i.remove(); }

                    public Map.Entry<K,V> next() {
                        return checkedEntry(i.next(), valueType);
                    }
                    // Android-note: Oversight of Iterator.forEachRemaining().
                    // http://b/110351017
                };
            }

            // Android-changed: Ignore IsInstanceOfClass warning. b/73288967, b/73344263.
            // @SuppressWarnings("unchecked")
            @SuppressWarnings({ "unchecked", "IsInstanceOfClass" })
            public Object[] toArray() {
                Object[] source = s.toArray();

                /*
                 * Ensure that we don't get an ArrayStoreException even if
                 * s.toArray returns an array of something other than Object
                 */
                Object[] dest = (CheckedEntry.class.isInstance(
                    source.getClass().getComponentType()) ? source :
                                 new Object[source.length]);

                for (int i = 0; i < source.length; i++)
                    dest[i] = checkedEntry((Map.Entry<K,V>)source[i],
                                           valueType);
                return dest;
            }

            @SuppressWarnings("unchecked")
            public <T> T[] toArray(T[] a) {
                // We don't pass a to s.toArray, to avoid window of
                // vulnerability wherein an unscrupulous multithreaded client
                // could get his hands on raw (unwrapped) Entries from s.
                T[] arr = s.toArray(a.length==0 ? a : Arrays.copyOf(a, 0));

                for (int i=0; i<arr.length; i++)
                    arr[i] = (T) checkedEntry((Map.Entry<K,V>)arr[i],
                                              valueType);
                if (arr.length > a.length)
                    return arr;

                System.arraycopy(arr, 0, a, 0, arr.length);
                if (a.length > arr.length)
                    a[arr.length] = null;
                return a;
            }

            /**
             * This method is overridden to protect the backing set against
             * an object with a nefarious equals function that senses
             * that the equality-candidate is Map.Entry and calls its
             * setValue method.
             */
            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                Map.Entry<?,?> e = (Map.Entry<?,?>) o;
                return s.contains(
                    (e instanceof CheckedEntry) ? e : checkedEntry(e, valueType));
            }

            /**
             * The bulk collection methods are overridden to protect
             * against an unscrupulous collection whose contains(Object o)
             * method senses when o is a Map.Entry, and calls o.setValue.
             */
            public boolean containsAll(Collection<?> c) {
                for (Object o : c)
                    if (!contains(o)) // Invokes safe contains() above
                        return false;
                return true;
            }

            public boolean remove(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                return s.remove(new AbstractMap.SimpleImmutableEntry
                                <>((Map.Entry<?,?>)o));
            }

            public boolean removeAll(Collection<?> c) {
                return batchRemove(c, false);
            }
            public boolean retainAll(Collection<?> c) {
                return batchRemove(c, true);
            }
            private boolean batchRemove(Collection<?> c, boolean complement) {
                Objects.requireNonNull(c);
                boolean modified = false;
                Iterator<Map.Entry<K,V>> it = iterator();
                while (it.hasNext()) {
                    if (c.contains(it.next()) != complement) {
                        it.remove();
                        modified = true;
                    }
                }
                return modified;
            }

            public boolean equals(Object o) {
                if (o == this)
                    return true;
                if (!(o instanceof Set))
                    return false;
                Set<?> that = (Set<?>) o;
                return that.size() == s.size()
                    && containsAll(that); // Invokes safe containsAll() above
            }

            static <K,V,T> CheckedEntry<K,V,T> checkedEntry(Map.Entry<K,V> e,
                                                            Class<T> valueType) {
                return new CheckedEntry<>(e, valueType);
            }

            /**
             * This "wrapper class" serves two purposes: it prevents
             * the client from modifying the backing Map, by short-circuiting
             * the setValue method, and it protects the backing Map against
             * an ill-behaved Map.Entry that attempts to modify another
             * Map.Entry when asked to perform an equality check.
             */
            private static class CheckedEntry<K,V,T> implements Map.Entry<K,V> {
                private final Map.Entry<K, V> e;
                private final Class<T> valueType;

                CheckedEntry(Map.Entry<K, V> e, Class<T> valueType) {
                    this.e = Objects.requireNonNull(e);
                    this.valueType = Objects.requireNonNull(valueType);
                }

                public K getKey()        { return e.getKey(); }
                public V getValue()      { return e.getValue(); }
                public int hashCode()    { return e.hashCode(); }
                public String toString() { return e.toString(); }

                public V setValue(V value) {
                    if (value != null && !valueType.isInstance(value))
                        throw new ClassCastException(badValueMsg(value));
                    return e.setValue(value);
                }

                private String badValueMsg(Object value) {
                    return "Attempt to insert " + value.getClass() +
                        " value into map with value type " + valueType;
                }

                public boolean equals(Object o) {
                    if (o == this)
                        return true;
                    if (!(o instanceof Map.Entry))
                        return false;
                    return e.equals(new AbstractMap.SimpleImmutableEntry
                                    <>((Map.Entry<?,?>)o));
                }
            }
        }
    }

    static class CheckedSortedMap<K,V> extends CheckedMap<K,V>
        implements SortedMap<K,V>, Serializable
    {

        private static final long serialVersionUID = 5457569999851959317L;
        private final SortedMap<K, V> sm;

        CheckedSortedMap(SortedMap<K, V> m,
                         Class<K> keyType, Class<V> valueType) {
            super(m, keyType, valueType);
            sm = m;
        }

        public Comparator<? super K> comparator() { return sm.comparator(); }
        public K firstKey()                       { return sm.firstKey(); }
        public K lastKey()                        { return sm.lastKey(); }

        public SortedMap<K,V> subMap(K fromKey, K toKey) {
            return checkedSortedMap(sm.subMap(fromKey, toKey),
                                    keyType, valueType);
        }
        public SortedMap<K,V> headMap(K toKey) {
            return checkedSortedMap(sm.headMap(toKey), keyType, valueType);
        }
        public SortedMap<K,V> tailMap(K fromKey) {
            return checkedSortedMap(sm.tailMap(fromKey), keyType, valueType);
        }
    }

    static class CheckedNavigableMap<K,V> extends CheckedSortedMap<K,V>
        implements NavigableMap<K,V>, Serializable
    {

        private static final long serialVersionUID = 3632423080137929419L;

        private final NavigableMap<K, V> nm;

        CheckedNavigableMap(NavigableMap<K, V> m,
                         Class<K> keyType, Class<V> valueType) {
            super(m, keyType, valueType);
            nm = m;
        }

        public Comparator<? super K> comparator()   { return nm.comparator(); }
        public K firstKey()                           { return nm.firstKey(); }
        public K lastKey()                             { return nm.lastKey(); }

        public Entry<K, V> lowerEntry(K key) {
            Entry<K,V> lower = nm.lowerEntry(key);
            return (null != lower)
                ? new CheckedMap.CheckedEntrySet.CheckedEntry<>(lower, valueType)
                : null;
        }

        public K lowerKey(K key)                   { return nm.lowerKey(key); }

        public Entry<K, V> floorEntry(K key) {
            Entry<K,V> floor = nm.floorEntry(key);
            return (null != floor)
                ? new CheckedMap.CheckedEntrySet.CheckedEntry<>(floor, valueType)
                : null;
        }

        public K floorKey(K key)                   { return nm.floorKey(key); }

        public Entry<K, V> ceilingEntry(K key) {
            Entry<K,V> ceiling = nm.ceilingEntry(key);
            return (null != ceiling)
                ? new CheckedMap.CheckedEntrySet.CheckedEntry<>(ceiling, valueType)
                : null;
        }

        public K ceilingKey(K key)               { return nm.ceilingKey(key); }

        public Entry<K, V> higherEntry(K key) {
            Entry<K,V> higher = nm.higherEntry(key);
            return (null != higher)
                ? new CheckedMap.CheckedEntrySet.CheckedEntry<>(higher, valueType)
                : null;
        }

        public K higherKey(K key)                 { return nm.higherKey(key); }

        public Entry<K, V> firstEntry() {
            Entry<K,V> first = nm.firstEntry();
            return (null != first)
                ? new CheckedMap.CheckedEntrySet.CheckedEntry<>(first, valueType)
                : null;
        }

        public Entry<K, V> lastEntry() {
            Entry<K,V> last = nm.lastEntry();
            return (null != last)
                ? new CheckedMap.CheckedEntrySet.CheckedEntry<>(last, valueType)
                : null;
        }

        public Entry<K, V> pollFirstEntry() {
            Entry<K,V> entry = nm.pollFirstEntry();
            return (null == entry)
                ? null
                : new CheckedMap.CheckedEntrySet.CheckedEntry<>(entry, valueType);
        }

        public Entry<K, V> pollLastEntry() {
            Entry<K,V> entry = nm.pollLastEntry();
            return (null == entry)
                ? null
                : new CheckedMap.CheckedEntrySet.CheckedEntry<>(entry, valueType);
        }

        public NavigableMap<K, V> descendingMap() {
            return checkedNavigableMap(nm.descendingMap(), keyType, valueType);
        }

        public NavigableSet<K> keySet() {
            return navigableKeySet();
        }

        public NavigableSet<K> navigableKeySet() {
            return checkedNavigableSet(nm.navigableKeySet(), keyType);
        }

        public NavigableSet<K> descendingKeySet() {
            return checkedNavigableSet(nm.descendingKeySet(), keyType);
        }

        @Override
        public NavigableMap<K,V> subMap(K fromKey, K toKey) {
            return checkedNavigableMap(nm.subMap(fromKey, true, toKey, false),
                                    keyType, valueType);
        }

        @Override
        public NavigableMap<K,V> headMap(K toKey) {
            return checkedNavigableMap(nm.headMap(toKey, false), keyType, valueType);
        }

        @Override
        public NavigableMap<K,V> tailMap(K fromKey) {
            return checkedNavigableMap(nm.tailMap(fromKey, true), keyType, valueType);
        }

        public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
            return checkedNavigableMap(nm.subMap(fromKey, fromInclusive, toKey, toInclusive), keyType, valueType);
        }

        public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
            return checkedNavigableMap(nm.headMap(toKey, inclusive), keyType, valueType);
        }

        public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
            return checkedNavigableMap(nm.tailMap(fromKey, inclusive), keyType, valueType);
        }
    }

    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        Collections.sort(list);
    }

    public static <T> void sort(List<T> list, Comparator<? super T> c) {
        Collections.sort(list, c);
    }

    public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T key) {
        return Collections.binarySearch(list, key);
    }

    public static <T> int binarySearch(List<? extends T> list, T key, Comparator<? super T> c) {
        return Collections.binarySearch(list, key, c);
    }

    public static void reverse(List<?> list) {
        Collections.reverse(list);
    }

    public static void shuffle(List<?> list) {
        Collections.shuffle(list);
    }

    public static void shuffle(List<?> list, Random rnd) {
        if (rnd == null) Collections.shuffle(list);
        else Collections.shuffle(list, rnd);
    }

    public static void swap(List<?> list, int i, int j) {
        Collections.swap(list, i, j);
    }

    public static <T> void fill(List<? super T> list, T obj) {
        Collections.fill(list, obj);
    }

    public static <T> void copy(List<? super T> dest, List<? extends T> src) {
        Collections.copy(dest, src);
    }

    public static <T extends Object & Comparable<? super T>> T min(Collection<? extends T> coll) {
        return Collections.min(coll);
    }

    public static <T> T min(Collection<? extends T> coll, Comparator<? super T> comp) {
        return Collections.min(coll, comp);
    }

    public static <T extends Object & Comparable<? super T>> T max(Collection<? extends T> coll) {
        return Collections.max(coll);
    }

    public static <T> T max(Collection<? extends T> coll, Comparator<? super T> comp) {
        return Collections.max(coll, comp);
    }

    public static void rotate(List<?> list, int distance) {
        Collections.rotate(list, distance);
    }

    public static <T> boolean replaceAll(List<T> list, T oldVal, T newVal) {
        return Collections.replaceAll(list, oldVal, newVal);
    }

    public static int indexOfSubList(List<?> source, List<?> target) {
        return Collections.indexOfSubList(source, target);
    }

    public static int lastIndexOfSubList(List<?> source, List<?> target) {
        return Collections.lastIndexOfSubList(source, target);
    }

    public static <T> Collection<T> unmodifiableCollection(Collection<? extends T> c) {
        return Collections.unmodifiableCollection(c);
    }

    public static <T> Set<T> unmodifiableSet(Set<? extends T> s) {
        return Collections.unmodifiableSet(s);
    }

    public static <T> SortedSet<T> unmodifiableSortedSet(SortedSet<T> s) {
        return Collections.unmodifiableSortedSet(s);
    }

    public static <T> NavigableSet<T> unmodifiableNavigableSet(NavigableSet<T> s) {
        if (s.getClass() == UnmodifiableNavigableSet.class) {
            return s;
        }
        return new UnmodifiableNavigableSet<>(s);
    }

    public static <T> List<T> unmodifiableList(List<? extends T> list) {
        return Collections.unmodifiableList(list);
    }

    public static <K,V> Map<K,V> unmodifiableMap(Map<? extends K, ? extends V> m) {
        return Collections.unmodifiableMap(m);
    }

    public static <K,V> SortedMap<K,V> unmodifiableSortedMap(SortedMap<K, ? extends V> m) {
        return Collections.unmodifiableSortedMap(m);
    }

    @SuppressWarnings("unchecked")
    public static <K,V> NavigableMap<K,V> unmodifiableNavigableMap(NavigableMap<K, ? extends V> m) {
        if (m.getClass() == UnmodifiableNavigableMap.class) {
            return (NavigableMap<K,V>) m;
        }
        return new UnmodifiableNavigableMap<>(m);
    }

    public static <T> Collection<T> synchronizedCollection(Collection<T> c) {
        return Collections.synchronizedCollection(c);
    }

    public static <T> Set<T> synchronizedSet(Set<T> s) {
        return Collections.synchronizedSet(s);
    }

    public static <T> SortedSet<T> synchronizedSortedSet(SortedSet<T> s) {
        return Collections.synchronizedSortedSet(s);
    }

    public static <T> NavigableSet<T> synchronizedNavigableSet(NavigableSet<T> s) {
        return new SynchronizedNavigableSet<>(s);
    }

    public static <T> List<T> synchronizedList(List<T> list) {
        return Collections.synchronizedList(list);
    }

    public static <K,V> Map<K,V> synchronizedMap(Map<K,V> m) {
        return Collections.synchronizedMap(m);
    }

    public static <K,V> SortedMap<K,V> synchronizedSortedMap(SortedMap<K,V> m) {
        return Collections.synchronizedSortedMap(m);
    }

    public static <K,V> NavigableMap<K,V> synchronizedNavigableMap(NavigableMap<K,V> m) {
        return new SynchronizedNavigableMap<>(m);
    }

    public static <E> Collection<E> checkedCollection(Collection<E> c, Class<E> type) {
        return Collections.checkedCollection(c, type);
    }

    public static <E> Queue<E> checkedQueue(Queue<E> queue, Class<E> type) {
        return new CheckedQueue<>(queue, type);
    }

    public static <E> Set<E> checkedSet(Set<E> s, Class<E> type) {
        return Collections.checkedSet(s, type);
    }

    public static <E> SortedSet<E> checkedSortedSet(SortedSet<E> s, Class<E> type) {
        return Collections.checkedSortedSet(s, type);
    }

    public static <E> NavigableSet<E> checkedNavigableSet(NavigableSet<E> s, Class<E> type) {
        return new CheckedNavigableSet<>(s, type);
    }

    public static <E> List<E> checkedList(List<E> list, Class<E> type) {
        return Collections.checkedList(list, type);
    }

    public static <K, V> Map<K, V> checkedMap(Map<K, V> m, Class<K> keyType, Class<V> valueType) {
        return Collections.checkedMap(m, keyType, valueType);
    }

    public static <K,V> SortedMap<K,V> checkedSortedMap(SortedMap<K, V> m, Class<K> keyType, Class<V> valueType) {
        return Collections.checkedSortedMap(m, keyType, valueType);
    }

    public static <K,V> NavigableMap<K,V> checkedNavigableMap(NavigableMap<K, V> m, Class<K> keyType, Class<V> valueType) {
        return new CheckedNavigableMap<>(m, keyType, valueType);
    }

    public static <T> Collection<T> asCollection(T o) {
        return Collections.singleton(o);
    }

    public static <T> Set<T> asSet(T o) {
        return Collections.singleton(o);
    }

    public static <T> List<T> asList(T o) {
        return Collections.singletonList(o);
    }

    public static <K,V> Map<K,V> singletonMap(K key, V value) {
        return Collections.singletonMap(key, value);
    }

    public static <T> List<T> copies(int n, T o) {
        return Collections.nCopies(n, o);
    }

    public static <T> Comparator<T> reverseOrder() {
        return Collections.reverseOrder();
    }

    public static <T> Comparator<T> reverseOrder(Comparator<T> cmp) {
        return Collections.reverseOrder(cmp);
    }

    public static int frequency(Collection<?> c, Object o) {
        return Collections.frequency(c, o);
    }

    public static boolean disjoint(Collection<?> a, Collection<?> b) {
        return Collections.disjoint(a, b);
    }

    public static <T> boolean addAll(Collection<? super T> c, T... elements) {
        return Collections.addAll(c, elements);
    }

    public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
        return Collections.newSetFromMap(map);
    }

    public static <T> Queue<T> asLifoQueue(Deque<T> deque) {
        return Collections.asLifoQueue(deque);
    }
    
}
