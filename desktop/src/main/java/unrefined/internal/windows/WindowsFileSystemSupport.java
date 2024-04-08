package unrefined.internal.windows;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Type;
import unrefined.desktop.FileSystemSupport;
import unrefined.desktop.ForeignSupport;
import unrefined.desktop.OSInfo;
import unrefined.desktop.SymbolSupport;
import unrefined.util.NotInstantiableError;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static unrefined.desktop.ForeignSupport.INVOKER;
import static unrefined.desktop.ForeignSupport.LAST_ERROR;
import static unrefined.desktop.UnsafeSupport.UNSAFE;

public final class WindowsFileSystemSupport {

    private WindowsFileSystemSupport() {
        throw new NotInstantiableError(WindowsFileSystemSupport.class);
    }

    private static final Function _get_osfhandle;

    public static long _get_osfhandle(int fd) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(_get_osfhandle);
        heapInvocationBuffer.putInt(fd);
        return INVOKER.invokeLong(_get_osfhandle, heapInvocationBuffer);
    }

    private static final Function GetFileInformationByHandle;
    private static final long lpFileInformation;

    static {
        if (OSInfo.IS_WINDOWS) {
            _get_osfhandle = new Function(SymbolSupport.find("_get_osfhandle"),
                    CallContext.getCallContext(Type.POINTER, new Type[] {Type.SINT}, CallingConvention.DEFAULT, false));
            GetFileInformationByHandle = new Function(WindowsSupport.Kernel32.getSymbolAddress("GetFileInformationByHandle"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.POINTER}, CallingConvention.DEFAULT, true));
            lpFileInformation = UNSAFE.allocateMemory(52);
        }
        else {
            _get_osfhandle = null;
            GetFileInformationByHandle = null;
            lpFileInformation = 0;
        }
    }

    public static boolean GetFileInformationByHandle(long hFile, long lpFileInformation) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(GetFileInformationByHandle);
        heapInvocationBuffer.putAddress(hFile);
        heapInvocationBuffer.putAddress(lpFileInformation);
        return INVOKER.invokeInt(GetFileInformationByHandle, heapInvocationBuffer) != 0;
    }

    public static synchronized int getNumberOfLinks(Path path) throws IOException {
        try (FileChannel channel = (FileChannel) Files.newByteChannel(path, StandardOpenOption.READ)) {
            long hFile = FileSystemSupport.FD_PROCESS.toHANDLE(FileSystemSupport.getFileDescriptor(channel));
            if (GetFileInformationByHandle(hFile, lpFileInformation)) return UNSAFE.getInt(lpFileInformation + 40);
            else throw new IOException(ForeignSupport.ERROR_STRING_PRODUCER.apply(LAST_ERROR.get()));
        }
    }

}
