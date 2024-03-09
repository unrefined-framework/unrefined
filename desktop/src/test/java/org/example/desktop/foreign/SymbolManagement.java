package org.example.desktop.foreign;

import unrefined.app.Logger;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.Strings;
import unrefined.util.concurrent.LongProducer;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Library;
import unrefined.util.foreign.Symbol;

import java.io.IOException;

/**
 * Symbols are internally managed by Unrefined.
 * Load the library, get the symbol, and do whatever you want!
 */
public class SymbolManagement {

    public static final boolean IS_WINDOWS = Strings.stripLeading(System.getProperty("os.name")).startsWith("Windows");
    public static final boolean IS_LINUX = System.getProperty("os.name").equalsIgnoreCase("linux");

    public static void main(String[] args) throws IOException {
        DesktopRuntime.initialize(args);              // Initialize the Unrefined runtime environment
        Foreign foreign = Foreign.getInstance(); // Get the platform-dependent FFI factory

        foreign.loadLibrary(foreign.mapLibraryName(IS_WINDOWS ? "Kernel32" : (IS_LINUX ? "libc.so.6" : "c")),
                Foreign.Loader.LINKER); // Load the library for symbols we want.
        // The symbols whether ClassLoader-managed or linker-managed depend on the loader type.
        // Also, Loader#CLASS follows java.library.path, Loader#LINKER follows native linker, to search for libraries.

        Symbol getpid = foreign.downcallHandle(foreign.getSymbolAddress(IS_WINDOWS ? "GetCurrentProcessId" : "getpid"),
                IS_WINDOWS ? int.class : long.class);
        LongProducer pidProducer = IS_WINDOWS ? new LongProducer() {
            final Windows windows = foreign.downcallProxy(Windows.class);
            @Override
            public long getAsLong() {
                return windows.pidInt();
            }
        } : new LongProducer() {
            final POSIX posix = foreign.downcallProxy(POSIX.class);
            @Override
            public long getAsLong() {
                return posix.pidLong();
            }
        };
        Logger.defaultInstance().info("Unrefined FFI", "PID: " + getpid.invokeNativeLong());
        Logger.defaultInstance().info("Unrefined FFI", "Library Mapping result == Handle Mapping result: " + (getpid.invokeNativeLong() == pidProducer.getAsLong()));
    }

    private interface Windows extends Library {
        @Redirect("GetCurrentProcessId")     // Redirect to the real symbol name
        int pidInt();
    }

    private interface POSIX extends Library {
        @Redirect("getpid")                  // Like above
        long pidLong();
    }

}
