package unrefined.internal.windows;

import unrefined.desktop.ReflectionSupport;
import unrefined.internal.AWTUtils;
import unrefined.internal.SystemUtils;
import unrefined.util.NotInstantiableError;

import java.awt.Component;
import java.awt.Toolkit;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static unrefined.internal.windows.WindowsLibrary.USER32_LOOKUP;

public final class WindowsAWTUtils {

    private WindowsAWTUtils() {
        throw new NotInstantiableError(WindowsAWTUtils.class);
    }

    private static final Method getHWndMethod;
    static {
        if (SystemUtils.IS_WINDOWS) {
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
                Object peer = AWTUtils.getPeer(component);
                if (peer == null) return 0;
                else return ReflectionSupport.invokeLongMethod(peer, getHWndMethod);
            }
            catch (InvocationTargetException e) {
                return 0;
            }
        }
    }

    private static final Linker LINKER = Linker.nativeLinker();

    static final SymbolLookup SHCORE_LOOKUP;
    private static final MethodHandle GetDpiForWindowMethodHandle;
    private static final MethodHandle MonitorFromWindowMethodHandle;
    private static final MethodHandle GetDpiForMonitorMethodHandle;
    private static final MemorySegment DPI_X_BUFFER;
    private static final MemorySegment DPI_Y_BUFFER;
    static {
        if (SystemUtils.IS_WINDOWS) {
            GetDpiForWindowMethodHandle = USER32_LOOKUP.find("GetDpiForWindow").map(
                    symbolSegment -> LINKER.downcallHandle(symbolSegment, FunctionDescriptor.of(JAVA_INT, ADDRESS))).orElse(null);
            if (GetDpiForWindowMethodHandle == null) {
                MonitorFromWindowMethodHandle = USER32_LOOKUP.find("MonitorFromWindow").map(
                        symbolSegment -> LINKER.downcallHandle(symbolSegment, FunctionDescriptor.of(ADDRESS, ADDRESS, JAVA_INT))).orElse(null);
                if (MonitorFromWindowMethodHandle == null) {
                    SHCORE_LOOKUP = null;
                    GetDpiForMonitorMethodHandle = null;
                }
                else {
                    SHCORE_LOOKUP = SymbolLookup.libraryLookup(System.mapLibraryName("Shcore"), Arena.global());
                    GetDpiForMonitorMethodHandle = SHCORE_LOOKUP.find("GetDpiForMonitor").map(
                            symbolSegment -> LINKER.downcallHandle(symbolSegment, FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT, ADDRESS, ADDRESS))
                    ).orElse(null);
                }
                if (GetDpiForMonitorMethodHandle == null) {
                    DPI_X_BUFFER = null;
                    DPI_Y_BUFFER = null;
                }
                else {
                    DPI_X_BUFFER = Arena.global().allocate(JAVA_INT);
                    DPI_Y_BUFFER = Arena.global().allocate(JAVA_INT);
                }
            }
            else {
                MonitorFromWindowMethodHandle = null;
                GetDpiForMonitorMethodHandle = null;
                SHCORE_LOOKUP = null;
                DPI_X_BUFFER = null;
                DPI_Y_BUFFER = null;
            }
        }
        else {
            GetDpiForWindowMethodHandle = null;
            MonitorFromWindowMethodHandle = null;
            GetDpiForMonitorMethodHandle = null;
            SHCORE_LOOKUP = null;
            DPI_X_BUFFER = null;
            DPI_Y_BUFFER = null;
        }
    }

    public static int getDotsPerInch(Component component) {
        long hWnd = getHWnd(component);
        if (hWnd != 0) {
            if (GetDpiForWindowMethodHandle != null) {
                try {
                    return (int) GetDpiForWindowMethodHandle.invoke(MemorySegment.ofAddress(hWnd));
                } catch (Throwable ignored) {
                }
            }
            if (GetDpiForMonitorMethodHandle != null) {
                try {
                    MemorySegment hMonitor = (MemorySegment) MonitorFromWindowMethodHandle.invoke(MemorySegment.ofAddress(hWnd), 1);
                    if (!hMonitor.equals(MemorySegment.NULL)) {
                        if ((int) GetDpiForMonitorMethodHandle.invoke(hMonitor, 0, DPI_X_BUFFER, DPI_Y_BUFFER) == 0)
                            return DPI_X_BUFFER.get(JAVA_INT, 0);
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        return Toolkit.getDefaultToolkit().getScreenResolution();
    }

    public static void patch() {
        WindowsFontUtils.patch();
    }

}
