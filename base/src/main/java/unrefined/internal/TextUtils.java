package unrefined.internal;

import unrefined.util.NotInstantiableError;

public final class TextUtils {

    private TextUtils() {
        throw new NotInstantiableError(TextUtils.class);
    }

    public static char[] toCharArray(CharSequence text, int start, int end) {
        char[] array = new char[end - start];
        for (int i = 0; i < array.length; i ++) {
            array[i] = text.charAt(i + start);
        }
        return array;
    }

}
