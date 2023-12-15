package unrefined.util;

import unrefined.context.Environment;
import unrefined.internal.BitwiseUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import static unrefined.util.Base64.Flag.DEFAULT;

public abstract class Base64 {

    private static volatile Base64 INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Base64 getBase64() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global().get("unrefined.runtime.base64", Base64.class);
        }
        return INSTANCE;
    }

    public static final class Flag {
        private Flag() {
            throw new NotInstantiableError(Flag.class);
        }
        public static final int DEFAULT = 0;
        public static final int NO_PADDING = 1;
        public static final int NO_WRAP = 1 << 1;
        public static final int CRLF = 1 << 2;
        public static final int URL_SAFE = 1 << 3;
        public static int removeUnusedBits(int flags) {
            return BitwiseUtils.removeUnusedBits(flags, 4);
        }
        public static String toString(int flags) {
            flags = removeUnusedBits(flags);
            StringBuilder builder = new StringBuilder("[");
            if ((flags & NO_PADDING) == NO_PADDING) builder.append("NO_PADDING");
            else builder.append("DEFAULT");
            if ((flags & NO_WRAP) == NO_WRAP) builder.append(", NO_WRAP");
            if ((flags & CRLF) == CRLF) builder.append(", CRLF");
            if ((flags & URL_SAFE) == URL_SAFE) builder.append(", URL_SAFE");
            builder.append("]");
            return builder.toString();
        }
    }

    public byte[] encode(ByteBuffer input) {
        return encode(input, DEFAULT);
    }
    public byte[] encode(ByteBuffer input, int flags) {
        if (input.hasArray()) {
            byte[] encoded = encode(input.array(), input.arrayOffset() + input.position(), input.remaining(), flags);
            input.position(input.position() + input.remaining());
            return encoded;
        }
        else {
            byte[] buffer = new byte[input.remaining()];
            input.get(buffer);
            return encode(buffer, flags);
        }
    }
    public byte[] encode(byte[] input, int flags) {
        return encode(input, 0, input.length, flags);
    }
    public byte[] encode(byte[] input) {
        return encode(input, DEFAULT);
    }
    public byte[] encode(byte[] input, int offset, int length) {
        return encode(input, offset, length, DEFAULT);
    }
    public abstract byte[] encode(byte[] input, int offset, int length, int flags);
    public abstract OutputStream wrap(OutputStream out, int flags);
    public byte[] decode(ByteBuffer input) {
        return decode(input, DEFAULT);
    }
    public byte[] decode(ByteBuffer input, int flags) {
        if (input.hasArray()) {
            byte[] decoded = decode(input.array(), input.arrayOffset() + input.position(), input.remaining(), flags);
            input.position(input.position() + input.remaining());
            return decoded;
        }
        else {
            byte[] buffer = new byte[input.remaining()];
            input.get(buffer);
            return decode(buffer, flags);
        }
    }
    public byte[] decode(byte[] input, int flags) {
        return decode(input, 0, input.length, flags);
    }
    public byte[] decode(byte[] input) {
        return decode(input, DEFAULT);
    }
    public byte[] decode(byte[] input, int offset, int length) {
        return decode(input, offset, length, DEFAULT);
    }
    public abstract byte[] decode(byte[] input, int offset, int length, int flags);
    public abstract InputStream wrap(InputStream in, int flags);

}
