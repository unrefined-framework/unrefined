package unrefinedx.game2d;

import unrefined.util.NotInstantiableError;

public final class Scaling {

    private Scaling() {
        throw new NotInstantiableError(Scaling.class);
    }

    public static final int NONE       = 0;
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL   = 2;
    public static final int AUTO       = 3;

    public static int checkValid(int scaling) {
        if (scaling < NONE || scaling > AUTO)
            throw new IllegalArgumentException("Illegal scaling: " + scaling);
        else return scaling;
    }

    public static boolean isValid(int scaling) {
        return scaling >= NONE && scaling <= AUTO;
    }

    public static String toString(int scaling) {
        switch (scaling) {
            case NONE: return "NONE";
            case HORIZONTAL: return "HORIZONTAL";
            case VERTICAL: return "VERTICAL";
            case AUTO: return "AUTO";
            default: throw new IllegalArgumentException("Illegal scaling: " + scaling);
        }
    }

}
