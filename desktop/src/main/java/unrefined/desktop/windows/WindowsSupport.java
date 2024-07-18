package unrefined.desktop.windows;

import com.kenai.jffi.Library;
import unrefined.desktop.ForeignSupport;
import unrefined.desktop.OSInfo;
import unrefined.util.NotInstantiableError;

public final class WindowsSupport {

    private WindowsSupport() {
        throw new NotInstantiableError(WindowsSupport.class);
    }

    public static final Library CoreDLL;
    public static final Library User32;
    public static final Library Kernel32;
    public static final Library Advapi32;

    static {
        if (OSInfo.IS_WINDOWS) {
            if (OSInfo.IS_WINDOWS_CE) {
                CoreDLL = Library.getCachedInstance(ForeignSupport.mapLibraryName("CoreDLL"), Library.GLOBAL | Library.LAZY);
                User32 = Kernel32 = Advapi32 = CoreDLL;
            }
            else {
                User32 = Library.getCachedInstance(ForeignSupport.mapLibraryName("User32"), Library.GLOBAL | Library.LAZY);
                Kernel32 = Library.getCachedInstance(ForeignSupport.mapLibraryName("Kernel32"), Library.GLOBAL | Library.LAZY);
                Advapi32 = Library.getCachedInstance(ForeignSupport.mapLibraryName("Advapi32"), Library.GLOBAL | Library.LAZY);
                CoreDLL = null;
            }
        }
        else {
            CoreDLL = User32 = Kernel32 = Advapi32 = null;
        }
    }

}
