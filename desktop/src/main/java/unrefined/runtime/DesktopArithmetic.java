package unrefined.runtime;

import unrefined.desktop.DesktopMath;
import unrefined.math.Arithmetic;

public class DesktopArithmetic extends Arithmetic {

    @Override
    protected long multiplyHigh(long x, long y) {
        return DesktopMath.multiplyHigh(x, y);
    }

    @Override
    protected long unsignedMultiplyHigh(long x, long y) {
        return DesktopMath.unsignedMultiplyHigh(x, y);
    }

    @Override
    protected double fma(double a, double b, double c) {
        return DesktopMath.fma(a, b, c);
    }

    @Override
    protected float fma(float a, float b, float c) {
        return DesktopMath.fma(a, b, c);
    }

}
