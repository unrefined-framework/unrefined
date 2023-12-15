package unrefined.media.input;

import unrefined.internal.BitwiseUtils;
import unrefined.util.NotInstantiableError;

public final class Input {

    private Input() {
        throw new NotInstantiableError(Input.class);
    }

    public static final class KeyModifier {
        private KeyModifier() {
            throw new NotInstantiableError(KeyModifier.class);
        }
        public static final int SHIFT = 1;
        public static final int CTRL  = 1 << 1;
        public static final int META  = 1 << 2;
        public static final int ALT   = 1 << 3;
        public static int removeUnusedBits(int modifiers) {
            return BitwiseUtils.removeUnusedBits(modifiers, 4);
        }
        public static String toString(int modifiers) {
            modifiers = removeUnusedBits(modifiers);
            StringBuilder builder = new StringBuilder("[");
            if ((modifiers & SHIFT) == SHIFT) builder.append("SHIFT, ");
            if ((modifiers & CTRL) == CTRL) builder.append("CTRL, ");
            if ((modifiers & META) == META) builder.append("META, ");
            if ((modifiers & ALT) == ALT) builder.append("ALT, ");
            int length = builder.length();
            if (length > 1) builder.setLength(length - 2);
            builder.append("]");
            return builder.toString();
        }
    }

    public static final class KeyLocation {
        private KeyLocation() {
            throw new NotInstantiableError(KeyLocation.class);
        }
        public static final int STANDARD = 0;
        public static final int LEFT = 1;
        public static final int RIGHT = 2;
        public static final int NUMPAD = 3;
        public static int checkValid(int location) {
            if (location < STANDARD || location > NUMPAD) throw new IllegalArgumentException("Illegal key location: " + location);
            else return location;
        }
        public static boolean isValid(int location) {
            return location >= STANDARD && location <= NUMPAD;
        }
        public static String toString(int location) {
            switch (location) {
                case STANDARD: return "STANDARD";
                case LEFT: return "LEFT";
                case RIGHT: return "RIGHT";
                case NUMPAD: return "NUMPAD";
                default: throw new IllegalArgumentException("Illegal key location: " + location);
            }
        }
    }

    public static final class PointerButton {
        private PointerButton() {
            throw new NotInstantiableError(PointerButton.class);
        }
        public static final int LEFT = 0;
        public static final int RIGHT = 1;
        public static final int MIDDLE = 2;
        public static int checkValid(int button) {
            if (button < LEFT || button > MIDDLE) throw new IllegalArgumentException("Illegal pointer button: " + button);
            else return button;
        }
        public static boolean isValid(int button) {
            return button >= LEFT && button <= MIDDLE;
        }
        public static String toString(int button) {
            switch (button) {
                case LEFT: return "LEFT";
                case RIGHT: return "RIGHT";
                case MIDDLE: return "MIDDLE";
                default: throw new IllegalArgumentException("Illegal pointer button: " + button);
            }
        }
    }

    public static final class ScrollType {
        private ScrollType() {
            throw new NotInstantiableError(ScrollType.class);
        }
        public static final int UNIT = 0;
        public static final int LINE = 1;
        public static final int BLOCK = 2;
        public static int checkValid(int type) {
            if (type < UNIT || type > BLOCK) throw new IllegalArgumentException("Illegal scroll type: " + type);
            else return type;
        }
        public static boolean isValid(int type) {
            return type >= UNIT && type <= BLOCK;
        }
        public static String toString(int type) {
            switch (type) {
                case UNIT: return "UNIT";
                case LINE: return "LINE";
                case BLOCK: return "BLOCK";
                default: throw new IllegalArgumentException("Illegal scroll type: " + type);
            }
        }
    }

}
