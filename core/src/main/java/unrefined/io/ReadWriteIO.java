package unrefined.io;

import unrefined.util.FastArray;
import unrefined.util.NotInstantiableError;
import unrefined.util.reflect.Reflection;

import java.io.Closeable;
import java.io.EOFException;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.FilterReader;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ReadWriteIO {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private ReadWriteIO() {
        throw new NotInstantiableError(ReadWriteIO.class);
    }

    private static final Field inFieldStream;
    private static final Field outFieldStream;
    static {
        Field field;
        try {
            field = FilterInputStream.class.getDeclaredField("in");
        } catch (NoSuchFieldException e) {
            field = null;
        }
        inFieldStream = field;
        try {
            field = FilterOutputStream.class.getDeclaredField("out");
        } catch (NoSuchFieldException e) {
            field = null;
        }
        outFieldStream = field;
    }

    public static InputStream getFilteredInputStream(InputStream in) {
        if (!(in instanceof FilterInputStream)) return in;
        return getFilteredInputStream((InputStream) Reflection.getInstance().getObjectField(in, inFieldStream));
    }

    public static OutputStream getFilteredOutputStream(OutputStream out) {
        if (!(out instanceof FilterOutputStream)) return out;
        return getFilteredOutputStream((OutputStream) Reflection.getInstance().getObjectField(out, outFieldStream));
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

    public int readNBytes(InputStream in, byte[] b, int off, int len) throws IOException {
        if (off < 0) throw new ArrayIndexOutOfBoundsException("Array index out of range: " + off);
        else if (len < 0) throw new ArrayIndexOutOfBoundsException("Array index out of range: " + len);
        else if (off + len > b.length) throw new ArrayIndexOutOfBoundsException("Array index out of range: " + (off + len));

        int n = 0;
        while (n < len) {
            int count = in.read(b, off + n, len - n);
            if (count < 0)
                break;
            n += count;
        }
        return n;
    }

    public int readAllBytes(InputStream in, byte[] b) throws IOException {
        return readNBytes(in, b, 0, b.length);
    }

    public static byte[] readNBytes(InputStream in, int n) throws IOException {
        if (n < 0) throw new IllegalArgumentException("n < 0");
        Objects.requireNonNull(in, "in");

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

    public void skipNBytes(InputStream in, int n) throws IOException {
        while (n > 0) {
            int ns = (int) in.skip(n);
            if (ns > 0 && ns <= n) {
                n -= ns;
            } else if (ns == 0) {
                if (in.read() == -1) throw new EOFException();
                n --;
            } else {
                throw new IOException("Unable to skip exactly");
            }
        }
    }

    public void skipNBytes(InputStream in, long n) throws IOException {
        while (n > 0) {
            long ns = in.skip(n);
            if (ns > 0 && ns <= n) {
                n -= ns;
            } else if (ns == 0) {
                if (in.read() == -1) throw new EOFException();
                n --;
            } else {
                throw new IOException("Unable to skip exactly");
            }
        }
    }

    public static void discardNBytes(InputStream in, int n) throws IOException {
        if (n < 0) throw new IllegalArgumentException("n < 0");
        Objects.requireNonNull(in, "in");

        int remaining = n;
        byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
        int nr;
        do {
            int nread = 0;
            while ((nr = in.read(buf, nread, Math.min(buf.length - nread, remaining))) > 0) {
                nread += nr;
                remaining -= nr;
            }
        } while (nr >= 0 && remaining > 0);
    }

    public static void discardNBytes(InputStream in, long n) throws IOException {
        if (n < 0) throw new IllegalArgumentException("n < 0");
        Objects.requireNonNull(in, "in");
        int last = (int) (n % FastArray.ARRAY_LENGTH_MAX);
        long times = n / FastArray.ARRAY_LENGTH_MAX;
        for (long i = 0; i < times; i ++) {
            discardNBytes(in, FastArray.ARRAY_LENGTH_MAX);
        }
        if (last > 0) discardNBytes(in, last);
    }

    public static void discardAllBytes(InputStream in) throws IOException {
        Objects.requireNonNull(in, "in");

        byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
        int nr;
        do {
            nr = in.read(buf, 0, DEFAULT_BUFFER_SIZE);
        } while (nr >= 0);
    }

    private static final Field inFieldReader;
    private static final Field outFieldWriter;
    private static final Field lockFieldReader;
    private static final Field lockFieldWriter;
    static {
        Field field;
        try {
            field = FilterReader.class.getDeclaredField("in");
        } catch (NoSuchFieldException e) {
            field = null;
        }
        inFieldReader = field;
        try {
            field = FilterWriter.class.getDeclaredField("out");
        } catch (NoSuchFieldException e) {
            field = null;
        }
        outFieldWriter = field;
        try {
            field = Reader.class.getDeclaredField("lock");
        } catch (NoSuchFieldException e) {
            field = null;
        }
        lockFieldReader = field;
        try {
            field = Writer.class.getDeclaredField("lock");
        } catch (NoSuchFieldException e) {
            field = null;
        }
        lockFieldWriter = field;
    }

    public static Reader getFilteredReader(Reader in) {
        if (!(in instanceof FilterReader)) return in;
        return getFilteredReader((Reader) Reflection.getInstance().getObjectField(in, inFieldReader));
    }

    public static Writer getFilteredWriter(Writer out) {
        if (!(out instanceof FilterWriter)) return out;
        return getFilteredWriter((Writer) Reflection.getInstance().getObjectField(out, outFieldWriter));
    }

    public static Object getLock(Reader in) {
        return Reflection.getInstance().getObjectField(in, lockFieldReader);
    }

    public static Object getLock(Writer out) {
        return Reflection.getInstance().getObjectField(out, lockFieldWriter);
    }

    public static InputStream getInputStream(InputStreamReader in) {
        return (InputStream) getLock(in);
    }

    public static OutputStream getOutputStream(OutputStreamWriter out) {
        return (OutputStream) getLock(out);
    }

    public static boolean isStandardWriter(Writer out) {
        out = getFilteredWriter(out);
        if (out instanceof OutputStreamWriter) {
            return isStandardOutputStream(getOutputStream((OutputStreamWriter) out));
        }
        else return false;
    }

    public static boolean isStandardError(Writer out) {
        out = getFilteredWriter(out);
        if (out instanceof OutputStreamWriter) {
            return isStandardError(getOutputStream((OutputStreamWriter) out));
        }
        else return false;
    }

    public static boolean isStandardOutput(Writer out) {
        out = getFilteredWriter(out);
        if (out instanceof OutputStreamWriter) {
            return isStandardOutput(getOutputStream((OutputStreamWriter) out));
        }
        else return false;
    }

    public static boolean isStandardInput(Reader in) {
        in = getFilteredReader(in);
        if (in instanceof InputStreamReader) {
            return isStandardInput(getInputStream((InputStreamReader) in));
        }
        else return false;
    }

    public static long transfer(InputStream in, OutputStream out) throws IOException {
        Objects.requireNonNull(in, "in");
        Objects.requireNonNull(out, "out");
        long transferred = 0;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;
        while ((read = in.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
            out.write(buffer, 0, read);
            if (transferred < Long.MAX_VALUE) {
                try {
                    transferred = Math.addExact(transferred, read);
                } catch (ArithmeticException ignore) {
                    transferred = Long.MAX_VALUE;
                }
            }
        }
        return transferred;
    }

    public static long transfer(Reader in, Writer out) throws IOException {
        Objects.requireNonNull(in, "in");
        Objects.requireNonNull(out, "out");
        long transferred = 0;
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        int nRead;
        while ((nRead = in.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
            out.write(buffer, 0, nRead);
            if (transferred < Long.MAX_VALUE) {
                try {
                    transferred = Math.addExact(transferred, nRead);
                } catch (ArithmeticException ignore) {
                    transferred = Long.MAX_VALUE;
                }
            }
        }
        return transferred;
    }

}
