package unrefined.io;

import unrefined.util.Half;
import unrefined.util.Rational;
import unrefined.util.function.BiSlot;
import unrefined.util.function.FunctionTargetException;

import java.io.EOFException;
import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BundleReader extends FilterReader implements BundleInput {

    private final Deque<String> prefixes = new ArrayDeque<>();

    private final Map<String, String> bundle = new HashMap<>();

    public BundleReader(Reader in) {
        super(in);
    }

    private void readBundle(int entries) throws IOException {
        // 0 = key, 1 = value, 2 = complete
        int status = 0;
        StringBuilder builder = new StringBuilder();
        String key = null;
        try {
            for (int i = 0; i < entries; i ++) {
                int read = read();
                if (read == -1) throw new EOFException();
                char ch = (char) read;
                if (ch == '\0') {
                    status ++;
                    switch (status) {
                        case 1: key = builder.toString(); builder.setLength(0); break;
                        case 2: status = 0; bundle.put(key, builder.toString()); builder.setLength(0); break;
                    }
                }
                else builder.append(ch);
            }
        }
        catch (EOFException ignored) {
        }
        if (status != 0) throw new IOException("Unexpected end of file");
    }

    private void ensureKey(String key) throws IOException {
        if (!bundle.containsKey(key)) readBundle(256);
    }
    
    private String getFullKey(String key) {
        Objects.requireNonNull(key);
        if (prefixes.isEmpty()) return key;
        else {
            StringBuilder builder = new StringBuilder(key);
            for (String prefix : prefixes) {
                builder.insert(0, prefix);
            }
            return builder.toString();
        }
    }

    @Override
    public String get(String key, String defaultValue) throws IOException {
        key = getFullKey(key);
        ensureKey(key);
        String value = bundle.get(key);
        return value == null ? defaultValue : value;
    }

    @Override
    public <T extends Bundleable> void getBundleable(String key, T obj) throws IOException {
        prefixes.push(Objects.requireNonNull(key));
        try {
            obj.readFromBundle(this);
        }
        finally {
            prefixes.pop();
        }
    }

    @Override
    public <T> void getObject(String key, T obj, BiSlot<T, BundleInput> readProc) throws IOException {
        prefixes.push(Objects.requireNonNull(key));
        try {
            readProc.accept(obj, this);
        }
        catch (FunctionTargetException e) {
            if (e.getTargetException() instanceof IOException) throw (IOException) e.getTargetException();
        }
        finally {
            prefixes.pop();
        }
    }

    private static boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) return true;
        else if (value.equalsIgnoreCase("false")) return false;
        else throw new ClassCastException("not a boolean");
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) throws IOException {
        key = getFullKey(key);
        ensureKey(key);
        String value = bundle.get(key);
        return value == null ? defaultValue : parseBoolean(value);
    }

    @Override
    public byte getByte(String key, byte defaultValue) throws IOException {
        key = getFullKey(key);
        ensureKey(key);
        String value = bundle.get(key);
        return value == null ? defaultValue : Byte.parseByte(value);
    }

    private static char parseChar(String value) {
        if (value.length() != 1) throw new ClassCastException("not a char");
        else return value.charAt(0);
    }

    @Override
    public char getChar(String key, char defaultValue) throws IOException {
        key = getFullKey(key);
        ensureKey(key);
        String value = bundle.get(key);
        return value == null ? defaultValue : parseChar(value);
    }

    @Override
    public short getShort(String key, short defaultValue) throws IOException {
        key = getFullKey(key);
        ensureKey(key);
        String value = bundle.get(key);
        return value == null ? defaultValue : Short.parseShort(value);
    }

    @Override
    public int getInt(String key, int defaultValue) throws IOException {
        key = getFullKey(key);
        ensureKey(key);
        String value = bundle.get(key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    @Override
    public long getLong(String key, long defaultValue) throws IOException {
        key = getFullKey(key);
        ensureKey(key);
        String value = bundle.get(key);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    @Override
    public float getFloat(String key, float defaultValue) throws IOException {
        key = getFullKey(key);
        ensureKey(key);
        String value = bundle.get(key);
        return value == null ? defaultValue : Float.parseFloat(value);
    }

    @Override
    public double getDouble(String key, double defaultValue) throws IOException {
        key = getFullKey(key);
        ensureKey(key);
        String value = bundle.get(key);
        return value == null ? defaultValue : Double.parseDouble(value);
    }

    @Override
    public BigInteger getBigInteger(String key, BigInteger defaultValue) throws IOException {
        key = getFullKey(key);
        ensureKey(key);
        String value = bundle.get(key);
        return value == null ? defaultValue : new BigInteger(value);
    }

    @Override
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) throws IOException {
        key = getFullKey(key);
        ensureKey(key);
        String value = bundle.get(key);
        return value == null ? defaultValue : new BigDecimal(value);
    }

    @Override
    public Rational getRational(String key, Rational defaultValue) throws IOException {
        key = getFullKey(key);
        ensureKey(key);
        String value = bundle.get(key);
        return value == null ? defaultValue : Rational.parseRational(value);
    }

    @Override
    public short getHalf(String key, short defaultValue) throws IOException {
        key = getFullKey(key);
        ensureKey(key);
        String value = bundle.get(key);
        return value == null ? defaultValue : Half.parseHalf(value);
    }

}
