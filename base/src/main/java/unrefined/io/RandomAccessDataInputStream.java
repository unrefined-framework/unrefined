package unrefined.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class RandomAccessDataInputStream extends InputStream implements RandomAccessDataInput {

    protected final RandomAccessDataInput input;
    private volatile int readlimit = -1;
    private volatile long markpos = -1;

    public RandomAccessDataInputStream(RandomAccessDataInput input) {
        this.input = Objects.requireNonNull(input);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return input.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return input.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return input.skipBytes(n);
    }

    @Override
    public int available() throws IOException {
        return (int) Math.min(Integer.MAX_VALUE, input.length() - input.position());
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        if (readlimit < 0) throw new IllegalArgumentException("Negative read limit");
        try {
            markpos = input.position();
            this.readlimit = readlimit;
        } catch (IOException ignored) {
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        if (markpos + readlimit < input.position()) markpos = -1;
        if (markpos < 0) throw new IOException("Resetting to invalid mark");
        input.seek(markpos);
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() throws IOException {
        return input.readUnsignedByte();
    }

    @Override
    public long length() throws IOException {
        return input.length();
    }

    @Override
    public void seek(long pos) throws IOException {
        input.seek(pos);
    }

    @Override
    public long position() throws IOException {
        return input.position();
    }

    @Override
    public long skipBytes(long n) throws IOException {
        return input.skipBytes(n);
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        input.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        input.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return input.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return input.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return input.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return input.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return input.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return input.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return input.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return input.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return input.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return input.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return input.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return input.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return input.readUTF();
    }

}
