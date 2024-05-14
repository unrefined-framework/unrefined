package unrefined.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public final class EmptyCollections {
    
    private EmptyCollections() {
        throw new NotInstantiableError(EmptyCollections.class);
    }
    
    public static <K, V> Map<K, V> ofMap() {
        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    public static <K, V> NavigableMap<K, V> ofNavigableMap() {
        return (NavigableMap<K, V>) FastCollections.UnmodifiableNavigableMap.EMPTY_NAVIGABLE_MAP;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> SortedMap<K, V> ofSortedMap() {
        return (SortedMap<K,V>) FastCollections.UnmodifiableNavigableMap.EMPTY_NAVIGABLE_MAP;
    }

    public static <E> Collection<E> ofCollection() {
        return Collections.emptySet();
    }

    public static <E> List<E> ofList() {
        return Collections.emptyList();
    }

    public static <E> Set<E> ofSet() {
        return Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public static <E> NavigableSet<E> ofNavigableSet() {
        return (NavigableSet<E>) FastCollections.UnmodifiableNavigableSet.EMPTY_NAVIGABLE_SET;
    }

    @SuppressWarnings("unchecked")
    public static <E> SortedSet<E> ofSortedSet() {
        return (NavigableSet<E>) FastCollections.UnmodifiableNavigableSet.EMPTY_NAVIGABLE_SET;
    }

    public static <E> Iterator<E> ofIterator() {
        return Collections.emptyIterator();
    }

    public static <E> ListIterator<E> ofListIterator() {
        return Collections.emptyListIterator();
    }

}
