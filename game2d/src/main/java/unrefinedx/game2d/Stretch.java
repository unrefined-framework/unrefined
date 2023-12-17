package unrefinedx.game2d;

import unrefined.internal.BitwiseUtils;
import unrefined.util.NotInstantiableError;

public final class Stretch {

    private Stretch() {
        throw new NotInstantiableError(Stretch.class);
    }

    public static final int NONE = 0;
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 1 << 1;
    public static final int ALL = HORIZONTAL | VERTICAL;

    public static int removeUnusedBits(int stretch) {
        return BitwiseUtils.removeUnusedBits(stretch, 2);
    }

    public static String toString(int stretch) {
        stretch = removeUnusedBits(stretch);
        if (stretch == NONE) return "[NONE]";
        else {
            StringBuilder builder = new StringBuilder("[");
            if ((stretch & HORIZONTAL) == HORIZONTAL) builder.append("HORIZONTAL, ");
            if ((stretch & VERTICAL) == VERTICAL) builder.append("VERTICAL, ");
            builder.setLength(builder.length() - 2);
            builder.append("]");
            return builder.toString();
        }
    }

}
