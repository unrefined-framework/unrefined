package unrefined.runtime;

import unrefined.desktop.FontSupport;
import unrefined.media.graphics.Font;

import java.util.Objects;

public class DesktopFont extends Font {

    private final java.awt.Font font;

    public DesktopFont(java.awt.Font font) {
        this.font = font == null ? FontSupport.getDefaultFont() : font;
    }

    public DesktopFont() {
        this.font = FontSupport.getDefaultFont();
    }

    public java.awt.Font getFont() {
        return font;
    }

    @Override
    public String getFamily() {
        return font.getFamily();
    }

    @Override
    public int getStyle() {
        return font.getStyle();
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
