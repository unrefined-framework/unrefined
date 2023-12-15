package unrefined.internal;

import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static unrefined.internal.MemoryLayoutUtils.SIZE_T;

public final class CLibraryUtils {

    private CLibraryUtils() {
        throw new NotInstantiableError(CLibraryUtils.class);
    }

    private static final Linker LINKER = Linker.nativeLinker();
    private static final SymbolLookup DEFAULT_LOOKUP = LINKER.defaultLookup();

    private static final MethodHandle memchrMethodHandle = DEFAULT_LOOKUP.find("memchr").map(symbolSegment ->
                    LINKER.downcallHandle(symbolSegment,
                            FunctionDescriptor.of(ADDRESS, ADDRESS, JAVA_INT, SIZE_T),
                            Linker.Option.isTrivial()))
            .orElseThrow(UnexpectedError::new);

    public static long memchr(MemorySegment ptr, int ch, long count) {
        try {
            return ((MemorySegment) memchrMethodHandle.invoke(ptr, ch, count)).address();
        } catch (Throwable e) {
            throw new UnexpectedError(e);
        }
    }

    private static final MethodHandle strlenMethodHandle = DEFAULT_LOOKUP.find("strlen").map(symbolSegment ->
                    LINKER.downcallHandle(symbolSegment,
                            FunctionDescriptor.of(SIZE_T, ADDRESS),
                            Linker.Option.isTrivial()))
            .orElseThrow(UnexpectedError::new);

    private static final MethodHandle strnlen_sMethodHandle = DEFAULT_LOOKUP.find("strnlen_s").map(symbolSegment ->
                    LINKER.downcallHandle(symbolSegment,
                            FunctionDescriptor.of(SIZE_T, ADDRESS, SIZE_T),
                            Linker.Option.isTrivial()))
            .orElse(null);

    public static long strlen(MemorySegment str) {
        try {
            return (long) strlenMethodHandle.invoke(str);
        } catch (Throwable e) {
            throw new UnexpectedError(e);
        }
    }

    public static long strnlen_s(MemorySegment str, long strsz) {
        try {
            if (strnlen_sMethodHandle == null) {
                long address = ((MemorySegment) memchrMethodHandle.invoke(str, '\0', strsz)).address();
                return address == 0 ? strsz : address - str.address();
            }
            else return (long) strnlen_sMethodHandle.invoke(str, strsz);
        }
        catch (Throwable e) {
            throw new UnexpectedError(e);
        }
    }

}
