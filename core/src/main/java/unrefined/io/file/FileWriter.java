package unrefined.io.file;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public class FileWriter extends java.io.FileWriter {

    private final Charset charset;
    private final OutputStreamWriter writer;

    public Charset getCharset() {
        return charset;
    }

    public FileWriter(String pathname) throws IOException {
        this(pathname, null);
    }

    public FileWriter(String pathname, boolean append) throws IOException {
        this(pathname, null, append);
    }

    public FileWriter(File file) throws IOException {
        this(file, null);
    }

    public FileWriter(File file, boolean append) throws IOException {
        this(file, null, append);
    }

    public FileWriter(FileDescriptor fileDescriptor) {
        this(fileDescriptor, null);
    }

    public FileWriter(String pathname, Charset charset) throws IOException {
        super(pathname);
        this.charset = charset == null ? Charset.defaultCharset() : charset;
        this.writer = new OutputStreamWriter((OutputStream) lock, this.charset);
    }

    public FileWriter(String pathname, Charset charset, boolean append) throws IOException {
        super(pathname, append);
        this.charset = charset == null ? Charset.defaultCharset() : charset;
        this.writer = new OutputStreamWriter((OutputStream) lock, this.charset);
    }

    public FileWriter(File file, Charset charset) throws IOException {
        super(file);
        this.charset = charset == null ? Charset.defaultCharset() : charset;
        this.writer = new OutputStreamWriter((OutputStream) lock, this.charset);
    }

    public FileWriter(File file, Charset charset, boolean append) throws IOException {
        super(file, append);
        this.charset = charset == null ? Charset.defaultCharset() : charset;
        this.writer = new OutputStreamWriter((OutputStream) lock, this.charset);
    }

    public FileWriter(FileDescriptor fileDescriptor, Charset charset) {
        super(fileDescriptor);
        this.charset = charset == null ? Charset.defaultCharset() : charset;
        this.writer = new OutputStreamWriter((OutputStream) lock, this.charset);
    }

    @Override
    public String getEncoding() {
        return writer.getEncoding();
    }

    @Override
    public void write(int c) throws IOException {
        writer.write(c);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        writer.write(cbuf, off, len);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        writer.write(str, off, len);
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        return writer.append(csq, start, end);
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        return writer.append(csq);
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        writer.write(cbuf);
    }

    @Override
    public void write(String str) throws IOException {
        writer.write(str);
    }

    @Override
    public Writer append(char c) throws IOException {
        return writer.append(c);
    }

}
