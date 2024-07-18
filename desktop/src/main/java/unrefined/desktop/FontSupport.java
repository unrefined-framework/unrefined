package unrefined.desktop;

import unrefined.desktop.X11.X11FontSupport;
import unrefined.desktop.macos.MacFontSupport;
import unrefined.desktop.windows.WindowsFontSupport;
import unrefined.io.UnsupportedFormatException;
import unrefined.media.graphics.FontNotFoundException;
import unrefined.media.graphics.Text;
import unrefined.util.NotInstantiableError;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.font.GraphicAttribute;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class FontSupport {

    private FontSupport() {
        throw new NotInstantiableError(FontSupport.class);
    }

    public static final int BOLD_ITALIC = Font.BOLD | Font.ITALIC;
    
    public static final Font DIALOG_FONT = Font.decode(Font.DIALOG);
    public static final Font DIALOG_INPUT_FONT = Font.decode(Font.DIALOG_INPUT);
    public static final Font SANS_SERIF_FONT = Font.decode(Font.SANS_SERIF);
    public static final Font SERIF_FONT = Font.decode(Font.SERIF);
    public static final Font MONOSPACED_FONT = Font.decode(Font.MONOSPACED);

    public static Font getDefaultFont() {
        if (OSInfo.IS_WINDOWS) return WindowsFontSupport.SYSTEM_FONT;
        else if (OSInfo.IS_MAC) return MacFontSupport.SYSTEM_FONT;
        else return X11FontSupport.getDefaultFont();
    }

    public static Font getFont(String family, int style) throws FontNotFoundException {
        if (family == null) throw new FontNotFoundException();
        if (unrefined.media.graphics.Font.DEFAULT.equals(family)) return getDefaultFont();
        StringBuilder builder = new StringBuilder(family);
        switch (style) {
            case Font.PLAIN: builder.append(" plain"); break;
            case Font.BOLD: builder.append(" bold"); break;
            case Font.ITALIC: builder.append(" italic"); break;
            case BOLD_ITALIC: builder.append(" bolditalic"); break;
        }
        Font font = Font.decode(builder.toString());
        if (!Font.DIALOG.equals(family) && DIALOG_FONT.equals(font)) throw new FontNotFoundException();
        else return font;
    }

    public static Font readFont(File input) throws IOException {
        Objects.requireNonNull(input);
        try {
            return Font.createFont(Font.TRUETYPE_FONT, input);
        } catch (FontFormatException e) {
            try {
                return Font.createFont(Font.TYPE1_FONT, input);
            } catch (FontFormatException ex) {
                throw new UnsupportedFormatException();
            }
        }
    }

    public static Font readFont(InputStream input) throws IOException {
        Objects.requireNonNull(input);
        try {
            return Font.createFont(Font.TRUETYPE_FONT, input);
        } catch (FontFormatException e) {
            try {
                return Font.createFont(Font.TYPE1_FONT, input);
            } catch (FontFormatException ex) {
                throw new UnsupportedFormatException();
            }
        }
    }

    public static int toFontLayout(int direction) {
        switch (direction) {
            case Text.Direction.LTR: return Font.LAYOUT_LEFT_TO_RIGHT;
            case Text.Direction.RTL: return Font.LAYOUT_RIGHT_TO_LEFT;
            default: throw new IllegalArgumentException("Illegal text direction: " + direction);
        }
    }

    public static int toFontBaseline(int baseline) {
        switch (baseline) {
            case Text.Baseline.ALPHABETIC: return GraphicAttribute.ROMAN_BASELINE;
            case Text.Baseline.IDEOGRAPHIC: return GraphicAttribute.CENTER_BASELINE;
            case Text.Baseline.HANGING: return GraphicAttribute.HANGING_BASELINE;
            case Text.Baseline.BOTTOM: return GraphicAttribute.BOTTOM_ALIGNMENT;
            case Text.Baseline.CENTER: return -3;
            case Text.Baseline.TOP: return GraphicAttribute.TOP_ALIGNMENT;
            default: throw new IllegalArgumentException("Illegal text baseline: " + baseline);
        }
    }

}
