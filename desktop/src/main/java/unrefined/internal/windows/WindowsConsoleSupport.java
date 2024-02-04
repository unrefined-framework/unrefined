package unrefined.internal.windows;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Type;
import unrefined.desktop.ShutdownGuard;
import unrefined.util.NotInstantiableError;

import static unrefined.desktop.ForeignSupport.INVOKER;
import static unrefined.desktop.ForeignSupport.MEMORY_IO;
import static unrefined.desktop.UnsafeSupport.UNSAFE;
import static unrefined.desktop.OSInfo.IS_WINDOWS;
import static unrefined.internal.posix.POSIXConsoleSupport.IS_CYGWIN;
import static unrefined.internal.windows.WindowsLibrary.Kernel32;

public final class WindowsConsoleSupport {

    private WindowsConsoleSupport() {
        throw new NotInstantiableError(WindowsConsoleSupport.class);
    }

    public static final int STD_INPUT_HANDLE = -10;
    public static final int STD_OUTPUT_HANDLE = -11;
    public static final int STD_ERROR_HANDLE = -12;

    public static final long INPUT;
    public static final long OUTPUT;
    public static final long ERROR;

    public static final boolean IS_MSYS_MINGW = IS_WINDOWS
            && System.getenv("MSYSTEM") != null
            && System.getenv("MSYSTEM").startsWith("MINGW");
    public static final boolean IS_MSYSTEM = IS_WINDOWS
            && System.getenv("MSYSTEM") != null
            && (System.getenv("MSYSTEM").startsWith("MINGW")
            || System.getenv("MSYSTEM").startsWith("MSYS"));
    public static final boolean IS_CONSOLE_EMULATOR = IS_WINDOWS && System.getenv("ConEmuPID") != null;

    public static final boolean IS_TERMINAL;
    public static final boolean IS_VIRTUAL_TERMINAL;
    public static final boolean IS_MINGW;
    public static final boolean IS_ANSI_TERMINAL;

    public static final boolean EMULATION_AVAILABLE = false; // FIXME

    private static final Function GetConsoleMode;
    private static final Function SetConsoleMode;

    static {
        if (IS_WINDOWS) {
            long GetStdHandle = Kernel32.getSymbolAddress("GetStdHandle");
            CallContext context = CallContext.getCallContext(Type.POINTER, new Type[] {Type.UINT32}, CallingConvention.STDCALL, false);
            HeapInvocationBuffer heapInvocationBuffer;
            heapInvocationBuffer = new HeapInvocationBuffer(context);
            heapInvocationBuffer.putInt(STD_INPUT_HANDLE);
            INPUT = INVOKER.invokeAddress(context, GetStdHandle, heapInvocationBuffer);
            heapInvocationBuffer = new HeapInvocationBuffer(context);
            heapInvocationBuffer.putInt(STD_OUTPUT_HANDLE);
            OUTPUT = INVOKER.invokeAddress(context, GetStdHandle, heapInvocationBuffer);
            heapInvocationBuffer = new HeapInvocationBuffer(context);
            heapInvocationBuffer.putInt(STD_ERROR_HANDLE);
            ERROR = INVOKER.invokeAddress(context, GetStdHandle, heapInvocationBuffer);
            GetConsoleMode = new Function(Kernel32.getSymbolAddress("GetConsoleMode"),
                    CallContext.getCallContext(Type.SINT, new Type[] {Type.POINTER, Type.POINTER}, CallingConvention.STDCALL, false));
            SetConsoleMode = new Function(Kernel32.getSymbolAddress("SetConsoleMode"),
                    CallContext.getCallContext(Type.SINT, new Type[] {Type.POINTER, Type.UINT32}, CallingConvention.STDCALL, false));
            long lpMode = UNSAFE.allocateMemory(Type.UINT32.size());
            try {
                IS_TERMINAL = GetConsoleMode(OUTPUT, lpMode) != 0;
                int mode = MEMORY_IO.getInt(lpMode);
                boolean vterm = false;
                if (IS_TERMINAL) {
                    if (SetConsoleMode(OUTPUT, mode | 0x0004) != 0) {
                        if (SetConsoleMode(ERROR, mode | 0x0004) != 0)
                            vterm = true;
                        else SetConsoleMode(OUTPUT, mode);
                    }
                }
                IS_VIRTUAL_TERMINAL = vterm;
                if (vterm) {
                    IS_MINGW = false;
                    /*
                    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SetConsoleMode(OUTPUT, mode);
                            SetConsoleMode(ERROR, mode);
                        }
                    }, "Unrefined Windows Virtual Terminal Cleanup"));

                     */
                    ShutdownGuard.register(new Runnable() {
                        @Override
                        public void run() {
                            SetConsoleMode(OUTPUT, mode);
                            SetConsoleMode(ERROR, mode);
                        }
                    });
                }
                else IS_MINGW = !IS_TERMINAL && (IS_CONSOLE_EMULATOR || IS_CYGWIN || IS_MSYSTEM);
            }
            finally {
                UNSAFE.freeMemory(lpMode);
            }
        }
        else {
            INPUT = 0;
            OUTPUT = 0;
            ERROR = 0;
            GetConsoleMode = null;
            IS_TERMINAL = false;
            IS_VIRTUAL_TERMINAL = false;
            IS_MINGW = false;
            SetConsoleMode = null;
        }
        IS_ANSI_TERMINAL = IS_VIRTUAL_TERMINAL || IS_MINGW;
    }

    public static int GetConsoleMode(long hConsoleHandle, long lpMode) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(GetConsoleMode);
        heapInvocationBuffer.putAddress(hConsoleHandle);
        heapInvocationBuffer.putAddress(lpMode);
        return INVOKER.invokeInt(GetConsoleMode, heapInvocationBuffer);
    }

    public static int SetConsoleMode(long hConsoleHandle, int dwMode) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(SetConsoleMode);
        heapInvocationBuffer.putAddress(hConsoleHandle);
        heapInvocationBuffer.putInt(dwMode);
        return INVOKER.invokeInt(SetConsoleMode, heapInvocationBuffer);
    }

}
