package unrefined.desktop;

import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.function.LongBinaryOperator;

public final class MathCompat {

    private MathCompat() {
        throw new NotInstantiableError(MathCompat.class);
    }

    @FunctionalInterface
    private interface FmaFloat {
        float compute(float a, float b, float c);
    }

    @FunctionalInterface
    private interface FmaDouble {
        double compute(double a, double b, double c);
    }

    private static final FmaFloat FMA_FLOAT;
    private static final FmaDouble FMA_DOUBLE;
    private static final LongBinaryOperator MULTIPLY_HIGH, UNSIGNED_MULTIPLY_HIGH;

    static {
        FmaFloat fmaFloat;
        FmaDouble fmaDouble;
        LongBinaryOperator longBinaryOperator;
        try {
            final Method fma = Math.class.getDeclaredMethod("fma", double.class, double.class, double.class);
            fmaDouble = (a, b, c) -> {
                try {
                    return ReflectionSupport.invokeDoubleMethod(null, fma, a, b, c);
                } catch (InvocationTargetException e) {
                    Throwable t = e.getTargetException();
                    if (t instanceof RuntimeException) throw (RuntimeException) t;
                    else if (t instanceof Error) throw (Error) t;
                    else throw new UnexpectedError(e);
                }
            };
        }
        catch (NoSuchMethodException e) {
            fmaDouble = (a, b, c) -> {
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
            };
        }
        FMA_DOUBLE = fmaDouble;
        try {
            final Method fma = Math.class.getDeclaredMethod("fma", float.class, float.class, float.class);
            fmaFloat = (a, b, c) -> {
                try {
                    return ReflectionSupport.invokeFloatMethod(null, fma, a, b, c);
                } catch (InvocationTargetException e) {
                    Throwable t = e.getTargetException();
                    if (t instanceof RuntimeException) throw (RuntimeException) t;
                    else if (t instanceof Error) throw (Error) t;
                    else throw new UnexpectedError(e);
                }
            };
        }
        catch (NoSuchMethodException e) {
            fmaFloat = (a, b, c) -> {
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
                    return (float)FMA_DOUBLE.compute((double)a, (double)b, (double)c);
                }
            };
        }
        FMA_FLOAT = fmaFloat;
        try {
            final Method multiplyHigh = Math.class.getDeclaredMethod("multiplyHigh", long.class, long.class);
            longBinaryOperator = (left, right) -> {
                try {
                    return ReflectionSupport.invokeLongMethod(null, multiplyHigh, left, right);
                } catch (InvocationTargetException e) {
                    Throwable t = e.getTargetException();
                    if (t instanceof RuntimeException) throw (RuntimeException) t;
                    else if (t instanceof Error) throw (Error) t;
                    else throw new UnexpectedError(e);
                }
            };
        }
        catch (NoSuchMethodException e) {
            longBinaryOperator = (left, right) -> {
                // Use technique from section 8-2 of Henry S. Warren, Jr.,
                // Hacker's Delight (2nd ed.) (Addison Wesley, 2013), 173-174.
                long x1 = left >> 32;
                long x2 = left & 0xFFFFFFFFL;
                long y1 = right >> 32;
                long y2 = right & 0xFFFFFFFFL;

                long z2 = x2 * y2;
                long t = x1 * y2 + (z2 >>> 32);
                long z1 = t & 0xFFFFFFFFL;
                long z0 = t >> 32;
                z1 += x2 * y1;

                return x1 * y1 + z0 + (z1 >> 32);
            };
        }
        MULTIPLY_HIGH = longBinaryOperator;
        try {
            final Method unsignedMultiplyHigh = Math.class.getDeclaredMethod("unsignedMultiplyHigh", long.class, long.class);
            longBinaryOperator = (left, right) -> {
                try {
                    return ReflectionSupport.invokeLongMethod(null, unsignedMultiplyHigh, left, right);
                } catch (InvocationTargetException e) {
                    Throwable t = e.getTargetException();
                    if (t instanceof RuntimeException) throw (RuntimeException) t;
                    else if (t instanceof Error) throw (Error) t;
                    else throw new UnexpectedError(e);
                }
            };
        }
        catch (NoSuchMethodException e) {
            longBinaryOperator = new LongBinaryOperator() {
                @Override
                public long applyAsLong(long left, long right) {
                    // Compute via multiplyHigh() to leverage the intrinsic
                    long result = MULTIPLY_HIGH.applyAsLong(left, right);
                    result += (right & (left >> 63)); // equivalent to `if (x < 0) result += y;`
                    result += (left & (right >> 63)); // equivalent to `if (y < 0) result += x;`
                    return result;
                }
            };
        }
        UNSIGNED_MULTIPLY_HIGH = longBinaryOperator;
    }

    public static double fma(double a, double b, double c) {
        return FMA_DOUBLE.compute(a, b, c);
    }

    public static float fma(float a, float b, float c) {
        return FMA_FLOAT.compute(a, b, c);
    }

    public static long multiplyHigh(long a, long b) {
        return MULTIPLY_HIGH.applyAsLong(a, b);
    }

    public static long unsignedMultiplyHigh(long a, long b) {
        return UNSIGNED_MULTIPLY_HIGH.applyAsLong(a, b);
    }

}
