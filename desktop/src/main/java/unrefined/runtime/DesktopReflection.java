package unrefined.runtime;

import unrefined.desktop.ReflectionSupport;
import unrefined.util.reflect.Reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DesktopReflection extends Reflection {

    @Override
    public boolean trySetAccessible(AccessibleObject accessible) throws SecurityException, NullPointerException {
        return ReflectionSupport.trySetAccessible(accessible);
    }

    @Override
    public <T> T allocateInstance(Class<T> clazz) throws InstantiationException, NullPointerException {
        return ReflectionSupport.allocateInstance(clazz);
    }

    @Override
    public <T> T newInstance(Constructor<T> constructor, Object... args) throws InstantiationException, InvocationTargetException, NullPointerException, IllegalArgumentException, ExceptionInInitializerError {
        return ReflectionSupport.newInstance(constructor, args);
    }

    @Override
    public Object getObjectField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.getObjectField(object, field);
    }

    @Override
    public boolean getBooleanField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.getBooleanField(object, field);
    }

    @Override
    public byte getByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.getByteField(object, field);
    }

    @Override
    public char getCharField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.getCharField(object, field);
    }

    @Override
    public short getShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.getShortField(object, field);
    }

    @Override
    public int getIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.getIntField(object, field);
    }

    @Override
    public long getLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.getLongField(object, field);
    }

    @Override
    public float getFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.getFloatField(object, field);
    }

    @Override
    public double getDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.getDoubleField(object, field);
    }

    @Override
    public void setObjectField(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        ReflectionSupport.setObjectField(object, field, value);
    }

    @Override
    public void setBooleanField(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        ReflectionSupport.setBooleanField(object, field, value);
    }

    @Override
    public void setByteField(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        ReflectionSupport.setByteField(object, field, value);
    }

    @Override
    public void setCharField(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        ReflectionSupport.setCharField(object, field, value);
    }

    @Override
    public void setShortField(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        ReflectionSupport.setShortField(object, field, value);
    }

    @Override
    public void setIntField(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        ReflectionSupport.setIntField(object, field, value);
    }

    @Override
    public void setLongField(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        ReflectionSupport.setLongField(object, field, value);
    }

    @Override
    public void setFloatField(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        ReflectionSupport.setFloatField(object, field, value);
    }

    @Override
    public void setDoubleField(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        ReflectionSupport.setDoubleField(object, field, value);
    }

    @Override
    public void invokeVoidMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        ReflectionSupport.invokeVoidMethod(object, method, args);
    }

    @Override
    public Object invokeObjectMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeObjectMethod(object, method, args);
    }

    @Override
    public boolean invokeBooleanMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeBooleanMethod(object, method, args);
    }

    @Override
    public byte invokeByteMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeByteMethod(object, method, args);
    }

    @Override
    public char invokeCharMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeCharMethod(object, method, args);
    }

    @Override
    public short invokeShortMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeShortMethod(object, method, args);
    }

    @Override
    public int invokeIntMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeIntMethod(object, method, args);
    }

    @Override
    public long invokeLongMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeLongMethod(object, method, args);
    }

    @Override
    public float invokeFloatMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeFloatMethod(object, method, args);
    }

    @Override
    public double invokeDoubleMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeDoubleMethod(object, method, args);
    }

    @Override
    public Object invokeMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeMethod(object, method, args);
    }

    @Override
    public void invokeNonVirtualVoidMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        ReflectionSupport.invokeNonVirtualVoidMethod(object, method, args);
    }

    @Override
    public Object invokeNonVirtualObjectMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeNonVirtualObjectMethod(object, method, args);
    }

    @Override
    public boolean invokeNonVirtualBooleanMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeNonVirtualBooleanMethod(object, method, args);
    }

    @Override
    public byte invokeNonVirtualByteMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeNonVirtualByteMethod(object, method, args);
    }

    @Override
    public char invokeNonVirtualCharMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeNonVirtualCharMethod(object, method, args);
    }

    @Override
    public short invokeNonVirtualShortMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeNonVirtualShortMethod(object, method, args);
    }

    @Override
    public int invokeNonVirtualIntMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeNonVirtualIntMethod(object, method, args);
    }

    @Override
    public long invokeNonVirtualLongMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeNonVirtualLongMethod(object, method, args);
    }

    @Override
    public float invokeNonVirtualFloatMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeNonVirtualFloatMethod(object, method, args);
    }

    @Override
    public double invokeNonVirtualDoubleMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeNonVirtualDoubleMethod(object, method, args);
    }

    @Override
    public Object invokeNonVirtualMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeNonVirtualMethod(object, method, args);
    }

    @Override
    public Object newProxyInstance(ClassLoader classLoader, InvocationHandler handler, Class<?>... interfaces) {
        return ReflectionSupport.newProxyInstance(classLoader, handler, interfaces);
    }

    @Override
    public <T> T newProxyInstance(ClassLoader classLoader, InvocationHandler handler, Class<T> theInterface) {
        return ReflectionSupport.newProxyInstance(classLoader, handler, theInterface);
    }

    @Override
    public boolean isProxyClass(Class<?> clazz) {
        return ReflectionSupport.isProxyClass(clazz);
    }

    @Override
    public boolean isProxyObject(Object object) {
        return ReflectionSupport.isProxyObject(object);
    }

    @Override
    public InvocationHandler getInvocationHandler(Object object) {
        return ReflectionSupport.getInvocationHandler(object);
    }

    @Override
    public void invokeDefaultVoidMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        ReflectionSupport.invokeDefaultVoidMethod(object, method, args);
    }

    @Override
    public boolean invokeDefaultBooleanMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeDefaultBooleanMethod(object, method, args);
    }

    @Override
    public byte invokeDefaultByteMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeDefaultByteMethod(object, method, args);
    }

    @Override
    public char invokeDefaultCharMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeDefaultCharMethod(object, method, args);
    }

    @Override
    public short invokeDefaultShortMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeDefaultShortMethod(object, method, args);
    }

    @Override
    public int invokeDefaultIntMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeDefaultIntMethod(object, method, args);
    }

    @Override
    public long invokeDefaultLongMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeDefaultLongMethod(object, method, args);
    }

    @Override
    public float invokeDefaultFloatMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeDefaultFloatMethod(object, method, args);
    }

    @Override
    public double invokeDefaultDoubleMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeDefaultDoubleMethod(object, method, args);
    }

    @Override
    public Object invokeDefaultObjectMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeDefaultObjectMethod(object, method, args);
    }

    @Override
    public Object invokeDefaultMethod(Object object, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return ReflectionSupport.invokeDefaultMethod(object, method, args);
    }

    @Override
    public Class<?> getCallerClass() {
        return ReflectionSupport.getCallerClass();
    }

    @Override
    public String getCallerMethod() {
        return ReflectionSupport.getCallerMethod();
    }

    @Override
    public Class<?> getCalleeClass() {
        return ReflectionSupport.getCallerClass(0);
    }

    @Override
    public String getCalleeMethod() {
        return ReflectionSupport.getCallerMethod(0);
    }

    @Override
    public Class<?> getCallerClass(int depth) {
        return ReflectionSupport.getCallerClass(depth + 1);
    }

    @Override
    public String getCallerMethod(int depth) {
        return ReflectionSupport.getCallerMethod(depth + 1);
    }

    @Override
    public void ensureInitialized(Class<?> clazz) {
        ReflectionSupport.ensureInitialized(clazz);
    }

    @Override
    public boolean shouldBeInitialized(Class<?> clazz) {
        return ReflectionSupport.shouldBeInitialized(clazz);
    }

}
