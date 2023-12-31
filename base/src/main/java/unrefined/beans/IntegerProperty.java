package unrefined.beans;

import unrefined.util.concurrent.IntProducer;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.function.IntSlot;
import unrefined.util.signal.Signal;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class IntegerProperty {

    public static IntegerProperty bind(Properties properties, String key) {
        return new PropertiesBinding(properties, key);
    }

    public static IntegerProperty bind(IntProducer getter, IntSlot setter) {
        return new FunctionBinding(getter, setter);
    }

    public static IntegerProperty bind(IntProducer getter) {
        return new FunctionBinding(getter);
    }

    public static IntegerProperty ofAtomic(int initialValue) {
        return new AtomicInstance(initialValue);
    }

    public static IntegerProperty ofAtomic() {
        return new AtomicInstance();
    }

    public static IntegerProperty of(int initialValue) {
        return new Instance(initialValue);
    }

    public static IntegerProperty ofDefault() {
        return new Instance();
    }

    private static class PropertiesBinding extends IntegerProperty {
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
        public void set(int value) {
            properties.setProperty(key, Integer.toString(value));
        }
        @Override
        public int get() {
            return Integer.parseInt(properties.getProperty(key));
        }
    }

    private static class FunctionBinding extends IntegerProperty {
        private final IntProducer getter;
        private final IntSlot setter;
        public FunctionBinding(IntProducer getter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = null;
        }
        public FunctionBinding(IntProducer getter, IntSlot setter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = setter;
        }
        @Override
        public boolean isReadOnly() {
            return setter == null;
        }
        @Override
        public void set(int value) {
            if (setter == null) throw new IllegalArgumentException("Property is read-only");
            else setter.accept(value);
        }
        @Override
        public int get() {
            return getter.get();
        }
    }

    private static class AtomicInstance extends IntegerProperty {
        private final AtomicInteger value;
        public AtomicInstance(int initialValue) {
            value = new AtomicInteger(initialValue);
        }
        public AtomicInstance() {
            value = new AtomicInteger();
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(int value) {
            int previousValue = this.value.getAndSet(value);
            if (previousValue != value && !onChange().isEmpty()) onChange().emit(new ChangeEvent(this, previousValue, value));
        }
        public int get() {
            return value.get();
        }
    }

    private static class Instance extends IntegerProperty {
        private int value;
        public Instance(int initialValue) {
            value = initialValue;
        }
        public Instance() {
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(int value) {
            int previousValue = this.value;
            this.value = value;
            if (previousValue != value && !onChange().isEmpty()) onChange().emit(new ChangeEvent(this, previousValue, value));
        }
        public int get() {
            return value;
        }
    }

    private final Signal<EventSlot<ChangeEvent>> onChange = Signal.ofSlot();
    public Signal<EventSlot<ChangeEvent>> onChange() {
        return onChange;
    }

    public abstract void set(int value);
    public abstract int get();
    public abstract boolean isReadOnly();

    @Override
    public String toString() {
        return Integer.toString(get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntegerProperty that = (IntegerProperty) o;

        return get() == that.get();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + get();
        return result;
    }

    public static final class ChangeEvent extends Event<IntegerProperty> {

        private final int previousValue, currentValue;

        public ChangeEvent(IntegerProperty source, int previousValue, int currentValue) {
            super(source);
            this.previousValue = previousValue;
            this.currentValue = currentValue;
        }

        public int getPreviousValue() {
            return previousValue;
        }

        public int getCurrentValue() {
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
            result = 31 * result + previousValue;
            result = 31 * result + currentValue;
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
