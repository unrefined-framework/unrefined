package unrefined.runtime;

import unrefined.desktop.MathCompat;
import unrefined.math.Arithmetic;

public class DesktopArithmetic extends Arithmetic {

    @Override
    protected long multiplyHigh(long x, long y) {
        return MathCompat.multiplyHigh(x, y);
    }

    @Override
    protected long unsignedMultiplyHigh(long x, long y) {
        return MathCompat.unsignedMultiplyHigh(x, y);
    }

    @Override
    protected double fma(double a, double b, double c) {
        return MathCompat.fma(a, b, c);
    }

    @Override
    protected float fma(float a, float b, float c) {
        return MathCompat.fma(a, b, c);
    }

}
