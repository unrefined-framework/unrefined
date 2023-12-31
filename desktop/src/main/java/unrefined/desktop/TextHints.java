package unrefined.desktop;

import unrefined.media.graphics.Text;
import unrefined.util.NotInstantiableError;

import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.TextAttribute;
import java.util.Map;

public final class TextHints {

    private TextHints() {
        throw new NotInstantiableError(TextHints.class);
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
        switch (direction) {
            case Text.Direction.AUTO: return null;
            case Text.Direction.LTR: return TextAttribute.RUN_DIRECTION_LTR;
            case Text.Direction.RTL: return TextAttribute.RUN_DIRECTION_RTL;
            default: throw new IllegalArgumentException("Illegal text direction: " + direction);
        }
    }

}
