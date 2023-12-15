package unrefined.runtime;

import unrefined.desktop.BiRadialGradientPaint;
import unrefined.desktop.TransformedTexturePaint;
import unrefined.media.graphics.Brush;
import unrefined.util.AlreadyDisposedException;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class DesktopBrush extends Brush {

    private volatile Paint paint;
    private final int type;

    public DesktopBrush(Paint paint) {
        this.paint = paint == null ? Color.BLACK : paint;
        if (this.paint instanceof Color) this.type = Type.COLOR;
        else if (this.paint instanceof LinearGradientPaint) this.type = Type.LINEAR_GRADIENT;
        else if (this.paint instanceof BiRadialGradientPaint) this.type = Type.RADIAL_GRADIENT;
        else if (this.paint instanceof TransformedTexturePaint) this.type = Type.BITMAP_PATTERN;
        else this.type = Type.CUSTOM;
    }

    public DesktopBrush() {
        this.paint = Color.BLACK;
        this.type = Type.COLOR;
    }

    public Paint getPaint() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return paint;
    }

    @Override
    public int getType() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return type;
    }

    private final AtomicBoolean disposed = new AtomicBoolean(false);

    @Override
    public void dispose() {
        if (disposed.compareAndSet(false, true)) paint = null;
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopBrush that = (DesktopBrush) object;

        if (type != that.type) return false;
        return Objects.equals(paint, that.paint);
    }

    @Override
    public int hashCode() {
        int result = paint != null ? paint.hashCode() : 0;
        result = 31 * result + type;
        return result;
    }

}
