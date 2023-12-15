package unrefined.internal.X11;

import unrefined.internal.SystemUtils;
import unrefined.util.NotInstantiableError;

import java.awt.GraphicsEnvironment;
import java.lang.foreign.Arena;
import java.lang.foreign.SymbolLookup;

public final class X11Library {

    private X11Library() {
        throw new NotInstantiableError(X11Library.class);
    }

    public static final SymbolLookup X11_LOOKUP;
    static {
        if (SystemUtils.IS_X11 && !GraphicsEnvironment.isHeadless()) X11_LOOKUP = SymbolLookup.libraryLookup(System.mapLibraryName("X11"), Arena.global());
        else X11_LOOKUP = null;
    }
    public static final SymbolLookup XCURSOR_LOOKUP = X11CursorUtils.XCURSOR_LOOKUP;

}
