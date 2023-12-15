package unrefined.internal;

import unrefined.util.NotInstantiableError;

import java.lang.foreign.MemorySegment;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static unrefined.internal.MemoryLayoutUtils.ADDRESS_SIZE;

public final class NativeStringUtils {

    private NativeStringUtils() {
        throw new NotInstantiableError(NativeStringUtils.class);
    }

    public interface LengthCalculator {
        long strlen(MemorySegment str);
        long strnlen_s(MemorySegment str, long strsz);
    }

    private static final LengthCalculator LENGTH_CALCULATOR_32 = new LengthCalculator() {
        @Override
        public long strlen(MemorySegment str) {
            return CLibraryUtils.strlen(str) & 0xFFFFFFFFL;
        }
        @Override
        public long strnlen_s(MemorySegment str, long strsz) {
            return CLibraryUtils.strnlen_s(str, Math.min(Integer.MAX_VALUE, strsz)) & 0xFFFFFFFFL;
        }
    };

    private static final LengthCalculator LENGTH_CALCULATOR_64 = new LengthCalculator() {
        @Override
        public long strlen(MemorySegment str) {
            return CLibraryUtils.strlen(str);
        }
        @Override
        public long strnlen_s(MemorySegment str, long strsz) {
            return CLibraryUtils.strnlen_s(str, strsz);
        }
    };

    public static final LengthCalculator LENGTH_CALCULATOR = ADDRESS_SIZE == 8 ? LENGTH_CALCULATOR_64 : LENGTH_CALCULATOR_32;

    // FIXME performance optimization
    public static String getString(MemorySegment segment, Charset charset) {
        if (charset == StandardCharsets.UTF_8) return segment.getUtf8String(0);
        else {
            long strlen = LENGTH_CALCULATOR.strlen(segment);
            int length = strlen < 0 ? Integer.MAX_VALUE : (int) Math.min(strlen, Integer.MAX_VALUE);
            byte[] bytes = new byte[length];
            MemorySegment.copy(segment, JAVA_BYTE, 0, bytes, 0, length);
            return new String(bytes, charset);
        }
    }

}
