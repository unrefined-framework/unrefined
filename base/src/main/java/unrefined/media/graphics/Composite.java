package unrefined.media.graphics;

import unrefined.io.Disposable;
import unrefined.util.NotInstantiableError;

import static unrefined.media.graphics.Composite.Mode.CUSTOM;

public abstract class Composite implements Disposable {

    public static final class Mode {
        private Mode() {
            throw new NotInstantiableError(Mode.class);
        }
        public static final int CUSTOM      = -1;
        public static final int SRC_OVER    = 0;
        public static final int SRC_IN      = 1;
        public static final int SRC_OUT     = 2;
        public static final int SRC_ATOP    = 3;
        public static final int DST_OVER    = 4;
        public static final int DST_IN      = 5;
        public static final int DST_OUT     = 6;
        public static final int DST_ATOP    = 7;
        public static final int PLUS        = 8;
        public static final int COPY        = 9;
        public static final int XOR         = 10;
        public static final int MULTIPLY    = 11;
        public static final int SCREEN      = 12;
        public static final int OVERLAY     = 13;
        public static final int DARKEN      = 14;
        public static final int LIGHTEN     = 15;
        public static final int COLOR_DODGE = 16;
        public static final int COLOR_BURN  = 17;
        public static final int HARD_LIGHT  = 18;
        public static final int SOFT_LIGHT  = 19;
        public static final int DIFFERENCE  = 20;
        public static final int EXCLUSION   = 21;
        public static final int HUE         = 22;
        public static final int SATURATION  = 23;
        public static final int COLOR       = 24;
        public static final int LUMINOSITY  = 25;
        public static final int DISCARD     = 26;
        public static final int CLEAR       = 27;

        public static boolean isValid(int mode) {
            return mode >= CUSTOM && mode <= CLEAR;
        }
        public static int checkValid(int mode) {
            if (mode < CUSTOM || mode > CLEAR) throw new IllegalArgumentException("Illegal composite mode: " + mode);
            else return mode;
        }
        public static String toString(int mode) {
            switch (mode) {
                case CUSTOM      : return "CUSTOM";
                case SRC_OVER    : return "SRC_OVER";
                case SRC_IN      : return "SRC_IN";
                case SRC_OUT     : return "SRC_OUT";
                case SRC_ATOP    : return "SRC_ATOP";
                case DST_OVER    : return "DST_OVER";
                case DST_IN      : return "DST_IN";
                case DST_OUT     : return "DST_OUT";
                case DST_ATOP    : return "DST_ATOP";
                case PLUS        : return "PLUS";
                case COPY        : return "COPY";
                case XOR         : return "XOR";
                case MULTIPLY    : return "MULTIPLY";
                case SCREEN      : return "SCREEN";
                case OVERLAY     : return "OVERLAY";
                case DARKEN      : return "DARKEN";
                case LIGHTEN     : return "LIGHTEN";
                case COLOR_DODGE : return "COLOR_DODGE";
                case COLOR_BURN  : return "COLOR_BURN";
                case HARD_LIGHT  : return "HARD_LIGHT";
                case SOFT_LIGHT  : return "SOFT_LIGHT";
                case DIFFERENCE  : return "DIFFERENCE";
                case EXCLUSION   : return "EXCLUSION";
                case HUE         : return "HUE";
                case SATURATION  : return "SATURATION";
                case COLOR       : return "COLOR";
                case LUMINOSITY  : return "LUMINOSITY";
                case DISCARD     : return "DISCARD";
                case CLEAR       : return "CLEAR";
                default: throw new IllegalArgumentException("Illegal composite mode: " + mode);
            }
        }
    }

    public abstract int getMode();
    public abstract float getAlpha();

    @Override
    public String toString() {
        if (isDisposed()) return getClass().getName() + "@" + Integer.toHexString(hashCode())
                + '{' +
                "disposed=true" +
                '}';
        else {
            int mode = getMode();
            if (mode == CUSTOM) return getClass().getName() + "@" + Integer.toHexString(hashCode())
                    + '{' +
                    "disposed=false" +
                    ", mode=CUSTOM" +
                    ", alpha=" + getAlpha() +
                    '}';
            else return getClass().getName()
                    + '{' +
                    "disposed=false" +
                    ", mode=" + Mode.toString(mode) +
                    ", alpha=" + getAlpha() +
                    '}';
        }
    }

}
