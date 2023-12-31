package unrefined.desktop;

import com.kenai.jffi.Library;
import com.kenai.jffi.NativeMethod;
import com.kenai.jffi.NativeMethods;
import unrefined.internal.OperatingSystem;
import unrefined.internal.windows.WindowsLibrary;
import unrefined.util.NotInstantiableError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;

public final class ProcessIdentifier {

    private ProcessIdentifier() {
        throw new NotInstantiableError(ProcessIdentifier.class);
    }

    public static final long CURRENT;
    static {
        long processIdentifier;
        try {
            Class<?> processHandleClazz = Class.forName("java.lang.ProcessHandle");
            Method currentMethod = processHandleClazz.getDeclaredMethod("current");
            Method pidMethod = processHandleClazz.getDeclaredMethod("pid");
            processIdentifier = (long) pidMethod.invoke(currentMethod.invoke(null));
        }
        catch (NoSuchMethodException | ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            if (OperatingSystem.IS_WINDOWS) {
                long address = WindowsLibrary.Kernel32.getSymbolAddress("GetCurrentProcessId");
                if (address == 0) processIdentifier = -1;
                else {
                    NativeMethods.register(Windows.class, Collections.singletonList(
                            new NativeMethod(address, "GetCurrentProcessId", "()I")
                    ));
                    try {
                        processIdentifier = Windows.GetCurrentProcessId();
                    }
                    finally {
                        NativeMethods.unregister(Windows.class);
                    }
                }
            }
            else {
                long address = Library.getDefault().getSymbolAddress("getpid");
                if (address == 0) processIdentifier = -1;
                else {
                    NativeMethods.register(POSIX.class, Collections.singletonList(
                            new NativeMethod(address, "getpid", "()J")
                    ));
                    try {
                        processIdentifier = POSIX.getpid();
                    }
                    finally {
                        NativeMethods.unregister(POSIX.class);
                    }
                }
            }
        }
        CURRENT = processIdentifier;
    }

    private static final class Windows {
        private Windows() {
            throw new NotInstantiableError(Windows.class);
        }
        public static native int GetCurrentProcessId();
    }

    private static final class POSIX {
        private POSIX() {
            throw new NotInstantiableError(POSIX.class);
        }
        public static native long getpid();
    }

}
