package unrefined.io.file;

import unrefined.io.RandomAccessDataInput;
import unrefined.io.RandomAccessDataOutput;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Objects;

import static unrefined.io.file.FileSystem.OpenOption.*;

public class ChannelFile implements RandomAccessDataInput, RandomAccessDataOutput {

    private final RandomAccessFile randomAccessFile;
    private final File file;
    private final int options;
    private final boolean deleteOnClose;
    public ChannelFile(File file, int options) throws IOException {
        Objects.requireNonNull(file);
        this.options = options = FileSystem.OpenOption.removeUnusedBits(options);
        randomAccessFile = new RandomAccessFile(file, toRandomAccessFileMode(options));
        this.file = file;
        if ((options & TRUNCATE_EXISTING) != 0 && file.exists()) randomAccessFile.setLength(0);
        if ((options & APPEND) != 0) randomAccessFile.seek(randomAccessFile.length());
        deleteOnClose = (options & DELETE_ON_CLOSE) != 0;
        if (deleteOnClose) file.deleteOnExit();
        if ((options & CREATE_NEW) != 0 && file.exists()) throw new IOException("file already exists");
    }
    public ChannelFile(String pathname, int options) throws IOException {
        this(new File(pathname), options);
    }

    public int getOpenOptions() {
        return options;
    }

    public File asFile() {
        return file;
    }

    public RandomAccessFile asRandomAccessFile() {
        return randomAccessFile;
    }

    public FileChannel getFileChannel() {
        return randomAccessFile.getChannel();
    }

    public FileDescriptor getFD() throws IOException {
        return randomAccessFile.getFD();
    }

    private static String toRandomAccessFileMode(int mode) {
        if (mode == READ) return "r";
        else if ((mode & SYNC) != 0) return "rws";
        else if ((mode & DSYNC) != 0) return "rwd";
        else return "rw";
    }

    @Override
    public int read() throws IOException {
        return randomAccessFile.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return randomAccessFile.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return randomAccessFile.read(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        randomAccessFile.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        randomAccessFile.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        randomAccessFile.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        randomAccessFile.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        randomAccessFile.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        randomAccessFile.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        randomAccessFile.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        randomAccessFile.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        randomAccessFile.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        randomAccessFile.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        randomAccessFile.writeDouble(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        randomAccessFile.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        randomAccessFile.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        randomAccessFile.writeUTF(s);
    }

    @Override
    public long length() throws IOException {
        return randomAccessFile.length();
    }

    @Override
    public void seek(long pos) throws IOException {
        randomAccessFile.seek(pos);
    }

    @Override
    public long position() throws IOException {
        return randomAccessFile.getFilePointer();
    }

    @Override
    public void setLength(long newLength) throws IOException {
        randomAccessFile.setLength(newLength);
    }

    @Override
    public long skipBytes(long n) throws IOException {
        long newpos;
        if (n <= 0) return 0;
        long pos = position();
        long len = length();
        newpos = pos + n;
        if (newpos > len) newpos = len;
        seek(newpos);
        return newpos - pos;
    }

    @Override
    public void flush() throws IOException {
        randomAccessFile.getFD().sync();
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
        if (deleteOnClose && !file.delete()) throw new IOException("cannot delete file");
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        randomAccessFile.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        randomAccessFile.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return randomAccessFile.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return randomAccessFile.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return randomAccessFile.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return randomAccessFile.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return randomAccessFile.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return randomAccessFile.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return randomAccessFile.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return randomAccessFile.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return randomAccessFile.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return randomAccessFile.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return randomAccessFile.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return randomAccessFile.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return randomAccessFile.readUTF();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChannelFile that = (ChannelFile) o;

        if (options != that.options) return false;
        if (deleteOnClose != that.deleteOnClose) return false;
        if (!randomAccessFile.equals(that.randomAccessFile)) return false;
        return file.equals(that.file);
    }

    @Override
    public int hashCode() {
        int result = randomAccessFile.hashCode();
        result = 31 * result + file.hashCode();
        result = 31 * result + options;
        result = 31 * result + (deleteOnClose ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "path=" + file.getAbsolutePath() +
                ", mode=" + FileSystem.OpenOption.toString(getOpenOptions()) +
                '}';
    }

}
