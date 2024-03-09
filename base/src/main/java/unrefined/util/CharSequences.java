package unrefined.util;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

public final class CharSequences {

    private CharSequences() {
        throw new NotInstantiableError(CharSequences.class);
    }

    private static class CollectableString implements CharSequence {
        private final char[] text;
        private final int offset;
        private final int length;
        public CollectableString(char[] text, int offset, int length) {
            this.text = Objects.requireNonNull(text);
            if (length < 0) throw new ArrayIndexOutOfBoundsException(length);
            if (offset + length > text.length) throw new ArrayIndexOutOfBoundsException(offset + length);
            this.offset = offset;
            this.length = length;
        }
        public CollectableString(char[] text) {
            this.text = Objects.requireNonNull(text);
            this.offset = 0;
            this.length = text.length;
        }
        @Override
        public int length() {
            return length;
        }
        @Override
        public char charAt(int index) {
            if (index < 0 || index >= length) throw new ArrayIndexOutOfBoundsException(index);
            return text[offset + index];
        }
        @Override
        public CharSequence subSequence(int start, int end) {
            return new CollectableString(text, offset + start, end - start);
        }
        @Override
        public String toString() {
            return new String(text, offset, length);
        }
    }

    public static CharSequence asCharSequence(char... chars) {
        return new CollectableString(chars);
    }

    public static CharSequence toCharSequence(char... chars) {
        return new CollectableString(chars.clone());
    }

    public static CharSequence toCharSequence(char[] chars, int offset, int length) {
        return new CollectableString(FastArray.copyOfRange(chars, offset, offset + length));
    }
    
    public static boolean isEmpty(CharSequence text) {
        return text.length() == 0;
    }
    
    public static boolean nullEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }
    
    public static CharSequence requireNonEmpty(CharSequence text) {
        if (Objects.requireNonNull(text).length() == 0) throw new IllegalArgumentException("Empty CharSequence");
        else return text;
    }

    public static int compare(CharSequence text1, CharSequence text2) {
        if (Objects.requireNonNull(text1) == Objects.requireNonNull(text2)) {
            return 0;
        }

        if (text1.getClass() == text2.getClass() && text1 instanceof Comparable) {
            return ((Comparable<Object>) text1).compareTo(text2);
        }

        for (int i = 0, len = Math.min(text1.length(), text2.length()); i < len; i++) {
            char a = text1.charAt(i);
            char b = text2.charAt(i);
            if (a != b) {
                return a - b;
            }
        }

        return text1.length() - text2.length();
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

    public static byte[] getBytes(CharSequence text, int start, int end, Charset charset) {
        if (charset == null) charset = Charset.defaultCharset();
        return charset.encode(CharBuffer.wrap(toCharArray(text, start, end))).array();
    }

    public static boolean contentEquals(CharSequence text1, CharSequence text2) {
        if (text1 == text2) return true;
        else if (text1 == null || text2 == null) return false;
        else if (text1 instanceof String) return ((String) text1).contentEquals(text2);
        else if (text2 instanceof String) return ((String) text2).contentEquals(text1);
        else if (text1.length() != text2.length()) return false;
        else {
            for (int i = 0, length = text1.length(); i < length; i ++) {
                if (text1.charAt(i) != text2.charAt(i)) return false;
            }
            return true;
        }
    }

}
