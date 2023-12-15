package unrefined.internal;

import unrefined.util.Copyable;
import unrefined.util.NotInstantiableError;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Objects;

public final class ArrayUtils {

    private ArrayUtils() {
        throw new NotInstantiableError(ArrayUtils.class);
    }

    public static int hashCode(byte[] a, int off, int len) {
        if (a == null) return 0;

        int result = 1;
        for (int i = 0; i < len; i ++) {
            result = 31 * result + a[off + i];
        }

        return result;
    }

    public static int hashCode(short[] a, int off, int len) {
        if (a == null) return 0;

        int result = 1;
        for (int i = 0; i < len; i ++) {
            result = 31 * result + a[off + i];
        }

        return result;
    }

    public static int hashCode(int[] a, int off, int len) {
        if (a == null) return 0;

        int result = 1;
        for (int i = 0; i < len; i ++) {
            result = 31 * result + a[off + i];
        }

        return result;
    }

    public static int hashCode(long[] a, int off, int len) {
        if (a == null) return 0;

        int result = 1;
        long element;
        for (int i = 0; i < len; i ++) {
            element = a[off + i];
            int elementHash = (int)(element ^ (element >>> 32));
            result = 31 * result + elementHash;
        }

        return result;
    }

    public static int hashCode(float[] a, int off, int len) {
        if (a == null) return 0;

        int result = 1;
        for (int i = 0; i < len; i ++) {
            result = 31 * result + Float.floatToIntBits(a[off + i]);
        }

        return result;
    }

    public static int hashCode(double[] a, int off, int len) {
        if (a == null) return 0;

        int result = 1;
        for (int i = 0; i < len; i ++) {
            long bits = Double.doubleToLongBits(a[off + i]);
            result = 31 * result + (int)(bits ^ (bits >>> 32));
        }

        return result;
    }

    public static int hashCode(boolean[] a, int off, int len) {
        if (a == null) return 0;

        int result = 1;
        for (int i = 0; i < len; i ++) {
            result = 31 * result + (a[off + i] ? 1231 : 1237);
        }

        return result;
    }

    public static int hashCode(char[] a, int off, int len) {
        if (a == null) return 0;

        int result = 1;
        for (int i = 0; i < len; i ++) {
            result = 31 * result + a[off + i];
        }

        return result;
    }

    public static int hashCode(Object[] a, int off, int len) {
        if (a == null) return 0;

        int result = 1;
        Object element;
        for (int i = 0; i < len; i ++) {
            element = a[off + i];
            result = 31 * result + (element == null ? 0 : element.hashCode());
        }

        return result;
    }

    public static int deepHashCode(Object[] a, int off, int len) {
        if (a == null)
            return 0;

        int result = 1;

        Object element;
        for (int i = 0; i < len; i ++) {
            element = a[off + i];
            final int elementHash;
            final Class<?> cl;
            if (element == null)
                elementHash = 0;
            else if ((cl = element.getClass().getComponentType()) == null)
                elementHash = element.hashCode();
            else if (element instanceof Object[])
                elementHash = deepHashCode((Object[]) element, off, len);
            else
                elementHash = primitiveArrayHashCode(element, off, len, cl);

            result = 31 * result + elementHash;
        }

        return result;
    }

    private static int primitiveArrayHashCode(Object a, int off, int len, Class<?> cl) {
        return
                (cl == byte.class)    ? hashCode((byte[]) a, off, len)    :
                (cl == int.class)     ? hashCode((int[]) a, off, len)     :
                (cl == long.class)    ? hashCode((long[]) a, off, len)    :
                (cl == char.class)    ? hashCode((char[]) a, off, len)    :
                (cl == short.class)   ? hashCode((short[]) a, off, len)   :
                (cl == boolean.class) ? hashCode((boolean[]) a, off, len) :
                (cl == double.class)  ? hashCode((double[]) a, off, len)  :
                // If new primitive types are ever added, this method must be
               // expanded or we will fail here with ClassCastException.
                hashCode((float[]) a, off, len);
    }

    public static boolean contains(byte[] array, byte element) {
        if (array == null) return false;
        for (byte b : array) {
            if (b == element) return true;
        }
        return false;
    }

    public static boolean contains(short[] array, short element) {
        if (array == null) return false;
        for (short s : array) {
            if (s == element) return true;
        }
        return false;
    }

    public static boolean contains(int[] array, int element) {
        if (array == null) return false;
        for (int i : array) {
            if (i == element) return true;
        }
        return false;
    }

    public static boolean contains(long[] array, long element) {
        if (array == null) return false;
        for (long l : array) {
            if (l == element) return true;
        }
        return false;
    }

    public static boolean contains(float[] array, float element) {
        if (array == null) return false;
        for (float f : array) {
            if (f == element) return true;
        }
        return false;
    }

    public static boolean contains(double[] array, double element) {
        if (array == null) return false;
        for (double d : array) {
            if (d == element) return true;
        }
        return false;
    }

    public static boolean contains(boolean[] array, boolean element) {
        if (array == null) return false;
        for (boolean b : array) {
            if (b == element) return true;
        }
        return false;
    }

    public static boolean contains(char[] array, char element) {
        if (array == null) return false;
        for (char c : array) {
            if (c == element) return true;
        }
        return false;
    }

    public static<T> boolean contains(T[] array, T element) {
        if (array == null) return false;
        for (T t : array) {
            if (Objects.equals(t, element)) return true;
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

    public static float[] copyOf(double[] original) {
        float[] copy = new float[original.length];
        for (int i = 0; i < copy.length; i ++) {
            copy[i] = (float) original[i];
        }
        return copy;
    }

    public static double[] copyOf(float[] original) {
        double[] copy = new double[original.length];
        for (int i = 0; i < copy.length; i ++) {
            copy[i] = original[i];
        }
        return copy;
    }

    public static int[] copyOf(long[] original) {
        int[] copy = new int[original.length];
        for (int i = 0; i < copy.length; i ++) {
            copy[i] = (int) original[i];
        }
        return copy;
    }

    public static long[] copyOf(int[] original) {
        long[] copy = new long[original.length];
        for (int i = 0; i < copy.length; i ++) {
            copy[i] = original[i];
        }
        return copy;
    }

    public static float[] copyOf(double[] original, int newLength) {
        float[] copy = new float[newLength];
        for (int i = 0, len = Math.min(original.length, newLength); i < len; i ++) {
            copy[i] = (float) original[i];
        }
        return copy;
    }

    public static double[] copyOf(float[] original, int newLength) {
        double[] copy = new double[newLength];
        for (int i = 0, len = Math.min(original.length, newLength); i < len; i ++) {
            copy[i] = original[i];
        }
        return copy;
    }

    public static int[] copyOf(long[] original, int newLength) {
        int[] copy = new int[newLength];
        for (int i = 0, len = Math.min(original.length, newLength); i < len; i ++) {
            copy[i] = (int) original[i];
        }
        return copy;
    }

    public static long[] copyOf(int[] original, int newLength) {
        long[] copy = new long[newLength];
        for (int i = 0, len = Math.min(original.length, newLength); i < len; i ++) {
            copy[i] = original[i];
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    public static<T extends Copyable> T[] deepCopyOf(T[] original, int newLength) {
        if (original == null) return null;
        T[] copy = (T[]) Array.newInstance(original.getClass(), newLength);
        for (int i = 0, len = Math.min(original.length, newLength); i < len; i ++) {
            copy[i] = (T) original[i].clone();
        }
        return copy;
    }

    public static float[] copyOfRange(double[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new ArrayIndexOutOfBoundsException(from + " > " + to);
        float[] copy = new float[newLength];
        for (int i = 0, len = Math.min(original.length - from, newLength); i < len; i ++) {
            copy[i] = (float) original[from + i];
        }
        return copy;
    }

    public static double[] copyOfRange(float[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new ArrayIndexOutOfBoundsException(from + " > " + to);
        double[] copy = new double[newLength];
        for (int i = 0, len = Math.min(original.length - from, newLength); i < len; i ++) {
            copy[i] = original[from + i];
        }
        return copy;
    }

    public static int[] copyOfRange(long[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new ArrayIndexOutOfBoundsException(from + " > " + to);
        int[] copy = new int[newLength];
        for (int i = 0, len = Math.min(original.length - from, newLength); i < len; i ++) {
            copy[i] = (int) original[from + i];
        }
        return copy;
    }

    public static long[] copyOfRange(int[] original, int from, int to) {
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
    public static<T extends Copyable> T[] deepCopyOfRange(T[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new ArrayIndexOutOfBoundsException(from + " > " + to);
        if (original == null) return null;
        T[] copy = (T[]) Array.newInstance(original.getClass(), newLength);
        for (int i = 0, len = Math.min(original.length - from, newLength); i < len; i ++) {
            copy[i] = (T) original[from + i].clone();
        }
        return copy;
    }

    @SafeVarargs
    public static<T> T[] collect(T... elements) {
        return elements;
    }

    public static boolean[] collect(boolean... elements) {
        return elements;
    }

    public static byte[] collect(byte... elements) {
        return elements;
    }

    public static char[] collect(char... elements) {
        return elements;
    }

    public static short[] collect(short... elements) {
        return elements;
    }

    public static int[] collect(int... elements) {
        return elements;
    }

    public static long[] collect(long... elements) {
        return elements;
    }

    public static float[] collect(float... elements) {
        return elements;
    }

    public static double[] collect(double... elements) {
        return elements;
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

    public static int binarySearchUnchecked(long[] a, int fromIndex, int toIndex, long key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            long midVal = a[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static int binarySearchUnchecked(int[] a, int fromIndex, int toIndex, int key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static int binarySearchUnchecked(short[] a, int fromIndex, int toIndex, short key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            short midVal = a[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static int binarySearchUnchecked(char[] a, int fromIndex, int toIndex, char key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            char midVal = a[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static int binarySearchUnchecked(byte[] a, int fromIndex, int toIndex, byte key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            byte midVal = a[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static int binarySearchUnchecked(double[] a, int fromIndex, int toIndex, double key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            double midVal = a[mid];

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

    public static int binarySearchUnchecked(float[] a, int fromIndex, int toIndex, float key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            float midVal = a[mid];

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

    public static int binarySearchUnchecked(Object[] a, int fromIndex, int toIndex, Object key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            @SuppressWarnings("rawtypes")
            Comparable midVal = (Comparable)a[mid];
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

    public static <T> int binarySearchUnchecked(T[] a, int fromIndex, int toIndex, T key, Comparator<? super T> c) {
        if (c == null) {
            return binarySearchUnchecked(a, fromIndex, toIndex, key);
        }
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = a[mid];
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

    public static int binarySearchUnchecked(long[] a, long key) {
        return binarySearchUnchecked(a, 0, a.length, key);
    }

    public static int binarySearchUnchecked(int[] a, int key) {
        return binarySearchUnchecked(a, 0, a.length, key);
    }

    public static int binarySearchUnchecked(short[] a, short key) {
        return binarySearchUnchecked(a, 0, a.length, key);
    }

    public static int binarySearchUnchecked(char[] a, char key) {
        return binarySearchUnchecked(a, 0, a.length, key);
    }

    public static int binarySearchUnchecked(byte[] a, byte key) {
        return binarySearchUnchecked(a, 0, a.length, key);
    }

    public static int binarySearchUnchecked(double[] a, double key) {
        return binarySearchUnchecked(a, 0, a.length, key);
    }

    public static int binarySearchUnchecked(float[] a, float key) {
        return binarySearchUnchecked(a, 0, a.length, key);
    }

    public static int binarySearchUnchecked(Object[] a, Object key) {
        return binarySearchUnchecked(a, 0, a.length, key);
    }

    public static <T> int binarySearchUnchecked(T[] a, T key, Comparator<? super T> c) {
        return binarySearchUnchecked(a, 0, a.length, key, c);
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

}
