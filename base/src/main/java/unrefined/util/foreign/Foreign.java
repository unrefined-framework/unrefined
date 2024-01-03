package unrefined.util.foreign;

import unrefined.context.Environment;
import unrefined.util.reflect.Reflection;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;

public abstract class Foreign {

    private static volatile Foreign INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Foreign getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global().get("unrefined.runtime.foreign", Foreign.class);
        }
        return INSTANCE;
    }

    public abstract void register(Class<?> clazz);
    public abstract void unregister(Class<?> clazz);

    public abstract <T extends Library> T downcallProxy(ClassLoader loader, Class<T> clazz);
    public <T extends Library> T downcallProxy(Class<T> clazz) {
        return downcallProxy(Reflection.getInstance().getCallerClass().getClassLoader(), clazz);
    }

    public abstract Symbol downcallHandle(long function, Class<?> returnType, Class<?>... parameterTypes);

    public abstract Symbol upcallStub(Object object, Method method, Class<?> returnType, Class<?>... parameterTypes);
    public Symbol upcallStub(Method method, Class<?> returnType, Class<?>... parameterTypes) {
        if (!Modifier.isStatic(method.getModifiers())) throw new IllegalArgumentException("Illegal method modifier; expected static");
        return upcallStub(null, method, returnType, parameterTypes);
    }

    public abstract void invokeVoidFunction(long address, Object... args);
    public abstract boolean invokeBooleanFunction(long address, Object... args);
    public abstract byte invokeByteFunction(long address, Object... args);
    public abstract char invokeCharFunction(long address, Object... args);
    public abstract short invokeShortFunction(long address, Object... args);
    public abstract int invokeIntFunction(long address, Object... args);
    public abstract long invokeNativeIntFunction(long address, Object... args);
    public abstract long invokeLongFunction(long address, Object... args);
    public abstract long invokeNativeLongFunction(long address, Object... args);
    public abstract float invokeFloatFunction(long address, Object... args);
    public abstract double invokeDoubleFunction(long address, Object... args);
    public abstract long invokeAddressFunction(long address, Object... args);
    public abstract <T> T invokeFunction(long address, Class<T> returnType, Object... args);

    public static final class Loader {
        public static final int CLASS  = 0;
        public static final int LINKER = 1;
        public static boolean isValid(int type) {
            return type >= CLASS && type <= LINKER;
        }
        public static int checkValid(int type) {
            if (type < CLASS || type > LINKER) throw new IllegalArgumentException("Illegal symbol loader: " + type);
            else return type;
        }
        public static String toString(int type) {
            switch (type) {
                case CLASS: return "CLASS";
                case LINKER: return "LINKER";
                default: throw new IllegalArgumentException("Illegal symbol loader: " + type);
            }
        }
    }

    public abstract void loadLibrary(String name, int loader) throws IOException;
    public abstract void loadLibrary(File file, int loader) throws IOException;

    public abstract String mapLibraryName(String name);

    public abstract long getSymbolAddress(String name) throws UnsatisfiedLinkError;

    /**
     * Gets the native {@code int} size in bytes.
     * @return the native {@code int} size
     */
    public abstract int nativeIntSize();

    /**
     * Gets the native {@code long} size in bytes.
     * @return the native {@code long} size
     */
    public abstract int nativeLongSize();

    /**
     * Gets the native address size in bytes.
     * @return the native address size
     */
    public abstract int addressSize();

    public Class<?> nativeIntClass() {
        return nativeIntSize() == 8 ? long.class : int.class;
    }

    public Class<?> nativeLongClass() {
        return nativeLongSize() == 8 ? long.class : int.class;
    }

    public Class<?> addressClass() {
        return addressSize() == 8 ? long.class : int.class;
    }

    /**
     * Gets the size in bytes of a native memory page (whatever that is).
     * This value will always be a power of two.
     */
    public abstract int memoryPageSize();

    public abstract int arrayIndexScale(Class<?> clazz);

    public abstract Charset systemCharset();
    public abstract int systemCharSize();

    public abstract Charset wideCharset();
    public abstract int wideCharSize();

    public abstract int getLastError();
    public abstract void setLastError(int errno);

}
