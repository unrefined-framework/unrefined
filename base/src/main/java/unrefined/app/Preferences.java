package unrefined.app;

import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.function.Slot;
import unrefined.util.signal.Signal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public abstract class Preferences {

    private final Signal<EventSlot<ChangedEvent>> onChanged = Signal.ofSlot();
    public Signal<EventSlot<ChangedEvent>> onChanged() {
        return onChanged;
    }

    public abstract String getName();

    public static abstract class Editor {

        public abstract Editor onChanged(Slot<Signal<EventSlot<ChangedEvent>>> consumer);

        public abstract Editor putBoolean(String key, boolean value);
        public abstract Editor putByte(String key, byte value);
        public abstract Editor putCharacter(String key, char value);
        public abstract Editor putShort(String key, short value);
        public abstract Editor putInteger(String key, int value);
        public abstract Editor putLong(String key, long value);
        public abstract Editor putFloat(String key, float value);
        public abstract Editor putDouble(String key, double value);
        public abstract Editor putString(String key, String value);
        public abstract Editor putBigInteger(String key, BigInteger value);
        public abstract Editor putBigDecimal(String key, BigDecimal value);

        public abstract Editor remove(String key);
        public abstract Editor clear();

        public abstract boolean commit();
        public abstract void apply();

    }

    public abstract Editor edit();

    public abstract boolean getBoolean(String key, boolean defaultValue);
    public abstract byte getByte(String key, byte defaultValue);
    public abstract char getCharacter(String key, char defaultValue);
    public abstract short getShort(String key, short defaultValue);
    public abstract int getInteger(String key, int defaultValue);
    public abstract long getLong(String key, long defaultValue);
    public abstract float getFloat(String key, float defaultValue);
    public abstract double getDouble(String key, double defaultValue);
    public abstract String getString(String key, String defaultValue);
    public abstract BigInteger getBigInteger(String key, BigInteger defaultValue);
    public abstract BigDecimal getBigDecimal(String key, BigDecimal defaultValue);

    public abstract int size();
    public abstract boolean contains(String key);

    public static final class ChangedEvent extends Event<Preferences> {

        private final String key;

        public ChangedEvent(Preferences source, String key) {
            super(source);
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;

            ChangedEvent that = (ChangedEvent) object;

            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return key != null ? key.hashCode() : 0;
        }

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "key='" + key + '\'' +
                    '}';
        }

    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                '<' + getName() + '>' +
                '}';
    }
    
}
