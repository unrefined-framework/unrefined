package unrefined.io.asset;

import unrefined.io.RandomAccessDataInput;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

public class RandomAccessAsset implements RandomAccessDataInput {

    protected final Asset asset;
    protected AssetOffsetCountInputStream stream;

    private DataInputStream readerStream = null;

    public RandomAccessAsset(Asset asset) {
        this.asset = Objects.requireNonNull(asset);
    }

    private void ensureStreamOpen() throws IOException {
        if (readerStream == null) readerStream = new DataInputStream(stream = new AssetOffsetCountInputStream(asset));
    }

    @Override
    public long length() throws IOException {
        return asset.length();
    }

    @Override
    public void seek(long pos) throws IOException {
        long diff = pos - position();
        if (diff < 0) {
            if (readerStream != null) {
                readerStream.close();
                readerStream = null;
            }
            ensureStreamOpen();
            skipBytes(pos);
        }
        else if (diff > 0) skipBytes(diff);
    }

    @Override
    public long position() throws IOException {
        ensureStreamOpen();
        return stream.offset();
    }

    @Override
    public long skipBytes(long n) throws IOException {
        ensureStreamOpen();
        long total = 0;
        long curr;
        while ((total < n) && ((curr = (int) readerStream.skip(n - total)) > 0)) {
            total += curr;
        }
        return total;
    }

    @Override
    public void close() throws IOException {
        ensureStreamOpen();
        readerStream.close();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        ensureStreamOpen();
        readerStream.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        ensureStreamOpen();
        readerStream.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        ensureStreamOpen();
        return readerStream.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        ensureStreamOpen();
        return readerStream.readBoolean();
    }

    @Override
    public int read() throws IOException {
        ensureStreamOpen();
        return readerStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        ensureStreamOpen();
        return readerStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        ensureStreamOpen();
        return readerStream.read(b, off, len);
    }

    @Override
    public byte readByte() throws IOException {
        ensureStreamOpen();
        return readerStream.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        ensureStreamOpen();
        return readerStream.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        ensureStreamOpen();
        return readerStream.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        ensureStreamOpen();
        return readerStream.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        ensureStreamOpen();
        return readerStream.readChar();
    }

    @Override
    public int readInt() throws IOException {
        ensureStreamOpen();
        return readerStream.readInt();
    }

    @Override
    public long readLong() throws IOException {
        ensureStreamOpen();
        return readerStream.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        ensureStreamOpen();
        return readerStream.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        ensureStreamOpen();
        return readerStream.readDouble();
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        ensureStreamOpen();
        return readerStream.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        ensureStreamOpen();
        return readerStream.readUTF();
    }

}
