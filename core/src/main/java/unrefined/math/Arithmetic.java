package unrefined.math;

import unrefined.context.Environment;
import unrefined.util.CharSequences;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Random;

public class Arithmetic {

    private static volatile Arithmetic INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Arithmetic getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.arithmetic", Arithmetic.class);
        }
        return INSTANCE;
    }

    public MathContext createMathContext(int precision, RoundingMode mode) {
        return new MathContext(precision, mode);
    }

    public MathContext createMathContext(int precision) {
        return new MathContext(precision);
    }

    public BigInteger createBigInteger(byte[] value) {
        return new BigInteger(value);
    }

    public BigInteger createBigInteger(byte[] value, int offset, int length) {
        return new BigInteger(Arrays.copyOfRange(value, offset, offset + length));
    }

    public BigInteger createBigInteger(int signum, byte[] magnitude) {
        return new BigInteger(signum, magnitude);
    }

    public BigInteger createBigInteger(int signum, byte[] magnitude, int offset, int length) {
        return new BigInteger(signum, Arrays.copyOfRange(magnitude, offset, offset + length));
    }

    public BigInteger createBigInteger(byte value) {
        return BigInteger.valueOf(value);
    }

    public BigInteger createBigInteger(short value) {
        return BigInteger.valueOf(value);
    }

    public BigInteger createBigInteger(int value) {
        return BigInteger.valueOf(value);
    }

    public BigInteger createBigInteger(long value) {
        return BigInteger.valueOf(value);
    }

    public BigInteger parseBigInteger(String value) {
        return new BigInteger(value);
    }

    public BigInteger parseBigInteger(String value, int radix) {
        return new BigInteger(value, radix);
    }

    public BigInteger randomBigInteger(int numBits, Random random) {
        return new BigInteger(numBits, random);
    }

    public BigInteger probablePrimeBigInteger(int bitLength, int certainty, Random random) {
        return new BigInteger(bitLength, certainty, random);
    }

    public BigInteger probablePrimeBigInteger(int bitLength, Random random) {
        return BigInteger.probablePrime(bitLength, random);
    }

    public BigDecimal createBigDecimal(double value, MathContext mathContext) {
        return new BigDecimal(value, mathContext == null ? MathContext.UNLIMITED : mathContext);
    }

    public BigDecimal createBigDecimal(double value) {
        return new BigDecimal(value);
    }

    public BigDecimal createBigDecimal(float value, MathContext mathContext) {
        return new BigDecimal(value, mathContext == null ? MathContext.UNLIMITED : mathContext);
    }

    public BigDecimal createBigDecimal(float value) {
        return new BigDecimal(value);
    }

    public BigDecimal createBigDecimal(byte value, MathContext mathContext) {
        return new BigDecimal(value, mathContext == null ? MathContext.UNLIMITED : mathContext);
    }

    public BigDecimal createBigDecimal(byte value) {
        return new BigDecimal(value);
    }

    public BigDecimal createBigDecimal(short value, MathContext mathContext) {
        return new BigDecimal(value, mathContext == null ? MathContext.UNLIMITED : mathContext);
    }

    public BigDecimal createBigDecimal(short value) {
        return new BigDecimal(value);
    }

    public BigDecimal createBigDecimal(int value, MathContext mathContext) {
        return new BigDecimal(value, mathContext == null ? MathContext.UNLIMITED : mathContext);
    }

    public BigDecimal createBigDecimal(int value) {
        return new BigDecimal(value);
    }

    public BigDecimal createBigDecimal(long value, MathContext mathContext) {
        return new BigDecimal(value, mathContext == null ? MathContext.UNLIMITED : mathContext);
    }

    public BigDecimal createBigDecimal(long value) {
        return BigDecimal.valueOf(value);
    }

    public BigDecimal createBigDecimal(long unscaled, int scale) {
        return BigDecimal.valueOf(unscaled, scale);
    }

    public BigDecimal createBigDecimal(long unscaled, int scale, MathContext mathContext) {
        return new BigDecimal(BigInteger.valueOf(unscaled), scale, mathContext == null ? MathContext.UNLIMITED : mathContext);
    }

    public BigDecimal createBigDecimal(BigInteger value, MathContext mathContext) {
        return new BigDecimal(value, mathContext == null ? MathContext.UNLIMITED : mathContext);
    }

    public BigDecimal createBigDecimal(BigInteger value) {
        return new BigDecimal(value);
    }

    public BigDecimal createBigDecimal(BigInteger unscaled, int scale) {
        return new BigDecimal(unscaled, scale);
    }

    public BigDecimal createBigDecimal(BigInteger unscaled, int scale, MathContext mathContext) {
        return new BigDecimal(unscaled, scale, mathContext == null ? MathContext.UNLIMITED : mathContext);
    }

    public BigDecimal parseBigDecimal(char[] value, int offset, int length) {
        return new BigDecimal(value, offset, length);
    }

    public BigDecimal parseBigDecimal(char[] value, int offset, int length, MathContext mathContext) {
        return new BigDecimal(value, offset, length, mathContext == null ? MathContext.UNLIMITED : mathContext);
    }

    public BigDecimal parseBigDecimal(char[] value) {
        return new BigDecimal(value);
    }

    public BigDecimal parseBigDecimal(char[] value, MathContext mathContext) {
        return new BigDecimal(value, mathContext == null ? MathContext.UNLIMITED : mathContext);
    }

    public BigDecimal parseBigDecimal(String value, int start, int end) {
        return new BigDecimal(CharSequences.toCharArray(value, start, end));
    }

    public BigDecimal parseBigDecimal(String value, int start, int end, MathContext mathContext) {
        return new BigDecimal(CharSequences.toCharArray(value, start, end), mathContext == null ? MathContext.UNLIMITED : mathContext);
    }

    public BigDecimal parseBigDecimal(String value) {
        return new BigDecimal(CharSequences.toCharArray(value));
    }

    public BigDecimal parseBigDecimal(String value, MathContext mathContext) {
        return new BigDecimal(CharSequences.toCharArray(value), mathContext == null ? MathContext.UNLIMITED : mathContext);
    }

    public BigDecimal fastBigDecimal(double value) {
        return BigDecimal.valueOf(value);
    }

    public BigDecimal fastBigDecimal(double value, MathContext mathContext) {
        return new BigDecimal(Double.toString(value), mathContext);
    }

    protected long multiplyHigh(long x, long y) {
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

    protected long unsignedMultiplyHigh(long x, long y) {
        // Compute via multiplyHigh() to leverage the intrinsic
        long result = multiplyHigh(x, y);
        result += (y & (x >> 63)); // equivalent to `if (x < 0) result += y;`
        result += (x & (y >> 63)); // equivalent to `if (y < 0) result += x;`
        return result;
    }

    protected double fma(double a, double b, double c) {
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

    protected float fma(float a, float b, float c) {
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

}
