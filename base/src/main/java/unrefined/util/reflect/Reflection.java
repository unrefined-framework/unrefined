package unrefined.util.reflect;

import unrefined.context.Environment;
import unrefined.util.FastArray;
import unrefined.util.UnexpectedError;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isStatic;

public abstract class Reflection {

    private static volatile Reflection INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Reflection getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.reflection", Reflection.class);
        }
        return INSTANCE;
    }

    /**
     * Set the {@code accessible} flag for this reflected object to {@code true}
     * if possible. This method sets the {@code accessible} flag, as if by
     * invoking {@link AccessibleObject#setAccessible(boolean) setAccessible(true)}, and returns
     * the possibly-updated value for the {@code accessible} flag. If access
     * cannot be enabled, i.e. the checks or Java language access control cannot
     * be suppressed, this method returns {@code false} (as opposed to {@code
     * setAccessible(true)} throwing {@code InaccessibleObjectException} when
     * it fails).
     *
     * <p> This method is a no-op if the {@code accessible} flag for
     * this reflected object is {@code true}.
     *
     * <p> For example, a caller can invoke {@code trySetAccessible}
     * on a {@code Method} object for a private instance method
     * {@code p.T::privateMethod} to suppress the checks for Java language access
     * control when the {@code Method} is invoked.
     * If {@code p.T} class is in a different module to the caller and
     * package {@code p} is open to at least the caller's module,
     * the code below successfully sets the {@code accessible} flag
     * to {@code true}.
     *
     * <pre>
     * {@code
     *     p.T obj = ....;  // instance of p.T
     *     :
     *     Method m = p.T.class.getDeclaredMethod("privateMethod");
     *     if (m.trySetAccessible()) {
     *         m.invoke(obj);
     *     } else {
     *         // package p is not opened to the caller to access private member of T
     *         ...
     *     }
     * }</pre>
     *
     * <p> If there is a security manager, its {@code checkPermission} method
     * is first called with a {@code ReflectPermission("suppressAccessChecks")}
     * permission. </p>
     *
     * @return {@code true} if the {@code accessible} flag is set to {@code true};
     *         {@code false} if access cannot be enabled.
     * @throws SecurityException if the request is denied by the security manager
     * @throws NullPointerException if the specified accessible object is null
     *
     */
    public abstract boolean trySetAccessible(AccessibleObject accessible) throws SecurityException, NullPointerException;

    /**
     * Allocates an instance but does not run any constructor.
     * Initializes the class if it has not yet been.
     *
     * @throws InstantiationException    if the class that declares the
     *           underlying constructor represents an abstract class.
     * @throws NullPointerException    if the specified class is null
     */
    public abstract <T> T allocateInstance(Class<T> clazz) throws InstantiationException, NullPointerException;

    /**
     * Uses the constructor represented by this {@code Constructor} object to
     * create and initialize a new instance of the constructor's
     * declaring class, with the specified initialization parameters.
     * Individual parameters are automatically unwrapped to match
     * primitive formal parameters, and both primitive and reference
     * parameters are subject to method invocation conversions as necessary.
     *
     * <p>If the number of formal parameters required by the underlying constructor
     * is 0, the supplied {@code initargs} array may be of length 0 or null.
     *
     * <p>If the constructor's declaring class is an inner class in a
     * non-static context, the first argument to the constructor needs
     * to be the enclosing instance; see section 15.9.3 of
     * <cite>The Java Language Specification</cite>.
     *
     * <p>If the required access and argument checks succeed and the
     * instantiation will proceed, the constructor's declaring class
     * is initialized if it has not already been initialized.
     *
     * <p>If the constructor completes normally, returns the newly
     * created and initialized instance.
     *
     * @param args array of objects to be passed as arguments to
     * the constructor call; values of primitive types are wrapped in
     * a wrapper object of the appropriate type (e.g. a {@code float}
     * in a {@link Float Float})
     *
     * @return a new object created by calling the constructor
     * this object represents
     *
     * @throws    IllegalArgumentException  if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion; if
     *              this constructor pertains to an enum class.
     * @throws    NullPointerException if the specified constructor is null
     * @throws    InstantiationException    if the class that declares the
     *              underlying constructor represents an abstract class.
     * @throws    InvocationTargetException if the underlying constructor
     *              throws an exception.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     */
    public abstract <T> T newInstance(Constructor<T> constructor, Object... args) throws InstantiationException, InvocationTargetException,
            NullPointerException, IllegalArgumentException, ExceptionInInitializerError;

    /**
     * Gets the value of a static or instance non-primitive field.
     *
     * @param object the object to extract the non-primitive value
     * from
     * @return the value of the non-primitive field
     *
     * @throws    IllegalArgumentException  if the specified object is not
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor
     *              thereof), or if the field value is primitive.
     * @throws    NullPointerException      if the specified field is null, or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #getField(Object, Field)
     */
    public abstract Object getObjectField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Gets the value of a static or instance {@code boolean} field.
     *
     * @param object the object to extract the {@code boolean} value
     * from
     * @return the value of the {@code boolean} field
     * 
     * @throws    IllegalArgumentException  if the specified object is not
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor
     *              thereof), or if the field value cannot be
     *              converted to the type {@code boolean} by a
     *              widening conversion.
     * @throws    NullPointerException      if the specified field is null, or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #getField(Object, Field)
     */
    public abstract boolean getBooleanField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Gets the value of a static or instance {@code byte} field.
     *
     * @param object the object to extract the {@code byte} value
     * from
     * @return the value of the {@code byte} field
     *
     * @throws    IllegalArgumentException  if the specified object is not
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor
     *              thereof), or if the field value cannot be
     *              converted to the type {@code byte} by a
     *              widening conversion.
     * @throws    NullPointerException      if the specified field is null, or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #getField(Object, Field)
     */
    public abstract byte getByteField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Gets the value of a static or instance {@code char} field.
     *
     * @param object the object to extract the {@code char} value
     * from
     * @return the value of the {@code char} field
     *
     * @throws    IllegalArgumentException  if the specified object is not
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor
     *              thereof), or if the field value cannot be
     *              converted to the type {@code char} by a
     *              widening conversion.
     * @throws    NullPointerException      if the specified field is null, or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #getField(Object, Field)
     */
    public abstract char getCharField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Gets the value of a static or instance {@code short} field.
     *
     * @param object the object to extract the {@code short} value
     * from
     * @return the value of the {@code short} field
     *
     * @throws    IllegalArgumentException  if the specified object is not
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor
     *              thereof), or if the field value cannot be
     *              converted to the type {@code short} by a
     *              widening conversion.
     * @throws    NullPointerException      if the specified field is null, or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #getField(Object, Field)
     */
    public abstract short getShortField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Gets the value of a static or instance {@code int} field.
     *
     * @param object the object to extract the {@code int} value
     * from
     * @return the value of the {@code int} field
     *
     * @throws    IllegalArgumentException  if the specified object is not
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor
     *              thereof), or if the field value cannot be
     *              converted to the type {@code int} by a
     *              widening conversion.
     * @throws    NullPointerException      if the specified field is null, or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #getField(Object, Field)
     */
    public abstract int getIntField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Gets the value of a static or instance {@code long} field.
     *
     * @param object the object to extract the {@code long} value
     * from
     * @return the value of the {@code long} field
     *
     * @throws    IllegalArgumentException  if the specified object is not
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor
     *              thereof), or if the field value cannot be
     *              converted to the type {@code long} by a
     *              widening conversion.
     * @throws    NullPointerException      if the specified field is null, or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #getField(Object, Field)
     */
    public abstract long getLongField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Gets the value of a static or instance {@code float} field.
     *
     * @param object the object to extract the {@code float} value
     * from
     * @return the value of the {@code float} field
     *
     * @throws    IllegalArgumentException  if the specified object is not
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor
     *              thereof), or if the field value cannot be
     *              converted to the type {@code float} by a
     *              widening conversion.
     * @throws    NullPointerException      if the specified field is null, or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #getField(Object, Field)
     */
    public abstract float getFloatField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Gets the value of a static or instance {@code double} field.
     *
     * @param object the object to extract the {@code double} value
     * from
     * @return the value of the {@code double} field
     *
     * @throws    IllegalArgumentException  if the specified object is not
     *              an instance of the class or interface declaring the
     *              underlying field (or a subclass or implementor
     *              thereof), or if the field value cannot be
     *              converted to the type {@code double} by a
     *              widening conversion.
     * @throws    NullPointerException      if the specified field is null, or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #getField(Object, Field)
     */
    public abstract double getDoubleField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Returns the value of the field represented by this {@code Field}, on
     * the specified object. The value is automatically wrapped in an
     * object if it has a primitive type.
     *
     * <p>The underlying field's value is obtained as follows:
     *
     * <p>If the underlying field is a static field, the {@code object} argument
     * is ignored; it may be null.
     *
     * <p>Otherwise, the underlying field is an instance field.  If the
     * specified {@code object} argument is null, the method throws a
     * {@code NullPointerException}. If the specified object is not an
     * instance of the class or interface declaring the underlying
     * field, the method throws an {@code IllegalArgumentException}.
     *
     * <p>If this {@code Field} object is enforcing Java language access control, and
     * the underlying field is inaccessible, the method throws an
     * {@code IllegalAccessException}.
     * If the underlying field is static, the class that declared the
     * field is initialized if it has not already been initialized.
     *
     * <p>Otherwise, the value is retrieved from the underlying instance
     * or static field.  If the field has a primitive type, the value
     * is wrapped in an object before being returned, otherwise it is
     * returned as is.
     *
     * <p>If the field is hidden in the type of {@code object},
     * the field's value is obtained according to the preceding rules.
     *
     * @param object object from which the represented field's value is
     * to be extracted
     * @return the value of the represented field in object
     * {@code object}; primitive values are wrapped in an appropriate
     * object before being returned
     *
     * @throws    IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof).
     * @throws    NullPointerException      if the specified field is null, or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     */
    public Object getField(Object object, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (isStatic(field.getModifiers())) object = field.getDeclaringClass();
        Class<?> fieldType = field.getType();
        if (fieldType == boolean.class) return getBooleanField(object, field);
        else if (fieldType == byte.class) return getByteField(object, field);
        else if (fieldType == char.class) return getCharField(object, field);
        else if (fieldType == short.class) return getShortField(object, field);
        else if (fieldType == int.class) return getIntField(object, field);
        else if (fieldType == long.class) return getLongField(object, field);
        else if (fieldType == float.class) return getFloatField(object, field);
        else if (fieldType == double.class) return getDoubleField(object, field);
        else return getObjectField(object, field);
    }

    /**
     * Sets the value of a field as an {@code Object} on the specified object.
     *
     * @param object the object whose field should be modified
     * @param value the new value for the field of {@code object}
     * being modified
     *
     * @throws    IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof),
     *              or if an unwrapping conversion fails.
     * @throws    NullPointerException      if the specified field is null or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #setField(Object, Field, Object)
     */
    public abstract void setObjectField(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Sets the value of a field as a {@code boolean} on the specified object.
     *
     * @param object the object whose field should be modified
     * @param value the new value for the field of {@code object}
     * being modified
     *
     * @throws    IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof),
     *              or if an unwrapping conversion fails.
     * @throws    NullPointerException      if the specified field is null or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #setField(Object, Field, Object)
     */
    public abstract void setBooleanField(Object object, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Sets the value of a field as a {@code byte} on the specified object.
     *
     * @param object the object whose field should be modified
     * @param value the new value for the field of {@code object}
     * being modified
     *
     * @throws    IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof),
     *              or if an unwrapping conversion fails.
     * @throws    NullPointerException      if the specified field is null or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #setField(Object, Field, Object)
     */
    public abstract void setByteField(Object object, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Sets the value of a field as a {@code char} on the specified object.
     *
     * @param object the object whose field should be modified
     * @param value the new value for the field of {@code object}
     * being modified
     *
     * @throws    IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof),
     *              or if an unwrapping conversion fails.
     * @throws    NullPointerException      if the specified field is null or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #setField(Object, Field, Object)
     */
    public abstract void setCharField(Object object, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Sets the value of a field as a {@code short} on the specified object.
     *
     * @param object the object whose field should be modified
     * @param value the new value for the field of {@code object}
     * being modified
     *
     * @throws    IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof),
     *              or if an unwrapping conversion fails.
     * @throws    NullPointerException      if the specified field is null or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #setField(Object, Field, Object)
     */
    public abstract void setShortField(Object object, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Sets the value of a field as a {@code int} on the specified object.
     *
     * @param object the object whose field should be modified
     * @param value the new value for the field of {@code object}
     * being modified
     *
     * @throws    IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof),
     *              or if an unwrapping conversion fails.
     * @throws    NullPointerException      if the specified field is null or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #setField(Object, Field, Object)
     */
    public abstract void setIntField(Object object, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Sets the value of a field as a {@code long} on the specified object.
     *
     * @param object the object whose field should be modified
     * @param value the new value for the field of {@code object}
     * being modified
     *
     * @throws    IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof),
     *              or if an unwrapping conversion fails.
     * @throws    NullPointerException      if the specified field is null or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #setField(Object, Field, Object)
     */
    public abstract void setLongField(Object object, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Sets the value of a field as a {@code float} on the specified object.
     *
     * @param object the object whose field should be modified
     * @param value the new value for the field of {@code object}
     * being modified
     *
     * @throws    IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof),
     *              or if an unwrapping conversion fails.
     * @throws    NullPointerException      if the specified field is null or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #setField(Object, Field, Object)
     */
    public abstract void setFloatField(Object object, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Sets the value of a field as a {@code double} on the specified object.
     *
     * @param object the object whose field should be modified
     * @param value the new value for the field of {@code object}
     * being modified
     *
     * @throws    IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof),
     *              or if an unwrapping conversion fails.
     * @throws    NullPointerException      if the specified field is null or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     *
     * @see #setField(Object, Field, Object)
     */
    public abstract void setDoubleField(Object object, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Sets the field represented by this {@code Field} object on the
     * specified object argument to the specified new value. The new
     * value is automatically unwrapped if the underlying field has a
     * primitive type.
     *
     * <p>The operation proceeds as follows:
     *
     * <p>If the underlying field is static, the {@code obj} argument is
     * ignored; it may be null.
     *
     * <p>Otherwise the underlying field is an instance field.  If the
     * specified object argument is null, the method throws a
     * {@code NullPointerException}.  If the specified object argument is not
     * an instance of the class or interface declaring the underlying
     * field, the method throws an {@code IllegalArgumentException}.
     *
     * <p> Setting a final field in this way
     * is meaningful only during deserialization or reconstruction of
     * instances of classes with blank final fields, before they are
     * made available for access by other parts of a program. Use in
     * any other context may have unpredictable effects, including cases
     * in which other parts of a program continue to use the original
     * value of this field.
     *
     * <p>If the underlying field is of a primitive type, an unwrapping
     * conversion is attempted to convert the new value to a value of
     * a primitive type.  If this attempt fails, the method throws an
     * {@code IllegalArgumentException}.
     *
     * <p>If, after possible unwrapping, the new value cannot be
     * converted to the type of the underlying field by an identity or
     * widening conversion, the method throws an
     * {@code IllegalArgumentException}.
     *
     * <p>If the underlying field is static, the class that declared the
     * field is initialized if it has not already been initialized.
     *
     * <p>The field is set to the possibly unwrapped and widened new value.
     *
     * <p>If the field is hidden in the type of {@code object},
     * the field's value is set according to the preceding rules.
     *
     * @param object the object whose field should be modified
     * @param value the new value for the field of {@code object}
     * being modified
     *
     * @throws    IllegalArgumentException  if the specified object is not an
     *              instance of the class or interface declaring the underlying
     *              field (or a subclass or implementor thereof),
     *              or if an unwrapping conversion fails.
     * @throws    NullPointerException      if the specified field is null or the specified object is null
     *              and the field is an instance field.
     * @throws    ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     */
    public void setField(Object object, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        Class<?> fieldType = field.getType();
        try {
            if (fieldType == boolean.class) setBooleanField(object, field, (Boolean) value);
            else if (fieldType == byte.class) setByteField(object, field, (Byte) value);
            else if (fieldType == char.class) setCharField(object, field, (Character) value);
            else if (fieldType == short.class) setShortField(object, field, (Short) value);
            else if (fieldType == int.class) setIntField(object, field, (Integer) value);
            else if (fieldType == long.class) setLongField(object, field, (Long) value);
            else if (fieldType == float.class) setFloatField(object, field, (Float) value);
            else if (fieldType == double.class) setDoubleField(object, field, (Double) value);
            else setObjectField(object, field, value);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException  if the method is an
     *              instance method and the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the method had a return value.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method is null,
     *              or specified object is null and the method is an instance method.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     */
    public abstract void invokeVoidMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException  if the method is an
     *              instance method and the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value is primitive.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method is null,
     *              or specified object is null and the method is an instance method.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     */
    public abstract Object invokeObjectMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException  if the method is an
     *              instance method and the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code boolean} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method is null,
     *              or specified object is null and the method is an instance method.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     */
    public abstract boolean invokeBooleanMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException  if the method is an
     *              instance method and the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code byte} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method is null,
     *              or specified object is null and the method is an instance method.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     */
    public abstract byte invokeByteMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException  if the method is an
     *              instance method and the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code char} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method is null,
     *              or specified object is null and the method is an instance method.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     */
    public abstract char invokeCharMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException  if the method is an
     *              instance method and the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code short} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method is null,
     *              or specified object is null and the method is an instance method.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     */
    public abstract short invokeShortMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException  if the method is an
     *              instance method and the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code int} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method is null,
     *              or specified object is null and the method is an instance method.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     */
    public abstract int invokeIntMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException  if the method is an
     *              instance method and the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code long} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method is null,
     *              or specified object is null and the method is an instance method.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     */
    public abstract long invokeLongMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException  if the method is an
     *              instance method and the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code float} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method is null,
     *              or specified object is null and the method is an instance method.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     */
    public abstract float invokeFloatMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException  if the method is an
     *              instance method and the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code double} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method is null,
     *              or specified object is null and the method is an instance method.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     */
    public abstract double invokeDoubleMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters.
     * Individual parameters are automatically unwrapped to match
     * primitive formal parameters, and both primitive and reference
     * parameters are subject to method invocation conversions as
     * necessary.
     *
     * <p>If the underlying method is static, then the specified {@code object}
     * argument is ignored. It may be null.
     *
     * <p>If the number of formal parameters required by the underlying method is
     * 0, the supplied {@code args} array may be of length 0 or null.
     *
     * <p>If the underlying method is an instance method, it is invoked
     * using dynamic method lookup as documented in The Java Language
     * Specification, section 15.12.4.4; in particular,
     * overriding based on the runtime type of the target object may occur.
     *
     * <p>If the underlying method is static, the class that declared
     * the method is initialized if it has not already been initialized.
     *
     * <p>If the method completes normally, the value it returns is
     * returned to the caller of invoke; if the value has a primitive
     * type, it is first appropriately wrapped in an object. However,
     * if the value has the type of array of a primitive type, the
     * elements of the array are <i>not</i> wrapped in objects; in
     * other words, an array of primitive type is returned.  If the
     * underlying method return type is void, the invocation returns
     * null.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException  if the method is an
     *              instance method and the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method is null,
     *              or specified object is null and the method is an instance method.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     */
    public Object invokeMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (isStatic(method.getModifiers())) object = method.getDeclaringClass();
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class) {
            invokeVoidMethod(object, method, args);
            return null;
        }
        else if (returnType == boolean.class) return invokeBooleanMethod(object, method, args);
        else if (returnType == byte.class) return invokeByteMethod(object, method, args);
        else if (returnType == char.class) return invokeCharMethod(object, method, args);
        else if (returnType == short.class) return invokeShortMethod(object, method, args);
        else if (returnType == int.class) return invokeIntMethod(object, method, args);
        else if (returnType == long.class) return invokeLongMethod(object, method, args);
        else if (returnType == float.class) return invokeFloatMethod(object, method, args);
        else if (returnType == double.class) return invokeDoubleMethod(object, method, args);
        else return invokeObjectMethod(object, method, args);
    }

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters,
     * bypassing all overriding methods.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException if the underlying method is
     *              a static method or the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the method had a return value.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method or specified object is null.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     *
     * @see #invokeNonVirtualMethod(Object, Method, Object...)
     */
    public abstract void invokeNonVirtualVoidMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters,
     * bypassing all overriding methods.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException if the underlying method is
     *              a static method or the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value is primitive.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method or specified object is null.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     *
     * @see #invokeNonVirtualMethod(Object, Method, Object...)
     */
    public abstract Object invokeNonVirtualObjectMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters,
     * bypassing all overriding methods.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException if the underlying method is
     *              a static method or the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code boolean} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method or specified object is null.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     *
     * @see #invokeNonVirtualMethod(Object, Method, Object...)
     */
    public abstract boolean invokeNonVirtualBooleanMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters,
     * bypassing all overriding methods.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException if the underlying method is
     *              a static method or the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code byte} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method or specified object is null.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     *
     * @see #invokeNonVirtualMethod(Object, Method, Object...)
     */
    public abstract byte invokeNonVirtualByteMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters,
     * bypassing all overriding methods.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException if the underlying method is
     *              a static method or the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code char} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method or specified object is null.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     *
     * @see #invokeNonVirtualMethod(Object, Method, Object...)
     */
    public abstract char invokeNonVirtualCharMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters,
     * bypassing all overriding methods.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException if the underlying method is
     *              a static method or the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code short} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method or specified object is null.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     *
     * @see #invokeNonVirtualMethod(Object, Method, Object...)
     */
    public abstract short invokeNonVirtualShortMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters,
     * bypassing all overriding methods.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException if the underlying method is
     *              a static method or the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code int} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method or specified object is null.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     *
     * @see #invokeNonVirtualMethod(Object, Method, Object...)
     */
    public abstract int invokeNonVirtualIntMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters,
     * bypassing all overriding methods.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException if the underlying method is
     *              a static method or the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code long} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method or specified object is null.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     *
     * @see #invokeNonVirtualMethod(Object, Method, Object...)
     */
    public abstract long invokeNonVirtualLongMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters,
     * bypassing all overriding methods.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException if the underlying method is
     *              a static method or the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code float} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method or specified object is null.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     *
     * @see #invokeNonVirtualMethod(Object, Method, Object...)
     */
    public abstract float invokeNonVirtualFloatMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters,
     * bypassing all overriding methods.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException if the underlying method is
     *              a static method or the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion;
     *              or if the returned value cannot be converted to
     *              the type {@code double} by a widening conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method or specified object is null.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     *
     * @see #invokeNonVirtualMethod(Object, Method, Object...)
     */
    public abstract double invokeNonVirtualDoubleMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters.
     * Individual parameters are automatically unwrapped to match
     * primitive formal parameters, and both primitive and reference
     * parameters are subject to method invocation conversions as
     * necessary.
     *
     * <p>If the number of formal parameters required by the underlying method is
     * 0, the supplied {@code args} array may be of length 0 or null.
     *
     * <p>If the underlying method is an instance method, it is invoked
     * using special method lookup bypassing all overriding methods which
     * documented in The Java Language Specification, section 15.12.4.4;
     * in particular, overriding based on the runtime type of the target
     * object never occur.
     *
     * <p>If the method completes normally, the value it returns is
     * returned to the caller of invoke; if the value has a primitive
     * type, it is first appropriately wrapped in an object. However,
     * if the value has the type of array of a primitive type, the
     * elements of the array are <i>not</i> wrapped in objects; in
     * other words, an array of primitive type is returned.  If the
     * underlying method return type is void, the invocation returns
     * null.
     *
     * @param object  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     *
     * @throws    IllegalArgumentException if the underlying method is
     *              a static method or the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion.
     * @throws    InvocationTargetException if the underlying method
     *              throws an exception.
     * @throws    NullPointerException      if the specified method or specified object is null.
     * @throws    ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     */
    public Object invokeNonVirtualMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class) {
            invokeNonVirtualVoidMethod(object, method, args);
            return null;
        }
        else if (returnType == int.class) return invokeNonVirtualIntMethod(object, method, args);
        else if (returnType == long.class) return invokeNonVirtualLongMethod(object, method, args);
        else if (returnType == short.class) return invokeNonVirtualShortMethod(object, method, args);
        else if (returnType == char.class) return invokeNonVirtualCharMethod(object, method, args);
        else if (returnType == boolean.class) return invokeNonVirtualBooleanMethod(object, method, args);
        else if (returnType == byte.class) return invokeNonVirtualByteMethod(object, method, args);
        else if (returnType == float.class) return invokeNonVirtualFloatMethod(object, method, args);
        else if (returnType == double.class) return invokeNonVirtualDoubleMethod(object, method, args);
        else return invokeNonVirtualObjectMethod(object, method, args);
    }
    
    public void invokeVoidMethod(Object object, Method method, boolean virtual, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (virtual) invokeVoidMethod(object, method, args);
        else invokeNonVirtualBooleanMethod(object, method, args);
    }
    
    public Object invokeObjectMethod(Object object, Method method, boolean virtual, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return virtual ? invokeObjectMethod(object, method, args) : invokeNonVirtualObjectMethod(object, method, args);
    }

    public boolean invokeBooleanMethod(Object object, Method method, boolean virtual, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return virtual ? invokeBooleanMethod(object, method, args) : invokeNonVirtualBooleanMethod(object, method, args);
    }

    public byte invokeByteMethod(Object object, Method method, boolean virtual, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return virtual ? invokeByteMethod(object, method, args) : invokeNonVirtualByteMethod(object, method, args);
    }

    public char invokeCharMethod(Object object, Method method, boolean virtual, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return virtual ? invokeCharMethod(object, method, args) : invokeNonVirtualCharMethod(object, method, args);
    }

    public short invokeShortMethod(Object object, Method method, boolean virtual, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return virtual ? invokeShortMethod(object, method, args) : invokeNonVirtualShortMethod(object, method, args);
    }

    public int invokeIntMethod(Object object, Method method, boolean virtual, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return virtual ? invokeIntMethod(object, method, args) : invokeNonVirtualIntMethod(object, method, args);
    }

    public long invokeLongMethod(Object object, Method method, boolean virtual, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return virtual ? invokeLongMethod(object, method, args) : invokeNonVirtualLongMethod(object, method, args);
    }

    public float invokeFloatMethod(Object object, Method method, boolean virtual, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return virtual ? invokeFloatMethod(object, method, args) : invokeNonVirtualFloatMethod(object, method, args);
    }

    public double invokeDoubleMethod(Object object, Method method, boolean virtual, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return virtual ? invokeDoubleMethod(object, method, args) : invokeNonVirtualDoubleMethod(object, method, args);
    }

    public Object invokeMethod(Object object, Method method, boolean virtual, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return virtual ? invokeMethod(object, method, args) : invokeNonVirtualMethod(object, method, args);
    }

    public abstract Object newProxyInstance(ClassLoader classLoader, InvocationHandler handler, Class<?>... interfaces);

    public abstract <T> T newProxyInstance(ClassLoader classLoader, InvocationHandler handler, Class<T> theInterface);

    public abstract boolean isProxyClass(Class<?> clazz);

    public abstract boolean isProxyObject(Object object);

    public abstract InvocationHandler getInvocationHandler(Object object);

    public abstract void invokeDefaultVoidMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract boolean invokeDefaultBooleanMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract byte invokeDefaultByteMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract char invokeDefaultCharMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract short invokeDefaultShortMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract int invokeDefaultIntMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract long invokeDefaultLongMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract float invokeDefaultFloatMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract double invokeDefaultDoubleMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public abstract Object invokeDefaultObjectMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError;

    public Object invokeDefaultMethod(Object object, Method method, Object... args)
            throws InvocationTargetException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (isStatic(method.getModifiers())) object = method.getDeclaringClass();
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class) {
            invokeDefaultVoidMethod(object, method, args);
            return null;
        }
        else if (returnType == boolean.class) return invokeDefaultBooleanMethod(object, method, args);
        else if (returnType == byte.class) return invokeDefaultByteMethod(object, method, args);
        else if (returnType == char.class) return invokeDefaultCharMethod(object, method, args);
        else if (returnType == short.class) return invokeDefaultShortMethod(object, method, args);
        else if (returnType == int.class) return invokeDefaultIntMethod(object, method, args);
        else if (returnType == long.class) return invokeDefaultLongMethod(object, method, args);
        else if (returnType == float.class) return invokeDefaultFloatMethod(object, method, args);
        else if (returnType == double.class) return invokeDefaultDoubleMethod(object, method, args);
        else return invokeDefaultObjectMethod(object, method, args);
    }

    /**
     * Creates a new array with the specified component type and
     * length.
     * Invoking this method is equivalent to creating an array
     * as follows:
     * <blockquote>
     * <pre>
     * int[] x = {length};
     * Array.newInstance(componentType, x);
     * </pre>
     * </blockquote>
     *
     * <p>The number of dimensions of the new array must not
     * exceed 255.
     *
     * @param  componentType the {@code Class} object representing the
     *         component type of the new array
     * @param  length the length of the new array
     * @return the new array
     * @throws NullPointerException if the specified
     *         {@code componentType} parameter is null
     * @throws IllegalArgumentException if componentType is {@link
     *         Void#TYPE} or if the number of dimensions of the requested array
     *         instance exceed 255.
     * @throws NegativeArraySizeException if the specified {@code length}
     *         is negative
     */
    @SuppressWarnings("unchecked")
    public <T> T[] newArrayInstance(Class<T> componentType, int length) throws NegativeArraySizeException {
        return (T[]) Array.newInstance(componentType, length);
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(Class<T> arrayType, int length) throws NegativeArraySizeException {
        return (T) Array.newInstance(FastArray.getComponentType(arrayType), length);
    }

    /**
     * Creates a new array
     * with the specified component type and dimensions.
     * If {@code componentType}
     * represents a non-array class or interface, the new array
     * has {@code dimensions.length} dimensions and
     * {@code componentType} as its component type. If
     * {@code componentType} represents an array class, the
     * number of dimensions of the new array is equal to the sum
     * of {@code dimensions.length} and the number of
     * dimensions of {@code componentType}. In this case, the
     * component type of the new array is the component type of
     * {@code componentType}.
     *
     * <p>The number of dimensions of the new array must not
     * exceed 255.
     *
     * @param componentType the {@code Class} object representing the component
     * type of the new array
     * @param dimensions an array of {@code int} representing the dimensions of
     * the new array
     * @return the new array
     * @throws    NullPointerException if the specified
     * {@code componentType} argument is null
     * @throws    IllegalArgumentException if the specified {@code dimensions}
     * argument is a zero-dimensional array, if componentType is {@link
     * Void#TYPE}, or if the number of dimensions of the requested array
     * instance exceed 255.
     * @throws    NegativeArraySizeException if any of the components in
     * the specified {@code dimensions} argument is negative.
     */
    public Object newArrayInstance(Class<?> componentType, int... dimensions) throws IllegalArgumentException, NegativeArraySizeException {
        return Array.newInstance(componentType, dimensions);
    }

    /**
     * Returns the length of the specified array object, as an {@code int}.
     *
     * @param array the array
     * @return the length of the array
     * @throws    IllegalArgumentException if the object argument is not
     * an array
     */
    public int getArrayLength(Object array) throws IllegalArgumentException {
        return Array.getLength(array);
    }

    public int getArrayDimensions(Object array) throws IllegalArgumentException {
        Class<?> clazz = array.getClass();
        if (!clazz.isArray()) throw new IllegalArgumentException("not an array");
        int dimensions = 0;
        while (clazz.isArray()) {
            dimensions ++;
            clazz = clazz.getComponentType();
        }
        return dimensions;
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object.  The value is automatically wrapped in an object
     * if it has a primitive type.
     *
     * @param array the array
     * @param index the index
     * @return the (possibly wrapped) value of the indexed component in
     * the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     */
    public Object getArrayElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return Array.get(array, index);
    }

    public Object getArrayObjectElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray()) return Array.get(array, index);
        else if (array.getClass().getComponentType().isPrimitive()) throw new IllegalArgumentException("Illegal array type; expected non-primitive");
        return Array.get(array, index);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code boolean}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see #getArrayElement(Object, int)
     */
    public boolean getArrayBooleanElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return Array.getBoolean(array, index);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code byte}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see #getArrayElement(Object, int)
     */
    public byte getArrayByteElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return Array.getByte(array, index);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code char}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see #getArrayElement(Object, int)
     */
    public char getArrayCharElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return Array.getChar(array, index);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code short}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see #getArrayElement(Object, int)
     */
    public short getArrayShortElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return Array.getShort(array, index);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as an {@code int}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see #getArrayElement(Object, int)
     */
    public int getArrayIntElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return Array.getInt(array, index);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code long}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see #getArrayElement(Object, int)
     */
    public long getArrayLongElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return Array.getLong(array, index);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code float}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see #getArrayElement(Object, int)
     */
    public float getArrayFloatElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return Array.getFloat(array, index);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code double}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see #getArrayElement(Object, int)
     */
    public double getArrayDoubleElement(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return Array.getDouble(array, index);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified new value.  The new value is first
     * automatically unwrapped if the array has a primitive component
     * type.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the array component type is primitive and
     * an unwrapping conversion fails
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     */
    public void setArrayElement(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        Array.set(array, index, value);
    }

    public void setArrayObjectElement(Object array, int index, Object value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (array == null || !array.getClass().isArray() || value == null) Array.set(array, index, null);
        else {
            Class<?> componentType = array.getClass().getComponentType();
            if (componentType.isPrimitive()) throw new IllegalArgumentException("Illegal array type; expected non-primitive");
            //else if (!componentType.isInstance(value)) throw new IllegalArgumentException("Illegal value type; expected " + componentType);
            else Array.set(array, index, value);
        }
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code boolean} value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see #setArrayElement(Object, int, Object)
     */
    public void setArrayBooleanElement(Object array, int index, boolean value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        Array.setBoolean(array, index, value);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code byte} value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see #setArrayElement(Object, int, Object)
     */
    public void setArrayByteElement(Object array, int index, byte value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        Array.setByte(array, index, value);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code char} value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see #setArrayElement(Object, int, Object)
     */
    public void setArrayCharElement(Object array, int index, char value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        Array.setChar(array, index, value);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code short} value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see #setArrayElement(Object, int, Object)
     */
    public void setArrayShortElement(Object array, int index, short value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        Array.setShort(array, index, value);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code int} value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see #setArrayElement(Object, int, Object)
     */
    public void setArrayIntElement(Object array, int index, int value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        Array.setInt(array, index, value);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code long} value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see #setArrayElement(Object, int, Object)
     */
    public void setArrayLongElement(Object array, int index, long value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        Array.setLong(array, index, value);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code float} value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see #setArrayElement(Object, int, Object)
     */
    public void setArrayFloatElement(Object array, int index, float value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        Array.setFloat(array, index, value);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code double} value.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see #setArrayElement(Object, int, Object)
     */
    public void setArrayDoubleElement(Object array, int index, double value) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        Array.setDouble(array, index, value);
    }

    public abstract Class<?> getCallerClass();
    public abstract String getCallerMethod();

    public void initialize(Class<?> clazz) {
        try {
            Class.forName(clazz.getName(), true, clazz.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new UnexpectedError(e);
        }
    }
    public abstract void ensureInitialized(Class<?> clazz);
    public abstract boolean shouldBeInitialized(Class<?> clazz);

}
