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

public final class WindowsRegistry {

    public static final long HKEY_CURRENT_USER = 0x80000001L;
    public static final long HKEY_LOCAL_MACHINE = 0x80000002L;

    public static final int KEY_QUERY_VALUE = 0x0001;
    public static final int KEY_SET_VALUE = 0x0002;
    public static final int KEY_CREATE_SUB_KEY = 0x0004;
    public static final int KEY_ENUMERATE_SUB_KEYS = 0x0008;
    public static final int KEY_READ = 0x20019;
    public static final int KEY_WRITE = 0x20006;
    public static final int KEY_ALL_ACCESS = 0xF003F;
    public static final int KEY_NOTIFY = 0x0010;

    public static final int KEY_WOW64_32KEY = 0x0200;
    public static final int KEY_WOW64_64KEY = 0x0100;

    private WindowsRegistry() {
        throw new NotInstantiableError(WindowsRegistry.class);
    }

    private static final Function RegOpenKeyExW;
    private static final Function RegCloseKey;
    private static final Function RegCreateKeyExW;
    private static final Function RegDeleteKeyW;
    private static final Function RegFlushKey;
    private static final Function RegQueryValueExW;
    private static final Function RegSetValueExW;
    private static final Function RegDeleteValueW;
    private static final Function RegQueryInfoKeyW;
    private static final Function RegEnumKeyExW;
    private static final Function RegEnumValueW;

    static {
        if (OSInfo.IS_WINDOWS) {
            RegOpenKeyExW = new Function(Advapi32.getSymbolAddress("RegOpenKeyExW"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.POINTER, Type.UINT32, Type.UINT32, Type.POINTER},
                            CallingConvention.STDCALL, false));
            RegCloseKey = new Function(Advapi32.getSymbolAddress("RegCloseKey"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER}, 
                            CallingConvention.STDCALL, false));
            RegCreateKeyExW = new Function(Advapi32.getSymbolAddress("RegCreateKeyExW"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.POINTER, Type.UINT32, Type.POINTER, Type.UINT32, Type.UINT32, Type.POINTER, Type.POINTER, Type.POINTER},
                            CallingConvention.STDCALL, false));
            RegDeleteKeyW = new Function(Advapi32.getSymbolAddress("RegDeleteKeyW"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.POINTER},
                            CallingConvention.STDCALL, false));
            RegFlushKey = new Function(Advapi32.getSymbolAddress("RegFlushKey"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER}, 
                            CallingConvention.STDCALL, false));
            RegQueryValueExW = new Function(Advapi32.getSymbolAddress("RegQueryValueExW"),
                    CallContext.getCallContext(Type.UINT32,
                            new Type[] {Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER},
                            CallingConvention.STDCALL, false));
            RegSetValueExW = new Function(Advapi32.getSymbolAddress("RegSetValueExW"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.POINTER, Type.UINT32, Type.UINT32, Type.POINTER, Type.UINT32},
                            CallingConvention.STDCALL, false));
            RegDeleteValueW = new Function(Advapi32.getSymbolAddress("RegDeleteValueW"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.POINTER},
                            CallingConvention.STDCALL, false));
            RegQueryInfoKeyW = new Function(Advapi32.getSymbolAddress("RegQueryInfoKeyW"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER},
                            CallingConvention.STDCALL, false));
            RegEnumKeyExW = new Function(Advapi32.getSymbolAddress("RegEnumKeyExW"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.UINT32, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER},
                            CallingConvention.STDCALL, false));
            RegEnumValueW = new Function(Advapi32.getSymbolAddress("RegEnumValueW"),
                    CallContext.getCallContext(Type.UINT32, new Type[] {Type.POINTER, Type.UINT32, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER, Type.POINTER},
                            CallingConvention.STDCALL, false));
        }
        else {
            RegOpenKeyExW = null;
            RegCloseKey = null;
            RegCreateKeyExW = null;
            RegDeleteKeyW = null;
            RegFlushKey = null;
            RegQueryValueExW = null;
            RegSetValueExW = null;
            RegDeleteValueW = null;
            RegQueryInfoKeyW = null;
            RegEnumKeyExW = null;
            RegEnumValueW = null;
        }
    }

    public static int RegOpenKeyExW(long hKey, long lpSubKey, int ulOptions, int samDesired, long phkResult) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegOpenKeyExW);
        heapInvocationBuffer.putAddress(hKey);
        heapInvocationBuffer.putAddress(lpSubKey);
        heapInvocationBuffer.putInt(ulOptions);
        heapInvocationBuffer.putInt(samDesired);
        heapInvocationBuffer.putAddress(phkResult);
        return INVOKER.invokeInt(RegOpenKeyExW, heapInvocationBuffer);
    }

    public static int RegCloseKey(long hKey) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegCloseKey);
        heapInvocationBuffer.putAddress(hKey);
        return INVOKER.invokeInt(RegCloseKey, heapInvocationBuffer);
    }

    public static int RegCreateKeyExW(long hKey, long lpSubKey, int Reserved, long lpClass, int dwOptions,
                                      int samDesired, long lpSecurityAttributes, long phkResult, long lpdwDisposition) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegCreateKeyExW);
        heapInvocationBuffer.putAddress(hKey);
        heapInvocationBuffer.putAddress(lpSubKey);
        heapInvocationBuffer.putInt(Reserved);
        heapInvocationBuffer.putAddress(lpClass);
        heapInvocationBuffer.putInt(dwOptions);
        heapInvocationBuffer.putInt(samDesired);
        heapInvocationBuffer.putAddress(lpSecurityAttributes);
        heapInvocationBuffer.putAddress(phkResult);
        heapInvocationBuffer.putAddress(lpdwDisposition);
        return INVOKER.invokeInt(RegCreateKeyExW, heapInvocationBuffer);
    }

    public static int RegDeleteKeyW(long hKey, long lpSubKey) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegDeleteKeyW);
        heapInvocationBuffer.putAddress(hKey);
        heapInvocationBuffer.putAddress(lpSubKey);
        return INVOKER.invokeInt(RegDeleteKeyW, heapInvocationBuffer);
    }

    public static int RegFlushKey(long hKey) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegFlushKey);
        heapInvocationBuffer.putAddress(hKey);
        return INVOKER.invokeInt(RegFlushKey, heapInvocationBuffer);
    }

    public static int RegQueryValueExW(long hKey, long lpValueName, long lpReserved, long lpType, long lpData, long lpcbData) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegQueryValueExW);
        heapInvocationBuffer.putAddress(hKey);
        heapInvocationBuffer.putAddress(lpValueName);
        heapInvocationBuffer.putAddress(lpReserved);
        heapInvocationBuffer.putAddress(lpType);
        heapInvocationBuffer.putAddress(lpData);
        heapInvocationBuffer.putAddress(lpcbData);
        return INVOKER.invokeInt(RegQueryValueExW, heapInvocationBuffer);
    }

    public static int RegSetValueExW(long hKey, long lpValueName, int Reserved, int dwType, long lpData, int cbData) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegSetValueExW);
        heapInvocationBuffer.putAddress(hKey);
        heapInvocationBuffer.putAddress(lpValueName);
        heapInvocationBuffer.putInt(Reserved);
        heapInvocationBuffer.putInt(dwType);
        heapInvocationBuffer.putAddress(lpData);
        heapInvocationBuffer.putInt(cbData);
        return INVOKER.invokeInt(RegSetValueExW, heapInvocationBuffer);
    }

    public static int RegDeleteValueW(long hKey, long lpValueName) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegDeleteValueW);
        heapInvocationBuffer.putAddress(hKey);
        heapInvocationBuffer.putAddress(lpValueName);
        return INVOKER.invokeInt(RegDeleteValueW, heapInvocationBuffer);
    }

    public static int RegQueryInfoKeyW(long hKey, long lpClass, long lpcchClass, long lpReserved, long lpcSubKeys, long lpcbMaxSubKeyLen,
                                       long lpcbMaxClassLen, long lpcValues, long lpcbMaxValueNameLen, long lpcbMaxValueLen, long lpcbSecurityDescriptor,
                                       long lpftLastWriteTime) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegQueryInfoKeyW);
        heapInvocationBuffer.putAddress(hKey);
        heapInvocationBuffer.putAddress(lpClass);
        heapInvocationBuffer.putAddress(lpcchClass);
        heapInvocationBuffer.putAddress(lpReserved);
        heapInvocationBuffer.putAddress(lpcSubKeys);
        heapInvocationBuffer.putAddress(lpcbMaxSubKeyLen);
        heapInvocationBuffer.putAddress(lpcbMaxClassLen);
        heapInvocationBuffer.putAddress(lpcValues);
        heapInvocationBuffer.putAddress(lpcbMaxValueNameLen);
        heapInvocationBuffer.putAddress(lpcbMaxValueLen);
        heapInvocationBuffer.putAddress(lpcbSecurityDescriptor);
        heapInvocationBuffer.putAddress(lpftLastWriteTime);
        return INVOKER.invokeInt(RegQueryInfoKeyW, heapInvocationBuffer);
    }

    public static int RegEnumKeyExW(long hKey, int dwIndex, long lpName, long lpcchName, long lpReserved,
                                    long lpClass, long lpcchClass, long lpftLastWriteTime) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegEnumKeyExW);
        heapInvocationBuffer.putAddress(hKey);
        heapInvocationBuffer.putInt(dwIndex);
        heapInvocationBuffer.putAddress(lpName);
        heapInvocationBuffer.putAddress(lpcchName);
        heapInvocationBuffer.putAddress(lpReserved);
        heapInvocationBuffer.putAddress(lpClass);
        heapInvocationBuffer.putAddress(lpcchClass);
        heapInvocationBuffer.putAddress(lpftLastWriteTime);
        return INVOKER.invokeInt(RegEnumKeyExW, heapInvocationBuffer);
    }

    public static int RegEnumValueW(long hKey, int dwIndex, long lpValueName, long lpcchValueName, long lpReserved,
                                    long lpType, long lpData, long lpcbData) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(RegEnumValueW);
        heapInvocationBuffer.putAddress(hKey);
        heapInvocationBuffer.putInt(dwIndex);
        heapInvocationBuffer.putAddress(lpValueName);
        heapInvocationBuffer.putAddress(lpcchValueName);
        heapInvocationBuffer.putAddress(lpReserved);
        heapInvocationBuffer.putAddress(lpType);
        heapInvocationBuffer.putAddress(lpData);
        heapInvocationBuffer.putAddress(lpcbData);
        return INVOKER.invokeInt(RegEnumValueW, heapInvocationBuffer);
    }

}
