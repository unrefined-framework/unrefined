package unrefined.internal.windows;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Library;
import com.kenai.jffi.Type;
import unrefined.desktop.OSInfo;
import unrefined.util.NotInstantiableError;

import static unrefined.desktop.ForeignSupport.INVOKER;

public final class WindowsSupport {

    private WindowsSupport() {
        throw new NotInstantiableError(WindowsSupport.class);
    }

    public static final Library User32;
    public static final Library Kernel32;
    public static final Library Advapi32;
    static {
        if (OSInfo.IS_WINDOWS) {
            if (OSInfo.IS_WINDOWS_CE) User32 = Kernel32 = Advapi32 =
                    Library.getCachedInstance(System.mapLibraryName("CoreDLL"), Library.LAZY | Library.GLOBAL);
            else {
                User32 = Library.getCachedInstance(System.mapLibraryName("User32"), Library.LAZY | Library.GLOBAL);
                Kernel32 = Library.getCachedInstance(System.mapLibraryName("Kernel32"), Library.LAZY | Library.GLOBAL);
                Advapi32 = Library.getCachedInstance(System.mapLibraryName("Advapi32"), Library.LAZY | Library.GLOBAL);
            }
        }
        else {
            User32 = null;
            Kernel32 = null;
            Advapi32 = null;
        }
    }

    public static final Function LocalFree;
    static {
        if (OSInfo.IS_WINDOWS) {
            LocalFree = new Function(Kernel32.getSymbolAddress("LocalFree"),
                    CallContext.getCallContext(Type.POINTER, new Type[] {Type.POINTER}, CallingConvention.DEFAULT, false));
        }
        else LocalFree = null;
    }

    public static boolean LocalFree(long hMem) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(LocalFree);
        heapInvocationBuffer.putAddress(hMem);
        long hLocal = INVOKER.invokeAddress(LocalFree, heapInvocationBuffer);
        return hLocal != 0;
    }

}
