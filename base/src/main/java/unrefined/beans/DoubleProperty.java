package unrefined.beans;

import unrefined.util.concurrent.DoubleProducer;
import unrefined.util.concurrent.atomic.AtomicDouble;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.function.DoubleSlot;
import unrefined.util.signal.Signal;

import java.util.Objects;
import java.util.Properties;

public abstract class DoubleProperty {

    public static DoubleProperty bind(Properties properties, String key) {
        return new PropertiesBinding(properties, key);
    }

    public static DoubleProperty bind(DoubleProducer getter, DoubleSlot setter) {
        return new FunctionBinding(getter, setter);
    }

    public static DoubleProperty bind(DoubleProducer getter) {
        return new FunctionBinding(getter);
    }

    public static DoubleProperty ofAtomicInstance(double initialValue) {
        return new AtomicInstance(initialValue);
    }

    public static DoubleProperty ofAtomicInstance() {
        return new AtomicInstance();
    }

    public static DoubleProperty ofInstance(double initialValue) {
        return new Instance(initialValue);
    }

    public static DoubleProperty ofInstance() {
        return new Instance();
    }

    private static class PropertiesBinding extends DoubleProperty {
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
        public void set(double value) {
            properties.setProperty(key, Double.toString(value));
        }
        @Override
        public double get() {
            return Double.parseDouble(properties.getProperty(key));
        }
    }

    private static class FunctionBinding extends DoubleProperty {
        private final DoubleProducer getter;
        private final DoubleSlot setter;
        public FunctionBinding(DoubleProducer getter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = null;
        }
        public FunctionBinding(DoubleProducer getter, DoubleSlot setter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = setter;
        }
        @Override
        public boolean isReadOnly() {
            return setter == null;
        }
        @Override
        public void set(double value) {
            if (setter == null) throw new IllegalArgumentException("Property is read-only");
            else setter.accept(value);
        }
        @Override
        public double get() {
            return getter.get();
        }
    }

    private static class AtomicInstance extends DoubleProperty {
        private final AtomicDouble value;
        public AtomicInstance(double initialValue) {
            value = new AtomicDouble(initialValue);
        }
        public AtomicInstance() {
            value = new AtomicDouble();
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(double value) {
            double previousValue = this.value.getAndSet(value);
            if (previousValue != value && !onChanged().isEmpty()) onChanged().emit(new ChangedEvent(this, previousValue, value));
        }
        public double get() {
            return value.get();
        }
    }

    private static class Instance extends DoubleProperty {
        private double value;
        public Instance(double initialValue) {
            value = initialValue;
        }
        public Instance() {
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(double value) {
            double previousValue = this.value;
            this.value = value;
            if (previousValue != value && !onChanged().isEmpty()) onChanged().emit(new ChangedEvent(this, previousValue, value));
        }
        public double get() {
            return value;
        }
    }

    private final Signal<EventSlot<ChangedEvent>> onChanged = Signal.ofSlot();
    public Signal<EventSlot<ChangedEvent>> onChanged() {
        return onChanged;
    }

    public abstract void set(double value);
    public abstract double get();
    public abstract boolean isReadOnly();

    @Override
    public String toString() {
        return Double.toString(get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DoubleProperty that = (DoubleProperty) o;

        return Double.compare(that.get(), get()) == 0;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(get());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public static final class ChangedEvent extends Event<DoubleProperty> {

        private final double previousValue, currentValue;

        public ChangedEvent(DoubleProperty source, double previousValue, double currentValue) {
            super(source);
            this.previousValue = previousValue;
            this.currentValue = currentValue;
        }

        public double getPreviousValue() {
            return previousValue;
        }

        public double getCurrentValue() {
            return currentValue;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;

            ChangedEvent that = (ChangedEvent) object;

            if (Double.compare(previousValue, that.previousValue) != 0) return false;
            return Double.compare(currentValue, that.currentValue) == 0;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(previousValue);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(currentValue);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
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
