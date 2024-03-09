package unrefined.io;

import unrefined.util.Half;
import unrefined.util.Rational;
import unrefined.util.function.BiSlot;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface BundleOutput {

    void put(String key, String value) throws IOException;

    void putBundleable(String key, Bundleable obj) throws IOException;
    <T> void putObject(String key, T obj, BiSlot<T, BundleOutput> writeProc) throws IOException;

    default void putBoolean(String key, boolean value) throws IOException {
        put(key, Boolean.toString(value));
    }
    default void putByte(String key, byte value) throws IOException {
        put(key, Byte.toString(value));
    }
    default void putByte(String key, int value) throws IOException {
        putByte(key, (byte) value);
    }
    default void putChar(String key, char value) throws IOException {
        put(key, Character.toString(value));
    }
    default void putChar(String key, int value) throws IOException {
        putChar(key, (char) value);
    }
    default void putShort(String key, short value) throws IOException {
        put(key, Short.toString(value));
    }
    default void putShort(String key, int value) throws IOException {
        putShort(key, (short) value);
    }
    default void putInt(String key, int value) throws IOException {
        put(key, Integer.toString(value));
    }
    default void putLong(String key, long value) throws IOException {
        put(key, Long.toString(value));
    }
    default void putFloat(String key, float value) throws IOException {
        put(key, Float.toString(value));
    }
    default void putDouble(String key, double value) throws IOException {
        put(key, Double.toString(value));
    }
    default void putBigInteger(String key, BigInteger value) throws IOException {
        put(key, value.toString());
    }
    default void putBigDecimal(String key, BigDecimal value) throws IOException {
        put(key, value.toEngineeringString());
    }
    default void putRational(String key, Rational value) throws IOException {
        put(key, value.toString());
    }
    default void putHalf(String key, short value) throws IOException {
        put(key, Half.toString(value));
    }

}
