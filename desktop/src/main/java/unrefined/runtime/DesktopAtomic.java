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
    public void lazySetObjectField(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetObjectField(object, field, value);
    }

    @Override
    public void lazySetBooleanField(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetBooleanField(object, field, value);
    }

    @Override
    public void lazySetByteField(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetByteField(object, field, value);
    }

    @Override
    public void lazySetCharField(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetCharField(object, field, value);
    }

    @Override
    public void lazySetShortField(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetShortField(object, field, value);
    }

    @Override
    public void lazySetIntField(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetIntField(object, field, value);
    }

    @Override
    public void lazySetLongField(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetLongField(object, field, value);
    }

    @Override
    public void lazySetFloatField(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetFloatField(object, field, value);
    }

    @Override
    public void lazySetDoubleField(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        AtomicSupport.lazySetDoubleField(object, field, value);
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

    @Override
    public Object getArrayObjectElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getArrayObjectElementVolatile(array, index);
    }

    @Override
    public boolean getArrayBooleanElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getArrayBooleanElementVolatile(array, index);
    }

    @Override
    public byte getArrayByteElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getArrayByteElementVolatile(array, index);
    }

    @Override
    public char getArrayCharElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getArrayCharElementVolatile(array, index);
    }

    @Override
    public short getArrayShortElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getArrayShortElementVolatile(array, index);
    }

    @Override
    public int getArrayIntElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getArrayIntElementVolatile(array, index);
    }

    @Override
    public long getArrayLongElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getArrayLongElementVolatile(array, index);
    }

    @Override
    public float getArrayFloatElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getArrayFloatElementVolatile(array, index);
    }

    @Override
    public double getArrayDoubleElementVolatile(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getArrayDoubleElementVolatile(array, index);
    }

    @Override
    public Object getAndSetArrayObjectElement(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndSetArrayObjectElement(array, index, value);
    }

    @Override
    public boolean getAndSetArrayBooleanElement(Object array, int index, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndSetArrayBooleanElement(array, index, value);
    }

    @Override
    public byte getAndSetArrayByteElement(Object array, int index, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndSetArrayByteElement(array, index, value);
    }

    @Override
    public char getAndSetArrayCharElement(Object array, int index, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndSetArrayCharElement(array, index, value);
    }

    @Override
    public short getAndSetArrayShortElement(Object array, int index, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndSetArrayShortElement(array, index, value);
    }

    @Override
    public int getAndSetArrayIntElement(Object array, int index, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndSetArrayIntElement(array, index, value);
    }

    @Override
    public long getAndSetArrayLongElement(Object array, int index, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndSetArrayLongElement(array, index, value);
    }

    @Override
    public float getAndSetArrayFloatElement(Object array, int index, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndSetArrayFloatElement(array, index, value);
    }

    @Override
    public double getAndSetArrayDoubleElement(Object array, int index, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndSetArrayDoubleElement(array, index, value);
    }

    @Override
    public void setArrayObjectElementVolatile(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.setArrayObjectElementVolatile(array, index, value);
    }

    @Override
    public void setArrayBooleanElementVolatile(Object array, int index, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.setArrayBooleanElementVolatile(array, index, value);
    }

    @Override
    public void setArrayByteElementVolatile(Object array, int index, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.setArrayByteElementVolatile(array, index, value);
    }

    @Override
    public void setArrayCharElementVolatile(Object array, int index, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.setArrayCharElementVolatile(array, index, value);
    }

    @Override
    public void setArrayShortElementVolatile(Object array, int index, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.setArrayShortElementVolatile(array, index, value);
    }

    @Override
    public void setArrayIntElementVolatile(Object array, int index, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.setArrayIntElementVolatile(array, index, value);
    }

    @Override
    public void setArrayLongElementVolatile(Object array, int index, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.setArrayLongElementVolatile(array, index, value);
    }

    @Override
    public void setArrayFloatElementVolatile(Object array, int index, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.setArrayFloatElementVolatile(array, index, value);
    }

    @Override
    public void setArrayDoubleElementVolatile(Object array, int index, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.setArrayDoubleElementVolatile(array, index, value);
    }

    @Override
    public void lazySetArrayObjectElement(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.lazySetArrayObjectElement(array, index, value);
    }

    @Override
    public void lazySetArrayBooleanElement(Object array, int index, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.lazySetArrayBooleanElement(array, index, value);
    }

    @Override
    public void lazySetArrayByteElement(Object array, int index, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.lazySetArrayByteElement(array, index, value);
    }

    @Override
    public void lazySetArrayCharElement(Object array, int index, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.lazySetArrayCharElement(array, index, value);
    }

    @Override
    public void lazySetArrayShortElement(Object array, int index, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.lazySetArrayShortElement(array, index, value);
    }

    @Override
    public void lazySetArrayIntElement(Object array, int index, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.lazySetArrayIntElement(array, index, value);
    }

    @Override
    public void lazySetArrayLongElement(Object array, int index, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.lazySetArrayLongElement(array, index, value);
    }

    @Override
    public void lazySetArrayFloatElement(Object array, int index, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.lazySetArrayFloatElement(array, index, value);
    }

    @Override
    public void lazySetArrayDoubleElement(Object array, int index, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        AtomicSupport.lazySetArrayDoubleElement(array, index, value);
    }

    @Override
    public boolean compareAndSetArrayObjectElement(Object array, int index, Object expected, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.compareAndSetArrayObjectElement(array, index, expected, value);
    }

    @Override
    public boolean compareAndSetArrayBooleanElement(Object array, int index, boolean expected, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.compareAndSetArrayBooleanElement(array, index, expected, value);
    }

    @Override
    public boolean compareAndSetArrayByteElement(Object array, int index, byte expected, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.compareAndSetArrayByteElement(array, index, expected, value);
    }

    @Override
    public boolean compareAndSetArrayCharElement(Object array, int index, char expected, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.compareAndSetArrayCharElement(array, index, expected, value);
    }

    @Override
    public boolean compareAndSetArrayShortElement(Object array, int index, short expected, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.compareAndSetArrayShortElement(array, index, expected, value);
    }

    @Override
    public boolean compareAndSetArrayIntElement(Object array, int index, int expected, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.compareAndSetArrayIntElement(array, index, expected, value);
    }

    @Override
    public boolean compareAndSetArrayLongElement(Object array, int index, long expected, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.compareAndSetArrayLongElement(array, index, expected, value);
    }

    @Override
    public boolean compareAndSetArrayFloatElement(Object array, int index, float expected, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.compareAndSetArrayFloatElement(array, index, expected, value);
    }

    @Override
    public boolean compareAndSetArrayDoubleElement(Object array, int index, double expected, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.compareAndSetArrayDoubleElement(array, index, expected, value);
    }

    @Override
    public boolean weakCompareAndSetArrayObjectElement(Object array, int index, Object expected, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.weakCompareAndSetArrayObjectElement(array, index, expected, value);
    }

    @Override
    public boolean weakCompareAndSetArrayBooleanElement(Object array, int index, boolean expected, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.weakCompareAndSetArrayBooleanElement(array, index, expected, value);
    }

    @Override
    public boolean weakCompareAndSetArrayByteElement(Object array, int index, byte expected, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.weakCompareAndSetArrayByteElement(array, index, expected, value);
    }

    @Override
    public boolean weakCompareAndSetArrayCharElement(Object array, int index, char expected, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.weakCompareAndSetArrayCharElement(array, index, expected, value);
    }

    @Override
    public boolean weakCompareAndSetArrayShortElement(Object array, int index, short expected, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.weakCompareAndSetArrayShortElement(array, index, expected, value);
    }

    @Override
    public boolean weakCompareAndSetArrayIntElement(Object array, int index, int expected, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.weakCompareAndSetArrayIntElement(array, index, expected, value);
    }

    @Override
    public boolean weakCompareAndSetArrayLongElement(Object array, int index, long expected, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.weakCompareAndSetArrayLongElement(array, index, expected, value);
    }

    @Override
    public boolean weakCompareAndSetArrayFloatElement(Object array, int index, float expected, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.weakCompareAndSetArrayFloatElement(array, index, expected, value);
    }

    @Override
    public boolean weakCompareAndSetArrayDoubleElement(Object array, int index, double expected, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.weakCompareAndSetArrayDoubleElement(array, index, expected, value);
    }

    @Override
    public byte getAndAddArrayByteElement(Object array, int index, byte delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndAddArrayByteElement(array, index, delta);
    }

    @Override
    public short getAndAddArrayShortElement(Object array, int index, short delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndAddArrayShortElement(array, index, delta);
    }

    @Override
    public int getAndAddArrayIntElement(Object array, int index, int delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndAddArrayIntElement(array, index, delta);
    }

    @Override
    public long getAndAddArrayLongElement(Object array, int index, long delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndAddArrayLongElement(array, index, delta);
    }

    @Override
    public float getAndAddArrayFloatElement(Object array, int index, float delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndAddArrayFloatElement(array, index, delta);
    }

    @Override
    public double getAndAddArrayDoubleElement(Object array, int index, double delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndAddArrayDoubleElement(array, index, delta);
    }

    @Override
    public byte getAndIncrementArrayByteElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndIncrementArrayByteElement(array, index);
    }

    @Override
    public short getAndIncrementArrayShortElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndIncrementArrayShortElement(array, index);
    }

    @Override
    public int getAndIncrementArrayIntElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndIncrementArrayIntElement(array, index);
    }

    @Override
    public long getAndIncrementArrayLongElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndIncrementArrayLongElement(array, index);
    }

    @Override
    public float getAndIncrementArrayFloatElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndIncrementArrayFloatElement(array, index);
    }

    @Override
    public double getAndIncrementArrayDoubleElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndIncrementArrayDoubleElement(array, index);
    }

    @Override
    public byte getAndDecrementArrayByteElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndDecrementArrayByteElement(array, index);
    }

    @Override
    public short getAndDecrementArrayShortElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndDecrementArrayShortElement(array, index);
    }

    @Override
    public int getAndDecrementArrayIntElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndDecrementArrayIntElement(array, index);
    }

    @Override
    public long getAndDecrementArrayLongElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndDecrementArrayLongElement(array, index);
    }

    @Override
    public float getAndDecrementArrayFloatElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndDecrementArrayFloatElement(array, index);
    }

    @Override
    public double getAndDecrementArrayDoubleElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.getAndDecrementArrayDoubleElement(array, index);
    }

    @Override
    public byte addAndGetArrayByteElement(Object array, int index, byte delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.addAndGetArrayByteElement(array, index, delta);
    }

    @Override
    public short addAndGetArrayShortElement(Object array, int index, short delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.addAndGetArrayShortElement(array, index, delta);
    }

    @Override
    public int addAndGetArrayIntElement(Object array, int index, int delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.addAndGetArrayIntElement(array, index, delta);
    }

    @Override
    public long addAndGetArrayLongElement(Object array, int index, long delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.addAndGetArrayLongElement(array, index, delta);
    }

    @Override
    public float addAndGetArrayFloatElement(Object array, int index, float delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.addAndGetArrayFloatElement(array, index, delta);
    }

    @Override
    public double addAndGetArrayDoubleElement(Object array, int index, double delta) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.addAndGetArrayDoubleElement(array, index, delta);
    }

    @Override
    public byte incrementAndGetArrayByteElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.incrementAndGetArrayByteElement(array, index);
    }

    @Override
    public short incrementAndGetArrayShortElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.incrementAndGetArrayShortElement(array, index);
    }

    @Override
    public int incrementAndGetArrayIntElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.incrementAndGetArrayIntElement(array, index);
    }

    @Override
    public long incrementAndGetArrayLongElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.incrementAndGetArrayLongElement(array, index);
    }

    @Override
    public float incrementAndGetArrayFloatElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.incrementAndGetArrayFloatElement(array, index);
    }

    @Override
    public double incrementAndGetArrayDoubleElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.incrementAndGetArrayDoubleElement(array, index);
    }

    @Override
    public byte decrementAndGetArrayByteElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.decrementAndGetArrayByteElement(array, index);
    }

    @Override
    public short decrementAndGetArrayShortElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.decrementAndGetArrayShortElement(array, index);
    }

    @Override
    public int decrementAndGetArrayIntElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.decrementAndGetArrayIntElement(array, index);
    }

    @Override
    public long decrementAndGetArrayLongElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.decrementAndGetArrayLongElement(array, index);
    }

    @Override
    public float decrementAndGetArrayFloatElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.decrementAndGetArrayFloatElement(array, index);
    }

    @Override
    public double decrementAndGetArrayDoubleElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return AtomicSupport.decrementAndGetArrayDoubleElement(array, index);
    }

}
