package org.example.desktop.foreign;

import unrefined.app.Logger;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.StringCompat;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Redirect;
import unrefined.util.foreign.Symbol;

import java.io.IOException;

/**
 * Symbols are internally managed by UXGL.
 * Load the library, get the symbol, and do whatever you want!
 */
public class SymbolManagement {

    public static final boolean IS_WINDOWS = StringCompat.stripLeading(System.getProperty("os.name")).startsWith("Windows");
    public static final boolean IS_LINUX = System.getProperty("os.name").equalsIgnoreCase("linux");

    public static void main(String[] args) throws IOException {
        DesktopRuntime.setup(args);              // Initialize the UXGL runtime environment
        Foreign foreign = Foreign.getInstance(); // Get the platform-dependent FFI factory

        foreign.loadLibrary(foreign.mapLibraryName(IS_WINDOWS ? "Kernel32" : (IS_LINUX ? "libc.so.6" : "c")),
                Foreign.Loader.LINKER); // Load the library for symbols we want.
        // The symbols whether ClassLoader-managed or linker-managed depend on the loader type.
        // Also, Loader#CLASS follows java.library.path, Loader#LINKER follows native linker, to search for libraries.

        Symbol getpid = foreign.downcallHandle(foreign.getSymbolAddress(IS_WINDOWS ? "GetCurrentProcessId" : "getpid"),
                IS_WINDOWS ? int.class : long.class);
        foreign.register(IS_WINDOWS ? Windows.class : POSIX.class);
        final long pid = IS_WINDOWS ? Windows.pidInt() : POSIX.pidLong();
        foreign.unregister(SymbolManagement.class); // We're able to cache the pid value, so it's okay to unregister
        Logger.defaultInstance().info("UXGL FFI", "PID: " + pid);
        Logger.defaultInstance().info("UXGL FFI", "Direct Mapping result == Handle Mapping result: " + (getpid.invokeNativeLong() == pid));
    }

    private static final class Windows {
        @Redirect("GetCurrentProcessId")     // Redirect to the real symbol name
        public static native int pidInt();
    }

    private static final class POSIX {
        @Redirect("getpid")
        public static native long pidLong(); // Like above
    }

}
