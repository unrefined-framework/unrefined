package unrefined.runtime;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.Closure;
import com.kenai.jffi.ClosureManager;
import com.kenai.jffi.Function;
import com.kenai.jffi.Invoker;
import com.kenai.jffi.Type;
import unrefined.desktop.ABI;
import unrefined.desktop.ReflectionSupport;
import unrefined.desktop.SymbolSupport;
import unrefined.nio.Pointer;
import unrefined.util.UnexpectedError;
import unrefined.util.foreign.Aggregate;
import unrefined.util.foreign.LastErrorException;
import unrefined.util.foreign.Symbol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static unrefined.desktop.ForeignSupport.*;

public class DesktopSymbol extends Symbol {

    private static final Invoker INVOKER = Invoker.getInstance();

    private final boolean varargs;
    private final Type[] nonVariadicFFITypes;
    private final Type returnFFIType;

    private final Class<?> returnType;
    private final Class<?>[] parameterTypes;
    private final List<Class<?>> markerTypes;

    private final long address;

    private final Function function;
    private final Closure.Handle closure;
    private final int options;

    public DesktopSymbol(int options, long address, Class<?> returnType, Class<?>... parameterTypes) {
        if (address == 0) throw new NullPointerException("address == NULL");
        else this.address = address;
        this.options = Option.removeUnusedBits(options);
        varargs = parameterTypes.length > 0 && parameterTypes[parameterTypes.length - 1].isArray();
        if (varargs) {
            function = null;
            returnFFIType = SymbolSupport.toFFIType(returnType);
            nonVariadicFFITypes = SymbolSupport.toFFITypes(parameterTypes, 0, parameterTypes.length - 1);
        }
        else {
            function = new Function(address, SymbolSupport.toFFIType(returnType), SymbolSupport.toFFITypes(parameterTypes),
                    CallingConvention.DEFAULT, false);
            nonVariadicFFITypes = null;
            returnFFIType = null;
        }
        this.returnType = returnType;
        this.parameterTypes = parameterTypes.clone();
        this.markerTypes = Arrays.asList(parameterTypes);
        closure = null;
    }

    private static byte[] toByteArray(Aggregate aggregate) {
        byte[] array = new byte[(int) aggregate.getDescriptor().getSize()];
        aggregate.memory().getByteArray(0, array);
        return SymbolSupport.reverseIfNeeded(array);
    }

    private static void push(Closure.Buffer buffer, Object result, Class<?> returnType) {
        if (result == null) return;
        if (returnType == boolean.class) buffer.setByteReturn((byte) (((Boolean) result) ? 1 : 0));
        else if (returnType == byte.class) buffer.setByteReturn(((Number) result).byteValue());
        else if (returnType == char.class) buffer.setShortReturn((short) ((Character) result).charValue());
        else if (returnType == short.class) buffer.setShortReturn(((Number) result).shortValue());
        else if (returnType == int.class) buffer.setIntReturn(((Number) result).intValue());
        else if (returnType == long.class) buffer.setLongReturn(((Number) result).longValue());
        else if (returnType == float.class) buffer.setFloatReturn(((Number) result).floatValue());
        else if (returnType == double.class) buffer.setDoubleReturn(((Number) result).doubleValue());
        else if (Aggregate.class.isAssignableFrom(returnType)) buffer.setStructReturn(toByteArray((Aggregate) result), 0);
    }

    @SuppressWarnings("unchecked")
    public DesktopSymbol(int options, Object object, Method method, Class<?> returnType, Class<?>... parameterTypes) {
        if (!Modifier.isStatic(method.getModifiers())) Objects.requireNonNull(object);
        this.options = Option.removeUnusedBits(options);
        Parameter[] parameters = method.getParameters();
        int parameterCount = parameters.length;
        if (parameterCount != parameterTypes.length) throw new IndexOutOfBoundsException("Array length mismatch");
        Class<?> methodReturnType = method.getReturnType();
        if (methodReturnType != returnType) throw new IllegalArgumentException("Illegal method return type; expected " + returnType);
        this.returnType = returnType;
        for (int i = 0; i < parameterCount; i ++) {
            if (Aggregate.class.isAssignableFrom(parameterTypes[i])) {
                if (parameters[i].getType() != parameterTypes[i]) throw new IllegalArgumentException("Illegal argument type; expected " + parameterTypes[i]);
            }
            else if (!SymbolSupport.matches(parameterTypes[i], parameters[i].getType()))
                throw new IllegalArgumentException("Illegal argument type; expected " + parameterTypes[i]);
        }
        if (parameterCount > 0 && parameterTypes[parameterCount - 1].isArray())
            throw new UnsupportedOperationException("Variadic arguments not supported");
        varargs = false;
        nonVariadicFFITypes = null;
        returnFFIType = null;
        CallContext context = CallContext.getCallContext(
                SymbolSupport.toFFIType(returnType), SymbolSupport.toFFITypes(parameterTypes),
                CallingConvention.DEFAULT, false);
        this.parameterTypes = parameterTypes.clone();
        markerTypes = Arrays.asList(parameterTypes);
        closure = ClosureManager.getInstance().newClosure(buffer -> {
            Object[] args = new Object[parameterCount];
            int index = 0;
            for (int i = 0; i < parameterCount; i ++) {
                Class<?> parameterType = parameterTypes[i];
                if (parameterType == boolean.class) args[i] = buffer.getByte(index) != 0;
                else if (parameterType == byte.class) args[i] = buffer.getByte(index);
                else if (parameterType == char.class) args[i] = (char) buffer.getShort(index);
                else if (parameterType == short.class) args[i] = buffer.getShort(index);
                else if (parameterType == int.class) args[i] = buffer.getInt(index);
                else if (parameterType == long.class) {
                    args[i] = buffer.getLong(index);
                    if (ABI.P != 8) index ++;
                }
                else if (parameterType == float.class) args[i] = buffer.getFloat(index);
                else if (parameterType == double.class) {
                    args[i] = buffer.getDouble(index);
                    if (ABI.P != 8) index ++;
                }
                else if (Aggregate.class.isAssignableFrom(parameterType)) {
                    byte[] struct = new byte[(int) Aggregate.sizeOfType((Class<? extends Aggregate>) parameterType)];
                    MEMORY_IO.getByteArray(buffer.getStruct(index), struct, 0, struct.length);
                    args[i] = Aggregate.newInstance((Class<? extends Aggregate>) parameterType, Pointer.wrap(SymbolSupport.reverseIfNeeded(struct)));
                }
                index ++;
            }
            try {
                push(buffer, ReflectionSupport.invokeMethod(object, method, args), returnType);
            } catch (InvocationTargetException e) {
                throw new UnexpectedError(e);
            }
        }, context);
        closure.setAutoRelease(true);
        address = closure.getAddress();
        function = new Function(address, context);
        
    }

    @Override
    public long address() {
        return address;
    }

    @Override
    public List<Class<?>> getParameterTypes() {
        return markerTypes;
    }

    @Override
    public Class<?> getReturnType() {
        return returnType;
    }

    @Override
    public boolean isVarargs() {
        return varargs;
    }

    private void invokeVoid0(int options, Object... args) {
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    (options & Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Option.SAVE_ERRNO) != 0);
            INVOKER.invokeInt(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else INVOKER.invokeInt(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public void invokeVoid(Object... args) {
        if (returnType != void.class) throw new IllegalArgumentException("Illegal return type; expected void");
        if ((options & Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            invokeVoid0(options | Option.SAVE_ERRNO, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno != 0) throw new LastErrorException(errno);
        }
        else invokeVoid0(options, args);
    }

    private boolean invokeBoolean0(int options, Object... args) {
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    (options & Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Option.SAVE_ERRNO) != 0);
            return INVOKER.invokeInt(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args)) != 0;
        }
        else return INVOKER.invokeInt(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args)) != 0;
    }

    @Override
    public boolean invokeBoolean(Object... args) {
        if (returnType != boolean.class) throw new IllegalArgumentException("Illegal return type; expected boolean");
        if ((options & Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            boolean result = invokeBoolean0(options | Option.SAVE_ERRNO, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        else return invokeBoolean0(options, args);
    }

    private byte invokeByte0(int options, Object... args) {
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    (options & Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Option.SAVE_ERRNO) != 0);
            return (byte) (INVOKER.invokeInt(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args)) & 0xFF);
        }
        else return (byte) (INVOKER.invokeInt(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args)) & 0xFF);
    }

    @Override
    public byte invokeByte(Object... args) {
        if (returnType != byte.class) throw new IllegalArgumentException("Illegal return type; expected byte");
        if ((options & Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            byte result = invokeByte0(options | Option.SAVE_ERRNO, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        else return invokeByte0(options, args);
    }

    private char invokeChar0(int options, Object... args) {
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    (options & Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Option.SAVE_ERRNO) != 0);
            return (char) (INVOKER.invokeInt(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args)) & 0xFFFF);
        }
        else return (char) (INVOKER.invokeInt(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args)) & 0xFFFF);
    }

    @Override
    public char invokeChar(Object... args) {
        if (returnType != char.class) throw new IllegalArgumentException("Illegal return type; expected char");
        if ((options & Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            char result = invokeChar0(options | Option.SAVE_ERRNO, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        else return invokeChar0(options, args);
    }

    private short invokeShort0(int options, Object... args) {
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    (options & Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Option.SAVE_ERRNO) != 0);
            return (short) (INVOKER.invokeInt(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args)) & 0xFFFF);
        }
        else return (short) (INVOKER.invokeInt(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args)) & 0xFFFF);
    }

    @Override
    public short invokeShort(Object... args) {
        if (returnType != short.class) throw new IllegalArgumentException("Illegal return type; expected short");
        if ((options & Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            short result = invokeShort0(options | Option.SAVE_ERRNO, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        else return invokeShort0(options, args);
    }

    private int invokeInt0(int options, Object... args) {
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    (options & Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Option.SAVE_ERRNO) != 0);
            return INVOKER.invokeInt(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else return INVOKER.invokeInt(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public int invokeInt(Object... args) {
        if (returnType != int.class) throw new IllegalArgumentException("Illegal return type; expected int");
        if ((options & Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            int result = invokeInt0(options | Option.SAVE_ERRNO, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        else return invokeInt0(options, args);
    }

    private long invokeNativeInt0(int options, Object... args) {
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    (options & Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Option.SAVE_ERRNO) != 0);
            return NATIVE_INT_INVOKER.invoke(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else return NATIVE_INT_INVOKER.invoke(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public long invokeNativeInt(Object... args) {
        if (returnType != ABI.I_TYPE) throw new IllegalArgumentException("Illegal return type; expected " + ABI.I_TYPE);
        if ((options & Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            long result = invokeNativeInt0(options | Option.SAVE_ERRNO, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        else return invokeNativeInt0(options, args);
    }

    private long invokeLong0(int options, Object... args) {
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    (options & Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Option.SAVE_ERRNO) != 0);
            return INVOKER.invokeLong(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else return INVOKER.invokeLong(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public long invokeLong(Object... args) {
        if (returnType != long.class) throw new IllegalArgumentException("Illegal return type; expected long");
        if ((options & Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            long result = invokeLong0(options | Option.SAVE_ERRNO, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        else return invokeLong0(options, args);
    }

    private long invokeNativeLong0(int options, Object... args) {
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    (options & Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Option.SAVE_ERRNO) != 0);
            return NATIVE_LONG_INVOKER.invoke(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else return NATIVE_LONG_INVOKER.invoke(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public long invokeNativeLong(Object... args) {
        if (returnType != ABI.L_TYPE) throw new IllegalArgumentException("Illegal return type; expected " + ABI.L_TYPE);
        if ((options & Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            long result = invokeNativeLong0(options | Option.SAVE_ERRNO, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        else return invokeNativeLong0(options, args);
    }

    private float invokeFloat0(int options, Object... args) {
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    (options & Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Option.SAVE_ERRNO) != 0);
            return INVOKER.invokeFloat(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else return INVOKER.invokeFloat(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public float invokeFloat(Object... args) {
        if (returnType != float.class) throw new IllegalArgumentException("Illegal return type; expected void");
        if ((options & Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            float result = invokeFloat0(options | Option.SAVE_ERRNO, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        else return invokeFloat0(options, args);
    }

    private double invokeDouble0(int options, Object... args) {
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    (options & Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Option.SAVE_ERRNO) != 0);
            return INVOKER.invokeDouble(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else return INVOKER.invokeDouble(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public double invokeDouble(Object... args) {
        if (returnType != double.class) throw new IllegalArgumentException("Illegal return type; expected double");
        if ((options & Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            double result = invokeDouble0(options | Option.SAVE_ERRNO, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        else return invokeDouble0(options, args);
    }

    private long invokeAddress0(int options, Object... args) {
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    (options & Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Option.SAVE_ERRNO) != 0);
            return INVOKER.invokeAddress(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else return INVOKER.invokeAddress(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public long invokeAddress(Object... args) {
        if (returnType != ABI.P_TYPE) throw new IllegalArgumentException("Illegal return type; expected " + ABI.P_TYPE);
        if ((options & Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            long result = invokeAddress0(options | Option.SAVE_ERRNO, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        else return invokeAddress0(options, args);
    }

    @SuppressWarnings("unchecked")
    private Aggregate invokeAggregate0(int options, Object... args) {
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    (options & Option.ALT_CALL) != 0 ? CallingConvention.STDCALL : CallingConvention.DEFAULT,
                    (options & Option.SAVE_ERRNO) != 0);
            return Aggregate.newInstance((Class<? extends Aggregate>) returnType, Pointer.wrap(SymbolSupport.reverseIfNeeded(
                    INVOKER.invokeStruct(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args)))));
        }
        else return Aggregate.newInstance((Class<? extends Aggregate>) returnType, Pointer.wrap(SymbolSupport.reverseIfNeeded(
                INVOKER.invokeStruct(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args)))));
    }

    @Override
    public Aggregate invokeAggregate(Object... args) {
        if (!Aggregate.class.isAssignableFrom(returnType)) throw new IllegalArgumentException("Illegal return type; expected aggregate");
        if ((options & Option.THROW_ERRNO) != 0) {
            int prev = LAST_ERROR.get();
            LAST_ERROR.set(0);
            Aggregate result = invokeAggregate0(options | Option.SAVE_ERRNO, args);
            int errno = LAST_ERROR.get();
            LAST_ERROR.set(prev);
            if (errno == 0) return result;
            else throw new LastErrorException(errno);
        }
        else return invokeAggregate0(options, args);
    }

    @Override
    public Object invoke(Object... args) {
        if (returnType == void.class) {
            invokeVoid(args);
            return null;
        }
        else if (returnType == boolean.class) return invokeBoolean(args);
        else if (returnType == byte.class) return invokeByte(args);
        else if (returnType == char.class) return invokeChar(args);
        else if (returnType == short.class) return invokeShort(args);
        else if (returnType == int.class) return invokeInt(args);
        else if (returnType == long.class) return invokeLong(args);
        else if (returnType == float.class) return invokeFloat(args);
        else if (returnType == double.class) return invokeDouble(args);
        else if (Aggregate.class.isAssignableFrom(returnType)) return invokeAggregate(args);
        else throw new UnexpectedError();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DesktopSymbol that = (DesktopSymbol) o;

        if (varargs != that.varargs) return false;
        if (address != that.address) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(nonVariadicFFITypes, that.nonVariadicFFITypes)) return false;
        if (!Objects.equals(returnFFIType, that.returnFFIType))
            return false;
        if (!returnType.equals(that.returnType)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(parameterTypes, that.parameterTypes)) return false;
        if (!markerTypes.equals(that.markerTypes)) return false;
        if (!Objects.equals(function, that.function)) return false;
        return Objects.equals(closure, that.closure);
    }

    @Override
    public int hashCode() {
        int result = (varargs ? 1 : 0);
        result = 31 * result + Arrays.hashCode(nonVariadicFFITypes);
        result = 31 * result + (returnFFIType != null ? returnFFIType.hashCode() : 0);
        result = 31 * result + returnType.hashCode();
        result = 31 * result + Arrays.hashCode(parameterTypes);
        result = 31 * result + markerTypes.hashCode();
        result = 31 * result + (int) (address ^ (address >>> 32));
        result = 31 * result + (function != null ? function.hashCode() : 0);
        result = 31 * result + (closure != null ? closure.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName() + '@' + Integer.toHexString(hashCode())
                + '{' +
                "options=" + Option.toString(options) +
                ", address=" + address +
                '}';
    }

}
