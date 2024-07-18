package unrefined.desktop.windows;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Type;
import unrefined.desktop.OSInfo;
import unrefined.util.NotInstantiableError;

import static unrefined.desktop.ForeignSupport.INVOKER;
import static unrefined.desktop.windows.WindowsSupport.Advapi32;

public final class WindowsConsole {

    private WindowsConsole() {
        throw new NotInstantiableError(WindowsConsole.class);
    }

    private static final Function GetConsoleMode;
    private static final Function SetConsoleMode;
    private static final Function GetConsoleScreenBufferInfo;
    private static final Function SetConsoleTextAttribute;
    private static final Function SetConsoleCursorPosition;
    private static final Function FillConsoleOutputCharacterW;
    private static final Function FillConsoleOutputAttribute;
    private static final Function ScrollConsoleScreenBufferW;
    private static final Function SetConsoleTitleW;

    static {
        if (OSInfo.IS_WINDOWS) {
            GetConsoleMode = new Function(Advapi32.getSymbolAddress("GetConsoleMode"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.POINTER},
                            CallingConvention.STDCALL, false));
            SetConsoleMode = new Function(Advapi32.getSymbolAddress("SetConsoleMode"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.UINT32},
                            CallingConvention.STDCALL, false));
            GetConsoleScreenBufferInfo = new Function(Advapi32.getSymbolAddress("GetConsoleScreenBufferInfo"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.POINTER},
                            CallingConvention.STDCALL, false));
            SetConsoleTextAttribute = new Function(Advapi32.getSymbolAddress("SetConsoleTextAttribute"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.UINT16},
                            CallingConvention.STDCALL, false));
            SetConsoleCursorPosition = new Function(Advapi32.getSymbolAddress("SetConsoleCursorPosition"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.UINT32},
                            CallingConvention.STDCALL, false));
            FillConsoleOutputCharacterW = new Function(Advapi32.getSymbolAddress("FillConsoleOutputCharacterW"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.UINT16, Type.UINT32, Type.UINT32, Type.POINTER},
                            CallingConvention.STDCALL, false));
            FillConsoleOutputAttribute = new Function(Advapi32.getSymbolAddress("FillConsoleOutputAttribute"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.UINT16, Type.UINT32, Type.UINT32, Type.POINTER},
                            CallingConvention.STDCALL, false));
            ScrollConsoleScreenBufferW = new Function(Advapi32.getSymbolAddress("ScrollConsoleScreenBufferW"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.POINTER, Type.POINTER, Type.UINT32, Type.POINTER},
                            CallingConvention.STDCALL, false));
            SetConsoleTitleW = new Function(Advapi32.getSymbolAddress("SetConsoleTitleW"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER},
                            CallingConvention.STDCALL, false));
        }
        else {
            GetConsoleMode = null;
            SetConsoleMode = null;
            GetConsoleScreenBufferInfo = null;
            SetConsoleTextAttribute = null;
            SetConsoleCursorPosition = null;
            FillConsoleOutputCharacterW = null;
            FillConsoleOutputAttribute = null;
            ScrollConsoleScreenBufferW = null;
            SetConsoleTitleW = null;
        }
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

    public static int GetConsoleScreenBufferInfo(long hConsoleOutput, long lpConsoleScreenBufferInfo) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(GetConsoleScreenBufferInfo);
        heapInvocationBuffer.putAddress(hConsoleOutput);
        heapInvocationBuffer.putAddress(lpConsoleScreenBufferInfo);
        return INVOKER.invokeInt(GetConsoleScreenBufferInfo, heapInvocationBuffer);
    }

    public static int SetConsoleTextAttribute(long hConsoleOutput, short wAttributes) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(SetConsoleTextAttribute);
        heapInvocationBuffer.putAddress(hConsoleOutput);
        heapInvocationBuffer.putShort(wAttributes);
        return INVOKER.invokeInt(SetConsoleTextAttribute, heapInvocationBuffer);
    }

    public static int SetConsoleCursorPosition(long hConsoleOutput, int dwCursorPosition) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(SetConsoleCursorPosition);
        heapInvocationBuffer.putAddress(hConsoleOutput);
        heapInvocationBuffer.putInt(dwCursorPosition);
        return INVOKER.invokeInt(SetConsoleCursorPosition, heapInvocationBuffer);
    }

    public static int FillConsoleOutputCharacterW(long hConsoleOutput, char cCharacter, int nLength, int dwWriteCoord, long lpNumberOfCharsWritten) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(FillConsoleOutputCharacterW);
        heapInvocationBuffer.putAddress(hConsoleOutput);
        heapInvocationBuffer.putShort(cCharacter);
        heapInvocationBuffer.putInt(nLength);
        heapInvocationBuffer.putInt(dwWriteCoord);
        heapInvocationBuffer.putAddress(lpNumberOfCharsWritten);
        return INVOKER.invokeInt(FillConsoleOutputCharacterW, heapInvocationBuffer);
    }

    public static int FillConsoleOutputAttribute(long hConsoleOutput, short wAttribute, int nLength, int dwWriteCoord, long lpNumberOfAttrsWritten) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(FillConsoleOutputAttribute);
        heapInvocationBuffer.putAddress(hConsoleOutput);
        heapInvocationBuffer.putShort(wAttribute);
        heapInvocationBuffer.putInt(nLength);
        heapInvocationBuffer.putInt(dwWriteCoord);
        heapInvocationBuffer.putAddress(lpNumberOfAttrsWritten);
        return INVOKER.invokeInt(FillConsoleOutputAttribute, heapInvocationBuffer);
    }

    public static int ScrollConsoleScreenBufferW(long hConsoleOutput, long lpScrollRectangle, long lpClipRectangle, int dwDestinationOrigin, long lpFill) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(ScrollConsoleScreenBufferW);
        heapInvocationBuffer.putAddress(hConsoleOutput);
        heapInvocationBuffer.putAddress(lpScrollRectangle);
        heapInvocationBuffer.putAddress(lpClipRectangle);
        heapInvocationBuffer.putInt(dwDestinationOrigin);
        heapInvocationBuffer.putAddress(lpFill);
        return INVOKER.invokeInt(ScrollConsoleScreenBufferW, heapInvocationBuffer);
    }

    public static int SetConsoleTitleW(long lpConsoleTitle) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(SetConsoleTitleW);
        heapInvocationBuffer.putAddress(lpConsoleTitle);
        return INVOKER.invokeInt(SetConsoleTitleW, heapInvocationBuffer);
    }

}
