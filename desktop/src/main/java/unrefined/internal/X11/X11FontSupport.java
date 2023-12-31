package unrefined.internal.X11;

import unrefined.internal.OperatingSystem;
import unrefined.util.NotInstantiableError;

import java.awt.Font;
import java.util.Locale;
import java.util.StringTokenizer;

public final class X11FontSupport {

    private X11FontSupport() {
        throw new NotInstantiableError(X11FontSupport.class);
    }

    private static volatile Font defaultFont;
    private static volatile float fontScale;
    private static volatile int dpi;
    private static volatile float density;
    private static volatile float scaledDensity;

    static {
        if (OperatingSystem.IS_X11) {
            syncFont(XSettings.getProperty("Gtk/FontName"));
            syncDensity(XSettings.getProperty("Gdk/UnscaledDPI"), XSettings.getProperty("Gdk/WindowScalingFactor"));
            XSettings.addPropertyChangeListener("Gtk/FontName", evt -> {
                syncFont(evt.getNewValue());
                syncDensity(XSettings.getProperty("Gdk/UnscaledDPI"), XSettings.getProperty("Gdk/WindowScalingFactor"));
            });
            XSettings.addPropertyChangeListener("Gdk/UnscaledDPI",
                    evt -> syncDensity(evt.getNewValue(), XSettings.getProperty("Gdk/WindowScalingFactor")));
            XSettings.addPropertyChangeListener("Gdk/WindowScalingFactor",
                    evt -> syncDensity(XSettings.getProperty("Gdk/UnscaledDPI"), evt.getNewValue()));
        }
    }

    public static Font getDefaultFont() {
        return defaultFont;
    }

    public static float getFontScale() {
        return fontScale;
    }

    public static int getDPI() {
        return dpi;
    }

    public static float getDensity() {
        return density;
    }

    public static float getScaledDensity() {
        return scaledDensity;
    }

    private static synchronized void syncDensity(Object unscaledDPI, Object windowScalingFactor) {
        if (unscaledDPI instanceof Integer && windowScalingFactor instanceof Integer) {
            dpi = ((Integer) unscaledDPI) / 1024 * ((Integer) windowScalingFactor);
            density = dpi / 96f;
            scaledDensity = density * fontScale;
        }
        else if (unscaledDPI instanceof Integer) {
            dpi = ((Integer) unscaledDPI) / 1024;
            density = dpi / 96f;
            scaledDensity = density * fontScale;
        }
        else if (windowScalingFactor instanceof Integer) {
            dpi = 96 * ((Integer) windowScalingFactor);
            density = dpi / 96f;
            scaledDensity = density * fontScale;
        }
    }

    private static synchronized void syncFont(Object fontName) {
        if (fontName instanceof String) defaultFont = parseFont((String) fontName);
        else defaultFont = parseFont("Sans 10");
        fontScale = (float) (defaultFont.getSize() / 10.0);
        // scale font size from dp to pt
        defaultFont = defaultFont.deriveFont(defaultFont.getStyle(), (float) (10.0 * (96.0 / 72.0)));
    }

    // Reference: FlatLaf
    // https://github.com/JFormDesigner/FlatLaf/blob/main/flatlaf-core/src/main/java/com/formdev/flatlaf/LinuxFontPolicy.java
    private static Font parseFont(String fontName) {
        // see class com.sun.java.swing.plaf.gtk.PangoFonts background information

        String family = "";
        int style = Font.PLAIN;
        int size = 10;

        // parse pango font description
        // see https://developer.gnome.org/pango/1.46/pango-Fonts.html#pango-font-description-from-string
        StringTokenizer tokenizer = new StringTokenizer(fontName);
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();

            // remove trailing ',' (e.g. in "Ubuntu Condensed, 11" or "Ubuntu Condensed, Bold 11")
            if (word.endsWith(",")) word = word.substring(0, word.length() - 1).trim();

            String lword = word.toLowerCase(Locale.ENGLISH);
            if (lword.equals("italic") || lword.equals("oblique")) style |= Font.ITALIC;
            else if (lword.equals("bold")) style |= Font.BOLD;
            else if (Character.isDigit(word.charAt(0))) {
                try {
                    size = Integer.parseInt(word);
                }
                catch(NumberFormatException ignored) {
                }
            }
            else {
                // remove '-' from "Semi-Bold", "Extra-Light", etc
                if (lword.startsWith("semi-") || lword.startsWith("demi-")) word = word.substring(0, 4) + word.substring(5);
                else if (lword.startsWith("extra-") || lword.startsWith("ultra-")) word = word.substring(0, 5) + word.substring(6);
                family = family.isEmpty() ? word : (family + ' ' + word);
            }
        }

        // handle logical font names
        String logicalFamily = mapLogicalFontName(family.toLowerCase(Locale.ENGLISH));
        if (logicalFamily != null) family = logicalFamily;

        // Create a font for the given family, style and size.
        // If the font family does not match any font on the system,
        // then the last word (usually a font weight) from the family name is removed and tried again.
        // E.g. family 'URW Bookman Light' is not found, but 'URW Bookman' is found.
        // If still not found, then font of family 'Dialog' is returned.
        while (true) {
            Font font = new Font(family, style, size);

            if (Font.DIALOG.equals(family)) return font;

            // if the font family does not match any font on the system, "Dialog" family is returned
            if (!Font.DIALOG.equals(font.getFamily())) return font;

            // find last word in family
            int index = family.lastIndexOf(' ');
            if (index < 0) return new Font(Font.DIALOG, style, size);

            // check whether last work contains some font weight (e.g. Ultra-Bold or Heavy)
            String lastWord = family.substring(index + 1).toLowerCase(Locale.ENGLISH);
            if (lastWord.contains("bold") || lastWord.contains("heavy") || lastWord.contains("black")) style |= Font.BOLD;

            // remove last word from family and try again
            family = family.substring(0, index);
        }
    }

    /**
     * Map GTK/FontConfig logical font name to equivalent JDK logical font name
     */
    private static String mapLogicalFontName(String name) {
        switch (name) {
            case "sans": case "sans-serif": return Font.SANS_SERIF;
            case "serif": return Font.SERIF;
            case "monospace": return Font.MONOSPACED;
            default: return null;
        }
    }

}
