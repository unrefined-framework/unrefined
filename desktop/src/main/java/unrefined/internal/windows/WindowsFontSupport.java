package unrefined.internal.windows;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Type;
import unrefined.desktop.ForeignSupport;
import unrefined.desktop.ShutdownGuard;
import unrefined.desktop.OSInfo;
import unrefined.util.NotInstantiableError;

import java.awt.Font;
import java.awt.Toolkit;
import java.util.concurrent.atomic.AtomicBoolean;

import static unrefined.desktop.ForeignSupport.INVOKER;
import static unrefined.desktop.ForeignSupport.MEMORY_IO;
import static unrefined.desktop.UnsafeSupport.UNSAFE;
import static unrefined.internal.windows.WindowsLibrary.*;

public final class WindowsFontSupport {

    private WindowsFontSupport() {
        throw new NotInstantiableError(WindowsFontSupport.class);
    }

    private static final long TextScaleFactorBufferDWORD;
    private static final long TextScaleFactorBufferDWORD1;
    private static final long TextScaleFactorBufferCWSTR;
    private static final Function SystemParametersInfoW;
    private static final long LOGFONTW;
    private static final Function RegOpenKeyExW;
    private static final long hKeyAccessibility;
    private static final long hEvent;
    private static final Function RegQueryValueExW;
    private static final Function CreateEventW;
    private static final Function RegNotifyChangeKeyValue;
    private static final Function WaitForSingleObject;
    private static final Function RegCloseKey;
    private static final Function CloseHandle;

    static {
        if (OSInfo.IS_WINDOWS) {
            TextScaleFactorBufferDWORD = UNSAFE.allocateMemory(Type.UINT32.size());
            TextScaleFactorBufferDWORD1 = UNSAFE.allocateMemory(Type.UINT32.size());
            TextScaleFactorBufferCWSTR = ForeignSupport.allocateWideCharString("TextScaleFactor");
            SystemParametersInfoW = new Function(User32.getSymbolAddress("SystemParametersInfoW"),
                    CallContext.getCallContext(Type.UINT32,
                            new Type[] {Type.UINT32, Type.UINT32, Type.POINTER, Type.UINT32},
                            CallingConvention.DEFAULT, false));
            LOGFONTW = UNSAFE.allocateMemory(92);
            RegOpenKeyExW = new Function(Advapi32.getSymbolAddress("RegOpenKeyExW"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.POINTER, Type.UINT32, Type.POINTER},
                            CallingConvention.DEFAULT, false));
            RegQueryValueExW = new Function(Advapi32.getSymbolAddress("RegQueryValueExW"),
                    CallContext.getCallContext(Type.UINT32,
                            new Type[] {Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER},
                            CallingConvention.DEFAULT, false));
            RegNotifyChangeKeyValue = new Function(Advapi32.getSymbolAddress("RegNotifyChangeKeyValue"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.UINT32, Type.UINT32, Type.POINTER, Type.UINT32},
                            CallingConvention.DEFAULT, false));
            RegCloseKey = new Function(Advapi32.getSymbolAddress("RegCloseKey"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER}, CallingConvention.DEFAULT, false));
            CreateEventW = new Function(Kernel32.getSymbolAddress("CreateEventW"),
                    CallContext.getCallContext(Type.POINTER, new Type[] {Type.POINTER, Type.UINT32, Type.UINT32, Type.POINTER},
                            CallingConvention.DEFAULT, false));
            WaitForSingleObject = new Function(Kernel32.getSymbolAddress("WaitForSingleObject"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.UINT32},
                            CallingConvention.DEFAULT, false));
            CloseHandle = new Function(Kernel32.getSymbolAddress("CloseHandle"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER},
                            CallingConvention.DEFAULT, false));
            long hKeyAccessibilityBuffer;
            long hEventBuffer;
            long lpSubKey = ForeignSupport.allocateWideCharString("SOFTWARE\\Microsoft\\Accessibility");
            long phkResult = UNSAFE.allocateMemory(Type.POINTER.size());
            try {
                HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegOpenKeyExW);
                heapInvocationBuffer.putAddress(0x80000001L);
                heapInvocationBuffer.putAddress(lpSubKey);
                heapInvocationBuffer.putInt(0);
                heapInvocationBuffer.putInt(0x20019);
                heapInvocationBuffer.putAddress(phkResult);
                if (INVOKER.invokeInt(RegOpenKeyExW, heapInvocationBuffer) == 0) {
                    heapInvocationBuffer = new HeapInvocationBuffer(CreateEventW);
                    heapInvocationBuffer.putAddress(0);
                    heapInvocationBuffer.putInt(1);
                    heapInvocationBuffer.putInt(0);
                    heapInvocationBuffer.putAddress(0);
                    hEventBuffer = INVOKER.invokeAddress(CreateEventW, heapInvocationBuffer);
                    hKeyAccessibilityBuffer = UNSAFE.allocateMemory(Type.POINTER.size());
                    MEMORY_IO.putAddress(hKeyAccessibilityBuffer, phkResult);
                }
                else {
                    hKeyAccessibilityBuffer = 0;
                    hEventBuffer = 0;
                }
            }
            finally {
                UNSAFE.freeMemory(lpSubKey);
                UNSAFE.freeMemory(phkResult);
            }
            hKeyAccessibility = hKeyAccessibilityBuffer;
            hEvent = hEventBuffer;
            daemon();
        }
        else {
            TextScaleFactorBufferDWORD = 0;
            TextScaleFactorBufferDWORD1 = 0;
            TextScaleFactorBufferCWSTR = 0;
            SystemParametersInfoW = null;
            LOGFONTW = 0;
            RegOpenKeyExW = null;
            hKeyAccessibility = 0;
            hEvent = 0;
            RegQueryValueExW = null;
            CreateEventW = null;
            RegNotifyChangeKeyValue = null;
            WaitForSingleObject = null;
            RegCloseKey = null;
            CloseHandle = null;
        }
    }

    public static final Font ICONTITLELOGFONT = Font.decode(getDefaultFontName());

    private static String getDefaultFontName() {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(SystemParametersInfoW);
        heapInvocationBuffer.putAddress(0x001F);
        heapInvocationBuffer.putInt(96);
        heapInvocationBuffer.putAddress(LOGFONTW);
        heapInvocationBuffer.putInt(0);
        if (INVOKER.invokeInt(SystemParametersInfoW, heapInvocationBuffer) != 0) {
            boolean bold = MEMORY_IO.getInt(LOGFONTW + 16) > 500;
            boolean italic = MEMORY_IO.getInt(LOGFONTW + 20) != 0;
            int size = (int) (ForeignSupport.wcslen(LOGFONTW + 28)) * OSInfo.WIDE_CHAR_SIZE;
            byte[] bytes = new byte[size];
            MEMORY_IO.getByteArray(LOGFONTW + 28, bytes, 0, size);
            StringBuilder builder = new StringBuilder(new String(bytes, OSInfo.WIDE_CHARSET));
            if (bold || italic) builder.append(" ");
            if (bold) builder.append("Bold");
            if (italic) builder.append("Italic");
            return builder.toString();
        }
        Font font = (Font) Toolkit.getDefaultToolkit().getDesktopProperty(OSInfo.IS_WINDOWS_PE ? "win.defaultGUI.font" : "win.messagebox.font");
        return font == null ? null : font.getFontName();
    }

    private static volatile float fontScale = 1;

    public static float getFontScale() {
        return fontScale;
    }

    private static int getTextScaleFactor() {
        if (hKeyAccessibility != 0) {
            HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegQueryValueExW);
            heapInvocationBuffer.putAddress(hKeyAccessibility);
            heapInvocationBuffer.putAddress(TextScaleFactorBufferCWSTR);
            heapInvocationBuffer.putInt(0);
            heapInvocationBuffer.putAddress(TextScaleFactorBufferDWORD);
            heapInvocationBuffer.putAddress(TextScaleFactorBufferDWORD1);
            heapInvocationBuffer.putInt(Type.UINT32.size());
            if (INVOKER.invokeInt(RegQueryValueExW, heapInvocationBuffer) == 0) {
                int dw = MEMORY_IO.getInt(TextScaleFactorBufferDWORD1);
                if (MEMORY_IO.getInt(TextScaleFactorBufferDWORD) == 0x00000004 && dw >= 100 && dw <= 225) return dw;
            }
        }
        return 100;
    }

    private static void daemon() {
        if (hKeyAccessibility != 0) {
            Thread thread = new Thread(() -> {
                final int dwFilter = 0x00000001 | 0x00000002 | 0x00000004 | 0x00000008;
                HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegNotifyChangeKeyValue);
                heapInvocationBuffer.putAddress(hKeyAccessibility);
                heapInvocationBuffer.putInt(1);
                heapInvocationBuffer.putInt(dwFilter);
                heapInvocationBuffer.putAddress(hEvent);
                heapInvocationBuffer.putInt(1);
                HeapInvocationBuffer heapInvocationBuffer1 = new HeapInvocationBuffer(WaitForSingleObject);
                heapInvocationBuffer1.putAddress(hEvent);
                heapInvocationBuffer1.putInt(-1);
                while (!shutdown.get()) {
                    if (INVOKER.invokeInt(RegNotifyChangeKeyValue, heapInvocationBuffer) != 0) continue;
                    if (INVOKER.invokeInt(WaitForSingleObject, heapInvocationBuffer1) != 0xFFFFFFFF) {
                        fontScale = getTextScaleFactor() / 100f;
                    }
                }
            }, "Unrefined Registry Daemon");
            thread.setDaemon(true);
            thread.start();
            //Runtime.getRuntime().addShutdownHook(new Thread(WindowsFontSupport::shutdown, "Unrefined Registry Daemon Cleanup"));
            ShutdownGuard.register(WindowsFontSupport::shutdown);
        }
    }

    private static final AtomicBoolean shutdown = new AtomicBoolean(false);

    private static void shutdown() {
        if (shutdown.compareAndSet(false, true)) {
            HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegCloseKey);
            heapInvocationBuffer.putAddress(hKeyAccessibility);
            try {
                INVOKER.invokeInt(RegCloseKey, heapInvocationBuffer);
            }
            finally {
                heapInvocationBuffer = new HeapInvocationBuffer(CloseHandle);
                heapInvocationBuffer.putAddress(hEvent);
                INVOKER.invokeInt(CloseHandle, heapInvocationBuffer);
            }
        }
    }

    public static void patch() {
    }

}
