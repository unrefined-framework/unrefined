package unrefined.internal.windows;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Library;
import com.kenai.jffi.Type;
import unrefined.desktop.ABI;
import unrefined.desktop.AWTSupport;
import unrefined.desktop.ReflectionSupport;
import unrefined.desktop.OSInfo;
import unrefined.util.NotInstantiableError;

import java.awt.Component;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static unrefined.desktop.ForeignSupport.INVOKER;
import static unrefined.desktop.ForeignSupport.MEMORY_IO;
import static unrefined.desktop.UnsafeSupport.UNSAFE;
import static unrefined.internal.windows.WindowsSupport.User32;

public final class WindowsAWTSupport {

    private WindowsAWTSupport() {
        throw new NotInstantiableError(WindowsAWTSupport.class);
    }

    private static final Method getHWndMethod;
    static {
        if (OSInfo.IS_WINDOWS) {
            Method method;
            try {
                method = Class.forName("sun.awt.windows.WComponentPeer").getDeclaredMethod("getHWnd");
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                method = null;
            }
            getHWndMethod = method;
        }
        else getHWndMethod = null;
    }

    public static long getHWnd(Component component) {
        if (getHWndMethod == null || component == null) return 0;
        else {
            try {
                Object peer = AWTSupport.getPeer(component);
                if (peer == null) return 0;
                else return ReflectionSupport.invokeLongMethod(peer, getHWndMethod);
            }
            catch (InvocationTargetException e) {
                return 0;
            }
        }
    }

    static final Library Shcore;

    private static final Function GetDpiForWindow;
    private static final Function MonitorFromWindow;
    private static final Function GetDpiForMonitor;
    private static final long dpiX;
    private static final long dpiY;

    static {
        if (OSInfo.IS_WINDOWS) {
            long address = User32.getSymbolAddress("GetDpiForWindow");
            if (address == 0) {
                GetDpiForWindow = null;
                address = User32.getSymbolAddress("MonitorFromWindow");
                if (address == 0) {
                    MonitorFromWindow = null;
                    Shcore = null;
                    GetDpiForMonitor = null;
                    dpiX = 0;
                    dpiY = 0;
                }
                else {
                    MonitorFromWindow = new Function(address,
                            CallContext.getCallContext(Type.POINTER, new Type[] {Type.POINTER, Type.UINT32},
                                    CallingConvention.DEFAULT, false));
                    Shcore = Library.getCachedInstance(System.mapLibraryName("Shcore"), Library.GLOBAL | Library.LAZY);
                    address = Shcore.getSymbolAddress("GetDpiForMonitor");
                    if (address == 0) {
                        GetDpiForMonitor = null;
                        dpiX = 0;
                        dpiY = 0;
                    }
                    else {
                        GetDpiForMonitor = new Function(address,
                                CallContext.getCallContext(Type.UINT, new Type[] {Type.POINTER, Type.UINT, Type.POINTER, Type.POINTER},
                                        CallingConvention.DEFAULT, false));
                        dpiX = UNSAFE.allocateMemory(ABI.I);
                        dpiY = UNSAFE.allocateMemory(ABI.I);
                    }
                }
            }
            else {
                GetDpiForWindow = new Function(address,
                        CallContext.getCallContext(Type.UINT, new Type[] {Type.POINTER}, CallingConvention.DEFAULT, false));
                MonitorFromWindow = null;
                GetDpiForMonitor = null;
                Shcore = null;
                dpiX = 0;
                dpiY = 0;
            }
        }
        else {
            GetDpiForWindow = null;
            MonitorFromWindow = null;
            GetDpiForMonitor = null;
            Shcore = null;
            dpiX = 0;
            dpiY = 0;
        }
    }

    public static int getDotsPerInch(Component component) {
        AWTSupport.awtLock();
        try {
            long hWnd = getHWnd(component);
            if (hWnd != 0) {
                if (GetDpiForWindow != null) {
                    HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(GetDpiForWindow);
                    heapInvocationBuffer.putAddress(hWnd);
                    return INVOKER.invokeInt(GetDpiForWindow, heapInvocationBuffer);
                }
                if (GetDpiForMonitor != null) {
                    HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(MonitorFromWindow);
                    heapInvocationBuffer.putAddress(hWnd);
                    heapInvocationBuffer.putInt(1);
                    long hMonitor = INVOKER.invokeAddress(MonitorFromWindow, heapInvocationBuffer);
                    if (hMonitor != 0) {
                        heapInvocationBuffer = new HeapInvocationBuffer(GetDpiForMonitor);
                        heapInvocationBuffer.putAddress(hMonitor);
                        heapInvocationBuffer.putInt(0);
                        heapInvocationBuffer.putAddress(dpiX);
                        heapInvocationBuffer.putAddress(dpiY);
                        if (INVOKER.invokeInt(GetDpiForMonitor, heapInvocationBuffer) == 0) {
                            return MEMORY_IO.getInt(dpiX);
                        }
                    }
                }
            }
            return Toolkit.getDefaultToolkit().getScreenResolution();
        }
        finally {
            AWTSupport.awtUnlock();
        }
    }

    public static void patch() {
        WindowsFontSupport.patch();
    }

}
