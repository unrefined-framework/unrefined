package unrefined.beans;

import unrefined.util.concurrent.ShortProducer;
import unrefined.util.concurrent.atomic.AtomicShort;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.function.ShortSlot;
import unrefined.util.signal.Signal;

import java.util.Objects;
import java.util.Properties;

public abstract class ShortProperty {

    public static ShortProperty bind(Properties properties, String key) {
        return new PropertiesBinding(properties, key);
    }

    public static ShortProperty bind(ShortProducer getter, ShortSlot setter) {
        return new FunctionBinding(getter, setter);
    }

    public static ShortProperty bind(ShortProducer getter) {
        return new FunctionBinding(getter);
    }

    public static ShortProperty ofAtomic(short initialValue) {
        return new AtomicInstance(initialValue);
    }

    public static ShortProperty ofAtomic() {
        return new AtomicInstance();
    }

    public static ShortProperty of(short initialValue) {
        return new Instance(initialValue);
    }

    public static ShortProperty of() {
        return new Instance();
    }

    private static class PropertiesBinding extends ShortProperty {
        private final Properties properties;
        private final String key;
        public PropertiesBinding(Properties properties, String key) {
            this.properties = Objects.requireNonNull(properties);
            this.key = Objects.requireNonNull(key);
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        @Override
        public void set(short value) {
            properties.setProperty(key, Short.toString(value));
        }
        @Override
        public short get() {
            return Short.parseShort(properties.getProperty(key));
        }
    }

    private static class FunctionBinding extends ShortProperty {
        private final ShortProducer getter;
        private final ShortSlot setter;
        public FunctionBinding(ShortProducer getter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = null;
        }
        public FunctionBinding(ShortProducer getter, ShortSlot setter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = setter;
        }
        @Override
        public boolean isReadOnly() {
            return setter == null;
        }
        @Override
        public void set(short value) {
            if (setter == null) throw new IllegalArgumentException("Property is read-only");
            else setter.accept(value);
        }
        @Override
        public short get() {
            return getter.getAsShort();
        }
    }

    private static class AtomicInstance extends ShortProperty {
        private final AtomicShort value;
        public AtomicInstance(short initialValue) {
            value = new AtomicShort(initialValue);
        }
        public AtomicInstance() {
            value = new AtomicShort();
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(short value) {
            short previousValue = this.value.getAndSet(value);
            if (previousValue != value && !onChange().isEmpty()) onChange().emit(new ChangeEvent(this, previousValue, value));
        }
        public short get() {
            return value.get();
        }
    }

    private static class Instance extends ShortProperty {
        private short value;
        public Instance(short initialValue) {
            value = initialValue;
        }
        public Instance() {
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(short value) {
            short previousValue = this.value;
            this.value = value;
            if (previousValue != value && !onChange().isEmpty()) onChange().emit(new ChangeEvent(this, previousValue, value));
        }
        public short get() {
            return value;
        }
    }

    private final Signal<EventSlot<ChangeEvent>> onChange = Signal.ofSlot();
    public Signal<EventSlot<ChangeEvent>> onChange() {
        return onChange;
    }

    public abstract void set(short value);
    public abstract short get();
    public abstract boolean isReadOnly();

    @Override
    public String toString() {
        return Short.toString(get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShortProperty that = (ShortProperty) o;

        return get() == that.get();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) get();
        return result;
    }

    public static final class ChangeEvent extends Event<ShortProperty> {

        private final short previousValue, currentValue;

        public ChangeEvent(ShortProperty source, short previousValue, short currentValue) {
            super(source);
            this.previousValue = previousValue;
            this.currentValue = currentValue;
        }

        public short getPreviousValue() {
            return previousValue;
        }

        public short getCurrentValue() {
            return currentValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            ChangeEvent that = (ChangeEvent) o;

            if (previousValue != that.previousValue) return false;
            return currentValue == that.currentValue;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (int) previousValue;
            result = 31 * result + (int) currentValue;
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
