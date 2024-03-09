package unrefined.desktop;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Library;
import com.kenai.jffi.Type;
import unrefined.internal.windows.WindowsSupport;
import unrefined.util.NotInstantiableError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static unrefined.desktop.ForeignSupport.INVOKER;

public final class RuntimeSupport {

    private RuntimeSupport() {
        throw new NotInstantiableError(RuntimeSupport.class);
    }

    public static final long PID;
    static {
        long processId;
        try {
            Class<?> processHandleClazz = Class.forName("java.lang.ProcessHandle");
            Method currentMethod = processHandleClazz.getDeclaredMethod("current");
            Method pidMethod = processHandleClazz.getDeclaredMethod("pid");
            processId = (long) pidMethod.invoke(currentMethod.invoke(null));
        }
        catch (NoSuchMethodException | ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            if (OSInfo.IS_WINDOWS) {
                long address = WindowsSupport.Kernel32.getSymbolAddress("GetCurrentProcessId");
                if (address == 0) processId = -1;
                else {
                    CallContext context = new CallContext(Type.UINT32, new Type[0], CallingConvention.DEFAULT, false);
                    processId = INVOKER.invokeI0(context, address);
                }
            }
            else {
                long address = Library.getDefault().getSymbolAddress("getpid");
                if (address == 0) processId = -1;
                else {
                    CallContext context = new CallContext(Type.SINT64, new Type[0], CallingConvention.DEFAULT, false);
                    processId = INVOKER.invokeN0(context, address);
                }
            }
        }
        PID = processId;
    }

}
