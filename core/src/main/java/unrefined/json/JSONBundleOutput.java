package unrefined.json;

import unrefined.io.BundleOutput;
import unrefined.io.Bundleable;
import unrefined.math.FastMath;
import unrefined.util.Half;
import unrefined.util.Rational;
import unrefined.util.function.BiSlot;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface JSONBundleOutput extends BundleOutput {

    @Override
    void put(String key, String value) throws IOException;
    @Override
    void putBoolean(String key, boolean value) throws IOException;
    @Override
    void putLong(String key, long value) throws IOException;
    @Override
    void putDouble(String key, double value) throws IOException;

    @Override
    void putBundleable(String key, Bundleable obj) throws IOException;
    @Override
    <T> void putObject(String key, T obj, BiSlot<T, BundleOutput> writeProc) throws IOException;
    
    @Override
    default void putByte(String key, byte value) throws IOException {
        putLong(key, value);
    }
    @Override
    default void putByte(String key, int value) throws IOException {
        putByte(key, (byte) value);
    }
    @Override
    default void putChar(String key, char value) throws IOException {
        put(key, Character.toString(value));
    }
    @Override
    default void putChar(String key, int value) throws IOException {
        putChar(key, (char) value);
    }
    @Override
    default void putShort(String key, short value) throws IOException {
        putLong(key, value);
    }
    @Override
    default void putShort(String key, int value) throws IOException {
        putShort(key, (short) value);
    }
    @Override
    default void putInt(String key, int value) throws IOException {
        putLong(key, value);
    }
    @Override
    default void putFloat(String key, float value) throws IOException {
        putDouble(key, value);
    }
    @Override
    default void putBigInteger(String key, BigInteger value) throws IOException {
        put(key, value.toString());
    }
    @Override
    default void putBigDecimal(String key, BigDecimal value) throws IOException {
        put(key, value.toEngineeringString());
    }
    @Override
    default void putRational(String key, Rational value) throws IOException {
        double doubleVal = value.doubleValue();
        if (FastMath.isInteger(doubleVal)) putLong(key, value.longValue());
        else putDouble(key, doubleVal);
    }
    @Override
    default void putHalf(String key, short value) throws IOException {
        putDouble(key, Half.toFloat(value));
    }
    
}
