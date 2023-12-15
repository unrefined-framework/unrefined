package unrefined.internal;

import unrefined.util.NotInstantiableError;

public final class BitwiseUtils {

    private BitwiseUtils() {
        throw new NotInstantiableError(BitwiseUtils.class);
    }

    public static int removeUnusedBits(int bitset, int used) {
        if (used > 32 || used < 1) throw new IllegalArgumentException("0 < used < 32");
        int offset = 32 - used;
        bitset = bitset << offset >>> offset;
        return bitset;
    }

}
