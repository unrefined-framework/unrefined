package unrefined.io;

import unrefined.util.function.BiSlot;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface BundleInput {

    String get(String key, String defaultValue) throws IOException;

    <T extends Bundleable> void getBundleable(String key, T obj) throws IOException;
    <T> void getObject(String key, T obj, BiSlot<T, BundleInput> readProc) throws IOException;

    boolean getBoolean(String key, boolean defaultValue) throws IOException;
    byte getByte(String key, byte defaultValue) throws IOException;
    char getChar(String key, char defaultValue) throws IOException;
    short getShort(String key, short defaultValue) throws IOException;
    int getInt(String key, int defaultValue) throws IOException;
    long getLong(String key, long defaultValue) throws IOException;
    float getFloat(String key, float defaultValue) throws IOException;
    double getDouble(String key, double defaultValue) throws IOException;
    BigInteger getBigInteger(String key, BigInteger defaultValue) throws IOException;
    BigDecimal getBigDecimal(String key, BigDecimal defaultValue) throws IOException;

}
