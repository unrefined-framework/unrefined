package unrefined.io.console;

import unrefined.util.UnexpectedError;
import unrefined.util.foreign.Foreign;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Objects;

import static unrefined.io.ReadWriteIO.getFilteredOutputStream;

public class ConsoleStream extends OutputStream {

    private final FileDescriptor descriptor;
    private final PrintStream out;

    public ConsoleStream(FileDescriptor descriptor) {
        this.descriptor = descriptor;
        if (descriptor == FileDescriptor.out || descriptor == FileDescriptor.err) out = null;
        else out = new PrintStream(new FileOutputStream(descriptor));
    }

    public ConsoleStream(OutputStream out) {
        this.out = new PrintStream(Objects.requireNonNull(out));
        FileDescriptor descriptor;
        if ((out = getFilteredOutputStream(out)) instanceof FileOutputStream) {
            try {
                descriptor = ((FileOutputStream) out).getFD();
            } catch (IOException e) {
                descriptor = null;
            }
        }
        else descriptor = null;
        this.descriptor = descriptor;
    }

    public PrintStream toPrintStream() {
        return out == null ? (descriptor == FileDescriptor.out ? System.out : System.err) : out;
    }

    @Override
    public void write(byte[] b) throws IOException {
        toPrintStream().write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        toPrintStream().write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        toPrintStream().flush();
    }

    @Override
    public void close() throws IOException {
        toPrintStream().close();
    }

    @Override
    public void write(int b) throws IOException {
        toPrintStream().write(b);
    }

    public int print(Object value) {
        return print(value, Foreign.getInstance().systemCharset());
    }

    public int print(Object value, Charset charset) {
        return print(String.valueOf(value), charset);
    }

    public int print(boolean value) {
        return print(value, Foreign.getInstance().systemCharset());
    }

    public int print(boolean value, Charset charset) {
        return print(String.valueOf(value), charset);
    }

    public int print(byte value) {
        return print(value, Foreign.getInstance().systemCharset());
    }

    public int print(byte value, Charset charset) {
        return print(String.valueOf(value), charset);
    }

    public int print(char value) {
        return print(value, Foreign.getInstance().systemCharset());
    }

    public int print(char value, Charset charset) {
        return print(String.valueOf(value), charset);
    }

    public int print(short value) {
        return print(value, Foreign.getInstance().systemCharset());
    }

    public int print(short value, Charset charset) {
        return print(String.valueOf(value), charset);
    }

    public int print(int value) {
        return print(value, Foreign.getInstance().systemCharset());
    }

    public int print(int value, Charset charset) {
        return print(String.valueOf(value), charset);
    }

    public int print(long value) {
        return print(value, Foreign.getInstance().systemCharset());
    }

    public int print(long value, Charset charset) {
        return print(String.valueOf(value), charset);
    }

    public int print(float value) {
        return print(value, Foreign.getInstance().systemCharset());
    }

    public int print(float value, Charset charset) {
        return print(String.valueOf(value), charset);
    }

    public int print(double value) {
        return print(value, Foreign.getInstance().systemCharset());
    }

    public int print(double value, Charset charset) {
        return print(String.valueOf(value), charset);
    }

    public int print(String string) {
        return print(string, Foreign.getInstance().systemCharset());
    }

    public int print(String string, Charset charset) {
        if (charset == null) charset = Foreign.getInstance().systemCharset();
        byte[] bytes = (string = String.valueOf(string)).getBytes(charset);
        try {
            toPrintStream().write(bytes);
        } catch (IOException e) {
            throw new UnexpectedError(e);
        }
        return string.length();
    }

    public int println(Object value) {
        return println(value, Foreign.getInstance().systemCharset());
    }

    public int println(Object value, Charset charset) {
        return println(String.valueOf(value), charset);
    }

    public int println(boolean value) {
        return println(value, Foreign.getInstance().systemCharset());
    }

    public int println(boolean value, Charset charset) {
        return println(String.valueOf(value), charset);
    }

    public int println(byte value) {
        return println(value, Foreign.getInstance().systemCharset());
    }

    public int println(byte value, Charset charset) {
        return println(String.valueOf(value), charset);
    }

    public int println(char value) {
        return println(value, Foreign.getInstance().systemCharset());
    }

    public int println(char value, Charset charset) {
        return println(String.valueOf(value), charset);
    }

    public int println(short value) {
        return println(value, Foreign.getInstance().systemCharset());
    }

    public int println(short value, Charset charset) {
        return println(String.valueOf(value), charset);
    }

    public int println(int value) {
        return println(value, Foreign.getInstance().systemCharset());
    }

    public int println(int value, Charset charset) {
        return println(String.valueOf(value), charset);
    }

    public int println(long value) {
        return println(value, Foreign.getInstance().systemCharset());
    }

    public int println(long value, Charset charset) {
        return println(String.valueOf(value), charset);
    }

    public int println(float value) {
        return println(value, Foreign.getInstance().systemCharset());
    }

    public int println(float value, Charset charset) {
        return println(String.valueOf(value), charset);
    }

    public int println(double value) {
        return println(value, Foreign.getInstance().systemCharset());
    }

    public int println(double value, Charset charset) {
        return println(String.valueOf(value), charset);
    }

    public int println(String string) {
        return println(string, Foreign.getInstance().systemCharset());
    }

    public int println(String string, Charset charset) {
        if (charset == null) charset = Foreign.getInstance().systemCharset();
        byte[] bytes = (string = String.valueOf(string)).getBytes(charset);
        try {
            toPrintStream().write(bytes);
            toPrintStream().write(System.lineSeparator().getBytes(charset));
        } catch (IOException e) {
            throw new UnexpectedError(e);
        }
        return string.length();
    }

    public int println() {
        return print(System.lineSeparator());
    }

    public int println(Charset charset) {
        return println(System.lineSeparator(), charset);
    }

    public int printf(String format, Object... args) {
        return printf(format, Foreign.getInstance().systemCharset(), args);
    }

    public int printf(String format, Charset charset, Object... args) {
        if (charset == null) charset = Foreign.getInstance().systemCharset();
        byte[] bytes = (format = String.format(String.valueOf(format), args)).getBytes(charset);
        try {
            toPrintStream().write(bytes);
        } catch (IOException e) {
            throw new UnexpectedError(e);
        }
        return format.length();
    }

    public int printf(Locale locale, String format, Object... args) {
        return printf(locale, format, Foreign.getInstance().systemCharset(), args);
    }

    public int printf(Locale locale, String format, Charset charset, Object... args) {
        if (charset == null) charset = Foreign.getInstance().systemCharset();
        byte[] bytes = (format = String.format(locale, String.valueOf(format), args)).getBytes(charset);
        try {
            toPrintStream().write(bytes);
        } catch (IOException e) {
            throw new UnexpectedError(e);
        }
        return format.length();
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "descriptor=" + (out == null ? (descriptor == FileDescriptor.out ? "stdout" : "stderr") : descriptor) +
                '}';
    }

}
