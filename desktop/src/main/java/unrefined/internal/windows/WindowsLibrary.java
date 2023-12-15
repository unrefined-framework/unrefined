package unrefined.internal.windows;

import unrefined.internal.SystemUtils;
import unrefined.util.NotInstantiableError;

import java.lang.foreign.Arena;
import java.lang.foreign.SymbolLookup;

public final class WindowsLibrary {

    private WindowsLibrary() {
        throw new NotInstantiableError(WindowsLibrary.class);
    }

    public static final SymbolLookup USER32_LOOKUP;
    public static final SymbolLookup KERNEL32_LOOKUP;
    public static final SymbolLookup ADVAPI32_LOOKUP;
    static {
        if (SystemUtils.IS_WINDOWS) {
            USER32_LOOKUP = SymbolLookup.libraryLookup(System.mapLibraryName("User32"), Arena.global());
            KERNEL32_LOOKUP = SymbolLookup.libraryLookup(System.mapLibraryName("Kernel32"), Arena.global());
            ADVAPI32_LOOKUP = SymbolLookup.libraryLookup(System.mapLibraryName("Advapi32"), Arena.global());
        }
        else {
            USER32_LOOKUP = null;
            KERNEL32_LOOKUP = null;
            ADVAPI32_LOOKUP = null;
        }
    }
    public static final SymbolLookup SHCORE_LOOKUP = WindowsAWTUtils.SHCORE_LOOKUP;

}
