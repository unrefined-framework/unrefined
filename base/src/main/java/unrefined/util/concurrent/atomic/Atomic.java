package unrefined.util.concurrent.atomic;

import unrefined.context.Environment;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static java.lang.reflect.Modifier.isStatic;

public abstract class Atomic {

    private static volatile Atomic INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Atomic getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.atomic", Atomic.class);
        }
        return INSTANCE;
    }

    public abstract void fullFence();
    public abstract void loadFence();
    public abstract void storeFence();

    public <T> AtomicBooleanFieldUpdater<T> createAtomicBooleanFieldUpdater(Class<T> clazz, String fieldName) {
        return AtomicBooleanFieldUpdater.newUpdater(clazz, fieldName);
    }

    public <T> AtomicBooleanFieldUpdater<T> createAtomicBooleanFieldUpdater(Class<T> clazz, Field field) {
        return AtomicBooleanFieldUpdater.newUpdater(clazz, field.getName());
    }

    public <T> AtomicByteFieldUpdater<T> createAtomicByteFieldUpdater(Class<T> clazz, String fieldName) {
        return AtomicByteFieldUpdater.newUpdater(clazz, fieldName);
    }

    public <T> AtomicByteFieldUpdater<T> createAtomicByteFieldUpdater(Class<T> clazz, Field field) {
        return AtomicByteFieldUpdater.newUpdater(clazz, field.getName());
    }

    public <T> AtomicCharacterFieldUpdater<T> createAtomicCharacterFieldUpdater(Class<T> clazz, String fieldName) {
        return AtomicCharacterFieldUpdater.newUpdater(clazz, fieldName);
    }

    public <T> AtomicCharacterFieldUpdater<T> createAtomicCharacterFieldUpdater(Class<T> clazz, Field field) {
        return AtomicCharacterFieldUpdater.newUpdater(clazz, field.getName());
    }

    public <T> AtomicShortFieldUpdater<T> createAtomicShortFieldUpdater(Class<T> clazz, String fieldName) {
        return AtomicShortFieldUpdater.newUpdater(clazz, fieldName);
    }

    public <T> AtomicShortFieldUpdater<T> createAtomicShortFieldUpdater(Class<T> clazz, Field field) {
        return AtomicShortFieldUpdater.newUpdater(clazz, field.getName());
    }

    public <T> AtomicIntegerFieldUpdater<T> createAtomicIntegerFieldUpdater(Class<T> clazz, String fieldName) {
        return AtomicIntegerFieldUpdater.newUpdater(clazz, fieldName);
    }

    public <T> AtomicIntegerFieldUpdater<T> createAtomicIntegerFieldUpdater(Class<T> clazz, Field field) {
        return AtomicIntegerFieldUpdater.newUpdater(clazz, field.getName());
    }

    public <T> AtomicLongFieldUpdater<T> createAtomicLongFieldUpdater(Class<T> clazz, String fieldName) {
        return AtomicLongFieldUpdater.newUpdater(clazz, fieldName);
    }

    public <T> AtomicLongFieldUpdater<T> createAtomicLongFieldUpdater(Class<T> clazz, Field field) {
        return AtomicLongFieldUpdater.newUpdater(clazz, field.getName());
    }

    public <T> AtomicFloatFieldUpdater<T> createAtomicFloatFieldUpdater(Class<T> clazz, String fieldName) {
        return AtomicFloatFieldUpdater.newUpdater(clazz, fieldName);
    }

    public <T> AtomicFloatFieldUpdater<T> createAtomicFloatFieldUpdater(Class<T> clazz, Field field) {
        return AtomicFloatFieldUpdater.newUpdater(clazz, field.getName());
    }

    public <T> AtomicDoubleFieldUpdater<T> createAtomicDoubleFieldUpdater(Class<T> clazz, String fieldName) {
        return AtomicDoubleFieldUpdater.newUpdater(clazz, fieldName);
    }

    public <T> AtomicDoubleFieldUpdater<T> createAtomicDoubleFieldUpdater(Class<T> clazz, Field field) {
        return AtomicDoubleFieldUpdater.newUpdater(clazz, field.getName());
    }

    public <T, V> AtomicReferenceFieldUpdater<T, V> createAtomicReferenceFieldUpdater(Class<T> tClazz, Class<V> vClazz, String fieldName) {
        return AtomicReferenceFieldUpdater.newUpdater(tClazz, vClazz, fieldName);
    }

    public <T, V> AtomicReferenceFieldUpdater<T, V> createAtomicReferenceFieldUpdater(Class<T> tClazz, Class<V> vClazz, Field field) {
        return AtomicReferenceFieldUpdater.newUpdater(tClazz, vClazz, field.getName());
    }

    public abstract Object getObjectFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean getBooleanFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract byte getByteFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract char getCharFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract short getShortFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract int getIntFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract long getLongFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract float getFloatFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract double getDoubleFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public Object getFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (isStatic(field.getModifiers())) object = field.getDeclaringClass();
        Class<?> fieldType = field.getType();
        if (fieldType == boolean.class) return getBooleanFieldVolatile(object, field);
        else if (fieldType == byte.class) return getByteFieldVolatile(object, field);
        else if (fieldType == char.class) return getCharFieldVolatile(object, field);
        else if (fieldType == short.class) return getShortFieldVolatile(object, field);
        else if (fieldType == int.class) return getIntFieldVolatile(object, field);
        else if (fieldType == long.class) return getLongFieldVolatile(object, field);
        else if (fieldType == float.class) return getFloatFieldVolatile(object, field);
        else if (fieldType == double.class) return getDoubleFieldVolatile(object, field);
        else return getObjectFieldVolatile(object, field);
    }

    public abstract Object getAndSetObjectField(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean getAndSetBooleanField(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract byte getAndSetByteField(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract char getAndSetCharField(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract short getAndSetShortField(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract int getAndSetIntField(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract long getAndSetLongField(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract float getAndSetFloatField(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract double getAndSetDoubleField(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public Object getAndSetField(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (isStatic(field.getModifiers())) object = field.getDeclaringClass();
        Class<?> fieldType = field.getType();
        if (fieldType == boolean.class) return getAndSetBooleanField(object, field, (Boolean) value);
        else if (fieldType == byte.class) return getAndSetByteField(object, field, (Byte) value);
        else if (fieldType == char.class) return getAndSetCharField(object, field, (Character) value);
        else if (fieldType == short.class) return getAndSetShortField(object, field, (Short) value);
        else if (fieldType == int.class) return getAndSetIntField(object, field, (Integer) value);
        else if (fieldType == long.class) return getAndSetLongField(object, field, (Long) value);
        else if (fieldType == float.class) return getAndSetFloatField(object, field, (Float) value);
        else if (fieldType == double.class) return getAndSetDoubleField(object, field, (Double) value);
        else return getAndSetObjectField(object, field, value);
    }

    public abstract void setObjectFieldVolatile(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void setBooleanFieldVolatile(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void setByteFieldVolatile(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void setCharFieldVolatile(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void setShortFieldVolatile(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void setIntFieldVolatile(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void setLongFieldVolatile(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void setFloatFieldVolatile(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void setDoubleFieldVolatile(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public void setFieldVolatile(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        Class<?> fieldType = field.getType();
        try {
            if (fieldType == boolean.class) setBooleanFieldVolatile(object, field, (Boolean) value);
            else if (fieldType == byte.class) setByteFieldVolatile(object, field, (Byte) value);
            else if (fieldType == char.class) setCharFieldVolatile(object, field, (Character) value);
            else if (fieldType == short.class) setShortFieldVolatile(object, field, (Short) value);
            else if (fieldType == int.class) setIntFieldVolatile(object, field, (Integer) value);
            else if (fieldType == long.class) setLongFieldVolatile(object, field, (Long) value);
            else if (fieldType == float.class) setFloatFieldVolatile(object, field, (Float) value);
            else if (fieldType == double.class) setDoubleFieldVolatile(object, field, (Double) value);
            else setObjectFieldVolatile(object, field, value);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public abstract void lazySetObjectField(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetBooleanField(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetByteField(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetCharField(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetShortField(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetIntField(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetLongField(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetFloatField(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetDoubleField(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public void lazySetFieldVolatile(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        Class<?> fieldType = field.getType();
        try {
            if (fieldType == boolean.class) lazySetBooleanField(object, field, (Boolean) value);
            else if (fieldType == byte.class) lazySetByteField(object, field, (Byte) value);
            else if (fieldType == char.class) lazySetCharField(object, field, (Character) value);
            else if (fieldType == short.class) lazySetShortField(object, field, (Short) value);
            else if (fieldType == int.class) lazySetIntField(object, field, (Integer) value);
            else if (fieldType == long.class) lazySetLongField(object, field, (Long) value);
            else if (fieldType == float.class) lazySetFloatField(object, field, (Float) value);
            else if (fieldType == double.class) lazySetDoubleField(object, field, (Double) value);
            else lazySetObjectField(object, field, value);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public abstract boolean compareAndSetObjectField(Object object, Field field, Object expected, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean compareAndSetBooleanField(Object object, Field field, boolean expected, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean compareAndSetByteField(Object object, Field field, byte expected, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean compareAndSetCharField(Object object, Field field, char expected, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean compareAndSetShortField(Object object, Field field, short expected, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean compareAndSetIntField(Object object, Field field, int expected, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean compareAndSetLongField(Object object, Field field, long expected, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean compareAndSetFloatField(Object object, Field field, float expected, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean compareAndSetDoubleField(Object object, Field field, double expected, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public boolean compareAndSetField(Object object, Field field, Object expected, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        Class<?> fieldType = field.getType();
        try {
            if (fieldType == boolean.class) return compareAndSetBooleanField(object, field, (Boolean) expected, (Boolean) value);
            else if (fieldType == byte.class) return compareAndSetByteField(object, field, (Byte) expected, (Byte) value);
            else if (fieldType == char.class) return compareAndSetCharField(object, field, (Character) expected, (Character) value);
            else if (fieldType == short.class) return compareAndSetShortField(object, field, (Short) expected, (Short) value);
            else if (fieldType == int.class) return compareAndSetIntField(object, field, (Integer) expected, (Integer) value);
            else if (fieldType == long.class) return compareAndSetLongField(object, field, (Long) expected, (Long) value);
            else if (fieldType == float.class) return compareAndSetFloatField(object, field, (Float) expected, (Float) value);
            else if (fieldType == double.class) return compareAndSetDoubleField(object, field, (Double) expected, (Double) value);
            else return compareAndSetObjectField(object, field, expected, value);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public abstract boolean weakCompareAndSetObjectField(Object object, Field field, Object expected, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean weakCompareAndSetBooleanField(Object object, Field field, boolean expected, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean weakCompareAndSetByteField(Object object, Field field, byte expected, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean weakCompareAndSetCharField(Object object, Field field, char expected, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean weakCompareAndSetShortField(Object object, Field field, short expected, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean weakCompareAndSetIntField(Object object, Field field, int expected, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean weakCompareAndSetLongField(Object object, Field field, long expected, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean weakCompareAndSetFloatField(Object object, Field field, float expected, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean weakCompareAndSetDoubleField(Object object, Field field, double expected, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public boolean weakCompareAndSetField(Object object, Field field, Object expected, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        Class<?> fieldType = field.getType();
        try {
            if (fieldType == boolean.class) return weakCompareAndSetBooleanField(object, field, (Boolean) expected, (Boolean) value);
            else if (fieldType == byte.class) return weakCompareAndSetByteField(object, field, (Byte) expected, (Byte) value);
            else if (fieldType == char.class) return weakCompareAndSetCharField(object, field, (Character) expected, (Character) value);
            else if (fieldType == short.class) return weakCompareAndSetShortField(object, field, (Short) expected, (Short) value);
            else if (fieldType == int.class) return weakCompareAndSetIntField(object, field, (Integer) expected, (Integer) value);
            else if (fieldType == long.class) return weakCompareAndSetLongField(object, field, (Long) expected, (Long) value);
            else if (fieldType == float.class) return weakCompareAndSetFloatField(object, field, (Float) expected, (Float) value);
            else if (fieldType == double.class) return weakCompareAndSetDoubleField(object, field, (Double) expected, (Double) value);
            else return weakCompareAndSetObjectField(object, field, expected, value);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public abstract byte getAndAddByteField(Object object, Field field, byte delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;
    
    public abstract short getAndAddShortField(Object object, Field field, short delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract int getAndAddIntField(Object object, Field field, int delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract long getAndAddLongField(Object object, Field field, long delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract float getAndAddFloatField(Object object, Field field, float delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract double getAndAddDoubleField(Object object, Field field, double delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract byte getAndIncrementByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract short getAndIncrementShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract int getAndIncrementIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract long getAndIncrementLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract float getAndIncrementFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract double getAndIncrementDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;
  
    public abstract byte getAndDecrementByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract short getAndDecrementShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract int getAndDecrementIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract long getAndDecrementLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract float getAndDecrementFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract double getAndDecrementDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract byte addAndGetByteField(Object object, Field field, byte delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract short addAndGetShortField(Object object, Field field, short delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract int addAndGetIntField(Object object, Field field, int delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract long addAndGetLongField(Object object, Field field, long delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract float addAndGetFloatField(Object object, Field field, float delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract double addAndGetDoubleField(Object object, Field field, double delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract byte incrementAndGetByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract short incrementAndGetShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract int incrementAndGetIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract long incrementAndGetLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract float incrementAndGetFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract double incrementAndGetDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract byte decrementAndGetByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract short decrementAndGetShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract int decrementAndGetIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract long decrementAndGetLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract float decrementAndGetFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract double decrementAndGetDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract Object getArrayObjectElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean getArrayBooleanElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract byte getArrayByteElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract char getArrayCharElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract short getArrayShortElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract int getArrayIntElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract long getArrayLongElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract float getArrayFloatElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract double getArrayDoubleElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public Object getArrayElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray()) return Array.get(array, index);
        else {
            Class<?> componentType = array.getClass().getComponentType();
            if (componentType == boolean.class) return getArrayBooleanElementVolatile(array, index);
            else if (componentType == byte.class) return getArrayByteElementVolatile(array, index);
            else if (componentType == char.class) return getArrayCharElementVolatile(array, index);
            else if (componentType == short.class) return getArrayShortElementVolatile(array, index);
            else if (componentType == int.class) return getArrayIntElementVolatile(array, index);
            else if (componentType == long.class) return getArrayLongElementVolatile(array, index);
            else if (componentType == float.class) return getArrayFloatElementVolatile(array, index);
            else if (componentType == double.class) return getArrayDoubleElementVolatile(array, index);
            else return getArrayObjectElementVolatile(array, index);
        }
    }

    public abstract Object getAndSetArrayObjectElement(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean getAndSetArrayBooleanElement(Object array, int index, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract byte getAndSetArrayByteElement(Object array, int index, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract char getAndSetArrayCharElement(Object array, int index, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract short getAndSetArrayShortElement(Object array, int index, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract int getAndSetArrayIntElement(Object array, int index, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract long getAndSetArrayLongElement(Object array, int index, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract float getAndSetArrayFloatElement(Object array, int index, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract double getAndSetArrayDoubleElement(Object array, int index, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public Object getAndSetArrayElement(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray()) {
            Object e = Array.get(array, index);
            Array.set(array, index, value);
            return e;
        }
        else {
            Class<?> componentType = array.getClass().getComponentType();
            if (componentType == boolean.class) return getAndSetArrayBooleanElement(array, index, (boolean) value);
            else if (componentType == byte.class) return getAndSetArrayByteElement(array, index, (byte) value);
            else if (componentType == char.class) return getAndSetArrayCharElement(array, index, (char) value);
            else if (componentType == short.class) return getAndSetArrayShortElement(array, index, (short) value);
            else if (componentType == int.class) return getAndSetArrayIntElement(array, index, (int) value);
            else if (componentType == long.class) return getAndSetArrayLongElement(array, index, (long) value);
            else if (componentType == float.class) return getAndSetArrayFloatElement(array, index, (float) value);
            else if (componentType == double.class) return getAndSetArrayDoubleElement(array, index, (double) value);
            else return getAndSetArrayObjectElement(array, index, value);
        }
    }

    public abstract void setArrayObjectElementVolatile(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void setArrayBooleanElementVolatile(Object array, int index, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void setArrayByteElementVolatile(Object array, int index, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void setArrayCharElementVolatile(Object array, int index, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void setArrayShortElementVolatile(Object array, int index, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void setArrayIntElementVolatile(Object array, int index, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void setArrayLongElementVolatile(Object array, int index, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void setArrayFloatElementVolatile(Object array, int index, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void setArrayDoubleElementVolatile(Object array, int index, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public void setArrayElementVolatile(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray()) Array.set(array, index, null);
        else if (value == null) setArrayObjectElementVolatile(array, index, value);
        else {
            Class<?> componentType = array.getClass().getComponentType();
            if (componentType == boolean.class) setArrayBooleanElementVolatile(array, index, (boolean) value);
            else if (componentType == byte.class) setArrayByteElementVolatile(array, index, (byte) value);
            else if (componentType == char.class) setArrayCharElementVolatile(array, index, (char) value);
            else if (componentType == short.class) setArrayShortElementVolatile(array, index, (short) value);
            else if (componentType == int.class) setArrayIntElementVolatile(array, index, (int) value);
            else if (componentType == long.class) setArrayLongElementVolatile(array, index, (long) value);
            else if (componentType == float.class) setArrayFloatElementVolatile(array, index, (float) value);
            else if (componentType == double.class) setArrayDoubleElementVolatile(array, index, (double) value);
            else setArrayObjectElementVolatile(array, index, value);
        }
    }

    public abstract void lazySetArrayObjectElement(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void lazySetArrayBooleanElement(Object array, int index, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void lazySetArrayByteElement(Object array, int index, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void lazySetArrayCharElement(Object array, int index, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void lazySetArrayShortElement(Object array, int index, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void lazySetArrayIntElement(Object array, int index, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void lazySetArrayLongElement(Object array, int index, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void lazySetArrayFloatElement(Object array, int index, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract void lazySetArrayDoubleElement(Object array, int index, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public void lazySetArrayElement(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray()) Array.set(array, index, null);
        else if (value == null) lazySetArrayObjectElement(array, index, value);
        else {
            Class<?> componentType = array.getClass().getComponentType();
            if (componentType == boolean.class) lazySetArrayBooleanElement(array, index, (boolean) value);
            else if (componentType == byte.class) lazySetArrayByteElement(array, index, (byte) value);
            else if (componentType == char.class) lazySetArrayCharElement(array, index, (char) value);
            else if (componentType == short.class) lazySetArrayShortElement(array, index, (short) value);
            else if (componentType == int.class) lazySetArrayIntElement(array, index, (int) value);
            else if (componentType == long.class) lazySetArrayLongElement(array, index, (long) value);
            else if (componentType == float.class) lazySetArrayFloatElement(array, index, (float) value);
            else if (componentType == double.class) lazySetArrayDoubleElement(array, index, (double) value);
            else lazySetArrayObjectElement(array, index, value);
        }
    }

    public abstract boolean compareAndSetArrayObjectElement(Object array, int index, Object expected, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean compareAndSetArrayBooleanElement(Object array, int index, boolean expected, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean compareAndSetArrayByteElement(Object array, int index, byte expected, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean compareAndSetArrayCharElement(Object array, int index, char expected, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean compareAndSetArrayShortElement(Object array, int index, short expected, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean compareAndSetArrayIntElement(Object array, int index, int expected, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean compareAndSetArrayLongElement(Object array, int index, long expected, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean compareAndSetArrayFloatElement(Object array, int index, float expected, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean compareAndSetArrayDoubleElement(Object array, int index, double expected, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public boolean compareAndSetArrayElement(Object array, int index, Object expected, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray()) {
            if (Array.get(array, index) == expected) {
                Array.set(array, index, null);
                return true;
            }
            else return false;
        }
        else {
            Class<?> componentType = array.getClass().getComponentType();
            if (componentType == boolean.class) return compareAndSetArrayBooleanElement(array, index, (boolean) expected, (boolean) value);
            else if (componentType == byte.class) return compareAndSetArrayByteElement(array, index, (byte) expected, (byte) value);
            else if (componentType == char.class) return compareAndSetArrayCharElement(array, index, (char) expected, (char) value);
            else if (componentType == short.class) return compareAndSetArrayShortElement(array, index, (short) expected, (short) value);
            else if (componentType == int.class) return compareAndSetArrayIntElement(array, index, (int) expected, (int) value);
            else if (componentType == long.class) return compareAndSetArrayLongElement(array, index, (long) expected, (long) value);
            else if (componentType == float.class) return compareAndSetArrayFloatElement(array, index, (float) expected, (float) value);
            else if (componentType == double.class) return compareAndSetArrayDoubleElement(array, index, (double) expected, (double) value);
            else return compareAndSetArrayObjectElement(array, index, expected, value);
        }
    }

    public abstract boolean weakCompareAndSetArrayObjectElement(Object array, int index, Object expected, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean weakCompareAndSetArrayBooleanElement(Object array, int index, boolean expected, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean weakCompareAndSetArrayByteElement(Object array, int index, byte expected, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean weakCompareAndSetArrayCharElement(Object array, int index, char expected, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean weakCompareAndSetArrayShortElement(Object array, int index, short expected, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean weakCompareAndSetArrayIntElement(Object array, int index, int expected, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean weakCompareAndSetArrayLongElement(Object array, int index, long expected, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean weakCompareAndSetArrayFloatElement(Object array, int index, float expected, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract boolean weakCompareAndSetArrayDoubleElement(Object array, int index, double expected, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public boolean weakCompareAndSetArrayElement(Object array, int index, Object expected, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray()) {
            if (Array.get(array, index) == expected) {
                Array.set(array, index, null);
                return true;
            }
            else return false;
        }
        else {
            Class<?> componentType = array.getClass().getComponentType();
            if (componentType == boolean.class) return weakCompareAndSetArrayBooleanElement(array, index, (boolean) expected, (boolean) value);
            else if (componentType == byte.class) return weakCompareAndSetArrayByteElement(array, index, (byte) expected, (byte) value);
            else if (componentType == char.class) return weakCompareAndSetArrayCharElement(array, index, (char) expected, (char) value);
            else if (componentType == short.class) return weakCompareAndSetArrayShortElement(array, index, (short) expected, (short) value);
            else if (componentType == int.class) return weakCompareAndSetArrayIntElement(array, index, (int) expected, (int) value);
            else if (componentType == long.class) return weakCompareAndSetArrayLongElement(array, index, (long) expected, (long) value);
            else if (componentType == float.class) return weakCompareAndSetArrayFloatElement(array, index, (float) expected, (float) value);
            else if (componentType == double.class) return weakCompareAndSetArrayDoubleElement(array, index, (double) expected, (double) value);
            else return weakCompareAndSetArrayObjectElement(array, index, expected, value);
        }
    }

    public abstract byte getAndAddArrayByteElement(Object array, int index, byte delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract short getAndAddArrayShortElement(Object array, int index, short delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract int getAndAddArrayIntElement(Object array, int index, int delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract long getAndAddArrayLongElement(Object array, int index, long delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract float getAndAddArrayFloatElement(Object array, int index, float delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract double getAndAddArrayDoubleElement(Object array, int index, double delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract byte getAndIncrementArrayByteElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract short getAndIncrementArrayShortElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract int getAndIncrementArrayIntElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract long getAndIncrementArrayLongElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract float getAndIncrementArrayFloatElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract double getAndIncrementArrayDoubleElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract byte getAndDecrementArrayByteElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract short getAndDecrementArrayShortElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract int getAndDecrementArrayIntElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract long getAndDecrementArrayLongElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract float getAndDecrementArrayFloatElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract double getAndDecrementArrayDoubleElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract byte addAndGetArrayByteElement(Object array, int index, byte delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract short addAndGetArrayShortElement(Object array, int index, short delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract int addAndGetArrayIntElement(Object array, int index, int delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract long addAndGetArrayLongElement(Object array, int index, long delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract float addAndGetArrayFloatElement(Object array, int index, float delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract double addAndGetArrayDoubleElement(Object array, int index, double delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract byte incrementAndGetArrayByteElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract short incrementAndGetArrayShortElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract int incrementAndGetArrayIntElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract long incrementAndGetArrayLongElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract float incrementAndGetArrayFloatElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract double incrementAndGetArrayDoubleElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract byte decrementAndGetArrayByteElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract short decrementAndGetArrayShortElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract int decrementAndGetArrayIntElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract long decrementAndGetArrayLongElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract float decrementAndGetArrayFloatElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    public abstract double decrementAndGetArrayDoubleElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
    
}
