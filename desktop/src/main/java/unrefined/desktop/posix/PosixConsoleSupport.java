package unrefined.desktop.posix;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Library;
import com.kenai.jffi.Type;
import unrefined.desktop.ABI;
import unrefined.desktop.ForeignSupport;
import unrefined.util.NotInstantiableError;

import static unrefined.desktop.ForeignSupport.NATIVE_INT_INVOKER;
import static unrefined.desktop.OSInfo.*;

public final class PosixConsoleSupport {

    private PosixConsoleSupport() {
        throw new NotInstantiableError(PosixConsoleSupport.class);
    }

    public static final boolean IS_CYGWIN =
            IS_WINDOWS && System.getenv("PWD") != null && System.getenv("PWD").startsWith("/");
    public static final boolean IS_MSYS_RUNTIME = IS_WINDOWS
            && System.getenv("MSYSTEM") != null
            && System.getenv("MSYSTEM").startsWith("MSYS");

    public static final boolean IOCTL_SUPPORTED;
    public static final int TIOCGWINSZ;
    static {
        if (IS_CYGWIN || IS_MSYS_RUNTIME) {
            TIOCGWINSZ = ('T' << 8) | 1;
            IOCTL_SUPPORTED = true;
        }
        else if (IS_LINUX) {
            String arch = System.getProperty("os.arch");
            boolean isMipsPpcOrSparc = arch.equals("mips")
                    || arch.equals("mips64")
                    || arch.equals("mipsel")
                    || arch.equals("mips64el")
                    || arch.startsWith("ppc")
                    || arch.startsWith("sparc");
            TIOCGWINSZ = isMipsPpcOrSparc ? 0x40087468 : 0x00005413;
            IOCTL_SUPPORTED = false;
        } else if (IS_SOLARIS) {
            TIOCGWINSZ = ('T' << 8) | 104;
            IOCTL_SUPPORTED = true;
        } else if (IS_MAC || IS_FREEBSD || IS_OPENBSD || IS_NETBSD) {
            TIOCGWINSZ = 0x40087468;
            IOCTL_SUPPORTED = true;
        } else {
            TIOCGWINSZ = 0;
            IOCTL_SUPPORTED = false;
        }
    }

    private static final Function ioctl;
    static {
        if (IOCTL_SUPPORTED) ioctl = new Function(ForeignSupport.C.getSymbolAddress("ioctl"),
                CallContext.getCallContext(Type.SINT, new Type[] {Type.SINT, Type.SINT32, Type.POINTER},
                        CallingConvention.DEFAULT, false));
        else ioctl = null;
    }

    public static long getConsoleWindowSize(int fd, long winsize) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(ioctl);
        if (ABI.I == 8) heapInvocationBuffer.putLong(fd);
        else heapInvocationBuffer.putInt(fd);
        if (ABI.I == 8) heapInvocationBuffer.putLong(PosixConsoleSupport.TIOCGWINSZ);
        else heapInvocationBuffer.putInt(PosixConsoleSupport.TIOCGWINSZ);
        heapInvocationBuffer.putAddress(winsize);
        return NATIVE_INT_INVOKER.invoke(ioctl, heapInvocationBuffer);
    }

}
