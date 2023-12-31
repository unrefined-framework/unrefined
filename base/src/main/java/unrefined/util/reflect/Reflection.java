package unrefined.util.reflect;

import unrefined.context.Environment;

import java.lang.reflect.AccessibleObject;
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
            if (INSTANCE == null) INSTANCE = Environment.global().get("unrefined.runtime.reflection", Reflection.class);
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

    public Object getObjectField(Object object, boolean forceVolatile, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return forceVolatile ? getObjectFieldVolatile(object, field) : getObjectField(object, field);
    }

    public boolean getBooleanField(Object object, boolean forceVolatile, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return forceVolatile ? getBooleanFieldVolatile(object, field) : getBooleanField(object, field);
    }

    public byte getByteField(Object object, boolean forceVolatile, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return forceVolatile ? getByteFieldVolatile(object, field) : getByteField(object, field);
    }

    public char getCharField(Object object, boolean forceVolatile, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return forceVolatile ? getCharFieldVolatile(object, field) : getCharField(object, field);
    }

    public short getShortField(Object object, boolean forceVolatile, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return forceVolatile ? getShortFieldVolatile(object, field) : getShortField(object, field);
    }

    public int getIntField(Object object, boolean forceVolatile, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return forceVolatile ? getIntFieldVolatile(object, field) : getIntField(object, field);
    }

    public long getLongField(Object object, boolean forceVolatile, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return forceVolatile ? getLongFieldVolatile(object, field) : getLongField(object, field);
    }

    public float getFloatField(Object object, boolean forceVolatile, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return forceVolatile ? getFloatFieldVolatile(object, field) : getFloatField(object, field);
    }

    public double getDoubleField(Object object, boolean forceVolatile, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return forceVolatile ? getDoubleFieldVolatile(object, field) : getDoubleField(object, field);
    }

    public Object getField(Object object, boolean forceVolatile, Field field) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        return forceVolatile ? getFieldVolatile(object, field) : getField(object, field);
    }

    public void setObjectField(Object object, boolean forceVolatile, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (forceVolatile) setObjectFieldVolatile(object, field, value);
        else setObjectField(object, field, value);
    }

    public void setBooleanField(Object object, boolean forceVolatile, Field field, boolean value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (forceVolatile) setBooleanFieldVolatile(object, field, value);
        else setBooleanField(object, field, value);
    }

    public void setByteField(Object object, boolean forceVolatile, Field field, byte value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (forceVolatile) setByteFieldVolatile(object, field, value);
        else setByteField(object, field, value);
    }

    public void setCharField(Object object, boolean forceVolatile, Field field, char value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (forceVolatile) setCharFieldVolatile(object, field, value);
        else setCharField(object, field, value);
    }

    public void setShortField(Object object, boolean forceVolatile, Field field, short value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (forceVolatile) setShortFieldVolatile(object, field, value);
        else setShortField(object, field, value);
    }

    public void setIntField(Object object, boolean forceVolatile, Field field, int value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (forceVolatile) setIntFieldVolatile(object, field, value);
        else setIntField(object, field, value);
    }

    public void setLongField(Object object, boolean forceVolatile, Field field, long value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (forceVolatile) setLongFieldVolatile(object, field, value);
        else setLongField(object, field, value);
    }

    public void setFloatField(Object object, boolean forceVolatile, Field field, float value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (forceVolatile) setFloatFieldVolatile(object, field, value);
        else setFloatField(object, field, value);
    }

    public void setDoubleField(Object object, boolean forceVolatile, Field field, double value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (forceVolatile) setDoubleFieldVolatile(object, field, value);
        else setDoubleField(object, field, value);
    }

    public void setField(Object object, boolean forceVolatile, Field field, Object value) throws IllegalArgumentException, NullPointerException, ExceptionInInitializerError {
        if (forceVolatile) setFieldVolatile(object, field, value);
        else setField(object, field, value);
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

    public abstract Class<?> getCallerClass();
    public abstract String getCallerMethod();

}
