package unrefined.beans;

import unrefined.util.concurrent.CharProducer;
import unrefined.util.concurrent.atomic.AtomicCharacter;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.function.CharSlot;
import unrefined.util.signal.Signal;

import java.util.Objects;
import java.util.Properties;

public abstract class CharacterProperty {

    public static CharacterProperty bind(Properties properties, String key) {
        return new PropertiesBinding(properties, key);
    }

    public static CharacterProperty bind(CharProducer getter, CharSlot setter) {
        return new FunctionBinding(getter, setter);
    }

    public static CharacterProperty bind(CharProducer getter) {
        return new FunctionBinding(getter);
    }

    public static CharacterProperty ofAtomic(char initialValue) {
        return new AtomicInstance(initialValue);
    }

    public static CharacterProperty ofAtomic() {
        return new AtomicInstance();
    }

    public static CharacterProperty of(char initialValue) {
        return new Instance(initialValue);
    }

    public static CharacterProperty ofDefault() {
        return new Instance();
    }

    private static class PropertiesBinding extends CharacterProperty {
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
        public void set(char value) {
            properties.setProperty(key, Character.toString(value));
        }
        @Override
        public char get() {
            String value = properties.getProperty(key);
            if (value.length() != 1) throw new StringIndexOutOfBoundsException();
            return value.charAt(0);
        }
    }

    private static class FunctionBinding extends CharacterProperty {
        private final CharProducer getter;
        private final CharSlot setter;
        public FunctionBinding(CharProducer getter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = null;
        }
        public FunctionBinding(CharProducer getter, CharSlot setter) {
            this.getter = Objects.requireNonNull(getter);
            this.setter = setter;
        }
        @Override
        public boolean isReadOnly() {
            return setter == null;
        }
        @Override
        public void set(char value) {
            if (setter == null) throw new IllegalArgumentException("Property is read-only");
            else setter.accept(value);
        }
        @Override
        public char get() {
            return getter.get();
        }
    }

    private static class AtomicInstance extends CharacterProperty {
        private final AtomicCharacter value;
        public AtomicInstance(char initialValue) {
            value = new AtomicCharacter(initialValue);
        }
        public AtomicInstance() {
            value = new AtomicCharacter();
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(char value) {
            char previousValue = this.value.getAndSet(value);
            if (previousValue != value && !onChange().isEmpty()) onChange().emit(new ChangeEvent(this, previousValue, value));
        }
        public char get() {
            return value.get();
        }
    }

    private static class Instance extends CharacterProperty {
        private char value;
        public Instance(char initialValue) {
            value = initialValue;
        }
        public Instance() {
        }
        @Override
        public boolean isReadOnly() {
            return false;
        }
        public void set(char value) {
            char previousValue = this.value;
            this.value = value;
            if (previousValue != value && !onChange().isEmpty()) onChange().emit(new ChangeEvent(this, previousValue, value));
        }
        public char get() {
            return value;
        }
    }

    private final Signal<EventSlot<ChangeEvent>> onChange = Signal.ofSlot();
    public Signal<EventSlot<ChangeEvent>> onChange() {
        return onChange;
    }

    public abstract void set(char value);
    public abstract char get();
    public abstract boolean isReadOnly();

    @Override
    public String toString() {
        return Character.toString(get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CharacterProperty that = (CharacterProperty) o;

        return get() == that.get();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) get();
        return result;
    }

    public static final class ChangeEvent extends Event<CharacterProperty> {

        private final char previousValue, currentValue;

        public ChangeEvent(CharacterProperty source, char previousValue, char currentValue) {
            super(source);
            this.previousValue = previousValue;
            this.currentValue = currentValue;
        }

        public char getPreviousValue() {
            return previousValue;
        }

        public char getCurrentValue() {
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
