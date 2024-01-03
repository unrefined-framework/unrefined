package unrefined.util.concurrent.atomic;

import unrefined.context.Environment;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import static java.lang.reflect.Modifier.isStatic;

public abstract class Atomic {

    private static volatile Atomic INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Atomic getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global().get("unrefined.runtime.atomic", Atomic.class);
        }
        return INSTANCE;
    }

    public abstract void fullFence();

    public abstract void loadFence();

    public abstract void storeFence();

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

    public abstract void lazySetObjectFieldVolatile(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetBooleanFieldVolatile(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetByteFieldVolatile(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetCharFieldVolatile(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetShortFieldVolatile(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetIntFieldVolatile(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetLongFieldVolatile(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetFloatFieldVolatile(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract void lazySetDoubleFieldVolatile(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public void lazySetFieldVolatile(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        Class<?> fieldType = field.getType();
        try {
            if (fieldType == boolean.class) lazySetBooleanFieldVolatile(object, field, (Boolean) value);
            else if (fieldType == byte.class) lazySetByteFieldVolatile(object, field, (Byte) value);
            else if (fieldType == char.class) lazySetCharFieldVolatile(object, field, (Character) value);
            else if (fieldType == short.class) lazySetShortFieldVolatile(object, field, (Short) value);
            else if (fieldType == int.class) lazySetIntFieldVolatile(object, field, (Integer) value);
            else if (fieldType == long.class) lazySetLongFieldVolatile(object, field, (Long) value);
            else if (fieldType == float.class) lazySetFloatFieldVolatile(object, field, (Float) value);
            else if (fieldType == double.class) lazySetDoubleFieldVolatile(object, field, (Double) value);
            else lazySetObjectFieldVolatile(object, field, value);
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
    
}
