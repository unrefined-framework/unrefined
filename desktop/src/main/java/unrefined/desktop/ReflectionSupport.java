package unrefined.desktop;

import sun.misc.Unsafe;
import unrefined.internal.MethodLookupUtils;
import unrefined.internal.UnsafeUtils;
import unrefined.util.UnexpectedError;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static java.lang.reflect.Modifier.isStatic;

public class ReflectionSupport {

    private static final Unsafe UNSAFE = UnsafeUtils.getUnsafe();
    private static final MethodHandles.Lookup IMPL_LOOKUP = MethodLookupUtils.getImplLookup();
    
    public static boolean trySetAccessible(AccessibleObject accessible) throws SecurityException, NullPointerException {
        return accessible.trySetAccessible();
    }

    @SuppressWarnings("unchecked")
    public static <T> T allocateInstance(Class<T> clazz) throws InstantiationException, NullPointerException {
        return (T) UNSAFE.allocateInstance(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Constructor<T> constructor, Object... args) throws InstantiationException, InvocationTargetException, NullPointerException, IllegalArgumentException, ExceptionInInitializerError {
        try {
            if (trySetAccessible(constructor)) return constructor.newInstance(args);
        } catch (IllegalAccessException ignored) {
        }
        try {
            return (T) IMPL_LOOKUP.unreflectConstructor(constructor).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            return null;
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InstantiationException | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }

    private static Object checkObject(Object object, Field field) {
        // NOTE: will throw NullPointerException, as specified, if object is null
        if (!field.getDeclaringClass().isAssignableFrom(object.getClass())) {
            StringBuilder builder = new StringBuilder("Can not set ");
            builder.append(field.getType().getName())
                    .append(" field ")
                    .append(field.getDeclaringClass().getName()).append(".").append(field.getName())
                    .append(" to ");
            String attemptedType = object.getClass().getName();
            if (attemptedType.length() > 0) builder.append(attemptedType);
            else builder.append("null value");
            throw new IllegalArgumentException(builder.toString());
        }
        return object;
    }

    public static Object getObjectField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType().isPrimitive()) throw new IllegalArgumentException("Illegal field type; expected non-primitive");
        try {
            if (trySetAccessible(field)) return field.get(object);
        } catch (IllegalAccessException ignored) {
        }
        return isStatic(field.getModifiers()) ?
                UNSAFE.getObject(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getObject(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }
    
    public static boolean getBooleanField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != boolean.class) throw new IllegalArgumentException("Illegal field type; expected boolean");
        try {
            if (trySetAccessible(field)) return field.getBoolean(object);
        } catch (IllegalAccessException ignored) {
        }
        return isStatic(field.getModifiers()) ?
                UNSAFE.getBoolean(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getBoolean(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }
    
    public static byte getByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        try {
            if (trySetAccessible(field)) return field.getByte(object);
        } catch (IllegalAccessException ignored) {
        }
        return isStatic(field.getModifiers()) ?
                UNSAFE.getByte(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getByte(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }
    
    public static char getCharField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != char.class) throw new IllegalArgumentException("Illegal field type; expected char");
        try {
            if (trySetAccessible(field)) return field.getChar(object);
        } catch (IllegalAccessException ignored) {
        }
        return isStatic(field.getModifiers()) ?
                UNSAFE.getChar(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getChar(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }
    
    public static short getShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        try {
            if (trySetAccessible(field)) return field.getShort(object);
        } catch (IllegalAccessException ignored) {
        }
        return isStatic(field.getModifiers()) ?
                UNSAFE.getShort(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getShort(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }
    
    public static int getIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        try {
            if (trySetAccessible(field)) return field.getInt(object);
        } catch (IllegalAccessException ignored) {
        }
        return isStatic(field.getModifiers()) ?
                UNSAFE.getInt(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getInt(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }
    
    public static long getLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        try {
            if (trySetAccessible(field)) return field.getLong(object);
        } catch (IllegalAccessException ignored) {
        }
        return isStatic(field.getModifiers()) ?
                UNSAFE.getLong(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getLong(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }
    
    public static float getFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        try {
            if (trySetAccessible(field)) return field.getFloat(object);
        } catch (IllegalAccessException ignored) {
        }
        return isStatic(field.getModifiers()) ?
                UNSAFE.getFloat(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getFloat(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }
    
    public static double getDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        try {
            if (trySetAccessible(field)) return field.getDouble(object);
        } catch (IllegalAccessException ignored) {
        }
        return isStatic(field.getModifiers()) ?
                UNSAFE.getDouble(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field)) :
                UNSAFE.getDouble(checkObject(object, field), UNSAFE.objectFieldOffset(field));
    }
    
    public static void setObjectField(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType().isPrimitive()) throw new IllegalArgumentException("Illegal field type; expected non-primitive");
        try {
            if (trySetAccessible(field)) {
                field.set(object, value);
                return;
            }
        } catch (IllegalAccessException ignored) {
        }
        if (isStatic(field.getModifiers())) UNSAFE.putObject(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putObject(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }
    
    public static void setBooleanField(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != boolean.class) throw new IllegalArgumentException("Illegal field type; expected boolean");
        try {
            if (trySetAccessible(field)) {
                field.set(object, value);
                return;
            }
        } catch (IllegalAccessException ignored) {
        }
        if (isStatic(field.getModifiers())) UNSAFE.putBoolean(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putBoolean(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }
    
    public static void setByteField(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != byte.class) throw new IllegalArgumentException("Illegal field type; expected byte");
        try {
            if (trySetAccessible(field)) {
                field.set(object, value);
                return;
            }
        } catch (IllegalAccessException ignored) {
        }
        if (isStatic(field.getModifiers())) UNSAFE.putByte(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putByte(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }
    
    public static void setCharField(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != char.class) throw new IllegalArgumentException("Illegal field type; expected char");
        try {
            if (trySetAccessible(field)) {
                field.set(object, value);
                return;
            }
        } catch (IllegalAccessException ignored) {
        }
        if (isStatic(field.getModifiers())) UNSAFE.putChar(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putChar(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }
    
    public static void setShortField(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != short.class) throw new IllegalArgumentException("Illegal field type; expected short");
        try {
            if (trySetAccessible(field)) {
                field.set(object, value);
                return;
            }
        } catch (IllegalAccessException ignored) {
        }
        if (isStatic(field.getModifiers())) UNSAFE.putShort(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putShort(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }
    
    public static void setIntField(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != int.class) throw new IllegalArgumentException("Illegal field type; expected int");
        try {
            if (trySetAccessible(field)) {
                field.set(object, value);
                return;
            }
        } catch (IllegalAccessException ignored) {
        }
        if (isStatic(field.getModifiers())) UNSAFE.putInt(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putInt(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }
    
    public static void setLongField(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != long.class) throw new IllegalArgumentException("Illegal field type; expected long");
        try {
            if (trySetAccessible(field)) {
                field.set(object, value);
                return;
            }
        } catch (IllegalAccessException ignored) {
        }
        if (isStatic(field.getModifiers())) UNSAFE.putLong(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putLong(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }
    
    public static void setFloatField(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != float.class) throw new IllegalArgumentException("Illegal field type; expected float");
        try {
            if (trySetAccessible(field)) {
                field.set(object, value);
                return;
            }
        } catch (IllegalAccessException ignored) {
        }
        if (isStatic(field.getModifiers())) UNSAFE.putFloat(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putFloat(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
    }
    
    public static void setDoubleField(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (field.getType() != double.class) throw new IllegalArgumentException("Illegal field type; expected double");
        try {
            if (trySetAccessible(field)) {
                field.set(object, value);
                return;
            }
        } catch (IllegalAccessException ignored) {
        }
        if (isStatic(field.getModifiers())) UNSAFE.putDouble(field.getDeclaringClass(), UNSAFE.staticFieldOffset(field), value);
        else UNSAFE.putDouble(checkObject(object, field), UNSAFE.objectFieldOffset(field), value);
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
    
    public static void invokeVoidMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != void.class) throw new IllegalArgumentException("Illegal return type; expected void");
        try {
            if (trySetAccessible(method)) {
                method.invoke(object, args);
                return;
            }
        } catch (IllegalAccessException ignored) {
        }
        try {
            if (Modifier.isStatic(method.getModifiers())) IMPL_LOOKUP.unreflect(method).invokeWithArguments(args);
            else IMPL_LOOKUP.unreflect(method).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static Object invokeObjectMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType().isPrimitive()) throw new IllegalArgumentException("Illegal return type; expected non-primitive");
        try {
            if (trySetAccessible(method)) return method.invoke(object, args);
        } catch (IllegalAccessException ignored) {
        }
        try {
            if (Modifier.isStatic(method.getModifiers())) return IMPL_LOOKUP.unreflect(method).invokeWithArguments(args);
            else return IMPL_LOOKUP.unreflect(method).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static boolean invokeBooleanMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != boolean.class) throw new IllegalArgumentException("Illegal return type; expected boolean");
        try {
            if (trySetAccessible(method)) return (boolean) method.invoke(object, args);
        } catch (IllegalAccessException ignored) {
        }
        try {
            if (Modifier.isStatic(method.getModifiers())) return (boolean) IMPL_LOOKUP.unreflect(method).invokeWithArguments(args);
            else return (boolean) IMPL_LOOKUP.unreflect(method).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static byte invokeByteMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != byte.class) throw new IllegalArgumentException("Illegal return type; expected byte");
        try {
            if (trySetAccessible(method)) return (byte) method.invoke(object, args);
        } catch (IllegalAccessException ignored) {
        }
        try {
            if (Modifier.isStatic(method.getModifiers())) return (byte) IMPL_LOOKUP.unreflect(method).invokeWithArguments(args);
            else return (byte) IMPL_LOOKUP.unreflect(method).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static char invokeCharMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != char.class) throw new IllegalArgumentException("Illegal return type; expected char");
        try {
            if (trySetAccessible(method)) return (char) method.invoke(object, args);
        } catch (IllegalAccessException ignored) {
        }
        try {
            if (Modifier.isStatic(method.getModifiers())) return (char) IMPL_LOOKUP.unreflect(method).invokeWithArguments(args);
            else return (char) IMPL_LOOKUP.unreflect(method).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static short invokeShortMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != short.class) throw new IllegalArgumentException("Illegal return type; expected short");
        try {
            if (trySetAccessible(method)) return (short) method.invoke(object, args);
        } catch (IllegalAccessException ignored) {
        }
        try {
            if (Modifier.isStatic(method.getModifiers())) return (short) IMPL_LOOKUP.unreflect(method).invokeWithArguments(args);
            else return (short) IMPL_LOOKUP.unreflect(method).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static int invokeIntMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != int.class) throw new IllegalArgumentException("Illegal return type; expected int");
        try {
            if (trySetAccessible(method)) return (int) method.invoke(object, args);
        } catch (IllegalAccessException ignored) {
        }
        try {
            if (Modifier.isStatic(method.getModifiers())) return (int) IMPL_LOOKUP.unreflect(method).invokeWithArguments(args);
            else return (int) IMPL_LOOKUP.unreflect(method).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static long invokeLongMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != long.class) throw new IllegalArgumentException("Illegal return type; expected long");
        try {
            if (trySetAccessible(method)) return (long) method.invoke(object, args);
        } catch (IllegalAccessException ignored) {
        }
        try {
            if (Modifier.isStatic(method.getModifiers())) return (long) IMPL_LOOKUP.unreflect(method).invokeWithArguments(args);
            else return (long) IMPL_LOOKUP.unreflect(method).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static float invokeFloatMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != float.class) throw new IllegalArgumentException("Illegal return type; expected float");
        try {
            if (trySetAccessible(method)) return (float) method.invoke(object, args);
        } catch (IllegalAccessException ignored) {
        }
        try {
            if (Modifier.isStatic(method.getModifiers())) return (float) IMPL_LOOKUP.unreflect(method).invokeWithArguments(args);
            else return (float) IMPL_LOOKUP.unreflect(method).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static double invokeDoubleMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != double.class) throw new IllegalArgumentException("Illegal return type; expected double");
        try {
            if (trySetAccessible(method)) return (double) method.invoke(object, args);
        } catch (IllegalAccessException ignored) {
        }
        try {
            if (Modifier.isStatic(method.getModifiers())) return (double) IMPL_LOOKUP.unreflect(method).invokeWithArguments(args);
            else return (double) IMPL_LOOKUP.unreflect(method).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static void invokeNonVirtualVoidMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != void.class) throw new IllegalArgumentException("Illegal return type; expected void");
        else if (isStatic(method.getModifiers())) throw new IllegalArgumentException("Illegal method modifier; expected non-static");
        try {
            IMPL_LOOKUP.unreflectSpecial(method, method.getDeclaringClass()).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static Object invokeNonVirtualObjectMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType().isPrimitive()) throw new IllegalArgumentException("Illegal return type; expected non-primitive");
        else if (isStatic(method.getModifiers())) throw new IllegalArgumentException("Illegal method modifier; expected non-static");
        try {
            return IMPL_LOOKUP.unreflectSpecial(method, method.getDeclaringClass()).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static boolean invokeNonVirtualBooleanMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != boolean.class) throw new IllegalArgumentException("Illegal return type; expected boolean");
        else if (isStatic(method.getModifiers())) throw new IllegalArgumentException("Illegal method modifier; expected non-static");
        try {
            return (boolean) IMPL_LOOKUP.unreflectSpecial(method, method.getDeclaringClass()).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static byte invokeNonVirtualByteMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != byte.class) throw new IllegalArgumentException("Illegal return type; expected byte");
        else if (isStatic(method.getModifiers())) throw new IllegalArgumentException("Illegal method modifier; expected non-static");
        try {
            return (byte) IMPL_LOOKUP.unreflectSpecial(method, method.getDeclaringClass()).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static char invokeNonVirtualCharMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != char.class) throw new IllegalArgumentException("Illegal return type; expected char");
        else if (isStatic(method.getModifiers())) throw new IllegalArgumentException("Illegal method modifier; expected non-static");
        try {
            return (char) IMPL_LOOKUP.unreflectSpecial(method, method.getDeclaringClass()).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static short invokeNonVirtualShortMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != short.class) throw new IllegalArgumentException("Illegal return type; expected short");
        else if (isStatic(method.getModifiers())) throw new IllegalArgumentException("Illegal method modifier; expected non-static");
        try {
            return (short) IMPL_LOOKUP.unreflectSpecial(method, method.getDeclaringClass()).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static int invokeNonVirtualIntMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != int.class) throw new IllegalArgumentException("Illegal return type; expected int");
        else if (isStatic(method.getModifiers())) throw new IllegalArgumentException("Illegal method modifier; expected non-static");
        try {
            return (int) IMPL_LOOKUP.unreflectSpecial(method, method.getDeclaringClass()).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static long invokeNonVirtualLongMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != long.class) throw new IllegalArgumentException("Illegal return type; expected long");
        else if (isStatic(method.getModifiers())) throw new IllegalArgumentException("Illegal method modifier; expected non-static");
        try {
            return (long) IMPL_LOOKUP.unreflectSpecial(method, method.getDeclaringClass()).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static float invokeNonVirtualFloatMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != float.class) throw new IllegalArgumentException("Illegal return type; expected float");
        else if (isStatic(method.getModifiers())) throw new IllegalArgumentException("Illegal method modifier; expected non-static");
        try {
            return (float) IMPL_LOOKUP.unreflectSpecial(method, method.getDeclaringClass()).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }
    
    public static double invokeNonVirtualDoubleMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (method.getReturnType() != double.class) throw new IllegalArgumentException("Illegal return type; expected double");
        else if (isStatic(method.getModifiers())) throw new IllegalArgumentException("Illegal method modifier; expected non-static");
        try {
            return (double) IMPL_LOOKUP.unreflectSpecial(method, method.getDeclaringClass()).bindTo(object).invokeWithArguments(args);
        } catch (IllegalAccessException e) {
            throw new UnexpectedError(e);
        } catch (ClassCastException | WrongMethodTypeException e) {
            throw new IllegalArgumentException(e);
        } catch (RuntimeException | Error | InvocationTargetException e) {
            throw e;
        } catch (Throwable e) {
            throw new InvocationTargetException(e);
        }
    }

}
