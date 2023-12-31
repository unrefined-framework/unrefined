package unrefined.internal.X11;

import com.kenai.jffi.Library;
import unrefined.internal.OperatingSystem;
import unrefined.util.NotInstantiableError;

import java.awt.GraphicsEnvironment;

public final class X11Library {

    private X11Library() {
        throw new NotInstantiableError(X11Library.class);
    }

    public static final Library X11;
    static {
        if (OperatingSystem.IS_X11 && !GraphicsEnvironment.isHeadless())
            X11 = Library.getCachedInstance(System.mapLibraryName("X11"), Library.LAZY | Library.GLOBAL);
        else X11 = null;
    }
    public static final Library Xcursor = X11CursorSupport.Xcursor;

}
