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
import unrefined.util.foreign.Symbol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static unrefined.desktop.ForeignSupport.NATIVE_INT_INVOKER;
import static unrefined.desktop.ForeignSupport.NATIVE_LONG_INVOKER;

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

    public DesktopSymbol(long address, Class<?> returnType, Class<?>... parameterTypes) {
        if (address == 0) throw new NullPointerException("address == NULL");
        else this.address = address;
        varargs = parameterTypes.length > 0 && parameterTypes[parameterTypes.length - 1].isArray();
        if (varargs) {
            function = null;
            returnFFIType = SymbolSupport.toFFIType(returnType);
            nonVariadicFFITypes = SymbolSupport.toFFITypes(parameterTypes, 0, parameterTypes.length - 1);
        }
        else {
            function = new Function(address, SymbolSupport.toFFIType(returnType), SymbolSupport.toFFITypes(parameterTypes));
            nonVariadicFFITypes = null;
            returnFFIType = null;
        }
        this.returnType = returnType;
        this.parameterTypes = parameterTypes.clone();
        this.markerTypes = Arrays.asList(parameterTypes);
        closure = null;
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
        else if (returnType == Pointer.class) buffer.setAddressReturn(((Number) result).longValue());
    }

    public DesktopSymbol(Object object, Method method, Class<?> returnType, Class<?>... parameterTypes) {
        if (!Modifier.isStatic(method.getModifiers())) Objects.requireNonNull(object);
        Parameter[] parameters = method.getParameters();
        int parameterCount = parameters.length;
        if (parameterCount != parameterTypes.length) throw new IndexOutOfBoundsException("Array length mismatch");
        Class<?> methodReturnType = method.getReturnType();
        if (returnType == Pointer.class) {
            if (methodReturnType != long.class) throw new IllegalArgumentException("Illegal method return type; expected long");
        }
        else if (methodReturnType != returnType)
            throw new IllegalArgumentException("Illegal method return type; expected " + returnType);
        this.returnType = returnType;
        for (int i = 0; i < parameterCount; i ++) {
            if (parameterTypes[i] == Pointer.class) {
                if (parameters[i].getType() != long.class) throw new IllegalArgumentException("Illegal method return type; expected long");
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
                CallingConvention.DEFAULT, true);
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
                else if (parameterType == Pointer.class) {
                    args[i] = buffer.getAddress(index);
                    if (ABI.P != 8) index ++;
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

    @Override
    public void invokeVoid(Object... args) {
        if (returnType != void.class) throw new IllegalArgumentException("Illegal return type; expected void");
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    CallingConvention.DEFAULT, true);
            INVOKER.invokeInt(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else INVOKER.invokeInt(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public boolean invokeBoolean(Object... args) {
        if (returnType != boolean.class) throw new IllegalArgumentException("Illegal return type; expected boolean");
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    CallingConvention.DEFAULT, true);
            return INVOKER.invokeInt(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args)) != 0;
        }
        else return INVOKER.invokeInt(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args)) != 0;
    }

    @Override
    public byte invokeByte(Object... args) {
        if (returnType != byte.class) throw new IllegalArgumentException("Illegal return type; expected byte");
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    CallingConvention.DEFAULT, true);
            return (byte) (INVOKER.invokeInt(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args)) & 0xFF);
        }
        else return (byte) (INVOKER.invokeInt(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args)) & 0xFF);
    }

    @Override
    public char invokeChar(Object... args) {
        if (returnType != char.class) throw new IllegalArgumentException("Illegal return type; expected char");
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    CallingConvention.DEFAULT, true);
            return (char) (INVOKER.invokeInt(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args)) & 0xFFFF);
        }
        else return (char) (INVOKER.invokeInt(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args)) & 0xFFFF);
    }

    @Override
    public short invokeShort(Object... args) {
        if (returnType != short.class) throw new IllegalArgumentException("Illegal return type; expected short");
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    CallingConvention.DEFAULT, true);
            return (short) (INVOKER.invokeInt(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args)) & 0xFFFF);
        }
        else return (short) (INVOKER.invokeInt(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args)) & 0xFFFF);
    }

    @Override
    public int invokeInt(Object... args) {
        if (returnType != int.class) throw new IllegalArgumentException("Illegal return type; expected int");
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    CallingConvention.DEFAULT, true);
            return INVOKER.invokeInt(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else return INVOKER.invokeInt(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public long invokeNativeInt(Object... args) {
        if (returnType != ABI.I_TYPE) throw new IllegalArgumentException("Illegal return type; expected " + ABI.I_TYPE);
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    CallingConvention.DEFAULT, true);
            return NATIVE_INT_INVOKER.invoke(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else return NATIVE_INT_INVOKER.invoke(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public long invokeLong(Object... args) {
        if (returnType != long.class) throw new IllegalArgumentException("Illegal return type; expected long");
        CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                CallingConvention.DEFAULT, true);
        if (varargs) return INVOKER.invokeLong(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        else return INVOKER.invokeLong(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public long invokeNativeLong(Object... args) {
        if (returnType != ABI.L_TYPE) throw new IllegalArgumentException("Illegal return type; expected " + ABI.L_TYPE);
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    CallingConvention.DEFAULT, true);
            return NATIVE_LONG_INVOKER.invoke(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else return NATIVE_LONG_INVOKER.invoke(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public float invokeFloat(Object... args) {
        if (returnType != float.class) throw new IllegalArgumentException("Illegal return type; expected void");
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    CallingConvention.DEFAULT, true);
            return INVOKER.invokeFloat(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else return INVOKER.invokeFloat(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public double invokeDouble(Object... args) {
        if (returnType != double.class) throw new IllegalArgumentException("Illegal return type; expected double");
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                    CallingConvention.DEFAULT, true);
            return INVOKER.invokeDouble(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else return INVOKER.invokeDouble(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
    }

    @Override
    public long invokeAddress(Object... args) {
        if (returnType != Pointer.class) throw new IllegalArgumentException("Illegal return type; expected pointer");
        if (varargs) {
            CallContext context = CallContext.getCallContext(returnFFIType, nonVariadicFFITypes.length,
                    SymbolSupport.expandVariadicFFITypes(nonVariadicFFITypes, args[args.length - 1]),
                CallingConvention.DEFAULT, true);
            return INVOKER.invokeAddress(context, address, SymbolSupport.toHeapInvocationBufferVariadic(context, parameterTypes, args));
        }
        else return INVOKER.invokeAddress(function, SymbolSupport.toHeapInvocationBuffer(function.getCallContext(), parameterTypes, args));
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
        else if (returnType == Pointer.class) return invokeAddress(args);
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
                "address=" + address +
                '}';
    }

}
