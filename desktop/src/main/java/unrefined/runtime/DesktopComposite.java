package unrefined.runtime;

import unrefined.desktop.ExtendedComposite;
import unrefined.media.graphics.Composite;
import unrefined.util.AlreadyDisposedException;

import java.awt.AlphaComposite;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class DesktopComposite extends Composite {

    private volatile java.awt.Composite composite;

    private final int mode;
    private final float alpha;

    public DesktopComposite(java.awt.Composite composite, float alpha) {
        if (composite instanceof AlphaComposite alphaComposite) {
            this.mode = getCompositeMode(alphaComposite.getRule());
            this.alpha = alphaComposite.getAlpha();
        }
        else if (composite instanceof ExtendedComposite extendedComposite) {
            this.mode = extendedComposite.getMode();
            this.alpha = extendedComposite.getAlpha();
        }
        else {
            this.mode = Mode.CUSTOM;
            this.alpha = Math.clamp(alpha, 0, 1);
        }
        this.composite = composite;
    }

    public static int getCompositeMode(int alphaCompositeRule) {
        return switch (alphaCompositeRule) {
            case AlphaComposite.SRC_OVER -> Composite.Mode.SRC_OVER;
            case AlphaComposite.SRC_IN -> Composite.Mode.SRC_IN;
            case AlphaComposite.SRC_OUT -> Composite.Mode.SRC_OUT;
            case AlphaComposite.SRC_ATOP -> Composite.Mode.SRC_ATOP;
            case AlphaComposite.DST_OVER -> Composite.Mode.DST_OVER;
            case AlphaComposite.DST_IN -> Composite.Mode.DST_IN;
            case AlphaComposite.DST_OUT -> Composite.Mode.DST_OUT;
            case AlphaComposite.DST_ATOP -> Composite.Mode.DST_ATOP;
            case AlphaComposite.DST -> Composite.Mode.COPY;
            case AlphaComposite.XOR -> Composite.Mode.XOR;
            case AlphaComposite.SRC -> Composite.Mode.DISCARD;
            case AlphaComposite.CLEAR -> Composite.Mode.CLEAR;
            default -> throw new IllegalArgumentException("Illegal alpha composite rule: " + alphaCompositeRule);
        };
    }

    public DesktopComposite(ExtendedComposite composite) {
        mode = composite.getMode();
        alpha = composite.getAlpha();
    }

    public DesktopComposite(AlphaComposite composite) {
        mode = getCompositeMode(composite.getRule());
        alpha = composite.getAlpha();
    }

    public java.awt.Composite getComposite() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return composite;
    }

    @Override
    public int getMode() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return mode;
    }

    @Override
    public float getAlpha() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return alpha;
    }

    private final AtomicBoolean disposed = new AtomicBoolean(false);

    @Override
    public void dispose() {
        if (disposed.compareAndSet(false, true)) composite = null;
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
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
