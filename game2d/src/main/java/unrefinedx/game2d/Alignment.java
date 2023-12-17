package unrefinedx.game2d;

import unrefined.util.NotInstantiableError;

public final class Alignment {

    private Alignment() {
        throw new NotInstantiableError(Alignment.class);
    }

    public static final class X {

        private X() {
            throw new NotInstantiableError(X.class);
        }

        public static final int LEFT = 0;
        public static final int CENTER = 1;
        public static final int RIGHT = 2;

        public static int checkValid(int alignment) {
            if (alignment < LEFT || alignment > RIGHT)
                throw new IllegalArgumentException("Illegal alignment: " + alignment);
            else return alignment;
        }

        public static boolean isValid(int alignment) {
            return alignment >= LEFT && alignment <= RIGHT;
        }

        public static String toString(int alignment) {
            switch (alignment) {
                case LEFT: return "LEFT";
                case CENTER: return "CENTER";
                case RIGHT: return "RIGHT";
                default: throw new IllegalArgumentException("Illegal alignment: " + alignment);
            }
        }

    }

    public static final class Y {

        private Y() {
            throw new NotInstantiableError(Y.class);
        }

        public static final int TOP = 0;
        public static final int CENTER = 1;
        public static final int BOTTOM = 2;

        public static int checkValid(int alignment) {
            if (alignment < TOP || alignment > BOTTOM)
                throw new IllegalArgumentException("Illegal alignment: " + alignment);
            else return alignment;
        }

        public static boolean isValid(int alignment) {
            return alignment >= TOP && alignment <= BOTTOM;
        }

        public static String toString(int alignment) {
            switch (alignment) {
                case TOP: return "TOP";
                case CENTER: return "CENTER";
                case BOTTOM: return "BOTTOM";
                default: throw new IllegalArgumentException("Illegal alignment: " + alignment);
            }
        }

    }

}
