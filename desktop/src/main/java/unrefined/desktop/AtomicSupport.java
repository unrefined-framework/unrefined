package unrefined.desktop;

import unrefined.util.NotInstantiableError;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Objects;

import static java.lang.reflect.Modifier.isStatic;
import static unrefined.desktop.ReflectionSupport.checkObject;
import static unrefined.desktop.UnsafeSupport.UNSAFE;

// FIXME weakCompareAndSet
@SuppressWarnings("BlockedPrivateApi")
public final class AtomicSupport {

    private AtomicSupport() {
        throw new NotInstantiableError(AtomicSupport.class);
    }

    public static void fullFence() {
        UNSAFE.fullFence();
    }

    public static void loadFence() {
        UNSAFE.loadFence();
    }

    public static void storeFence() {
        UNSAFE.storeFence();
    }

    /*
    private static final Method storeStoreFence;
    static {
        Method method;
        try {
            method = Class.forName("java.lang.invoke.VarHandle").getDeclaredMethod("storeStoreFence");
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            method = null;
        }
        storeStoreFence = method;
    }

    public static void loadLoadFence() {
        UNSAFE.loadFence();
    }

    public static void storeStoreFence() {
        if (storeStoreFence == null) UNSAFE.storeFence();
        else {
            try {
                ReflectionSupport.invokeVoidMethod(null, storeStoreFence);
            } catch (InvocationTargetException e) {
                throw new UnexpectedError(e);
            }
        }
    }
     */

    public static Object getObjectFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType().isPrimitive()) throw new IllegalArgumentException("Illegal field type; expected non-primitive");
        return isStatic(field.getModifiers()) ?
                UNSAFE.getObjectVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getObjectVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }

    public static boolean getBooleanFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != boolean.class) throw new IllegalArgumentException("Illegal field type; expected boolean");
        return isStatic(field.getModifiers()) ?
                UNSAFE.getBooleanVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getBooleanVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }

    public static byte getByteFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        return isStatic(field.getModifiers()) ?
                UNSAFE.getByteVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getByteVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }

    public static char getCharFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != char.class) throw new IllegalArgumentException("Illegal field type; expected char");
        return isStatic(field.getModifiers()) ?
                UNSAFE.getCharVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getCharVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }

    public static short getShortFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        return isStatic(field.getModifiers()) ?
                UNSAFE.getShortVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getShortVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }

    public static int getIntFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        return isStatic(field.getModifiers()) ?
                UNSAFE.getIntVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getIntVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }

    public static long getLongFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        return isStatic(field.getModifiers()) ?
                UNSAFE.getLongVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getLongVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }

    public static float getFloatFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        return isStatic(field.getModifiers()) ?
                UNSAFE.getFloatVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getFloatVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }

    public static double getDoubleFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        return isStatic(field.getModifiers()) ?
                UNSAFE.getDoubleVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getDoubleVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }

    public static Object getAndSetObjectField(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType().isPrimitive()) throw new IllegalArgumentException("Illegal field type; expected non-primitive");
        return isStatic(field.getModifiers()) ?
                UNSAFE.getAndSetObject(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value) :
                UNSAFE.getAndSetObject(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static boolean getAndSetBooleanField(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != boolean.class) throw new IllegalArgumentException("Illegal field type; expected boolean");
        return isStatic(field.getModifiers()) ?
                UnsafeSupport.getAndSetBoolean(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value) :
                UnsafeSupport.getAndSetBoolean(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static byte getAndSetByteField(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        return isStatic(field.getModifiers()) ?
                UnsafeSupport.getAndSetByte(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value) :
                UnsafeSupport.getAndSetByte(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static char getAndSetCharField(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != char.class) throw new IllegalArgumentException("Illegal field type; expected char");
        return isStatic(field.getModifiers()) ?
                UnsafeSupport.getAndSetChar(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value) :
                UnsafeSupport.getAndSetChar(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static short getAndSetShortField(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        return isStatic(field.getModifiers()) ?
                UnsafeSupport.getAndSetShort(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value) :
                UnsafeSupport.getAndSetShort(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static int getAndSetIntField(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        return isStatic(field.getModifiers()) ?
                UNSAFE.getAndSetInt(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value) :
                UNSAFE.getAndSetInt(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static long getAndSetLongField(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        return isStatic(field.getModifiers()) ?
                UNSAFE.getAndSetLong(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value) :
                UNSAFE.getAndSetLong(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static float getAndSetFloatField(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        return isStatic(field.getModifiers()) ?
                UnsafeSupport.getAndSetFloat(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value) :
                UnsafeSupport.getAndSetFloat(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static double getAndSetDoubleField(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        return isStatic(field.getModifiers()) ?
                UnsafeSupport.getAndSetDouble(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value) :
                UnsafeSupport.getAndSetDouble(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void setObjectFieldVolatile(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType().isPrimitive()) throw new IllegalArgumentException("Illegal field type; expected non-primitive");
        if (isStatic(field.getModifiers())) UNSAFE.putObjectVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putObjectVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void setBooleanFieldVolatile(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != boolean.class) throw new IllegalArgumentException("Illegal field type; expected boolean");
        if (isStatic(field.getModifiers())) UNSAFE.putBooleanVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putBooleanVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void setByteFieldVolatile(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        if (isStatic(field.getModifiers())) UNSAFE.putByteVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putByteVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void setCharFieldVolatile(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != char.class) throw new IllegalArgumentException("Illegal field type; expected char");
        if (isStatic(field.getModifiers())) UNSAFE.putCharVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putCharVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void setShortFieldVolatile(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        if (isStatic(field.getModifiers())) UNSAFE.putShortVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putShortVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void setIntFieldVolatile(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        if (isStatic(field.getModifiers())) UNSAFE.putIntVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putIntVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void setLongFieldVolatile(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        if (isStatic(field.getModifiers())) UNSAFE.putLongVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putLongVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void setFloatFieldVolatile(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        if (isStatic(field.getModifiers())) UNSAFE.putFloatVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putFloatVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void setDoubleFieldVolatile(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        if (isStatic(field.getModifiers())) UNSAFE.putDoubleVolatile(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putDoubleVolatile(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetObjectField(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType().isPrimitive()) throw new IllegalArgumentException("Illegal field type; expected non-primitive");
        if (isStatic(field.getModifiers())) UNSAFE.putOrderedObject(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putOrderedObject(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetBooleanField(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != boolean.class) throw new IllegalArgumentException("Illegal field type; expected boolean");
        if (isStatic(field.getModifiers())) UnsafeSupport.putOrderedBoolean(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UnsafeSupport.putOrderedBoolean(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetByteField(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        if (isStatic(field.getModifiers())) UnsafeSupport.putOrderedByte(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UnsafeSupport.putOrderedByte(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetCharField(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != char.class) throw new IllegalArgumentException("Illegal field type; expected char");
        if (isStatic(field.getModifiers())) UnsafeSupport.putOrderedChar(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UnsafeSupport.putOrderedChar(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetShortField(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        if (isStatic(field.getModifiers())) UnsafeSupport.putOrderedShort(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UnsafeSupport.putOrderedShort(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetIntField(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        if (isStatic(field.getModifiers())) UNSAFE.putOrderedInt(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putOrderedInt(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetLongField(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        if (isStatic(field.getModifiers())) UNSAFE.putOrderedLong(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putOrderedLong(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetFloatField(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        if (isStatic(field.getModifiers())) UnsafeSupport.putOrderedFloat(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UnsafeSupport.putOrderedFloat(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetDoubleField(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        if (isStatic(field.getModifiers())) UnsafeSupport.putOrderedDouble(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UnsafeSupport.putOrderedDouble(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static boolean compareAndSetObjectField(Object object, Field field, Object expected, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType().isPrimitive()) throw new IllegalArgumentException("Illegal field type; expected non-primitive");
        if (isStatic(field.getModifiers())) return UNSAFE.compareAndSwapObject(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UNSAFE.compareAndSwapObject(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean compareAndSetBooleanField(Object object, Field field, boolean expected, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != boolean.class) throw new IllegalArgumentException("Illegal field type; expected boolean");
        if (isStatic(field.getModifiers())) return UnsafeSupport.compareAndSwapBoolean(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UnsafeSupport.compareAndSwapBoolean(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean compareAndSetByteField(Object object, Field field, byte expected, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        if (isStatic(field.getModifiers())) return UnsafeSupport.compareAndSwapByte(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UnsafeSupport.compareAndSwapByte(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean compareAndSetCharField(Object object, Field field, char expected, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != char.class) throw new IllegalArgumentException("Illegal field type; expected char");
        if (isStatic(field.getModifiers())) return UnsafeSupport.compareAndSwapChar(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UnsafeSupport.compareAndSwapChar(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean compareAndSetShortField(Object object, Field field, short expected, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        if (isStatic(field.getModifiers())) return UnsafeSupport.compareAndSwapShort(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UnsafeSupport.compareAndSwapShort(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean compareAndSetIntField(Object object, Field field, int expected, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        if (isStatic(field.getModifiers())) return UNSAFE.compareAndSwapInt(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UNSAFE.compareAndSwapInt(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean compareAndSetLongField(Object object, Field field, long expected, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        if (isStatic(field.getModifiers())) return UNSAFE.compareAndSwapLong(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UNSAFE.compareAndSwapLong(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean compareAndSetFloatField(Object object, Field field, float expected, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        if (isStatic(field.getModifiers())) return UnsafeSupport.compareAndSwapFloat(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UnsafeSupport.compareAndSwapFloat(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean compareAndSetDoubleField(Object object, Field field, double expected, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        if (isStatic(field.getModifiers())) return UnsafeSupport.compareAndSwapDouble(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UnsafeSupport.compareAndSwapDouble(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean weakCompareAndSetObjectField(Object object, Field field, Object expected, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType().isPrimitive()) throw new IllegalArgumentException("Illegal field type; expected non-primitive");
        if (isStatic(field.getModifiers())) return UNSAFE.compareAndSwapObject(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UNSAFE.compareAndSwapObject(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean weakCompareAndSetBooleanField(Object object, Field field, boolean expected, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != boolean.class) throw new IllegalArgumentException("Illegal field type; expected boolean");
        if (isStatic(field.getModifiers())) return UnsafeSupport.compareAndSwapBoolean(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UnsafeSupport.compareAndSwapBoolean(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean weakCompareAndSetByteField(Object object, Field field, byte expected, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        if (isStatic(field.getModifiers())) return UnsafeSupport.compareAndSwapByte(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UnsafeSupport.compareAndSwapByte(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean weakCompareAndSetCharField(Object object, Field field, char expected, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != char.class) throw new IllegalArgumentException("Illegal field type; expected char");
        if (isStatic(field.getModifiers())) return UnsafeSupport.compareAndSwapChar(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UnsafeSupport.compareAndSwapChar(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean weakCompareAndSetShortField(Object object, Field field, short expected, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        if (isStatic(field.getModifiers())) return UnsafeSupport.compareAndSwapShort(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UnsafeSupport.compareAndSwapShort(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean weakCompareAndSetIntField(Object object, Field field, int expected, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        if (isStatic(field.getModifiers())) return UNSAFE.compareAndSwapInt(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UNSAFE.compareAndSwapInt(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean weakCompareAndSetLongField(Object object, Field field, long expected, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        if (isStatic(field.getModifiers())) return UNSAFE.compareAndSwapLong(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UNSAFE.compareAndSwapLong(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean weakCompareAndSetFloatField(Object object, Field field, float expected, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        if (isStatic(field.getModifiers())) return UnsafeSupport.compareAndSwapFloat(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UnsafeSupport.compareAndSwapFloat(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static boolean weakCompareAndSetDoubleField(Object object, Field field, double expected, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        if (isStatic(field.getModifiers())) return UnsafeSupport.compareAndSwapDouble(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), expected, value);
        else return UnsafeSupport.compareAndSwapDouble(checkObject(object, field), UNSAFE.objectFieldOffset(field), expected, value);
    }

    public static byte getAndAddByteField(Object object, Field field, byte delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddByte(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), delta);
        else return UnsafeSupport.getAndAddByte(checkObject(object, field), UNSAFE.objectFieldOffset(field), delta);
    }

    public static short getAndAddShortField(Object object, Field field, short delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddShort(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), delta);
        else return UnsafeSupport.getAndAddShort(checkObject(object, field), UNSAFE.objectFieldOffset(field), delta);
    }

    public static int getAndAddIntField(Object object, Field field, int delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        if (isStatic(field.getModifiers())) return UNSAFE.getAndAddInt(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), delta);
        else return UNSAFE.getAndAddInt(checkObject(object, field), UNSAFE.objectFieldOffset(field), delta);
    }

    public static long getAndAddLongField(Object object, Field field, long delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        if (isStatic(field.getModifiers())) return UNSAFE.getAndAddLong(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), delta);
        else return UNSAFE.getAndAddLong(checkObject(object, field), UNSAFE.objectFieldOffset(field), delta);
    }

    public static float getAndAddFloatField(Object object, Field field, float delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddFloat(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), delta);
        else return UnsafeSupport.getAndAddFloat(checkObject(object, field), UNSAFE.objectFieldOffset(field), delta);
    }

    public static double getAndAddDoubleField(Object object, Field field, double delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddDouble(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), delta);
        else return UnsafeSupport.getAndAddDouble(checkObject(object, field), UNSAFE.objectFieldOffset(field), delta);
    }

    public static byte getAndIncrementByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddByte(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), (byte) 1);
        else return UnsafeSupport.getAndAddByte(checkObject(object, field), UNSAFE.objectFieldOffset(field), (byte) 1);
    }

    public static short getAndIncrementShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddShort(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), (short) 1);
        else return UnsafeSupport.getAndAddShort(checkObject(object, field), UNSAFE.objectFieldOffset(field), (short) 1);
    }

    public static int getAndIncrementIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        if (isStatic(field.getModifiers())) return UNSAFE.getAndAddInt(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), 1);
        else return UNSAFE.getAndAddInt(checkObject(object, field), UNSAFE.objectFieldOffset(field), 1);
    }

    public static long getAndIncrementLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        if (isStatic(field.getModifiers())) return UNSAFE.getAndAddLong(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), 1);
        else return UNSAFE.getAndAddLong(checkObject(object, field), UNSAFE.objectFieldOffset(field), 1);
    }

    public static float getAndIncrementFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddFloat(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), 1);
        else return UnsafeSupport.getAndAddFloat(checkObject(object, field), UNSAFE.objectFieldOffset(field), 1);
    }

    public static double getAndIncrementDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddDouble(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), 1);
        else return UnsafeSupport.getAndAddDouble(checkObject(object, field), UNSAFE.objectFieldOffset(field), 1);
    }

    public static byte getAndDecrementByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddByte(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), (byte) -1);
        else return UnsafeSupport.getAndAddByte(checkObject(object, field), UNSAFE.objectFieldOffset(field), (byte) -1);
    }

    public static short getAndDecrementShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddShort(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), (short) -1);
        else return UnsafeSupport.getAndAddShort(checkObject(object, field), UNSAFE.objectFieldOffset(field), (short) -1);
    }

    public static int getAndDecrementIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        if (isStatic(field.getModifiers())) return UNSAFE.getAndAddInt(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), -1);
        else return UNSAFE.getAndAddInt(checkObject(object, field), UNSAFE.objectFieldOffset(field), -1);
    }

    public static long getAndDecrementLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        if (isStatic(field.getModifiers())) return UNSAFE.getAndAddLong(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), -1);
        else return UNSAFE.getAndAddLong(checkObject(object, field), UNSAFE.objectFieldOffset(field), -1);
    }

    public static float getAndDecrementFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddFloat(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), -1);
        else return UnsafeSupport.getAndAddFloat(checkObject(object, field), UNSAFE.objectFieldOffset(field), -1);
    }

    public static double getAndDecrementDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddDouble(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), -1);
        else return UnsafeSupport.getAndAddDouble(checkObject(object, field), UNSAFE.objectFieldOffset(field), -1);
    }

    public static byte addAndGetByteField(Object object, Field field, byte delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        if (isStatic(field.getModifiers())) return (byte) (UnsafeSupport.getAndAddByte(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), delta) + delta);
        else return (byte) (UnsafeSupport.getAndAddByte(checkObject(object, field), UNSAFE.objectFieldOffset(field), delta) + delta);
    }

    public static short addAndGetShortField(Object object, Field field, short delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        if (isStatic(field.getModifiers())) return (short) (UnsafeSupport.getAndAddShort(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), delta) + delta);
        else return (short) (UnsafeSupport.getAndAddShort(checkObject(object, field), UNSAFE.objectFieldOffset(field), delta) + delta);
    }

    public static int addAndGetIntField(Object object, Field field, int delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        if (isStatic(field.getModifiers())) return UNSAFE.getAndAddInt(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), delta) + delta;
        else return UNSAFE.getAndAddInt(checkObject(object, field), UNSAFE.objectFieldOffset(field), delta) + delta;
    }

    public static long addAndGetLongField(Object object, Field field, long delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        if (isStatic(field.getModifiers())) return UNSAFE.getAndAddLong(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), delta) + delta;
        else return UNSAFE.getAndAddLong(checkObject(object, field), UNSAFE.objectFieldOffset(field), delta) + delta;
    }

    public static float addAndGetFloatField(Object object, Field field, float delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddFloat(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), delta) + delta;
        else return UnsafeSupport.getAndAddFloat(checkObject(object, field), UNSAFE.objectFieldOffset(field), delta) + delta;
    }

    public static double addAndGetDoubleField(Object object, Field field, double delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddDouble(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), delta) + delta;
        else return UnsafeSupport.getAndAddDouble(checkObject(object, field), UNSAFE.objectFieldOffset(field), delta) + delta;
    }

    public static byte incrementAndGetByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        if (isStatic(field.getModifiers())) return (byte) (UnsafeSupport.getAndAddByte(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), (byte) 1) + 1);
        else return (byte) (UnsafeSupport.getAndAddByte(checkObject(object, field), UNSAFE.objectFieldOffset(field), (byte) 1) + 1);
    }

    public static short incrementAndGetShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        if (isStatic(field.getModifiers())) return (short) (UnsafeSupport.getAndAddShort(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), (short) 1) + 1);
        else return (short) (UnsafeSupport.getAndAddShort(checkObject(object, field), UNSAFE.objectFieldOffset(field), (short) 1) + 1);
    }

    public static int incrementAndGetIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        if (isStatic(field.getModifiers())) return UNSAFE.getAndAddInt(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), 1) + 1;
        else return UNSAFE.getAndAddInt(checkObject(object, field), UNSAFE.objectFieldOffset(field), 1) + 1;
    }

    public static long incrementAndGetLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        if (isStatic(field.getModifiers())) return UNSAFE.getAndAddLong(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), 1) + 1;
        else return UNSAFE.getAndAddLong(checkObject(object, field), UNSAFE.objectFieldOffset(field), 1) + 1;
    }

    public static float incrementAndGetFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddFloat(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), 1) + 1;
        else return UnsafeSupport.getAndAddFloat(checkObject(object, field), UNSAFE.objectFieldOffset(field), 1) + 1;
    }

    public static double incrementAndGetDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddDouble(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), 1) + 1;
        else return UnsafeSupport.getAndAddDouble(checkObject(object, field), UNSAFE.objectFieldOffset(field), 1) + 1;
    }

    public static byte decrementAndGetByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        if (isStatic(field.getModifiers())) return (byte) (UnsafeSupport.getAndAddByte(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), (byte) -1) - 1);
        else return (byte) (UnsafeSupport.getAndAddByte(checkObject(object, field), UNSAFE.objectFieldOffset(field), (byte) -1) - 1);
    }

    public static short decrementAndGetShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        if (isStatic(field.getModifiers())) return (short) (UnsafeSupport.getAndAddShort(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), (short) -1) - 1);
        else return (short) (UnsafeSupport.getAndAddShort(checkObject(object, field), UNSAFE.objectFieldOffset(field), (short) -1) - 1);
    }

    public static int decrementAndGetIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        if (isStatic(field.getModifiers())) return UNSAFE.getAndAddInt(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), -1) - 1;
        else return UNSAFE.getAndAddInt(checkObject(object, field), UNSAFE.objectFieldOffset(field), -1) - 1;
    }

    public static long decrementAndGetLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        if (isStatic(field.getModifiers())) return UNSAFE.getAndAddLong(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), -1) - 1;
        else return UNSAFE.getAndAddLong(checkObject(object, field), UNSAFE.objectFieldOffset(field), -1) - 1;
    }

    public static float decrementAndGetFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddFloat(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), -1) - 1;
        else return UnsafeSupport.getAndAddFloat(checkObject(object, field), UNSAFE.objectFieldOffset(field), -1) - 1;
    }

    public static double decrementAndGetDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        if (isStatic(field.getModifiers())) return UnsafeSupport.getAndAddDouble(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), -1) - 1;
        else return UnsafeSupport.getAndAddDouble(checkObject(object, field), UNSAFE.objectFieldOffset(field), -1) - 1;
    }

    public static Object getArrayObjectElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray()) return Array.get(array, index);
        else if (array.getClass().getComponentType().isPrimitive()) throw new IllegalArgumentException("Illegal array type; expected non-primitive");
        else return UNSAFE.getObjectVolatile(array, UNSAFE.arrayBaseOffset(array.getClass()) + (long) index * UNSAFE.arrayIndexScale(array.getClass()));
    }

    public static boolean getArrayBooleanElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != boolean[].class) return Array.getBoolean(array, index);
        else return UNSAFE.getByteVolatile(array, UNSAFE.arrayBaseOffset(boolean[].class) + (long) index) != 0;
    }

    public static byte getArrayByteElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != byte[].class) return Array.getByte(array, index);
        else return UNSAFE.getByteVolatile(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index);
    }

    public static char getArrayCharElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != char[].class) return Array.getChar(array, index);
        else return UNSAFE.getCharVolatile(array, UNSAFE.arrayBaseOffset(char[].class) + (long) index);
    }

    public static short getArrayShortElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) return Array.getShort(array, index);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == byte[].class)
                return UNSAFE.getByteVolatile(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index);
            else if (clazz == short[].class)
                return UNSAFE.getShortVolatile(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1);
            else return Array.getShort(array, index);
        }
    }

    public static int getArrayIntElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) return Array.getInt(array, index);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == byte[].class)
                return UNSAFE.getByteVolatile(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index);
            else if (clazz == char[].class)
                return UNSAFE.getCharVolatile(array, UNSAFE.arrayBaseOffset(char[].class) + (long) index << 1);
            else if (clazz == short[].class)
                return UNSAFE.getShortVolatile(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1);
            else if (clazz == int[].class)
                return UNSAFE.getIntVolatile(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2);
            else return Array.getInt(array, index);
        }
    }

    public static long getArrayLongElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) return Array.getLong(array, index);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == byte[].class)
                return UNSAFE.getByteVolatile(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index);
            else if (clazz == char[].class)
                return UNSAFE.getCharVolatile(array, UNSAFE.arrayBaseOffset(char[].class) + (long) index << 1);
            else if (clazz == short[].class)
                return UNSAFE.getShortVolatile(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1);
            else if (clazz == int[].class)
                return UNSAFE.getIntVolatile(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2);
            else if (clazz == long[].class)
                return UNSAFE.getLongVolatile(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3);
            else return Array.getLong(array, index);
        }
    }

    public static float getArrayFloatElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) return Array.getFloat(array, index);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == byte[].class)
                return UNSAFE.getByteVolatile(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index);
            else if (clazz == char[].class)
                return UNSAFE.getCharVolatile(array, UNSAFE.arrayBaseOffset(char[].class) + (long) index << 1);
            else if (clazz == short[].class)
                return UNSAFE.getShortVolatile(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1);
            else if (clazz == int[].class)
                return UNSAFE.getIntVolatile(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2);
            else if (clazz == long[].class)
                return UNSAFE.getLongVolatile(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3);
            else if (clazz == float[].class)
                return UNSAFE.getFloatVolatile(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2);
            else return Array.getFloat(array, index);
        }
    }

    public static double getArrayDoubleElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) return Array.getDouble(array, index);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == byte[].class)
                return UNSAFE.getByteVolatile(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index);
            else if (clazz == char[].class)
                return UNSAFE.getCharVolatile(array, UNSAFE.arrayBaseOffset(char[].class) + (long) index << 1);
            else if (clazz == short[].class)
                return UNSAFE.getShortVolatile(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1);
            else if (clazz == int[].class)
                return UNSAFE.getIntVolatile(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2);
            else if (clazz == long[].class)
                return UNSAFE.getLongVolatile(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3);
            else if (clazz == float[].class)
                return UNSAFE.getFloatVolatile(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2);
            else if (clazz == double[].class)
                return UNSAFE.getDoubleVolatile(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3);
            else return Array.getDouble(array, index);
        }
    }

    public static Object getAndSetArrayObjectElement(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray()) {
            Object e = Array.get(array, index);
            Array.set(array, index, value);
            return e;
        }
        else if (array.getClass().getComponentType().isPrimitive()) throw new IllegalArgumentException("Illegal array type; expected non-primitive");
        return UNSAFE.getAndSetObject(array,
                UNSAFE.arrayBaseOffset(array.getClass()) + (long) index * UNSAFE.arrayIndexScale(array.getClass()), value);
    }

    public static boolean getAndSetArrayBooleanElement(Object array, int index, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != boolean[].class) {
            boolean e = Array.getBoolean(array, index);
            Array.setBoolean(array, index, value);
            return e;
        }
        return UnsafeSupport.getAndSetBoolean(array, UNSAFE.arrayBaseOffset(boolean[].class) + (long) index, value);
    }

    public static byte getAndSetArrayByteElement(Object array, int index, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != byte[].class) {
            byte e = Array.getByte(array, index);
            Array.setByte(array, index, value);
            return e;
        }
        return UnsafeSupport.getAndSetByte(array, UNSAFE.arrayBaseOffset(boolean[].class) + (long) index, value);
    }

    public static char getAndSetArrayCharElement(Object array, int index, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != char[].class) {
            char e = Array.getChar(array, index);
            Array.setChar(array, index, value);
            return e;
        }
        return UnsafeSupport.getAndSetChar(array, UNSAFE.arrayBaseOffset(char[].class) + (long) index << 1, value);
    }

    public static short getAndSetArrayShortElement(Object array, int index, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) {
            short e = Array.getShort(array, index);
            Array.setShort(array, index, value);
            return e;
        }
        else {
            Class<?> clazz = array.getClass();
            if (clazz == byte[].class)
                return UnsafeSupport.getAndSetByte(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index, (byte) value);
            else if (clazz == short[].class)
                return UnsafeSupport.getAndSetShort(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, value);
            else {
                short e = Array.getShort(array, index);
                Array.setShort(array, index, value);
                return e;
            }
        }
    }

    public static int getAndSetArrayIntElement(Object array, int index, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) {
            int e = Array.getInt(array, index);
            Array.setInt(array, index, value);
            return e;
        }
        else {
            Class<?> clazz = array.getClass();
            if (clazz == byte[].class)
                return UnsafeSupport.getAndSetByte(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index, (byte) value);
            else if (clazz == char[].class)
                return UnsafeSupport.getAndSetChar(array, UNSAFE.arrayBaseOffset(char[].class) + (long) index << 1, (char) value);
            else if (clazz == short[].class)
                return UnsafeSupport.getAndSetShort(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, (short) value);
            else if (clazz == int[].class)
                return UNSAFE.getAndSetInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, value);
            else {
                int e = Array.getInt(array, index);
                Array.setInt(array, index, value);
                return e;
            }
        }
    }

    public static long getAndSetArrayLongElement(Object array, int index, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) {
            long e = Array.getLong(array, index);
            Array.setLong(array, index, value);
            return e;
        }
        else {
            Class<?> clazz = array.getClass();
            if (clazz == byte[].class)
                return UnsafeSupport.getAndSetByte(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index, (byte) value);
            else if (clazz == char[].class)
                return UnsafeSupport.getAndSetChar(array, UNSAFE.arrayBaseOffset(char[].class) + (long) index << 1, (char) value);
            else if (clazz == short[].class)
                return UnsafeSupport.getAndSetShort(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, (short) value);
            else if (clazz == int[].class)
                return UNSAFE.getAndSetInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, (int) value);
            else if (clazz == long[].class)
                return UNSAFE.getAndSetLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, value);
            else {
                long e = Array.getLong(array, index);
                Array.setLong(array, index, value);
                return e;
            }
        }
    }

    public static float getAndSetArrayFloatElement(Object array, int index, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) {
            float e = Array.getFloat(array, index);
            Array.setFloat(array, index, value);
            return e;
        }
        else {
            Class<?> clazz = array.getClass();
            if (clazz == byte[].class)
                return UnsafeSupport.getAndSetByte(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index, (byte) value);
            else if (clazz == char[].class)
                return UnsafeSupport.getAndSetChar(array, UNSAFE.arrayBaseOffset(char[].class) + (long) index << 1, (char) value);
            else if (clazz == short[].class)
                return UnsafeSupport.getAndSetShort(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, (short) value);
            else if (clazz == int[].class)
                return UNSAFE.getAndSetInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, (int) value);
            else if (clazz == long[].class)
                return UNSAFE.getAndSetLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, (long) value);
            else if (clazz == float[].class)
                return UnsafeSupport.getAndSetFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, value);
            else {
                float e = Array.getFloat(array, index);
                Array.setFloat(array, index, value);
                return e;
            }
        }
    }

    public static double getAndSetArrayDoubleElement(Object array, int index, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) {
            double e = Array.getDouble(array, index);
            Array.setDouble(array, index, value);
            return e;
        }
        else {
            Class<?> clazz = array.getClass();
            if (clazz == byte[].class)
                return UnsafeSupport.getAndSetByte(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index, (byte) value);
            else if (clazz == char[].class)
                return UnsafeSupport.getAndSetChar(array, UNSAFE.arrayBaseOffset(char[].class) + (long) index << 1, (char) value);
            else if (clazz == short[].class)
                return UnsafeSupport.getAndSetShort(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, (short) value);
            else if (clazz == int[].class)
                return UNSAFE.getAndSetInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, (int) value);
            else if (clazz == long[].class)
                return UNSAFE.getAndSetLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, (long) value);
            else if (clazz == float[].class)
                return UnsafeSupport.getAndSetFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, (float) value);
            else if (clazz == double[].class)
                return UnsafeSupport.getAndSetDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
            else {
                double e = Array.getDouble(array, index);
                Array.setDouble(array, index, value);
                return e;
            }
        }
    }

    public static void setArrayObjectElementVolatile(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray()) Array.set(array, index, value);
        else if (array.getClass().getComponentType().isPrimitive()) throw new IllegalArgumentException("Illegal array type; expected non-primitive");
        else UNSAFE.putObjectVolatile(array, UNSAFE.arrayBaseOffset(array.getClass()) + (long) index * UNSAFE.arrayIndexScale(array.getClass()), value);
    }

    public static void setArrayBooleanElementVolatile(Object array, int index, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != boolean[].class) Array.setBoolean(array, index, value);
        else UNSAFE.putByteVolatile(array, UNSAFE.arrayBaseOffset(boolean[].class) + (long) index, (byte) (value ? 1 : 0));
    }

    public static void setArrayByteElementVolatile(Object array, int index, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) Array.setByte(array, index, value);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == byte[].class)
                UNSAFE.putByteVolatile(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index, value);
            else if (clazz == short[].class)
                UNSAFE.putShortVolatile(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, value);
            else if (clazz == int[].class)
                UNSAFE.putIntVolatile(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, value);
            else if (clazz == long[].class)
                UNSAFE.putLongVolatile(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, value);
            else if (clazz == float[].class)
                UNSAFE.putFloatVolatile(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, value);
            else if (clazz == double[].class)
                UNSAFE.putDoubleVolatile(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
            else Array.setByte(array, index, value);
        }
    }

    public static void setArrayCharElementVolatile(Object array, int index, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) Array.setChar(array, index, value);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == char[].class)
                UNSAFE.putCharVolatile(array, UNSAFE.arrayBaseOffset(char[].class) + (long) index << 1, value);
            else if (clazz == int[].class)
                UNSAFE.putIntVolatile(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, value);
            else if (clazz == long[].class)
                UNSAFE.putLongVolatile(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, value);
            else if (clazz == float[].class)
                UNSAFE.putFloatVolatile(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, value);
            else if (clazz == double[].class)
                UNSAFE.putDoubleVolatile(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
            else Array.setChar(array, index, value);
        }
    }

    public static void setArrayShortElementVolatile(Object array, int index, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) Array.setShort(array, index, value);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == short[].class)
                UNSAFE.putShortVolatile(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, value);
            else if (clazz == int[].class)
                UNSAFE.putIntVolatile(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, value);
            else if (clazz == long[].class)
                UNSAFE.putLongVolatile(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, value);
            else if (clazz == float[].class)
                UNSAFE.putFloatVolatile(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, value);
            else if (clazz == double[].class)
                UNSAFE.putDoubleVolatile(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
            else Array.setShort(array, index, value);
        }
    }

    public static void setArrayIntElementVolatile(Object array, int index, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) Array.setInt(array, index, value);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == int[].class)
                UNSAFE.putIntVolatile(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, value);
            else if (clazz == long[].class)
                UNSAFE.putLongVolatile(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, value);
            else if (clazz == float[].class)
                UNSAFE.putFloatVolatile(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, value);
            else if (clazz == double[].class)
                UNSAFE.putDoubleVolatile(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
            else Array.setInt(array, index, value);
        }
    }

    public static void setArrayLongElementVolatile(Object array, int index, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) Array.setLong(array, index, value);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == long[].class)
                UNSAFE.putLongVolatile(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, value);
            else if (clazz == float[].class)
                UNSAFE.putFloatVolatile(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, value);
            else if (clazz == double[].class)
                UNSAFE.putDoubleVolatile(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
            else Array.setLong(array, index, value);
        }
    }

    public static void setArrayFloatElementVolatile(Object array, int index, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) Array.setFloat(array, index, value);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == float[].class)
                UNSAFE.putFloatVolatile(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, value);
            else if (clazz == double[].class)
                UNSAFE.putDoubleVolatile(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
            else Array.setFloat(array, index, value);
        }
    }

    public static void setArrayDoubleElementVolatile(Object array, int index, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != double[].class) Array.setDouble(array, index, value);
        else UNSAFE.putDoubleVolatile(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
    }

    public static void lazySetArrayObjectElement(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray()) Array.set(array, index, value);
        else if (array.getClass().getComponentType().isPrimitive()) throw new IllegalArgumentException("Illegal array type; expected non-primitive");
        else UNSAFE.putOrderedObject(array, UNSAFE.arrayBaseOffset(array.getClass()) + (long) index * UNSAFE.arrayIndexScale(array.getClass()), value);
    }

    public static void lazySetArrayBooleanElement(Object array, int index, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != boolean[].class) Array.setBoolean(array, index, value);
        else UnsafeSupport.putOrderedByte(array, UNSAFE.arrayBaseOffset(boolean[].class) + (long) index, (byte) (value ? 1 : 0));
    }

    public static void lazySetArrayByteElement(Object array, int index, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) Array.setByte(array, index, value);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == byte[].class)
                UnsafeSupport.putOrderedByte(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index, value);
            else if (clazz == short[].class)
                UnsafeSupport.putOrderedShort(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, value);
            else if (clazz == int[].class)
                UNSAFE.putOrderedInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, value);
            else if (clazz == long[].class)
                UNSAFE.putOrderedLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, value);
            else if (clazz == float[].class)
                UnsafeSupport.putOrderedFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, value);
            else if (clazz == double[].class)
                UnsafeSupport.putOrderedDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
            else Array.setByte(array, index, value);
        }
    }

    public static void lazySetArrayCharElement(Object array, int index, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) Array.setChar(array, index, value);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == char[].class)
                UnsafeSupport.putOrderedChar(array, UNSAFE.arrayBaseOffset(char[].class) + (long) index << 1, value);
            else if (clazz == int[].class)
                UNSAFE.putOrderedInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, value);
            else if (clazz == long[].class)
                UNSAFE.putOrderedLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, value);
            else if (clazz == float[].class)
                UnsafeSupport.putOrderedFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, value);
            else if (clazz == double[].class)
                UnsafeSupport.putOrderedDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
            else Array.setChar(array, index, value);
        }
    }

    public static void lazySetArrayShortElement(Object array, int index, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) Array.setShort(array, index, value);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == short[].class)
                UnsafeSupport.putOrderedShort(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, value);
            else if (clazz == int[].class)
                UNSAFE.putOrderedInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, value);
            else if (clazz == long[].class)
                UNSAFE.putOrderedLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, value);
            else if (clazz == float[].class)
                UnsafeSupport.putOrderedFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, value);
            else if (clazz == double[].class)
                UnsafeSupport.putOrderedDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
            else Array.setShort(array, index, value);
        }
    }

    public static void lazySetArrayIntElement(Object array, int index, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) Array.setInt(array, index, value);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == int[].class)
                UNSAFE.putOrderedInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, value);
            else if (clazz == long[].class)
                UNSAFE.putOrderedLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, value);
            else if (clazz == float[].class)
                UnsafeSupport.putOrderedFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, value);
            else if (clazz == double[].class)
                UnsafeSupport.putOrderedDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
            else Array.setInt(array, index, value);
        }
    }

    public static void lazySetArrayLongElement(Object array, int index, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) Array.setLong(array, index, value);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == long[].class)
                UNSAFE.putOrderedLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, value);
            else if (clazz == float[].class)
                UnsafeSupport.putOrderedFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, value);
            else if (clazz == double[].class)
                UnsafeSupport.putOrderedDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
            else Array.setLong(array, index, value);
        }
    }

    public static void lazySetArrayFloatElement(Object array, int index, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null) Array.setFloat(array, index, value);
        else {
            Class<?> clazz = array.getClass();
            if (clazz == float[].class)
                UnsafeSupport.putOrderedFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, value);
            else if (clazz == double[].class)
                UnsafeSupport.putOrderedDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
            else Array.setFloat(array, index, value);
        }
    }

    public static void lazySetArrayDoubleElement(Object array, int index, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != double[].class) Array.setDouble(array, index, value);
        else UnsafeSupport.putOrderedDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, value);
    }

    public static boolean compareAndSetArrayObjectElement(Object array, int index, Object expected, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray()) {
            Object e = Array.get(array, index);
            if (Objects.equals(e, expected)) {
                Array.set(array, index, value);
                return true;
            }
            else return false;
        }
        else if (array.getClass().getComponentType().isPrimitive()) throw new IllegalArgumentException("Illegal array type; expected non-primitive");
        else return UNSAFE.compareAndSwapObject(array,
                    UNSAFE.arrayBaseOffset(array.getClass()) + (long) index * UNSAFE.arrayIndexScale(array.getClass()), expected, value);
    }

    public static boolean compareAndSetArrayBooleanElement(Object array, int index, boolean expected, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != boolean[].class) {
            boolean e = Array.getBoolean(array, index);
            if (e == expected) {
                Array.setBoolean(array, index, value);
                return true;
            }
            else return false;
        }
        else return UnsafeSupport.compareAndSwapByte(array,
                    UNSAFE.arrayBaseOffset(boolean[].class) + (long) index, (byte) (expected ? 1 : 0), (byte) (value ? 1 : 0));
    }

    public static boolean compareAndSetArrayByteElement(Object array, int index, byte expected, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != byte[].class) {
            byte e = Array.getByte(array, index);
            if (e == expected) {
                Array.setByte(array, index, value);
                return true;
            }
            else return false;
        }
        else return UnsafeSupport.compareAndSwapByte(array,
                UNSAFE.arrayBaseOffset(byte[].class) + (long) index, expected, value);
    }

    public static boolean compareAndSetArrayCharElement(Object array, int index, char expected, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != char[].class) {
            char e = Array.getChar(array, index);
            if (e == expected) {
                Array.setChar(array, index, value);
                return true;
            }
            else return false;
        }
        else return UnsafeSupport.compareAndSwapChar(array,
                UNSAFE.arrayBaseOffset(char[].class) + (long) index << 1, expected, value);
    }

    public static boolean compareAndSetArrayShortElement(Object array, int index, short expected, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != short[].class) {
            short e = Array.getShort(array, index);
            if (e == expected) {
                Array.setShort(array, index, value);
                return true;
            }
            else return false;
        }
        else return UnsafeSupport.compareAndSwapShort(array,
                UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, expected, value);
    }

    public static boolean compareAndSetArrayIntElement(Object array, int index, int expected, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != int[].class) {
            int e = Array.getInt(array, index);
            if (e == expected) {
                Array.setInt(array, index, value);
                return true;
            }
            else return false;
        }
        else return UNSAFE.compareAndSwapInt(array,
                UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, expected, value);
    }

    public static boolean compareAndSetArrayLongElement(Object array, int index, long expected, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != long[].class) {
            long e = Array.getLong(array, index);
            if (e == expected) {
                Array.setLong(array, index, value);
                return true;
            }
            else return false;
        }
        else return UNSAFE.compareAndSwapLong(array,
                UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, expected, value);
    }

    public static boolean compareAndSetArrayFloatElement(Object array, int index, float expected, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != float[].class) {
            float e = Array.getFloat(array, index);
            if (e == expected) {
                Array.setFloat(array, index, value);
                return true;
            }
            else return false;
        }
        else return UnsafeSupport.compareAndSwapFloat(array,
                UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, expected, value);
    }

    public static boolean compareAndSetArrayDoubleElement(Object array, int index, double expected, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != double[].class) {
            double e = Array.getDouble(array, index);
            if (e == expected) {
                Array.setDouble(array, index, value);
                return true;
            }
            else return false;
        }
        else return UnsafeSupport.compareAndSwapDouble(array,
                UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, expected, value);
    }

    public static boolean weakCompareAndSetArrayObjectElement(Object array, int index, Object expected, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray()) {
            Object e = Array.get(array, index);
            if (Objects.equals(e, expected)) {
                Array.set(array, index, value);
                return true;
            }
            else return false;
        }
        else if (array.getClass().getComponentType().isPrimitive()) throw new IllegalArgumentException("Illegal array type; expected non-primitive");
        else return UNSAFE.compareAndSwapObject(array,
                    UNSAFE.arrayBaseOffset(array.getClass()) + (long) index * UNSAFE.arrayIndexScale(array.getClass()), expected, value);
    }

    public static boolean weakCompareAndSetArrayBooleanElement(Object array, int index, boolean expected, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != boolean[].class) {
            boolean e = Array.getBoolean(array, index);
            if (e == expected) {
                Array.setBoolean(array, index, value);
                return true;
            }
            else return false;
        }
        else return UnsafeSupport.compareAndSwapByte(array,
                UNSAFE.arrayBaseOffset(boolean[].class) + (long) index, (byte) (expected ? 1 : 0), (byte) (value ? 1 : 0));
    }

    public static boolean weakCompareAndSetArrayByteElement(Object array, int index, byte expected, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != byte[].class) {
            byte e = Array.getByte(array, index);
            if (e == expected) {
                Array.setByte(array, index, value);
                return true;
            }
            else return false;
        }
        else return UnsafeSupport.compareAndSwapByte(array,
                UNSAFE.arrayBaseOffset(byte[].class) + (long) index, expected, value);
    }

    public static boolean weakCompareAndSetArrayCharElement(Object array, int index, char expected, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != char[].class) {
            char e = Array.getChar(array, index);
            if (e == expected) {
                Array.setChar(array, index, value);
                return true;
            }
            else return false;
        }
        else return UnsafeSupport.compareAndSwapChar(array,
                UNSAFE.arrayBaseOffset(char[].class) + (long) index << 1, expected, value);
    }

    public static boolean weakCompareAndSetArrayShortElement(Object array, int index, short expected, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != short[].class) {
            short e = Array.getShort(array, index);
            if (e == expected) {
                Array.setShort(array, index, value);
                return true;
            }
            else return false;
        }
        else return UnsafeSupport.compareAndSwapShort(array,
                UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, expected, value);
    }

    public static boolean weakCompareAndSetArrayIntElement(Object array, int index, int expected, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != int[].class) {
            int e = Array.getInt(array, index);
            if (e == expected) {
                Array.setInt(array, index, value);
                return true;
            }
            else return false;
        }
        else return UNSAFE.compareAndSwapInt(array,
                UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, expected, value);
    }

    public static boolean weakCompareAndSetArrayLongElement(Object array, int index, long expected, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != long[].class) {
            long e = Array.getLong(array, index);
            if (e == expected) {
                Array.setLong(array, index, value);
                return true;
            }
            else return false;
        }
        else return UNSAFE.compareAndSwapLong(array,
                UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, expected, value);
    }

    public static boolean weakCompareAndSetArrayFloatElement(Object array, int index, float expected, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != float[].class) {
            float e = Array.getFloat(array, index);
            if (e == expected) {
                Array.setFloat(array, index, value);
                return true;
            }
            else return false;
        }
        else return UnsafeSupport.compareAndSwapFloat(array,
                UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, expected, value);
    }

    public static boolean weakCompareAndSetArrayDoubleElement(Object array, int index, double expected, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != double[].class) {
            double e = Array.getDouble(array, index);
            if (e == expected) {
                Array.setDouble(array, index, value);
                return true;
            }
            else return false;
        }
        else return UnsafeSupport.compareAndSwapDouble(array,
                UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, expected, value);
    }

    public static byte getAndAddArrayByteElement(Object array, int index, byte delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != byte[].class) {
            byte e = Array.getByte(array, index);
            Array.setByte(array, index, (byte) (e + delta));
            return e;
        }
        else return UnsafeSupport.getAndAddByte(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index, delta);
    }

    public static short getAndAddArrayShortElement(Object array, int index, short delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != short[].class) {
            short e = Array.getShort(array, index);
            Array.setShort(array, index, (short) (e + delta));
            return e;
        }
        else return UnsafeSupport.getAndAddShort(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, delta);
    }

    public static int getAndAddArrayIntElement(Object array, int index, int delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != int[].class) {
            int e = Array.getInt(array, index);
            Array.setInt(array, index, e + delta);
            return e;
        }
        else return UNSAFE.getAndSetInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, delta);
    }

    public static long getAndAddArrayLongElement(Object array, int index, long delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != long[].class) {
            long e = Array.getLong(array, index);
            Array.setLong(array, index, e + delta);
            return e;
        }
        else return UNSAFE.getAndSetLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, delta);
    }

    public static float getAndAddArrayFloatElement(Object array, int index, float delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != float[].class) {
            float e = Array.getFloat(array, index);
            Array.setFloat(array, index, e + delta);
            return e;
        }
        else return UnsafeSupport.getAndAddFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, delta);
    }

    public static double getAndAddArrayDoubleElement(Object array, int index, double delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != double[].class) {
            double e = Array.getDouble(array, index);
            Array.setDouble(array, index, e + delta);
            return e;
        }
        else return UnsafeSupport.getAndAddDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, delta);
    }

    public static byte getAndIncrementArrayByteElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != byte[].class) {
            byte e = Array.getByte(array, index);
            Array.setByte(array, index, (byte) (e + 1));
            return e;
        }
        else return UnsafeSupport.getAndAddByte(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index, (byte) 1);
    }

    public static short getAndIncrementArrayShortElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != short[].class) {
            short e = Array.getShort(array, index);
            Array.setShort(array, index, (short) (e + 1));
            return e;
        }
        else return UnsafeSupport.getAndAddShort(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, (short) 1);
    }

    public static int getAndIncrementArrayIntElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != int[].class) {
            int e = Array.getInt(array, index);
            Array.setInt(array, index, e + 1);
            return e;
        }
        else return UNSAFE.getAndAddInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, 1);
    }

    public static long getAndIncrementArrayLongElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != long[].class) {
            long e = Array.getLong(array, index);
            Array.setLong(array, index, e + 1);
            return e;
        }
        else return UNSAFE.getAndAddLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, 1);
    }

    public static float getAndIncrementArrayFloatElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != float[].class) {
            float e = Array.getFloat(array, index);
            Array.setFloat(array, index, e + 1);
            return e;
        }
        else return UnsafeSupport.getAndAddFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, 1);
    }

    public static double getAndIncrementArrayDoubleElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != double[].class) {
            double e = Array.getDouble(array, index);
            Array.setDouble(array, index, e + 1);
            return e;
        }
        else return UnsafeSupport.getAndAddDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, 1);
    }

    public static byte getAndDecrementArrayByteElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != byte[].class) {
            byte e = Array.getByte(array, index);
            Array.setByte(array, index, (byte) (e - 1));
            return e;
        }
        else return UnsafeSupport.getAndAddByte(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index, (byte) -1);
    }

    public static short getAndDecrementArrayShortElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != short[].class) {
            short e = Array.getShort(array, index);
            Array.setShort(array, index, (short) (e - 1));
            return e;
        }
        else return UnsafeSupport.getAndAddShort(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, (short) -1);
    }

    public static int getAndDecrementArrayIntElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != int[].class) {
            int e = Array.getInt(array, index);
            Array.setInt(array, index, e - 1);
            return e;
        }
        else return UNSAFE.getAndAddInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, -1);
    }

    public static long getAndDecrementArrayLongElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != long[].class) {
            long e = Array.getLong(array, index);
            Array.setLong(array, index, e - 1);
            return e;
        }
        else return UNSAFE.getAndAddLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, -1);
    }

    public static float getAndDecrementArrayFloatElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != float[].class) {
            float e = Array.getFloat(array, index);
            Array.setFloat(array, index, e - 1);
            return e;
        }
        else return UnsafeSupport.getAndAddFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, -1);
    }

    public static double getAndDecrementArrayDoubleElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != double[].class) {
            double e = Array.getDouble(array, index);
            Array.setDouble(array, index, e - 1);
            return e;
        }
        else return UnsafeSupport.getAndAddDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, -1);
    }

    public static byte addAndGetArrayByteElement(Object array, int index, byte delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != byte[].class) {
            byte e = Array.getByte(array, index);
            Array.setByte(array, index, (byte) (e + delta));
            return (byte) (e + delta);
        }
        else return (byte) (UnsafeSupport.getAndAddByte(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index, delta) + delta);
    }

    public static short addAndGetArrayShortElement(Object array, int index, short delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != short[].class) {
            short e = Array.getShort(array, index);
            Array.setShort(array, index, (short) (e + delta));
            return (short) (e + delta);
        }
        else return (short) (UnsafeSupport.getAndAddShort(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, delta) + delta);
    }

    public static int addAndGetArrayIntElement(Object array, int index, int delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != int[].class) {
            int e = Array.getInt(array, index);
            Array.setInt(array, index, e + delta);
            return e + delta;
        }
        else return UNSAFE.getAndAddInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, delta) + delta;
    }

    public static long addAndGetArrayLongElement(Object array, int index, long delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != int[].class) {
            long e = Array.getLong(array, index);
            Array.setLong(array, index, e + delta);
            return e + delta;
        }
        else return UNSAFE.getAndAddLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, delta) + delta;
    }

    public static float addAndGetArrayFloatElement(Object array, int index, float delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != float[].class) {
            float e = Array.getFloat(array, index);
            Array.setFloat(array, index, e + delta);
            return e + delta;
        }
        else return UnsafeSupport.getAndAddFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, delta) + delta;
    }

    public static double addAndGetArrayDoubleElement(Object array, int index, double delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != double[].class) {
            double e = Array.getDouble(array, index);
            Array.setDouble(array, index, e + delta);
            return e + delta;
        }
        else return UnsafeSupport.getAndAddDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, delta) + delta;
    }

    public static byte incrementAndGetArrayByteElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != byte[].class) {
            byte e = Array.getByte(array, index);
            Array.setByte(array, index, (byte) (e + 1));
            return (byte) (e + 1);
        }
        else return (byte) (UnsafeSupport.getAndAddByte(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index, (byte) 1) + 1);
    }

    public static short incrementAndGetArrayShortElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != short[].class) {
            short e = Array.getShort(array, index);
            Array.setShort(array, index, (short) (e + 1));
            return (short) (e + 1);
        }
        else return (short) (UnsafeSupport.getAndAddShort(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, (short) 1) + 1);
    }

    public static int incrementAndGetArrayIntElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != int[].class) {
            int e = Array.getInt(array, index);
            Array.setInt(array, index, e + 1);
            return e + 1;
        }
        else return UNSAFE.getAndAddInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, 1) + 1;
    }

    public static long incrementAndGetArrayLongElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != int[].class) {
            long e = Array.getLong(array, index);
            Array.setLong(array, index, e + 1);
            return e + 1;
        }
        else return UNSAFE.getAndAddLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, 1) + 1;
    }

    public static float incrementAndGetArrayFloatElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != float[].class) {
            float e = Array.getFloat(array, index);
            Array.setFloat(array, index, e + 1);
            return e + 1;
        }
        else return UnsafeSupport.getAndAddFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, 1) + 1;
    }

    public static double incrementAndGetArrayDoubleElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != double[].class) {
            double e = Array.getDouble(array, index);
            Array.setDouble(array, index, e + 1);
            return e + 1;
        }
        else return UnsafeSupport.getAndAddDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, 1) + 1;
    }

    public static byte decrementAndGetArrayByteElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != byte[].class) {
            byte e = Array.getByte(array, index);
            Array.setByte(array, index, (byte) (e - 1));
            return (byte) (e - 1);
        }
        else return (byte) (UnsafeSupport.getAndAddByte(array, UNSAFE.arrayBaseOffset(byte[].class) + (long) index, (byte) -1) - 1);
    }

    public static short decrementAndGetArrayShortElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != short[].class) {
            short e = Array.getShort(array, index);
            Array.setShort(array, index, (short) (e - 1));
            return (short) (e - 1);
        }
        else return (short) (UnsafeSupport.getAndAddShort(array, UNSAFE.arrayBaseOffset(short[].class) + (long) index << 1, (short) -1) - 1);
    }

    public static int decrementAndGetArrayIntElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != int[].class) {
            int e = Array.getInt(array, index);
            Array.setInt(array, index, e - 1);
            return e - 1;
        }
        else return UNSAFE.getAndAddInt(array, UNSAFE.arrayBaseOffset(int[].class) + (long) index << 2, -1) - 1;
    }

    public static long decrementAndGetArrayLongElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != int[].class) {
            long e = Array.getLong(array, index);
            Array.setLong(array, index, e - 1);
            return e - 1;
        }
        else return UNSAFE.getAndAddLong(array, UNSAFE.arrayBaseOffset(long[].class) + (long) index << 3, -1) - 1;
    }

    public static float decrementAndGetArrayFloatElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != float[].class) {
            float e = Array.getFloat(array, index);
            Array.setFloat(array, index, e - 1);
            return e - 1;
        }
        else return UnsafeSupport.getAndAddFloat(array, UNSAFE.arrayBaseOffset(float[].class) + (long) index << 2, -1) - 1;
    }

    public static double decrementAndGetArrayDoubleElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || array.getClass() != double[].class) {
            double e = Array.getDouble(array, index);
            Array.setDouble(array, index, e - 1);
            return e - 1;
        }
        else return UnsafeSupport.getAndAddDouble(array, UNSAFE.arrayBaseOffset(double[].class) + (long) index << 3, -1) - 1;
    }

}
