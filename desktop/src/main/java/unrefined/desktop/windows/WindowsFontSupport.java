package unrefined.desktop.windows;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Type;
import unrefined.desktop.ForeignSupport;
import unrefined.desktop.OSInfo;
import unrefined.desktop.ShutdownHook;
import unrefined.util.NotInstantiableError;
import unrefined.util.concurrent.FloatProducer;

import java.awt.Font;
import java.awt.Toolkit;
import java.util.concurrent.atomic.AtomicBoolean;

import static unrefined.desktop.ForeignSupport.INVOKER;
import static unrefined.desktop.ForeignSupport.MEMORY_IO;
import static unrefined.desktop.UnsafeSupport.UNSAFE;
import static unrefined.desktop.windows.WindowsRegistry.*;

public final class WindowsFontSupport {

    private WindowsFontSupport() {
        throw new NotInstantiableError(WindowsFontSupport.class);
    }

    private static final AtomicBoolean shutdown = new AtomicBoolean(false);

    private static volatile float fontScale;

    public static float getFontScale() {
        return fontScale;
    }

    static {
        if (OSInfo.IS_WINDOWS) {
            Function CreateEventW = new Function(WindowsSupport.Kernel32.getSymbolAddress("CreateEventW"),
                    CallContext.getCallContext(Type.POINTER, new Type[] {Type.POINTER, Type.UINT32, Type.UINT32, Type.POINTER},
                            CallingConvention.DEFAULT, false));
            Function RegNotifyChangeKeyValue = new Function(WindowsSupport.Advapi32.getSymbolAddress("RegNotifyChangeKeyValue"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.UINT32, Type.UINT32, Type.POINTER, Type.UINT32},
                            CallingConvention.DEFAULT, false));
            Function WaitForSingleObject = new Function(WindowsSupport.Kernel32.getSymbolAddress("WaitForSingleObject"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.UINT32},
                            CallingConvention.DEFAULT, false));
            Function CloseHandle = new Function(WindowsSupport.Kernel32.getSymbolAddress("CloseHandle"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER},
                            CallingConvention.DEFAULT, false));
            long lpSubKey = ForeignSupport.allocateWideCharString("SOFTWARE\\Microsoft\\Accessibility");
            long phkResult = UNSAFE.allocateMemory(Type.POINTER.size());
            try {
                if (WindowsRegistry.RegOpenKeyExW(HKEY_CURRENT_USER, lpSubKey, 0, KEY_QUERY_VALUE | KEY_NOTIFY, phkResult) == 0) {
                    HeapInvocationBuffer createBuffer = new HeapInvocationBuffer(CreateEventW);
                    createBuffer.putAddress(0);
                    createBuffer.putInt(1);
                    createBuffer.putInt(0);
                    createBuffer.putAddress(0);
                    long hEvent = INVOKER.invokeAddress(CreateEventW, createBuffer);
                    long hKeyAccessibility = MEMORY_IO.getAddress(phkResult);
                    long lpDWORDSize = UNSAFE.allocateMemory(Type.UINT32.size());
                    MEMORY_IO.putInt(lpDWORDSize, Type.UINT32.size());
                    long TextScaleFactorBufferDWORD = UNSAFE.allocateMemory(Type.UINT32.size());
                    long TextScaleFactorBufferDWORD1 = UNSAFE.allocateMemory(Type.UINT32.size());
                    long TextScaleFactorBufferCWSTR = ForeignSupport.allocateWideCharString("TextScaleFactor");
                    FloatProducer TextScaleFactor = () -> {
                        if (WindowsRegistry.RegQueryValueExW(hKeyAccessibility, TextScaleFactorBufferCWSTR,
                                0, TextScaleFactorBufferDWORD, TextScaleFactorBufferDWORD1, lpDWORDSize) == 0) {
                            if (MEMORY_IO.getInt(TextScaleFactorBufferDWORD) == 0x00000004 /* REG_DWORD */ ) {
                                int dword = MEMORY_IO.getInt(TextScaleFactorBufferDWORD1);
                                if (dword >= 100 && dword <= 225) return dword / 100f;
                            }
                        }
                        return 1;
                    };
                    fontScale = TextScaleFactor.getAsFloat();
                    Thread thread = new Thread(() -> {
                        HeapInvocationBuffer notifyBuffer = new HeapInvocationBuffer(RegNotifyChangeKeyValue);
                        notifyBuffer.putAddress(hKeyAccessibility);
                        notifyBuffer.putInt(0);
                        notifyBuffer.putInt(KEY_SET_VALUE | KEY_CREATE_SUB_KEY);
                        notifyBuffer.putAddress(hEvent);
                        notifyBuffer.putInt(1);
                        HeapInvocationBuffer waitBuffer = new HeapInvocationBuffer(WaitForSingleObject);
                        waitBuffer.putAddress(hEvent);
                        waitBuffer.putInt(-1);
                        while (!shutdown.get()) {
                            if (INVOKER.invokeInt(RegNotifyChangeKeyValue, notifyBuffer) != 0) continue;
                            if (INVOKER.invokeInt(WaitForSingleObject, waitBuffer) != 0xFFFFFFFF /* WAIT_FAILED */) {
                                if (!shutdown.get()) {
                                    fontScale = TextScaleFactor.getAsFloat();
                                }
                            }
                        }
                    }, "Unrefined Registry Daemon");
                    thread.setDaemon(true);
                    thread.start();
                    Runnable shutdown = () -> {
                        if (WindowsFontSupport.shutdown.compareAndSet(false, true)) {
                            try {
                                WindowsRegistry.RegCloseKey(hKeyAccessibility);
                            }
                            finally {
                                HeapInvocationBuffer closeBuffer = new HeapInvocationBuffer(CloseHandle);
                                closeBuffer.putAddress(hEvent);
                                INVOKER.invokeInt(CloseHandle, closeBuffer);
                            }
                        }
                    };
                    //Runtime.getRuntime().addShutdownHook(new Thread(shutdown, "Unrefined Registry Daemon Cleanup"));
                    ShutdownHook.register(shutdown);
                }
            }
            finally {
                UNSAFE.freeMemory(lpSubKey);
                UNSAFE.freeMemory(phkResult);
            }
        }
    }

    public static final Font SYSTEM_FONT = (Font) Toolkit.getDefaultToolkit().getDesktopProperty(OSInfo.IS_WINDOWS_PE ? "win.defaultGUI.font" : "win.messagebox.font");

    public static void patch() {
    }

}
