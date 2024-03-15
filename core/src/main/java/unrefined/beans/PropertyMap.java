package unrefined.beans;

import unrefined.util.Half;
import unrefined.util.Rational;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.signal.Signal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public interface PropertyMap extends Map<Object, Object> {

    static PropertyMap of(Map<Object, Object> map) {
        if (map instanceof PropertyMap) return (PropertyMap) map;
        else return new Wrapper(map);
    }

    @SuppressWarnings("unchecked")
    default <T> T get(Object key, Class<T> clazz) {
        Object value = get(key);
        if (clazz.isInstance(value)) return (T) value;
        else return null;
    }

    default <T> T get(Object key, Class<T> clazz, T defaultValue) {
        T value = get(key, clazz);
        return value == null ? defaultValue : value;
    }

    default String getProperty(String key) {
        return get(key, String.class);
    }

    default String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : value;
    }

    default String putProperty(String key, String value) {
        Object result = put(key, value);
        return result instanceof String ? (String) result : null;
    }

    default String putBooleanProperty(String key, boolean value) {
        return putProperty(key, Boolean.toString(value));
    }

    default String putByteProperty(String key, byte value) {
        return putProperty(key, Byte.toString(value));
    }

    default String putShortProperty(String key, short value) {
        return putProperty(key, Short.toString(value));
    }

    default String putCharProperty(String key, char value) {
        return putProperty(key, Character.toString(value));
    }

    default String putIntProperty(String key, int value) {
        return putProperty(key, Integer.toString(value));
    }

    default String putLongProperty(String key, long value) {
        return putProperty(key, Long.toString(value));
    }

    default String putFloatProperty(String key, float value) {
        return putProperty(key, Float.toString(value));
    }

    default String putDoubleProperty(String key, double value) {
        return putProperty(key, Double.toString(value));
    }

    default String putBigIntegerProperty(String key, BigInteger value) {
        return putProperty(key, value.toString());
    }

    default String putBigDecimalProperty(String key, BigDecimal value) {
        return putProperty(key, value.toEngineeringString());
    }

    default String putRational(String key, Rational value) {
        return putProperty(key, value.toString());
    }

    default String putHalf(String key, short value) {
        return putProperty(key, Half.toString(value));
    }

    default String removeProperty(String key) {
        Object result = remove(key);
        return result instanceof String ? (String) result : null;
    }

    default boolean parseBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    default byte parseByteProperty(String key, int radix, byte defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Byte.parseByte(value, radix);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    default byte parseByteProperty(String key, byte defaultValue) {
        return parseByteProperty(key, 10, defaultValue);
    }

    default byte parseByteProperty(String key) {
        return Byte.parseByte(getProperty(key));
    }

    default char parseCharProperty(String key) {
        String value = getProperty(key);
        if (value.length() != 1) throw new StringIndexOutOfBoundsException();
        return value.charAt(0);
    }

    default char parseCharProperty(String key, char defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : (value.length() == 1 ? value.charAt(0) : defaultValue);
    }

    default short parseShortProperty(String key, int radix, short defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Short.parseShort(value, radix);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    default short parseShortProperty(String key, short defaultValue) {
        return parseShortProperty(key, 10, defaultValue);
    }

    default short parseShortProperty(String key) {
        return Short.parseShort(getProperty(key));
    }

    default int parseIntProperty(String key, int radix, int defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Integer.parseInt(value, radix);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    default int parseIntProperty(String key, int defaultValue) {
        return parseIntProperty(key, 10, defaultValue);
    }

    default int parseIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }

    default long parseLongProperty(String key, int radix, long defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Long.parseLong(value, radix);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    default long parseLongProperty(String key, long defaultValue) {
        return parseLongProperty(key, 10, defaultValue);
    }

    default long parseLongProperty(String key) {
        return Long.parseLong(getProperty(key));
    }

    default float parseFloatProperty(String key, float defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Float.parseFloat(value);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    default float parseFloatProperty(String key) {
        return Float.parseFloat(getProperty(key));
    }

    default double parseDoubleProperty(String key, double defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Double.parseDouble(value);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    default double parseDoubleProperty(String key) {
        return Double.parseDouble(getProperty(key));
    }

    default BigInteger parseBigIntegerProperty(String key, int radix, BigInteger defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : new BigInteger(value, radix);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    default BigInteger parseBigIntegerProperty(String key, BigInteger defaultValue) {
        return parseBigIntegerProperty(key, 10, defaultValue);
    }

    default BigInteger parseBigIntegerProperty(String key) {
        return new BigInteger(getProperty(key));
    }

    default BigDecimal parseBigDecimalProperty(String key, BigDecimal defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : new BigDecimal(value);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    default BigDecimal parseBigDecimalProperty(String key) {
        return new BigDecimal(getProperty(key));
    }

    default Rational parseRationalProperty(String key, Rational defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Rational.parseRational(value);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    default Rational parseRationalProperty(String key) {
        return Rational.parseRational(getProperty(key));
    }

    default short parseHalfProperty(String key, short defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Half.parseHalf(value);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    default short parseHalfProperty(String key) {
        return Half.parseHalf(getProperty(key));
    }

    Signal<EventSlot<PropertyChangeEvent>> onChange();

    final class PropertyChangeEvent extends Event<PropertyMap> {

        private final Object key;
        private final Object previousValue, currentValue;

        public PropertyChangeEvent(PropertyMap source, Object key, Object previousValue, Object currentValue) {
            super(source);
            this.key = key;
            this.previousValue = previousValue;
            this.currentValue = currentValue;
        }

        public Object getKey() {
            return key;
        }

        @SuppressWarnings("unchecked")
        public <T> T getKey(Class<T> clazz) {
            if (clazz.isInstance(key)) return (T) key;
            else return null;
        }

        public Object getPreviousValue() {
            return previousValue;
        }

        @SuppressWarnings("unchecked")
        public <T> T getPreviousValue(Class<T> clazz) {
            if (clazz.isInstance(previousValue)) return (T) previousValue;
            else return null;
        }

        public Object getCurrentValue() {
            return currentValue;
        }

        @SuppressWarnings("unchecked")
        public <T> T getCurrentValue(Class<T> clazz) {
            if (clazz.isInstance(currentValue)) return (T) currentValue;
            else return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            PropertyChangeEvent that = (PropertyChangeEvent) o;

            if (!Objects.equals(key, that.key)) return false;
            if (!Objects.equals(previousValue, that.previousValue))
                return false;
            return Objects.equals(currentValue, that.currentValue);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (key != null ? key.hashCode() : 0);
            result = 31 * result + (previousValue != null ? previousValue.hashCode() : 0);
            result = 31 * result + (currentValue != null ? currentValue.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "source=" + getSource() +
                    ", key=" + key +
                    ", previousValue=" + previousValue +
                    ", currentValue=" + currentValue +
                    '}';
        }

    }
    
    final class Wrapper implements PropertyMap {

        private volatile EntrySet entrySet;
        private volatile KeySet keySet;
        private volatile ValuesCollection valuesCollection;

        private final Map<Object, Object> map;
        public Wrapper(Map<Object, Object> map) {
            this.map = Objects.requireNonNull(map);
        }

        private final Signal<EventSlot<PropertyChangeEvent>> onChange = Signal.ofSlot();
        @Override
        public Signal<EventSlot<PropertyChangeEvent>> onChange() {
            return onChange;
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return map.containsValue(value);
        }

        @Override
        public Object get(Object key) {
            return map.get(key);
        }

        @Override
        public Object put(Object key, Object value) {
            Object ret;
            if (map.containsKey(key)) {
                ret = map.put(key, value);
                if (ret == null && value != null || ret != null && !ret.equals(value)) {
                    onChange.emit(new PropertyChangeEvent(this, key, ret, value));
                }
            } else {
                ret = map.put(key, value);
                onChange.emit(new PropertyChangeEvent(this, key, ret, value));
            }
            return ret;
        }

        @Override
        public Object remove(Object key) {
            if (!map.containsKey(key)) {
                return null;
            }
            Object ret = map.remove(key);
            onChange.emit(new PropertyChangeEvent(this, key, ret, null));
            return ret;
        }

        @Override
        public void putAll(Map<?, ?> m) {
            for (Entry<?, ?> e : m.entrySet()) {
                put(e.getKey(), e.getValue());
            }
        }

        @Override
        public void clear() {
            for (Iterator<Entry<Object, Object>> i = map.entrySet().iterator(); i.hasNext(); ) {
                Entry<Object, Object> e = i.next();
                Object key = e.getKey();
                Object val = e.getValue();
                i.remove();
                onChange.emit(new PropertyChangeEvent(this, key, val, null));
            }
        }

        @Override
        public Set<Object> keySet() {
            KeySet set;
            if ((set = keySet) != null) return set;
            return keySet = new KeySet();
        }

        @Override
        public Collection<Object> values() {
            ValuesCollection collection;
            if ((collection = valuesCollection) != null) return collection;
            return valuesCollection = new ValuesCollection();
        }

        @Override
        public Set<Entry<Object, Object>> entrySet() {
            EntrySet set;
            if ((set = entrySet) != null) return set;
            return entrySet = new EntrySet();
        }

        @Override
        public String toString() {
            return map.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return map.equals(obj);
        }

        @Override
        public int hashCode() {
            return map.hashCode();
        }

        private class KeySet implements Set<Object> {
            @Override
            public int size() {
                return map.size();
            }
            @Override
            public boolean isEmpty() {
                return map.isEmpty();
            }
            @Override
            public boolean contains(Object o) {
                return map.keySet().contains(o);
            }
            @Override
            public Iterator<Object> iterator() {
                return new Iterator<Object>() {
                    private final Iterator<Entry<Object, Object>> iterator = map.entrySet().iterator();
                    private Object lastKey;
                    private Object lastValue;
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }
                    @Override
                    public Object next() {
                        Entry<Object, Object> last = iterator.next();
                        lastKey = last.getKey();
                        lastValue = last.getValue();
                        return last.getKey();
                    }
                    @Override
                    public void remove() {
                        iterator.remove();
                        onChange.emit(new PropertyChangeEvent(Wrapper.this, lastKey, lastValue, null));
                    }
                };
            }
            @Override
            public Object[] toArray() {
                return map.keySet().toArray();
            }
            @Override
            public <T> T[] toArray(T[] a) {
                return map.keySet().toArray(a);
            }
            @Override
            public boolean add(Object e) {
                throw new UnsupportedOperationException("Not supported.");
            }
            @Override
            public boolean remove(Object o) {
                return Wrapper.this.remove(o) != null;
            }
            @Override
            public boolean containsAll(Collection<?> c) {
                return map.keySet().containsAll(c);
            }
            @Override
            public boolean addAll(Collection<?> c) {
                throw new UnsupportedOperationException("Not supported.");
            }
            @Override
            public boolean retainAll(Collection<?> c) {
                return removeRetain(c, false);
            }
            private boolean removeRetain(Collection<?> c, boolean remove) {
                boolean removed = false;
                for (Iterator<Entry<Object, Object>> i = map.entrySet().iterator(); i.hasNext();) {
                    Entry<Object, Object> e = i.next();
                    if (remove == c.contains(e.getKey())) {
                        removed = true;
                        Object key = e.getKey();
                        Object value = e.getValue();
                        i.remove();
                        onChange.emit(new PropertyChangeEvent(Wrapper.this, key, value, null));
                    }
                }
                return removed;
            }
            @Override
            public boolean removeAll(Collection<?> c) {
                return removeRetain(c, true);
            }
            @Override
            public void clear() {
                Wrapper.this.clear();
            }
            @Override
            public String toString() {
                return map.keySet().toString();
            }
            @Override
            public boolean equals(Object obj) {
                return map.keySet().equals(obj);
            }
            @Override
            public int hashCode() {
                return map.keySet().hashCode();
            }
        }

        private class ValuesCollection implements Collection<Object> {
            @Override
            public int size() {
                return map.size();
            }
            @Override
            public boolean isEmpty() {
                return map.isEmpty();
            }
            @Override
            public boolean contains(Object o) {
                return map.values().contains(o);
            }
            @Override
            public Iterator<Object> iterator() {
                return new Iterator<Object>() {
                    private final Iterator<Entry<Object, Object>> iterator = map.entrySet().iterator();
                    private Object lastKey;
                    private Object lastValue;
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }
                    @Override
                    public Object next() {
                        Entry<Object, Object> last = iterator.next();
                        lastKey = last.getKey();
                        lastValue = last.getValue();
                        return lastValue;
                    }
                    @Override
                    public void remove() {
                        iterator.remove();
                        onChange.emit(new PropertyChangeEvent(Wrapper.this, lastKey, lastValue, null));
                    }
                };
            }
            @Override
            public Object[] toArray() {
                return map.values().toArray();
            }
            @Override
            public <T> T[] toArray(T[] a) {
                return map.values().toArray(a);
            }
            @Override
            public boolean add(Object e) {
                throw new UnsupportedOperationException("Not supported.");
            }
            @Override
            public boolean remove(Object o) {
                for(Iterator<Object> i = iterator(); i.hasNext();) {
                    if (i.next().equals(o)) {
                        i.remove();
                        return true;
                    }
                }
                return false;
            }
            @Override
            public boolean containsAll(Collection<?> c) {
                return map.values().containsAll(c);
            }
            @Override
            public boolean addAll(Collection<?> c) {
                throw new UnsupportedOperationException("Not supported.");
            }
            @Override
            public boolean removeAll(Collection<?> c) {
                return removeRetain(c, true);
            }
            private boolean removeRetain(Collection<?> c, boolean remove) {
                boolean removed = false;
                for (Iterator<Entry<Object, Object>> i = map.entrySet().iterator(); i.hasNext();) {
                    Entry<Object, Object> e = i.next();
                    if (remove == c.contains(e.getValue())) {
                        removed = true;
                        Object key = e.getKey();
                        Object value = e.getValue();
                        i.remove();
                        onChange.emit(new PropertyChangeEvent(Wrapper.this, key, value, null));
                    }
                }
                return removed;
            }
            @Override
            public boolean retainAll(Collection<?> c) {
                return removeRetain(c, false);
            }
            @Override
            public void clear() {
                Wrapper.this.clear();
            }
            @Override
            public String toString() {
                return map.values().toString();
            }
            @Override
            public boolean equals(Object obj) {
                return map.values().equals(obj);
            }
            @Override
            public int hashCode() {
                return map.values().hashCode();
            }
        }

        private class ObservableEntry implements Entry<Object, Object> {
            private final Entry<Object, Object> entry;
            public ObservableEntry(Entry<Object, Object> entry) {
                this.entry = entry;
            }
            @Override
            public Object getKey() {
                return entry.getKey();
            }
            @Override
            public Object getValue() {
                return entry.getValue();
            }
            @Override
            public Object setValue(Object value) {
                Object oldValue = entry.setValue(value);
                onChange.emit(new PropertyChangeEvent(Wrapper.this, getKey(), oldValue, value));
                return oldValue;
            }
            @Override
            public final boolean equals(Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                Entry<?, ?> e = (Entry<?, ?>) o;
                Object k1 = getKey();
                Object k2 = e.getKey();
                if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                    Object v1 = getValue();
                    Object v2 = e.getValue();
                    if (v1 == v2 || (v1 != null && v1.equals(v2))) {
                        return true;
                    }
                }
                return false;
            }
            @Override
            public final int hashCode() {
                return (getKey() == null ? 0 : getKey().hashCode())
                        ^ (getValue() == null ? 0 : getValue().hashCode());
            }
            @Override
            public final String toString() {
                return getKey() + "=" + getValue();
            }
        }

        private class EntrySet implements Set<Entry<Object, Object>> {
            @Override
            public int size() {
                return map.size();
            }
            @Override
            public boolean isEmpty() {
                return map.isEmpty();
            }
            @Override
            public boolean contains(Object o) {
                return map.entrySet().contains(o);
            }
            @Override
            public Iterator<Entry<Object, Object>> iterator() {
                return new Iterator<Entry<Object, Object>>() {
                    private final Iterator<Entry<Object, Object>> iterator = map.entrySet().iterator();
                    private Object lastKey;
                    private Object lastValue;
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }
                    @Override
                    public Entry<Object, Object> next() {
                        Entry<Object, Object> last = iterator.next();
                        lastKey = last.getKey();
                        lastValue = last.getValue();
                        return new ObservableEntry(last);
                    }
                    @Override
                    public void remove() {
                        iterator.remove();
                        onChange.emit(new PropertyChangeEvent(Wrapper.this, lastKey, lastValue, null));
                    }
                };
            }
            @SuppressWarnings("unchecked")
            @Override
            public Object[] toArray() {
                Object[] array = map.entrySet().toArray();
                for (int i = 0; i < array.length; i ++) {
                    array[i] = new ObservableEntry((Entry<Object, Object>) array[i]);
                }
                return array;
            }
            @SuppressWarnings("unchecked")
            @Override
            public <T> T[] toArray(T[] a) {
                T[] array = map.entrySet().toArray(a);
                for (int i = 0; i < array.length; i ++) {
                    array[i] = (T) new ObservableEntry((Entry<Object, Object>)array[i]);
                }
                return array;
            }
            @Override
            public boolean add(Entry<Object, Object> e) {
                throw new UnsupportedOperationException("Not supported.");
            }
            @SuppressWarnings("unchecked")
            @Override
            public boolean remove(Object o) {
                boolean ret = map.entrySet().remove(o);
                if (ret) {
                    Entry<Object, Object> entry = (Entry<Object, Object>) o;
                    onChange.emit(new PropertyChangeEvent(Wrapper.this, entry.getKey(), entry.getValue(), null));
                }
                return ret;
            }
            @Override
            public boolean containsAll(Collection<?> c) {
                return map.entrySet().containsAll(c);
            }
            @Override
            public boolean addAll(Collection<? extends Entry<Object, Object>> c) {
                throw new UnsupportedOperationException("Not supported.");
            }
            @Override
            public boolean retainAll(Collection<?> c) {
                return removeRetain(c, false);
            }
            private boolean removeRetain(Collection<?> c, boolean remove) {
                boolean removed = false;
                for (Iterator<Entry<Object, Object>> i = map.entrySet().iterator(); i.hasNext();) {
                    Entry<Object, Object> e = i.next();
                    if (remove == c.contains(e)) {
                        removed = true;
                        Object key = e.getKey();
                        Object value = e.getValue();
                        i.remove();
                        onChange.emit(new PropertyChangeEvent(Wrapper.this, key, value, null));
                    }
                }
                return removed;
            }
            @Override
            public boolean removeAll(Collection<?> c) {
                return removeRetain(c, true);
            }
            @Override
            public void clear() {
                Wrapper.this.clear();
            }
            @Override
            public String toString() {
                return map.entrySet().toString();
            }
            @Override
            public boolean equals(Object obj) {
                return map.entrySet().equals(obj);
            }
            @Override
            public int hashCode() {
                return map.entrySet().hashCode();
            }
        }

    }

}
