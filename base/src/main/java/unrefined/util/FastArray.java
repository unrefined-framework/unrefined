package unrefined.util;

import unrefined.math.FastMath;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class FastArray {

    private FastArray() {
        throw new NotInstantiableError(FastArray.class);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> List<T> asList(T... array) {
        return Arrays.asList(array);
    }
    
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> List<T> toList(T... array) {
        return Arrays.asList(array.clone());
    }
    
    public static List<Boolean> toBoxedList(boolean... array) {
        return Arrays.asList(box(array));
    }

    public static List<Byte> toBoxedList(byte... array) {
        return Arrays.asList(box(array));
    }

    public static List<Character> toBoxedList(char... array) {
        return Arrays.asList(box(array));
    }

    public static List<Short> toBoxedList(short... array) {
        return Arrays.asList(box(array));
    }

    public static List<Integer> toBoxedList(int... array) {
        return Arrays.asList(box(array));
    }

    public static List<Long> toBoxedList(long... array) {
        return Arrays.asList(box(array));
    }

    public static List<Float> toBoxedList(float... array) {
        return Arrays.asList(box(array));
    }

    public static List<Double> toBoxedList(double... array) {
        return Arrays.asList(box(array));
    }

    public static Class<?> getComponentType(Class<?> arrayType) {
        if (!arrayType.isArray()) throw new IllegalArgumentException("not an array class");
        return arrayType.getComponentType();
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T[]> getArrayType(Class<T> componentType) {
        ClassLoader classLoader = componentType.getClassLoader();
        String name;
        if (componentType.isArray()) {
            name = "[" + componentType.getName();
        } else if (componentType == boolean.class) {
            name = "[Z";
        } else if (componentType == byte.class) {
            name = "[B";
        } else if (componentType == char.class) {
            name = "[C";
        } else if (componentType == short.class) {
            name = "[S";
        } else if (componentType == int.class) {
            name = "[I";
        } else if (componentType == long.class) {
            name = "[J";
        } else if (componentType == float.class) {
            name = "[F";
        } else if (componentType == double.class) {
            name = "[D";
        } else if (componentType == void.class) {
            throw new IllegalArgumentException("Illegal component type: " + componentType);
        } else {
            name = "[L" + componentType.getName() + ";";
        }
        try {
            return (Class<T[]>) (classLoader == null ? Class.forName(name) : classLoader.loadClass(name));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Illegal component type: " + componentType);
        }
    }

    public static int hashCode(byte[] array) {
        return Arrays.hashCode(array);
    }

    public static int hashCode(boolean[] array) {
        return Arrays.hashCode(array);
    }

    public static int hashCode(char[] array) {
        return Arrays.hashCode(array);
    }

    public static int hashCode(short[] array) {
        return Arrays.hashCode(array);
    }

    public static int hashCode(int[] array) {
        return Arrays.hashCode(array);
    }

    public static int hashCode(long[] array) {
        return Arrays.hashCode(array);
    }

    public static int hashCode(float[] array) {
        return Arrays.hashCode(array);
    }

    public static int hashCode(double[] array) {
        return Arrays.hashCode(array);
    }

    public static int hashCode(Object[] array) {
        return Arrays.hashCode(array);
    }

    public static int deepHashCode(Object[] array) {
        return Arrays.deepHashCode(array);
    }

    public static int hashCode(byte[] array, int offset, int length) {
        if (array == null) return 0;

        int result = 1;
        for (int i = 0; i < length; i ++) {
            result = 31 * result + array[offset + i];
        }

        return result;
    }

    public static int hashCode(short[] array, int offset, int length) {
        if (array == null) return 0;

        int result = 1;
        for (int i = 0; i < length; i ++) {
            result = 31 * result + array[offset + i];
        }

        return result;
    }

    public static int hashCode(int[] array, int offset, int length) {
        if (array == null) return 0;

        int result = 1;
        for (int i = 0; i < length; i ++) {
            result = 31 * result + array[offset + i];
        }

        return result;
    }

    public static int hashCode(long[] array, int offset, int length) {
        if (array == null) return 0;

        int result = 1;
        long element;
        for (int i = 0; i < length; i ++) {
            element = array[offset + i];
            int elementHash = (int)(element ^ (element >>> 32));
            result = 31 * result + elementHash;
        }

        return result;
    }

    public static int hashCode(float[] array, int offset, int length) {
        if (array == null) return 0;

        int result = 1;
        for (int i = 0; i < length; i ++) {
            result = 31 * result + Float.floatToIntBits(array[offset + i]);
        }

        return result;
    }

    public static int hashCode(double[] array, int offset, int length) {
        if (array == null) return 0;

        int result = 1;
        for (int i = 0; i < length; i ++) {
            long bits = Double.doubleToLongBits(array[offset + i]);
            result = 31 * result + (int)(bits ^ (bits >>> 32));
        }

        return result;
    }

    public static int hashCode(boolean[] array, int offset, int length) {
        if (array == null) return 0;

        int result = 1;
        for (int i = 0; i < length; i ++) {
            result = 31 * result + (array[offset + i] ? 1231 : 1237);
        }

        return result;
    }

    public static int hashCode(char[] array, int offset, int length) {
        if (array == null) return 0;

        int result = 1;
        for (int i = 0; i < length; i ++) {
            result = 31 * result + array[offset + i];
        }

        return result;
    }

    public static int hashCode(Object[] array, int offset, int length) {
        if (array == null) return 0;

        int result = 1;
        Object element;
        for (int i = 0; i < length; i ++) {
            element = array[offset + i];
            result = 31 * result + (element == null ? 0 : element.hashCode());
        }

        return result;
    }

    public static int deepHashCode(Object[] array, int offset, int length) {
        if (array == null)
            return 0;

        int result = 1;

        Object element;
        for (int i = 0; i < length; i ++) {
            element = array[offset + i];
            final int elementHash;
            final Class<?> cl;
            if (element == null)
                elementHash = 0;
            else if ((cl = element.getClass().getComponentType()) == null)
                elementHash = element.hashCode();
            else if (element instanceof Object[])
                elementHash = deepHashCode((Object[]) element, offset, length);
            else
                elementHash = primitiveArrayHashCode(element, offset, length, cl);

            result = 31 * result + elementHash;
        }

        return result;
    }

    private static int primitiveArrayHashCode(Object array, int offset, int length, Class<?> clazz) {
        return
                (clazz == byte.class)    ? hashCode((byte[]) array, offset, length)    :
                (clazz == int.class)     ? hashCode((int[]) array, offset, length)     :
                (clazz == long.class)    ? hashCode((long[]) array, offset, length)    :
                (clazz == char.class)    ? hashCode((char[]) array, offset, length)    :
                (clazz == short.class)   ? hashCode((short[]) array, offset, length)   :
                (clazz == boolean.class) ? hashCode((boolean[]) array, offset, length) :
                (clazz == double.class)  ? hashCode((double[]) array, offset, length)  :
                // If new primitive types are ever added, this method must be
               // expanded or we will fail here with ClassCastException.
                hashCode((float[]) array, offset, length);
    }

    public static boolean contains(byte[] array, byte value) {
        if (array == null) return false;
        for (byte b : array) {
            if (b == value) return true;
        }
        return false;
    }

    public static boolean contains(short[] array, short value) {
        if (array == null) return false;
        for (short s : array) {
            if (s == value) return true;
        }
        return false;
    }

    public static boolean contains(int[] array, int value) {
        if (array == null) return false;
        for (int i : array) {
            if (i == value) return true;
        }
        return false;
    }

    public static boolean contains(long[] array, long value) {
        if (array == null) return false;
        for (long l : array) {
            if (l == value) return true;
        }
        return false;
    }

    public static boolean contains(float[] array, float value) {
        if (array == null) return false;
        for (float f : array) {
            if (f == value) return true;
        }
        return false;
    }

    public static boolean contains(double[] array, double value) {
        if (array == null) return false;
        for (double d : array) {
            if (d == value) return true;
        }
        return false;
    }

    public static boolean contains(boolean[] array, boolean value) {
        if (array == null) return false;
        for (boolean b : array) {
            if (b == value) return true;
        }
        return false;
    }

    public static boolean contains(char[] array, char value) {
        if (array == null) return false;
        for (char c : array) {
            if (c == value) return true;
        }
        return false;
    }

    public static <T> boolean contains(T[] array, T value) {
        if (array == null) return false;
        for (T t : array) {
            if (Objects.equals(t, value)) return true;
        }
        return false;
    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Object[] array) {
        if (array == null) return true;
        for (Object element : array) {
            if (element != null) return false;
        }
        return true;
    }

    public static boolean deepIsEmpty(Object[] array) {
        if (array == null) return true;
        boolean result = true;
        for (Object element : array) {
            if (element instanceof byte[]) {
                result = isEmpty((byte[]) element);
            }
            else if (element instanceof short[]) {
                result = isEmpty((short[]) element);
            }
            else if (element instanceof int[]) {
                result = isEmpty((int[]) element);
            }
            else if (element instanceof long[]) {
                result = isEmpty((long[]) element);
            }
            else if (element instanceof float[]) {
                result = isEmpty((float[]) element);
            }
            else if (element instanceof double[]) {
                result = isEmpty((double[]) element);
            }
            else if (element instanceof boolean[]) {
                result = isEmpty((boolean[]) element);
            }
            else if (element instanceof char[]) {
                result = isEmpty((char[]) element);
            }
            else if (element instanceof Object[]) {
                result = deepIsEmpty((Object[]) element);
            }
            if (!result) break;
        }
        return result;
    }

    public static boolean[] copyOf(boolean[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    public static byte[] copyOf(byte[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    public static char[] copyOf(char[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    public static short[] copyOf(short[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    public static int[] copyOf(int[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    public static long[] copyOf(long[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    public static float[] copyOf(float[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    public static double[] copyOf(double[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    public static <T> T[] copyOf(T[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }

    public static <T, U> T[] copyOfArrayType(U[] original, int newLength, Class<? extends T[]> newType) {
        return Arrays.copyOf(original, newLength, newType);
    }

    public static <T, U> T[] copyOfComponentType(U[] original, int newLength, Class<T> newType) {
        return Arrays.copyOf(original, newLength, getArrayType(newType));
    }

    public static boolean[] copyOfRange(boolean[] original, int from, int to) {
        return Arrays.copyOfRange(original, from, to);
    }

    public static byte[] copyOfRange(byte[] original, int from, int to) {
        return Arrays.copyOfRange(original, from, to);
    }

    public static char[] copyOfRange(char[] original, int from, int to) {
        return Arrays.copyOfRange(original, from, to);
    }

    public static short[] copyOfRange(short[] original, int from, int to) {
        return Arrays.copyOfRange(original, from, to);
    }

    public static int[] copyOfRange(int[] original, int from, int to) {
        return Arrays.copyOfRange(original, from, to);
    }

    public static long[] copyOfRange(long[] original, int from, int to) {
        return Arrays.copyOfRange(original, from, to);
    }

    public static float[] copyOfRange(float[] original, int from, int to) {
        return Arrays.copyOfRange(original, from, to);
    }

    public static double[] copyOfRange(double[] original, int from, int to) {
        return Arrays.copyOfRange(original, from, to);
    }

    public static <T> T[] copyOfRange(T[] original, int from, int to) {
        return Arrays.copyOfRange(original, from, to);
    }

    public static <T, U> T[] copyOfRangeArrayType(U[] original, int from, int to, Class<? extends T[]> newType) {
        return Arrays.copyOfRange(original, from, to, newType);
    }

    public static <T, U> T[] copyOfRangeComponentType(U[] original, int from, int to, Class<T> newType) {
        return Arrays.copyOfRange(original, from, to, getArrayType(newType));
    }

    public static float[] copyOfFloat(double[] original) {
        float[] copy = new float[original.length];
        for (int i = 0; i < copy.length; i ++) {
            copy[i] = (float) original[i];
        }
        return copy;
    }

    public static double[] copyOfDouble(float[] original) {
        double[] copy = new double[original.length];
        for (int i = 0; i < copy.length; i ++) {
            copy[i] = original[i];
        }
        return copy;
    }

    public static int[] copyOfInt(long[] original) {
        int[] copy = new int[original.length];
        for (int i = 0; i < copy.length; i ++) {
            copy[i] = (int) original[i];
        }
        return copy;
    }

    public static long[] copyOfLong(int[] original) {
        long[] copy = new long[original.length];
        for (int i = 0; i < copy.length; i ++) {
            copy[i] = original[i];
        }
        return copy;
    }

    public static float[] copyOfFloat(double[] original, int newLength) {
        float[] copy = new float[newLength];
        for (int i = 0, len = Math.min(original.length, newLength); i < len; i ++) {
            copy[i] = (float) original[i];
        }
        return copy;
    }

    public static double[] copyOfDouble(float[] original, int newLength) {
        double[] copy = new double[newLength];
        for (int i = 0, len = Math.min(original.length, newLength); i < len; i ++) {
            copy[i] = original[i];
        }
        return copy;
    }

    public static int[] copyOfInt(long[] original, int newLength) {
        int[] copy = new int[newLength];
        for (int i = 0, len = Math.min(original.length, newLength); i < len; i ++) {
            copy[i] = (int) original[i];
        }
        return copy;
    }

    public static long[] copyOfLong(int[] original, int newLength) {
        long[] copy = new long[newLength];
        for (int i = 0, len = Math.min(original.length, newLength); i < len; i ++) {
            copy[i] = original[i];
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    public static<T extends Copyable> T[] deepCopyOf(T[] original, int newLength) {
        T[] copy = (T[]) Array.newInstance(original.getClass().getComponentType(), newLength);
        for (int i = 0, len = Math.min(original.length, newLength); i < len; i ++) {
            copy[i] = (T) original[i].clone();
        }
        return copy;
    }

    public static float[] copyOfRangeFloat(double[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new ArrayIndexOutOfBoundsException(from + " > " + to);
        float[] copy = new float[newLength];
        for (int i = 0, len = Math.min(original.length - from, newLength); i < len; i ++) {
            copy[i] = (float) original[from + i];
        }
        return copy;
    }

    public static double[] copyOfRangeDouble(float[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new ArrayIndexOutOfBoundsException(from + " > " + to);
        double[] copy = new double[newLength];
        for (int i = 0, len = Math.min(original.length - from, newLength); i < len; i ++) {
            copy[i] = original[from + i];
        }
        return copy;
    }

    public static int[] copyOfRangeInt(long[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new ArrayIndexOutOfBoundsException(from + " > " + to);
        int[] copy = new int[newLength];
        for (int i = 0, len = Math.min(original.length - from, newLength); i < len; i ++) {
            copy[i] = (int) original[from + i];
        }
        return copy;
    }

    public static long[] copyOfRangeLong(int[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new ArrayIndexOutOfBoundsException(from + " > " + to);
        long[] copy = new long[newLength];
        for (int i = 0, len = Math.min(original.length - from, newLength); i < len; i ++) {
            copy[i] = original[from + i];
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Copyable> T[] deepCopyOfRange(T[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new ArrayIndexOutOfBoundsException(from + " > " + to);
        if (original == null) return null;
        T[] copy = (T[]) Array.newInstance(original.getClass().getComponentType(), newLength);
        for (int i = 0, len = Math.min(original.length - from, newLength); i < len; i ++) {
            copy[i] = (T) original[from + i].clone();
        }
        return copy;
    }

    @SafeVarargs
    public static <T> T[] copyOf(T... original) {
        return original.clone();
    }
    
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T extends Copyable> T[] deepCopyOf(T... original) {
        T[] copy = (T[]) Array.newInstance(original.getClass().getComponentType());
        for (int i = 0, len = original.length; i < len; i ++) {
            copy[i] = (T) original[i].clone();
        }
        return copy;
    }

    public static boolean[] copyOf(boolean... original) {
        return original.clone();
    }

    public static byte[] copyOf(byte... original) {
        return original.clone();
    }

    public static char[] copyOf(char... original) {
        return original.clone();
    }

    public static short[] copyOf(short... original) {
        return original.clone();
    }

    public static int[] copyOf(int... original) {
        return original.clone();
    }

    public static long[] copyOf(long... original) {
        return original.clone();
    }

    public static float[] copyOf(float... original) {
        return original.clone();
    }

    public static double[] copyOf(double... original) {
        return original.clone();
    }

    public static Boolean[] box(boolean[] array) {
        if (array == null) return null;
        Boolean[] boxed = new Boolean[array.length];
        for (int i = 0; i < array.length; i ++) {
            boxed[i] = array[i];
        }
        return boxed;
    }

    public static Byte[] box(byte[] array) {
        if (array == null) return null;
        Byte[] boxed = new Byte[array.length];
        for (int i = 0; i < array.length; i ++) {
            boxed[i] = array[i];
        }
        return boxed;
    }

    public static Character[] box(char[] array) {
        if (array == null) return null;
        Character[] boxed = new Character[array.length];
        for (int i = 0; i < array.length; i ++) {
            boxed[i] = array[i];
        }
        return boxed;
    }

    public static Short[] box(short[] array) {
        if (array == null) return null;
        Short[] boxed = new Short[array.length];
        for (int i = 0; i < array.length; i ++) {
            boxed[i] = array[i];
        }
        return boxed;
    }

    public static Integer[] box(int[] array) {
        if (array == null) return null;
        Integer[] boxed = new Integer[array.length];
        for (int i = 0; i < array.length; i ++) {
            boxed[i] = array[i];
        }
        return boxed;
    }

    public static Long[] box(long[] array) {
        if (array == null) return null;
        Long[] boxed = new Long[array.length];
        for (int i = 0; i < array.length; i ++) {
            boxed[i] = array[i];
        }
        return boxed;
    }

    public static Float[] box(float[] array) {
        if (array == null) return null;
        Float[] boxed = new Float[array.length];
        for (int i = 0; i < array.length; i ++) {
            boxed[i] = array[i];
        }
        return boxed;
    }

    public static Double[] box(double[] array) {
        if (array == null) return null;
        Double[] boxed = new Double[array.length];
        for (int i = 0; i < array.length; i ++) {
            boxed[i] = array[i];
        }
        return boxed;
    }

    public static boolean[] unbox(Boolean[] boxed) {
        if (boxed == null) return null;
        boolean[] unboxed = new boolean[boxed.length];
        for (int i = 0; i < boxed.length; i ++) {
            unboxed[i] = boxed[i];
        }
        return unboxed;
    }

    public static byte[] unbox(Byte[] boxed) {
        if (boxed == null) return null;
        byte[] unboxed = new byte[boxed.length];
        for (int i = 0; i < boxed.length; i ++) {
            unboxed[i] = boxed[i];
        }
        return unboxed;
    }

    public static char[] unbox(Character[] boxed) {
        if (boxed == null) return null;
        char[] unboxed = new char[boxed.length];
        for (int i = 0; i < boxed.length; i ++) {
            unboxed[i] = boxed[i];
        }
        return unboxed;
    }

    public static short[] unbox(Short[] boxed) {
        if (boxed == null) return null;
        short[] unboxed = new short[boxed.length];
        for (int i = 0; i < boxed.length; i ++) {
            unboxed[i] = boxed[i];
        }
        return unboxed;
    }

    public static int[] unbox(Integer[] boxed) {
        if (boxed == null) return null;
        int[] unboxed = new int[boxed.length];
        for (int i = 0; i < boxed.length; i ++) {
            unboxed[i] = boxed[i];
        }
        return unboxed;
    }

    public static long[] unbox(Long[] boxed) {
        if (boxed == null) return null;
        long[] unboxed = new long[boxed.length];
        for (int i = 0; i < boxed.length; i ++) {
            unboxed[i] = boxed[i];
        }
        return unboxed;
    }

    public static float[] unbox(Float[] boxed) {
        if (boxed == null) return null;
        float[] unboxed = new float[boxed.length];
        for (int i = 0; i < boxed.length; i ++) {
            unboxed[i] = boxed[i];
        }
        return unboxed;
    }

    public static double[] unbox(Double[] boxed) {
        if (boxed == null) return null;
        double[] unboxed = new double[boxed.length];
        for (int i = 0; i < boxed.length; i ++) {
            unboxed[i] = boxed[i];
        }
        return unboxed;
    }

    public static int sparseLongArraySize(int need) {
        return sparseByteArraySize(need * 8) / 8;
    }

    public static int sparseIntArraySize(int need) {
        return sparseByteArraySize(need * 4) / 4;
    }

    public static int sparseShortArraySize(int need) {
        return sparseByteArraySize(need * 2) / 2;
    }

    public static int sparseByteArraySize(int need) {
        for (int i = 4; i < 32; i ++) {
            if (need <= (1 << i) - 12) return (1 << i) - 12;
        }
        return need;
    }

    public static int binarySearch(byte[] array, int fromIndex, int toIndex, byte key) {
        return Arrays.binarySearch(array, fromIndex, toIndex, key);
    }

    public static int binarySearch(boolean[] array, int fromIndex, int toIndex, boolean key) {
        rangeCheck(array, fromIndex, toIndex);
        return binarySearchUnchecked(array, fromIndex, toIndex, key);
    }

    public static int binarySearch(char[] array, int fromIndex, int toIndex, char key) {
        return Arrays.binarySearch(array, fromIndex, toIndex, key);
    }

    public static int binarySearch(short[] array, int fromIndex, int toIndex, short key) {
        return Arrays.binarySearch(array, fromIndex, toIndex, key);
    }

    public static int binarySearch(int[] array, int fromIndex, int toIndex, int key) {
        return Arrays.binarySearch(array, fromIndex, toIndex, key);
    }

    public static int binarySearch(long[] array, int fromIndex, int toIndex, long key) {
        return Arrays.binarySearch(array, fromIndex, toIndex, key);
    }

    public static int binarySearch(float[] array, int fromIndex, int toIndex, float key) {
        return Arrays.binarySearch(array, fromIndex, toIndex, key);
    }

    public static int binarySearch(double[] array, int fromIndex, int toIndex, double key) {
        return Arrays.binarySearch(array, fromIndex, toIndex, key);
    }

    public static int binarySearch(Object[] array, int fromIndex, int toIndex, Object key) {
        return Arrays.binarySearch(array, fromIndex, toIndex, key);
    }

    public static <T> int binarySearch(T[] array, int fromIndex, int toIndex, T key, Comparator<? super T> comparator) {
        return Arrays.binarySearch(array, fromIndex, toIndex, key, comparator);
    }

    public static int binarySearch(byte[] array, byte key) {
        return Arrays.binarySearch(array, key);
    }

    public static int binarySearch(boolean[] array, boolean key) {
        return binarySearchUnchecked(array, 0, array.length, key);
    }

    public static int binarySearch(char[] array, char key) {
        return Arrays.binarySearch(array, key);
    }

    public static int binarySearch(short[] array, short key) {
        return Arrays.binarySearch(array, key);
    }

    public static int binarySearch(int[] array, int key) {
        return Arrays.binarySearch(array, key);
    }

    public static int binarySearch(long[] array, long key) {
        return Arrays.binarySearch(array, key);
    }

    public static int binarySearch(float[] array, float key) {
        return Arrays.binarySearch(array, key);
    }

    public static int binarySearch(double[] array, double key) {
        return Arrays.binarySearch(array, key);
    }

    public static int binarySearch(Object[] array, Object key) {
        return Arrays.binarySearch(array, key);
    }

    public static <T> int binarySearch(T[] array, T key, Comparator<? super T> comparator) {
        return Arrays.binarySearch(array, key, comparator);
    }

    public static int binarySearchUnchecked(long[] array, int fromIndex, int toIndex, long key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            long midVal = array[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static int binarySearchUnchecked(int[] array, int fromIndex, int toIndex, int key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = array[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static int binarySearchUnchecked(short[] array, int fromIndex, int toIndex, short key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            short midVal = array[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static int binarySearchUnchecked(char[] array, int fromIndex, int toIndex, char key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            char midVal = array[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static int binarySearchUnchecked(byte[] array, int fromIndex, int toIndex, byte key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            byte midVal = array[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static int binarySearchUnchecked(boolean[] array, int fromIndex, int toIndex, boolean key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            boolean midVal = array[mid];

            if (!midVal && key)
                low = mid + 1;
            else if (midVal && !key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static int binarySearchUnchecked(double[] array, int fromIndex, int toIndex, double key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            double midVal = array[mid];

            if (midVal < key)
                low = mid + 1;  // Neither val is NaN, thisVal is smaller
            else if (midVal > key)
                high = mid - 1; // Neither val is NaN, thisVal is larger
            else {
                long midBits = Double.doubleToLongBits(midVal);
                long keyBits = Double.doubleToLongBits(key);
                if (midBits == keyBits)     // Values are equal
                    return mid;             // Key found
                else if (midBits < keyBits) // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                else                        // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
            }
        }
        return -(low + 1);  // key not found.
    }

    public static int binarySearchUnchecked(float[] array, int fromIndex, int toIndex, float key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            float midVal = array[mid];

            if (midVal < key)
                low = mid + 1;  // Neither val is NaN, thisVal is smaller
            else if (midVal > key)
                high = mid - 1; // Neither val is NaN, thisVal is larger
            else {
                int midBits = Float.floatToIntBits(midVal);
                int keyBits = Float.floatToIntBits(key);
                if (midBits == keyBits)     // Values are equal
                    return mid;             // Key found
                else if (midBits < keyBits) // (-0.0, 0.0) or (!NaN, NaN)
                    low = mid + 1;
                else                        // (0.0, -0.0) or (NaN, !NaN)
                    high = mid - 1;
            }
        }
        return -(low + 1);  // key not found.
    }

    public static int binarySearchUnchecked(Object[] array, int fromIndex, int toIndex, Object key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            @SuppressWarnings("rawtypes")
            Comparable midVal = (Comparable)array[mid];
            @SuppressWarnings("unchecked")
            int cmp = midVal.compareTo(key);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static <T> int binarySearchUnchecked(T[] array, int fromIndex, int toIndex, T key, Comparator<? super T> c) {
        if (c == null) {
            return binarySearchUnchecked(array, fromIndex, toIndex, key);
        }
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = array[mid];
            int cmp = c.compare(midVal, key);
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static void rangeCheck(boolean[] array, int fromIndex, int toIndex) {
        rangeCheck(array.length, fromIndex, toIndex);
    }

    public static void rangeCheck(byte[] array, int fromIndex, int toIndex) {
        rangeCheck(array.length, fromIndex, toIndex);
    }

    public static void rangeCheck(char[] array, int fromIndex, int toIndex) {
        rangeCheck(array.length, fromIndex, toIndex);
    }

    public static void rangeCheck(short[] array, int fromIndex, int toIndex) {
        rangeCheck(array.length, fromIndex, toIndex);
    }

    public static void rangeCheck(int[] array, int fromIndex, int toIndex) {
        rangeCheck(array.length, fromIndex, toIndex);
    }

    public static void rangeCheck(long[] array, int fromIndex, int toIndex) {
        rangeCheck(array.length, fromIndex, toIndex);
    }

    public static void rangeCheck(float[] array, int fromIndex, int toIndex) {
        rangeCheck(array.length, fromIndex, toIndex);
    }

    public static void rangeCheck(double[] array, int fromIndex, int toIndex) {
        rangeCheck(array.length, fromIndex, toIndex);
    }

    public static <T> void rangeCheck(T[] array, int fromIndex, int toIndex) {
        rangeCheck(array.length, fromIndex, toIndex);
    }

    public static void rangeCheck(int arrayLength, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > arrayLength) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
    }

    public static boolean equals(byte[] a, byte[] b) {
        return Arrays.equals(a, b);
    }

    public static boolean equals(boolean[] a, boolean[] b) {
        return Arrays.equals(a, b);
    }

    public static boolean equals(char[] a, char[] b) {
        return Arrays.equals(a, b);
    }

    public static boolean equals(short[] a, short[] b) {
        return Arrays.equals(a, b);
    }

    public static boolean equals(int[] a, int[] b) {
        return Arrays.equals(a, b);
    }

    public static boolean equals(long[] a, long[] b) {
        return Arrays.equals(a, b);
    }

    public static boolean equals(float[] a, float[] b) {
        return Arrays.equals(a, b);
    }

    public static boolean equals(double[] a, double[] b) {
        return Arrays.equals(a, b);
    }

    public static boolean equals(Object[] a, Object[] b) {
        return Arrays.equals(a, b);
    }

    public static <T> boolean equals(T[] a, T[] b, Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        if (a==b)
            return true;
        if (a==null || b==null)
            return false;

        int length = a.length;
        if (b.length != length)
            return false;

        for (int i=0; i<length; i++) {
            if (comparator.compare(a[i], b[i]) != 0)
                return false;
        }

        return true;
    }

    public static boolean deepEquals(Object[] a, Object[] b) {
        return Arrays.deepEquals(a, b);
    }

    public static boolean equals(byte[] a, int aFromIndex, int aToIndex,
                                 byte[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if (aLength != bLength)
            return false;

        for (int i = 0; i < aLength; i ++) {
            if (a[aFromIndex + i] != b[bFromIndex + i]) return false;
        }

        return true;
    }

    public static boolean equals(boolean[] a, int aFromIndex, int aToIndex,
                                 boolean[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if (aLength != bLength)
            return false;

        for (int i = 0; i < aLength; i ++) {
            if (a[aFromIndex + i] != b[bFromIndex + i]) return false;
        }

        return true;
    }

    public static boolean equals(char[] a, int aFromIndex, int aToIndex,
                                 char[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if (aLength != bLength)
            return false;

        for (int i = 0; i < aLength; i ++) {
            if (a[aFromIndex + i] != b[bFromIndex + i]) return false;
        }

        return true;
    }

    public static boolean equals(short[] a, int aFromIndex, int aToIndex,
                                 short[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if (aLength != bLength)
            return false;

        for (int i = 0; i < aLength; i ++) {
            if (a[aFromIndex + i] != b[bFromIndex + i]) return false;
        }

        return true;
    }

    public static boolean equals(int[] a, int aFromIndex, int aToIndex,
                                 int[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if (aLength != bLength)
            return false;

        for (int i = 0; i < aLength; i ++) {
            if (a[aFromIndex + i] != b[bFromIndex + i]) return false;
        }

        return true;
    }

    public static boolean equals(long[] a, int aFromIndex, int aToIndex,
                                 long[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if (aLength != bLength)
            return false;

        for (int i = 0; i < aLength; i ++) {
            if (a[aFromIndex + i] != b[bFromIndex + i]) return false;
        }

        return true;
    }

    public static boolean equals(float[] a, int aFromIndex, int aToIndex,
                                 float[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if (aLength != bLength)
            return false;

        for (int i = 0; i < aLength; i ++) {
            if (Float.compare(a[aFromIndex + i], b[bFromIndex + i]) != 0) return false;
        }

        return true;
    }

    public static boolean equals(double[] a, int aFromIndex, int aToIndex,
                                 double[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if (aLength != bLength)
            return false;

        for (int i = 0; i < aLength; i ++) {
            if (Double.compare(a[aFromIndex + i], b[bFromIndex + i]) != 0) return false;
        }

        return true;
    }

    public static boolean equals(Object[] a, int aFromIndex, int aToIndex,
                                 Object[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if (aLength != bLength)
            return false;

        for (int i = 0; i < aLength; i++) {
            if (!Objects.equals(a[aFromIndex++], b[bFromIndex++]))
                return false;
        }

        return true;
    }

    public static boolean deepEquals(Object[] a, int aFromIndex, int aToIndex,
                                     Object[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if (aLength != bLength)
            return false;

        for (int i = 0; i < aLength; i++) {
            Object e1 = a[aFromIndex + i];
            Object e2 = b[bFromIndex + i];

            if (e1 == e2)
                continue;
            if (e1 == null)
                return false;

            // Figure out whether the two elements are equal
            boolean eq = deepEquals0(e1, e2);

            if (!eq)
                return false;
        }
        return true;
    }

    private static boolean deepEquals0(Object e1, Object e2) {
        boolean eq;
        if (e1 instanceof Object[] && e2 instanceof Object[])
            eq = deepEquals ((Object[]) e1, (Object[]) e2);
        else if (e1 instanceof byte[] && e2 instanceof byte[])
            eq = equals((byte[]) e1, (byte[]) e2);
        else if (e1 instanceof short[] && e2 instanceof short[])
            eq = equals((short[]) e1, (short[]) e2);
        else if (e1 instanceof int[] && e2 instanceof int[])
            eq = equals((int[]) e1, (int[]) e2);
        else if (e1 instanceof long[] && e2 instanceof long[])
            eq = equals((long[]) e1, (long[]) e2);
        else if (e1 instanceof char[] && e2 instanceof char[])
            eq = equals((char[]) e1, (char[]) e2);
        else if (e1 instanceof float[] && e2 instanceof float[])
            eq = equals((float[]) e1, (float[]) e2);
        else if (e1 instanceof double[] && e2 instanceof double[])
            eq = equals((double[]) e1, (double[]) e2);
        else if (e1 instanceof boolean[] && e2 instanceof boolean[])
            eq = equals((boolean[]) e1, (boolean[]) e2);
        else
            eq = e1.equals(e2);
        return eq;
    }

    public static <T> boolean equals(T[] a, int aFromIndex, int aToIndex,
                                     T[] b, int bFromIndex, int bToIndex,
                                     Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if (aLength != bLength)
            return false;

        for (int i = 0; i < aLength; i++) {
            if (comparator.compare(a[aFromIndex++], b[bFromIndex++]) != 0)
                return false;
        }

        return true;
    }

    public static boolean equals(byte[] a, int aOffset, byte[] b, int bOffset, int length) {
        return equals(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static boolean equals(boolean[] a, int aOffset, boolean[] b, int bOffset, int length) {
        return equals(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static boolean equals(short[] a, int aOffset, short[] b, int bOffset, int length) {
        return equals(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static boolean equals(char[] a, int aOffset, char[] b, int bOffset, int length) {
        return equals(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static boolean equals(int[] a, int aOffset, int[] b, int bOffset, int length) {
        return equals(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static boolean equals(long[] a, int aOffset, long[] b, int bOffset, int length) {
        return equals(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static boolean equals(float[] a, int aOffset, float[] b, int bOffset, int length) {
        return equals(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static boolean equals(double[] a, int aOffset, double[] b, int bOffset, int length) {
        return equals(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static boolean equals(Object[] a, int aOffset, Object[] b, int bOffset, int length) {
        return equals(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static boolean deepEquals(Object[] a, int aOffset, Object[] b, int bOffset, int length) {
        return deepEquals(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static void fill(byte[] array, byte value) {
        Arrays.fill(array, value);
    }

    public static void fill(boolean[] array, boolean value) {
        Arrays.fill(array, value);
    }

    public static void fill(char[] array, char value) {
        Arrays.fill(array, value);
    }

    public static void fill(short[] array, short value) {
        Arrays.fill(array, value);
    }

    public static void fill(int[] array, int value) {
        Arrays.fill(array, value);
    }

    public static void fill(long[] array, long value) {
        Arrays.fill(array, value);
    }

    public static void fill(float[] array, float value) {
        Arrays.fill(array, value);
    }

    public static void fill(double[] array, double value) {
        Arrays.fill(array, value);
    }

    public static void fill(Object[] array, Object value) {
        Arrays.fill(array, value);
    }

    public static void fill(byte[] array, int fromIndex, int toIndex, byte value) {
        Arrays.fill(array, fromIndex, toIndex, value);
    }

    public static void fill(boolean[] array, int fromIndex, int toIndex, boolean value) {
        Arrays.fill(array, fromIndex, toIndex, value);
    }

    public static void fill(char[] array, int fromIndex, int toIndex, char value) {
        Arrays.fill(array, fromIndex, toIndex, value);
    }

    public static void fill(short[] array, int fromIndex, int toIndex, short value) {
        Arrays.fill(array, fromIndex, toIndex, value);
    }

    public static void fill(int[] array, int fromIndex, int toIndex, int value) {
        Arrays.fill(array, fromIndex, toIndex, value);
    }

    public static void fill(long[] array, int fromIndex, int toIndex, long value) {
        Arrays.fill(array, fromIndex, toIndex, value);
    }

    public static void fill(float[] array, int fromIndex, int toIndex, float value) {
        Arrays.fill(array, fromIndex, toIndex, value);
    }

    public static void fill(double[] array, int fromIndex, int toIndex, double value) {
        Arrays.fill(array, fromIndex, toIndex, value);
    }

    public static void fill(Object[] array, int fromIndex, int toIndex, Object value) {
        Arrays.fill(array, fromIndex, toIndex, value);
    }

    public static byte[] sort(byte[] array) {
        Arrays.sort(array);
        return array;
    }

    public static boolean[] sort(boolean[] array) {
        sort(array, 0, array.length);
        return array;
    }

    public static char[] sort(char[] array) {
        Arrays.sort(array);
        return array;
    }

    public static short[] sort(short[] array) {
        Arrays.sort(array);
        return array;
    }

    public static int[] sort(int[] array) {
        Arrays.sort(array);
        return array;
    }

    public static long[] sort(long[] array) {
        Arrays.sort(array);
        return array;
    }

    public static float[] sort(float[] array) {
        Arrays.sort(array);
        return array;
    }

    public static double[] sort(double[] array) {
        Arrays.sort(array);
        return array;
    }

    public static <T/* extends Comparable<? super T>*/> T[] sort(T[] array) {
        Arrays.sort(array);
        return array;
    }

    public static <T> T[] sort(T[] array, Comparator<? super T> comparator) {
        Arrays.sort(array, comparator);
        return array;
    }

    public static byte[] sort(byte[] array, int fromIndex, int toIndex) {
        Arrays.sort(array, fromIndex, toIndex);
        return array;
    }

    public static boolean[] sort(boolean[] array, int fromIndex, int toIndex) {
        for (int i, k = fromIndex; ++k < toIndex; ) {
            boolean arrayi = array[i = k];

            if (!arrayi && array[i - 1]) {
                while (--i >= fromIndex && array[i]) {
                    array[i + 1] = array[i];
                }
                array[i + 1] = arrayi;
            }
        }
        return array;
    }

    public static char[] sort(char[] array, int fromIndex, int toIndex) {
        Arrays.sort(array, fromIndex, toIndex);
        return array;
    }

    public static short[] sort(short[] array, int fromIndex, int toIndex) {
        Arrays.sort(array, fromIndex, toIndex);
        return array;
    }

    public static int[] sort(int[] array, int fromIndex, int toIndex) {
        Arrays.sort(array, fromIndex, toIndex);
        return array;
    }

    public static long[] sort(long[] array, int fromIndex, int toIndex) {
        Arrays.sort(array, fromIndex, toIndex);
        return array;
    }

    public static float[] sort(float[] array, int fromIndex, int toIndex) {
        Arrays.sort(array, fromIndex, toIndex);
        return array;
    }

    public static double[] sort(double[] array, int fromIndex, int toIndex) {
        Arrays.sort(array, fromIndex, toIndex);
        return array;
    }

    public static <T/* extends Comparable<? super T>*/> T[] sort(T[] array, int fromIndex, int toIndex) {
        Arrays.sort(array, fromIndex, toIndex);
        return array;
    }

    public static <T> T[] sort(T[] array, int fromIndex, int toIndex, Comparator<? super T> comparator) {
        Arrays.sort(array, fromIndex, toIndex, comparator);
        return array;
    }

    public static String toString(byte[] array) {
        return Arrays.toString(array);
    }

    public static String toString(boolean[] array) {
        return Arrays.toString(array);
    }

    public static String toString(char[] array) {
        return Arrays.toString(array);
    }

    public static String toString(short[] array) {
        return Arrays.toString(array);
    }

    public static String toString(int[] array) {
        return Arrays.toString(array);
    }

    public static String toString(long[] array) {
        return Arrays.toString(array);
    }

    public static String toString(float[] array) {
        return Arrays.toString(array);
    }

    public static String toString(double[] array) {
        return Arrays.toString(array);
    }

    public static String toString(Object[] array) {
        return Arrays.toString(array);
    }

    public static String deepToString(Object[] array) {
        return Arrays.deepToString(array);
    }

    public static int compare(byte[] a, byte[] b) {
        if (a == b)
            return 0;
        // A null array is less than a non-null array
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            byte oa = a[i];
            byte ob = b[i];
            if (oa != ob) {
                return oa - ob;
            }
        }

        return a.length - b.length;
    }

    public static int compare(boolean[] a, boolean[] b) {
        if (a == b)
            return 0;
        // A null array is less than a non-null array
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            boolean oa = a[i];
            boolean ob = b[i];
            if (oa != ob) {
                return oa ? 1 : -1;
            }
        }

        return a.length - b.length;
    }

    public static int compare(char[] a, char[] b) {
        if (a == b)
            return 0;
        // A null array is less than a non-null array
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            char oa = a[i];
            char ob = b[i];
            if (oa != ob) {
                return oa - ob;
            }
        }

        return a.length - b.length;
    }

    public static int compare(short[] a, short[] b) {
        if (a == b)
            return 0;
        // A null array is less than a non-null array
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            short oa = a[i];
            short ob = b[i];
            if (oa != ob) {
                return oa - ob;
            }
        }

        return a.length - b.length;
    }

    public static int compare(int[] a, int[] b) {
        if (a == b)
            return 0;
        // A null array is less than a non-null array
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            int oa = a[i];
            int ob = b[i];
            if (oa != ob) {
                return oa < ob ? -1 : 1;
            }
        }

        return a.length - b.length;
    }

    public static int compare(long[] a, long[] b) {
        if (a == b)
            return 0;
        // A null array is less than a non-null array
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            long oa = a[i];
            long ob = b[i];
            if (oa != ob) {
                return oa < ob ? -1 : 1;
            }
        }

        return a.length - b.length;
    }

    public static int compare(float[] a, float[] b) {
        if (a == b)
            return 0;
        // A null array is less than a non-null array
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            float oa = a[i];
            float ob = b[i];
            int compared = Float.compare(oa, ob);
            if (compared != 0) {
                return compared;
            }
        }

        return a.length - b.length;
    }

    public static int compare(double[] a, double[] b) {
        if (a == b)
            return 0;
        // A null array is less than a non-null array
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            double oa = a[i];
            double ob = b[i];
            int compared = Double.compare(oa, ob);
            if (compared != 0) {
                return compared;
            }
        }

        return a.length - b.length;
    }

    @SuppressWarnings("unchecked")
    public static <T/* extends Comparable<? super T>*/> int compare(T[] a, T[] b) {
        if (a == b)
            return 0;
        // A null array is less than a non-null array
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            T oa = a[i];
            T ob = b[i];
            if (oa != ob) {
                // A null element is less than a non-null element
                if (oa == null || ob == null)
                    return oa == null ? -1 : 1;
                int v = ((Comparable<? super T>) oa).compareTo(ob);
                if (v != 0) {
                    return v;
                }
            }
        }

        return a.length - b.length;
    }

    public static <T> int compare(T[] a, T[] b,
                                  Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        if (a == b)
            return 0;
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            T oa = a[i];
            T ob = b[i];
            if (oa != ob) {
                // Null-value comparison is deferred to the comparator
                int v = comparator.compare(oa, ob);
                if (v != 0) {
                    return v;
                }
            }
        }

        return a.length - b.length;
    }

    public static int compare(
            boolean[] a, int aFromIndex, int aToIndex,
            boolean[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            boolean oa = a[aFromIndex++];
            boolean ob = b[bFromIndex++];
            if (oa != ob) {
                return oa ? 1 : -1;
            }
        }

        return aLength - bLength;
    }

    public static int compare(
            byte[] a, int aFromIndex, int aToIndex,
            byte[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            byte oa = a[aFromIndex++];
            byte ob = b[bFromIndex++];
            if (oa != ob) {
                return oa - ob;
            }
        }

        return aLength - bLength;
    }

    public static int compare(
            char[] a, int aFromIndex, int aToIndex,
            char[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            char oa = a[aFromIndex++];
            char ob = b[bFromIndex++];
            if (oa != ob) {
                return oa - ob;
            }
        }

        return aLength - bLength;
    }

    public static int compare(
            short[] a, int aFromIndex, int aToIndex,
            short[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            short oa = a[aFromIndex++];
            short ob = b[bFromIndex++];
            if (oa != ob) {
                return oa - ob;
            }
        }

        return aLength - bLength;
    }

    public static int compare(
            int[] a, int aFromIndex, int aToIndex,
            int[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            int oa = a[aFromIndex++];
            int ob = b[bFromIndex++];
            if (oa != ob) {
                return oa < ob ? -1 : 1;
            }
        }

        return aLength - bLength;
    }

    public static int compare(
            long[] a, int aFromIndex, int aToIndex,
            long[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            long oa = a[aFromIndex++];
            long ob = b[bFromIndex++];
            if (oa != ob) {
                return oa < ob ? -1 : 1;
            }
        }

        return aLength - bLength;
    }

    public static int compare(
            float[] a, int aFromIndex, int aToIndex,
            float[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            float oa = a[aFromIndex++];
            float ob = b[bFromIndex++];
            int compared = Float.compare(oa, ob);
            if (compared != 0) {
                return compared;
            }
        }

        return aLength - bLength;
    }

    public static int compare(
            double[] a, int aFromIndex, int aToIndex,
            double[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            double oa = a[aFromIndex++];
            double ob = b[bFromIndex++];
            int compared = Double.compare(oa, ob);
            if (compared != 0) {
                return compared;
            }
        }

        return aLength - bLength;
    }

    @SuppressWarnings("unchecked")
    public static <T/* extends Comparable<? super T>*/> int compare(
            T[] a, int aFromIndex, int aToIndex,
            T[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            T oa = a[aFromIndex++];
            T ob = b[bFromIndex++];
            if (oa != ob) {
                if (oa == null || ob == null)
                    return oa == null ? -1 : 1;
                int v = ((Comparable<? super T>) oa).compareTo(ob);
                if (v != 0) {
                    return v;
                }
            }
        }

        return aLength - bLength;
    }

    public static <T> int compare(
            T[] a, int aFromIndex, int aToIndex,
            T[] b, int bFromIndex, int bToIndex,
            Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            T oa = a[aFromIndex++];
            T ob = b[bFromIndex++];
            if (oa != ob) {
                // Null-value comparison is deferred to the comparator
                int v = comparator.compare(oa, ob);
                if (v != 0) {
                    return v;
                }
            }
        }

        return aLength - bLength;
    }

    public static int compare(boolean[] a, int aOffset, boolean[] b, int bOffset, int length) {
        return compare(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int compare(byte[] a, int aOffset, byte[] b, int bOffset, int length) {
        return compare(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int compare(char[] a, int aOffset, char[] b, int bOffset, int length) {
        return compare(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int compare(short[] a, int aOffset, short[] b, int bOffset, int length) {
        return compare(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int compare(int[] a, int aOffset, int[] b, int bOffset, int length) {
        return compare(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int compare(long[] a, int aOffset, long[] b, int bOffset, int length) {
        return compare(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int compare(float[] a, int aOffset, float[] b, int bOffset, int length) {
        return compare(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int compare(double[] a, int aOffset, double[] b, int bOffset, int length) {
        return compare(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static <T/* extends Comparable<? super T>*/> int compare(T[] a, int aOffset, T[] b, int bOffset, int length) {
        return compare(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static <T> int compare(T[] a, int aOffset, T[] b, int bOffset, int length, Comparator<? super T> comparator) {
        return compare(a, aOffset, aOffset + length, b, bOffset, bOffset + length, comparator);
    }

    public static int compareUnsigned(byte[] a, byte[] b) {
        if (a == b)
            return 0;
        // A null array is less than a non-null array
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            byte oa = a[i];
            byte ob = b[i];
            int compared = FastMath.compareUnsigned(oa, ob);
            if (compared != 0) {
                return compared;
            }
        }

        return a.length - b.length;
    }

    public static int compareUnsigned(short[] a, short[] b) {
        if (a == b)
            return 0;
        // A null array is less than a non-null array
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            short oa = a[i];
            short ob = b[i];
            int compared = FastMath.compareUnsigned(oa, ob);
            if (compared != 0) {
                return compared;
            }
        }

        return a.length - b.length;
    }

    public static int compareUnsigned(int[] a, int[] b) {
        if (a == b)
            return 0;
        // A null array is less than a non-null array
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            int oa = a[i];
            int ob = b[i];
            int compared = FastMath.compareUnsigned(oa, ob);
            if (compared != 0) {
                return compared;
            }
        }

        return a.length - b.length;
    }

    public static int compareUnsigned(long[] a, long[] b) {
        if (a == b)
            return 0;
        // A null array is less than a non-null array
        if (a == null || b == null)
            return a == null ? -1 : 1;

        int length = Math.min(a.length, b.length);
        for (int i = 0; i < length; i++) {
            long oa = a[i];
            long ob = b[i];
            int compared = FastMath.compareUnsigned(oa, ob);
            if (compared != 0) {
                return compared;
            }
        }

        return a.length - b.length;
    }

    public static int compareUnsigned(
            byte[] a, int aFromIndex, int aToIndex,
            byte[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            byte oa = a[aFromIndex++];
            byte ob = b[bFromIndex++];
            int compared = FastMath.compareUnsigned(oa, ob);
            if (compared != 0) {
                return compared;
            }
        }

        return aLength - bLength;
    }

    public static int compareUnsigned(
            short[] a, int aFromIndex, int aToIndex,
            short[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            short oa = a[aFromIndex++];
            short ob = b[bFromIndex++];
            int compared = FastMath.compareUnsigned(oa, ob);
            if (compared != 0) {
                return compared;
            }
        }

        return aLength - bLength;
    }

    public static int compareUnsigned(
            int[] a, int aFromIndex, int aToIndex,
            int[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            int oa = a[aFromIndex++];
            int ob = b[bFromIndex++];
            int compared = FastMath.compareUnsigned(oa, ob);
            if (compared != 0) {
                return compared;
            }
        }

        return aLength - bLength;
    }

    public static int compareUnsigned(
            long[] a, int aFromIndex, int aToIndex,
            long[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            long oa = a[aFromIndex++];
            long ob = b[bFromIndex++];
            int compared = FastMath.compareUnsigned(oa, ob);
            if (compared != 0) {
                return compared;
            }
        }

        return aLength - bLength;
    }

    public static int compareUnsigned(byte[] a, int aOffset, byte[] b, int bOffset, int length) {
        return compareUnsigned(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int compareUnsigned(short[] a, int aOffset, short[] b, int bOffset, int length) {
        return compareUnsigned(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int compareUnsigned(int[] a, int aOffset, int[] b, int bOffset, int length) {
        return compareUnsigned(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int compareUnsigned(long[] a, int aOffset, long[] b, int bOffset, int length) {
        return compareUnsigned(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int mismatch(boolean[] a, boolean[] b) {
        int length = Math.min(a.length, b.length); // Check null array refs
        if (a == b)
            return -1;

        for (int i = 0; i < length; i++) {
            if (a[i] != b[i])
                return i;
        }

        return a.length != b.length ? length : -1;
    }

    public static int mismatch(byte[] a, byte[] b) {
        int length = Math.min(a.length, b.length); // Check null array refs
        if (a == b)
            return -1;

        for (int i = 0; i < length; i++) {
            if (a[i] != b[i])
                return i;
        }

        return a.length != b.length ? length : -1;
    }

    public static int mismatch(char[] a, char[] b) {
        int length = Math.min(a.length, b.length); // Check null array refs
        if (a == b)
            return -1;

        for (int i = 0; i < length; i++) {
            if (a[i] != b[i])
                return i;
        }

        return a.length != b.length ? length : -1;
    }

    public static int mismatch(short[] a, short[] b) {
        int length = Math.min(a.length, b.length); // Check null array refs
        if (a == b)
            return -1;

        for (int i = 0; i < length; i++) {
            if (a[i] != b[i])
                return i;
        }

        return a.length != b.length ? length : -1;
    }

    public static int mismatch(int[] a, int[] b) {
        int length = Math.min(a.length, b.length); // Check null array refs
        if (a == b)
            return -1;

        for (int i = 0; i < length; i++) {
            if (a[i] != b[i])
                return i;
        }

        return a.length != b.length ? length : -1;
    }

    public static int mismatch(long[] a, long[] b) {
        int length = Math.min(a.length, b.length); // Check null array refs
        if (a == b)
            return -1;

        for (int i = 0; i < length; i++) {
            if (a[i] != b[i])
                return i;
        }

        return a.length != b.length ? length : -1;
    }

    public static int mismatch(float[] a, float[] b) {
        int length = Math.min(a.length, b.length); // Check null array refs
        if (a == b)
            return -1;

        for (int i = 0; i < length; i++) {
            if (Float.compare(a[i], b[i]) != 0)
                return i;
        }

        return a.length != b.length ? length : -1;
    }

    public static int mismatch(double[] a, double[] b) {
        int length = Math.min(a.length, b.length); // Check null array refs
        if (a == b)
            return -1;

        for (int i = 0; i < length; i++) {
            if (Double.compare(a[i], b[i]) != 0)
                return i;
        }

        return a.length != b.length ? length : -1;
    }

    public static int mismatch(Object[] a, Object[] b) {
        int length = Math.min(a.length, b.length); // Check null array refs
        if (a == b)
            return -1;

        for (int i = 0; i < length; i++) {
            if (!Objects.equals(a[i], b[i]))
                return i;
        }

        return a.length != b.length ? length : -1;
    }

    public static <T> int mismatch(T[] a, T[] b, Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        int length = Math.min(a.length, b.length); // Check null array refs
        if (a == b)
            return -1;

        for (int i = 0; i < length; i++) {
            T oa = a[i];
            T ob = b[i];
            if (oa != ob) {
                // Null-value comparison is deferred to the comparator
                int v = comparator.compare(oa, ob);
                if (v != 0) {
                    return i;
                }
            }
        }

        return a.length != b.length ? length : -1;
    }

    public static int mismatch(
            long[] a, int aFromIndex, int aToIndex,
            long[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex++] != b[bFromIndex++])
                return i;
        }

        return aLength != bLength ? length : -1;
    }

    public static int mismatch(
            boolean[] a, int aFromIndex, int aToIndex,
            boolean[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex++] != b[bFromIndex++])
                return i;
        }

        return aLength != bLength ? length : -1;
    }

    public static int mismatch(
            byte[] a, int aFromIndex, int aToIndex,
            byte[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex++] != b[bFromIndex++])
                return i;
        }

        return aLength != bLength ? length : -1;
    }

    public static int mismatch(
            char[] a, int aFromIndex, int aToIndex,
            char[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex++] != b[bFromIndex++])
                return i;
        }

        return aLength != bLength ? length : -1;
    }

    public static int mismatch(
            short[] a, int aFromIndex, int aToIndex,
            short[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex++] != b[bFromIndex++])
                return i;
        }

        return aLength != bLength ? length : -1;
    }

    public static int mismatch(
            int[] a, int aFromIndex, int aToIndex,
            int[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex++] != b[bFromIndex++])
                return i;
        }

        return aLength != bLength ? length : -1;
    }

    public static int mismatch(
            float[] a, int aFromIndex, int aToIndex,
            float[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            if (Float.compare(a[aFromIndex++], b[bFromIndex++]) != 0)
                return i;
        }

        return aLength != bLength ? length : -1;
    }

    public static int mismatch(
            double[] a, int aFromIndex, int aToIndex,
            double[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            if (Double.compare(a[aFromIndex++], b[bFromIndex++]) != 0)
                return i;
        }

        return aLength != bLength ? length : -1;
    }

    public static int mismatch(
            Object[] a, int aFromIndex, int aToIndex,
            Object[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            if (!Objects.equals(a[aFromIndex++], b[bFromIndex++]))
                return i;
        }

        return aLength != bLength ? length : -1;
    }

    public static <T> int mismatch(
            T[] a, int aFromIndex, int aToIndex,
            T[] b, int bFromIndex, int bToIndex,
            Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);

        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for (int i = 0; i < length; i++) {
            T oa = a[aFromIndex++];
            T ob = b[bFromIndex++];
            if (oa != ob) {
                // Null-value comparison is deferred to the comparator
                int v = comparator.compare(oa, ob);
                if (v != 0) {
                    return i;
                }
            }
        }

        return aLength != bLength ? length : -1;
    }

    public static int mismatch(boolean[] a, int aOffset, boolean[] b, int bOffset, int length) {
        return mismatch(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int mismatch(byte[] a, int aOffset, byte[] b, int bOffset, int length) {
        return mismatch(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int mismatch(char[] a, int aOffset, char[] b, int bOffset, int length) {
        return mismatch(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int mismatch(short[] a, int aOffset, short[] b, int bOffset, int length) {
        return mismatch(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int mismatch(int[] a, int aOffset, int[] b, int bOffset, int length) {
        return mismatch(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int mismatch(long[] a, int aOffset, long[] b, int bOffset, int length) {
        return mismatch(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int mismatch(float[] a, int aOffset, float[] b, int bOffset, int length) {
        return mismatch(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int mismatch(double[] a, int aOffset, double[] b, int bOffset, int length) {
        return mismatch(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static int mismatch(Object[] a, int aOffset, Object[] b, int bOffset, int length) {
        return mismatch(a, aOffset, aOffset + length, b, bOffset, bOffset + length);
    }

    public static <T> int mismatch(T[] a, int aOffset, T[] b, int bOffset, int length, Comparator<? super T> comparator) {
        return mismatch(a, aOffset, aOffset + length, b, bOffset, bOffset + length, comparator);
    }

}
