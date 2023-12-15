package unrefined.media.graphics;

import unrefined.io.Disposable;
import unrefined.util.NotInstantiableError;

import static unrefined.media.graphics.Composite.Mode.CUSTOM;

public abstract class Brush implements Disposable {

    public static final class TileMode {
        private TileMode() {
            throw new NotInstantiableError(TileMode.class);
        }
        public static final int CLAMP  = 0;
        public static final int MIRROR = 1;
        public static final int REPEAT = 2;
        public static boolean isValid(int mode) {
            return mode >= CLAMP && mode <= REPEAT;
        }
        public static int checkValid(int mode) {
            if (mode < CLAMP || mode > REPEAT) throw new IllegalArgumentException("Illegal tile mode: " + mode);
            else return mode;
        }
        public static String toString(int mode) {
            switch (mode) {
                case CLAMP: return "CLAMP";
                case MIRROR: return "MIRROR";
                case REPEAT: return "REPEAT";
                default: throw new IllegalArgumentException("Illegal tile mode: " + mode);
            }
        }
    }

    public static final class Type {
        private Type() {
            throw new NotInstantiableError(Type.class);
        }
        public static final int CUSTOM          = -1;
        public static final int COLOR           = 0;
        public static final int LINEAR_GRADIENT = 1;
        public static final int RADIAL_GRADIENT = 2;
        public static final int BITMAP_PATTERN  = 3;

        public static boolean isValid(int type) {
            return type >= CUSTOM && type <= BITMAP_PATTERN;
        }
        public static int checkValid(int type) {
            if (type < CUSTOM || type > BITMAP_PATTERN) throw new IllegalArgumentException("Illegal brush type: " + type);
            else return type;
        }
        public static String toString(int type) {
            switch (type) {
                case CUSTOM : return "CUSTOM";
                case COLOR : return "COLOR";
                case LINEAR_GRADIENT : return "LINEAR_GRADIENT";
                case RADIAL_GRADIENT : return "RADIAL_GRADIENT";
                case BITMAP_PATTERN : return "BITMAP_PATTERN";
                default: throw new IllegalArgumentException("Illegal brush type: " + type);
            }
        }
    }

    public abstract int getType();

    @Override
    public String toString() {
        if (isDisposed()) return getClass().getName() + "@" + Integer.toHexString(hashCode())
                + '{' +
                "disposed=true" +
                '}';
        else {
            int type = getType();
            if (type == CUSTOM) return getClass().getName() + "@" + Integer.toHexString(hashCode())
                    + '{' +
                    "disposed=false" +
                    ", type=CUSTOM" +
                    '}';
            else return getClass().getName()
                    + '{' +
                    "disposed=false" +
                    ", type=" + Brush.Type.toString(type) +
                    '}';
        }
    }

}
