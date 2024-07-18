package unrefined.desktop.posix;

import unrefined.app.Preferences;
import unrefined.desktop.PreferencesSupport;
import unrefined.util.Half;
import unrefined.util.HierarchicalProperties;
import unrefined.util.Rational;
import unrefined.util.event.EventSlot;
import unrefined.util.signal.SignalSlot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.*;

public class PosixPreferences extends Preferences {

    private static final Map<File, Properties> propertiesMap = new HashMap<>();
    private static final Map<String, PosixPreferences> preferencesMap = new HashMap<>();

    private static Properties get(File file, File bakFile) {
        if (!propertiesMap.containsKey(file)) synchronized (propertiesMap) {
            if (!propertiesMap.containsKey(file)) {
                Properties properties = new Properties();
                try {
                    if (bakFile.exists() && bakFile.isFile()) {
                        Files.copy(bakFile.toPath(), file.toPath(), REPLACE_EXISTING);
                        Files.delete(bakFile.toPath());
                    }
                    if (file.exists() && file.isFile()) {
                        try (InputStream stream = Files.newInputStream(file.toPath(), READ)) {
                            HierarchicalProperties.loadFromIni(properties, stream, StandardCharsets.UTF_8);
                        }
                        catch (IOException ignored) {
                        }
                    }
                }
                catch (IOException ignored) {
                }
                propertiesMap.put(file, properties);
            }
        }
        return propertiesMap.get(file);
    }

    public static PosixPreferences get(File parent, String namespace, String name) {
        Objects.requireNonNull(namespace);
        Objects.requireNonNull(name);
        if (!preferencesMap.containsKey(name)) synchronized (preferencesMap) {
            if (!preferencesMap.containsKey(name)) {
                File file = new File(parent, namespace + "rc");
                File bakFile = new File(file.getAbsolutePath() + ".bak");
                preferencesMap.put(name, new PosixPreferences(file, bakFile, get(file, bakFile), name));
            }
        }
        return preferencesMap.get(name);
    }

    public static boolean delete(File parent, String namespace, String name) {
        Objects.requireNonNull(namespace);
        Objects.requireNonNull(name);
        synchronized (preferencesMap) {
            preferencesMap.remove(name);
        }
        File file = new File(parent, namespace + "rc");
        synchronized (propertiesMap) {
            Properties properties;
            boolean deleted = !HierarchicalProperties.removeTree(properties = propertiesMap.get(file), name).isEmpty();
            if (deleted) {
                File bakFile = new File(file.getAbsolutePath() + ".bak");
                synchronized (properties) {
                    return write(file, bakFile, properties);
                }
            }
            else return false;
        }
    }

    private final Properties properties;
    private final String name;
    private final File file;
    private final File bakFile;

    private PosixPreferences(File file, File bakFile, Properties properties, String name) {
        this.file = file;
        this.bakFile = bakFile;
        this.properties = properties;
        this.name = name;
    }

    public Properties getProperties() {
        return properties;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Preferences.Editor edit() {
        return new Editor(this);
    }

    private static class Editor extends Preferences.Editor {
        private final PosixPreferences preferences;
        public Editor(PosixPreferences preferences) {
            this.preferences = preferences;
        }
        private final Set<String> removed = new HashSet<>();
        private final Map<String, String> cache = new HashMap<>();
        private Editor put(String key, String value) {
            key = preferences.name + "." + Objects.requireNonNull(key);
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
            key = preferences.name + "." + Objects.requireNonNull(key);
            synchronized (this) {
                removed.add(key);
                cache.remove(key);
            }
            return this;
        }
        @Override
        public Editor clear() {
            synchronized (this) {
                removed.addAll(preferences.properties.stringPropertyNames());
                cache.clear();
            }
            return this;
        }
        private void flush() {
            synchronized (this) {
                for (String key : removed) {
                    if (preferences.properties.remove(key) != null)
                        preferences.onChange().emit(new ChangeEvent(preferences, key.substring(key.lastIndexOf('.')) + 1));
                }
                removed.clear();
                for (Map.Entry<String, String> entry : cache.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (!value.equals(preferences.properties.put(key, value)))
                        preferences.onChange().emit(new ChangeEvent(preferences, key.substring(key.lastIndexOf('.') + 1)));
                }
                cache.clear();
            }
        }
        @Override
        public boolean commit() {
            flush();
            return preferences.write();
        }
        @Override
        public void apply() {
            flush();
            PreferencesSupport.enqueueWrite(preferences::write);
        }
    }
    
    private String getProperty(String key) {
        return properties.getProperty(name + "." + Objects.requireNonNull(key));
    }

    private static boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) return true;
        else if (value.equalsIgnoreCase("false")) return false;
        else throw new ClassCastException("not a boolean");
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : parseBoolean(value);
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : Byte.parseByte(value);
    }

    private static char parseChar(String value) {
        if (value.length() != 1) throw new ClassCastException("not a char");
        else return value.charAt(0);
    }

    @Override
    public char getChar(String key, char defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : parseChar(value);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : Short.parseShort(value);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : Float.parseFloat(value);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : Double.parseDouble(value);
    }

    @Override
    public String getString(String key, String defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : value;
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : new BigInteger(value);
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : new BigDecimal(value);
    }

    @Override
    public Rational getRational(String key, Rational defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : Rational.parseRational(value);
    }

    @Override
    public short getHalf(String key, short defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : Half.parseHalf(value);
    }

    @Override
    public int size() {
        return HierarchicalProperties.treeSize(properties, name);
    }

    @Override
    public boolean contains(String key) {
        return properties.containsKey(name + "." + Objects.requireNonNull(key));
    }

    private boolean write() {
        synchronized (properties) {
            return write(file, bakFile, properties);
        }
    }

    private static boolean write(File file, File bakFile, Properties properties) {
        try {
            if (file.exists()) {
                if (createFileIfNotExists(bakFile)) Files.copy(file.toPath(), bakFile.toPath(), REPLACE_EXISTING);
                else return false;
            }
            if (createFileIfNotExists(file)) {
                try (OutputStream stream = Files.newOutputStream(file.toPath(), WRITE, TRUNCATE_EXISTING, CREATE, SYNC)) {
                    HierarchicalProperties.storeToIni(properties, stream, null, StandardCharsets.UTF_8);
                    Files.delete(bakFile.toPath());
                    return true;
                }
            }
            Files.delete(bakFile.toPath());
            return false;
        }
        catch (IOException e) {
            return false;
        }
    }

    private static boolean createFileIfNotExists(File file) {
        try {
            if (file.exists() && file.isFile()) return true;
            else {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    if (!parentFile.mkdirs()) return false;
                }
                return file.createNewFile();
            }
        }
        catch (IOException e) {
            return false;
        }
    }

}
