package unrefined.context;

import unrefined.util.Half;
import unrefined.util.ProducerThreadLocal;
import unrefined.util.Rational;
import unrefined.util.concurrent.Producer;
import unrefined.util.function.Functor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Environment implements Map<Object, Object> {

    public static <S> S obtain(Class<S> clazz, ClassLoader classLoader) {
        Iterator<S> iterator = ServiceLoader.load(clazz, classLoader).iterator();
        if (iterator.hasNext()) return iterator.next();
        else return null;
    }

    public static <S> S obtain(Class<S> clazz) {
        Iterator<S> iterator = ServiceLoader.load(clazz).iterator();
        if (iterator.hasNext()) return iterator.next();
        else return null;
    }

    public static final Environment system = new Environment(() -> new ConcurrentHashMap<>(System.getenv()), "SYSTEM ENVIRONMENT VARIABLES");
    public static final Environment properties = new Environment(System::getProperties, "JAVA VIRTUAL MACHINE PROPERTIES");
    public static final Environment global = new Environment(new ConcurrentHashMap<>(), "APPLICATION GLOBAL ENVIRONMENT");
    public static final Environment threadLocal = new Environment(new ProducerThreadLocal<>(HashMap::new)::get, "THREAD LOCAL ENVIRONMENT");

    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> clazz) {
        Object value = get(key);
        if (clazz.isInstance(value)) return (T) value;
        else return null;
    }

    public <T> T get(Object key, Class<T> clazz, T defaultValue) {
        T value = get(key, clazz);
        return value == null ? defaultValue : value;
    }

    public String getProperty(String key) {
        return get(key, String.class);
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : value;
    }

    public String putProperty(String key, String value) {
        Object result = put(key, value);
        return result instanceof String ? (String) result : null;
    }

    public String setProperty(String key, String value) {
        return value == null ? removeProperty(key) : putProperty(key, value);
    }

    public String putBooleanProperty(String key, boolean value) {
        return putProperty(key, Boolean.toString(value));
    }

    public String putByteProperty(String key, byte value) {
        return putProperty(key, Byte.toString(value));
    }

    public String putShortProperty(String key, short value) {
        return putProperty(key, Short.toString(value));
    }

    public String putCharProperty(String key, char value) {
        return putProperty(key, Character.toString(value));
    }

    public String putIntProperty(String key, int value) {
        return putProperty(key, Integer.toString(value));
    }

    public String putLongProperty(String key, long value) {
        return putProperty(key, Long.toString(value));
    }

    public String putFloatProperty(String key, float value) {
        return putProperty(key, Float.toString(value));
    }

    public String putDoubleProperty(String key, double value) {
        return putProperty(key, Double.toString(value));
    }

    public String putBigIntegerProperty(String key, BigInteger value) {
        return putProperty(key, value.toString());
    }

    public String putBigDecimalProperty(String key, BigDecimal value) {
        return putProperty(key, value.toEngineeringString());
    }

    public String putRational(String key, Rational value) {
        return putProperty(key, value.toString());
    }

    public String putHalf(String key, short value) {
        return putProperty(key, Half.toString(value));
    }

    public String removeProperty(String key) {
        Object result = remove(key);
        return result instanceof String ? (String) result : null;
    }

    public boolean parseBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    public byte parseByteProperty(String key, int radix, byte defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Byte.parseByte(value, radix);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public byte parseByteProperty(String key, byte defaultValue) {
        return parseByteProperty(key, 10, defaultValue);
    }

    public byte parseByteProperty(String key) {
        return Byte.parseByte(getProperty(key));
    }

    public char parseCharProperty(String key) {
        String value = getProperty(key);
        if (value.length() != 1) throw new StringIndexOutOfBoundsException();
        return value.charAt(0);
    }

    public char parseCharProperty(String key, char defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : (value.length() == 1 ? value.charAt(0) : defaultValue);
    }

    public short parseShortProperty(String key, int radix, short defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Short.parseShort(value, radix);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public short parseShortProperty(String key, short defaultValue) {
        return parseShortProperty(key, 10, defaultValue);
    }

    public short parseShortProperty(String key) {
        return Short.parseShort(getProperty(key));
    }

    public int parseIntProperty(String key, int radix, int defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Integer.parseInt(value, radix);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public int parseIntProperty(String key, int defaultValue) {
        return parseIntProperty(key, 10, defaultValue);
    }

    public int parseIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public long parseLongProperty(String key, int radix, long defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Long.parseLong(value, radix);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public long parseLongProperty(String key, long defaultValue) {
        return parseLongProperty(key, 10, defaultValue);
    }

    public long parseLongProperty(String key) {
        return Long.parseLong(getProperty(key));
    }

    public float parseFloatProperty(String key, float defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Float.parseFloat(value);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public float parseFloatProperty(String key) {
        return Float.parseFloat(getProperty(key));
    }

    public double parseDoubleProperty(String key, double defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Double.parseDouble(value);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double parseDoubleProperty(String key) {
        return Double.parseDouble(getProperty(key));
    }

    public BigInteger parseBigIntegerProperty(String key, int radix, BigInteger defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : new BigInteger(value, radix);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public BigInteger parseBigIntegerProperty(String key, BigInteger defaultValue) {
        return parseBigIntegerProperty(key, 10, defaultValue);
    }

    public BigInteger parseBigIntegerProperty(String key) {
        return new BigInteger(getProperty(key));
    }

    public BigDecimal parseBigDecimalProperty(String key, BigDecimal defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : new BigDecimal(value);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public BigDecimal parseBigDecimalProperty(String key) {
        return new BigDecimal(getProperty(key));
    }

    public Rational parseRationalProperty(String key, Rational defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Rational.parseRational(value);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public Rational parseRationalProperty(String key) {
        return Rational.parseRational(getProperty(key));
    }

    public short parseHalfProperty(String key, short defaultValue) {
        String value = getProperty(key);
        try {
            return value == null ? defaultValue : Half.parseHalf(value);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public short parseHalfProperty(String key) {
        return Half.parseHalf(getProperty(key));
    }

    public <T> T parseProperty(String key, T defaultValue, Functor<String, T> parseProc) {
        String value = getProperty(key);
        return value == null ? defaultValue : parseProc.apply(value);
    }

    public Producer<?> getProducer(Object key) {
        return get(key, Producer.class);
    }

    public Object getAndInvokeProducer(Object key) {
        Producer<?> value = getProducer(key);
        return value == null ? null : value.get();
    }

    @SuppressWarnings("unchecked")
    public <V> V getAndInvokeProducer(Object key, Class<V> clazz) {
        Object result = getAndInvokeProducer(key);
        return clazz.isInstance(result) ? (V) result : null;
    }

    public Producer<?> putProducer(Object key, Producer<?> value) {
        Object result = put(key, value);
        return result instanceof Producer ? (Producer<?>) result : null;
    }

    public Producer<?> removeProducer(Object key) {
        Object result = remove(key);
        return result instanceof Producer ? (Producer<?>) result : null;
    }

    private final String name;
    private final Producer<Map<Object, Object>> m;

    public Environment(Producer<Map<Object, Object>> m) {
        this(m, null);
    }

    public Environment(Map<Object, Object> m) {
        this(() -> m);
    }

    public Environment(Map<Object, Object> m, String name) {
        this(() -> m, name);
    }

    public Environment(Producer<Map<Object, Object>> m, String name) {
        this.name = name;
        this.m = m;
    }

    @Override
    public int size() {
        return m.get().size();
    }

    @Override
    public boolean isEmpty() {
        return m.get().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return m.get().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return m.get().containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return m.get().get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        return m.get().put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return m.get().remove(key);
    }

    @Override
    public void putAll(Map<?, ?> m) {
        this.m.get().putAll(m);
    }

    @Override
    public void clear() {
        m.get().clear();
    }

    @Override
    public Set<Object> keySet() {
        return m.get().keySet();
    }

    @Override
    public Collection<Object> values() {
        return m.get().values();
    }

    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return m.get().entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Map)) return false;
        return m.get().equals(o);
    }

    @Override
    public int hashCode() {
        return m.get().hashCode();
    }

    @Override
    public String toString() {
        if (name == null) return m.get().toString();
        else return getClass().getName()
                + '{' +
                "<" + name + '>' +
                '}';
    }

}
