package unrefined.desktop.windows;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Type;
import unrefined.desktop.FileSystemSupport;
import unrefined.desktop.ForeignSupport;
import unrefined.desktop.OSInfo;
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
    private static final Function GetFileInformationByHandle;
    static {
        if (OSInfo.IS_WINDOWS) {
            _get_osfhandle = new Function(ForeignSupport.C.getSymbolAddress("_get_osfhandle"),
                    CallContext.getCallContext(Type.POINTER, new Type[] {Type.SINT32},
                            CallingConvention.DEFAULT, false));
            GetFileInformationByHandle = new Function(WindowsSupport.Kernel32.getSymbolAddress("GetFileInformationByHandle"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.POINTER}, CallingConvention.DEFAULT, true));
        }
        else {
            _get_osfhandle = null;
            GetFileInformationByHandle = null;
        }
    }

    public static long _get_osfhandle(int fd) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(_get_osfhandle);
        heapInvocationBuffer.putInt(fd);
        return INVOKER.invokeAddress(_get_osfhandle, heapInvocationBuffer);
    }

    public static int GetFileInformationByFileHandle(long hFile, long lpFileInformation) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(GetFileInformationByHandle);
        heapInvocationBuffer.putAddress(hFile);
        heapInvocationBuffer.putAddress(lpFileInformation);
        return INVOKER.invokeInt(GetFileInformationByHandle, heapInvocationBuffer);
    }

    public static int getNumberOfLinks(Path path) throws IOException {
        try (FileChannel channel = (FileChannel) Files.newByteChannel(path, StandardOpenOption.READ)) {
            long hFile = FileSystemSupport.FD_PROCESS.toHANDLE(FileSystemSupport.getFD(channel));
            long lpFileInformation = UNSAFE.allocateMemory(52);
            try {
                if (GetFileInformationByFileHandle(hFile, lpFileInformation) != 0) return UNSAFE.getInt(lpFileInformation + 40);
                else throw new IOException(ForeignSupport.ERROR_STRING_PRODUCER.apply(LAST_ERROR.get()));
            }
            finally {
                UNSAFE.freeMemory(lpFileInformation);
            }
        }
    }

}
