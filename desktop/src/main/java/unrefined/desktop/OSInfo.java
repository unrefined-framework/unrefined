package unrefined.desktop;

import unrefined.util.NotInstantiableError;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public final class OSInfo {

    private OSInfo() {
        throw new NotInstantiableError(OSInfo.class);
    }

    private static final String OS_NAME = System.getProperty("os.name");
    private static final String OS_VERSION = System.getProperty("os.version");
    public static final long OS_VERSION_LONG;
    static {
        int major = 1;
        int minor = 0;
        int micro = 0;
        int patch = 0;
        try {
            StringTokenizer st = new StringTokenizer(OSInfo.OS_VERSION, "._-+");
            major = Integer.parseInt(st.nextToken());
            minor = Integer.parseInt(st.nextToken());
            micro = Integer.parseInt(st.nextToken());
            patch = Integer.parseInt(st.nextToken());
        }
        catch (NumberFormatException | NoSuchElementException ignored) {
        }
        OS_VERSION_LONG = toVersionLong(major, minor, micro, patch);
    }

    public static final boolean IS_WINDOWS = OS_NAME.startsWith("Windows");
    public static final boolean IS_WINDOWS_PE = IS_WINDOWS && "X:\\Windows\\System32".equalsIgnoreCase(System.getProperty("user.dir"));
    public static final boolean IS_WINDOWS_CE = OS_NAME.startsWith("Windows CE");
    public static final boolean IS_MAC = OS_NAME.startsWith("Mac") || OS_NAME.startsWith("Darwin");
    public static final boolean IS_LINUX = OS_NAME.startsWith("Linux");
    public static final boolean IS_SOLARIS = OS_NAME.startsWith("Solaris") || OS_NAME.startsWith("SunOS");
    public static final boolean IS_AIX = OS_NAME.startsWith("AIX");
    public static final boolean IS_FREEBSD = OS_NAME.startsWith("FreeBSD");
    public static final boolean IS_OPENBSD = OS_NAME.startsWith("OpenBSD");
    public static final boolean IS_NETBSD = OS_NAME.startsWith("NetBSD");
    public static final boolean IS_HP_UX = OS_NAME.startsWith("HP-UX");
    public static final boolean IS_X11 = !IS_WINDOWS && !IS_MAC; // Currently Wayland isn't supported

    public static final Charset NATIVE_CHARSET = Charset.forName(System.getProperty("native.encoding", System.getProperty("sun.jnu.encoding")));
    private static final byte[] NATIVE_STRING_TERMINATOR = "\0".getBytes(NATIVE_CHARSET);
    public static final int NATIVE_CHAR_SIZE = NATIVE_STRING_TERMINATOR.length;
    public static final Charset WIDE_CHARSET = IS_WINDOWS ? StandardCharsets.UTF_16LE : Charset.forName(ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN) ? "UTF-32BE" : "UTF-32LE");
    private static final byte[] WIDE_CHAR_STRING_TERMINATOR = "\0".getBytes(WIDE_CHARSET);
    public static final int WIDE_CHAR_SIZE = WIDE_CHAR_STRING_TERMINATOR.length;

    public static boolean isNativeStringCompatible(byte[] terminator) {
        return Arrays.equals(terminator, NATIVE_STRING_TERMINATOR);
    }

    public static boolean isWideCharStringCompatible(byte[] terminator) {
        return Arrays.equals(terminator, WIDE_CHAR_STRING_TERMINATOR);
    }

    public static String normalize(String pathname) {
        return (OSInfo.IS_WINDOWS || OSInfo.IS_MAC) ? pathname : normalizeToUNIXStyle(pathname);
    }

    private static String normalizeToUNIXStyle(String pathname) {
        if (pathname == null) return null;
        return pathname.toLowerCase(Locale.ENGLISH).replace(" ", "-");
    }

    public static boolean checkOSVersion(int major, int minor, int micro, int patch) {
        return OS_VERSION_LONG >= toVersionLong(major, minor, micro, patch);
    }

    private static long toVersionLong(int major, int minor, int micro, int patch) {
        return ((long) major << 48) | ((long) minor << 32) | ((long) micro << 16) | patch;
    }

    public static final File NULL_FILE = IS_WINDOWS ? new File(File.listRoots()[0], "NUL") : new File("/dev/null");

    public static final Charset CONSOLE_CHARSET =
            Charset.forName(System.getProperty("stdout.encoding", System.getProperty("sun.stdout.encoding",
            System.getProperty("native.encoding", System.getProperty("sun.jnu.encoding")))));


}
