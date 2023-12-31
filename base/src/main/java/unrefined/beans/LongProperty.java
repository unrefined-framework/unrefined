package unrefined.beans;

import unrefined.util.concurrent.LongProducer;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.function.LongSlot;
import unrefined.util.signal.Signal;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

public abstract class LongProperty {

    public static LongProperty bind(Properties properties, String key) {
        return new PropertiesBinding(properties, key);
    }

    public static LongProperty bind(LongProducer getter, LongSlot setter) {
        return new FunctionBinding(getter, setter);
    }

    public static LongProperty bind(LongProducer getter) {
        return new FunctionBinding(getter);
    }

    public static LongProperty ofAtomic(long initialValue) {
        return new AtomicInstance(initialValue);
    }

    public static LongProperty ofAtomic() {
        return new AtomicInstance();
    }

    public static LongProperty of(long initialValue) {
        return new Instance(initialValue);
    }

    public static LongProperty ofDefault() {
        return new Instance();
    }

    private static class PropertiesBinding extends LongProperty {
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
        public void set(long value) {
            properties.setProperty(key, Long.toString(value));
        }
        @Override
        public long get() {
            return Long.parseLong(properties.getProperty(key));
        }
    }

    private static class FunctionBinding extends LongProperty {
        private final LongProducer getter;
        private final LongSlot setter;
        public FunctionBinding(LongProducer getter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = null;
        }
        public FunctionBinding(LongProducer getter, LongSlot setter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = setter;
        }
        @Override
        public boolean isReadOnly() {
            return setter == null;
        }
        @Override
        public void set(long value) {
            if (setter == null) throw new IllegalArgumentException("Property is read-only");
            else setter.accept(value);
        }
        @Override
        public long get() {
            return getter.get();
        }
    }

    private static class AtomicInstance extends LongProperty {
        private final AtomicLong value;
        public AtomicInstance(long initialValue) {
            value = new AtomicLong(initialValue);
        }
        public AtomicInstance() {
            value = new AtomicLong();
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(long value) {
            long previousValue = this.value.getAndSet(value);
            if (previousValue != value && !onChange().isEmpty()) onChange().emit(new ChangeEvent(this, previousValue, value));
        }
        public long get() {
            return value.get();
        }
    }

    private static class Instance extends LongProperty {
        private long value;
        public Instance(long initialValue) {
            value = initialValue;
        }
        public Instance() {
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(long value) {
            long previousValue = this.value;
            this.value = value;
            if (previousValue != value && !onChange().isEmpty()) onChange().emit(new ChangeEvent(this, previousValue, value));
        }
        public long get() {
            return value;
        }
    }

    private final Signal<EventSlot<ChangeEvent>> onChange = Signal.ofSlot();
    public Signal<EventSlot<ChangeEvent>> onChange() {
        return onChange;
    }

    public abstract void set(long value);
    public abstract long get();
    public abstract boolean isReadOnly();

    @Override
    public String toString() {
        return Long.toString(get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LongProperty that = (LongProperty) o;

        return get() == that.get();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp = get();
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public static final class ChangeEvent extends Event<LongProperty> {

        private final long previousValue, currentValue;

        public ChangeEvent(LongProperty source, long previousValue, long currentValue) {
            super(source);
            this.previousValue = previousValue;
            this.currentValue = currentValue;
        }

        public long getPreviousValue() {
            return previousValue;
        }

        public long getCurrentValue() {
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
            result = 31 * result + (int) (previousValue ^ (previousValue >>> 32));
            result = 31 * result + (int) (currentValue ^ (currentValue >>> 32));
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
