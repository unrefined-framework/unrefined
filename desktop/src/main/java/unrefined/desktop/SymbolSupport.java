package unrefined.desktop;

import com.kenai.jffi.CallContext;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Library;
import com.kenai.jffi.Type;
import unrefined.nio.Pointer;
import unrefined.util.NotInstantiableError;
import unrefined.util.StringCompat;
import unrefined.util.UnexpectedError;
import unrefined.util.foreign.Aggregate;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;

public class SymbolSupport {

    private SymbolSupport() {
        throw new NotInstantiableError(SymbolSupport.class);
    }

    public static long find(Library library, String name) {
        if (StringCompat.isBlank(Objects.requireNonNull(name))) throw new IllegalArgumentException("blank symbol name");
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
        if (StringCompat.isBlank(Objects.requireNonNull(name))) throw new IllegalArgumentException("blank symbol name");
        try {
            return ReflectionSupport.invokeLongMethod(null, findNativeMethod, classLoader, name);
        } catch (InvocationTargetException e) {
            throw new UnexpectedError(e);
        }
    }

    public static long find(String name) {
        if (StringCompat.isBlank(Objects.requireNonNull(name))) throw new IllegalArgumentException("blank symbol name");
        return find(ReflectionSupport.getCallerClass().getClassLoader(), name);
    }

    @SuppressWarnings("unchecked")
    public static Type toFFIType(Class<?> clazz) {
        if (clazz == void.class) return Type.VOID;
        else if (clazz == boolean.class) return Type.UINT8;
        else if (clazz == byte.class) return Type.SINT8;
        else if (clazz == char.class) return Type.UINT16;
        else if (clazz == short.class) return Type.SINT16;
        else if (clazz == int.class) return Type.SINT32;
        else if (clazz == long.class) return Type.SINT64;
        else if (clazz == float.class) return Type.FLOAT;
        else if (clazz == double.class) return Type.DOUBLE;
        else if (Aggregate.class.isAssignableFrom(clazz)) return AggregateSupport.typeOf((Class<? extends Aggregate>) clazz);
        else {
            Objects.requireNonNull(clazz);
            throw new IllegalArgumentException("Illegal type class: " + clazz);
        }
    }

    @SuppressWarnings("unchecked")
    public static Type toFFITypeBoxed(Class<?> clazz) {
        if (clazz == Boolean.class) return Type.UINT8;
        else if (clazz == Byte.class) return Type.SINT8;
        else if (clazz == Character.class) return Type.UINT16;
        else if (clazz == Short.class) return Type.SINT16;
        else if (clazz == Integer.class) return Type.SINT32;
        else if (clazz == Long.class) return Type.SINT64;
        else if (clazz == Float.class) return Type.FLOAT;
        else if (clazz == Double.class) return Type.DOUBLE;
        else if (Aggregate.class.isAssignableFrom(clazz)) return AggregateSupport.typeOf((Class<? extends Aggregate>) clazz);
        else {
            Objects.requireNonNull(clazz);
            throw new IllegalArgumentException("Illegal type class: " + clazz);
        }
    }

    public static Type[] toFFITypes(Class<?>... classes) {
        Type[] types = new Type[classes.length];
        for (int i = 0; i < classes.length; i ++) {
            types[i] = toFFIType(classes[i]);
        }
        return types;
    }

    public static Type[] toFFITypes(Class<?>[] classes, int offset, int length) {
        Type[] types = new Type[length];
        for (int i = 0; i < length; i ++) {
            types[i] = toFFIType(classes[offset + i]);
        }
        return types;
    }

    public static Type[] toFFITypes(Object... args) {
        Type[] types = new Type[args.length];
        for (int i = 0; i < args.length; i ++) {
            types[i] = toFFITypeBoxed(args[i].getClass());
        }
        return types;
    }

    public static Type[] toFFITypes(Object[] args, int offset, int length) {
        Type[] types = new Type[length];
        for (int i = 0; i < args.length; i ++) {
            types[i] = toFFITypeBoxed(args[offset + i].getClass());
        }
        return types;
    }

    public static Type[] getVariadicFFITypes(Object... args) {
        return expandVariadicFFITypes(toFFITypes(args, 0, args.length - 1), args[args.length - 1]);
    }

    public static Type[] getFFITypes(Object array) {
        Class<?> clazz = array.getClass();
        if (clazz.isArray()) {
            Type[] types = new Type[Array.getLength(array)];
            clazz = clazz.getComponentType();
            if (clazz == boolean.class) {
                Arrays.fill(types, Type.UINT8);
                return types;
            }
            else if (clazz == byte.class) {
                Arrays.fill(types, Type.SINT8);
                return types;
            }
            else if (clazz == char.class) {
                Arrays.fill(types, Type.UINT16);
                return types;
            }
            else if (clazz == short.class) {
                Arrays.fill(types, Type.SINT16);
                return types;
            }
            else if (clazz == int.class) {
                Arrays.fill(types, Type.SINT32);
                return types;
            }
            else if (clazz == long.class) {
                Arrays.fill(types, Type.SINT64);
                return types;
            }
            else if (clazz == float.class) {
                Arrays.fill(types, Type.FLOAT);
                return types;
            }
            else if (clazz == double.class) {
                Arrays.fill(types, Type.DOUBLE);
                return types;
            }
            else if (clazz == Object.class) {
                for (int i = 0; i < types.length; i ++) {
                    types[i] = toFFITypeBoxed(Array.get(array, i).getClass());
                }
                return types;
            }
        }
        throw new IllegalArgumentException("Illegal type class: " + clazz);
    }

    @SuppressWarnings("unchecked")
    public static void pushArgument(HeapInvocationBuffer heapInvocationBuffer, Class<?> marker, Object arg) {
        if (marker == boolean.class) heapInvocationBuffer.putByte((Boolean) arg ? 1 : 0);
        else if (marker == byte.class) heapInvocationBuffer.putByte(((Number) arg).byteValue());
        else if (marker == char.class) heapInvocationBuffer.putShort((Character) arg);
        else if (marker == short.class) heapInvocationBuffer.putShort(((Number) arg).shortValue());
        else if (marker == int.class) heapInvocationBuffer.putInt(((Number) arg).intValue());
        else if (marker == long.class) heapInvocationBuffer.putLong(((Number) arg).longValue());
        else if (marker == float.class) heapInvocationBuffer.putFloat(((Number) arg).floatValue());
        else if (marker == double.class) heapInvocationBuffer.putDouble(((Number) arg).doubleValue());
        else if (Aggregate.class.isAssignableFrom(marker)) {
            Pointer memory = ((Aggregate) arg).memory();
            if (memory.isDirect()) heapInvocationBuffer.putStruct(memory.address());
            else {
                byte[] struct = new byte[(int) Aggregate.sizeOfType((Class<? extends Aggregate>) marker)];
                memory.getByteArray(0, struct);
                heapInvocationBuffer.putStruct(reverseIfNeeded(struct), 0);
            }
        }
        else throw new IllegalArgumentException("Illegal argument type: " + marker);
    }

    private static final boolean LITTLE_ENDIAN = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN);

    private static void pushArgument(HeapInvocationBuffer heapInvocationBuffer, Aggregate arg) {
        Pointer pointer = arg.memory();
        if (pointer.isDirect()) heapInvocationBuffer.putStruct(pointer.address());
        else if (pointer.hasArrays()) {
            if (!LITTLE_ENDIAN) {
                if (pointer.size() == arg.getDescriptor().getSize()) {
                    if (pointer.arraysOffset() % Integer.MAX_VALUE + pointer.size() <= Integer.MAX_VALUE) {
                        heapInvocationBuffer.putStruct(
                                pointer.arrays()[(int) (pointer.arraysOffset() / Integer.MAX_VALUE)],
                                (int) (pointer.arraysOffset() % Integer.MAX_VALUE));
                    }
                    else {
                        byte[] array = new byte[(int) arg.getDescriptor().getSize()];
                        pointer.getByteArray(0, array);
                        heapInvocationBuffer.putStruct(array, 0);
                    }
                }
            }
            else {
                byte[] array = new byte[(int) arg.getDescriptor().getSize()];
                pointer.getByteArray(0, array);
                reverse0(array);
                heapInvocationBuffer.putStruct(array, 0);
            }
        }
        else throw new IllegalArgumentException("Illegal pointer; neither direct nor arrays-backed");
    }

    public static byte[] reverseIfNeeded(byte[] array) {
        if (LITTLE_ENDIAN) reverse0(array);
        return array;
    }

    private static void reverse0(byte[] array) {
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
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
        Type[] variadicTypes = getFFITypes(varargs);
        Type[] types = Arrays.copyOf(nonVariadicTypes, nonVariadicTypes.length + variadicTypes.length);
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

    public static HeapInvocationBuffer toHeapInvocationBuffer(CallContext context, Class<?>[] parameterTypes, Object... args) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(context);
        if (args.length > 0) {
            for (int i = 0; i < parameterTypes.length; i ++) {
                Class<?> marker = parameterTypes[i];
                Class<?> boxed = args[i].getClass();
                if (!matchesBoxed(marker, boxed)) throw new IllegalArgumentException("Illegal argument type; expected " + marker);
                pushArgument(heapInvocationBuffer, marker, args[i]);
            }
        }
        return heapInvocationBuffer;
    }

    public static HeapInvocationBuffer toHeapInvocationBufferVariadic(CallContext context, Class<?>[] parameterTypes, Object... args) {
        HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(context);
        if (args.length > 0) {
            int variadic = parameterTypes.length - 1;
            for (int i = 0; i < variadic; i ++) {
                Class<?> marker = parameterTypes[i];
                Class<?> boxed = args[i].getClass();
                if (!matchesBoxed(marker, boxed)) throw new IllegalArgumentException("Illegal argument type; expected " + marker);
                pushArgument(heapInvocationBuffer, marker, args[i]);
            }
            pushVariadicArguments(heapInvocationBuffer, parameterTypes[variadic], args[variadic]);
        }
        return heapInvocationBuffer;
    }

}
