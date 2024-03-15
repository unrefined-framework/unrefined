package unrefined.io;

import unrefined.util.FastArray;
import unrefined.util.NotInstantiableError;
import unrefined.util.reflect.Reflection;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class IOStreams {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private IOStreams() {
        throw new NotInstantiableError(IOStreams.class);
    }

    private static final Field outField;
    static {
        Field field;
        try {
            field = FilterOutputStream.class.getDeclaredField("out");
        } catch (NoSuchFieldException e) {
            field = null;
        }
        outField = field;
    }

    public static OutputStream getFilteredOutputStream(OutputStream out) {
        if (!(out instanceof FilterOutputStream)) return out;
        return getFilteredOutputStream((OutputStream) Reflection.getInstance().getObjectField(out, outField));
    }

    public static boolean isStandardOutputStream(OutputStream out) {
        out = getFilteredOutputStream(out);
        if (out instanceof FileOutputStream) {
            try {
                FileDescriptor fd = ((FileOutputStream) out).getFD();
                if (fd == FileDescriptor.out || fd == FileDescriptor.err) return true;
            } catch (IOException ignored) {
            }
        }
        return false;
    }

    public static boolean isStandardError(OutputStream out) {
        out = getFilteredOutputStream(out);
        if (out instanceof FileOutputStream) {
            try {
                FileDescriptor fd = ((FileOutputStream) out).getFD();
                if (fd == FileDescriptor.err) return true;
            } catch (IOException ignored) {
            }
        }
        return false;
    }

    public static boolean isStandardOutput(OutputStream out) {
        out = getFilteredOutputStream(out);
        if (out instanceof FileOutputStream) {
            try {
                FileDescriptor fd = ((FileOutputStream) out).getFD();
                if (fd == FileDescriptor.out) return true;
            } catch (IOException ignored) {
            }
        }
        return false;
    }

    private static final Field inField;
    static {
        Field field;
        try {
            field = FilterInputStream.class.getDeclaredField("in");
        } catch (NoSuchFieldException e) {
            field = null;
        }
        inField = field;
    }

    public static InputStream getFilteredInputStream(InputStream in) {
        if (!(in instanceof FilterInputStream)) return in;
        return getFilteredInputStream((InputStream) Reflection.getInstance().getObjectField(in, inField));
    }

    public static boolean isStandardInput(InputStream in) {
        in = getFilteredInputStream(in);
        if (in instanceof FileInputStream) {
            try {
                FileDescriptor fd = ((FileInputStream) in).getFD();
                if (fd == FileDescriptor.in) return true;
            }
            catch (IOException ignored) {
            }
        }
        return false;
    }

    public static void closeQuietly(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Exception ignored) {
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }

    public static byte[] readAllBytes(InputStream in) throws IOException {
        return readNBytes(in, FastArray.ARRAY_LENGTH_MAX);
    }

    public static byte[] readNBytes(InputStream in, int n) throws IOException {
        if (n < 0) throw new IllegalArgumentException("n < 0");
        Objects.requireNonNull(in);

        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = n;
        int nr;
        do {
            byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
            int nread = 0;

            // read to EOF which may read more or less than buffer size
            while ((nr = in.read(buf, nread,
                    Math.min(buf.length - nread, remaining))) > 0) {
                nread += nr;
                remaining -= nr;
            }

            if (nread > 0) {
                if (FastArray.ARRAY_LENGTH_MAX - total < nread) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                if (nread < buf.length) {
                    buf = Arrays.copyOfRange(buf, 0, nread);
                }
                total += nread;
                if (result == null) {
                    result = buf;
                } else {
                    if (bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (nr >= 0 && remaining > 0);

        if (bufs == null) {
            if (result == null) {
                return new byte[0];
            }
            return result.length == total ?
                    result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }

        return result;
    }

    public static void discardNBytes(InputStream in, int n) throws IOException {
        if (n < 0) throw new IllegalArgumentException("n < 0");
        Objects.requireNonNull(in);

        int remaining = n;
        int nr;
        do {
            byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
            int nread = 0;

            // read to EOF which may read more or less than buffer size
            while ((nr = in.read(buf, nread,
                    Math.min(buf.length - nread, remaining))) > 0) {
                nread += nr;
                remaining -= nr;
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (nr >= 0 && remaining > 0);
    }

    public static void discardNBytes(InputStream in, long n) throws IOException {
        if (n < 0) throw new IllegalArgumentException("n < 0");
        Objects.requireNonNull(in);
        int last = (int) (n % FastArray.ARRAY_LENGTH_MAX);
        long times = n / FastArray.ARRAY_LENGTH_MAX;
        for (long i = 0; i < times; i ++) {
            discardNBytes(in, FastArray.ARRAY_LENGTH_MAX);
        }
        if (last > 0) discardNBytes(in, last);
    }

}
