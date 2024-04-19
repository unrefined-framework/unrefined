package unrefined.util;

import unrefined.nio.charset.Charsets;

import java.util.Arrays;
import java.util.Locale;

public final class Strings {

    private Strings() {
        throw new NotInstantiableError(Strings.class);
    }

    public static String repeat(String s, int count) {
        if (count < 0) throw new IllegalArgumentException("count is negative: " + count);
        else if (count == 0) return "";
        else if (count == 1) return s;
        final byte[] value = s.getBytes(Charsets.UTF_8);
        final int len = value.length;
        if (len == 0) return "";
        if (Integer.MAX_VALUE / count < len) {
            throw new OutOfMemoryError("Required length exceeds implementation limit");
        }
        if (len == 1) {
            final byte[] single = new byte[count];
            Arrays.fill(single, value[0]);
            return new String(single, Charsets.UTF_8);
        }
        final int limit = len * count;
        final byte[] multiple = new byte[limit];
        System.arraycopy(value, 0, multiple, 0, len);
        repeatCopyRest(multiple, 0, limit, len);
        return new String(multiple, Charsets.UTF_8);
    }

    private static void repeatCopyRest(byte[] buffer, int offset, int limit, int copied) {
        // Initial copy is in the buffer.
        for (; copied < limit - copied; copied <<= 1) {
            // Power of two duplicate.
            System.arraycopy(buffer, offset, buffer, offset + copied, copied);
        }
        // Duplicate remainder.
        System.arraycopy(buffer, offset, buffer, offset + copied, limit - copied);
    }

    public static int indexOfNonWhitespace(String s) {
        for (int i = 0; i < s.length(); i ++) {
            char ch = s.charAt(i);
            if (!Character.isWhitespace(ch)) return i;
        }
        return -1;
    }

    public static int lastIndexOfNonWhitespace(String s) {
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
        return s.isEmpty();
    }

    public static boolean isBlank(String s) {
        return indexOfNonWhitespace(s) == -1;
    }

    public static boolean nullEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static String firstCharToUpperCase(String s, Locale locale) {
        String upper = s.substring(0, 1).toUpperCase(locale == null ? Locale.getDefault() : locale);
        return upper + s.substring(1);
    }

    public static String firstCharToUpperCase(String s) {
        String upper = s.substring(0, 1).toUpperCase();
        return upper + s.substring(1);
    }

}
