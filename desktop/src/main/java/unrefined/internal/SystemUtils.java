package unrefined.internal;

import unrefined.util.NotInstantiableError;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public final class SystemUtils {

    private SystemUtils() {
        throw new NotInstantiableError(SystemUtils.class);
    }

    private static final String OS_NAME = System.getProperty("os.name");
    private static final String OS_VERSION = System.getProperty("os.version");
    private static final long OS_VERSION_LONG;
    static {
        int major = 1;
        int minor = 0;
        int micro = 0;
        int patch = 0;
        try {
            StringTokenizer st = new StringTokenizer(SystemUtils.OS_VERSION, "._-+");
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
    public static final boolean IS_MAC = OS_NAME.startsWith("Mac") || OS_NAME.startsWith("Darwin");
    public static final boolean IS_X11 = !IS_WINDOWS && !IS_MAC; // Currently Wayland isn't supported

    public static String normalize(String pathname) {
        return (SystemUtils.IS_WINDOWS || SystemUtils.IS_MAC) ? pathname : FileUtils.normalizeToUNIXStyle(pathname);
    }

    public static boolean checkOSVersion(int major, int minor, int micro, int patch) {
        return OS_VERSION_LONG >= toVersionLong(major, minor, micro, patch);
    }

    private static long toVersionLong(int major, int minor, int micro, int patch) {
        return ((long) major << 48) | ((long) minor << 32) | ((long) micro << 16) | patch;
    }

}
