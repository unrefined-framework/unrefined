package unrefined.runtime;

import unrefined.desktop.FontFactory;
import unrefined.media.graphics.Font;
import unrefined.util.AlreadyDisposedException;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class DesktopFont extends Font {

    private volatile java.awt.Font font;

    public DesktopFont(java.awt.Font font) {
        this.font = font == null ? FontFactory.getDefaultFont() : font;
    }

    public DesktopFont() {
        this.font = FontFactory.getDefaultFont();
    }

    public java.awt.Font getFont() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return font;
    }

    @Override
    public String getFamily() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return font.getFamily();
    }

    @Override
    public int getStyle() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return font.getStyle();
    }

    private final AtomicBoolean disposed = new AtomicBoolean(false);

    @Override
    public void dispose() {
        if (disposed.compareAndSet(false, true)) font = null;
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopFont that = (DesktopFont) object;

        return Objects.equals(font, that.font);
    }

    @Override
    public int hashCode() {
        return font != null ? font.hashCode() : 0;
    }

}
