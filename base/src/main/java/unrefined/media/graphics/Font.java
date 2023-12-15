package unrefined.media.graphics;

import unrefined.internal.BitwiseUtils;
import unrefined.io.Disposable;
import unrefined.util.NotInstantiableError;

import static unrefined.media.graphics.Font.Style.*;

public abstract class Font implements Disposable {

    public static final class Style {
        private Style() {
            throw new NotInstantiableError(Style.class);
        }
        public static final int REGULAR     = 0;
        public static final int BOLD        = 1;
        public static final int ITALIC      = 1 << 1;
        public static final int BOLD_ITALIC = BOLD | ITALIC;
        public static int removeUnusedBits(int style) {
            return BitwiseUtils.removeUnusedBits(style, 2);
        }
        public static String toString(int style) {
            style = removeUnusedBits(style);
            StringBuilder builder = new StringBuilder("[");
            if (style == REGULAR) builder.append("REGULAR");
            else if ((style & BOLD) == BOLD) builder.append("BOLD");
            if ((style & ITALIC) == ITALIC) builder.append(", ITALIC");
            builder.append("]");
            return builder.toString();
        }
    }

    public static final String DEFAULT    = "Default";
    public static final String SANS_SERIF = "SansSerif";
    public static final String SERIF      = "Serif";
    public static final String MONOSPACED = "Monospaced";

    public abstract String getFamily();
    public abstract int getStyle();

    public boolean isRegular() {
        return Style.removeUnusedBits(getStyle()) == REGULAR;
    }
    public boolean isBold() {
        return (Style.removeUnusedBits(getStyle()) & BOLD) == BOLD;
    }
    public boolean isItalic() {
        return (Style.removeUnusedBits(getStyle()) & ITALIC) == ITALIC;
    }
    public boolean isBoldItalic() {
        return (Style.removeUnusedBits(getStyle()) & BOLD_ITALIC) == BOLD_ITALIC;
    }

    @Override
    public String toString() {
        if (isDisposed()) return getClass().getName() + "@" + Integer.toHexString(hashCode())
                + '{' +
                "disposed=true" +
                '}';
        else return getClass().getName()
                + '{' +
                "disposed=false" +
                ", style=" + Style.toString(getStyle()) +
                '}';
    }

}
