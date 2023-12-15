package unrefined.internal;

import unrefined.internal.X11.X11FontUtils;
import unrefined.internal.macos.MacFontUtils;
import unrefined.internal.windows.WindowsFontUtils;
import unrefined.media.graphics.Text;
import unrefined.util.NotInstantiableError;

import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.TextAttribute;
import java.util.Map;

public final class FontUtils {

    private FontUtils() {
        throw new NotInstantiableError(FontUtils.class);
    }

    public static Font getDefaultFont() {
        if (SystemUtils.IS_WINDOWS) return WindowsFontUtils.ICONTITLELOGFONT;
        else if (SystemUtils.IS_MAC) return MacFontUtils.SYSTEM_FONT;
        else return X11FontUtils.getDefaultFont();
    }

    public static final Object VALUE_TEXT_ANTIALIAS_SYSTEM;
    public static final Object VALUE_TEXT_ANTIALIAS_SYSTEM_ON;
    static {
        Map<?, ?> textRenderingHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        if (textRenderingHints == null) VALUE_TEXT_ANTIALIAS_SYSTEM = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
        else {
            Object value = textRenderingHints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
            VALUE_TEXT_ANTIALIAS_SYSTEM = value == null ? RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT : value;
        }
        if (VALUE_TEXT_ANTIALIAS_SYSTEM == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT ||
                VALUE_TEXT_ANTIALIAS_SYSTEM == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
            VALUE_TEXT_ANTIALIAS_SYSTEM_ON = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
        else VALUE_TEXT_ANTIALIAS_SYSTEM_ON = VALUE_TEXT_ANTIALIAS_SYSTEM;
    }
    public static final Integer UNDERLINE_OFF = -1;
    public static final Boolean STRIKETHROUGH_OFF = Boolean.FALSE;

    public static Boolean toRunDirection(int direction) {
        return switch (direction) {
            case Text.Direction.AUTO -> null;
            case Text.Direction.LTR -> TextAttribute.RUN_DIRECTION_LTR;
            case Text.Direction.RTL -> TextAttribute.RUN_DIRECTION_RTL;
            default -> throw new IllegalArgumentException("Illegal text direction: " + direction);
        };
    }

}
