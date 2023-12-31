package unrefined.runtime;

import unrefined.desktop.ExtendedComposite;
import unrefined.math.FastMath;
import unrefined.media.graphics.Composite;

import java.awt.AlphaComposite;
import java.util.Objects;

public class DesktopComposite extends Composite {

    private final java.awt.Composite composite;

    private final int mode;
    private final float alpha;

    public DesktopComposite(java.awt.Composite composite, float alpha) {
        if (composite instanceof AlphaComposite) {
            AlphaComposite alphaComposite = (AlphaComposite) composite; 
            this.mode = getCompositeMode(alphaComposite.getRule());
            this.alpha = alphaComposite.getAlpha();
        }
        else if (composite instanceof ExtendedComposite) {
            ExtendedComposite extendedComposite = (ExtendedComposite) composite; 
            this.mode = extendedComposite.getMode();
            this.alpha = extendedComposite.getAlpha();
        }
        else {
            this.mode = Mode.CUSTOM;
            this.alpha = FastMath.clamp(alpha, 0, 1);
        }
        this.composite = composite;
    }

    public static int getCompositeMode(int alphaCompositeRule) {
        switch (alphaCompositeRule) {
            case AlphaComposite.SRC_OVER: return Composite.Mode.SRC_OVER;
            case AlphaComposite.SRC_IN: return Composite.Mode.SRC_IN;
            case AlphaComposite.SRC_OUT: return Composite.Mode.SRC_OUT;
            case AlphaComposite.SRC_ATOP: return Composite.Mode.SRC_ATOP;
            case AlphaComposite.DST_OVER: return Composite.Mode.DST_OVER;
            case AlphaComposite.DST_IN: return Composite.Mode.DST_IN;
            case AlphaComposite.DST_OUT: return Composite.Mode.DST_OUT;
            case AlphaComposite.DST_ATOP: return Composite.Mode.DST_ATOP;
            case AlphaComposite.DST: return Composite.Mode.COPY;
            case AlphaComposite.XOR: return Composite.Mode.XOR;
            case AlphaComposite.SRC: return Composite.Mode.DISCARD;
            case AlphaComposite.CLEAR: return Composite.Mode.CLEAR;
            default: throw new IllegalArgumentException("Illegal alpha composite rule: " + alphaCompositeRule);
        }
    }

    public DesktopComposite(ExtendedComposite composite) {
        mode = composite.getMode();
        alpha = composite.getAlpha();
        this.composite = composite;
    }

    public DesktopComposite(AlphaComposite composite) {
        mode = getCompositeMode(composite.getRule());
        alpha = composite.getAlpha();
        this.composite = composite;
    }

    public java.awt.Composite getComposite() {
        return composite;
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public float getAlpha() {
        return alpha;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopComposite that = (DesktopComposite) object;

        if (mode != that.mode) return false;
        if (Float.compare(alpha, that.alpha) != 0) return false;
        return Objects.equals(composite, that.composite);
    }

    @Override
    public int hashCode() {
        int result = composite != null ? composite.hashCode() : 0;
        result = 31 * result + mode;
        result = 31 * result + (alpha != 0.0f ? Float.floatToIntBits(alpha) : 0);
        return result;
    }

}
