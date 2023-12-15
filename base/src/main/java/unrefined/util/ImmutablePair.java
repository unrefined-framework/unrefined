package unrefined.util;

import java.util.AbstractMap;
import java.util.Map;

public class ImmutablePair<K, V> extends AbstractMap.SimpleImmutableEntry<K, V> {

    private static final long serialVersionUID = 5227601981759172721L;

    public ImmutablePair(K key, V value) {
        super(key, value);
    }

    public ImmutablePair(Map.Entry<? extends K, ? extends V> entry) {
        super(entry);
    }

}
