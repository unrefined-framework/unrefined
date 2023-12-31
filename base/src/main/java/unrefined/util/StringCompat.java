package unrefined.util;

public final class StringCompat {

    private StringCompat() {
        throw new NotInstantiableError(StringCompat.class);
    }

    public static int indexOfNonWhitespace(String s) {
        if (s == null) return -1;
        for (int i = 0; i < s.length(); i ++) {
            char ch = s.charAt(i);
            if (!Character.isWhitespace(ch)) return i;
        }
        return -1;
    }

    public static int lastIndexOfNonWhitespace(String s) {
        if (s == null) return -1;
        for (int i = s.length() - 1; i >= 0; i --) {
            char ch = s.charAt(i);
            if (!Character.isWhitespace(ch)) return i;
        }
        return -1;
    }

    public static String strip(String s) {
        if (s == null) return s;
        int index = indexOfNonWhitespace(s);
        if (index == -1) return "";
        int lastIndex = lastIndexOfNonWhitespace(s);
        if (lastIndex == index) return "";
        return s.substring(index, lastIndex + 1);
    }

    public static String stripLeading(String s) {
        if (s == null) return s;
        int index = indexOfNonWhitespace(s);
        return index == -1 ? "" : s.substring(index);
    }

    public static String stripTrailing(String s) {
        if (s == null) return s;
        int index = lastIndexOfNonWhitespace(s);
        return index == -1 ? "" : s.substring(0, index + 1);
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isBlank(String s) {
        return indexOfNonWhitespace(s) == -1;
    }

}
