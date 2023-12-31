package unrefined.desktop;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Invoker;
import com.kenai.jffi.LastError;
import com.kenai.jffi.Library;
import com.kenai.jffi.MemoryIO;
import com.kenai.jffi.NativeMethod;
import com.kenai.jffi.NativeMethods;
import com.kenai.jffi.Platform;
import com.kenai.jffi.Type;
import unrefined.internal.OperatingSystem;
import unrefined.nio.Pointer;
import unrefined.runtime.DesktopSymbol;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;
import unrefined.util.concurrent.ConcurrentHashSet;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Redirect;
import unrefined.util.foreign.Symbol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ForeignSupport {

    public static final Invoker INVOKER = Invoker.getInstance();
    public static final MemoryIO MEMORY_IO = MemoryIO.getInstance();

    public interface NativeTypeInvoker {
        long invoke(Function function, HeapInvocationBuffer heapInvocationBuffer);
        long invoke(CallContext context, long function, HeapInvocationBuffer heapInvocationBuffer);
    }

    public static final NativeTypeInvoker NATIVE_TYPE_INVOKER_64 = new NativeTypeInvoker() {
        @Override
        public long invoke(Function function, HeapInvocationBuffer heapInvocationBuffer) {
            return INVOKER.invokeLong(function, heapInvocationBuffer);
        }
        @Override
        public long invoke(CallContext context, long function, HeapInvocationBuffer heapInvocationBuffer) {
            return INVOKER.invokeLong(context, function, heapInvocationBuffer);
        }
    };
    public static final NativeTypeInvoker NATIVE_TYPE_INVOKER_32 = new NativeTypeInvoker() {
        @Override
        public long invoke(Function function, HeapInvocationBuffer heapInvocationBuffer) {
            return INVOKER.invokeInt(function, heapInvocationBuffer);
        }
        @Override
        public long invoke(CallContext context, long function, HeapInvocationBuffer heapInvocationBuffer) {
            return INVOKER.invokeInt(context, function, heapInvocationBuffer);
        }
    };
    public static final NativeTypeInvoker NATIVE_LONG_INVOKER = ABI.L == 8 ? NATIVE_TYPE_INVOKER_64 : NATIVE_TYPE_INVOKER_32;
    public static final NativeTypeInvoker NATIVE_INT_INVOKER = ABI.I == 8 ? NATIVE_TYPE_INVOKER_64 : NATIVE_TYPE_INVOKER_32;

    private static final Set<Library> CACHED = new ConcurrentHashSet<>();
    static {
        ForeignSupport.CACHED.add(com.kenai.jffi.Library.getDefault());
    }

    private ForeignSupport() {
        throw new NotInstantiableError(ForeignSupport.class);
    }
    
    private static final Function wcslen =
            new Function(Library.getDefault().getSymbolAddress("wcslen"),
                    CallContext.getCallContext(Type.POINTER, new Type[] { Type.POINTER }, CallingConvention.DEFAULT, true));

    public static long wcslen(long str) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(wcslen);
        heapInvocationBuffer.putAddress(str);
        return INVOKER.invokeAddress(wcslen, heapInvocationBuffer);
    }

    private static final Function wmemchr =
            new Function(Library.getDefault().getSymbolAddress("wmemchr"),
                    CallContext.getCallContext(Type.POINTER,
                            new Type[] { Type.POINTER, OperatingSystem.IS_WINDOWS ? Type.UINT16 : Type.UINT32, Type.POINTER },
                            CallingConvention.DEFAULT, true));

    @FunctionalInterface
    private interface WideCharProcess {
        long wmemchr(long str, int ch, long count);
    }

    private static final WideCharProcess WIDE_CHAR_PROCESS;
    static {
        if (OperatingSystem.IS_WINDOWS) WIDE_CHAR_PROCESS = (str, ch, count) -> {
            HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(wmemchr);
            heapInvocationBuffer.putAddress(str);
            heapInvocationBuffer.putShort((short) ch);
            heapInvocationBuffer.putLong(count);
            return INVOKER.invokeAddress(wmemchr, heapInvocationBuffer);
        };
        else WIDE_CHAR_PROCESS = (str, ch, count) -> {
            HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(wmemchr);
            heapInvocationBuffer.putAddress(str);
            heapInvocationBuffer.putInt(ch);
            heapInvocationBuffer.putLong(count);
            return INVOKER.invokeAddress(wmemchr, heapInvocationBuffer);
        };
    }

    public static long wmemchr(long str, int ch, long count) {
        return WIDE_CHAR_PROCESS.wmemchr(str, ch, count);
    }

    public static long allocateString(String string) {
        return allocateString(string, null);
    }

    public static long allocateWideCharString(String string) {
        return allocateString(string, OperatingSystem.WIDE_CHARSET);
    }

    public static long allocateString(String string, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        byte[] terminator = "\0".getBytes(charset);
        byte[] bytes = string.getBytes(charset);
        long address = MEMORY_IO.allocateMemory(terminator.length + bytes.length, false);
        MEMORY_IO.putByteArray(address + bytes.length, terminator, 0, terminator.length);
        MEMORY_IO.putByteArray(address, bytes, 0, bytes.length);
        return address;
    }

    private static String signature(Class<?> clazz) {
        if (clazz == void.class) return "V";
        else if (clazz == boolean.class) return "Z";
        else if (clazz == byte.class) return "B";
        else if (clazz == char.class) return "C";
        else if (clazz == short.class) return "S";
        else if (clazz == int.class) return "I";
        else if (clazz == long.class) return "J";
        else if (clazz == float.class) return "F";
        else if (clazz == double.class) return "D";
        else throw new UnexpectedError();
    }

    private static final Set<Class<?>> REGISTERED = new HashSet<>();

    public static void register(Class<?> clazz) {
        if (!REGISTERED.contains(clazz)) synchronized (REGISTERED) {
            if (REGISTERED.contains(clazz)) return;
            List<NativeMethod> nativeMethods = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            boolean next;
            for (Method method : clazz.getDeclaredMethods()) {
                builder.setLength(0);
                next = false;
                if (!Modifier.isNative(method.getModifiers())) continue;
                Class<?> returnType = method.getReturnType();
                if (!returnType.isPrimitive()) continue;
                builder.append("(");
                for (Class<?> parameterType : method.getParameterTypes()) {
                    if (!parameterType.isPrimitive()) {
                        builder.setLength(0);
                        next = true;
                        break;
                    }
                    builder.append(signature(parameterType));
                }
                if (next) continue;
                builder.append(")");
                builder.append(signature(returnType));
                String name;
                Redirect redirect = method.getDeclaredAnnotation(Redirect.class);
                if (redirect == null) name = method.getName();
                else name = redirect.value();
                nativeMethods.add(new NativeMethod(getSymbolAddress(name), method.getName(), builder.toString()));
            }
            NativeMethods.register(clazz, nativeMethods);
            REGISTERED.add(clazz);
        }
    }

    public static void unregister(Class<?> clazz) {
        synchronized (REGISTERED) {
            if (REGISTERED.remove(clazz)) NativeMethods.unregister(clazz);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends unrefined.util.foreign.Library> T downcallProxy(ClassLoader loader, Class<T> clazz) {
        if (!clazz.isInterface()) throw new IllegalArgumentException("not an interface");
        Map<Method, DesktopSymbol> cache = new HashMap<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getDeclaringClass() == Object.class) continue;
            else {
                if (method.isDefault()) continue;
                String name;
                Redirect redirect = method.getDeclaredAnnotation(Redirect.class);
                if (redirect == null) name = method.getName();
                else name = redirect.value();
                cache.put(method, new DesktopSymbol(getSymbolAddress(name), method.getReturnType(), method.getParameterTypes()));
            }
        }
        return (T) Proxy.newProxyInstance(loader, new Class[] { clazz }, (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) return ReflectionSupport.invokeMethod(proxy, method, args);
            else return cache.get(method).invoke(args);
        });
    }

    public static <T extends unrefined.util.foreign.Library> T downcallProxy(Class<T> clazz) {
        return downcallProxy(ReflectionSupport.getCallerClass().getClassLoader(), clazz);
    }

    public static Symbol downcallHandle(long function, Class<?> returnType, Class<?>... parameterTypes) {
        return new DesktopSymbol(function, returnType, parameterTypes);
    }

    public static Symbol upcallStub(Object object, Method method, Class<?> returnType, Class<?>... parameterTypes) {
        return new DesktopSymbol(object, method, returnType, parameterTypes);
    }

    public static void invokeVoidFunction(long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.VOID, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.VOID, SymbolSupport.toFFITypes(args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        INVOKER.invokeInt(context, address, heapInvocationBuffer);
    }

    public static boolean invokeBooleanFunction(long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.UINT8, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.UINT8, SymbolSupport.toFFITypes(args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return INVOKER.invokeInt(context, address, heapInvocationBuffer) != 0;
    }

    public static byte invokeByteFunction(long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.SINT8, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.SINT8, SymbolSupport.toFFITypes(args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return (byte) (INVOKER.invokeInt(context, address, heapInvocationBuffer) & 0xFF);
    }

    public static char invokeCharFunction(long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.UINT16, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.UINT16, SymbolSupport.toFFITypes(args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return (char) (INVOKER.invokeInt(context, address, heapInvocationBuffer) & 0xFFFF);
    }

    public static short invokeShortFunction(long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.SINT16, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.SINT16, SymbolSupport.toFFITypes(args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return (short) (INVOKER.invokeInt(context, address, heapInvocationBuffer) & 0xFFFF);
    }

    public static int invokeIntFunction(long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.SINT32, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.SINT32, SymbolSupport.toFFITypes(args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return INVOKER.invokeInt(context, address, heapInvocationBuffer);
    }

    public static long invokeNativeIntFunction(long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.SINT, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.SINT, SymbolSupport.toFFITypes(args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return NATIVE_INT_INVOKER.invoke(context, address, heapInvocationBuffer);
    }

    public static long invokeLongFunction(long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.SINT64, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.SINT64, SymbolSupport.toFFITypes(args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return INVOKER.invokeLong(context, address, heapInvocationBuffer);
    }

    public static long invokeNativeLongFunction(long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.SLONG, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.SLONG, SymbolSupport.toFFITypes(args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return NATIVE_LONG_INVOKER.invoke(context, address, heapInvocationBuffer);
    }

    public static float invokeFloatFunction(long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.FLOAT, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.FLOAT, SymbolSupport.toFFITypes(args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return INVOKER.invokeFloat(context, address, heapInvocationBuffer);
    }

    public static double invokeDoubleFunction(long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.DOUBLE, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.DOUBLE, SymbolSupport.toFFITypes(args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return INVOKER.invokeDouble(context, address, heapInvocationBuffer);
    }

    public static long invokeAddressFunction(long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.POINTER, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.POINTER, SymbolSupport.toFFITypes(args),
                    CallingConvention.DEFAULT, true);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return INVOKER.invokeAddress(context, address, heapInvocationBuffer);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeFunction(long address, Class<T> returnType, Object... args) {
        if (returnType == void.class) {
            invokeVoidFunction(address, args);
            return null;
        }
        else if (returnType == boolean.class) return (T) Boolean.valueOf(invokeBooleanFunction(address, args));
        else if (returnType == byte.class) return (T) Byte.valueOf(invokeByteFunction(address, args));
        else if (returnType == char.class) return (T) Character.valueOf(invokeCharFunction(address, args));
        else if (returnType == short.class) return (T) Short.valueOf(invokeShortFunction(address, args));
        else if (returnType == int.class) return (T) Integer.valueOf(invokeIntFunction(address, args));
        else if (returnType == long.class) return (T) Long.valueOf(invokeLongFunction(address, args));
        else if (returnType == float.class) return (T) Float.valueOf(invokeFloatFunction(address, args));
        else if (returnType == double.class) return (T) Double.valueOf(invokeDoubleFunction(address, args));
        else if (returnType == Pointer.class) return (T) Long.valueOf(invokeAddressFunction(address, args));
        else {
            Objects.requireNonNull(returnType);
            throw new IllegalArgumentException("Illegal return type: " + returnType);
        }
    }

    public static void loadLibrary(String name, int loader) throws IOException {
        switch (loader) {
            case Foreign.Loader.CLASS:
                try {
                    System.loadLibrary(name);
                }
                catch (UnsatisfiedLinkError e) {
                    throw new IOException(e.getMessage());
                }
                break;
            case Foreign.Loader.LINKER:
                Library loaded = Library.getCachedInstance(name,
                        Library.GLOBAL | Library.LAZY);
                if (loaded == null) throw new IOException(Library.getLastError());
                else CACHED.add(loaded);
                break;
            default: throw new IllegalArgumentException("Illegal symbol loader: " + loader);
        }
    }

    @SuppressWarnings("UnsafeDynamicallyLoadedCode")
    public static void loadLibrary(File file, int loader) throws IOException {
        String path = file.getAbsolutePath();
        if (!file.exists()) throw new FileNotFoundException(path);
        switch (loader) {
            case Foreign.Loader.CLASS:
                try {
                    System.load(path);
                }
                catch (UnsatisfiedLinkError e) {
                    throw new IOException(e.getMessage());
                }
                break;
            case Foreign.Loader.LINKER:
                Library loaded = Library.getCachedInstance(path,
                        Library.GLOBAL | Library.LAZY);
                if (loaded == null) throw new IOException(Library.getLastError());
                else CACHED.add(loaded);
                break;
            default: throw new IllegalArgumentException("Illegal symbol loader: " + loader);
        }
    }

    public static String mapLibraryName(String name) {
        return Platform.getPlatform().mapLibraryName(name);
    }

    public static long getSymbolAddress(String name) throws UnsatisfiedLinkError {
        long symbol = SymbolSupport.find(name);
        if (symbol != 0) return symbol;
        synchronized (CACHED) {
            for (Library library : CACHED) {
                symbol = library.getSymbolAddress(name);
                if (symbol != 0) return symbol;
            }
        }
        throw new UnsatisfiedLinkError("Undefined symbol `" + name + "`");
    }

    public static int nativeIntSize() {
        return ABI.I;
    }

    public static int nativeLongSize() {
        return ABI.L;
    }

    public static int addressSize() {
        return ABI.P;
    }

    public static Class<?> nativeIntClass() {
        return ABI.I_TYPE;
    }

    public static Class<?> nativeLongClass() {
        return ABI.L_TYPE;
    }

    public static Class<?> addressClass() {
        return ABI.P_TYPE;
    }

    public static Charset systemCharset() {
        return OperatingSystem.NATIVE_CHARSET;
    }

    public static int systemCharSize() {
        return OperatingSystem.NATIVE_CHAR_SIZE;
    }

    public static Charset wideCharset() {
        return OperatingSystem.WIDE_CHARSET;
    }

    public static int wideCharSize() {
        return OperatingSystem.WIDE_CHAR_SIZE;
    }

    public static int getLastError() {
        return LastError.getInstance().get();
    }

    public static void setLastError(int errno) {
        LastError.getInstance().set(errno);
    }

}
