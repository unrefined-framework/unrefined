package unrefined.runtime;

import unrefined.desktop.ReflectionSupport;
import unrefined.util.Base64;
import unrefined.util.UnexpectedError;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Objects;

import static unrefined.util.Base64.Flag.*;

public class DesktopBase64 extends Base64 {

    private static final int MIMELINEMAX = 76;
    private static final byte[] CRLF = new byte[] {'\r', '\n'};
    private static final byte[] LF = new byte[] {'\n'};

    private static final Constructor<java.util.Base64.Encoder> base64EncoderConstructor;
    static {
        try {
            base64EncoderConstructor = java.util.Base64.Encoder
                    .class.getDeclaredConstructor(boolean.class, byte[].class, int.class, boolean.class);
        } catch (NoSuchMethodException e) {
            throw new UnexpectedError(e);
        }
    }

    private static java.util.Base64.Encoder getEncoder(int flags) {
        flags = Flag.removeUnusedBits(flags);
        try {
            int linemax = (flags & NO_WRAP) != 0 ? -1 : MIMELINEMAX;
            return ReflectionSupport.newInstance(
                    base64EncoderConstructor,
                   (flags & URL_SAFE) != 0,
                   (flags & Flag.CRLF) != 0 ? CRLF : (linemax > 0 ? LF : null),
                   linemax,
                   (flags & NO_PADDING) == 0
            );
        } catch (InvocationTargetException | InstantiationException e) {
            throw new UnexpectedError(e);
        }
    }

    private static java.util.Base64.Decoder getDecoder(int flags) {
        if ((flags & URL_SAFE) != 0) return java.util.Base64.getUrlDecoder();
        else return java.util.Base64.getDecoder();
    }

    @Override
    public byte[] encode(byte[] input, int offset, int length, int flags) {
        return getEncoder(flags).encode(ByteBuffer.wrap(input, offset, length)).array();
    }

    @Override
    public OutputStream wrap(OutputStream out, int flags) {
        Objects.requireNonNull(out);
        return getEncoder(flags).wrap(out);
    }

    @Override
    public byte[] decode(byte[] input, int offset, int length, int flags) {
        return getDecoder(flags).decode(ByteBuffer.wrap(input, offset, length)).array();
    }

    @Override
    public InputStream wrap(InputStream in, int flags) {
        Objects.requireNonNull(in);
        return getDecoder(flags).wrap(in);
    }

}
