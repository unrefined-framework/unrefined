package unrefined.desktop.macos;

import unrefined.app.Preferences;
import unrefined.desktop.PreferencesSupport;
import unrefined.util.Half;
import unrefined.util.Rational;
import unrefined.util.UnexpectedError;
import unrefined.util.event.EventSlot;
import unrefined.util.signal.SignalSlot;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

public class MacPreferences extends Preferences implements PreferenceChangeListener {

    private static final Map<java.util.prefs.Preferences, MacPreferences> preferencesMap = new HashMap<>();

    public static MacPreferences get(String namespace, String name) {
        java.util.prefs.Preferences preferences = 
                java.util.prefs.Preferences.userRoot()
                        .node(Objects.requireNonNull(namespace))
                        .node(Objects.requireNonNull(name));
        if (!preferencesMap.containsKey(preferences)) synchronized (preferencesMap) {
            if (!preferencesMap.containsKey(preferences)) preferencesMap.put(preferences, new MacPreferences(preferences));
        }
        return preferencesMap.get(preferences);
    }

    public static boolean delete(String namespace, String name) {
        java.util.prefs.Preferences preferences =
                java.util.prefs.Preferences.userRoot()
                        .node(Objects.requireNonNull(namespace))
                        .node(Objects.requireNonNull(name));
        synchronized (preferencesMap) {
            preferencesMap.remove(preferences);
        }
        try {
            preferences.removeNode();
            return true;
        } catch (BackingStoreException e) {
            return false;
        }
    }

    private final java.util.prefs.Preferences preferences;

    private MacPreferences(java.util.prefs.Preferences preferences) {
        this.preferences = preferences;
        preferences.addPreferenceChangeListener(this);
    }

    public java.util.prefs.Preferences getPreferences() {
        return preferences;
    }

    private static class Editor extends Preferences.Editor {
        private final MacPreferences preferences;
        public Editor(MacPreferences preferences) {
            this.preferences = preferences;
        }
        private final Set<String> removed = new HashSet<>();
        private final Map<String, String> cache = new HashMap<>();
        private Editor put(String key, String value) {
            synchronized (this) {
                removed.remove(key);
                cache.put(key, value);
            }
            return this;
        }
        @Override
        public Editor onChange(SignalSlot<EventSlot<ChangeEvent>> consumer) {
            consumer.accept(preferences.onChange());
            return this;
        }
        @Override
        public Editor putBoolean(String key, boolean value) {
            return put(key, Boolean.toString(value));
        }
        @Override
        public Editor putByte(String key, byte value) {
            return put(key, Byte.toString(value));
        }
        @Override
        public Editor putChar(String key, char value) {
            return put(key, Character.toString(value));
        }
        @Override
        public Editor putShort(String key, short value) {
            return put(key, Short.toString(value));
        }
        @Override
        public Editor putInt(String key, int value) {
            return put(key, Integer.toString(value));
        }
        @Override
        public Editor putLong(String key, long value) {
            return put(key, Long.toString(value));
        }
        @Override
        public Editor putFloat(String key, float value) {
            return put(key, Float.toString(value));
        }
        @Override
        public Editor putDouble(String key, double value) {
            return put(key, Double.toString(value));
        }
        @Override
        public Editor putString(String key, String value) {
            return put(key, value);
        }
        @Override
        public Editor putBigInteger(String key, BigInteger value) {
            return put(key, value.toString());
        }
        @Override
        public Editor putBigDecimal(String key, BigDecimal value) {
            return put(key, value.toEngineeringString());
        }
        @Override
        public Editor putRational(String key, Rational value) {
            return put(key, value.toString());
        }
        @Override
        public Editor putHalf(String key, short value) {
            return put(key, Half.toString(value));
        }
        @Override
        public Editor remove(String key) {
            synchronized (this) {
                removed.add(key);
                cache.remove(key);
            }
            return this;
        }
        @Override
        public Editor clear() {
            synchronized (this) {
                try {
                    removed.addAll(Arrays.asList(preferences.preferences.keys()));
                    cache.clear();
                } catch (BackingStoreException e) {
                    throw new UnexpectedError(e);
                }
            }
            return this;
        }
        private void flush() {
            synchronized (this) {
                for (String key : removed) {
                    if (preferences.preferences.get(key, null) != null) {
                        preferences.preferences.remove(key);
                        preferences.onChange().emit(new ChangeEvent(preferences, key));
                    }
                }
                removed.clear();
                for (Map.Entry<String, String> entry : cache.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (!value.equals(preferences.preferences.get(key, null))) {
                        preferences.preferences.put(key, value);
                        preferences.onChange().emit(new ChangeEvent(preferences, key));
                    }
                }
                cache.clear();
            }
        }
        @Override
        public boolean commit() {
            flush();
            return preferences.sync();
        }
        @Override
        public void apply() {
            PreferencesSupport.enqueueWrite(this::flush);
        }
    }

    private boolean sync() {
        try {
            preferences.sync();
            return true;
        } catch (BackingStoreException e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return preferences.name();
    }

    @Override
    public Preferences.Editor edit() {
        return new Editor(this);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        return (byte) preferences.getInt(key, defaultValue);
    }

    @Override
    public char getChar(String key, char defaultValue) {
        return (char) preferences.getInt(key, defaultValue);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        return (short) preferences.getInt(key, defaultValue);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return preferences.getDouble(key, defaultValue);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return preferences.get(key, defaultValue);
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        String value = preferences.get(key, null);
        return value == null ? defaultValue : new BigInteger(value);
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        String value = preferences.get(key, null);
        return value == null ? defaultValue : new BigDecimal(value);
    }

    @Override
    public Rational getRational(String key, Rational defaultValue) {
        String value = preferences.get(key, null);
        return value == null ? defaultValue : Rational.parseRational(value);
    }

    @Override
    public short getHalf(String key, short defaultValue) {
        return Half.toHalf(preferences.getFloat(key, defaultValue));
    }

    @Override
    public int size() {
        try {
            return preferences.keys().length;
        } catch (BackingStoreException e) {
            throw new UnexpectedError(e);
        }
    }

    @Override
    public boolean contains(String key) {
        return preferences.get(key, null) != null;
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent event) {
        onChange().emit(new Preferences.ChangeEvent(this, event.getKey()));
    }

}
