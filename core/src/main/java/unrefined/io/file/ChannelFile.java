package unrefined.io.file;

import unrefined.io.RandomAccessDataInput;
import unrefined.io.RandomAccessDataOutput;
import unrefined.util.concurrent.ConcurrentHashSet;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;
import java.util.Set;

import static unrefined.io.file.FileSystem.OpenOption.*;

public class ChannelFile extends RandomAccessFile implements RandomAccessDataInput, RandomAccessDataOutput {

    private static final Set<Runnable> CLEANERS = new ConcurrentHashSet<>();
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            synchronized (CLEANERS) {
                for (Runnable runnable : CLEANERS) {
                    runnable.run();
                }
            }
        }, "Unrefined ChannelFile Cleanup"));
    }

    private final File file;
    private final int mode;
    private final boolean deleteOnClose;
    private final Runnable cleaner;

    public ChannelFile(File file, int options) throws IOException {
        super(Objects.requireNonNull(file), toRandomAccessFileMode(file, options));
        this.mode = options = FileSystem.OpenOption.removeUnusedBits(options);
        this.file = file;
        if (options != READ && (options & TRUNCATE_EXISTING) != 0 && file.exists()) setLength(0);
        if ((options & WRITE) != 0 && (options & APPEND) != 0) seek(length());
        deleteOnClose = (options & DELETE_ON_CLOSE) != 0;
        if (deleteOnClose) CLEANERS.add(cleaner = file::delete);
        else cleaner = null;
    }

    private static String toRandomAccessFileMode(File file, int options) throws IOException {
        if ((options & CREATE_NEW) != 0 && file.exists()) throw new IOException("File already exists");
        if (options == READ) {
            if (!(file.exists()) && (options & CREATE) != 0 || (options & CREATE_NEW) != 0) {
                if (!file.createNewFile()) throw new IOException("Cannot create file: " + file);
            }
            return "r";
        }
        else if ((options & SYNC) != 0) return "rws";
        else if ((options & DSYNC) != 0) return "rwd";
        else return "rw";
    }

    public ChannelFile(String pathname, int options) throws IOException {
        this(new File(pathname), options);
    }

    public ChannelFile(File file) throws IOException {
        this(file, READ | WRITE);
    }

    public ChannelFile(String pathname) throws IOException {
        this(pathname, READ | WRITE);
    }

    public File getFile() {
        return file;
    }

    @Override
    public long position() throws IOException {
        return getFilePointer();
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
        getFD().sync();
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (deleteOnClose) {
            CLEANERS.remove(cleaner);
            if (!file.delete()) throw new IOException("Cannot delete file: " + file);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChannelFile that = (ChannelFile) o;

        if (mode != that.mode) return false;
        if (deleteOnClose != that.deleteOnClose) return false;
        return file.equals(that.file);
    }

    @Override
    public int hashCode() {
        int result = file.hashCode();
        result = 31 * result + mode;
        result = 31 * result + (deleteOnClose ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "path=" + file.getAbsolutePath() +
                ", mode=" + FileSystem.OpenOption.toString(mode) +
                '}';
    }

}
