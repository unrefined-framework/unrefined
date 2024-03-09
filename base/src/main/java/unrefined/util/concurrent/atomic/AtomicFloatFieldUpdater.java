package unrefined.util.concurrent.atomic;

import unrefined.util.reflect.Reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A reflection-based utility that enables atomic updates to
 * designated {@code volatile float} fields of designated classes.
 * This class is designed for use in atomic data structures in which
 * several fields of the same node are independently subject to atomic
 * updates.
 *
 * <p>Note that the guarantees of the {@code compareAndSet}
 * method in this class are weaker than in other atomic classes.
 * Because this class cannot ensure that all uses of the field
 * are appropriate for purposes of atomic access, it can
 * guarantee atomicity only with respect to other invocations of
 * {@code compareAndSet} and {@code set} on the same updater.
 *
 * <p>Object arguments for parameters of type {@code T} that are not
 * instances of the class passed to {@link #newUpdater} will result in
 * a {@link ClassCastException} being thrown.
 *
 * @author Doug Lea
 * @param <T> The type of the object holding the updatable field
 */
public abstract class AtomicFloatFieldUpdater<T> {
    /**
     * Creates and returns an updater for objects with the given field.
     * The Class argument is needed to check that reflective types and
     * generic types match.
     *
     * @param tclass the class of the objects holding the field
     * @param fieldName the name of the field to be updated
     * @param <U> the type of instances of tclass
     * @return the updater
     * @throws IllegalArgumentException if the field is not a
     * volatile float type
     * @throws RuntimeException with a nested reflection-based
     * exception if the class does not hold field or is the wrong type,
     * or the field is inaccessible to the caller according to Java language
     * access control
     */
    public static <U> AtomicFloatFieldUpdater<U> newUpdater(Class<U> tclass,
                                                            String fieldName) {
        return new AtomicFloatFieldUpdaterImpl<U>
            (tclass, fieldName, Reflection.getInstance().getCallerClass());
    }

    /**
     * Protected do-nothing constructor for use by subclasses.
     */
    protected AtomicFloatFieldUpdater() {
    }

    /**
     * Atomically sets the field of the given object managed by this updater
     * to the given updated value if the current value {@code ==} the
     * expected value. This method is guaranteed to be atomic with respect to
     * other calls to {@code compareAndSet} and {@code set}, but not
     * necessarily with respect to other changes in the field.
     *
     * @param obj An object whose field to conditionally set
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful
     */
    public abstract boolean compareAndSet(T obj, float expect, float update);

    /**
     * Atomically sets the field of the given object managed by this updater
     * to the given updated value if the current value {@code ==} the
     * expected value. This method is guaranteed to be atomic with respect to
     * other calls to {@code compareAndSet} and {@code set}, but not
     * necessarily with respect to other changes in the field.
     *
     * <p>This operation may fail spuriously and does not provide
     * ordering guarantees, so is only rarely an appropriate
     * alternative to {@code compareAndSet}.
     *
     * @param obj An object whose field to conditionally set
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful
     */
    public abstract boolean weakCompareAndSet(T obj, float expect, float update);

    /**
     * Sets the field of the given object managed by this updater to the
     * given updated value. This operation is guaranteed to act as a volatile
     * store with respect to subsequent invocations of {@code compareAndSet}.
     *
     * @param obj An object whose field to set
     * @param newValue the new value
     */
    public abstract void set(T obj, float newValue);

    /**
     * Eventually sets the field of the given object managed by this
     * updater to the given updated value.
     *
     * @param obj An object whose field to set
     * @param newValue the new value
     */
    public abstract void lazySet(T obj, float newValue);

    /**
     * Returns the current value held in the field of the given object
     * managed by this updater.
     *
     * @param obj An object whose field to get
     * @return the current value
     */
    public abstract float get(T obj);

    /**
     * Atomically sets the field of the given object managed by this updater
     * to the given value and returns the old value.
     *
     * @param obj An object whose field to get and set
     * @param newValue the new value
     * @return the previous value
     */
    public float getAndSet(T obj, float newValue) {
        float prev;
        do {
            prev = get(obj);
        } while (!compareAndSet(obj, prev, newValue));
        return prev;
    }

    /**
     * Atomically increments by one the current value of the field of the
     * given object managed by this updater.
     *
     * @param obj An object whose field to get and set
     * @return the previous value
     */
    public float getAndIncrement(T obj) {
        float prev, next;
        do {
            prev = get(obj);
            next = prev + 1;
        } while (!compareAndSet(obj, prev, next));
        return prev;
    }

    /**
     * Atomically decrements by one the current value of the field of the
     * given object managed by this updater.
     *
     * @param obj An object whose field to get and set
     * @return the previous value
     */
    public float getAndDecrement(T obj) {
        float prev, next;
        do {
            prev = get(obj);
            next = prev - 1;
        } while (!compareAndSet(obj, prev, next));
        return prev;
    }

    /**
     * Atomically adds the given value to the current value of the field of
     * the given object managed by this updater.
     *
     * @param obj An object whose field to get and set
     * @param delta the value to add
     * @return the previous value
     */
    public float getAndAdd(T obj, float delta) {
        float prev, next;
        do {
            prev = get(obj);
            next = prev + delta;
        } while (!compareAndSet(obj, prev, next));
        return prev;
    }

    /**
     * Atomically increments by one the current value of the field of the
     * given object managed by this updater.
     *
     * @param obj An object whose field to get and set
     * @return the updated value
     */
    public float incrementAndGet(T obj) {
        float prev, next;
        do {
            prev = get(obj);
            next = prev + 1;
        } while (!compareAndSet(obj, prev, next));
        return next;
    }

    /**
     * Atomically decrements by one the current value of the field of the
     * given object managed by this updater.
     *
     * @param obj An object whose field to get and set
     * @return the updated value
     */
    public float decrementAndGet(T obj) {
        float prev, next;
        do {
            prev = get(obj);
            next = prev - 1;
        } while (!compareAndSet(obj, prev, next));
        return next;
    }

    /**
     * Atomically adds the given value to the current value of the field of
     * the given object managed by this updater.
     *
     * @param obj An object whose field to get and set
     * @param delta the value to add
     * @return the updated value
     */
    public float addAndGet(T obj, float delta) {
        float prev, next;
        do {
            prev = get(obj);
            next = prev + delta;
        } while (!compareAndSet(obj, prev, next));
        return next;
    }

    private static final class AtomicFloatFieldUpdaterImpl<T>
        extends AtomicFloatFieldUpdater<T> {
        private static final Atomic ATOMIC = Atomic.getInstance();
        private final Field field;
        /**
         * if field is protected, the subclass constructing updater, else
         * the same as tclass
         */
        private final Class<?> cclass;
        /** class holding the field */
        private final Class<T> tclass;

        AtomicFloatFieldUpdaterImpl(final Class<T> tclass,
                                    final String fieldName,
                                    final Class<?> caller) {
            final int modifiers;
            try {
                field = tclass.getDeclaredField(fieldName);
                modifiers = field.getModifiers();
                field.setAccessible(true);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            if (field.getType() != float.class)
                throw new IllegalArgumentException("Must be float type");

            if (!Modifier.isVolatile(modifiers))
                throw new IllegalArgumentException("Must be volatile type");

            // Access to protected field members is restricted to receivers only
            // of the accessing class, or one of its subclasses, and the
            // accessing class must in turn be a subclass (or package sibling)
            // of the protected member's defining class.
            // If the updater refers to a protected field of a declaring class
            // outside the current package, the receiver argument will be
            // narrowed to the type of the accessing class.
            this.cclass = (Modifier.isProtected(modifiers) &&
                    tclass.isAssignableFrom(caller) &&
                    !isSamePackage(tclass, caller))
                    ? caller : tclass;
            this.tclass = tclass;
        }

        /**
         * Returns true if the second classloader can be found in the first
         * classloader's delegation chain.
         * Equivalent to the inaccessible: first.isAncestor(second).
         */
        private static boolean isAncestor(ClassLoader first, ClassLoader second) {
            ClassLoader acl = first;
            do {
                acl = acl.getParent();
                if (second == acl) {
                    return true;
                }
            } while (acl != null);
            return false;
        }

        /**
         * Returns true if the two classes have the same class loader and
         * package qualifier
         */
        private static boolean isSamePackage(Class<?> class1, Class<?> class2) {
            return class1.getClassLoader() == class2.getClassLoader()
                   && getPackageName(class1).equals(getPackageName(class2));
        }

        private static String getPackageName(Class<?> clazz) {
            Class<?> c = clazz.isArray() ? elementType(clazz) : clazz;
            if (c.isPrimitive()) {
                return "java.lang";
            } else {
                String cn = c.getName();
                int dot = cn.lastIndexOf('.');
                return (dot != -1) ? cn.substring(0, dot).intern() : "";
            }
        }

        private static Class<?> elementType(Class<?> clazz) {
            if (!clazz.isArray()) return null;

            Class<?> c = clazz;
            while (c.isArray()) {
                c = c.getComponentType();
            }
            return c;
        }

        /**
         * Checks that target argument is instance of cclass.  On
         * failure, throws cause.
         */
        private final void accessCheck(T obj) {
            if (!cclass.isInstance(obj))
                throwAccessCheckException(obj);
        }

        /**
         * Throws access exception if accessCheck failed due to
         * protected access, else ClassCastException.
         */
        private final void throwAccessCheckException(T obj) {
            if (cclass == tclass)
                throw new ClassCastException();
            else
                throw new RuntimeException(
                    new IllegalAccessException(
                        "Class " +
                        cclass.getName() +
                        " can not access a protected member of class " +
                        tclass.getName() +
                        " using an instance of " +
                        obj.getClass().getName()));
        }

        public final boolean compareAndSet(T obj, float expect, float update) {
            accessCheck(obj);
            return ATOMIC.compareAndSetFloatField(obj, field, expect, update);
        }

        public final boolean weakCompareAndSet(T obj, float expect, float update) {
            accessCheck(obj);
            return ATOMIC.compareAndSetFloatField(obj, field, expect, update);
        }

        public final void set(T obj, float newValue) {
            accessCheck(obj);
            ATOMIC.setFloatFieldVolatile(obj, field, newValue);
        }

        public final void lazySet(T obj, float newValue) {
            accessCheck(obj);
            ATOMIC.lazySetFloatField(obj, field, newValue);
        }

        public final float get(T obj) {
            accessCheck(obj);
            return ATOMIC.getFloatFieldVolatile(obj, field);
        }

        public final float getAndSet(T obj, float newValue) {
            accessCheck(obj);
            return ATOMIC.getAndSetFloatField(obj, field, newValue);
        }

        public final float getAndAdd(T obj, float delta) {
            accessCheck(obj);
            return ATOMIC.getAndAddFloatField(obj, field, delta);
        }

        public final float getAndIncrement(T obj) {
            return getAndAdd(obj, 1);
        }

        public final float getAndDecrement(T obj) {
            return getAndAdd(obj, -1);
        }

        public final float incrementAndGet(T obj) {
            return getAndAdd(obj, 1) + 1;
        }

        public final float decrementAndGet(T obj) {
            return getAndAdd(obj, -1) - 1;
        }

        public final float addAndGet(T obj, float delta) {
            return getAndAdd(obj, delta) + delta;
        }

    }

}
