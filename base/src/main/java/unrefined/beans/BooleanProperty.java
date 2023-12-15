package unrefined.beans;

import unrefined.util.concurrent.BooleanProducer;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.function.BooleanSlot;
import unrefined.util.signal.Signal;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BooleanProperty {

    public static BooleanProperty bind(Properties properties, String key) {
        return new PropertiesBinding(properties, key);
    }

    public static BooleanProperty bind(BooleanProducer getter, BooleanSlot setter) {
        return new FunctionBinding(getter, setter);
    }

    public static BooleanProperty bind(BooleanProducer getter) {
        return new FunctionBinding(getter);
    }

    public static BooleanProperty ofAtomicInstance(boolean initialValue) {
        return new AtomicInstance(initialValue);
    }

    public static BooleanProperty ofAtomicInstance() {
        return new AtomicInstance();
    }

    public static BooleanProperty ofInstance(boolean initialValue) {
        return new Instance(initialValue);
    }

    public static BooleanProperty ofInstance() {
        return new Instance();
    }

    private static class PropertiesBinding extends BooleanProperty {
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
        public void set(boolean value) {
            properties.setProperty(key, Boolean.toString(value));
        }
        @Override
        public boolean get() {
            return Boolean.parseBoolean(properties.getProperty(key));
        }
    }

    private static class FunctionBinding extends BooleanProperty {
        private final BooleanProducer getter;
        private final BooleanSlot setter;
        public FunctionBinding(BooleanProducer getter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = null;
        }
        public FunctionBinding(BooleanProducer getter, BooleanSlot setter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = setter;
        }
        @Override
        public boolean isReadOnly() {
            return setter == null;
        }
        @Override
        public void set(boolean value) {
            if (setter == null) throw new IllegalArgumentException("Property is read-only");
            else setter.accept(value);
        }
        @Override
        public boolean get() {
            return getter.get();
        }
    }

    private static class AtomicInstance extends BooleanProperty {
        private final AtomicBoolean value;
        public AtomicInstance(boolean initialValue) {
            value = new AtomicBoolean(initialValue);
        }
        public AtomicInstance() {
            value = new AtomicBoolean();
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(boolean value) {
            boolean previousValue = this.value.getAndSet(value);
            if (previousValue != value && !onChanged().isEmpty()) onChanged().emit(new ChangedEvent(this, previousValue, value));
        }
        public boolean get() {
            return value.get();
        }
    }

    private static class Instance extends BooleanProperty {
        private boolean value;
        public Instance(boolean initialValue) {
            value = initialValue;
        }
        public Instance() {
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(boolean value) {
            boolean previousValue = this.value;
            this.value = value;
            if (previousValue != value && !onChanged().isEmpty()) onChanged().emit(new ChangedEvent(this, previousValue, value));
        }
        public boolean get() {
            return value;
        }
    }

    private final Signal<EventSlot<ChangedEvent>> onChanged = Signal.ofSlot();
    public Signal<EventSlot<ChangedEvent>> onChanged() {
        return onChanged;
    }

    public abstract void set(boolean value);
    public abstract boolean get();
    public abstract boolean isReadOnly();

    @Override
    public String toString() {
        return Boolean.toString(get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BooleanProperty that = (BooleanProperty) o;

        return get() == that.get();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (get() ? 1 : 0);
        return result;
    }

    public static final class ChangedEvent extends Event<BooleanProperty> {

        private final boolean previousValue, currentValue;

        public ChangedEvent(BooleanProperty source, boolean previousValue, boolean currentValue) {
            super(source);
            this.previousValue = previousValue;
            this.currentValue = currentValue;
        }

        public boolean getPreviousValue() {
            return previousValue;
        }

        public boolean getCurrentValue() {
            return currentValue;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;

            ChangedEvent that = (ChangedEvent) object;

            if (previousValue != that.previousValue) return false;
            return currentValue == that.currentValue;
        }

        @Override
        public int hashCode() {
            int result = (previousValue ? 1 : 0);
            result = 31 * result + (currentValue ? 1 : 0);
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
