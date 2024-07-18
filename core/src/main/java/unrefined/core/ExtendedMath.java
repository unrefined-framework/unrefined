package unrefined.core;

import unrefined.math.FastMath;
import unrefined.util.NotInstantiableError;
import unrefined.util.Objects;
import unrefined.util.Randoms;

import java.math.BigInteger;
import java.util.Random;

public final class ExtendedMath {

    private ExtendedMath() {
        throw new NotInstantiableError(ExtendedMath.class);
    }
    
    public static byte unsignedMax(byte a, byte b) {
        return Objects.compareUnsigned(a, b) > 0 ? a : b;
    }
    
    public static short unsignedMax(short a, short b) {
        return Objects.compareUnsigned(a, b) > 0 ? a : b;
    }

    public static int unsignedMax(int a, int b) {
        return Integer.compareUnsigned(a, b) > 0 ? a : b;
    }

    public static long unsignedMax(long a, long b) {
        return Long.compareUnsigned(a, b) > 0 ? a : b;
    }

    public static byte unsignedMax(byte a, byte b, byte c) {
        return unsignedMax(unsignedMax(a, b), c);
    }

    public static short unsignedMax(short a, short b, short c) {
        return unsignedMax(unsignedMax(a, b), c);
    }

    public static int unsignedMax(int a, int b, int c) {
        return unsignedMax(unsignedMax(a, b), c);
    }

    public static long unsignedMax(long a, long b, long c) {
        return unsignedMax(unsignedMax(a, b), c);
    }

    public static byte unsignedMin(byte a, byte b) {
        return Objects.compareUnsigned(a, b) < 0 ? a : b;
    }

    public static short unsignedMin(short a, short b) {
        return Objects.compareUnsigned(a, b) < 0 ? a : b;
    }

    public static int unsignedMin(int a, int b) {
        return Integer.compareUnsigned(a, b) < 0 ? a : b;
    }

    public static long unsignedMin(long a, long b) {
        return Long.compareUnsigned(a, b) < 0 ? a : b;
    }

    public static byte unsignedMin(byte a, byte b, byte c) {
        return unsignedMin(unsignedMin(a, b), c);
    }

    public static short unsignedMin(short a, short b, short c) {
        return unsignedMin(unsignedMin(a, b), c);
    }

    public static int unsignedMin(int a, int b, int c) {
        return unsignedMin(unsignedMin(a, b), c);
    }

    public static long unsignedMin(long a, long b, long c) {
        return unsignedMin(unsignedMin(a, b), c);
    }

    public static byte sign(short x) throws ArithmeticException {
        if (x > Objects.UBYTE_MAX_UNSIGNED) throw new ArithmeticException("integer overflow");
        else return (byte) x;
    }

    public static short sign(int x) throws ArithmeticException {
        if (x > Objects.USHORT_MAX_UNSIGNED) throw new ArithmeticException("integer overflow");
        else return (short) x;
    }

    public static int sign(long x) throws ArithmeticException {
        if (x > Objects.UINT_MAX_UNSIGNED) throw new ArithmeticException("integer overflow");
        else return (int) x;
    }

    public static long sign(BigInteger x) throws ArithmeticException {
        if (x.compareTo(Objects.ULONG_MAX_UNSIGNED) > 0) throw new ArithmeticException("integer overflow");
        else return x.longValue();
    }

    public static long multiplyFull(int x, int y) {
        return (long) x * (long) y;
    }

    public static int ceilDiv(int x, int y) {
        final int q = x / y;
        // if the signs are the same and modulo not zero, round up
        if ((x ^ y) >= 0 && (q * y != x)) {
            return q + 1;
        }
        return q;
    }

    public static long ceilDiv(long x, long y) {
        final long q = x / y;
        // if the signs are the same and modulo not zero, round up
        if ((x ^ y) >= 0 && (q * y != x)) {
            return q + 1;
        }
        return q;
    }

    public static int ceilMod(int x, int y) {
        final int r = x % y;
        // if the signs are the same and modulo not zero, adjust result
        if ((x ^ y) >= 0 && r != 0) {
            return r - y;
        }
        return r;
    }

    public static long ceilMod(long x, long y) {
        final long r = x % y;
        // if the signs are the same and modulo not zero, adjust result
        if ((x ^ y) >= 0 && r != 0) {
            return r - y;
        }
        return r;
    }

    public static int absExact(int a) {
        if (a == Integer.MIN_VALUE)
            throw new ArithmeticException(
                    "Overflow to represent absolute value of Integer.MIN_VALUE");
        else
            return FastMath.abs(a);
    }

    public static long absExact(long a) {
        if (a == Long.MIN_VALUE)
            throw new ArithmeticException(
                    "Overflow to represent absolute value of Long.MIN_VALUE");
        else
            return FastMath.abs(a);
    }

    public static int divideExact(int x, int y) {
        int q = x / y;
        if ((x & y & q) >= 0) {
            return q;
        }
        throw new ArithmeticException("integer overflow");
    }

    public static long divideExact(long x, long y) {
        long q = x / y;
        if ((x & y & q) >= 0) {
            return q;
        }
        throw new ArithmeticException("long overflow");
    }

    public static int floorDivExact(int x, int y) {
        final int q = x / y;
        if ((x & y & q) >= 0) {
            // if the signs are different and modulo not zero, round down
            if ((x ^ y) < 0 && (q * y != x)) {
                return q - 1;
            }
            return q;
        }
        throw new ArithmeticException("integer overflow");
    }

    public static long floorDivExact(long x, long y) {
        final long q = x / y;
        if ((x & y & q) >= 0) {
            // if the signs are different and modulo not zero, round down
            if ((x ^ y) < 0 && (q * y != x)) {
                return q - 1;
            }
            return q;
        }
        throw new ArithmeticException("long overflow");
    }

    public static int ceilDivExact(int x, int y) {
        final int q = x / y;
        if ((x & y & q) >= 0) {
            // if the signs are the same and modulo not zero, round up
            if ((x ^ y) >= 0 && (q * y != x)) {
                return q + 1;
            }
            return q;
        }
        throw new ArithmeticException("integer overflow");
    }

    public static long ceilDivExact(long x, long y) {
        final long q = x / y;
        if ((x & y & q) >= 0) {
            // if the signs are the same and modulo not zero, round up
            if ((x ^ y) >= 0 && (q * y != x)) {
                return q + 1;
            }
            return q;
        }
        throw new ArithmeticException("long overflow");
    }

    public static int clamp(long value, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(min + " > " + max);
        }
        return (int) Math.min(max, Math.max(value, min));
    }

    public static long clamp(long value, long min, long max) {
        if (min > max) {
            throw new IllegalArgumentException(min + " > " + max);
        }
        return Math.min(max, Math.max(value, min));
    }

    public static double clamp(double value, double min, double max) {
        // This unusual condition allows keeping only one branch
        // on common path when min < max and neither of them is NaN.
        // If min == max, we should additionally check for +0.0/-0.0 case,
        // so we're still visiting the if statement.
        if (!(min < max)) { // min greater than, equal to, or unordered with respect to max; NaN values are unordered
            if (Double.isNaN(min)) {
                throw new IllegalArgumentException("min is NaN");
            }
            if (Double.isNaN(max)) {
                throw new IllegalArgumentException("max is NaN");
            }
            if (Double.compare(min, max) > 0) {
                throw new IllegalArgumentException(min + " > " + max);
            }
            // Fall-through if min and max are exactly equal (or min = -0.0 and max = +0.0)
            // and none of them is NaN
        }
        return Math.min(max, Math.max(value, min));
    }

    public static float clamp(float value, float min, float max) {
        // This unusual condition allows keeping only one branch
        // on common path when min < max and neither of them is NaN.
        // If min == max, we should additionally check for +0.0/-0.0 case,
        // so we're still visiting the if statement.
        if (!(min < max)) { // min greater than, equal to, or unordered with respect to max; NaN values are unordered
            if (Float.isNaN(min)) {
                throw new IllegalArgumentException("min is NaN");
            }
            if (Float.isNaN(max)) {
                throw new IllegalArgumentException("max is NaN");
            }
            if (Float.compare(min, max) > 0) {
                throw new IllegalArgumentException(min + " > " + max);
            }
            // Fall-through if min and max are exactly equal (or min = -0.0 and max = +0.0)
            // and none of them is NaN
        }
        return Math.min(max, Math.max(value, min));
    }

    public static double cosFromSin(double sin, double angle) {
        // sin(x)^2 + cos(x)^2 = 1
        double cos = Math.sqrt(1.0 - sin * sin);
        double a = angle + FastMath.PI_HALF;
        double b = a - (int) (a / FastMath.TAU) * FastMath.TAU;
        if (b < 0.0) b = FastMath.TAU + b;
        if (b >= FastMath.PI) return -cos;
        return cos;
    }

    public static double safeAsin(double a) {
        return a <= -1.0 ? -FastMath.PI_HALF : a >= 1.0 ? FastMath.PI_HALF : Math.asin(a);
    }

    public static double safeAcos(double a) {
        if (a < -1.0) return FastMath.PI;
        else if (a > 1.0) return 0.0;
        else return Math.acos(a);
    }

    private static final class Log2 {
        private Log2() {
            throw new UnsupportedOperationException();
        }

        private static final double LN_2 = Math.log(2);

        public static double compute(double x) {
            return Math.log(x) / LN_2;
        }
    }

    public static double log2(double a) {
        return Log2.compute(a);
    }

    private static final class RandomHolder {
        private RandomHolder() {
            throw new UnsupportedOperationException();
        }

        public static final Random GENERATOR = new Random();
    }

    public static float randomFloat(float bound) {
        return Randoms.nextFloat(RandomHolder.GENERATOR, bound);
    }

    public static float randomFloat(float min, float max) {
        return Randoms.nextFloat(RandomHolder.GENERATOR, min, max);
    }

    public static float randomFloat() {
        return Randoms.nextFloat(RandomHolder.GENERATOR);
    }

    public static double randomDouble(double bound) {
        return Randoms.nextDouble(RandomHolder.GENERATOR, bound);
    }

    public static double randomDouble(double min, double max) {
        return Randoms.nextDouble(RandomHolder.GENERATOR, min, max);
    }

    public static double randomDouble() {
        return Randoms.nextDouble(RandomHolder.GENERATOR);
    }

    public static int randomInt(int bound) {
        return Randoms.nextInt(RandomHolder.GENERATOR, bound);
    }

    public static int randomInt(int min, int max) {
        return Randoms.nextInt(RandomHolder.GENERATOR, min, max);
    }

    public static int randomInt() {
        return Randoms.nextInt(RandomHolder.GENERATOR);
    }

    public static long randomLong(long bound) {
        return Randoms.nextLong(RandomHolder.GENERATOR, bound);
    }

    public static long randomLong(long min, long max) {
        return Randoms.nextLong(RandomHolder.GENERATOR, min, max);
    }

    public static long randomLong() {
        return Randoms.nextLong(RandomHolder.GENERATOR);
    }

    public static int signum(int value) {
        return Integer.signum(value);
    }

    public static long signum(long value) {
        return Long.signum(value);
    }

    // Reference: Apache Commons Math
    // https://github.com/apache/commons-math/blob/3.6.1-release/src/main/java/org/apache/commons/math3/util/FastMath.java
    private static final class AsinhAtanh {

        private AsinhAtanh() {
            throw new UnsupportedOperationException();
        }

        private static final double F_1_3 = 1d / 3d;
        private static final double F_1_5 = 1d / 5d;
        private static final double F_1_7 = 1d / 7d;
        private static final double F_1_9 = 1d / 9d;
        private static final double F_1_11 = 1d / 11d;
        private static final double F_1_13 = 1d / 13d;
        private static final double F_1_15 = 1d / 15d;
        private static final double F_1_17 = 1d / 17d;
        private static final double F_3_4 = 3d / 4d;
        private static final double F_15_16 = 15d / 16d;
        private static final double F_13_14 = 13d / 14d;
        private static final double F_11_12 = 11d / 12d;
        private static final double F_9_10 = 9d / 10d;
        private static final double F_7_8 = 7d / 8d;
        private static final double F_5_6 = 5d / 6d;
        private static final double F_1_2 = 1d / 2d;

        public static double asinh(double a) {
            boolean negative = false;
            if (a < 0) {
                negative = true;
                a = -a;
            }

            double absAsinh;
            if (a > 0.167) {
                absAsinh = Math.log(Math.sqrt(a * a + 1) + a);
            } else {
                final double a2 = a * a;
                if (a > 0.097) {
                    absAsinh = a * (1 - a2 * (F_1_3 - a2 * (F_1_5 - a2 * (F_1_7 - a2 * (F_1_9 - a2 * (F_1_11 - a2 * (F_1_13 - a2 * (F_1_15 - a2 * F_1_17 * F_15_16) * F_13_14) * F_11_12) * F_9_10) * F_7_8) * F_5_6) * F_3_4) * F_1_2);
                } else if (a > 0.036) {
                    absAsinh = a * (1 - a2 * (F_1_3 - a2 * (F_1_5 - a2 * (F_1_7 - a2 * (F_1_9 - a2 * (F_1_11 - a2 * F_1_13 * F_11_12) * F_9_10) * F_7_8) * F_5_6) * F_3_4) * F_1_2);
                } else if (a > 0.0036) {
                    absAsinh = a * (1 - a2 * (F_1_3 - a2 * (F_1_5 - a2 * (F_1_7 - a2 * F_1_9 * F_7_8) * F_5_6) * F_3_4) * F_1_2);
                } else {
                    absAsinh = a * (1 - a2 * (F_1_3 - a2 * F_1_5 * F_3_4) * F_1_2);
                }
            }

            return negative ? -absAsinh : absAsinh;
        }

        public static double atanh(double a) {
            boolean negative = false;
            if (a < 0) {
                negative = true;
                a = -a;
            }

            double absAtanh;
            if (a > 0.15) {
                absAtanh = 0.5 * Math.log((1 + a) / (1 - a));
            } else {
                final double a2 = a * a;
                if (a > 0.087) {
                    absAtanh = a * (1 + a2 * (F_1_3 + a2 * (F_1_5 + a2 * (F_1_7 + a2 * (F_1_9 + a2 * (F_1_11 + a2 * (F_1_13 + a2 * (F_1_15 + a2 * F_1_17))))))));
                } else if (a > 0.031) {
                    absAtanh = a * (1 + a2 * (F_1_3 + a2 * (F_1_5 + a2 * (F_1_7 + a2 * (F_1_9 + a2 * (F_1_11 + a2 * F_1_13))))));
                } else if (a > 0.003) {
                    absAtanh = a * (1 + a2 * (F_1_3 + a2 * (F_1_5 + a2 * (F_1_7 + a2 * F_1_9))));
                } else {
                    absAtanh = a * (1 + a2 * (F_1_3 + a2 * F_1_5));
                }
            }

            return negative ? -absAtanh : absAtanh;
        }
    }

    public static double asinh(double x) {
        return AsinhAtanh.asinh(x);
    }

    public static double acosh(double x) {
        return Math.log(x + Math.sqrt(x * x - 1));
    }

    public static double atanh(double x) {
        return AsinhAtanh.atanh(x);
    }

    public static double invsqrt(double a) {
        return 1.0 / Math.sqrt(a);
    }

    public static float lerp(float a, float b, float t) {
        return FastMath.fma(b - a, t, a);
    }

    public static double lerp(double a, double b, double t) {
        return FastMath.fma(b - a, t, a);
    }

    public static float biLerp(float q00, float q10, float q01, float q11, float tx, float ty) {
        float lerpX1 = lerp(q00, q10, tx);
        float lerpX2 = lerp(q01, q11, tx);
        return lerp(lerpX1, lerpX2, ty);
    }

    public static double biLerp(double q00, double q10, double q01, double q11, double tx, double ty) {
        double lerpX1 = lerp(q00, q10, tx);
        double lerpX2 = lerp(q01, q11, tx);
        return lerp(lerpX1, lerpX2, ty);
    }

    public static float triLerp(float q000, float q100, float q010, float q110, float q001, float q101, float q011, float q111, float tx, float ty, float tz) {
        float x00 = lerp(q000, q100, tx);
        float x10 = lerp(q010, q110, tx);
        float x01 = lerp(q001, q101, tx);
        float x11 = lerp(q011, q111, tx);
        float y0 = lerp(x00, x10, ty);
        float y1 = lerp(x01, x11, ty);
        return lerp(y0, y1, tz);
    }

    public static double triLerp(double q000, double q100, double q010, double q110, double q001, double q101, double q011, double q111, double tx, double ty, double tz) {
        double x00 = lerp(q000, q100, tx);
        double x10 = lerp(q010, q110, tx);
        double x01 = lerp(q001, q101, tx);
        double x11 = lerp(q011, q111, tx);
        double y0 = lerp(x00, x10, ty);
        double y1 = lerp(x01, x11, ty);
        return lerp(y0, y1, tz);
    }

    public static double down(double a) {
        return (a > 0) ? Math.ceil(a) : Math.floor(a);
    }

    public static double up(double a) {
        return (a > 0) ? Math.floor(a) : Math.ceil(a);
    }

    public static double halfDown(double a) {
        return (a > 0) ? Math.ceil(a - 0.5d) : Math.floor(a + 0.5d);
    }

    public static double halfUp(double a) {
        return (a > 0) ? Math.floor(a + 0.5d) : Math.ceil(a - 0.5d);
    }

    public static int clz(int value) {
        return Integer.numberOfLeadingZeros(value);
    }

    public static int clz(long value) {
        return Long.numberOfLeadingZeros(value);
    }

    public static int clon(int value) {
        return Integer.numberOfLeadingZeros(~value);
    }

    public static int clon(long value) {
        return Long.numberOfLeadingZeros(~value);
    }

    public static int ctz(int value) {
        return Integer.numberOfTrailingZeros(value);
    }

    public static int ctz(long value) {
        return Long.numberOfTrailingZeros(value);
    }

    public static int cton(int value) {
        return Integer.numberOfTrailingZeros(~value);
    }

    public static int cton(long value) {
        return Long.numberOfTrailingZeros(~value);
    }

    public static double log(double base, double x) {
        return Math.log(x) / Math.log(base);
    }

    public static boolean isPowerOfTwo(int value) {
        return (value > 0) && (value & (value - 1)) == 0;
    }

    public static boolean isPowerOfTwo(long value) {
        return (value > 0) && (value & (value - 1)) == 0;
    }

    public static int nearestPowerOfTwo(int value) {
        if (value <= 0) return 0;
        value--;
        value |= value >>> 1;
        value |= value >>> 2;
        value |= value >>> 4;
        value |= value >>> 8;
        value |= value >>> 16;
        value++;
        return value < 0 ? value >>> 1 : value;
    }

    public static long nearestPowerOfTwo(long value) {
        if (value <= 0) return 0;
        long i = Long.highestOneBit(value);
        value = value > i ? i << 1 : i;
        return value < 0 ? value >>> 1 : value;
    }

    public static boolean unsignedIsPowerOfTwo(int value) {
        return (value != 0) && (value & (value - 1)) == 0;
    }

    public static boolean unsignedIsPowerOfTwo(long value) {
        return (value != 0) && (value & (value - 1)) == 0;
    }

    public static int unsignedNearestPowerOfTwo(int value) {
        value--;
        value |= value >>> 1;
        value |= value >>> 2;
        value |= value >>> 4;
        value |= value >>> 8;
        value |= value >>> 16;
        value++;
        return value;
    }

    public static long unsignedNearestPowerOfTwo(long value) {
        long i = Long.highestOneBit(value);
        return value > i ? i << 1 : i;
    }

    public static double sq(double a) {
        return a * a;
    }

    public static long sq(int a) {
        return (long) a * a;
    }

    public static float normalize(float value, float min, float max) {
        if (Float.isInfinite(value) || Float.isNaN(value)) return Float.NaN;
        float range = max - min;
        while (value > max) value -= range;
        while (value < min) value += range;
        return value;
    }

    public static double normalize(double value, double min, double max) {
        if (Double.isInfinite(value) || Double.isNaN(value)) return Double.NaN;
        double range = max - min;
        while (value > max) value -= range;
        while (value < min) value += range;
        return value;
    }

    public static float saturate(float value) {
        return ExtendedMath.clamp(value, 0f, 1f);
    }

    public static double saturate(double value) {
        return ExtendedMath.clamp(value, 0d, 1d);
    }

    public static double interpolateLinear(double scale, double min, double max) {
        if (Double.isNaN(scale) || Double.isNaN(min) || Double.isNaN(max)) return Double.NaN;
        if (Double.doubleToRawLongBits(min) == Double.doubleToRawLongBits(max)) return min;
        if (scale <= 0d) return min;
        if (scale >= 1d) return max;
        return (1d - scale) * min + scale * max;
    }

    public static double extrapolateLinear(double scale, double min, double max) {
        if (Double.isNaN(scale) || Double.isNaN(min) || Double.isNaN(max)) return Double.NaN;
        return (1d - scale) * min + scale * max;
    }

    public static double unInterpolateLinear(double value, double min, double max) {
        if (Double.isNaN(value) || Double.isNaN(min) || Double.isNaN(max)) return Double.NaN;
        return (value - min) / (max - min);
    }

    public static double interpolateBezier(double u, double p0, double p1, double p2, double p3) {
        double oneMinusU = 1.0 - u;
        double oneMinusU2 = oneMinusU * oneMinusU;
        double u2 = u * u;

        return p0 * oneMinusU2 * oneMinusU
                + 3.0 * p1 * u * oneMinusU2
                + 3.0 * p2 * u2 * oneMinusU
                + p3 * u2 * u;
    }

    public static double interpolateCatmullRom(double u, double t, double p0, double p1, double p2, double p3) {
        double c1, c2, c3, c4;
        c1 = p1;
        c2 = -1.0 * t * p0 + t * p2;
        c3 = 2 * t * p0 + (t - 3) * p1 + (3 - 2 * t) * p2 + -t * p3;
        c4 = -t * p0 + (2 - t) * p1 + (t - 2) * p2 + t * p3;

        return ((c4 * u + c3) * u + c2) * u + c1;
    }

    public static int gcd(int p, int q) {
        int a = p;
        int b = q;
        if (a == 0 ||
                b == 0) {
            if (a == Integer.MIN_VALUE || b == Integer.MIN_VALUE) throw new ArithmeticException("integer overflow");
            return Math.abs(a + b);
        }

        long al = a;
        long bl = b;
        boolean useLong = false;
        if (a < 0) {
            if (Integer.MIN_VALUE == a) {
                useLong = true;
            } else {
                a = -a;
            }
            al = -al;
        }
        if (b < 0) {
            if (Integer.MIN_VALUE == b) {
                useLong = true;
            } else {
                b = -b;
            }
            bl = -bl;
        }
        if (useLong) {
            if (al == bl) throw new ArithmeticException("integer overflow");
            long blbu = bl;
            bl = al;
            al = blbu % al;
            if (al == 0) {
                if (bl > Integer.MAX_VALUE) throw new ArithmeticException("integer overflow");
                return (int) bl;
            }
            blbu = bl;

            // Now "al" and "bl" fit in an "int".
            b = (int) al;
            a = (int) (blbu % al);
        }

        return gcdPositive(a, b);
    }

    private static int gcdPositive(int a, int b) {
        if (a == 0) return b;
        else if (b == 0) return a;

        // Make "a" and "b" odd, keeping track of common power of 2.
        final int aTwos = Integer.numberOfTrailingZeros(a);
        a >>= aTwos;
        final int bTwos = Integer.numberOfTrailingZeros(b);
        b >>= bTwos;
        final int shift = Math.min(aTwos, bTwos);

        // "a" and "b" are positive.
        // If a > b then "gdc(a, b)" is equal to "gcd(a - b, b)".
        // If a < b then "gcd(a, b)" is equal to "gcd(b - a, a)".
        // Hence, in the successive iterations:
        //  "a" becomes the absolute difference of the current values,
        //  "b" becomes the minimum of the current values.
        while (a != b) {
            final int delta = a - b;
            b = Math.min(a, b);
            a = Math.abs(delta);

            // Remove any power of 2 in "a" ("b" is guaranteed to be odd).
            a >>= Integer.numberOfTrailingZeros(a);
        }

        // Recover the common power of 2.
        return a << shift;
    }

    public static long gcd(long p, long q) {
        long u = p;
        long v = q;
        if ((u == 0) || (v == 0)) {
            if ((u == Long.MIN_VALUE) || (v == Long.MIN_VALUE)) throw new ArithmeticException("long overflow");
            return Math.abs(u) + Math.abs(v);
        }
        // keep u and v negative, as negative integers range down to
        // -2^63, while positive numbers can only be as large as 2^63-1
        // (i.e. we can't necessarily negate a negative number without
        // overflow)
        /* assert u!=0 && v!=0; */
        if (u > 0) {
            u = -u;
        } // make u negative
        if (v > 0) {
            v = -v;
        } // make v negative
        // B1. [Find power of 2]
        int k = 0;
        while ((u & 1) == 0 && (v & 1) == 0 && k < 63) { // while u and v are
            // both even...
            u /= 2;
            v /= 2;
            k++; // cast out twos.
        }
        if (k == 63) throw new ArithmeticException("long overflow");
        // B2. Initialize: u and v have been divided by 2^k and at least
        // one is odd.
        long t = ((u & 1) == 1) ? v : -(u / 2)/* B3 */;
        // t negative: u was odd, v may be even (t replaces v)
        // t positive: u was even, v is odd (t replaces u)
        do {
            /* assert u<0 && v<0; */
            // B4/B3: cast out twos from t.
            while ((t & 1) == 0) { // while t is even..
                t /= 2; // cast out twos
            }
            // B5 [reset max(u,v)]
            if (t > 0) {
                u = -t;
            } else {
                v = t;
            }
            // B6/B3. at this point both u and v should be odd.
            t = (v - u) / 2;
            // |u| larger: t positive (replace u)
            // |v| larger: t negative (replace v)
        } while (t != 0);
        return -u * (1L << k); // gcd is u*2^k
    }

    public static int lcm(int a, int b) {
        if (a == 0 || b == 0) return 0;
        int lcm = Math.abs(Math.multiplyExact(a / gcd(a, b), b));
        if (lcm == Integer.MIN_VALUE) throw new ArithmeticException("integer overflow");
        return lcm;
    }

    public static long lcm(long a, long b) {
        if (a == 0 || b == 0) return 0;
        long lcm = Math.abs(Math.multiplyExact(a / gcd(a, b), b));
        if (lcm == Long.MIN_VALUE) throw new ArithmeticException("long overflow");
        return lcm;
    }

    public static boolean equals(float a, float b, float delta) {
        return Float.compare(a, b) == 0 || Math.abs(a - b) <= delta;
    }

    public static boolean equals(double a, double b, double delta) {
        return Double.compare(a, b) == 0 || Math.abs(a - b) <= delta;
    }

    public static BigInteger unsign(long x) {
        return Unsign.compute(x);
    }

    private static final class Unsign {
        private Unsign() {
            throw new NotInstantiableError(Unsign.class);
        }

        private static final BigInteger UNSIGNED_LONG_MASK = BigInteger.ONE.shiftLeft(Long.SIZE).subtract(BigInteger.ONE);

        public static BigInteger compute(long x) {
            if (x >= 0) return BigInteger.valueOf(x);
            else return BigInteger.valueOf(x).and(UNSIGNED_LONG_MASK);
        }
    }

    public static int divideUnsigned(int dividend, int divisor) {
        return Integer.divideUnsigned(dividend, divisor);
    }

    public static int remainderUnsigned(int dividend, int divisor) {
        return Integer.remainderUnsigned(dividend, divisor);
    }

    public static long divideUnsigned(long dividend, long divisor) {
        return Long.divideUnsigned(dividend, divisor);
    }

    public static long remainderUnsigned(long dividend, long divisor) {
        return Long.remainderUnsigned(dividend, divisor);
    }

    public static int add(int a, int b) {
        return Integer.sum(a, b);
    }

    public static long add(long a, long b) {
        return Long.sum(a, b);
    }

    public static float add(float a, float b) {
        return Float.sum(a, b);
    }

    public static double add(double a, double b) {
        return Double.sum(a, b);
    }

}
