package unrefined.media.graphics;

import unrefined.io.asset.Asset;
import unrefined.util.NotInstantiableError;

import java.io.File;
import java.io.IOException;

import static unrefined.media.graphics.Font.Style.*;

public abstract class Font {

    public static Font of(String family, int style) throws FontNotFoundException {
        return Drawing.getInstance().getFont(family, style);
    }

    public static Font read(File input) throws IOException {
        return Drawing.getInstance().readFont(input);
    }

    public static Font read(Asset input) throws IOException {
        return Drawing.getInstance().readFont(input);
    }

    public static final class Style {
        private Style() {
            throw new NotInstantiableError(Style.class);
        }
        public static final int REGULAR     = 0;
        public static final int BOLD        = 1;
        public static final int ITALIC      = 1 << 1;
        public static final int BOLD_ITALIC = BOLD | ITALIC;
        public static int removeUnusedBits(int style) {
            return style << 30 >>> 30;
        }
        public static String toString(int style) {
            style = removeUnusedBits(style);
            StringBuilder builder = new StringBuilder("[");
            if (style == REGULAR) builder.append("REGULAR");
            else if ((style & BOLD) != 0) builder.append("BOLD");
            if ((style & ITALIC) != 0) builder.append(", ITALIC");
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
        return (Style.removeUnusedBits(getStyle()) & BOLD) != 0;
    }
    public boolean isItalic() {
        return (Style.removeUnusedBits(getStyle()) & ITALIC) != 0;
    }
    public boolean isBoldItalic() {
        return (Style.removeUnusedBits(getStyle()) & BOLD_ITALIC) != 0;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "family=" + getFamily() +
                ", style=" + Style.toString(getStyle()) +
                '}';
    }

}
