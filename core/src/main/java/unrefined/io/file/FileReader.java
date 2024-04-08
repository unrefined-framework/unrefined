package unrefined.io.file;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class FileReader extends java.io.FileReader {

    private final Charset charset;
    private final InputStreamReader reader;

    public Charset getCharset() {
        return charset;
    }

    public FileReader(String pathname) throws IOException {
        this(pathname, null);
    }

    public FileReader(File file) throws IOException {
        this(file, null);
    }

    public FileReader(FileDescriptor fileDescriptor) {
        this(fileDescriptor, null);
    }

    public FileReader(FileDescriptor fileDescriptor, Charset charset) {
        super(fileDescriptor);
        this.charset = charset == null ? Charset.defaultCharset() : charset;
        this.reader = new InputStreamReader((InputStream) lock, this.charset);
    }

    public FileReader(String pathname, Charset charset) throws IOException {
        super(pathname);
        this.charset = charset == null ? Charset.defaultCharset() : charset;
        this.reader = new InputStreamReader((InputStream) lock, this.charset);
    }

    public FileReader(File file, Charset charset) throws IOException {
        super(file);
        this.charset = charset == null ? Charset.defaultCharset() : charset;
        this.reader = new InputStreamReader((InputStream) lock, this.charset);
    }

    public FileDescriptor getFD() throws IOException {
        return ((FileInputStream) lock).getFD();
    }

    public FileChannel getChannel() {
        return ((FileInputStream) lock).getChannel();
    }

    @Override
    public String getEncoding() {
        return reader.getEncoding();
    }

    @Override
    public int read(CharBuffer target) throws IOException {
        return reader.read(target);
    }

    @Override
    public int read() throws IOException {
        return reader.read();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return reader.read(cbuf, off, len);
    }

    @Override
    public boolean ready() throws IOException {
        return reader.ready();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public int read(char[] cbuf) throws IOException {
        return reader.read(cbuf);
    }

    @Override
    public long skip(long n) throws IOException {
        return reader.skip(n);
    }

    @Override
    public boolean markSupported() {
        return reader.markSupported();
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        reader.mark(readAheadLimit);
    }

    @Override
    public void reset() throws IOException {
        reader.reset();
    }

}
