package unrefined.media.graphics;

import unrefined.util.NotInstantiableError;

public final class Stroke {

    private Stroke() {
        throw new NotInstantiableError(Stroke.class);
    }

    public static final class Join {
        private Join() {
            throw new NotInstantiableError(Join.class);
        }
        public static final int MITER = 0;
        public static final int ROUND = 1;
        public static final int BEVEL = 2;
        public static boolean isValid(int join) {
            return join >= MITER && join <= BEVEL;
        }
        public static int checkValid(int join) {
            if (join < MITER || join > BEVEL) throw new IllegalArgumentException("Illegal stroke join: " + join);
            else return join;
        }
        public static String toString(int join) {
            switch (join) {
                case MITER: return "MITER";
                case ROUND: return "ROUND";
                case BEVEL: return "BEVEL";
                default: throw new IllegalArgumentException("Illegal stroke join: " + join);
            }
        }
    }

    public static final class Cap {
        private Cap() {
            throw new NotInstantiableError(Cap.class);
        }
        public static final int BUTT = 0;
        public static final int ROUND = 1;
        public static final int SQUARE = 2;
        public static boolean isValid(int cap) {
            return cap >= BUTT && cap <= SQUARE;
        }
        public static int checkValid(int cap) {
            if (cap < BUTT || cap > SQUARE) throw new IllegalArgumentException("Illegal stroke cap: " + cap);
            else return cap;
        }
        public static String toString(int cap) {
            switch (cap) {
                case BUTT: return "BUTT";
                case ROUND: return "ROUND";
                case SQUARE: return "SQUARE";
                default: throw new IllegalArgumentException("Illegal stroke cap: " + cap);
            }
        }
    }

}
