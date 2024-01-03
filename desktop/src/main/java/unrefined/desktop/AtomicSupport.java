package unrefined.desktop;

import unrefined.util.NotInstantiableError;

import java.lang.reflect.Field;

import static java.lang.reflect.Modifier.isStatic;
import static unrefined.desktop.ReflectionSupport.checkObject;
import static unrefined.desktop.UnsafeSupport.UNSAFE;

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

    public static void lazySetObjectFieldVolatile(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType().isPrimitive()) throw new IllegalArgumentException("Illegal field type; expected non-primitive");
        if (isStatic(field.getModifiers())) UNSAFE.putOrderedObject(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putOrderedObject(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetBooleanFieldVolatile(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != boolean.class) throw new IllegalArgumentException("Illegal field type; expected boolean");
        if (isStatic(field.getModifiers())) UnsafeSupport.putOrderedBoolean(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UnsafeSupport.putOrderedBoolean(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetByteFieldVolatile(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        if (isStatic(field.getModifiers())) UnsafeSupport.putOrderedByte(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UnsafeSupport.putOrderedByte(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetCharFieldVolatile(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != char.class) throw new IllegalArgumentException("Illegal field type; expected char");
        if (isStatic(field.getModifiers())) UnsafeSupport.putOrderedChar(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UnsafeSupport.putOrderedChar(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetShortFieldVolatile(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        if (isStatic(field.getModifiers())) UnsafeSupport.putOrderedShort(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UnsafeSupport.putOrderedShort(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetIntFieldVolatile(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        if (isStatic(field.getModifiers())) UNSAFE.putOrderedInt(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putOrderedInt(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetLongFieldVolatile(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        if (isStatic(field.getModifiers())) UNSAFE.putOrderedLong(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putOrderedLong(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetFloatFieldVolatile(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        if (isStatic(field.getModifiers())) UnsafeSupport.putOrderedFloat(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UnsafeSupport.putOrderedFloat(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }

    public static void lazySetDoubleFieldVolatile(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
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

}
