package unrefined.desktop.windows;

import com.kenai.jffi.Type;
import unrefined.app.Preferences;
import unrefined.desktop.ForeignSupport;
import unrefined.desktop.OSInfo;
import unrefined.desktop.PreferencesSupport;
import unrefined.desktop.ShutdownHook;
import unrefined.util.Half;
import unrefined.util.Pair;
import unrefined.util.Rational;
import unrefined.util.UnexpectedError;
import unrefined.util.event.EventSlot;
import unrefined.util.foreign.LastErrorException;
import unrefined.util.signal.SignalSlot;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static unrefined.desktop.ForeignSupport.MEMORY_IO;
import static unrefined.desktop.UnsafeSupport.UNSAFE;
import static unrefined.desktop.windows.WindowsRegistry.*;

public class WindowsPreferences extends Preferences {

    private static final Map<String, WindowsPreferences> preferencesMap = new HashMap<>();

    public static WindowsPreferences get(String namespace, String name) {
        String key = Objects.requireNonNull(namespace) + "\\" + Objects.requireNonNull(name);
        String hashKey = name.toLowerCase(Locale.ENGLISH);
        if (!preferencesMap.containsKey(hashKey)) synchronized (preferencesMap) {
            if (!preferencesMap.containsKey(hashKey)) preferencesMap.put(hashKey, new WindowsPreferences(openHKey(key), name));
        }
        return preferencesMap.get(hashKey);
    }

    public static boolean delete(String namespace, String name) {
        String key = Objects.requireNonNull(namespace) + "\\" + Objects.requireNonNull(name);
        String hashKey = name.toLowerCase(Locale.ENGLISH);
        synchronized (preferencesMap) {
            WindowsPreferences preferences = preferencesMap.remove(hashKey);
            if (preferences != null) WindowsRegistry.RegCloseKey(preferences.hKey);
            long lpSubKey = ForeignSupport.allocateWideCharString(key);
            try {
                int code = WindowsRegistry.RegDeleteKeyW(HKEY_CURRENT_USER, lpSubKey);
                return code == 0;
            }
            finally {
                UNSAFE.freeMemory(lpSubKey);
            }
        }
    }

    static {
        ShutdownHook.register(() -> {
            synchronized (preferencesMap) {
                for (WindowsPreferences preferences : preferencesMap.values()) {
                    WindowsRegistry.RegCloseKey(preferences.hKey);
                }
            }
        });
    }

    private static long openHKey(String name) {
        long lpSubKey = ForeignSupport.allocateWideCharString(name);
        long phkResult = UNSAFE.allocateMemory(Type.POINTER.size());
        try {
            int errno;
            if ((errno = WindowsRegistry.RegCreateKeyExW(HKEY_CURRENT_USER, lpSubKey, 0, 0,
                    0 /* REG_OPTION_NON_VOLATILE */, KEY_QUERY_VALUE | KEY_SET_VALUE,
                    0, phkResult, 0)) == 0) {
                return MEMORY_IO.getAddress(phkResult);
            }
            else throw new UnexpectedError(new LastErrorException(errno));
        }
        finally {
            UNSAFE.freeMemory(lpSubKey);
            UNSAFE.freeMemory(phkResult);
        }
    }

    private final String name;
    private final long hKey;

    private WindowsPreferences(long hKey, String name) {
        this.hKey = hKey;
        this.name = name;
    }

    public long getHKey() {
        return hKey;
    }

    @Override
    public String getName() {
        return name;
    }

    private static class Editor extends Preferences.Editor {
        private final WindowsPreferences preferences;
        public Editor(WindowsPreferences preferences) {
            this.preferences = preferences;
        }
        private final Set<String> removed = new HashSet<>();
        private final Map<String, Map.Entry<String, String>> cache = new HashMap<>();
        private String valueOf(Map.Entry<String, String> pair) {
            return pair == null ? null : pair.getValue();
        }
        private Editor put(String key, String value) {
            synchronized (this) {
                String hashKey = key.toLowerCase(Locale.ENGLISH);
                removed.remove(hashKey);
                cache.put(hashKey, Pair.ofImmutable(key, value));
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
                String hashKey = key.toLowerCase(Locale.ENGLISH);
                removed.add(hashKey);
                cache.remove(hashKey);
            }
            return this;
        }
        @Override
        public Editor clear() {
            synchronized (this) {
                try {
                    preferences.getValueNames(removed);
                }
                catch (LastErrorException e) {
                    throw new UnexpectedError(e);
                }
                cache.clear();
            }
            return this;
        }
        private void flush() {
            synchronized (this) {
                for (String key : removed) {
                    long lpValueName = ForeignSupport.allocateWideCharString(key);
                    try {
                        if (WindowsRegistry.RegQueryValueExW(preferences.hKey, lpValueName, 0, 0, 0, 0) == 0) {
                            int errno;
                            if ((errno = WindowsRegistry.RegDeleteValueW(preferences.hKey, lpValueName)) != 0) {
                                throw new UnexpectedError(new LastErrorException(errno));
                            }
                            preferences.onChange().emit(new ChangeEvent(preferences, key));
                        }
                    }
                    finally {
                        UNSAFE.freeMemory(lpValueName);
                    }
                }
                removed.clear();
                for (Map.Entry<String, Map.Entry<String, String>> entry : cache.entrySet()) {
                    String hashKey = entry.getKey();
                    String key = entry.getValue().getKey();
                    String value = entry.getValue().getValue();
                    long lpValueName = ForeignSupport.allocateWideCharString(hashKey);
                    long lpType = UNSAFE.allocateMemory(Type.UINT32.size());
                    long lpcbData = UNSAFE.allocateMemory(Type.UINT32.size());
                    try {
                        boolean changed = true;
                        if (WindowsRegistry.RegQueryValueExW(preferences.hKey, lpValueName, 0, lpType, 0, lpcbData) == 0) {
                            if (MEMORY_IO.getInt(lpType) == 0x00000001 /* REG_SZ */) {
                                int cbData = MEMORY_IO.getInt(lpcbData);
                                if (cbData < OSInfo.WIDE_CHAR_SIZE) changed = !value.isEmpty();
                                else {
                                    long lpData = UNSAFE.allocateMemory(cbData + OSInfo.WIDE_CHAR_SIZE /* for terminator */);
                                    try {
                                        UNSAFE.setMemory(lpData + cbData, OSInfo.WIDE_CHAR_SIZE, (byte) '\0');
                                        if (WindowsRegistry.RegQueryValueExW(preferences.hKey, lpValueName, 0, lpType, lpData, lpcbData) == 0) {
                                            changed = !value.equals(ForeignSupport.getZeroTerminatedString(lpData, OSInfo.WIDE_CHARSET));
                                        }
                                    }
                                    finally {
                                        UNSAFE.freeMemory(lpData);
                                    }
                                }
                            }
                        }
                        if (changed) {
                            int cbData = (value.length() + 1) * OSInfo.WIDE_CHAR_SIZE;
                            long lpData = UNSAFE.allocateMemory(cbData);
                            byte[] array = value.getBytes(OSInfo.WIDE_CHARSET);
                            MEMORY_IO.putByteArray(lpData, array, 0, array.length);
                            UNSAFE.setMemory(lpData + array.length, OSInfo.WIDE_CHAR_SIZE, (byte) '\0');
                            try {
                                int errno;
                                if ((errno = WindowsRegistry.RegSetValueExW(preferences.hKey, lpValueName, 0, 0x00000001 /* REG_SZ */, lpData, cbData)) != 0) {
                                    throw new UnexpectedError(new LastErrorException(errno));
                                }
                                preferences.onChange().emit(new ChangeEvent(preferences, key));
                            }
                            finally {
                                UNSAFE.freeMemory(lpData);
                            }
                        }
                    }
                    finally {
                        UNSAFE.freeMemory(lpValueName);
                        UNSAFE.freeMemory(lpType);
                        UNSAFE.freeMemory(lpcbData);
                    }
                }
            }
        }
        @Override
        public boolean commit() {
            flush();
            return preferences.flush();
        }
        @Override
        public void apply() {
            PreferencesSupport.enqueueWrite(this::flush);
        }
    }

    @Override
    public Preferences.Editor edit() {
        return new Editor(this);
    }

    private boolean flush() {
        return WindowsRegistry.RegFlushKey(hKey) == 0;
    }

    private void getValueNames(Set<String> target) {
        long lpcValues = UNSAFE.allocateMemory(Type.UINT32.size());
        long lpcbMaxValueNameLen = UNSAFE.allocateMemory(Type.UINT32.size());
        try {
            int errno;
            if ((errno = WindowsRegistry.RegQueryInfoKeyW(hKey, 0, 0, 0, 0, 0,
                    0, lpcValues, lpcbMaxValueNameLen, 0, 0, 0)) == 0) {
                int cValues = MEMORY_IO.getInt(lpcValues);
                long cbMaxValueNameLen = MEMORY_IO.getInt(lpcbMaxValueNameLen);
                long lpValueName = UNSAFE.allocateMemory((cbMaxValueNameLen + 1) * OSInfo.WIDE_CHAR_SIZE);
                try {
                    for (int dwIndex = 0; dwIndex < cValues; dwIndex ++) {
                        if ((errno = WindowsRegistry.RegEnumValueW(hKey, dwIndex, lpValueName, lpcbMaxValueNameLen, 0, 0, 0, 0)) == 0) {
                            target.add(ForeignSupport.getZeroTerminatedString(lpValueName, OSInfo.WIDE_CHARSET));
                        }
                        else throw new LastErrorException(errno);
                    }
                }
                finally {
                    UNSAFE.freeMemory(lpValueName);
                }
            }
            else throw new LastErrorException(errno);
        }
        finally {
            UNSAFE.freeMemory(lpcValues);
        }
    }

    private String query(String key, String defaultValue) {
        long lpValueName = ForeignSupport.allocateWideCharString(key);
        long lpType = UNSAFE.allocateMemory(Type.UINT32.size());
        long lpcbData = UNSAFE.allocateMemory(Type.UINT32.size());
        try {
            if (WindowsRegistry.RegQueryValueExW(hKey, lpValueName, 0, lpType, 0, lpcbData) == 0) {
                if (MEMORY_IO.getInt(lpType) == 0x00000001 /* REG_SZ */) {
                    int cbData = MEMORY_IO.getInt(lpcbData);
                    if (cbData < OSInfo.WIDE_CHAR_SIZE) return "";
                    else {
                        long lpData = UNSAFE.allocateMemory(cbData + OSInfo.WIDE_CHAR_SIZE /* for terminator */);
                        try {
                            UNSAFE.setMemory(lpData + cbData, OSInfo.WIDE_CHAR_SIZE, (byte) '\0');
                            if (WindowsRegistry.RegQueryValueExW(hKey, lpValueName, 0, lpType, lpData, lpcbData) == 0) {
                                return ForeignSupport.getZeroTerminatedString(lpData, OSInfo.WIDE_CHARSET);
                            }
                        }
                        finally {
                            UNSAFE.freeMemory(lpData);
                        }
                    }
                }
            }
            return defaultValue;
        }
        finally {
            UNSAFE.freeMemory(lpValueName);
            UNSAFE.freeMemory(lpType);
            UNSAFE.freeMemory(lpcbData);
        }
    }

    private static boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) return true;
        else if (value.equalsIgnoreCase("false")) return false;
        else throw new ClassCastException("not a boolean");
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = query(key, null);
        return value == null ? defaultValue : parseBoolean(value);
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        String value = query(key, null);
        return value == null ? defaultValue : Byte.parseByte(value);
    }

    private static char parseChar(String value) {
        if (value.length() != 1) throw new ClassCastException("not a char");
        else return value.charAt(0);
    }

    @Override
    public char getChar(String key, char defaultValue) {
        String value = query(key, null);
        return value == null ? defaultValue : parseChar(value);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        String value = query(key, null);
        return value == null ? defaultValue : Short.parseShort(value);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        String value = query(key, null);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        String value = query(key, null);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        String value = query(key, null);
        return value == null ? defaultValue : Float.parseFloat(value);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        String value = query(key, null);
        return value == null ? defaultValue : Double.parseDouble(value);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return query(key, defaultValue);
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        String value = query(key, null);
        return value == null ? defaultValue : new BigInteger(value);
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        String value = query(key, null);
        return value == null ? defaultValue : new BigDecimal(value);
    }

    @Override
    public Rational getRational(String key, Rational defaultValue) {
        String value = query(key, null);
        return value == null ? defaultValue : Rational.parseRational(value);
    }

    @Override
    public short getHalf(String key, short defaultValue) {
        String value = query(key, null);
        return value == null ? defaultValue : Half.parseHalf(value);
    }

    @Override
    public int size() {
        long lpcValues = UNSAFE.allocateMemory(Type.UINT32.size());
        try {
            int errno;
            if ((errno = WindowsRegistry.RegQueryInfoKeyW(hKey, 0, 0, 0, 0, 0,
                    0, lpcValues, 0, 0, 0, 0)) == 0) {
                return MEMORY_IO.getInt(lpcValues);
            }
            else throw new UnexpectedError(new LastErrorException(errno));
        }
        finally {
            UNSAFE.freeMemory(lpcValues);
        }
    }

    @Override
    public boolean contains(String key) {
        long lpValueName = ForeignSupport.allocateWideCharString(key);
        try {
            return WindowsRegistry.RegQueryValueExW(hKey, lpValueName, 0, 0, 0, 0) == 0;
        }
        finally {
            UNSAFE.freeMemory(lpValueName);
        }
    }

}
