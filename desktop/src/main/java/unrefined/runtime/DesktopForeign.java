package unrefined.runtime;

import unrefined.desktop.ForeignSupport;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Library;
import unrefined.util.foreign.Symbol;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

public class DesktopForeign extends Foreign {

    @Override
    public void register(Class<?> clazz) {
        ForeignSupport.register(clazz);
    }

    @Override
    public void unregister(Class<?> clazz) {
        ForeignSupport.unregister(clazz);
    }

    @Override
    public <T extends Library> T downcallProxy(ClassLoader loader, Class<T> clazz) {
        return ForeignSupport.downcallProxy(loader, clazz);
    }

    @Override
    public Symbol downcallHandle(long function, Class<?> returnType, Class<?>... parameterTypes) {
        return ForeignSupport.downcallHandle(function, returnType, parameterTypes);
    }

    @Override
    public Symbol upcallStub(Object object, Method method, Class<?> returnType, Class<?>... parameterTypes) {
        return ForeignSupport.upcallStub(object, method, returnType, parameterTypes);
    }

    @Override
    public void invokeVoidFunction(long address, Object... args) {
        ForeignSupport.invokeVoidFunction(address, args);
    }

    @Override
    public boolean invokeBooleanFunction(long address, Object... args) {
        return ForeignSupport.invokeBooleanFunction(address, args);
    }

    @Override
    public byte invokeByteFunction(long address, Object... args) {
        return ForeignSupport.invokeByteFunction(address, args);
    }

    @Override
    public char invokeCharFunction(long address, Object... args) {
        return ForeignSupport.invokeCharFunction(address, args);
    }

    @Override
    public short invokeShortFunction(long address, Object... args) {
        return ForeignSupport.invokeShortFunction(address, args);
    }

    @Override
    public int invokeIntFunction(long address, Object... args) {
        return ForeignSupport.invokeIntFunction(address, args);
    }

    @Override
    public long invokeNativeIntFunction(long address, Object... args) {
        return ForeignSupport.invokeNativeIntFunction(address, args);
    }

    @Override
    public long invokeLongFunction(long address, Object... args) {
        return ForeignSupport.invokeLongFunction(address, args);
    }

    @Override
    public long invokeNativeLongFunction(long address, Object... args) {
        return ForeignSupport.invokeNativeLongFunction(address, args);
    }

    @Override
    public float invokeFloatFunction(long address, Object... args) {
        return ForeignSupport.invokeFloatFunction(address, args);
    }

    @Override
    public double invokeDoubleFunction(long address, Object... args) {
        return ForeignSupport.invokeDoubleFunction(address, args);
    }

    @Override
    public long invokeAddressFunction(long address, Object... args) {
        return ForeignSupport.invokeAddressFunction(address, args);
    }

    @Override
    public <T> T invokeFunction(long address, Class<T> returnType, Object... args) {
        return ForeignSupport.invokeFunction(address, returnType, args);
    }

    @Override
    public void loadLibrary(String name, int loader) throws IOException {
        ForeignSupport.loadLibrary(name, loader);
    }

    @Override
    public void loadLibrary(File file, int loader) throws IOException {
        ForeignSupport.loadLibrary(file, loader);
    }

    @Override
    public String mapLibraryName(String name) {
        return ForeignSupport.mapLibraryName(name);
    }

    @Override
    public long getSymbolAddress(String name) throws UnsatisfiedLinkError {
        return ForeignSupport.getSymbolAddress(name);
    }

    @Override
    public int nativeIntSize() {
        return ForeignSupport.nativeIntSize();
    }

    @Override
    public int nativeLongSize() {
        return ForeignSupport.nativeLongSize();
    }

    @Override
    public int addressSize() {
        return ForeignSupport.addressSize();
    }

    @Override
    public Class<?> nativeIntClass() {
        return ForeignSupport.nativeIntClass();
    }

    @Override
    public Class<?> nativeLongClass() {
        return ForeignSupport.nativeLongClass();
    }

    @Override
    public Class<?> addressClass() {
        return ForeignSupport.addressClass();
    }

    @Override
    public Charset systemCharset() {
        return ForeignSupport.systemCharset();
    }

    @Override
    public int systemCharSize() {
        return ForeignSupport.systemCharSize();
    }

    @Override
    public Charset wideCharset() {
        return ForeignSupport.wideCharset();
    }

    @Override
    public int wideCharSize() {
        return ForeignSupport.wideCharSize();
    }

    @Override
    public int getLastError() {
        return ForeignSupport.getLastError();
    }

    @Override
    public void setLastError(int errno) {
        ForeignSupport.setLastError(errno);
    }

}
