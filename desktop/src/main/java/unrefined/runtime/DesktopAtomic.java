package unrefined.runtime;

import unrefined.desktop.AtomicSupport;
import unrefined.util.concurrent.atomic.Atomic;

import java.lang.reflect.Field;

public class DesktopAtomic extends Atomic {

    @Override
    public void fullFence() {
        AtomicSupport.fullFence();
    }

    @Override
    public void loadFence() {
        AtomicSupport.loadFence();
    }

    @Override
    public void storeFence() {
        AtomicSupport.storeFence();
    }

    @Override
    public Object getObjectFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getObjectFieldVolatile(object, field);
    }

    @Override
    public boolean getBooleanFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getBooleanFieldVolatile(object, field);
    }

    @Override
    public byte getByteFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getByteFieldVolatile(object, field);
    }

    @Override
    public char getCharFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getCharFieldVolatile(object, field);
    }

    @Override
    public short getShortFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getShortFieldVolatile(object, field);
    }

    @Override
    public int getIntFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getIntFieldVolatile(object, field);
    }

    @Override
    public long getLongFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getLongFieldVolatile(object, field);
    }

    @Override
    public float getFloatFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getFloatFieldVolatile(object, field);
    }

    @Override
    public double getDoubleFieldVolatile(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getDoubleFieldVolatile(object, field);
    }

    @Override
    public Object getAndSetObjectField(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndSetObjectField(object, field, value);
    }

    @Override
    public boolean getAndSetBooleanField(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndSetBooleanField(object, field, value);
    }

    @Override
    public byte getAndSetByteField(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndSetByteField(object, field, value);
    }

    @Override
    public char getAndSetCharField(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndSetCharField(object, field, value);
    }

    @Override
    public short getAndSetShortField(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndSetShortField(object, field, value);
    }

    @Override
    public int getAndSetIntField(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndSetIntField(object, field, value);
    }

    @Override
    public long getAndSetLongField(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndSetLongField(object, field, value);
    }

    @Override
    public float getAndSetFloatField(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndSetFloatField(object, field, value);
    }

    @Override
    public double getAndSetDoubleField(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndSetDoubleField(object, field, value);
    }

    @Override
    public void setObjectFieldVolatile(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.setObjectFieldVolatile(object, field, value);
    }

    @Override
    public void setBooleanFieldVolatile(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.setBooleanFieldVolatile(object, field, value);
    }

    @Override
    public void setByteFieldVolatile(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.setByteFieldVolatile(object, field, value);
    }

    @Override
    public void setCharFieldVolatile(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.setCharFieldVolatile(object, field, value);
    }

    @Override
    public void setShortFieldVolatile(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.setShortFieldVolatile(object, field, value);
    }

    @Override
    public void setIntFieldVolatile(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.setIntFieldVolatile(object, field, value);
    }

    @Override
    public void setLongFieldVolatile(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.setLongFieldVolatile(object, field, value);
    }

    @Override
    public void setFloatFieldVolatile(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.setFloatFieldVolatile(object, field, value);
    }

    @Override
    public void setDoubleFieldVolatile(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.setDoubleFieldVolatile(object, field, value);
    }

    @Override
    public void lazySetObjectFieldVolatile(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetObjectFieldVolatile(object, field, value);
    }

    @Override
    public void lazySetBooleanFieldVolatile(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetBooleanFieldVolatile(object, field, value);
    }

    @Override
    public void lazySetByteFieldVolatile(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetByteFieldVolatile(object, field, value);
    }

    @Override
    public void lazySetCharFieldVolatile(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetCharFieldVolatile(object, field, value);
    }

    @Override
    public void lazySetShortFieldVolatile(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetShortFieldVolatile(object, field, value);
    }

    @Override
    public void lazySetIntFieldVolatile(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetIntFieldVolatile(object, field, value);
    }

    @Override
    public void lazySetLongFieldVolatile(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetLongFieldVolatile(object, field, value);
    }

    @Override
    public void lazySetFloatFieldVolatile(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetFloatFieldVolatile(object, field, value);
    }

    @Override
    public void lazySetDoubleFieldVolatile(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetDoubleFieldVolatile(object, field, value);
    }

    @Override
    public boolean compareAndSetObjectField(Object object, Field field, Object expected, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.compareAndSetObjectField(object, field, expected, value);
    }

    @Override
    public boolean compareAndSetBooleanField(Object object, Field field, boolean expected, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.compareAndSetBooleanField(object, field, expected, value);
    }

    @Override
    public boolean compareAndSetByteField(Object object, Field field, byte expected, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.compareAndSetByteField(object, field, expected, value);
    }

    @Override
    public boolean compareAndSetCharField(Object object, Field field, char expected, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.compareAndSetCharField(object, field, expected, value);
    }

    @Override
    public boolean compareAndSetShortField(Object object, Field field, short expected, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.compareAndSetShortField(object, field, expected, value);
    }

    @Override
    public boolean compareAndSetIntField(Object object, Field field, int expected, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.compareAndSetIntField(object, field, expected, value);
    }

    @Override
    public boolean compareAndSetLongField(Object object, Field field, long expected, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.compareAndSetLongField(object, field, expected, value);
    }

    @Override
    public boolean compareAndSetFloatField(Object object, Field field, float expected, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.compareAndSetFloatField(object, field, expected, value);
    }

    @Override
    public boolean compareAndSetDoubleField(Object object, Field field, double expected, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.compareAndSetDoubleField(object, field, expected, value);
    }

    @Override
    public boolean weakCompareAndSetObjectField(Object object, Field field, Object expected, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.weakCompareAndSetObjectField(object, field, expected, value);
    }

    @Override
    public boolean weakCompareAndSetBooleanField(Object object, Field field, boolean expected, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.weakCompareAndSetBooleanField(object, field, expected, value);
    }

    @Override
    public boolean weakCompareAndSetByteField(Object object, Field field, byte expected, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.weakCompareAndSetByteField(object, field, expected, value);
    }

    @Override
    public boolean weakCompareAndSetCharField(Object object, Field field, char expected, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.weakCompareAndSetCharField(object, field, expected, value);
    }

    @Override
    public boolean weakCompareAndSetShortField(Object object, Field field, short expected, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.weakCompareAndSetShortField(object, field, expected, value);
    }

    @Override
    public boolean weakCompareAndSetIntField(Object object, Field field, int expected, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.weakCompareAndSetIntField(object, field, expected, value);
    }

    @Override
    public boolean weakCompareAndSetLongField(Object object, Field field, long expected, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.weakCompareAndSetLongField(object, field, expected, value);
    }

    @Override
    public boolean weakCompareAndSetFloatField(Object object, Field field, float expected, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.weakCompareAndSetFloatField(object, field, expected, value);
    }

    @Override
    public boolean weakCompareAndSetDoubleField(Object object, Field field, double expected, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.weakCompareAndSetDoubleField(object, field, expected, value);
    }

    @Override
    public byte getAndAddByteField(Object object, Field field, byte delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndAddByteField(object, field, delta);
    }

    @Override
    public short getAndAddShortField(Object object, Field field, short delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndAddShortField(object, field, delta);
    }

    @Override
    public int getAndAddIntField(Object object, Field field, int delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndAddIntField(object, field, delta);
    }

    @Override
    public long getAndAddLongField(Object object, Field field, long delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndAddLongField(object, field, delta);
    }

    @Override
    public float getAndAddFloatField(Object object, Field field, float delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndAddFloatField(object, field, delta);
    }

    @Override
    public double getAndAddDoubleField(Object object, Field field, double delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndAddDoubleField(object, field, delta);
    }

    @Override
    public byte getAndIncrementByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndIncrementByteField(object, field);
    }

    @Override
    public short getAndIncrementShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndIncrementShortField(object, field);
    }

    @Override
    public int getAndIncrementIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndIncrementIntField(object, field);
    }

    @Override
    public long getAndIncrementLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndIncrementLongField(object, field);
    }

    @Override
    public float getAndIncrementFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndIncrementFloatField(object, field);
    }

    @Override
    public double getAndIncrementDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndIncrementDoubleField(object, field);
    }

    @Override
    public byte getAndDecrementByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndDecrementByteField(object, field);
    }

    @Override
    public short getAndDecrementShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndDecrementShortField(object, field);
    }

    @Override
    public int getAndDecrementIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndDecrementIntField(object, field);
    }

    @Override
    public long getAndDecrementLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndDecrementLongField(object, field);
    }

    @Override
    public float getAndDecrementFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndDecrementFloatField(object, field);
    }

    @Override
    public double getAndDecrementDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.getAndDecrementDoubleField(object, field);
    }

    @Override
    public byte addAndGetByteField(Object object, Field field, byte delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.addAndGetByteField(object, field, delta);
    }

    @Override
    public short addAndGetShortField(Object object, Field field, short delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.addAndGetShortField(object, field, delta);
    }

    @Override
    public int addAndGetIntField(Object object, Field field, int delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.addAndGetIntField(object, field, delta);
    }

    @Override
    public long addAndGetLongField(Object object, Field field, long delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.addAndGetLongField(object, field, delta);
    }

    @Override
    public float addAndGetFloatField(Object object, Field field, float delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.addAndGetFloatField(object, field, delta);
    }

    @Override
    public double addAndGetDoubleField(Object object, Field field, double delta) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.addAndGetDoubleField(object, field, delta);
    }

    @Override
    public byte incrementAndGetByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.incrementAndGetByteField(object, field);
    }

    @Override
    public short incrementAndGetShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.incrementAndGetShortField(object, field);
    }

    @Override
    public int incrementAndGetIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.incrementAndGetIntField(object, field);
    }

    @Override
    public long incrementAndGetLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.incrementAndGetLongField(object, field);
    }

    @Override
    public float incrementAndGetFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.incrementAndGetFloatField(object, field);
    }

    @Override
    public double incrementAndGetDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.incrementAndGetDoubleField(object, field);
    }

    @Override
    public byte decrementAndGetByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.decrementAndGetByteField(object, field);
    }

    @Override
    public short decrementAndGetShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.decrementAndGetShortField(object, field);
    }

    @Override
    public int decrementAndGetIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.decrementAndGetIntField(object, field);
    }

    @Override
    public long decrementAndGetLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.decrementAndGetLongField(object, field);
    }

    @Override
    public float decrementAndGetFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.decrementAndGetFloatField(object, field);
    }

    @Override
    public double decrementAndGetDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return AtomicSupport.decrementAndGetDoubleField(object, field);
    }

}
