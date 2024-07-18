package unrefined.desktop;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.Closure;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Library;
import com.kenai.jffi.Type;
import unrefined.nio.Pointer;
import unrefined.util.Arrays;
import unrefined.util.NotInstantiableError;
import unrefined.util.Strings;
import unrefined.util.UnexpectedError;
import unrefined.util.foreign.Aggregate;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteOrder;
import java.util.Objects;

public class SymbolSupport {

    private SymbolSupport() {
        throw new NotInstantiableError(SymbolSupport.class);
    }

    public static long find(Library library, String name) {
        if (Strings.isBlank(Objects.requireNonNull(name))) throw new IllegalArgumentException("blank symbol name");
        return library.getSymbolAddress(name);
    }

    private static final Method findNativeMethod;
    static {
        Method method;
        try {
            method = ClassLoader.class.getDeclaredMethod("findNative", ClassLoader.class, String.class);
        } catch (NoSuchMethodException e) {
            method = null;
        }
        findNativeMethod = method;
    }

    public static long find(ClassLoader classLoader, String name) {
        if (Strings.isBlank(Objects.requireNonNull(name))) throw new IllegalArgumentException("blank symbol name");
        try {
            return ReflectionSupport.invokeLongMethod(null, findNativeMethod, classLoader, name);
        } catch (InvocationTargetException e) {
            throw new UnexpectedError(e);
        }
    }

    public static long find(String name) {
        if (Strings.isBlank(Objects.requireNonNull(name))) throw new IllegalArgumentException("blank symbol name");
        return find(ReflectionSupport.getCallerClass().getClassLoader(), name);
    }

    @SuppressWarnings("unchecked")
    public static Type toFFIType(Object type) {
        if (type instanceof Class) {
            if (type == void.class) return Type.VOID;
            else if (type == boolean.class) return Type.UINT8;
            else if (type == byte.class) return Type.SINT8;
            else if (type == char.class) return Type.UINT16;
            else if (type == short.class) return Type.SINT16;
            else if (type == int.class) return Type.SINT32;
            else if (type == long.class) return Type.SINT64;
            else if (type == float.class) return Type.FLOAT;
            else if (type == double.class) return Type.DOUBLE;
            else if (Aggregate.class.isAssignableFrom((Class<?>) type))
                return AggregateSupport.typeOf((Class<? extends Aggregate>) type);
        }
        else if (type instanceof Aggregate.Descriptor) return AggregateSupport.typeOf((Aggregate.Descriptor) type);
        Objects.requireNonNull(type);
        throw new IllegalArgumentException("Illegal type: " + type);
    }

    @SuppressWarnings("unchecked")
    public static Type toFFITypeBoxed(Object type) {
        if (type instanceof Class) {
            if (type == Boolean.class) return Type.UINT8;
            else if (type == Byte.class) return Type.SINT8;
            else if (type == Character.class) return Type.UINT16;
            else if (type == Short.class) return Type.SINT16;
            else if (type == Integer.class) return Type.SINT32;
            else if (type == Long.class) return Type.SINT64;
            else if (type == Float.class) return Type.FLOAT;
            else if (type == Double.class) return Type.DOUBLE;
            else if (Aggregate.class.isAssignableFrom((Class<?>) type))
                return AggregateSupport.typeOf((Class<? extends Aggregate>) type);
        }
        else if (type instanceof Aggregate.Descriptor) return AggregateSupport.typeOf((Aggregate.Descriptor) type);
        Objects.requireNonNull(type);
        throw new IllegalArgumentException("Illegal type: " + type);
    }

    public static Type[] toFFITypes(Object... types) {
        Type[] result = new Type[types.length];
        for (int i = 0; i < types.length; i ++) {
            result[i] = toFFIType(types[i]);
        }
        return result;
    }

    public static Type[] toFFITypes(Object[] types, int offset, int length) {
        Type[] result = new Type[length];
        for (int i = 0; i < length; i ++) {
            result[i] = toFFIType(types[offset + i]);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Type[] getFFITypesFromArray(Object array) {
        Class<?> clazz = array.getClass();
        if (clazz.isArray()) {
            Type[] types = new Type[Array.getLength(array)];
            clazz = clazz.getComponentType();
            if (clazz == boolean.class) {
                java.util.Arrays.fill(types, Type.UINT8);
                return types;
            }
            else if (clazz == byte.class) {
                java.util.Arrays.fill(types, Type.SINT8);
                return types;
            }
            else if (clazz == char.class) {
                java.util.Arrays.fill(types, Type.UINT16);
                return types;
            }
            else if (clazz == short.class) {
                java.util.Arrays.fill(types, Type.SINT16);
                return types;
            }
            else if (clazz == int.class) {
                java.util.Arrays.fill(types, Type.SINT32);
                return types;
            }
            else if (clazz == long.class) {
                java.util.Arrays.fill(types, Type.SINT64);
                return types;
            }
            else if (clazz == float.class) {
                java.util.Arrays.fill(types, Type.FLOAT);
                return types;
            }
            else if (clazz == double.class) {
                java.util.Arrays.fill(types, Type.DOUBLE);
                return types;
            }
            else if (clazz == Object.class) {
                for (int i = 0; i < types.length; i ++) {
                    Object obj = Array.get(array, i);
                    Class<?> cl = obj.getClass();
                    types[i] = toFFITypeBoxed(
                            (Aggregate.class.isAssignableFrom(cl) &&
                                    Aggregate.isProxyClass((Class<? extends Aggregate>) cl)) ?
                                    ((Aggregate) obj).getDescriptor() : cl);
                }
                return types;
            }
        }
        throw new IllegalArgumentException("Illegal type: " + clazz);
    }

    @SuppressWarnings("unchecked")
    public static void pushArgument(HeapInvocationBuffer heapInvocationBuffer, Object marker, Object arg) {
        if (marker instanceof Class) {
            if (marker == boolean.class) heapInvocationBuffer.putByte((Boolean) arg ? 1 : 0);
            else if (marker == byte.class) heapInvocationBuffer.putByte(((Number) arg).byteValue());
            else if (marker == char.class) heapInvocationBuffer.putShort((Character) arg);
            else if (marker == short.class) heapInvocationBuffer.putShort(((Number) arg).shortValue());
            else if (marker == int.class) heapInvocationBuffer.putInt(((Number) arg).intValue());
            else if (marker == long.class) heapInvocationBuffer.putLong(((Number) arg).longValue());
            else if (marker == float.class) heapInvocationBuffer.putFloat(((Number) arg).floatValue());
            else if (marker == double.class) heapInvocationBuffer.putDouble(((Number) arg).doubleValue());
            else if (Aggregate.class.isAssignableFrom((Class<?>) marker)) {
                Pointer memory = ((Aggregate) arg).payload();
                if (memory.isDirect()) heapInvocationBuffer.putStruct(memory.address());
                else {
                    byte[] struct = new byte[(int) Aggregate.sizeOfType((Class<? extends Aggregate>) marker)];
                    memory.getByteArray(0, struct);
                    heapInvocationBuffer.putStruct(reverseIfNeeded(struct, Aggregate.descriptorOf((Class<? extends Aggregate>) marker)), 0);
                }
            }
        }
        else if (marker instanceof Aggregate.Descriptor) {
            Pointer memory = ((Aggregate) arg).payload();
            if (memory.isDirect()) heapInvocationBuffer.putStruct(memory.address());
            else {
                byte[] struct = new byte[(int) ((Aggregate.Descriptor) marker).size()];
                memory.getByteArray(0, struct);
                heapInvocationBuffer.putStruct(reverseIfNeeded(struct, (Aggregate.Descriptor) marker), 0);
            }
        }
        else throw new IllegalArgumentException("Illegal argument type: " + marker);
    }

    private static final boolean LITTLE_ENDIAN = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN);

    private static void pushArgument(HeapInvocationBuffer heapInvocationBuffer, Aggregate arg) {
        Pointer pointer = arg.payload();
        if (pointer.isDirect()) heapInvocationBuffer.putStruct(pointer.address());
        else {
            byte[] struct = new byte[(int) arg.getDescriptor().size()];
            pointer.getByteArray(0, struct);
            heapInvocationBuffer.putStruct(reverseIfNeeded(struct, arg.getDescriptor()), 0);
        }
    }

    public static byte[] reverseIfNeeded(byte[] array, Aggregate.Descriptor descriptor) {
        if (LITTLE_ENDIAN) {
            for (Aggregate.Member member : descriptor.getMembers()) {
                Arrays.reverse(array, (int) member.getOffset(), (int) member.getOffset() + (int) member.size());
            }
        }
        return array;
    }

    public static void pushArgument(HeapInvocationBuffer heapInvocationBuffer, Object arg) {
        Class<?> marker = arg.getClass();
        if (marker == Boolean.class) heapInvocationBuffer.putByte((Boolean) arg ? 1 : 0);
        else if (marker == Byte.class) heapInvocationBuffer.putByte(((Number) arg).byteValue());
        else if (marker == Character.class) heapInvocationBuffer.putShort((Character) arg);
        else if (marker == Short.class) heapInvocationBuffer.putShort(((Number) arg).shortValue());
        else if (marker == Integer.class) heapInvocationBuffer.putInt(((Number) arg).intValue());
        else if (marker == Long.class) heapInvocationBuffer.putLong(((Number) arg).longValue());
        else if (marker == Float.class) heapInvocationBuffer.putFloat(((Number) arg).floatValue());
        else if (marker == Double.class) heapInvocationBuffer.putDouble(((Number) arg).doubleValue());
        else if (Aggregate.class.isAssignableFrom(marker)) pushArgument(heapInvocationBuffer, (Aggregate) arg);
        else throw new IllegalArgumentException("Illegal argument type: " + marker);
    }

    public static void pushVariadicArguments(HeapInvocationBuffer heapInvocationBuffer, Class<?> marker, Object varargs) {
        if (marker == boolean[].class) {
            boolean[] array = (boolean[]) varargs;
            for (boolean b : array) {
                heapInvocationBuffer.putByte(b ? 1 : 0);
            }
        }
        else if (marker == byte[].class) {
            byte[] array = (byte[]) varargs;
            for (byte b : array) {
                heapInvocationBuffer.putByte(b);
            }
        }
        else if (marker == char[].class) {
            char[] array = (char[]) varargs;
            for (char c : array) {
                heapInvocationBuffer.putShort(c);
            }
        }
        else if (marker == short[].class) {
            short[] array = (short[]) varargs;
            for (short s : array) {
                heapInvocationBuffer.putShort(s);
            }
        }
        else if (marker == int[].class) {
            int[] array = (int[]) varargs;
            for (int i : array) {
                heapInvocationBuffer.putInt(i);
            }
        }
        else if (marker == long[].class) {
            long[] array = (long[]) varargs;
            for (long l : array) {
                heapInvocationBuffer.putLong(l);
            }
        }
        else if (marker == float[].class) {
            float[] array = (float[]) varargs;
            for (float f : array) {
                heapInvocationBuffer.putFloat(f);
            }
        }
        else if (marker == double[].class) {
            double[] array = (double[]) varargs;
            for (double d : array) {
                heapInvocationBuffer.putDouble(d);
            }
        }
        else if (marker == Object[].class) {
            Object[] array = (Object[]) varargs;
            for (Object o : array) {
                pushArgument(heapInvocationBuffer, o);
            }
        }
        else throw new IllegalArgumentException("Illegal argument type: " + marker);
    }

    public static Type[] expandVariadicFFITypes(Type[] nonVariadicTypes, Object varargs) {
        Type[] variadicTypes = getFFITypesFromArray(varargs);
        Type[] types = java.util.Arrays.copyOf(nonVariadicTypes, nonVariadicTypes.length + variadicTypes.length);
        System.arraycopy(variadicTypes, 0, types, nonVariadicTypes.length, variadicTypes.length);
        return types;
    }

    public static boolean matchesBoxed(Class<?> marker, Class<?> boxed) {
        if (boxed == Void.class) return marker == void.class;
        else if (boxed == Boolean.class) return marker == boolean.class;
        else if (boxed == Character.class) return marker == char.class;
        else if (Number.class.isAssignableFrom(boxed)) {
            return marker == byte.class || marker == short.class || marker == int.class || marker == long.class
                    || marker == float.class || marker == double.class;
        }
        else if (Aggregate.class.isAssignableFrom(boxed)) {
            return marker == boxed;
        }
        else throw new UnexpectedError();
    }

    public static boolean matches(Class<?> marker, Class<?> primitive) {
        if (primitive == void.class) return marker == void.class;
        else if (primitive == boolean.class) return marker == boolean.class;
        else if (primitive == char.class) return marker == char.class;
        else if (primitive.isPrimitive()) {
            return marker == byte.class || marker == short.class || marker == int.class || marker == long.class
                    || marker == float.class || marker == double.class;
        }
        else if (Aggregate.class.isAssignableFrom(marker)) {
            return marker == primitive;
        }
        else throw new UnexpectedError();
    }

    public static HeapInvocationBuffer toHeapInvocationBuffer(CallContext context, Object... args) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(context);
        for (Object arg : args) {
            pushArgument(heapInvocationBuffer, arg);
        }
        return heapInvocationBuffer;
    }

    public static HeapInvocationBuffer toHeapInvocationBufferVariadic(CallContext context, Object... args) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(context);
        if (args.length > 0) {
            int variadic = args.length - 1;
            for (int i = 0; i < variadic; i ++) {
                pushArgument(heapInvocationBuffer, args[i]);
            }
            pushVariadicArguments(heapInvocationBuffer, args[variadic].getClass(), args[variadic]);
        }
        return heapInvocationBuffer;
    }

    public static HeapInvocationBuffer toHeapInvocationBuffer(CallContext context, Object[] parameterTypes, Object... args) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(context);
        if (args.length > 0) {
            for (int i = 0; i < parameterTypes.length; i ++) {
                Object marker = parameterTypes[i];
                if (marker instanceof Class) {
                    Class<?> boxed = args[i].getClass();
                    if (!matchesBoxed((Class<?>) marker, boxed)) throw new IllegalArgumentException("Illegal argument type; expected " + marker);
                }
                else if (marker instanceof Aggregate.Descriptor) {
                    if (!(args[i] instanceof Aggregate)) throw new IllegalArgumentException("Illegal argument type; expected " + Aggregate.class);
                    else if (!((Aggregate) args[i]).getDescriptor().equals(marker))
                        throw new IllegalArgumentException("Aggregate descriptor type mismatch");
                }
                else throw new UnexpectedError();
                pushArgument(heapInvocationBuffer, marker, args[i]);
            }
        }
        return heapInvocationBuffer;
    }

    public static HeapInvocationBuffer toHeapInvocationBufferVariadic(CallContext context, Object[] parameterTypes, Object... args) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(context);
        if (args.length > 0) {
            int variadic = parameterTypes.length - 1;
            for (int i = 0; i < variadic; i ++) {
                Object marker = parameterTypes[i];
                if (marker instanceof Class) {
                    Class<?> boxed = args[i].getClass();
                    if (!matchesBoxed((Class<?>) marker, boxed)) throw new IllegalArgumentException("Illegal argument type; expected " + marker);
                }
                else if (marker instanceof Aggregate.Descriptor) {
                    if (!(args[i] instanceof Aggregate)) throw new IllegalArgumentException("Illegal argument type; expected " + Aggregate.class);
                    else if (!((Aggregate) args[i]).getDescriptor().equals(marker))
                        throw new IllegalArgumentException("Aggregate descriptor type mismatch");
                }
                else throw new UnexpectedError();
                pushArgument(heapInvocationBuffer, marker, args[i]);
            }
            pushVariadicArguments(heapInvocationBuffer, (Class<?>) parameterTypes[variadic], args[variadic]);
        }
        return heapInvocationBuffer;
    }

    private static byte[] toByteArray(Aggregate aggregate) {
        byte[] array = new byte[(int) aggregate.getDescriptor().size()];
        aggregate.payload().getByteArray(0, array);
        return reverseIfNeeded(array, aggregate.getDescriptor());
    }

    public static void push(Closure.Buffer buffer, Object result, Object returnType) {
        if (result == null) return;
        if (returnType instanceof Class) {
            if (returnType == boolean.class) buffer.setByteReturn((byte) (((Boolean) result) ? 1 : 0));
            else if (returnType == byte.class) buffer.setByteReturn(((Number) result).byteValue());
            else if (returnType == char.class) buffer.setShortReturn((short) ((Character) result).charValue());
            else if (returnType == short.class) buffer.setShortReturn(((Number) result).shortValue());
            else if (returnType == int.class) buffer.setIntReturn(((Number) result).intValue());
            else if (returnType == long.class) buffer.setLongReturn(((Number) result).longValue());
            else if (returnType == float.class) buffer.setFloatReturn(((Number) result).floatValue());
            else if (returnType == double.class) buffer.setDoubleReturn(((Number) result).doubleValue());
            else if (Aggregate.class.isAssignableFrom((Class<?>) returnType)) buffer.setStructReturn(toByteArray((Aggregate) result), 0);
        }
        else if (returnType instanceof Aggregate.Descriptor) buffer.setStructReturn(toByteArray((Aggregate) result), 0);
    }

    public static Type getFFITypeFromObject(Object object) {
        if (object instanceof Aggregate && Aggregate.isProxyObject((Aggregate) object)) {
            return AggregateSupport.typeOf(((Aggregate) object).getDescriptor());
        }
        else return toFFITypeBoxed(object.getClass());
    }

    public static Type[] getFFITypesFromObjects(Object... objects) {
        Type[] result = new Type[objects.length];
        for (int i = 0; i < objects.length; i ++) {
            result[i] = getFFITypeFromObject(objects[i]);
        }
        return result;
    }

    public static Type[] getFFITypesFromObjects(Object[] objects, int offset, int length) {
        Type[] result = new Type[length];
        for (int i = 0; i < length; i ++) {
            result[i] = getFFITypeFromObject(objects[offset + i]);
        }
        return result;
    }

}
