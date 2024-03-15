package unrefined.io;

import unrefined.util.NotInstantiableError;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Objects;

import static unrefined.io.ChannelFile.Mode.*;

public class ChannelFile implements RandomAccessDataInput, RandomAccessDataOutput {

    public static final class Mode {
        private Mode() {
            throw new NotInstantiableError(Mode.class);
        }
        public static final int READ              = 0;
        public static final int WRITE             = 1;
        public static final int APPEND            = 1 << 1;
        public static final int TRUNCATE_EXISTING = 1 << 2;
        public static final int CREATE            = 1 << 3;
        public static final int CREATE_NEW        = 1 << 4;
        public static final int DELETE_ON_CLOSE   = 1 << 5;
        public static final int SYNC              = 1 << 6;
        public static final int DSYNC             = 1 << 7;
        public static int removeUnusedBits(int mode) {
            return mode << 24 >>> 24;
        }
        public static String toString(int mode) {
            mode = removeUnusedBits(mode);
            if (mode == READ) return "[READ]";
            else {
                StringBuilder builder = new StringBuilder("[READ, WRITE");
                if ((mode & TRUNCATE_EXISTING) != 0) builder.append(", TRUNCATE_EXISTING");
                else if ((mode & APPEND) != 0) builder.append(", APPEND");
                if ((mode & CREATE_NEW) != 0) builder.append(", CREATE_NEW");
                else builder.append(", CREATE");
                if ((mode & DELETE_ON_CLOSE) != 0) builder.append(", DELETE_ON_CLOSE");
                if ((mode & SYNC) != 0) builder.append(", SYNC");
                else if ((mode & DSYNC) != 0) builder.append(", DSYNC");
                builder.append("]");
                return builder.toString();
            }
        }
    }

    private final RandomAccessFile randomAccessFile;
    private final File file;
    private final int mode;
    private final boolean alreadyExists;
    private final boolean deleteOnClose;
    public ChannelFile(File file, int mode) throws IOException {
        Objects.requireNonNull(file);
        this.mode = mode = Mode.removeUnusedBits(mode);
        randomAccessFile = new RandomAccessFile(file, toRandomAccessFileMode(mode));
        this.file = file;
        if ((mode & TRUNCATE_EXISTING) != 0 && file.exists()) randomAccessFile.setLength(0);
        if ((mode & APPEND) != 0) randomAccessFile.seek(randomAccessFile.length());
        deleteOnClose = (mode & DELETE_ON_CLOSE) != 0;
        if (deleteOnClose) file.deleteOnExit();
        alreadyExists = (mode & CREATE_NEW) != 0 && file.exists();
    }

    public int getMode() {
        return mode;
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
        if (alreadyExists) throw new IOException("file already exists");
        randomAccessFile.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (alreadyExists) throw new IOException("file already exists");
        randomAccessFile.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (alreadyExists) throw new IOException("file already exists");
        randomAccessFile.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        if (alreadyExists) throw new IOException("file already exists");
        randomAccessFile.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        if (alreadyExists) throw new IOException("file already exists");
        randomAccessFile.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        if (alreadyExists) throw new IOException("file already exists");
        randomAccessFile.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        if (alreadyExists) throw new IOException("file already exists");
        randomAccessFile.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        if (alreadyExists) throw new IOException("file already exists");
        randomAccessFile.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        if (alreadyExists) throw new IOException("file already exists");
        randomAccessFile.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        if (alreadyExists) throw new IOException("file already exists");
        randomAccessFile.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        if (alreadyExists) throw new IOException("file already exists");
        randomAccessFile.writeDouble(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        if (alreadyExists) throw new IOException("file already exists");
        randomAccessFile.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        if (alreadyExists) throw new IOException("file already exists");
        randomAccessFile.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        if (alreadyExists) throw new IOException("file already exists");
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
        if (alreadyExists) throw new IOException("file already exists");
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
        if (alreadyExists) throw new IOException("file already exists");
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

        if (mode != that.mode) return false;
        if (alreadyExists != that.alreadyExists) return false;
        if (deleteOnClose != that.deleteOnClose) return false;
        if (!randomAccessFile.equals(that.randomAccessFile)) return false;
        return file.equals(that.file);
    }

    @Override
    public int hashCode() {
        int result = randomAccessFile.hashCode();
        result = 31 * result + file.hashCode();
        result = 31 * result + mode;
        result = 31 * result + (alreadyExists ? 1 : 0);
        result = 31 * result + (deleteOnClose ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "path=" + file.getAbsolutePath() +
                ", mode=" + Mode.toString(getMode()) +
                '}';
    }

}
