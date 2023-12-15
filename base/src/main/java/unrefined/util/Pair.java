package unrefined.util;

import java.util.AbstractMap;
import java.util.Map;

public class Pair<K, V> extends AbstractMap.SimpleEntry<K, V> {

    private static final long serialVersionUID = 5211543988717518521L;

    public Pair(K key, V value) {
        super(key, value);
    }

    public Pair(Map.Entry<? extends K, ? extends V> entry) {
        super(entry);
    }

}
