package unrefined.util;

public final class CharFactory {

    private CharFactory() {
        throw new NotInstantiableError(CharFactory.class);
    }

    public static char[] toCharArray(CharSequence text) {
        if (text instanceof String) return ((String) text).toCharArray();
        char[] array = new char[text.length()];
        for (int i = 0; i < array.length; i ++) {
            array[i] = text.charAt(i);
        }
        return array;
    }

    public static char[] toCharArray(CharSequence text, int start, int end) {
        char[] array = new char[end - start];
        for (int i = 0; i < array.length; i ++) {
            array[i] = text.charAt(i + start);
        }
        return array;
    }

}
