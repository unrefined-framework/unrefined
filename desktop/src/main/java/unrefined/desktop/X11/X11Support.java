package unrefined.desktop.X11;

import com.kenai.jffi.Library;
import unrefined.desktop.ForeignSupport;
import unrefined.desktop.OSInfo;
import unrefined.util.NotInstantiableError;

import java.awt.GraphicsEnvironment;

public final class X11Support {

    private X11Support() {
        throw new NotInstantiableError(X11Support.class);
    }

    public static final Library X11;
    static {
        if (OSInfo.IS_X11 && !GraphicsEnvironment.isHeadless()) {
            X11 = Library.getCachedInstance(ForeignSupport.mapLibraryName("X11"), Library.GLOBAL | Library.LAZY);
        }
        else {
            X11 = null;
        }
    }

}
