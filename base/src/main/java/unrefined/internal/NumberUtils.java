/*
 * MIT License
 *
 * Copyright (c) 2021 Tianscar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package unrefined.internal;

import unrefined.util.NotInstantiableError;

import java.math.BigInteger;
import java.text.NumberFormat;

public final class NumberUtils {

    private NumberUtils() {
        throw new NotInstantiableError(NumberUtils.class);
    }

    private static final BigInteger UNSIGNED_LONG_MASK = BigInteger.ONE.shiftLeft(Long.SIZE).subtract(BigInteger.ONE);
    public static BigInteger toUnsignedBigInteger(long i) {
        if (i >= 0) return BigInteger.valueOf(i);
        else return BigInteger.valueOf(i).and(UNSIGNED_LONG_MASK);
    }

    public static String format(double v, NumberFormat format) {
        if (Double.isNaN(v) || Double.isInfinite(v) || format == null) return Double.toString(v);
        else return format.format(v);
    }

    public static String format(float v, NumberFormat format) {
        if (Float.isNaN(v) || Float.isInfinite(v) || format == null) return Float.toString(v);
        else return format.format(v);
    }

    public static String format(int v, NumberFormat format) {
        if (format == null) return Integer.toString(v);
        else return format.format(v);
    }

    public static String format(long v, NumberFormat format) {
        if (format == null) return Long.toString(v);
        else return format.format(v);
    }

}