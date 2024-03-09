package unrefined.internal.windows;

import com.kenai.jffi.Library;
import unrefined.desktop.OSInfo;
import unrefined.util.NotInstantiableError;

public final class WindowsSupport {

    private WindowsSupport() {
        throw new NotInstantiableError(WindowsSupport.class);
    }

    public static final Library User32;
    public static final Library Kernel32;
    public static final Library Advapi32;
    static {
        if (OSInfo.IS_WINDOWS) {
            User32 = Library.getCachedInstance(System.mapLibraryName("User32"), Library.LAZY | Library.GLOBAL);
            Kernel32 = Library.getCachedInstance(System.mapLibraryName("Kernel32"), Library.LAZY | Library.GLOBAL);
            Advapi32 = Library.getCachedInstance(System.mapLibraryName("Advapi32"), Library.LAZY | Library.GLOBAL);
        }
        else {
            User32 = null;
            Kernel32 = null;
            Advapi32 = null;
        }
    }

}
