package unrefined.json;

import unrefined.io.BundleInput;
import unrefined.io.Bundleable;
import unrefined.util.Half;
import unrefined.util.Objects;
import unrefined.util.Rational;
import unrefined.util.function.BiSlot;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface JSONBundleInput extends BundleInput {

    @Override
    String get(String key, String defaultValue) throws IOException;
    @Override
    boolean getBoolean(String key, boolean defaultValue) throws IOException;
    @Override
    long getLong(String key, long defaultValue) throws IOException;
    @Override
    double getDouble(String key, double defaultValue) throws IOException;

    @Override
    <T extends Bundleable> void getBundleable(String key, T obj) throws IOException;
    @Override
    <T> void getObject(String key, T obj, BiSlot<T, BundleInput> readProc) throws IOException;

    @Override
    default byte getByte(String key, byte defaultValue) throws IOException {
        return Objects.toByteExact(getLong(key, defaultValue));
    }
    @Override
    default char getChar(String key, char defaultValue) throws IOException {
        return Objects.toCharExact(get(key, String.valueOf(defaultValue)));
    }
    @Override
    default short getShort(String key, short defaultValue) throws IOException {
        return Objects.toByteExact(getLong(key, defaultValue));
    }
    @Override
    default int getInt(String key, int defaultValue) throws IOException {
        return Objects.toByteExact(getLong(key, defaultValue));
    }
    @Override
    default float getFloat(String key, float defaultValue) throws IOException {
        return Objects.toFloatExact(getDouble(key, defaultValue));
    }
    @Override
    default BigInteger getBigInteger(String key, BigInteger defaultValue) throws IOException {
        String value = get(key, null);
        return value == null ? defaultValue : new BigInteger(value);
    }
    @Override
    default BigDecimal getBigDecimal(String key, BigDecimal defaultValue) throws IOException {
        String value = get(key, null);
        return value == null ? defaultValue : new BigDecimal(value);
    }
    @Override
    Rational getRational(String key, Rational defaultValue) throws IOException;
    @Override
    default short getHalf(String key, short defaultValue) throws IOException {
        return Objects.toHalfExact(getDouble(key, Half.toFloat(defaultValue)));
    }

}
