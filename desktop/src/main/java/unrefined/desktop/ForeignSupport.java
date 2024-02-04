package unrefined.desktop;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Invoker;
import com.kenai.jffi.LastError;
import com.kenai.jffi.Library;
import com.kenai.jffi.MemoryIO;
import com.kenai.jffi.Platform;
import com.kenai.jffi.Type;
import unrefined.internal.windows.WindowsLibrary;
import unrefined.nio.Pointer;
import unrefined.runtime.DesktopSymbol;
import unrefined.util.EmptyArray;
import unrefined.util.NotInstantiableError;
import unrefined.util.concurrent.ConcurrentHashSet;
import unrefined.util.foreign.Aggregate;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.LastErrorException;
import unrefined.util.foreign.Redirect;
import unrefined.util.foreign.Symbol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;

import static unrefined.desktop.UnsafeSupport.UNSAFE;

public final class ForeignSupport {

    public static final Invoker INVOKER = Invoker.getInstance();
    public static final MemoryIO MEMORY_IO = MemoryIO.getInstance();
    public static final LastError LAST_ERROR = LastError.getInstance();

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
                    CallContext.getCallContext(Type.POINTER, new Type[] { Type.POINTER }, CallingConvention.DEFAULT, false));

    public static long wcslen(long str) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(wcslen);
        heapInvocationBuffer.putAddress(str);
        return INVOKER.invokeAddress(wcslen, heapInvocationBuffer);
    }

    private static final Function memcmp =
            new Function(Library.getDefault().getSymbolAddress("memcmp"),
                    CallContext.getCallContext(Type.SINT, new Type[] { Type.POINTER, Type.POINTER, Type.POINTER },
                            CallingConvention.DEFAULT, false));

    public static long memcmp(long lhs, long rhs, long count) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(memcmp);
        heapInvocationBuffer.putAddress(lhs);
        heapInvocationBuffer.putAddress(rhs);
        heapInvocationBuffer.putAddress(count);
        return NATIVE_INT_INVOKER.invoke(memcmp, heapInvocationBuffer);
    }

    private static final Function wmemchr =
            new Function(Library.getDefault().getSymbolAddress("wmemchr"),
                    CallContext.getCallContext(Type.POINTER,
                            new Type[] { Type.POINTER, OSInfo.IS_WINDOWS ? Type.UINT16 : Type.UINT32, Type.POINTER },
                            CallingConvention.DEFAULT, false));

    @FunctionalInterface
    private interface WideCharProcess {
        long wmemchr(long str, int ch, long count);
    }

    private static final WideCharProcess WIDE_CHAR_PROCESS;
    static {
        if (OSInfo.IS_WINDOWS) WIDE_CHAR_PROCESS = (str, ch, count) -> {
            HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(wmemchr);
            heapInvocationBuffer.putAddress(str);
            heapInvocationBuffer.putShort((short) ch);
            heapInvocationBuffer.putAddress(count);
            return INVOKER.invokeAddress(wmemchr, heapInvocationBuffer);
        };
        else WIDE_CHAR_PROCESS = (str, ch, count) -> {
            HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(wmemchr);
            heapInvocationBuffer.putAddress(str);
            heapInvocationBuffer.putInt(ch);
            heapInvocationBuffer.putAddress(count);
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
        return allocateString(string, OSInfo.WIDE_CHARSET);
    }

    public static long allocateString(String string, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        byte[] terminator = "\0".getBytes(charset);
        byte[] bytes = string.getBytes(charset);
        long address = UNSAFE.allocateMemory(terminator.length + bytes.length);
        MEMORY_IO.putByteArray(address + bytes.length, terminator, 0, terminator.length);
        MEMORY_IO.putByteArray(address, bytes, 0, bytes.length);
        return address;
    }

    @SuppressWarnings("unchecked")
    public static <T extends unrefined.util.foreign.Library> T downcallProxy(int options, Class<T> clazz, ClassLoader loader) {
        if (!clazz.isInterface()) throw new IllegalArgumentException("not an interface");
        Map<Method, DesktopSymbol> cache = new HashMap<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getDeclaringClass() != Object.class) {
                if (method.isDefault()) continue;
                String name;
                Redirect redirect = method.getAnnotation(Redirect.class);
                if (redirect == null) name = method.getName();
                else name = redirect.value();
                unrefined.util.foreign.Library.Options override =
                        method.getAnnotation(unrefined.util.foreign.Library.Options.class);
                if (override != null) options = override.value();
                Class<?> returnType = method.getReturnType();
                if (returnType == long.class) {
                    unrefined.util.foreign.Library.Marshal marshal = method.getAnnotation(unrefined.util.foreign.Library.Marshal.class);
                    if (marshal != null) {
                        String value = marshal.value();
                        if ("int".equals(value)) returnType = ABI.I_TYPE;
                        else if ("long".equals(value)) returnType = ABI.L_TYPE;
                        else if ("size_t".equals(value)) returnType = ABI.P_TYPE;
                        else throw new IllegalArgumentException("Illegal marshal type: " + value);
                    }
                }
                Parameter[] parameters = method.getParameters();
                Class<?>[] parameterTypes = new Class[parameters.length];
                for (int i = 0; i < parameterTypes.length; i ++) {
                    Class<?> parameterType = parameters[i].getType();
                    if (parameterType == long.class) {
                        unrefined.util.foreign.Library.Marshal marshal = parameters[i].getAnnotation(unrefined.util.foreign.Library.Marshal.class);
                        if (marshal != null) {
                            String value = marshal.value();
                            if ("int".equals(value)) parameterTypes[i] = ABI.I_TYPE;
                            else if ("long".equals(value)) parameterTypes[i] = ABI.L_TYPE;
                            else if ("size_t".equals(value)) parameterTypes[i] = ABI.P_TYPE;
                            else throw new IllegalArgumentException("Illegal marshal type: " + value);
                        }
                    }
                    else parameterTypes[i] = parameterType;
                }
                cache.put(method, new DesktopSymbol(options, getSymbolAddress(name), returnType, parameterTypes));
            }
        }
        return (T) Proxy.newProxyInstance(loader, new Class[] { clazz }, (proxy, method, args) -> {
            if (args == null) args = EmptyArray.OBJECT;
            if (method.getDeclaringClass() == Object.class) return ReflectionSupport.invokeMethod(proxy, method, args);
            else {
                Object result = cache.get(method).invoke(args);
                if (result instanceof Integer) {
                    Class<?> returnType = method.getReturnType();
                    if (returnType == long.class) return ((Number) result).longValue();
                    else return result;
                }
                else return result;
            }
        });
    }

    public static <T extends unrefined.util.foreign.Library> T downcallProxy(int options, Class<T> clazz) {
        return downcallProxy(options, clazz, ReflectionSupport.getCallerClass().getClassLoader());
    }

    public static Symbol downcallHandle(int options, long function, Class<?> returnType, Class<?>... parameterTypes) {
        return new DesktopSymbol(options, function, returnType, parameterTypes);
    }

    public static Symbol upcallStub(int options, Object object, Method method, Class<?> returnType, Class<?>... parameterTypes) {
        return new DesktopSymbol(options, object, method, returnType, parameterTypes);
    }

    private static void invokeVoidFunction0(int options, long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.VOID, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.VOID, SymbolSupport.toFFITypes(args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        INVOKER.invokeInt(context, address, heapInvocationBuffer);
    }

    public static void invokeVoidFunction(int options, long address, Object... args) {
        if ((options & Symbol.Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            invokeVoidFunction0(options | Symbol.Option.SAVE_ERRNO, address, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno != 0) throw new LastErrorException(errno);
        }
        invokeVoidFunction0(options, address, args);
    }

    private static boolean invokeBooleanFunction0(int options, long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.UINT8, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.UINT8, SymbolSupport.toFFITypes(args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return INVOKER.invokeInt(context, address, heapInvocationBuffer) != 0;
    }

    public static boolean invokeBooleanFunction(int options, long address, Object... args) {
        if ((options & Symbol.Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            boolean result = invokeBooleanFunction0(options | Symbol.Option.SAVE_ERRNO, address, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        return invokeBooleanFunction0(options, address, args);
    }

    private static byte invokeByteFunction0(int options, long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.SINT8, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.SINT8, SymbolSupport.toFFITypes(args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return (byte) (INVOKER.invokeInt(context, address, heapInvocationBuffer) & 0xFF);
    }

    public static byte invokeByteFunction(int options, long address, Object... args) {
        if ((options & Symbol.Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            byte result = invokeByteFunction0(options | Symbol.Option.SAVE_ERRNO, address, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        return invokeByteFunction0(options, address, args);
    }

    private static char invokeCharFunction0(int options, long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.UINT16, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.UINT16, SymbolSupport.toFFITypes(args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return (char) (INVOKER.invokeInt(context, address, heapInvocationBuffer) & 0xFFFF);
    }

    public static char invokeCharFunction(int options, long address, Object... args) {
        if ((options & Symbol.Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            char result = invokeCharFunction0(options | Symbol.Option.SAVE_ERRNO, address, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        return invokeCharFunction0(options, address, args);
    }

    private static short invokeShortFunction0(int options, long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.SINT16, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.SINT16, SymbolSupport.toFFITypes(args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return (short) (INVOKER.invokeInt(context, address, heapInvocationBuffer) & 0xFFFF);
    }

    public static short invokeShortFunction(int options, long address, Object... args) {
        if ((options & Symbol.Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            short result = invokeShortFunction0(options | Symbol.Option.SAVE_ERRNO, address, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        return invokeShortFunction0(options, address, args);
    }

    private static int invokeIntFunction0(int options, long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.SINT32, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.SINT32, SymbolSupport.toFFITypes(args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return INVOKER.invokeInt(context, address, heapInvocationBuffer);
    }

    public static int invokeIntFunction(int options, long address, Object... args) {
        if ((options & Symbol.Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            int result = invokeIntFunction0(options | Symbol.Option.SAVE_ERRNO, address, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        return invokeIntFunction0(options, address, args);
    }

    private static long invokeNativeIntFunction0(int options, long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.SINT, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.SINT, SymbolSupport.toFFITypes(args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return NATIVE_INT_INVOKER.invoke(context, address, heapInvocationBuffer);
    }

    public static long invokeNativeIntFunction(int options, long address, Object... args) {
        if ((options & Symbol.Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            long result = invokeNativeIntFunction0(options | Symbol.Option.SAVE_ERRNO, address, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        return invokeNativeIntFunction0(options, address, args);
    }

    private static long invokeLongFunction0(int options, long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.SINT64, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.SINT64, SymbolSupport.toFFITypes(args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return INVOKER.invokeLong(context, address, heapInvocationBuffer);
    }

    public static long invokeLongFunction(int options, long address, Object... args) {
        if ((options & Symbol.Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            long result = invokeLongFunction0(options | Symbol.Option.SAVE_ERRNO, address, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        return invokeLongFunction0(options, address, args);
    }

    private static long invokeNativeLongFunction0(int options, long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.SLONG, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.SLONG, SymbolSupport.toFFITypes(args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return NATIVE_LONG_INVOKER.invoke(context, address, heapInvocationBuffer);
    }

    public static long invokeNativeLongFunction(int options, long address, Object... args) {
        if ((options & Symbol.Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            long result = invokeNativeLongFunction0(options | Symbol.Option.SAVE_ERRNO, address, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        return invokeNativeLongFunction0(options, address, args);
    }

    private static float invokeFloatFunction0(int options, long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.FLOAT, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.FLOAT, SymbolSupport.toFFITypes(args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return INVOKER.invokeFloat(context, address, heapInvocationBuffer);
    }

    public static float invokeFloatFunction(int options, long address, Object... args) {
        if ((options & Symbol.Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            float result = invokeFloatFunction0(options | Symbol.Option.SAVE_ERRNO, address, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        return invokeFloatFunction0(options, address, args);
    }

    private static double invokeDoubleFunction0(int options, long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.DOUBLE, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.DOUBLE, SymbolSupport.toFFITypes(args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return INVOKER.invokeDouble(context, address, heapInvocationBuffer);
    }

    public static double invokeDoubleFunction(int options, long address, Object... args) {
        if ((options & Symbol.Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            double result = invokeDoubleFunction0(options | Symbol.Option.SAVE_ERRNO, address, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        return invokeDoubleFunction0(options, address, args);
    }

    private static long invokeAddressFunction0(int options, long address, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(Type.POINTER, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(Type.POINTER, SymbolSupport.toFFITypes(args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        return INVOKER.invokeAddress(context, address, heapInvocationBuffer);
    }

    public static long invokeAddressFunction(int options, long address, Object... args) {
        if ((options & Symbol.Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            long result = invokeAddressFunction0(options | Symbol.Option.SAVE_ERRNO, address, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        return invokeAddressFunction0(options, address, args);
    }

    public static <T extends Aggregate> T invokeAggregateFunction0(int options, long address, Class<T> returnType, Object... args) {
        CallContext context;
        HeapInvocationBuffer heapInvocationBuffer;
        if (args.length > 0 && args[args.length - 1].getClass().isArray()) {
            Type[] nonVariadicFFITypes = SymbolSupport.toFFITypes(args, args.length - 1);
            context = CallContext.getCallContext(SymbolSupport.toFFIType(returnType), nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBufferVariadic(context, args);
        }
        else {
            context = CallContext.getCallContext(SymbolSupport.toFFIType(returnType), SymbolSupport.toFFITypes(args),
                    (options & Symbol.Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Symbol.Option.SAVE_ERRNO) != 0);
            heapInvocationBuffer = SymbolSupport.toHeapInvocationBuffer(context, args);
        }
        byte[] struct = INVOKER.invokeStruct(context, address, heapInvocationBuffer);
        SymbolSupport.reverseIfNeeded(struct);
        return Aggregate.newInstance(returnType, Pointer.wrap(struct));
    }

    public static <T extends Aggregate> T invokeAggregateFunction(int options, long address, Class<T> returnType, Object... args) {
        if ((options & Symbol.Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            T result = invokeAggregateFunction0(options | Symbol.Option.SAVE_ERRNO, address, returnType, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        return invokeAggregateFunction0(options, address, returnType, args);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeFunction(int options, long address, Class<T> returnType, Object... args) {
        if (returnType == void.class) {
            invokeVoidFunction(options, address, args);
            return null;
        }
        else if (returnType == boolean.class) return (T) Boolean.valueOf(invokeBooleanFunction(options, address, args));
        else if (returnType == byte.class) return (T) Byte.valueOf(invokeByteFunction(options, address, args));
        else if (returnType == char.class) return (T) Character.valueOf(invokeCharFunction(options, address, args));
        else if (returnType == short.class) return (T) Short.valueOf(invokeShortFunction(options, address, args));
        else if (returnType == int.class) return (T) Integer.valueOf(invokeIntFunction(options, address, args));
        else if (returnType == long.class) return (T) Long.valueOf(invokeLongFunction(options, address, args));
        else if (returnType == float.class) return (T) Float.valueOf(invokeFloatFunction(options, address, args));
        else if (returnType == double.class) return (T) Double.valueOf(invokeDoubleFunction(options, address, args));
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
        return OSInfo.NATIVE_CHARSET;
    }

    public static int systemCharSize() {
        return OSInfo.NATIVE_CHAR_SIZE;
    }

    public static Charset wideCharset() {
        return OSInfo.WIDE_CHARSET;
    }

    public static int wideCharSize() {
        return OSInfo.WIDE_CHAR_SIZE;
    }

    public static final IntFunction<String> ERROR_STRING_PRODUCER;
    static {
        if (OSInfo.IS_WINDOWS) {
            Function FormatMessageW = new Function(WindowsLibrary.Kernel32.getSymbolAddress("FormatMessageW"),
                    CallContext.getCallContext(Type.UINT32,
                            new Type[] {Type.UINT32, Type.POINTER, Type.UINT32, Type.UINT32, Type.POINTER, Type.UINT32, Type.POINTER},
                            CallingConvention.DEFAULT, false));
            int size = 4096;
            long lpBuffer = UNSAFE.allocateMemory((long) size * OSInfo.WIDE_CHAR_SIZE);
            ERROR_STRING_PRODUCER = new IntFunction<String>() {
                @Override
                public synchronized String apply(int errno) {
                    HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(FormatMessageW);
                    heapInvocationBuffer.putInt(0x00001000); // FORMAT_MESSAGE_FROM_SYSTEM
                    heapInvocationBuffer.putAddress(0);
                    heapInvocationBuffer.putInt(errno);
                    heapInvocationBuffer.putInt(0);
                    heapInvocationBuffer.putAddress(lpBuffer);
                    heapInvocationBuffer.putInt(size);
                    heapInvocationBuffer.putAddress(0);
                    if (INVOKER.invokeInt(FormatMessageW, heapInvocationBuffer) == 0) return null;
                    else return new String(MEMORY_IO.getZeroTerminatedByteArray(lpBuffer), OSInfo.NATIVE_CHARSET);
                }
            };
        }
        else {
            Function function = new Function(Library.getDefault().getSymbolAddress("strerror"),
                    CallContext.getCallContext(Type.POINTER, new Type[] {Type.SINT}, CallingConvention.DEFAULT, false));
            ERROR_STRING_PRODUCER = errno -> {
                HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(function);
                if (ABI.I == 8) heapInvocationBuffer.putLong(errno);
                else heapInvocationBuffer.putInt(errno);
                long string = INVOKER.invokeAddress(function, heapInvocationBuffer);
                return string == 0 ? null : new String(MEMORY_IO.getZeroTerminatedByteArray(string), OSInfo.NATIVE_CHARSET);
            };
        }
    }

}
