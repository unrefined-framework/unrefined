package unrefined.beans;

import unrefined.util.concurrent.Producer;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.function.BiSlot;
import unrefined.util.function.Functor;
import unrefined.util.function.Slot;
import unrefined.util.ref.Cleaner;
import unrefined.util.signal.Signal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Property<V> {

    public static <K, V> Property<V> bind(PropertyMap map, K key, Functor<V, String> toStringProc, Functor<String, V> fromStringProc) {
        return new MappedPropertyMapBinding<>(map, key, toStringProc, fromStringProc);
    }

    public static <K, V> Property<V> bind(PropertyMap map, K key) {
        return new PropertyMapBinding<>(map, key);
    }

    public static <V> Property<V> bind(Producer<V> getter, Slot<V> setter) {
        return new FunctionBinding<>(getter, setter);
    }

    public static <V> Property<V> bind(Producer<V> getter) {
        return new FunctionBinding<>(getter);
    }

    public static <V> Property<V> bind(String key, BiSlot<String, PropertyChangeListener> registerProc, BiSlot<String, PropertyChangeListener> unregisterProc, Functor<String, V> getter, Slot<V> setter) {
        return new BeanBinding<>(key, registerProc, unregisterProc, getter, setter);
    }

    public static <V> Property<V> bind(String key, BiSlot<String, PropertyChangeListener> registerProc, BiSlot<String, PropertyChangeListener> unregisterProc, Functor<String, V> getter) {
        return new BeanBinding<>(key, registerProc, unregisterProc, getter);
    }

    public static <V> Property<V> ofAtomic(V initialValue) {
        return new AtomicInstance<>(initialValue);
    }

    public static <V> Property<V> ofAtomic() {
        return new AtomicInstance<>();
    }

    public static <V> Property<V> of(V initialValue) {
        return new Instance<>(initialValue);
    }

    public static <V> Property<V> ofDefault() {
        return new Instance<>();
    }

    private static class MappedPropertyMapBinding<K, V> extends Property<V> {
        private final PropertyMap map;
        private final K key;
        private final Functor<V, String> toStringProc;
        private final Functor<String, V> fromStringProc;
        public MappedPropertyMapBinding(PropertyMap map, K key, Functor<V, String> toStringProc, Functor<String, V> fromStringProc) {
            this.map = Objects.requireNonNull(map);
            this.key = Objects.requireNonNull(key);
            map.onChange().connect(changedEvent -> {
                V previousValue = fromStringProc.apply(changedEvent.getPreviousValue(String.class));
                V currentValue = fromStringProc.apply(changedEvent.getCurrentValue(String.class));
                if (previousValue != null || currentValue != null && !onChange().isEmpty()) {
                    onChange().emit(new ChangeEvent<>(MappedPropertyMapBinding.this, previousValue, currentValue));
                }
            });
            this.toStringProc = Objects.requireNonNull(toStringProc);
            this.fromStringProc = Objects.requireNonNull(fromStringProc);
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        @Override
        public void set(V value) {
            if (value == null) map.remove(key);
            else map.put(key, toStringProc.apply(value));
        }
        @Override
        public V get() {
            return fromStringProc.apply(map.get(key, String.class));
        }
    }

    private static class PropertyMapBinding<K, V> extends Property<V> {
        private final PropertyMap map;
        private final K key;
        @SuppressWarnings("unchecked")
        public PropertyMapBinding(PropertyMap map, K key) {
            this.map = Objects.requireNonNull(map);
            this.key = Objects.requireNonNull(key);
            map.onChange().connect(changedEvent -> {
                V previousValue, currentValue;
                try {
                    previousValue = (V) changedEvent.getPreviousValue();
                }
                catch (ClassCastException e) {
                    previousValue = null;
                }
                try {
                    currentValue = (V) changedEvent.getCurrentValue();
                }
                catch (ClassCastException e) {
                    currentValue = null;
                }
                if (previousValue != null || currentValue != null && !onChange().isEmpty()) {
                    onChange().emit(new ChangeEvent<>(PropertyMapBinding.this, previousValue, currentValue));
                }
            });
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        @Override
        public void set(V value) {
            if (value == null) map.remove(key);
            else map.put(key, value);
        }
        @SuppressWarnings("unchecked")
        @Override
        public V get() {
            try {
                return (V) map.get(key);
            }
            catch (ClassCastException e) {
                return null;
            }
        }
    }

    private static class FunctionBinding<V> extends Property<V> {
        private final Producer<V> getter;
        private final Slot<V> setter;
        public FunctionBinding(Producer<V> getter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = null;
        }
        public FunctionBinding(Producer<V> getter, Slot<V> setter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = setter;
        }
        @Override
        public boolean isReadOnly() {
            return setter == null;
        }
        @Override
        public void set(V value) {
            if (setter == null) throw new IllegalArgumentException("Property is read-only");
            else setter.accept(value);
        }
        @Override
        public V get() {
            return getter.get();
        }
    }

    private static class BeanBinding<V> extends Property<V> implements PropertyChangeListener {
        private final String key;
        private final Functor<String, V> getter;
        private final Slot<V> setter;
        public BeanBinding(String key, BiSlot<String, PropertyChangeListener> registerProc, BiSlot<String, PropertyChangeListener> unregisterProc, Functor<String, V> getter, Slot<V> setter) {
            this.key = Objects.requireNonNull(key);
            registerProc.accept(key, this);
            Cleaner.getInstance().register(this, () -> unregisterProc.accept(key, BeanBinding.this));
            this.getter = Objects.requireNonNull(getter);
            this.setter = setter;
        }
        public BeanBinding(String key, BiSlot<String, PropertyChangeListener> registerProc, BiSlot<String, PropertyChangeListener> unregisterProc, Functor<String, V> getter) {
            this(key, registerProc, unregisterProc, getter, null);
        }
        @Override
        public boolean isReadOnly() {
            return setter == null;
        }
        @Override
        public void set(V value) {
            if (setter == null) throw new IllegalArgumentException("Property is read-only");
            else setter.accept(value);
        }
        @Override
        public V get() {
            return getter.apply(key);
        }
        @SuppressWarnings("unchecked")
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            V previousValue, currentValue;
            try {
                previousValue = (V) evt.getOldValue();
            }
            catch (ClassCastException e) {
                previousValue = null;
            }
            try {
                currentValue = (V) evt.getNewValue();
            }
            catch (ClassCastException e) {
                currentValue = null;
            }
            if ((previousValue != null || currentValue != null) && !Objects.equals(previousValue, currentValue) && !onChange().isEmpty()) {
                onChange().emit(new ChangeEvent<>(BeanBinding.this, previousValue, currentValue));
            }
        }
    }

    private static class AtomicInstance<V> extends Property<V> {
        private final AtomicReference<V> value;
        public AtomicInstance(V initialValue) {
            value = new AtomicReference<>(initialValue);
        }
        public AtomicInstance() {
            value = new AtomicReference<>();
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(V value) {
            V previousValue = this.value.getAndSet(value);
            if ((previousValue != null || value != null) && !Objects.equals(previousValue, value) && !onChange().isEmpty())
                onChange().emit(new ChangeEvent<>(AtomicInstance.this, previousValue, value));
        }
        public V get() {
            return value.get();
        }
    }

    private static class Instance<V> extends Property<V> {
        private V value;
        public Instance(V initialValue) {
            value = initialValue;
        }
        public Instance() {
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(V value) {
            V previousValue = this.value;
            this.value = value;
            if ((previousValue != null || value != null) && !Objects.equals(previousValue, value) && !onChange().isEmpty())
                onChange().emit(new ChangeEvent<>(Instance.this, previousValue, value));
        }
        public V get() {
            return value;
        }
    }

    private final Signal<EventSlot<ChangeEvent<V>>> onChange = Signal.ofSlot();
    public Signal<EventSlot<ChangeEvent<V>>> onChange() {
        return onChange;
    }

    public abstract void set(V value);
    public abstract V get();
    public abstract boolean isReadOnly();

    @Override
    public String toString() {
        V temp = get();
        return temp == null ? "null" : temp.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Property<?> that = (Property<?>) o;

        return Objects.equals(get(), that.get());
    }

    @Override
    public int hashCode() {
        V value = get();
        return value != null ? value.hashCode() : 0;
    }

    public static final class ChangeEvent<V> extends Event<Property<V>> {

        private final V previousValue, currentValue;

        public ChangeEvent(Property<V> source, V previousValue, V currentValue) {
            super(source);
            this.previousValue = previousValue;
            this.currentValue = currentValue;
        }

        public V getPreviousValue() {
            return previousValue;
        }

        public V getCurrentValue() {
            return currentValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            ChangeEvent<?> that = (ChangeEvent<?>) o;

            if (!Objects.equals(previousValue, that.previousValue))
                return false;
            return Objects.equals(currentValue, that.currentValue);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (previousValue != null ? previousValue.hashCode() : 0);
            result = 31 * result + (currentValue != null ? currentValue.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "previousValue=" + previousValue +
                    ", currentValue=" + currentValue +
                    '}';
        }

    }

}
