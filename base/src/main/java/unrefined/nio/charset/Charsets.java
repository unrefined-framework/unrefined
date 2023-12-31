package unrefined.nio.charset;

import unrefined.util.NotInstantiableError;
import unrefined.util.foreign.Foreign;

import java.nio.charset.Charset;

/**
 * Constant definitions for the standard {@link Charset charsets}. These
 * charsets are guaranteed to be available on every implementation of the Java
 * platform.
 */
public final class Charsets {

    private Charsets() {
        throw new NotInstantiableError(Charsets.class);
    }

    public static final Charset DEFAULT = Charset.defaultCharset();
    public static final Charset SYSTEM  = Foreign.getInstance().systemCharset();
    public static final Charset WIDE    = Foreign.getInstance().wideCharset();

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
