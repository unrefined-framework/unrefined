package unrefined.util.foreign;

import unrefined.context.Environment;
import unrefined.nio.Pointer;
import unrefined.util.function.VarFunctor;
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
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.foreign", Foreign.class);
        }
        return INSTANCE;
    }

    public Aggregate.Descriptor getEmptyDescriptor() {
        return Aggregate.declareEmpty();
    }

    public Aggregate.Descriptor createDescriptor(Object[] memberTypes, long[] memberOffsets, long[] memberRepetitions, long padding) {
        return Aggregate.declare(memberTypes, memberOffsets, memberRepetitions, padding);
    }

    public Aggregate.Descriptor createDescriptor(Object[] memberTypes, long[] memberOffsets, long padding) {
        return Aggregate.declare(memberTypes, memberOffsets, padding);
    }

    public Aggregate.Descriptor createDescriptor(Object[] memberTypes, int memberTypesOffset,
                                               long[] memberOffsets, int memberOffsetsOffset,
                                               long[] memberRepetitions, int memberRepetitionsOffset,
                                               int length, long padding) {
        return Aggregate.declare(memberTypes, memberTypesOffset, memberOffsets, memberOffsetsOffset, memberRepetitions, memberRepetitionsOffset, length, padding);
    }

    public Aggregate.Descriptor createDescriptor(Object[] memberTypes, int memberTypesOffset,
                                               long[] memberOffsets, int memberOffsetsOffset,
                                               int length, long padding) {
        return Aggregate.declare(memberTypes, memberTypesOffset, memberOffsets, memberOffsetsOffset, length, padding);
    }

    public Aggregate.Descriptor createStructDescriptor(Object[] memberTypes, int memberTypesOffset,
                                                     long[] memberRepetitions, int memberRepetitionsOffset,
                                                     int length) {
        return Aggregate.declareStruct(memberTypes, memberTypesOffset, memberRepetitions, memberRepetitionsOffset, length);
    }

    public Aggregate.Descriptor createStructDescriptor(Object[] memberTypes, int memberTypesOffset, int length) {
        return Aggregate.declareStruct(memberTypes, memberTypesOffset, length);
    }

    public Aggregate.Descriptor createStructDescriptor(Object[] memberTypes, long[] memberRepetitions) {
        return Aggregate.declareStruct(memberTypes, memberRepetitions);
    }

    public Aggregate.Descriptor createStructDescriptor(Object... memberTypes) {
        return Aggregate.declareStruct(memberTypes);
    }

    public Aggregate.Descriptor createUnionDescriptor(Object[] memberTypes, int memberTypesOffset,
                                                    long[] memberRepetitions, int memberRepetitionsOffset,
                                                    int length) {
        return Aggregate.declareUnion(memberTypes, memberTypesOffset, memberRepetitions, memberRepetitionsOffset, length);
    }

    public Aggregate.Descriptor createUnionDescriptor(Object[] memberTypes, int memberTypesOffset, int length) {
        return Aggregate.declareUnion(memberTypes, memberTypesOffset, length);
    }

    public Aggregate.Descriptor createUnionDescriptor(Object[] memberTypes, long[] memberRepetitions) {
        return Aggregate.declareUnion(memberTypes, memberRepetitions);
    }

    public Aggregate.Descriptor createUnionDescriptor(Object... memberTypes) {
        return Aggregate.declareUnion(memberTypes);
    }

    public Aggregate createProxyAggregateInstance(Aggregate.Descriptor descriptor, Pointer memory) {
        return Aggregate.newProxyInstance(descriptor, memory);
    }

    public <T extends Aggregate> T createAggregateInstance(Class<T> clazz, Pointer memory) {
        return Aggregate.newInstance(clazz, memory);
    }

    public boolean isIncompleteAggregateClass(Class<? extends Aggregate> clazz) {
        return Aggregate.isIncompleteClass(clazz);
    }

    public boolean isProxyAggregateObject(Aggregate object) {
        return Aggregate.isProxyObject(object);
    }

    public boolean isProxyAggregateClass(Class<? extends Aggregate> clazz) {
        return Aggregate.isProxyClass(clazz);
    }

    public boolean isIncompleteAggregateObject(Aggregate object) {
        return Aggregate.isIncompleteObject(object);
    }

    public Aggregate.Descriptor descriptorOf(Class<? extends Aggregate> clazz) {
        return Aggregate.descriptorOf(clazz);
    }
    public long sizeOfType(Class<? extends Aggregate> clazz) {
        return Aggregate.sizeOfType(clazz);
    }
    public long sizeOf(Aggregate aggregate) {
        return Aggregate.sizeOf(aggregate);
    }

    public abstract <T extends Library> T downcallProxy(int options, Class<T> clazz, ClassLoader loader);
    public <T extends Library> T downcallProxy(Class<T> clazz, ClassLoader loader) {
        return downcallProxy(Symbol.Option.DEFAULT, clazz, loader);
    }
    public <T extends Library> T downcallProxy(int options, Class<T> clazz) {
        return downcallProxy(options, clazz, Reflection.getInstance().getCallerClass().getClassLoader());
    }
    public <T extends Library> T downcallProxy(Class<T> clazz) {
        return downcallProxy(Symbol.Option.DEFAULT, clazz);
    }

    public abstract Symbol downcallHandle(int options, long function, Object returnType, Object... parameterTypes);
    public Symbol downcallHandle(long function, Object returnType, Object... parameterTypes) {
        return downcallHandle(Symbol.Option.DEFAULT, function, returnType, parameterTypes);
    }

    public abstract Symbol upcallStub(int options, Object object, Method method, Object returnType, Object... parameterTypes);
    public Symbol upcallStub(Object object, Method method, Object returnType, Object... parameterTypes) {
        return upcallStub(Symbol.Option.DEFAULT, object, method, returnType, parameterTypes);
    }
    public Symbol upcallStub(int options, Method method, Object returnType, Object... parameterTypes) {
        if (!Modifier.isStatic(method.getModifiers())) throw new IllegalArgumentException("Illegal method modifier; expected static");
        return upcallStub(options, null, method, returnType, parameterTypes);
    }
    public Symbol upcallStub(Method method, Object returnType, Object... parameterTypes) {
        return upcallStub(Symbol.Option.DEFAULT, method, returnType, parameterTypes);
    }
    public abstract Symbol upcallStub(int options, VarFunctor<?> closure, Object returnType, Object... parameterTypes);
    public Symbol upcallStub(VarFunctor<?> closure, Object returnType, Object... parameterTypes) {
        return upcallStub(Symbol.Option.DEFAULT, closure, returnType, parameterTypes);
    }

    public abstract void invokeVoidFunction(int options, long address, Object... args);
    public void invokeVoidFunction(long address, Object... args) {
        invokeVoidFunction(Symbol.Option.DEFAULT, address, args);
    }
    public abstract boolean invokeBooleanFunction(int options, long address, Object... args);
    public boolean invokeBooleanFunction(long address, Object... args) {
        return invokeBooleanFunction(Symbol.Option.DEFAULT, address, args);
    }
    public abstract byte invokeByteFunction(int options, long address, Object... args);
    public byte invokeByteFunction(long address, Object... args) {
        return invokeByteFunction(Symbol.Option.DEFAULT, address, args);
    }
    public abstract char invokeCharFunction(int options, long address, Object... args);
    public char invokeCharFunction(long address, Object... args) {
        return invokeCharFunction(Symbol.Option.DEFAULT, address, args);
    }
    public abstract short invokeShortFunction(int options, long address, Object... args);
    public short invokeShortFunction(long address, Object... args) {
        return invokeShortFunction(Symbol.Option.DEFAULT, address, args);
    }
    public abstract int invokeIntFunction(int options, long address, Object... args);
    public int invokeIntFunction(long address, Object... args) {
        return invokeIntFunction(Symbol.Option.DEFAULT, address, args);
    }
    public abstract long invokeNativeIntFunction(int options, long address, Object... args);
    public long invokeNativeIntFunction(long address, Object... args) {
        return invokeNativeIntFunction(Symbol.Option.DEFAULT, address, args);
    }
    public abstract long invokeLongFunction(int options, long address, Object... args);
    public long invokeLongFunction(long address, Object... args) {
        return invokeLongFunction(Symbol.Option.DEFAULT, address, args);
    }
    public abstract long invokeNativeLongFunction(int options, long address, Object... args);
    public long invokeNativeLongFunction(long address, Object... args) {
        return invokeNativeLongFunction(Symbol.Option.DEFAULT, address, args);
    }
    public abstract float invokeFloatFunction(int options, long address, Object... args);
    public float invokeFloatFunction(long address, Object... args) {
        return invokeFloatFunction(Symbol.Option.DEFAULT, address, args);
    }
    public abstract double invokeDoubleFunction(int options, long address, Object... args);
    public double invokeDoubleFunction(long address, Object... args) {
        return invokeDoubleFunction(Symbol.Option.DEFAULT, address, args);
    }
    public abstract long invokeAddressFunction(int options, long address, Object... args);
    public long invokeAddressFunction(long address, Object... args) {
        return invokeAddressFunction(Symbol.Option.DEFAULT, address, args);
    }
    public abstract <T extends Aggregate> T invokeAggregateFunction(int options, long address, Class<T> returnType, Object... args);
    public <T extends Aggregate> T invokeAggregateFunction(long address, Class<T> returnType, Object... args) {
        return invokeAggregateFunction(Symbol.Option.DEFAULT, address, returnType, args);
    }
    public abstract Aggregate invokeDescriptorFunction(int options, long address, Aggregate.Descriptor returnType, Object... args);
    public Aggregate invokeDescriptorFunction(long address, Aggregate.Descriptor returnType, Object... args) {
        return invokeDescriptorFunction(Symbol.Option.DEFAULT, address, returnType, args);
    }
    public abstract Object invokeFunction(int options, long address, Object returnType, Object... args);
    public Object invokeFunction(long address, Object returnType, Object... args) {
        return invokeFunction(Symbol.Option.DEFAULT, address, returnType, args);
    }

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

    public abstract Charset systemCharset();
    public abstract int systemCharSize();

    public abstract Charset wideCharset();
    public abstract int wideCharSize();

    public abstract int getLastError();
    public abstract void setLastError(int errno);

    public abstract String getErrorString(int errno);

}
