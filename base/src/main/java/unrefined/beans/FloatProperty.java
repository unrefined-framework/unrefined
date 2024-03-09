package unrefined.beans;

import unrefined.util.concurrent.FloatProducer;
import unrefined.util.concurrent.atomic.AtomicFloat;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.function.FloatSlot;
import unrefined.util.signal.Signal;

import java.util.Objects;
import java.util.Properties;

public abstract class FloatProperty {

    public static FloatProperty bind(Properties properties, String key) {
        return new PropertiesBinding(properties, key);
    }

    public static FloatProperty bind(FloatProducer getter, FloatSlot setter) {
        return new FunctionBinding(getter, setter);
    }

    public static FloatProperty bind(FloatProducer getter) {
        return new FunctionBinding(getter);
    }

    public static FloatProperty ofAtomic(float initialValue) {
        return new AtomicInstance(initialValue);
    }

    public static FloatProperty ofAtomic() {
        return new AtomicInstance();
    }

    public static FloatProperty of(float initialValue) {
        return new Instance(initialValue);
    }

    public static FloatProperty of() {
        return new Instance();
    }

    private static class PropertiesBinding extends FloatProperty {
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
        public void set(float value) {
            properties.setProperty(key, Float.toString(value));
        }
        @Override
        public float get() {
            return Float.parseFloat(properties.getProperty(key));
        }
    }

    private static class FunctionBinding extends FloatProperty {
        private final FloatProducer getter;
        private final FloatSlot setter;
        public FunctionBinding(FloatProducer getter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = null;
        }
        public FunctionBinding(FloatProducer getter, FloatSlot setter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = setter;
        }
        @Override
        public boolean isReadOnly() {
            return setter == null;
        }
        @Override
        public void set(float value) {
            if (setter == null) throw new IllegalArgumentException("Property is read-only");
            else setter.accept(value);
        }
        @Override
        public float get() {
            return getter.getAsFloat();
        }
    }

    private static class AtomicInstance extends FloatProperty {
        private final AtomicFloat value;
        public AtomicInstance(float initialValue) {
            value = new AtomicFloat(initialValue);
        }
        public AtomicInstance() {
            value = new AtomicFloat();
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(float value) {
            float previousValue = this.value.getAndSet(value);
            if (previousValue != value && !onChange().isEmpty()) onChange().emit(new ChangeEvent(this, previousValue, value));
        }
        public float get() {
            return value.get();
        }
    }

    private static class Instance extends FloatProperty {
        private float value;
        public Instance(float initialValue) {
            value = initialValue;
        }
        public Instance() {
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(float value) {
            float previousValue = this.value;
            this.value = value;
            if (previousValue != value && !onChange().isEmpty()) onChange().emit(new ChangeEvent(this, previousValue, value));
        }
        public float get() {
            return value;
        }
    }

    private final Signal<EventSlot<ChangeEvent>> onChange = Signal.ofSlot();
    public Signal<EventSlot<ChangeEvent>> onChange() {
        return onChange;
    }

    public abstract void set(float value);
    public abstract float get();
    public abstract boolean isReadOnly();

    @Override
    public String toString() {
        return Float.toString(get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FloatProperty that = (FloatProperty) o;

        return Float.compare(that.get(), get()) == 0;
    }

    @Override
    public int hashCode() {
        float temp = get();
        return (temp != +0.0f ? Float.floatToIntBits(temp) : 0);
    }

    public static final class ChangeEvent extends Event<FloatProperty> {

        private final float previousValue, currentValue;

        public ChangeEvent(FloatProperty source, float previousValue, float currentValue) {
            super(source);
            this.previousValue = previousValue;
            this.currentValue = currentValue;
        }

        public float getPreviousValue() {
            return previousValue;
        }

        public float getCurrentValue() {
            return currentValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            ChangeEvent that = (ChangeEvent) o;

            if (Float.compare(previousValue, that.previousValue) != 0) return false;
            return Float.compare(currentValue, that.currentValue) == 0;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (previousValue != 0.0f ? Float.floatToIntBits(previousValue) : 0);
            result = 31 * result + (currentValue != 0.0f ? Float.floatToIntBits(currentValue) : 0);
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
