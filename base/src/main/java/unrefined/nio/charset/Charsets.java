package unrefined.nio.charset;

import unrefined.io.console.Console;
import unrefined.util.NotInstantiableError;
import unrefined.util.foreign.Foreign;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.SortedMap;

public final class Charsets {

    private Charsets() {
        throw new NotInstantiableError(Charsets.class);
    }

    public static boolean isSupported(String charsetName) {
        return Charset.isSupported(charsetName);
    }

    public static Charset forName(String charsetName) {
        return Charset.forName(charsetName);
    }

    public static Charset getCharset(String charsetName) {
        try {
            return Charset.forName(charsetName);
        }
        catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
            return null;
        }
    }

    public static Charset getCharset(String charsetName, Charset fallback) {
        try {
            return Charset.forName(charsetName);
        }
        catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
            return fallback;
        }
    }

    public static SortedMap<String, Charset> availableCharsets() {
        return Charset.availableCharsets();
    }

    public static Charset defaultCharset() {
        return Charset.defaultCharset();
    }

    public static Charset systemCharset() {
        return Foreign.getInstance().systemCharset();
    }

    public static Charset wideCharset() {
        return Foreign.getInstance().wideCharset();
    }

    public static Charset consoleCharset() {
        return Console.getInstance().getCharset();
    }

    /**
     * Seven-bit ASCII, also known as ISO646-US, also known as the
     * Basic Latin block of the Unicode character set.
     */
    public static final Charset US_ASCII = Charset.forName("US-ASCII");
    /**
     * ISO Latin Alphabet {@literal No. 1}, also known as ISO-LATIN-1.
     */
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    /**
     * Eight-bit UCS Transformation Format.
     */
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    /**
     * Sixteen-bit UCS Transformation Format, byte order identified by an
     * optional byte-order mark.
     */
    public static final Charset UTF_16 = Charset.forName("UTF-16");
    /**
     * Sixteen-bit UCS Transformation Format, little-endian byte order.
     */
    public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
    /**
     * Sixteen-bit UCS Transformation Format, big-endian byte order.
     */
    public static final Charset UTF_16BE = Charset.forName("UTF-16BE");

}
