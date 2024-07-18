package unrefined.util;

import java.util.Random;

public final class Randoms {

    private Randoms() {
        throw new NotInstantiableError(Randoms.class);
    }

    public static boolean nextBoolean(Random random) {
        return random.nextBoolean();
    }

    public static void nextBytes(Random random, byte[] bytes) {
        random.nextBytes(bytes);
    }

    public static float nextFloat(Random random) {
        return random.nextFloat();
    }

    public static float nextFloat(Random random, float bound) {
        if (!(0.0f < bound && bound < Float.POSITIVE_INFINITY)) {
            throw new IllegalArgumentException("bound must be finite and positive");
        }

        // Specialize boundedNextFloat for origin == 0, bound > 0
        float r = random.nextFloat();
        r = r * bound;
        if (r >= bound) // may need to correct a rounding problem
            r = Math.nextDown(bound);
        return r;
    }

    public static float nextFloat(Random random, float origin, float bound) {
        if (!(Float.NEGATIVE_INFINITY < origin && origin < bound &&
                bound < Float.POSITIVE_INFINITY)) {
            throw new IllegalArgumentException("bound must be greater than origin");
        }

        float r = random.nextFloat();
        if (origin < bound) {
            if (bound - origin < Float.POSITIVE_INFINITY) {
                r = r * (bound - origin) + origin;
            } else {
                /* avoids overflow at the cost of 3 more multiplications */
                float halfOrigin = 0.5f * origin;
                r = (r * (0.5f * bound - halfOrigin) + halfOrigin) * 2.0f;
            }
            if (r >= bound) // may need to correct a rounding problem
                r = Math.nextDown(bound);
        }
        return r;
    }

    public static double nextDouble(Random random) {
        return random.nextDouble();
    }

    public static double nextDouble(Random random, double bound) {
        if (!(0.0 < bound && bound < Double.POSITIVE_INFINITY)) {
            throw new IllegalArgumentException("bound must be finite and positive");
        }

        // Specialize boundedNextDouble for origin == 0, bound > 0
        double r = random.nextDouble();
        r = r * bound;
        if (r >= bound)  // may need to correct a rounding problem
            r = Math.nextDown(bound);
        return r;
    }

    public static double nextDouble(Random random, double origin, double bound) {
        if (!(Double.NEGATIVE_INFINITY < origin && origin < bound &&
                bound < Double.POSITIVE_INFINITY)) {
            throw new IllegalArgumentException("bound must be greater than origin");
        }

        double r = random.nextDouble();
        if (origin < bound) {
            if (bound - origin < Double.POSITIVE_INFINITY) {
                r = r * (bound - origin) + origin;
            } else {
                /* avoids overflow at the cost of 3 more multiplications */
                double halfOrigin = 0.5 * origin;
                r = (r * (0.5 * bound - halfOrigin) + halfOrigin) * 2.0;
            }
            if (r >= bound)  // may need to correct a rounding problem
                r = Math.nextDown(bound);
        }
        return r;
    }

    public static int nextInt(Random random) {
        return random.nextInt();
    }

    public static int nextInt(Random random, int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }

        // Specialize boundedNextInt for origin == 0, bound > 0
        final int m = bound - 1;
        int r = random.nextInt();
        if ((bound & m) == 0) {
            // The bound is a power of 2.
            r &= m;
        } else {
            // Must reject over-represented candidates
            for (int u = r >>> 1;
                 u + m - (r = u % bound) < 0;
                 u = random.nextInt() >>> 1)
                ;
        }
        return r;
    }

    public static int nextInt(Random random, int origin, int bound) {
        if (origin >= bound) {
            throw new IllegalArgumentException("bound must be greater than origin");
        }

        int r = random.nextInt();
        if (origin < bound) {
            // It's not case (1).
            final int n = bound - origin;
            final int m = n - 1;
            if ((n & m) == 0) {
                // It is case (2): length of range is a power of 2.
                r = (r & m) + origin;
            } else if (n > 0) {
                // It is case (3): need to reject over-represented candidates.
                for (int u = r >>> 1;
                     u + m - (r = u % n) < 0;
                     u = random.nextInt() >>> 1)
                    ;
                r += origin;
            }
            else {
                // It is case (4): length of range not representable as long.
                while (r < origin || r >= bound) {
                    r = random.nextInt();
                }
            }
        }
        return r;
    }

    public static long nextLong(Random random) {
        return random.nextLong();
    }

    public static long nextLong(Random random, long bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }

        // Specialize boundedNextLong for origin == 0, bound > 0
        final long m = bound - 1;
        long r = random.nextLong();
        if ((bound & m) == 0L) {
            // The bound is a power of 2.
            r &= m;
        } else {
            // Must reject over-represented candidates
            /* This loop takes an unlovable form (but it works):
               because the first candidate is already available,
               we need a break-in-the-middle construction,
               which is concisely but cryptically performed
               within the while-condition of a body-less for loop. */
            for (long u = r >>> 1;
                 u + m - (r = u % bound) < 0L;
                 u = random.nextLong() >>> 1)
                ;
        }
        return r;
    }

    public static long nextLong(Random random, long origin, long bound) {
        if (origin >= bound) {
            throw new IllegalArgumentException("bound must be greater than origin");
        }

        long r = random.nextLong();
        if (origin < bound) {
            // It's not case (1).
            final long n = bound - origin;
            final long m = n - 1;
            if ((n & m) == 0L) {
                // It is case (2): length of range is a power of 2.
                r = (r & m) + origin;
            } else if (n > 0L) {
                // It is case (3): need to reject over-represented candidates.
                /* This loop takes an unlovable form (but it works):
                   because the first candidate is already available,
                   we need a break-in-the-middle construction,
                   which is concisely but cryptically performed
                   within the while-condition of a body-less for loop. */
                for (long u = r >>> 1;            // ensure nonnegative
                     u + m - (r = u % n) < 0L;    // rejection check
                     u = random.nextLong() >>> 1) // retry
                    ;
                r += origin;
            }
            else {
                // It is case (4): length of range not representable as long.
                while (r < origin || r >= bound)
                    r = random.nextLong();
            }
        }
        return r;
    }

    public static double nextGaussian(Random random) {
        return random.nextGaussian();
    }

}
