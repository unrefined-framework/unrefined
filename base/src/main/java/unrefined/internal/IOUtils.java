package unrefined.internal;

import unrefined.util.NotInstantiableError;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class IOUtils {

    private IOUtils() {
        throw new NotInstantiableError(IOUtils.class);
    }

    private static final int BUFFER_SIZE = 8192;

    public static void discardAllBytes(InputStream stream) throws IOException {
        Objects.requireNonNull(stream);
        byte[] buf = new byte[BUFFER_SIZE];
        while (stream.read(buf) != -1) {
        }
    }

    public static void closeQuietly(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception ignored) {
        }
    }

}
