package unrefined.internal;

import unrefined.context.Environment;

//import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;

import static java.lang.foreign.ValueLayout.*;

public final class MemoryLayoutUtils {

    // FIXME JDK 22 containing Linker#canonicalLayouts to directly get the native type size, but JDK 21 not
    /*
    private static final Linker LINKER = Linker.nativeLinker();

    public static final int SIZE_T_SIZE = (int) LINKER.canonicalLayouts().get("size_t").byteSize();

    public static final int NATIVE_INT_SIZE_BITS = Environment.properties().parseIntProperty(
            "unrefined.os.arch.bitmode.int", LINKER.canonicalLayouts().get("int").byteSize() * 8);
    public static final int NATIVE_LONG_SIZE_BITS = Environment.properties().parseIntProperty(
            "unrefined.os.arch.bitmode.long", LINKER.canonicalLayouts().get("long").byteSize() * 8);
     */

    // For 64-bit systems:
    // Windows is LLP64 (32-bit int, 32-bit long, 64-bit long long, 64-bit pointer)
    // Most common modern UNIX are LP64 (32-bit int, 64-bit long, 64-bit long long, 64-bit pointer)
    // For 32-bit systems:
    // Most common systems are ILP32 (32-bit int, 32-bit long, 32-bit long long, 32-bit pointer)
    // So I treat them as below

    // FIXME may incorrect
    //  also, you can fix them manually by setting the properties...

    public static final int SIZE_T_SIZE = (int) ADDRESS.byteSize();

    public static final MemoryLayout SIZE_T = SIZE_T_SIZE == 8 ? JAVA_LONG : JAVA_INT;
    public static final int SIZE_T_SIZE_BITS = SIZE_T_SIZE * 8;

    public static final int NATIVE_INT_SIZE_BITS = Environment.properties().parseIntProperty("unrefined.os.arch.bitmode.int", 32);
    public static final int NATIVE_LONG_SIZE_BITS = Environment.properties().parseIntProperty("unrefined.os.arch.bitmode.long",
            SystemUtils.IS_WINDOWS ? 32 : 64);

    public static final MemoryLayout NATIVE_INT = NATIVE_INT_SIZE_BITS == 64 ? JAVA_LONG : JAVA_INT;
    public static final MemoryLayout NATIVE_INT_UNALIGNED = NATIVE_INT_SIZE_BITS == 64 ? JAVA_LONG_UNALIGNED : JAVA_INT_UNALIGNED;
    public static final int NATIVE_INT_SIZE = (int) NATIVE_INT.byteSize();
    public static final MemoryLayout NATIVE_LONG = NATIVE_LONG_SIZE_BITS == 64 ? JAVA_LONG : JAVA_INT;
    public static final MemoryLayout NATIVE_LONG_UNALIGNED = NATIVE_LONG_SIZE_BITS == 64 ? JAVA_LONG_UNALIGNED : JAVA_INT_UNALIGNED;
    public static final int NATIVE_LONG_SIZE = (int) NATIVE_LONG.byteSize();

    public static final int ADDRESS_SIZE = (int) ADDRESS.byteSize();
    public static final int ADDRESS_SIZE_BITS = ADDRESS_SIZE * 8;

    public static final String ABI;
    static {
        if (ADDRESS_SIZE == 8) {
            if (NATIVE_LONG_SIZE == 8) {
                if (NATIVE_INT_SIZE == 8) ABI = "ILP64";
                else ABI = "LP64";
            }
            else ABI = "LLP64";
        }
        else ABI = "ILP32";
    }

}
