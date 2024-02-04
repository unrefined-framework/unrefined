package unrefined.runtime;

import com.kenai.jffi.LastError;
import unrefined.desktop.ForeignSupport;
import unrefined.util.foreign.Aggregate;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Library;
import unrefined.util.foreign.Symbol;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

public class DesktopForeign extends Foreign {

    @Override
    public <T extends Library> T downcallProxy(int options, Class<T> clazz, ClassLoader loader) {
        return ForeignSupport.downcallProxy(options, clazz, loader);
    }

    @Override
    public Symbol downcallHandle(int options, long function, Class<?> returnType, Class<?>... parameterTypes) {
        return ForeignSupport.downcallHandle(options, function, returnType, parameterTypes);
    }

    @Override
    public Symbol upcallStub(int options, Object object, Method method, Class<?> returnType, Class<?>... parameterTypes) {
        return ForeignSupport.upcallStub(options, object, method, returnType, parameterTypes);
    }

    @Override
    public void invokeVoidFunction(int options, long address, Object... args) {
        ForeignSupport.invokeVoidFunction(options, address, args);
    }

    @Override
    public boolean invokeBooleanFunction(int options, long address, Object... args) {
        return ForeignSupport.invokeBooleanFunction(options, address, args);
    }

    @Override
    public byte invokeByteFunction(int options, long address, Object... args) {
        return ForeignSupport.invokeByteFunction(options, address, args);
    }

    @Override
    public char invokeCharFunction(int options, long address, Object... args) {
        return ForeignSupport.invokeCharFunction(options, address, args);
    }

    @Override
    public short invokeShortFunction(int options, long address, Object... args) {
        return ForeignSupport.invokeShortFunction(options, address, args);
    }

    @Override
    public int invokeIntFunction(int options, long address, Object... args) {
        return ForeignSupport.invokeIntFunction(options, address, args);
    }

    @Override
    public long invokeNativeIntFunction(int options, long address, Object... args) {
        return ForeignSupport.invokeNativeIntFunction(options, address, args);
    }

    @Override
    public long invokeLongFunction(int options, long address, Object... args) {
        return ForeignSupport.invokeLongFunction(options, address, args);
    }

    @Override
    public long invokeNativeLongFunction(int options, long address, Object... args) {
        return ForeignSupport.invokeNativeLongFunction(options, address, args);
    }

    @Override
    public float invokeFloatFunction(int options, long address, Object... args) {
        return ForeignSupport.invokeFloatFunction(options, address, args);
    }

    @Override
    public double invokeDoubleFunction(int options, long address, Object... args) {
        return ForeignSupport.invokeDoubleFunction(options, address, args);
    }

    @Override
    public long invokeAddressFunction(int options, long address, Object... args) {
        return ForeignSupport.invokeAddressFunction(options, address, args);
    }

    @Override
    public <T extends Aggregate> T invokeAggregateFunction(int options, long address, Class<T> returnType, Object... args) {
        return ForeignSupport.invokeAggregateFunction(options, address, returnType, args);
    }

    @Override
    public <T> T invokeFunction(int options, long address, Class<T> returnType, Object... args) {
        return ForeignSupport.invokeFunction(options, address, returnType, args);
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
        return LastError.getInstance().get();
    }

    @Override
    public void setLastError(int errno) {
        LastError.getInstance().set(errno);
    }

    @Override
    public String getErrorString(int errno) {
        return ForeignSupport.ERROR_STRING_PRODUCER.apply(errno);
    }

}
