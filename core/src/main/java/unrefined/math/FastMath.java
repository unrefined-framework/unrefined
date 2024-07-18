package unrefined.math;

import unrefined.core.ExtendedMath;
import unrefined.util.NotInstantiableError;

import java.math.BigInteger;
import java.math.RoundingMode;

public final class FastMath {

    private FastMath() {
        throw new NotInstantiableError(FastMath.class);
    }

    public static final double E = Math.E;
    public static final double PI = Math.PI;
    public static final double PI_HALF = PI * 0.5;
    public static final double TAU = 2.0 * PI;

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
        return Arithmetic.getInstance().multiplyHigh(x, y);
    }

    public static long unsignedMultiplyHigh(long x, long y) {
        return Arithmetic.getInstance().unsignedMultiplyHigh(x, y);
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
        return Arithmetic.getInstance().fma(a, b, c);
    }

    public static double fma(double a, double b, double c) {
        return Arithmetic.getInstance().fma(a, b, c);
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

    public static float round(float value, RoundingMode mode) {
        switch (mode) {
            case UP: return (float) ExtendedMath.up(value);
            case DOWN: return (float) ExtendedMath.down(value);
            case CEILING: return (float) Math.ceil(value);
            case FLOOR: return (float) Math.floor(value);
            case HALF_UP: return (float) ExtendedMath.halfUp(value);
            case HALF_DOWN: return (float) ExtendedMath.halfDown(value);
            case HALF_EVEN: return (float) Math.rint(value);
            case UNNECESSARY: return value;
            default: throw new IllegalArgumentException("Illegal rounding mode: " + mode);
        }
    }

    public static double round(double value, RoundingMode mode) {
        switch (mode) {
            case UP: return ExtendedMath.up(value);
            case DOWN: return ExtendedMath.down(value);
            case CEILING: return Math.ceil(value);
            case FLOOR: return Math.floor(value);
            case HALF_UP: return ExtendedMath.halfUp(value);
            case HALF_DOWN: return ExtendedMath.halfDown(value);
            case HALF_EVEN : return Math.rint(value);
            case UNNECESSARY: return value;
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

    public static boolean isInteger(float x) {
        return x % 1 == 0;
    }

    public static boolean isInteger(double x) {
        return x % 1 == 0;
    }

    public static byte unsignedMax(byte a, byte b) {
        return ExtendedMath.unsignedMax(a, b);
    }

    public static short unsignedMax(short a, short b) {
        return ExtendedMath.unsignedMax(a, b);
    }

    public static int unsignedMax(int a, int b) {
        return ExtendedMath.unsignedMax(a, b);
    }

    public static long unsignedMax(long a, long b) {
        return ExtendedMath.unsignedMax(a, b);
    }

    public static byte unsignedMax(byte a, byte b, byte c) {
        return ExtendedMath.unsignedMax(a, b, c);
    }

    public static short unsignedMax(short a, short b, short c) {
        return ExtendedMath.unsignedMax(a, b, c);
    }

    public static int unsignedMax(int a, int b, int c) {
        return ExtendedMath.unsignedMax(a, b, c);
    }

    public static long unsignedMax(long a, long b, long c) {
        return ExtendedMath.unsignedMax(a, b, c);
    }

    public static byte unsignedMin(byte a, byte b) {
        return ExtendedMath.unsignedMin(a, b);
    }

    public static short unsignedMin(short a, short b) {
        return ExtendedMath.unsignedMin(a, b);
    }

    public static int unsignedMin(int a, int b) {
        return ExtendedMath.unsignedMin(a, b);
    }

    public static long unsignedMin(long a, long b) {
        return ExtendedMath.unsignedMin(a, b);
    }

    public static byte unsignedMin(byte a, byte b, byte c) {
        return ExtendedMath.unsignedMin(a, b, c);
    }

    public static short unsignedMin(short a, short b, short c) {
        return ExtendedMath.unsignedMin(a, b, c);
    }

    public static int unsignedMin(int a, int b, int c) {
        return ExtendedMath.unsignedMin(a, b, c);
    }

    public static long unsignedMin(long a, long b, long c) {
        return ExtendedMath.unsignedMin(a, b, c);
    }

    public static byte sign(short x) throws ArithmeticException {
        return ExtendedMath.sign(x);
    }

    public static short sign(int x) throws ArithmeticException {
        return ExtendedMath.sign(x);
    }

    public static int sign(long x) throws ArithmeticException {
        return ExtendedMath.sign(x);
    }

    public static long sign(BigInteger x) throws ArithmeticException {
        return ExtendedMath.sign(x);
    }

    public static int divideUnsigned(int dividend, int divisor) {
        return ExtendedMath.divideUnsigned(dividend, divisor);
    }

    public static int remainderUnsigned(int dividend, int divisor) {
        return ExtendedMath.remainderUnsigned(dividend, divisor);
    }

    public static long divideUnsigned(long dividend, long divisor) {
        return ExtendedMath.divideUnsigned(dividend, divisor);
    }

    public static long remainderUnsigned(long dividend, long divisor) {
        return ExtendedMath.remainderUnsigned(dividend, divisor);
    }

    public static int add(int a, int b) {
        return ExtendedMath.add(a, b);
    }

    public static long add(long a, long b) {
        return ExtendedMath.add(a, b);
    }

    public static float add(float a, float b) {
        return ExtendedMath.add(a, b);
    }

    public static double add(double a, double b) {
        return ExtendedMath.add(a, b);
    }

}
