package unrefined.math;

import unrefined.util.NotInstantiableError;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

public final class FastMath {

    private FastMath() {
        throw new NotInstantiableError(FastMath.class);
    }

    public static final double E = Math.E;
    public static final double PI = Math.PI;
    public static final double PI_HALF = PI * 0.5;
    public static final double TAU = 2.0 * PI;

    public static final class RoundingMode {
        private RoundingMode() {
            throw new NotInstantiableError(RoundingMode.class);
        }
        public static final int UP = BigDecimal.ROUND_UP;
        public static final int DOWN = BigDecimal.ROUND_DOWN;
        public static final int CEILING = BigDecimal.ROUND_CEILING;
        public static final int FLOOR = BigDecimal.ROUND_FLOOR;
        public static final int HALF_UP = BigDecimal.ROUND_HALF_UP;
        public static final int HALF_DOWN = BigDecimal.ROUND_HALF_DOWN;
        public static final int HALF_EVEN = BigDecimal.ROUND_HALF_EVEN;
        public static final int UNNECESSARY = BigDecimal.ROUND_UNNECESSARY;
        public static boolean isValid(int mode) {
            return mode >= UP && mode <= UNNECESSARY;
        }
        public static int checkValid(int mode) {
            if (mode < UP || mode > UNNECESSARY) throw new IllegalArgumentException("Illegal rounding mode: " + mode);
            else return mode;
        }
        public static String toString(int mode) {
            switch (mode) {
                case UP: return "UP";
                case DOWN: return "DOWN";
                case CEILING: return "CEILING";
                case FLOOR: return "FLOOR";
                case HALF_UP: return "HALF_UP";
                case HALF_DOWN: return "HALF_DOWN";
                case HALF_EVEN: return "HALF_EVEN";
                case UNNECESSARY: return "UNNECESSARY";
                default: throw new IllegalArgumentException("Illegal rounding mode: " + mode);
            }
        }
    }

    public static float sin(float a) {
        return (float) Math.sin(a);
    }

    public static double sin(double a) {
        return Math.sin(a);
    }

    public static float cos(float a) {
        return (float) Math.cos(a);
    }

    public static double cos(double a) {
        return Math.cos(a);
    }

    public static float cosFromSin(float sin, float angle) {
        return (float) ExtendedMath.cosFromSin(sin, angle);
    }

    public static double cosFromSin(double sin, double angle) {
        return ExtendedMath.cosFromSin(sin, angle);
    }

    public static float tan(float a) {
        return (float) Math.tan(a);
    }

    public static double tan(double a) {
        return Math.tan(a);
    }

    public static float asin(float a) {
        return (float) Math.asin(a);
    }

    public static double asin(double a) {
        return Math.asin(a);
    }

    public static float safeAsin(float a) {
        return (float) ExtendedMath.safeAsin(a);
    }

    public static double safeAsin(double a) {
        return ExtendedMath.safeAsin(a);
    }

    public static float acos(float a) {
        return (float) Math.acos(a);
    }

    public static double acos(double a) {
        return Math.acos(a);
    }

    public static float safeAcos(float a) {
        return (float) ExtendedMath.safeAcos(a);
    }

    public static double safeAcos(double a) {
        return ExtendedMath.safeAcos(a);
    }

    public static float atan(float a) {
        return (float) Math.atan(a);
    }

    public static double atan(double a) {
        return Math.atan(a);
    }


    public static float toRadians(float degrees) {
        return (float) Math.toRadians(degrees);
    }


    public static double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }


    public static float toDegrees(float radians) {
        return (float) Math.toDegrees(radians);
    }


    public static double toDegrees(double radians) {
        return Math.toDegrees(radians);
    }


    public static float exp(float a) {
        return (float) Math.exp(a);
    }


    public static double exp(double a) {
        return Math.exp(a);
    }


    public static float log(float a) {
        return (float) Math.log(a);
    }


    public static double log(double a) {
        return Math.log(a);
    }


    public static float log10(float a) {
        return (float) Math.log10(a);
    }

    public static double log10(double a) {
        return Math.log10(a);
    }

    public static float log2(float a) {
        return (float) ExtendedMath.log2(a);
    }

    public static double log2(double a) {
        return ExtendedMath.log2(a);
    }

    public static float sqrt(float a) {
        return (float) Math.sqrt(a);
    }

    public static double sqrt(double a) {
        return Math.sqrt(a);
    }

    public static float cbrt(float a) {
        return (float) Math.cbrt(a);
    }

    public static double cbrt(double a) {
        return Math.cbrt(a);
    }

    public static double IEEEremainder(double dividend, double divisor) {
        return Math.IEEEremainder(dividend, divisor);
    }

    public static float ceil(float a) {
        return (float) Math.ceil(a);
    }

    public static double ceil(double a) {
        return Math.ceil(a);
    }

    public static float floor(float a) {
        return (float) Math.floor(a);
    }

    public static double floor(double a) {
        return Math.floor(a);
    }

    public static float rint(float a) {
        return (float) Math.rint(a);
    }

    public static double rint(double a) {
        return Math.rint(a);
    }

    public static float atan2(float y, float x) {
        return (float) Math.atan2(y, x);
    }

    public static double atan2(double y, double x) {
        return Math.atan2(y, x);
    }

    public static float pow(float base, float exponent) {
        return (float) Math.pow(base, exponent);
    }

    public static double pow(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    public static int round(float value) {
        return Math.round(value);
    }

    public static long round(double value) {
        return Math.round(value);
    }

    public static float randomFloat(float bound) {
        return ExtendedMath.randomFloat(bound);
    }

    public static float randomFloat(float min, float max) {
        return ExtendedMath.randomFloat(min, max);
    }

    public static float randomFloat() {
        return ExtendedMath.randomFloat();
    }

    public static double randomDouble(double bound) {
        return ExtendedMath.randomDouble(bound);
    }

    public static double randomDouble(double min, double max) {
        return ExtendedMath.randomDouble(min, max);
    }

    public static double randomDouble() {
        return ExtendedMath.randomDouble();
    }

    public static int randomInt(int bound) {
        return ExtendedMath.randomInt(bound);
    }

    public static int randomInt(int min, int max) {
        return ExtendedMath.randomInt(min, max);
    }

    public static int randomInt() {
        return ExtendedMath.randomInt();
    }

    public static long randomLong(long bound) {
        return ExtendedMath.randomLong(bound);
    }

    public static long randomLong(long min, long max) {
        return ExtendedMath.randomLong(min, max);
    }

    public static long randomLong() {
        return ExtendedMath.randomLong();
    }

    public static int addExact(int x, int y) {
        return Math.addExact(x, y);
    }

    public static long addExact(long x, long y) {
        return Math.addExact(x, y);
    }

    public static int subtractExact(int x, int y) {
        return Math.subtractExact(x, y);
    }

    public static long subtractExact(long x, long y) {
        return Math.subtractExact(x, y);
    }

    public static int multiplyExact(int x, int y) {
        return Math.multiplyExact(x, y);
    }

    public static long multiplyExact(long x, long y) {
        return Math.multiplyExact(x, y);
    }

    public static int divideExact(int x, int y) {
        return ExtendedMath.divideExact(x, y);
    }

    public static long divideExact(long x, long y) {
        return ExtendedMath.divideExact(x, y);
    }

    public static int floorDivExact(int x, int y) {
        return ExtendedMath.floorDivExact(x, y);
    }

    public static long floorDivExact(long x, long y) {
        return ExtendedMath.floorDivExact(x, y);
    }

    public static int ceilDivExact(int x, int y) {
        return ExtendedMath.ceilDivExact(x, y);
    }

    public static long ceilDivExact(long x, long y) {
        return ExtendedMath.ceilDivExact(x, y);
    }

    public static int incrementExact(int a) {
        return Math.incrementExact(a);
    }

    public static long incrementExact(long a) {
        return Math.incrementExact(a);
    }

    public static int decrementExact(int a) {
        return Math.decrementExact(a);
    }

    public static long decrementExact(long a) {
        return Math.decrementExact(a);
    }

    public static int negateExact(int a) {
        return Math.negateExact(a);
    }

    public static long negateExact(long a) {
        return Math.negateExact(a);
    }

    public static int toIntExact(long value) {
        return Math.toIntExact(value);
    }

    public static long multiplyFull(int x, int y) {
        return ExtendedMath.multiplyFull(x, y);
    }

    public static long multiplyHigh(long x, long y) {
        return ExtendedMath.multiplyHigh(x, y);
    }

    public static long unsignedMultiplyHigh(long x, long y) {
        return ExtendedMath.unsignedMultiplyHigh(x, y);
    }

    public static int floorDiv(int x, int y) {
        return Math.floorDiv(x, y);
    }

    public static long floorDiv(long x, long y) {
        return Math.floorDiv(x, y);
    }

    public static int floorMod(int x, int y) {
        return Math.floorMod(x, y);
    }

    public static long floorMod(long x, long y) {
        return Math.floorMod(x, y);
    }

    public static int ceilDiv(int x, int y) {
        return ExtendedMath.ceilDiv(x, y);
    }

    public static long ceilDiv(long x, long y) {
        return ExtendedMath.ceilDiv(x, y);
    }

    public static int ceilMod(int x, int y) {
        return ExtendedMath.ceilMod(x, y);
    }

    public static long ceilMod(long x, long y) {
        return ExtendedMath.ceilMod(x, y);
    }

    public static int abs(int a) {
        return Math.abs(a);
    }

    public static int absExact(int a) {
        return ExtendedMath.absExact(a);
    }

    public static long abs(long a) {
        return Math.abs(a);
    }

    public static long absExact(long a) {
        return ExtendedMath.absExact(a);
    }

    public static float abs(float a) {
        return Math.abs(a);
    }

    public static double abs(double a) {
        return Math.abs(a);
    }

    public static int max(int a, int b) {
        return Math.max(a, b);
    }

    public static long max(long a, long b) {
        return Math.max(a, b);
    }

    public static float max(float a, float b) {
        return Math.max(a, b);
    }

    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    public static int max(int a, int b, int c) {
        return Math.max(a, Math.max(b, c));
    }

    public static long max(long a, long b, long c) {
        return Math.max(a, Math.max(b, c));
    }

    public static float max(float a, float b, float c) {
        return Math.max(a, Math.max(b, c));
    }

    public static double max(double a, double b, double c) {
        return Math.max(a, Math.max(b, c));
    }

    public static int min(int a, int b) {
        return Math.min(a, b);
    }

    public static long min(long a, long b) {
        return Math.min(a, b);
    }

    public static float min(float a, float b) {
        return Math.min(a, b);
    }

    public static double min(double a, double b) {
        return Math.min(a, b);
    }

    public static int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }

    public static long min(long a, long b, long c) {
        return Math.min(a, Math.min(b, c));
    }

    public static float min(float a, float b, float c) {
        return Math.min(a, Math.min(b, c));
    }

    public static double min(double a, double b, double c) {
        return Math.min(a, Math.min(b, c));
    }

    public static int clamp(int value, int min, int max) {
        return ExtendedMath.clamp(value, min, max);
    }

    public static int clamp(long value, int min, int max) {
        return ExtendedMath.clamp(value, min, max);
    }

    public static long clamp(long value, long min, long max) {
        return ExtendedMath.clamp(value, min, max);
    }

    public static float clamp(float value, float min, float max) {
        return ExtendedMath.clamp(value, min, max);
    }

    public static double clamp(double value, double min, double max) {
        return ExtendedMath.clamp(value, min, max);
    }

    public static float fma(float a, float b, float c) {
        return ExtendedMath.fma(a, b, c);
    }

    public static double fma(double a, double b, double c) {
        return ExtendedMath.fma(a, b, c);
    }

    public static float ulp(float f) {
        return Math.ulp(f);
    }

    public static double ulp(double d) {
        return Math.ulp(d);
    }

    public static int signum(int value) {
        return ExtendedMath.signum(value);
    }

    public static long signum(long value) {
        return ExtendedMath.signum(value);
    }

    public static float signum(float value) {
        return Math.signum(value);
    }

    public static double signum(double value) {
        return Math.signum(value);
    }

    public static float sinh(float x) {
        return (float) Math.sinh(x);
    }

    public static double sinh(double x) {
        return Math.sinh(x);
    }

    public static float cosh(float x) {
        return (float) Math.cosh(x);
    }

    public static double cosh(double x) {
        return Math.cosh(x);
    }

    public static float tanh(float x) {
        return (float) Math.tanh(x);
    }

    public static double tanh(double x) {
        return Math.tanh(x);
    }

    public static float asinh(float x) {
        return (float) ExtendedMath.asinh(x);
    }

    public static double asinh(double x) {
        return ExtendedMath.asinh(x);
    }

    public static float acosh(float x) {
        return (float) ExtendedMath.acosh(x);
    }

    public static double acosh(double x) {
        return ExtendedMath.acosh(x);
    }

    public static float atanh(float x) {
        return (float) ExtendedMath.atanh(x);
    }

    public static double atanh(double x) {
        return ExtendedMath.atanh(x);
    }

    public static float hypot(float x, float y) {
        return (float) Math.hypot(x, y);
    }

    public static double hypot(double x, double y) {
        return Math.hypot(x, y);
    }

    public static float expm1(float x) {
        return (float) Math.expm1(x);
    }

    public static double expm1(double x) {
        return Math.expm1(x);
    }

    public static float log1p(float x) {
        return (float) Math.log1p(x);
    }

    public static double log1p(double x) {
        return Math.log1p(x);
    }

    public static float copySign(float magnitude, float sign) {
        return Math.copySign(magnitude, sign);
    }

    public static double copySign(double magnitude, double sign) {
        return Math.copySign(magnitude, sign);
    }

    public static int getExponent(float value) {
        return Math.getExponent(value);
    }

    public static int getExponent(double value) {
        return Math.getExponent(value);
    }

    public static float nextAfter(float start, double direction) {
        return Math.nextAfter(start, direction);
    }

    public static double nextAfter(double start, double direction) {
        return Math.nextAfter(start, direction);
    }

    public static float nextUp(float f) {
        return Math.nextUp(f);
    }

    public static double nextUp(double d) {
        return Math.nextUp(d);
    }

    public static float nextDown(float f) {
        return Math.nextDown(f);
    }

    public static double nextDown(double d) {
        return Math.nextDown(d);
    }

    public static float scalb(float f, int scaleFactor) {
        return Math.scalb(f, scaleFactor);
    }

    public static double scalb(double d, int scaleFactor) {
        return Math.scalb(d, scaleFactor);
    }

    public static float invsqrt(float a) {
        return (float) ExtendedMath.invsqrt(a);
    }

    public static double invsqrt(double a) {
        return ExtendedMath.invsqrt(a);
    }

    public static float lerp(float a, float b, float t) {
        return ExtendedMath.lerp(a, b, t);
    }

    public static double lerp(double a, double b, double t) {
        return ExtendedMath.lerp(a, b, t);
    }

    public static float biLerp(float q00, float q10, float q01, float q11, float tx, float ty) {
        return ExtendedMath.biLerp(q00, q10, q01, q11, tx, ty);
    }

    public static double biLerp(double q00, double q10, double q01, double q11, double tx, double ty) {
        return ExtendedMath.biLerp(q00, q10, q01, q11, tx, ty);
    }

    public static float triLerp(float q000, float q100, float q010, float q110, float q001, float q101, float q011, float q111, float tx, float ty, float tz) {
        return ExtendedMath.triLerp(q000, q100, q010, q110, q001, q101, q011, q111, tx, ty, tz);
    }

    public static double triLerp(double q000, double q100, double q010, double q110, double q001, double q101, double q011, double q111, double tx, double ty, double tz) {
        return ExtendedMath.triLerp(q000, q100, q010, q110, q001, q101, q011, q111, tx, ty, tz);
    }

    public static float round(float value, int mode) {
        switch (mode) {
            case RoundingMode.UP: return (float) ExtendedMath.up(value);
            case RoundingMode.DOWN: return (float) ExtendedMath.down(value);
            case RoundingMode.CEILING: return (float) Math.ceil(value);
            case RoundingMode.FLOOR: return (float) Math.floor(value);
            case RoundingMode.HALF_UP: return (float) ExtendedMath.halfUp(value);
            case RoundingMode.HALF_DOWN: return (float) ExtendedMath.halfDown(value);
            case RoundingMode.HALF_EVEN: return (float) Math.rint(value);
            case RoundingMode.UNNECESSARY: return value;
            default: throw new IllegalArgumentException("Illegal rounding mode: " + mode);
        }
    }

    public static double round(double value, int mode) {
        switch (mode) {
            case RoundingMode.UP: return ExtendedMath.up(value);
            case RoundingMode.DOWN: return ExtendedMath.down(value);
            case RoundingMode.CEILING: return Math.ceil(value);
            case RoundingMode.FLOOR: return Math.floor(value);
            case RoundingMode.HALF_UP: return ExtendedMath.halfUp(value);
            case RoundingMode.HALF_DOWN: return ExtendedMath.halfDown(value);
            case RoundingMode.HALF_EVEN : return Math.rint(value);
            case RoundingMode.UNNECESSARY: return value;
            default: throw new IllegalArgumentException("Illegal rounding mode: " + mode);
        }
    }

    public static float down(float a) {
        return (float) ExtendedMath.down(a);
    }

    public static double down(double a) {
        return ExtendedMath.down(a);
    }

    public static float up(float a) {
        return (float) ExtendedMath.up(a);
    }

    public static double up(double a) {
        return ExtendedMath.up(a);
    }

    public static float halfDown(float a) {
        return (float) ExtendedMath.halfDown(a);
    }

    public static double halfDown(double a) {
        return ExtendedMath.halfDown(a);
    }

    public static float halfUp(float a) {
        return (float) ExtendedMath.halfUp(a);
    }

    public static double halfUp(double a) {
        return ExtendedMath.halfUp(a);
    }

    public static int clz(int value) {
        return ExtendedMath.clz(value);
    }

    public static int clz(long value) {
        return ExtendedMath.clz(value);
    }

    public static int clon(int value) {
        return ExtendedMath.clon(value);
    }

    public static int clon(long value) {
        return ExtendedMath.clon(value);
    }

    public static int ctz(int value) {
        return ExtendedMath.ctz(value);
    }

    public static int ctz(long value) {
        return ExtendedMath.ctz(value);
    }

    public static int cton(int value) {
        return ExtendedMath.cton(value);
    }

    public static int cton(long value) {
        return ExtendedMath.cton(value);
    }

    public static float log(float base, float x) {
        return (float) ExtendedMath.log(base, x);
    }

    public static double log(double base, double x) {
        return ExtendedMath.log(base, x);
    }

    public static boolean isPowerOfTwo(int value) {
        return ExtendedMath.isPowerOfTwo(value);
    }

    public static boolean isPowerOfTwo(long value) {
        return ExtendedMath.isPowerOfTwo(value);
    }

    public static int nearestPowerOfTwo(int value) {
        return ExtendedMath.nearestPowerOfTwo(value);
    }

    public static long nearestPowerOfTwo(long value) {
        return ExtendedMath.nearestPowerOfTwo(value);
    }

    public static boolean unsignedIsPowerOfTwo(int value) {
        return ExtendedMath.unsignedIsPowerOfTwo(value);
    }

    public static boolean unsignedIsPowerOfTwo(long value) {
        return ExtendedMath.unsignedIsPowerOfTwo(value);
    }

    public static int unsignedNearestPowerOfTwo(int value) {
        return ExtendedMath.unsignedNearestPowerOfTwo(value);
    }

    public static long unsignedNearestPowerOfTwo(long value) {
        return ExtendedMath.unsignedNearestPowerOfTwo(value);
    }

    public static float sq(float a) {
        return (float) ExtendedMath.sq(a);
    }

    public static double sq(double a) {
        return ExtendedMath.sq(a);
    }

    public static long sq(int a) {
        return ExtendedMath.sq(a);
    }

    public static float normalize(float value, float min, float max) {
        return ExtendedMath.normalize(value, min, max);
    }

    public static double normalize(double value, double min, double max) {
        return ExtendedMath.normalize(value, min, max);
    }

    public static float saturate(float value) {
        return ExtendedMath.saturate(value);
    }

    public static double saturate(double value) {
        return ExtendedMath.saturate(value);
    }

    public static float interpolateLinear(float scale, float min, float max) {
        return (float) ExtendedMath.interpolateLinear(scale, min, max);
    }

    public static double interpolateLinear(double scale, double min, double max) {
        return ExtendedMath.interpolateLinear(scale, min, max);
    }

    public static float extrapolateLinear(float scale, float min, float max) {
        return (float) ExtendedMath.extrapolateLinear(scale, min, max);
    }

    public static double extrapolateLinear(double scale, double min, double max) {
        return ExtendedMath.extrapolateLinear(scale, min, max);
    }

    public static float unInterpolateLinear(float value, float min, float max) {
        return (float) ExtendedMath.unInterpolateLinear(value, min, max);
    }

    public static double unInterpolateLinear(double value, double min, double max) {
        return ExtendedMath.unInterpolateLinear(value, min, max);
    }

    public static float interpolateBezier(float u, float p0, float p1, float p2, float p3) {
        return (float) ExtendedMath.interpolateBezier(u, p0, p1, p2, p3);
    }

    public static double interpolateBezier(double u, double p0, double p1, double p2, double p3) {
        return ExtendedMath.interpolateBezier(u, p0, p1, p2, p3);
    }

    public static float interpolateCatmullRom(float u, float t, float p0, float p1, float p2, float p3) {
        return (float) ExtendedMath.interpolateCatmullRom(u, t, p0, p1, p2, p3);
    }

    public static double interpolateCatmullRom(double u, double t, double p0, double p1, double p2, double p3) {
        return ExtendedMath.interpolateCatmullRom(u, t, p0, p1, p2, p3);
    }

    public static int gcd(int a, int b) {
        return ExtendedMath.gcd(a, b);
    }

    public static long gcd(long a, long b) {
        return ExtendedMath.gcd(a, b);
    }

    public static int lcm(int a, int b) {
        return ExtendedMath.lcm(a, b);
    }

    public static long lcm(long a, long b) {
        return ExtendedMath.lcm(a, b);
    }

    public static boolean equals(float a, float b, float delta) {
        return ExtendedMath.equals(a, b, delta);
    }

    public static boolean equals(double a, double b, double delta) {
        return ExtendedMath.equals(a, b, delta);
    }

    public static short unsign(byte x) {
        return (short) Byte.toUnsignedInt(x);
    }

    public static int unsign(short x) {
        return Short.toUnsignedInt(x);
    }

    public static long unsign(int x) {
        return Integer.toUnsignedLong(x);
    }

    public static BigInteger unsign(long x) {
        return ExtendedMath.unsign(x);
    }

    private static final class ExtendedMath {

        private ExtendedMath() {
            throw new NotInstantiableError(ExtendedMath.class);
        }

        public static long multiplyFull(int x, int y) {
            return (long)x * (long)y;
        }

        public static long multiplyHigh(long x, long y) {
            // Use technique from section 8-2 of Henry S. Warren, Jr.,
            // Hacker's Delight (2nd ed.) (Addison Wesley, 2013), 173-174.
            long x1 = x >> 32;
            long x2 = x & 0xFFFFFFFFL;
            long y1 = y >> 32;
            long y2 = y & 0xFFFFFFFFL;

            long z2 = x2 * y2;
            long t = x1 * y2 + (z2 >>> 32);
            long z1 = t & 0xFFFFFFFFL;
            long z0 = t >> 32;
            z1 += x2 * y1;

            return x1 * y1 + z0 + (z1 >> 32);
        }

        public static long unsignedMultiplyHigh(long x, long y) {
            // Compute via multiplyHigh() to leverage the intrinsic
            long result = ExtendedMath.multiplyHigh(x, y);
            result += (y & (x >> 63)); // equivalent to `if (x < 0) result += y;`
            result += (x & (y >> 63)); // equivalent to `if (y < 0) result += x;`
            return result;
        }

        public static double fma(double a, double b, double c) {
            /*
             * Infinity and NaN arithmetic is not quite the same with two
             * roundings as opposed to just one so the simple expression
             * "a * b + c" cannot always be used to compute the correct
             * result.  With two roundings, the product can overflow and
             * if the addend is infinite, a spurious NaN can be produced
             * if the infinity from the overflow and the infinite addend
             * have opposite signs.
             */

            // First, screen for and handle non-finite input values whose
            // arithmetic is not supported by BigDecimal.
            if (Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(c)) {
                return Double.NaN;
            } else { // All inputs non-NaN
                boolean infiniteA = Double.isInfinite(a);
                boolean infiniteB = Double.isInfinite(b);
                boolean infiniteC = Double.isInfinite(c);
                double result;

                if (infiniteA || infiniteB || infiniteC) {
                    if (infiniteA && b == 0.0 ||
                            infiniteB && a == 0.0 ) {
                        return Double.NaN;
                    }
                    double product = a * b;
                    if (Double.isInfinite(product) && !infiniteA && !infiniteB) {
                        // Intermediate overflow; might cause a
                        // spurious NaN if added to infinite c.
                        /* assert Double.isInfinite(c); */
                        if (!Double.isInfinite(c)) throw new ArithmeticException("Intermediate overflow");
                        return c;
                    } else {
                        result = product + c;
                        /* assert !Double.isFinite(result); */
                        if (Double.isFinite(result)) throw new ArithmeticException("Intermediate overflow");
                        return result;
                    }
                } else { // All inputs finite
                    BigDecimal product = (new BigDecimal(a)).multiply(new BigDecimal(b));
                    if (c == 0.0) { // Positive or negative zero
                        // If the product is an exact zero, use a
                        // floating-point expression to compute the sign
                        // of the zero final result. The product is an
                        // exact zero if and only if at least one of a and
                        // b is zero.
                        if (a == 0.0 || b == 0.0) {
                            return a * b + c;
                        } else {
                            // The sign of a zero addend doesn't matter if
                            // the product is nonzero. The sign of a zero
                            // addend is not factored in the result if the
                            // exact product is nonzero but underflows to
                            // zero; see IEEE-754 2008 section 6.3 "The
                            // sign bit".
                            return product.doubleValue();
                        }
                    } else {
                        return product.add(new BigDecimal(c)).doubleValue();
                    }
                }
            }
        }

        public static float fma(float a, float b, float c) {
            if (Float.isFinite(a) && Float.isFinite(b) && Float.isFinite(c)) {
                if (a == 0.0 || b == 0.0) {
                    return a * b + c; // Handled signed zero cases
                } else {
                    return (new BigDecimal((double)a * (double)b) // Exact multiply
                            .add(new BigDecimal((double)c)))      // Exact sum
                            .floatValue();                            // One rounding
                    // to a float value
                }
            } else {
                // At least one of a,b, and c is non-finite. The result
                // will be non-finite as well and will be the same
                // non-finite value under double as float arithmetic.
                return (float)fma((double)a, (double)b, (double)c);
            }
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
                return abs(a);
        }

        public static long absExact(long a) {
            if (a == Long.MIN_VALUE)
                throw new ArithmeticException(
                        "Overflow to represent absolute value of Long.MIN_VALUE");
            else
                return abs(a);
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
            double a = angle + PI_HALF;
            double b = a - (int) (a / TAU) * TAU;
            if (b < 0.0) b = TAU + b;
            if (b >= PI) return -cos;
            return cos;
        }

        public static double safeAsin(double a) {
            return a <= -1.0 ? -PI_HALF : a >= 1.0 ? PI_HALF : Math.asin(a);
        }

        public static double safeAcos(double a) {
            if (a < -1.0) return PI;
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
            return RandomHolder.GENERATOR.nextFloat(bound);
        }

        public static float randomFloat(float min, float max) {
            return RandomHolder.GENERATOR.nextFloat(min, max);
        }

        public static float randomFloat() {
            return RandomHolder.GENERATOR.nextFloat();
        }

        public static double randomDouble(double bound) {
            return RandomHolder.GENERATOR.nextDouble(bound);
        }

        public static double randomDouble(double min, double max) {
            return RandomHolder.GENERATOR.nextDouble(min, max);
        }

        public static double randomDouble() {
            return RandomHolder.GENERATOR.nextDouble();
        }

        public static int randomInt(int bound) {
            return RandomHolder.GENERATOR.nextInt(bound);
        }

        public static int randomInt(int min, int max) {
            return RandomHolder.GENERATOR.nextInt(min, max);
        }

        public static int randomInt() {
            return RandomHolder.GENERATOR.nextInt();
        }

        public static long randomLong(long bound) {
            return RandomHolder.GENERATOR.nextLong(bound);
        }

        public static long randomLong(long min, long max) {
            return RandomHolder.GENERATOR.nextLong(min, max);
        }

        public static long randomLong() {
            return RandomHolder.GENERATOR.nextLong();
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
            return ExtendedMath.fma(b - a, t, a);
        }

        public static double lerp(double a, double b, double t) {
            return ExtendedMath.fma(b - a, t, a);
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
            value --;
            value |= value >>> 1;
            value |= value >>> 2;
            value |= value >>> 4;
            value |= value >>> 8;
            value |= value >>> 16;
            value ++;
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
            value --;
            value |= value >>> 1;
            value |= value >>> 2;
            value |= value >>> 4;
            value |= value >>> 8;
            value |= value >>> 16;
            value ++;
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
                if(Integer.MIN_VALUE == a) {
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
                k ++; // cast out twos.
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

    }

}
