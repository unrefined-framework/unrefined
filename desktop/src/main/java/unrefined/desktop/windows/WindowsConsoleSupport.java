package unrefined.desktop.windows;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Type;
import unrefined.desktop.ShutdownHook;
import unrefined.desktop.posix.PosixConsoleSupport;
import unrefined.util.NotInstantiableError;

import static unrefined.desktop.ForeignSupport.INVOKER;
import static unrefined.desktop.ForeignSupport.MEMORY_IO;
import static unrefined.desktop.UnsafeSupport.UNSAFE;
import static unrefined.desktop.OSInfo.IS_WINDOWS;

public final class WindowsConsoleSupport {

    private WindowsConsoleSupport() {
        throw new NotInstantiableError(WindowsConsoleSupport.class);
    }

    public static final int FOREGROUND_BLUE = 0x0001;
    public static final int FOREGROUND_GREEN = 0x0002;
    public static final int FOREGROUND_RED = 0x0004;
    public static final int FOREGROUND_INTENSITY = 0x0008;
    public static final int BACKGROUND_BLUE = 0x0010;
    public static final int BACKGROUND_GREEN = 0x0020;
    public static final int BACKGROUND_RED = 0x0040;
    public static final int BACKGROUND_INTENSITY = 0x0080;

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

    private static final int STD_INPUT_HANDLE = -10;
    private static final int STD_OUTPUT_HANDLE = -11;
    private static final int STD_ERROR_HANDLE = -12;

    static {
        if (IS_WINDOWS) {
            Function GetStdHandle = new Function(WindowsSupport.Kernel32.getSymbolAddress("GetStdHandle"),
                    CallContext.getCallContext(Type.POINTER, new Type[] {Type.UINT32}, CallingConvention.STDCALL, false));
            HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(GetStdHandle);
            heapInvocationBuffer.putInt(STD_INPUT_HANDLE);
            INPUT = INVOKER.invokeAddress(GetStdHandle, heapInvocationBuffer);
            heapInvocationBuffer = new HeapInvocationBuffer(GetStdHandle);
            heapInvocationBuffer.putInt(STD_OUTPUT_HANDLE);
            OUTPUT = INVOKER.invokeAddress(GetStdHandle, heapInvocationBuffer);
            heapInvocationBuffer = new HeapInvocationBuffer(GetStdHandle);
            heapInvocationBuffer.putInt(STD_ERROR_HANDLE);
            ERROR = INVOKER.invokeAddress(GetStdHandle, heapInvocationBuffer);
            long lpMode = UNSAFE.allocateMemory(Type.UINT32.size());
            try {
                IS_TERMINAL = WindowsConsole.GetConsoleMode(OUTPUT, lpMode) != 0;
                int mode = MEMORY_IO.getInt(lpMode);
                boolean vterm = false;
                if (IS_TERMINAL) {
                    if (WindowsConsole.SetConsoleMode(OUTPUT, mode | 0x0004 /* ENABLE_ECHO_INPUT */) != 0) {
                        if (WindowsConsole.SetConsoleMode(ERROR, mode | 0x0004 /* ENABLE_ECHO_INPUT */) != 0)
                            vterm = true;
                        else WindowsConsole.SetConsoleMode(OUTPUT, mode);
                    }
                }
                IS_VIRTUAL_TERMINAL = vterm;
                if (vterm) {
                    IS_MINGW = false;
                    /*
                    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            WindowsConsole.SetConsoleMode(OUTPUT, mode);
                            WindowsConsole.SetConsoleMode(ERROR, mode);
                        }
                    }, "Unrefined Windows Virtual Terminal Cleanup"));

                     */
                    ShutdownHook.register(new Runnable() {
                        @Override
                        public void run() {
                            WindowsConsole.SetConsoleMode(OUTPUT, mode);
                            WindowsConsole.SetConsoleMode(ERROR, mode);
                        }
                    });
                }
                else IS_MINGW = !IS_TERMINAL && (IS_CONSOLE_EMULATOR || PosixConsoleSupport.IS_CYGWIN || IS_MSYSTEM);
            }
            finally {
                UNSAFE.freeMemory(lpMode);
            }
        }
        else {
            INPUT = 0;
            OUTPUT = 0;
            ERROR = 0;
            IS_TERMINAL = false;
            IS_VIRTUAL_TERMINAL = false;
            IS_MINGW = false;
        }
        IS_ANSI_TERMINAL = IS_VIRTUAL_TERMINAL || IS_MINGW;
    }

}
