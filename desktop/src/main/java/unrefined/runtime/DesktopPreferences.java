package unrefined.runtime;

import unrefined.app.Preferences;
import unrefined.internal.FileUtils;
import unrefined.util.event.EventSlot;
import unrefined.util.function.Slot;
import unrefined.util.signal.Signal;

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
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.SYNC;

public class DesktopPreferences extends Preferences {

    private final Properties properties = new Properties();

    private final Map<Object, Object> cache = new HashMap<>();
    private final Set<Object> removed = new HashSet<>();

    private final String name;
    private final File file;
    private final File bakFile;
    private final Object fileLock = new Object();
    private final Object cacheLock = new Object();

    private final Signal<Runnable> onApply = Signal.ofRunnable();
    private static final BaseDispatcher DISPATCHER = new BaseDispatcher("UXGL Desktop Preferences");
    static {
        DISPATCHER.start();
    }

    private final Editor editor;

    public DesktopPreferences(File parent, String name) {
        Objects.requireNonNull(name);
        onApply.connect(this::write, DISPATCHER);
        this.file = new File(parent, name + ".xml");
        String fileName = file.getName();
        this.name = fileName.substring(0, fileName.lastIndexOf('.'));
        this.bakFile = new File(file.getAbsolutePath() + ".bak");
        synchronized (fileLock) {
            try {
                if (bakFile.exists() && bakFile.isFile()) {
                    Files.copy(bakFile.toPath(), file.toPath(), REPLACE_EXISTING);
                    Files.delete(bakFile.toPath());
                }
                if (file.exists() && file.isFile()) {
                    try (InputStream stream = Files.newInputStream(file.toPath(), READ)) {
                        properties.load(stream);
                    }
                    catch (IOException ignored) {
                    }
                }
            }
            catch (IOException ignored) {
            }
        }
        editor = new Editor();
    }

    public Properties getProperties() {
        return properties;
    }

    public Map<Object, Object> getCache() {
        return cache;
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
        return editor;
    }

    private class Editor extends Preferences.Editor {
        private Editor put(String key, String value) {
            synchronized (cacheLock) {
                if (!value.equals(cache.put(key, value)) && !DesktopPreferences.this.onChanged().isEmpty())
                    DesktopPreferences.this.onChanged().emit(new ChangedEvent(DesktopPreferences.this, key));
            }
            return this;
        }
        @Override
        public Editor onChanged(Slot<Signal<EventSlot<ChangedEvent>>> consumer) {
            consumer.accept(DesktopPreferences.this.onChanged());
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
        public Editor putCharacter(String key, char value) {
            return put(key, Character.toString(value));
        }
        @Override
        public Editor putShort(String key, short value) {
            return put(key, Short.toString(value));
        }
        @Override
        public Editor putInteger(String key, int value) {
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
        public Editor remove(String key) {
            synchronized (cacheLock) {
                cache.remove(key);
                removed.add(key);
            }
            return this;
        }
        @Override
        public Editor clear() {
            synchronized (cacheLock) {
                removed.addAll(properties.keySet());
                cache.clear();
            }
            return this;
        }
        @Override
        public boolean commit() {
            flush();
            return write();
        }
        @Override
        public void apply() {
            flush();
            onApply.emit();
        }
    }

    private static boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) return true;
        else if (value.equalsIgnoreCase("false")) return false;
        else throw new ClassCastException("not a boolean");
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : parseBoolean(value);
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : Byte.parseByte(value);
    }

    private static char parseChar(String value) {
        if (value.length() != 1) throw new ClassCastException("not a char");
        else return value.charAt(0);
    }

    @Override
    public char getCharacter(String key, char defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : parseChar(value);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : Short.parseShort(value);
    }

    @Override
    public int getInteger(String key, int defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : Float.parseFloat(value);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : Double.parseDouble(value);
    }

    @Override
    public String getString(String key, String defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : value;
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : new BigInteger(value);
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : new BigDecimal(value);
    }

    @Override
    public int size() {
        return properties.size();
    }

    @Override
    public boolean contains(String key) {
        return properties.containsKey(key);
    }

    private void flush() {
        synchronized (cacheLock) {
            removed.removeAll(cache.keySet());
            properties.putAll(cache);
            cache.clear();
            properties.keySet().removeAll(removed);
            removed.clear();
        }
    }

    private boolean write() {
        synchronized (fileLock) {
            try {
                if (file.exists()) {
                    if (FileUtils.createFileIfNotExists(bakFile)) Files.copy(file.toPath(), bakFile.toPath(), REPLACE_EXISTING);
                    else return false;
                }
                if (FileUtils.createFileIfNotExists(file)) {
                    try (OutputStream stream = Files.newOutputStream(file.toPath(), WRITE, CREATE, SYNC)) {
                        properties.storeToXML(stream, null, StandardCharsets.UTF_8);
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
    }

}
