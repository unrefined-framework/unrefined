package unrefined.media.graphics;

import unrefined.io.Disposable;
import unrefined.util.NotInstantiableError;

public abstract class Cursor implements Disposable {

    public static final class Type {
        private Type() {
            throw new NotInstantiableError(Type.class);
        }
        public static final int CUSTOM         = -1;
        public static final int ARROW          = 0;
        public static final int CROSSHAIR      = 1;
        public static final int IBEAM          = 2;
        public static final int WAIT           = 3;
        public static final int POINTING_HAND  = 4;
        public static final int MOVE           = 5;
        public static final int RESIZE_N       = 6;
        public static final int RESIZE_S       = 7;
        public static final int RESIZE_W       = 8;
        public static final int RESIZE_E       = 9;
        public static final int RESIZE_SW      = 10;
        public static final int RESIZE_SE      = 11;
        public static final int RESIZE_NW      = 12;
        public static final int RESIZE_NE      = 13;
        public static final int RESIZE_NS      = 14;
        public static final int RESIZE_WE      = 15;
        public static final int RESIZE_NWSE    = 16;
        public static final int RESIZE_NESW    = 17;
        public static final int RESIZE_COL     = 18;
        public static final int RESIZE_ROW     = 19;
        public static final int CELL           = 20;
        public static final int HELP           = 21;
        public static final int ZOOM_IN        = 22;
        public static final int ZOOM_OUT       = 23;
        public static final int NO             = 24;
        public static final int GRAB           = 25;
        public static final int GRABBING       = 26;
        public static final int COPY_DROP      = 27;
        public static final int LINK_DROP      = 28;
        public static final int MOVE_DROP      = 29;
        public static final int NO_DROP        = 30;
        public static final int UP_ARROW       = 31;
        public static final int VERTICAL_IBEAM = 32;
        public static final int CONTEXT_MENU   = 33;
        public static final int PROGRESS       = 34;
        public static final int FLEUR          = 35;
        public static final int NONE           = 100;
        public static void checkValid(int type) {
            if (!isValid(type)) throw new IllegalArgumentException("Illegal cursor type: " + type);
        }
        public static boolean isValid(int type) {
            return type == NONE || (type >= CUSTOM && type <= FLEUR);
        }
        public static String toString(int type) {
            switch (type) {
                case CUSTOM         : return "CUSTOM";
                case ARROW          : return "ARROW";
                case CROSSHAIR      : return "CROSSHAIR";
                case IBEAM          : return "IBEAM";
                case WAIT           : return "WAIT";
                case POINTING_HAND  : return "POINTING_HAND";
                case MOVE           : return "MOVE";
                case RESIZE_N       : return "RESIZE_N";
                case RESIZE_S       : return "RESIZE_S";
                case RESIZE_W       : return "RESIZE_W";
                case RESIZE_E       : return "RESIZE_E";
                case RESIZE_SW      : return "RESIZE_SW";
                case RESIZE_SE      : return "RESIZE_SE";
                case RESIZE_NW      : return "RESIZE_NW";
                case RESIZE_NE      : return "RESIZE_NE";
                case RESIZE_NS      : return "RESIZE_NS";
                case RESIZE_WE      : return "RESIZE_WE";
                case RESIZE_NWSE    : return "RESIZE_NWSE";
                case RESIZE_NESW    : return "RESIZE_NESW";
                case RESIZE_COL     : return "RESIZE_COL";
                case RESIZE_ROW     : return "RESIZE_ROW";
                case CELL           : return "CELL";
                case HELP           : return "HELP";
                case ZOOM_IN        : return "ZOOM_IN";
                case ZOOM_OUT       : return "ZOOM_OUT";
                case NO             : return "NO";
                case GRAB           : return "GRAB";
                case GRABBING       : return "GRABBING";
                case COPY_DROP      : return "COPY_DROP";
                case LINK_DROP      : return "LINK_DROP";
                case MOVE_DROP      : return "MOVE_DROP";
                case NO_DROP        : return "NO_DROP";
                case UP_ARROW       : return "UP_ARROW";
                case VERTICAL_IBEAM : return "VERTICAL_IBEAM";
                case CONTEXT_MENU   : return "CONTEXT_MENU";
                case PROGRESS       : return "PROGRESS";
                case FLEUR          : return "FLEUR";
                case NONE           : return "NONE";
                default             : throw new IllegalArgumentException("Illegal cursor type: " + type);
            }
        }
    }

    public abstract int getType();

    public abstract int getHotSpotX();
    public abstract int getHotSpotY();

    public void getHotSpot(Point hotSpot) {
        hotSpot.setPoint(getHotSpotX(), getHotSpotY());
    }

    @Override
    public String toString() {
        if (isDisposed()) return getClass().getName() + "@" + Integer.toHexString(hashCode())
                + '{' +
                "disposed=true" +
                '}';
        else {
            int type = getType();
            if (type == Type.CUSTOM) return getClass().getName()
                    + '{' +
                    "disposed=false" +
                    ", type=CUSTOM" +
                    ", hotSpotX=" + getHotSpotX() +
                    ", hotSpotY=" + getHotSpotY() +
                    '}';
            else return getClass().getName()
                    + '{' +
                    "disposed=false" +
                    ", type=" + Type.toString(getType()) +
                    '}';
        }
    }

}
