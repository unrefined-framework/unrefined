package unrefined.util;

import unrefined.context.Environment;
import unrefined.math.FastMath;
import unrefined.util.concurrent.Producer;
import unrefined.util.function.BiFunctor;
import unrefined.util.function.Functor;
import unrefined.util.function.Slot;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class Objects {

    private Objects() {
        throw new NotInstantiableError(Objects.class);
    }

    public static final byte       BYTE_MIN            = Byte.MIN_VALUE;
    public static final byte       BYTE_MAX            = Byte.MAX_VALUE;
    public static final byte       UBYTE_MAX           = -1;
    public static final short      UBYTE_MAX_UNSIGNED  = (short) Byte.toUnsignedInt(UBYTE_MAX);
    public static final short      SHORT_MIN           = Short.MIN_VALUE;
    public static final short      SHORT_MAX           = Short.MAX_VALUE;
    public static final short      USHORT_MAX          = -1;
    public static final int        USHORT_MAX_UNSIGNED = Short.toUnsignedInt(USHORT_MAX);
    public static final int        INT_MIN             = Integer.MIN_VALUE;
    public static final int        INT_MAX             = Integer.MAX_VALUE;
    public static final int        UINT_MAX            = 0xFFFFFF;
    public static final long       UINT_MAX_UNSIGNED   = Integer.toUnsignedLong(UINT_MAX);
    public static final long       LONG_MIN            = Long.MIN_VALUE;
    public static final long       LONG_MAX            = Long.MAX_VALUE;
    public static final long       ULONG_MAX           = 0xFFFFFFFFL;
    public static final BigInteger ULONG_MAX_UNSIGNED  = FastMath.unsign(ULONG_MAX);

    public static final Boolean TRUE = Boolean.TRUE;
    public static final Boolean FALSE = Boolean.FALSE;
    
    public static final float FLOAT_MIN = Float.MIN_VALUE;
    public static final float FLOAT_MAX = Float.MAX_VALUE;
    public static final double DOUBLE_MIN = Double.MIN_VALUE;
    public static final double DOUBLE_MAX = Double.MAX_VALUE;

    public static final float FLOAT_POSITIVE_INFINITY = Float.POSITIVE_INFINITY;
    public static final float FLOAT_NEGATIVE_INFINITY = Float.NEGATIVE_INFINITY;
    public static final float FLOAT_MIN_NORMAL = Float.MIN_NORMAL;
    public static final float FLOAT_MAX_SUBNORMAL = 0x0.fffffep-126f;
    public static final int FLOAT_MAX_EXPONENT = Float.MAX_EXPONENT;
    public static final int FLOAT_MIN_EXPONENT = Float.MIN_EXPONENT;
    public static final double DOUBLE_POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
    public static final double DOUBLE_NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
    public static final double DOUBLE_MIN_NORMAL = Double.MIN_NORMAL;
    public static final double DOUBLE_MAX_SUBNORMAL = 0x0.fffffffffffffp-1022;
    public static final int DOUBLE_MAX_EXPONENT = Double.MAX_EXPONENT;
    public static final int DOUBLE_MIN_EXPONENT = Double.MIN_EXPONENT;

    public static boolean unbox(Boolean value) {
        return value.booleanValue();
    }

    public static byte unbox(Byte value) {
        return value.byteValue();
    }

    public static char unbox(Character value) {
        return value.charValue();
    }

    public static short unbox(Short value) {
        return value.shortValue();
    }

    public static int unbox(Integer value) {
        return value.intValue();
    }

    public static long unbox(Long value) {
        return value.longValue();
    }

    public static float unbox(Float value) {
        return value.floatValue();
    }

    public static double unbox(Double value) {
        return value.doubleValue();
    }

    public static Boolean box(boolean value) {
        return Boolean.valueOf(value);
    }

    public static Byte box(byte value) {
        return Byte.valueOf(value);
    }

    public static Character box(char value) {
        return Character.valueOf(value);
    }

    public static Short box(short value) {
        return Short.valueOf(value);
    }

    public static Integer box(Integer value) {
        return Integer.valueOf(value);
    }

    public static Long box(long value) {
        return Long.valueOf(value);
    }

    public static Float box(float value) {
        return Float.valueOf(value);
    }

    public static Double box(double value) {
        return Double.valueOf(value);
    }

    public static byte decodeByte(String value) {
        return Byte.decode(value);
    }

    public static short decodeShort(String value) {
        return Short.decode(value);
    }

    public static int decodeInt(String value) {
        return Integer.decode(value);
    }

    public static long decodeLong(String value) {
        return Long.decode(value);
    }

    public static boolean parseBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    public static char parseChar(String value) {
        if (value.length() != 1) throw new StringIndexOutOfBoundsException();
        else return value.charAt(0);
    }

    public static short parseShort(String value) {
        return Short.parseShort(value);
    }

    public static short parseShort(String value, int radix) {
        return Short.parseShort(value, radix);
    }

    public static int parseInt(String value) {
        return Integer.parseInt(value);
    }

    public static int parseInt(String value, int radix) {
        return Integer.parseInt(value, radix);
    }

    public static long parseLong(String value) {
        return Long.parseLong(value);
    }

    public static long parseLong(String value, int radix) {
        return Long.parseLong(value, radix);
    }

    public static float parseFloat(String value) {
        return Float.parseFloat(value);
    }

    public static double parseDouble(String value) {
        return Double.parseDouble(value);
    }

    public static byte parseUnsignedByte(String value) {
        return parseUnsignedByte(value, 10);
    }

    public static byte parseUnsignedByte(String value, int radix) {
        try {
            return FastMath.sign(Short.parseShort(value, radix));
        }
        catch (ArithmeticException e) {
            throw new NumberFormatException(String.format("String value %s exceeds range of unsigned byte.", value));
        }
    }

    public static short parseUnsignedShort(String value) {
        return parseUnsignedShort(value, 10);
    }

    public static short parseUnsignedShort(String value, int radix) {
        try {
            return FastMath.sign(Integer.parseInt(value, radix));
        }
        catch (ArithmeticException e) {
            throw new NumberFormatException(String.format("String value %s exceeds range of unsigned short.", value));
        }
    }

    public static int parseUnsignedInt(String value) {
        return Integer.parseUnsignedInt(value);
    }

    public static int parseUnsignedInt(String value, int radix) {
        return Integer.parseUnsignedInt(value, radix);
    }

    public static long parseUnsignedLong(String value) {
        return Long.parseUnsignedLong(value);
    }

    public static long parseUnsignedLong(String value, int radix) {
        return Long.parseUnsignedLong(value, radix);
    }

    public static int compare(boolean a, boolean b) {
        return Boolean.compare(a, b);
    }

    public static int compare(Boolean a, Boolean b) {
        return a.compareTo(b);
    }

    public static int compare(byte a, byte b) {
        return Byte.compare(a, b);
    }

    public static int compareUnsigned(byte a, byte b) {
        return Byte.toUnsignedInt(a) - Byte.toUnsignedInt(b);
    }

    public static int compare(short a, short b) {
        return Short.compare(a, b);
    }

    public static int compareUnsigned(short a, short b) {
        return Short.toUnsignedInt(a) - Short.toUnsignedInt(b);
    }

    public static int compare(int a, int b) {
        return Integer.compare(a, b);
    }

    public static int compareUnsigned(int a, int b) {
        return Integer.compareUnsigned(a, b);
    }

    public static int compare(long a, long b) {
        return Long.compare(a, b);
    }

    public static int compareUnsigned(long a, long b) {
        return Long.compareUnsigned(a, b);
    }

    public static boolean logicalAnd(boolean a, boolean b) {
        return Boolean.logicalAnd(a, b);
    }

    public static boolean logicalOr(boolean a, boolean b) {
        return Boolean.logicalOr(a, b);
    }

    public static boolean logicalXor(boolean a, boolean b) {
        return Boolean.logicalXor(a, b);
    }

    public static int hashCode(boolean value) {
        return Boolean.hashCode(value);
    }
    
    public static int hashCode(byte value) {
        return Byte.hashCode(value);
    }

    public static int hashCode(char value) {
        return Character.hashCode(value);
    }

    public static int hashCode(short value) {
        return Short.hashCode(value);
    }

    public static int hashCode(int value) {
        return Integer.hashCode(value);
    }

    public static int hashCode(long value) {
        return Long.hashCode(value);
    }

    public static int hashCode(float value) {
        return Float.hashCode(value);
    }

    public static int hashCode(double value) {
        return Double.hashCode(value);
    }

    public static short reverseBytes(short x) {
        return Short.reverseBytes(x);
    }

    public static char reverseBytes(char x) {
        return Character.reverseBytes(x);
    }

    public static int reverseBytes(int x) {
        return Integer.reverseBytes(x);
    }

    public static int reverse(int x) {
        return Integer.reverse(x);
    }

    public static long reverseBytes(long x) {
        return Long.reverseBytes(x);
    }

    public static long reverse(long x) {
        return Long.reverse(x);
    }

    public static boolean check(Object object) {
        return nonNull(object);
    }

    public static boolean check(byte value) {
        return value != 0;
    }

    public static boolean check(char value) {
        return value != 0;
    }

    public static boolean check(short value) {
        return value != 0;
    }

    public static boolean check(int value) {
        return value != 0;
    }

    public static boolean check(long value) {
        return value != 0;
    }

    public static boolean check(float value) {
        return Float.floatToIntBits(value) != 0;
    }

    public static boolean check(double value) {
        return Double.doubleToLongBits(value) != 0;
    }
    
    public static int bitCount(int x) {
        return Integer.bitCount(x);
    }
    
    public static int bitCount(long x) {
        return Long.bitCount(x);
    }
    
    public static int highestOneBit(int x) {
        return Integer.highestOneBit(x);
    }
    
    public static long highestOneBit(long x) {
        return Long.highestOneBit(x);
    }
    
    public static int lowestOneBit(int x) {
        return Integer.lowestOneBit(x);
    }
    
    public static long lowestOneBit(long x) {
        return Long.lowestOneBit(x);
    }
    
    public static int rotateLeft(int x, int distance) {
        return Integer.rotateLeft(x, distance);
    }
    
    public static long rotateLeft(long x, int distance) {
        return Long.rotateLeft(x, distance);
    }
    
    public static int rotateRight(int x, int distance) {
        return Integer.rotateRight(x, distance);
    }
    
    public static long rotateRight(long x, int distance) {
        return Long.rotateRight(x, distance);
    }
    
    public static int floatToIntBits(float value) {
        return Float.floatToIntBits(value);
    }
    
    public static int floatToRawIntBits(float value) {
        return Float.floatToRawIntBits(value);
    }
    
    public static long doubleToLongBits(double value) {
        return Double.doubleToLongBits(value);
    }
    
    public static long doubleToRawLongBits(double value) {
        return Double.doubleToRawLongBits(value);
    }
    
    public static float intBitsToFloat(int bits) {
        return Float.intBitsToFloat(bits);
    }
    
    public static double longBitsToDouble(long bits) {
        return Double.longBitsToDouble(bits);
    }
    
    public static boolean isFinite(float x) {
        return Float.isFinite(x);
    }
    
    public static boolean isInfinite(float x) {
        return Float.isInfinite(x);
    }
    
    public static boolean isNaN(float x) {
        return Float.isNaN(x);
    }
    
    public static boolean isFinite(double x) {
        return Double.isFinite(x);
    }
    
    public static boolean isInfinite(double x) {
        return Double.isInfinite(x);
    }
    
    public static boolean isNaN(double x) {
        return Double.isNaN(x);
    }

    public static String toString(boolean value) {
        return Boolean.toString(value);
    }

    public static String toString(byte value) {
        return Byte.toString(value);
    }
    
    public static String toBinaryString(byte value) {
        return Integer.toBinaryString(Byte.toUnsignedInt(value));
    }
    
    public static String toOctalString(byte value) {
        return Integer.toOctalString(Byte.toUnsignedInt(value));
    }
    
    public static String toHexString(byte value) {
        return Integer.toHexString(Byte.toUnsignedInt(value));
    }

    public static String toString(char value) {
        return Character.toString(value);
    }

    public static String toString(short value) {
        return Short.toString(value);
    }

    public static String toBinaryString(short value) {
        return Integer.toBinaryString(Short.toUnsignedInt(value));
    }

    public static String toOctalString(short value) {
        return Integer.toOctalString(Short.toUnsignedInt(value));
    }

    public static String toHexString(short value) {
        return Integer.toHexString(Short.toUnsignedInt(value));
    }

    public static String toString(int value) {
        return Integer.toString(value);
    }

    public static String toBinaryString(int value) {
        return Integer.toBinaryString(value);
    }

    public static String toOctalString(int value) {
        return Integer.toOctalString(value);
    }

    public static String toHexString(int value) {
        return Integer.toHexString(value);
    }

    public static String toString(int value, int radix) {
        return Integer.toString(value, radix);
    }

    public static String toString(long value) {
        return Long.toString(value);
    }

    public static String toBinaryString(long value) {
        return Long.toBinaryString(value);
    }

    public static String toOctalString(long value) {
        return Long.toOctalString(value);
    }

    public static String toHexString(long value) {
        return Long.toHexString(value);
    }

    public static String toString(long value, int radix) {
        return Long.toString(value, radix);
    }

    public static String toString(float value) {
        return Float.toString(value);
    }

    public static String toHexString(float value) {
        return Float.toHexString(value);
    }

    public static String toString(double value) {
        return Double.toString(value);
    }

    public static String toHexString(double value) {
        return Double.toHexString(value);
    }

    public static char toCharExact(String s) {
        if (s.length() != 1) throw new StringIndexOutOfBoundsException();
        return s.charAt(0);
    }

    public static byte toByteExact(short x) {
        if (x < Byte.MIN_VALUE || x > Byte.MAX_VALUE) throw new ArithmeticException("integer overflow");
        else return (byte) x;
    }

    public static byte toByteExact(int x) {
        if (x < Byte.MIN_VALUE || x > Byte.MAX_VALUE) throw new ArithmeticException("integer overflow");
        else return (byte) x;
    }

    public static byte toByteExact(long x) {
        if (x < Byte.MIN_VALUE || x > Byte.MAX_VALUE) throw new ArithmeticException("integer overflow");
        else return (byte) x;
    }

    public static short toShortExact(int x) {
        if (x < Short.MIN_VALUE || x > Short.MAX_VALUE) throw new ArithmeticException("integer overflow");
        else return (short) x;
    }

    public static short toShortExact(long x) {
        if (x < Short.MIN_VALUE || x > Short.MAX_VALUE) throw new ArithmeticException("integer overflow");
        else return (short) x;
    }

    public static int toIntExact(long x) {
        if (x < Integer.MIN_VALUE || x > Integer.MAX_VALUE) throw new ArithmeticException("integer overflow");
        else return (int) x;
    }

    public static short toHalfExact(float x) {
        if (x < Half.toFloat(Half.MIN_VALUE) || x > Half.toFloat(Half.MAX_VALUE)) throw new ArithmeticException("integer overflow");
        else return Half.toHalf(x);
    }

    public static short toHalfExact(double x) {
        if (x < Half.toFloat(Half.MIN_VALUE) || x > Half.toFloat(Half.MAX_VALUE)) throw new ArithmeticException("integer overflow");
        else return Half.toHalf((float) x);
    }

    public static float toFloatExact(double x) {
        if (x < Float.MIN_VALUE || x > Float.MAX_VALUE) throw new ArithmeticException("integer overflow");
        else return (float) x;
    }

    public static <T> T operate(T t, Slot<T> consumer) {
        consumer.accept(t);
        return t;
    }

    public static Class<?> box(Class<?> clazz) {
        if (clazz == null) return null;
        else if (clazz == boolean.class) return Boolean.class;
        else if (clazz == byte.class) return Byte.class;
        else if (clazz == char.class) return Character.class;
        else if (clazz == short.class) return Short.class;
        else if (clazz == int.class) return Integer.class;
        else if (clazz == long.class) return Long.class;
        else if (clazz == float.class) return Float.class;
        else if (clazz == double.class) return Double.class;
        else if (clazz == void.class) return Void.class;
        else return clazz;
    }

    public static Class<?> unbox(Class<?> clazz) {
        if (clazz == null) return null;
        else if (clazz == Boolean.class) return boolean.class;
        else if (clazz == Byte.class) return byte.class;
        else if (clazz == Character.class) return char.class;
        else if (clazz == Short.class) return short.class;
        else if (clazz == Integer.class) return int.class;
        else if (clazz == Long.class) return long.class;
        else if (clazz == Float.class) return float.class;
        else if (clazz == Double.class) return double.class;
        else if (clazz == Void.class) return void.class;
        else return clazz;
    }

    public static boolean equals(Object a, Object b) {
        return java.util.Objects.equals(a, b);
    }

    public static boolean deepEquals(Object a, Object b) {
        return java.util.Objects.deepEquals(a, b);
    }

    public static int hashCode(Object o) {
        return java.util.Objects.hashCode(o);
    }

    public static int hash(Object... values) {
        return java.util.Objects.hash(values);
    }

    public static String toString(Object o) {
        return java.util.Objects.toString(o);
    }

    public static String toString(Object o, String nullDefault) {
        return java.util.Objects.toString(o, nullDefault);
    }

    public static String toIdentityString(Object o) {
        requireNonNull(o);
        return o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
    }

    public static <T> int compare(T a, T b, Comparator<? super T> cmp) {
        return java.util.Objects.compare(a, b, cmp);
    }

    public static <T extends Comparable<? super T>> int compare(T a, T b) {
        return a.compareTo(b);
    }

    public static <U, T extends Comparable<? super U>> int compare(T a, U b) {
        return a.compareTo(b);
    }

    public static <T> T requireNonNull(T t) {
        return java.util.Objects.requireNonNull(t);
    }

    public static <T> T requireNonNull(T t, String message) {
        return java.util.Objects.requireNonNull(t, message);
    }

    public static boolean isNull(Object o) {
        return java.util.Objects.isNull(o);
    }

    public static boolean nonNull(Object o) {
        return java.util.Objects.nonNull(o);
    }

    public static boolean isInstance(Class<?> clazz, Object o) {
        return clazz.isInstance(o);
    }

    public static boolean nullInstance(Class<?> clazz, Object o) {
        return o == null || clazz.isInstance(o);
    }

    public static <T> T requireNonNullElse(T t, T nullDefault) {
        return (t != null) ? t : requireNonNull(nullDefault, "nullDefault");
    }

    public static <T> T requireNonNullElseGet(T t, Producer<? extends T> producer) {
        return (t != null) ? t
                : requireNonNull(requireNonNull(producer, "producer").get(), "producer.get()");
    }

    public static <T> T requireNonNull(T t, Producer<String> messageProducer) {
        if (t == null)
            throw new NullPointerException(messageProducer == null ?
                    null : messageProducer.get());
        return t;
    }

    public static final BiFunctor<String, List<Number>, StringIndexOutOfBoundsException>
            SIOOBE_FORMATTER = outOfBoundsExceptionFormatter(new Functor<String, StringIndexOutOfBoundsException>() {
        @Override
        public StringIndexOutOfBoundsException apply(String s) {
            return new StringIndexOutOfBoundsException(s);
        }
    });

    public static final BiFunctor<String, List<Number>, ArrayIndexOutOfBoundsException>
            AIOOBE_FORMATTER = outOfBoundsExceptionFormatter(new Functor<String, ArrayIndexOutOfBoundsException>() {
        @Override
        public ArrayIndexOutOfBoundsException apply(String s) {
            return new ArrayIndexOutOfBoundsException(s);
        }
    });

    public static final BiFunctor<String, List<Number>, IndexOutOfBoundsException>
            IOOBE_FORMATTER = outOfBoundsExceptionFormatter(new Functor<String, IndexOutOfBoundsException>() {
        @Override
        public IndexOutOfBoundsException apply(String s) {
            return new IndexOutOfBoundsException(s);
        }
    });

    private static RuntimeException outOfBounds(
            BiFunctor<String, List<Number>, ? extends RuntimeException> oobef,
            String checkKind,
            Number... args) {
        List<Number> largs = Arrays.asList(args);
        RuntimeException e = oobef == null
                ? null : oobef.apply(checkKind, largs);
        return e == null
                ? new IndexOutOfBoundsException(outOfBoundsMessage(checkKind, largs)) : e;
    }

    private static RuntimeException outOfBoundsCheckIndex(
            BiFunctor<String, List<Number>, ? extends RuntimeException> oobe,
            int index, int length) {
        return outOfBounds(oobe, "checkIndex", index, length);
    }

    private static RuntimeException outOfBoundsCheckFromToIndex(
            BiFunctor<String, List<Number>, ? extends RuntimeException> oobe,
            int fromIndex, int toIndex, int length) {
        return outOfBounds(oobe, "checkFromToIndex", fromIndex, toIndex, length);
    }

    private static RuntimeException outOfBoundsCheckFromIndexSize(
            BiFunctor<String, List<Number>, ? extends RuntimeException> oobe,
            int fromIndex, int size, int length) {
        return outOfBounds(oobe, "checkFromIndexSize", fromIndex, size, length);
    }

    private static RuntimeException outOfBoundsCheckIndex(
            BiFunctor<String, List<Number>, ? extends RuntimeException> oobe,
            long index, long length) {
        return outOfBounds(oobe, "checkIndex", index, length);
    }

    private static RuntimeException outOfBoundsCheckFromToIndex(
            BiFunctor<String, List<Number>, ? extends RuntimeException> oobe,
            long fromIndex, long toIndex, long length) {
        return outOfBounds(oobe, "checkFromToIndex", fromIndex, toIndex, length);
    }

    private static RuntimeException outOfBoundsCheckFromIndexSize(
            BiFunctor<String, List<Number>, ? extends RuntimeException> oobe,
            long fromIndex, long size, long length) {
        return outOfBounds(oobe, "checkFromIndexSize", fromIndex, size, length);
    }

    public static <X extends RuntimeException>
    BiFunctor<String, List<Number>, X> outOfBoundsExceptionFormatter(Functor<String, X> f) {
        // Use anonymous class to avoid bootstrap issues if this method is
        // used early in startup
        return new BiFunctor<String, List<Number>, X>() {
            @Override
            public X apply(String checkKind, List<Number> args) {
                return f.apply(outOfBoundsMessage(checkKind, args));
            }
        };
    }

    private static String outOfBoundsMessage(String checkKind, List<? extends Number> args) {
        if (checkKind == null && args == null) {
            return "Range check failed";
        } else if (checkKind == null) {
            return String.format("Range check failed: %s", args);
        } else if (args == null) {
            return String.format("Range check failed: %s", checkKind);
        }

        int argSize = 0;
        switch (checkKind) {
            case "checkIndex":
                argSize = 2;
                break;
            case "checkFromToIndex":
            case "checkFromIndexSize":
                argSize = 3;
                break;
            default:
        }

        // Switch to default if fewer or more arguments than required are supplied
        switch ((args.size() != argSize) ? "" : checkKind) {
            case "checkIndex":
                return String.format("Index %s out of bounds for length %s",
                        args.get(0), args.get(1));
            case "checkFromToIndex":
                return String.format("Range [%s, %s) out of bounds for length %s",
                        args.get(0), args.get(1), args.get(2));
            case "checkFromIndexSize":
                return String.format("Range [%s, %<s + %s) out of bounds for length %s",
                        args.get(0), args.get(1), args.get(2));
            default:
                return String.format("Range check failed: %s %s", checkKind, args);
        }
    }

    public static <X extends RuntimeException>
    int checkIndex(int index, int length,
                   BiFunctor<String, List<Number>, X> oobef) {
        if (index < 0 || index >= length)
            throw outOfBoundsCheckIndex(oobef, index, length);
        return index;
    }

    public static <X extends RuntimeException>
    int checkFromToIndex(int fromIndex, int toIndex, int length,
                         BiFunctor<String, List<Number>, X> oobef) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > length)
            throw outOfBoundsCheckFromToIndex(oobef, fromIndex, toIndex, length);
        return fromIndex;
    }

    public static <X extends RuntimeException>
    int checkFromIndexSize(int fromIndex, int size, int length,
                           BiFunctor<String, List<Number>, X> oobef) {
        if ((length | fromIndex | size) < 0 || size > length - fromIndex)
            throw outOfBoundsCheckFromIndexSize(oobef, fromIndex, size, length);
        return fromIndex;
    }

    public static <X extends RuntimeException>
    long checkIndex(long index, long length,
                    BiFunctor<String, List<Number>, X> oobef) {
        if (index < 0 || index >= length)
            throw outOfBoundsCheckIndex(oobef, index, length);
        return index;
    }

    public static <X extends RuntimeException>
    long checkFromToIndex(long fromIndex, long toIndex, long length,
                          BiFunctor<String, List<Number>, X> oobef) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > length)
            throw outOfBoundsCheckFromToIndex(oobef, fromIndex, toIndex, length);
        return fromIndex;
    }

    public static <X extends RuntimeException>
    long checkFromIndexSize(long fromIndex, long size, long length,
                            BiFunctor<String, List<Number>, X> oobef) {
        if ((length | fromIndex | size) < 0 || size > length - fromIndex)
            throw outOfBoundsCheckFromIndexSize(oobef, fromIndex, size, length);
        return fromIndex;
    }

    public static
    int checkIndex(int index, int length) {
        return checkIndex(index, length, null);
    }
    
    public static
    int checkFromToIndex(int fromIndex, int toIndex, int length) {
        return checkFromToIndex(fromIndex, toIndex, length, null);
    }
    
    public static
    int checkFromIndexSize(int fromIndex, int size, int length) {
        return checkFromIndexSize(fromIndex, size, length, null);
    }
    
    public static
    long checkIndex(long index, long length) {
        return checkIndex(index, length, null);
    }
    
    public static
    long checkFromToIndex(long fromIndex, long toIndex, long length) {
        return checkFromToIndex(fromIndex, toIndex, length, null);
    }
    
    public static
    long checkFromIndexSize(long fromIndex, long size, long length) {
        return checkFromIndexSize(fromIndex, size, length, null);
    }

    public static Object checkObjectField(Object object, Field field) {
        // NOTE: will throw NullPointerException, as specified, if the field is null
        if (Modifier.isStatic(field.getModifiers())) return null;
        // NOTE: will throw NullPointerException, as specified, if the object is null
        else if (!field.getDeclaringClass().isAssignableFrom(object.getClass())) {
            StringBuilder builder = new StringBuilder("Can not set ");
            builder.append(field.getType().getName())
                    .append(" field ")
                    .append(field.getDeclaringClass().getName()).append(".").append(field.getName())
                    .append(" to ");
            String attemptedType = object.getClass().getName();
            if (!attemptedType.isEmpty()) builder.append(attemptedType);
            else builder.append("null value");
            throw new IllegalArgumentException(builder.toString());
        }
        return object;
    }

}
