package unrefined.io.file;

import unrefined.context.Environment;
import unrefined.util.NotInstantiableError;
import unrefined.util.ScopedIterable;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public abstract class FileSystem {

    private static volatile FileSystem INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static FileSystem getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.fileSystem", FileSystem.class);
        }
        return INSTANCE;
    }

    public static final class OpenOption {
        private OpenOption() {
            throw new NotInstantiableError(OpenOption.class);
        }
        public static final int READ              = 0;
        public static final int WRITE             = 1;
        public static final int APPEND            = 1 << 1;
        public static final int TRUNCATE_EXISTING = 1 << 2;
        public static final int CREATE            = 1 << 3;
        public static final int CREATE_NEW        = 1 << 4;
        public static final int DELETE_ON_CLOSE   = 1 << 5;
        public static final int SPARSE            = 1 << 6;
        public static final int SYNC              = 1 << 7;
        public static final int DSYNC             = 1 << 8;
        public static int removeUnusedBits(int options) {
            return options << 23 >>> 23;
        }
        public static String toString(int options) {
            options = removeUnusedBits(options);
            if (options == READ) return "[READ]";
            else {
                StringBuilder builder = new StringBuilder("[READ, WRITE");
                if ((options & TRUNCATE_EXISTING) != 0) builder.append(", TRUNCATE_EXISTING");
                else if ((options & APPEND) != 0) builder.append(", APPEND");
                if ((options & CREATE_NEW) != 0) builder.append(", CREATE_NEW");
                else builder.append(", CREATE");
                if ((options & DELETE_ON_CLOSE) != 0) builder.append(", DELETE_ON_CLOSE");
                if ((options & SPARSE) != 0) builder.append(", SPARSE");
                if ((options & SYNC) != 0) builder.append(", SYNC");
                else if ((options & DSYNC) != 0) builder.append(", DSYNC");
                builder.append("]");
                return builder.toString();
            }
        }
    }

    public static final class CopyOption {
        private CopyOption() {
            throw new NotInstantiableError(CopyOption.class);
        }
        public static final int MOVE              = 0;
        public static final int REPLACE_EXISTING  = 1;
        public static final int COPY_ATTRIBUTES   = 1 << 1;
        public static final int ATOMIC_MOVE       = 1 << 2;
        public static int removeUnusedBits(int options) {
            return options << 29 >>> 29;
        }
        public static String toString(int options) {
            options = removeUnusedBits(options);
            if (options == MOVE) return "[MOVE]";
            else {
                StringBuilder builder = new StringBuilder("[MOVE");
                if ((options & REPLACE_EXISTING) != 0) builder.append(", REPLACE_EXISTING");
                if ((options & COPY_ATTRIBUTES) != 0) builder.append(", COPY_ATTRIBUTES");
                if ((options & ATOMIC_MOVE) != 0) builder.append(", ATOMIC_MOVE");
                builder.append("]");
                return builder.toString();
            }
        }
    }

    public static final class Mode {
        private Mode() {
            throw new NotInstantiableError(Mode.class);
        }
        public static final int OWNER_READ = 1 << 8;
        public static final int OWNER_WRITE = 1 << 7;
        public static final int OWNER_EXECUTE = 1 << 6;
        public static final int GROUP_READ = 1 << 5;
        public static final int GROUP_WRITE = 1 << 4;
        public static final int GROUP_EXECUTE = 1 << 3;
        public static final int OTHERS_READ = 1 << 2;
        public static final int OTHERS_WRITE = 1 << 1;
        public static final int OTHERS_EXECUTE = 1;
        public static final int OWNER_RW = OWNER_READ | OWNER_WRITE;
        public static final int OWNER_ALL = OWNER_READ | OWNER_WRITE | OWNER_EXECUTE;
        public static final int GROUP_RW = GROUP_READ | GROUP_WRITE;
        public static final int GROUP_ALL = GROUP_READ | GROUP_WRITE | GROUP_EXECUTE;
        public static final int OTHERS_RW = OTHERS_READ | OTHERS_WRITE;
        public static final int OTHERS_ALL = OTHERS_READ | OTHERS_WRITE | OTHERS_EXECUTE;
        public static final int ALL_READ = OWNER_READ | GROUP_READ | OTHERS_READ;
        public static final int ALL_WRITE = OWNER_WRITE | GROUP_WRITE | OTHERS_WRITE;
        public static final int ALL_EXECUTE = OWNER_EXECUTE | GROUP_EXECUTE | OTHERS_EXECUTE;
        public static final int ALL_RW = ALL_READ | ALL_WRITE;
        public static final int ALL = ALL_READ | ALL_WRITE | ALL_EXECUTE;
        public static int removeUnusedBits(int mode) {
            return mode << 23 >>> 23;
        }
        private static void writeBits(StringBuilder builder, boolean r, boolean w, boolean x) {
            builder.append(r ? "r" : "-");
            builder.append(w ? "w" : "-");
            builder.append(x ? "x" : "-");
        }
        public static String toString(int mode) {
            StringBuilder builder = new StringBuilder(9);
            writeBits(builder, (mode & OWNER_READ) != 0, (mode & OWNER_WRITE) != 0, (mode & OWNER_EXECUTE) != 0);
            writeBits(builder, (mode & GROUP_READ) != 0, (mode & GROUP_WRITE) != 0, (mode & GROUP_EXECUTE) != 0);
            writeBits(builder, (mode & OTHERS_READ) != 0, (mode & OTHERS_WRITE) != 0, (mode & OTHERS_EXECUTE) != 0);
            return builder.toString();
        }
        private static boolean isSet(char c, char setValue) {
            if (c == setValue) return true;
            if (c == '-') return false;
            throw new IllegalArgumentException("Invalid mode");
        }
        private static boolean isR(char c) {
            return isSet(c, 'r');
        }
        private static boolean isW(char c) {
            return isSet(c, 'w');
        }
        private static boolean isX(char c) {
            return isSet(c, 'x');
        }
        public static int parseMode(String string) {
            if (string.length() != 9) throw new IllegalArgumentException("Invalid mode");
            int mode = 0;
            if (isR(string.charAt(0))) mode |= OWNER_READ;
            if (isW(string.charAt(1))) mode |= OWNER_WRITE;
            if (isX(string.charAt(2))) mode |= OWNER_EXECUTE;
            if (isR(string.charAt(3))) mode |= GROUP_READ;
            if (isW(string.charAt(4))) mode |= GROUP_WRITE;
            if (isX(string.charAt(5))) mode |= GROUP_EXECUTE;
            if (isR(string.charAt(6))) mode |= OTHERS_READ;
            if (isW(string.charAt(7))) mode |= OTHERS_WRITE;
            if (isX(string.charAt(8))) mode |= OTHERS_EXECUTE;
            return mode;
        }
    }

    public abstract String getPermissions(File file) throws IOException;
    public abstract void setPermissions(File file, String permissions) throws IOException;
    public abstract int getMode(File file) throws IOException;
    public abstract void setMode(File file, int mode) throws IOException;

    public abstract void createFile(File file, String permissions) throws IOException;
    public abstract void createDirectory(File directory, String permissions) throws IOException;
    public abstract void createDirectories(File directory, String permissions) throws IOException;
    public abstract void createSymbolicLink(File link, File target, String permissions) throws IOException;
    public abstract void createFile(File file, int mode) throws IOException;
    public abstract void createDirectory(File directory, int mode) throws IOException;
    public abstract void createDirectories(File directory, int mode) throws IOException;
    public abstract void createSymbolicLink(File link, File target, int mode) throws IOException;
    public abstract void createHardLink(File link, File target) throws IOException;
    public void createFile(File file) throws IOException {
        createFile(file, null);
    }
    public void createDirectory(File directory) throws IOException {
        createDirectory(directory, null);
    }
    public void createDirectories(File directory) throws IOException {
        createDirectories(directory, null);
    }
    public void createSymbolicLink(File link, File target) throws IOException {
        createSymbolicLink(link, target, null);
    }
    public void createFileInclusively(File file, String permissions) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null) createDirectories(parent, permissions);
        createFile(file, permissions);
    }
    public void createFileInclusively(File file, int mode) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null) createDirectories(parent, mode);
        createFile(file, mode);
    }
    public void createFileInclusively(File file) throws IOException {
        createFileInclusively(file, null);
    }
    public abstract File createTempFile(File directory, String prefix, String suffix, String permissions) throws IOException;
    public abstract File createTempDirectory(File directory, String prefix, String permissions) throws IOException;
    public abstract File createTempFile(File directory, String prefix, String suffix, int mode) throws IOException;
    public abstract File createTempDirectory(File directory, String prefix, int mode) throws IOException;
    public File createTempFile(String prefix, String suffix, String permissions) throws IOException {
        return createTempFile(null, prefix, suffix, permissions);
    }
    public File createTempDirectory(String prefix, String permissions) throws IOException {
        return createTempDirectory(null, prefix, permissions);
    }
    public File createTempFile(String prefix, String suffix, int mode) throws IOException {
        return createTempFile(null, prefix, suffix, mode);
    }
    public File createTempDirectory(String prefix, int mode) throws IOException {
        return createTempDirectory(null, prefix, mode);
    }

    public abstract boolean isFile(File file);
    public abstract boolean isDirectory(File file);
    public abstract boolean isReadable(File file);
    public abstract boolean isWritable(File file);
    public abstract boolean isExecutable(File file);
    public abstract boolean isSymbolicLink(File file);
    public abstract File getSymbolicLinkTarget(File link) throws IOException;
    public abstract int getHardLinkCount(File file) throws IOException;
    public abstract boolean isSameFile(File a, File b) throws IOException;
    public boolean isSameFile(File a, File b, File... files) throws IOException {
        if (!isSameFile(a, b)) return false;
        else {
            for (File file : files) {
                if (!isSameFile(a, file)) return false;
            }
        }
        return true;
    }
    public abstract boolean isHidden(File file) throws IOException;
    public abstract void setHidden(File file, boolean hidden) throws IOException;
    public abstract boolean isReadOnly(File file) throws IOException;
    public abstract void setReadOnly(File file, boolean readOnly) throws IOException;

    public abstract void delete(File file) throws IOException;
    public abstract boolean deleteIfExists(File file) throws IOException;
    public void deleteOnExit(File file) {
        file.deleteOnExit();
    }

    public String getPath(File file) {
        return file.getPath();
    }

    public String getAbsolutePath(File file) {
        return file.getAbsolutePath();
    }

    public String getCanonicalPath(File file) throws IOException {
        return file.getCanonicalPath();
    }

    public File getAbsoluteFile(File file) {
        return file.getAbsoluteFile();
    }

    public File getCanonicalFile(File file) throws IOException {
        return file.getCanonicalFile();
    }

    public File getParentFile(File file) {
        return file.getAbsoluteFile().getParentFile();
    }

    public String getParentPath(File file) {
        return file.isAbsolute() ? file.getParent() : file.getAbsoluteFile().getParent();
    }

    public boolean isAbsolute(File file) {
        return file.isAbsolute();
    }

    public boolean hasParent(File file) {
        return file.getAbsoluteFile().getParent() != null;
    }

    public abstract boolean exists(File file);
    public abstract boolean notExists(File file);
    public abstract boolean isValid(File file);

    public abstract long length(File file) throws IOException;

    public abstract void move(File source, File target, int copyOptions) throws IOException;

    public abstract Set<File> listRootDirectories();
    public abstract Iterable<File> lazyListRootDirectories();
    public String getFileSeparator() {
        return File.separator;
    }
    public char getFileSeparatorChar() {
        return File.separatorChar;
    }
    public String getPathSeparator() {
        return File.pathSeparator;
    }
    public char getPathSeparatorChar() {
        return File.pathSeparatorChar;
    }
    public String normalize(String pathname) {
        return new File(pathname).getPath();
    }
    public String absolution(String pathname) {
        return new File(pathname).getAbsolutePath();
    }
    public String canonicalize(String pathname) throws IOException {
        return new File(pathname).getCanonicalPath();
    }
    public String resolve(String parent, String child) {
        return new File(parent, child).getPath();
    }
    public String resolve(String parent) {
        return resolve(parent, "");
    }
    public String resolve(String parent, String child, String... children) {
        parent = resolve(parent, child);
        for (String str : children) {
            parent = resolve(parent, str);
        }
        return parent;
    }
    public File resolve(File parent) {
        return parent.getAbsoluteFile();
    }
    public File resolve(File parent, String child) {
        return new File(parent, child);
    }
    public File resolve(File parent, String child, String... children) {
        return new File(resolve(parent == null ? null : parent.getAbsolutePath(), child, children));
    }
    private static final File DEFAULT_ROOT = new File("", "");
    public File getDefaultRootDirectory() {
        return DEFAULT_ROOT;
    }
    private static final File DEFAULT_CWD = new File("");
    public File getFirstWorkingDirectory() {
        return DEFAULT_CWD;
    }

    public abstract long getLastModifiedTime(File file) throws IOException;
    public abstract void setLastModifiedTime(File file, long timestamp) throws IOException;
    public abstract long getLastAccessTime(File file) throws IOException;
    public abstract void setLastAccessTime(File file, long timestamp) throws IOException;
    public abstract long getCreationTime(File file) throws IOException;
    public abstract void setCreationTime(File file, long timestamp) throws IOException;

    public ChannelFile openChannelFile(File file, int openOptions) throws IOException {
        return new ChannelFile(file, openOptions);
    }
    public ChannelFile openChannelFile(File file) throws IOException {
        return new ChannelFile(file);
    }
    public abstract FileChannel openFileChannel(File file, int openOptions) throws IOException;
    public abstract FileChannel openFileChannel(File file) throws IOException;
    public abstract AsynchronousFileChannel openAsynchronousFileChannel(File file, ExecutorService executor, int openOptions) throws IOException;
    public AsynchronousFileChannel openAsynchronousFileChannel(File file, int openOptions) throws IOException {
        return openAsynchronousFileChannel(file, null, openOptions);
    }
    public abstract AsynchronousFileChannel openAsynchronousFileChannel(File file, ExecutorService executor) throws IOException;
    public AsynchronousFileChannel openAsynchronousFileChannel(File file) throws IOException {
        return openAsynchronousFileChannel(file, null);
    }
    public FileInputStream openFileInputStream(File file, int openOptions) throws IOException {
        FileChannel channel = openFileChannel(file, openOptions);
        return new FileInputStream(getFD(channel)) {
            @Override
            public void close() throws IOException {
                super.close();
                channel.close();
            }
        };
    }
    public FileInputStream openFileInputStream(File file) throws IOException {
        return new FileInputStream(file);
    }
    public FileInputStream wrapFileInputStream(FileDescriptor fileDescriptor) {
        return new FileInputStream(fileDescriptor);
    }
    public FileOutputStream openFileOutputStream(File file) throws IOException {
        return new FileOutputStream(file);
    }
    public FileOutputStream openFileOutputStream(File file, int openOptions) throws IOException {
        FileChannel channel = openFileChannel(file, openOptions);
        return new FileOutputStream(getFD(channel)) {
            @Override
            public void close() throws IOException {
                super.close();
                channel.close();
            }
        };
    }
    public FileOutputStream wrapFileOutputStream(FileDescriptor fileDescriptor) {
        return new FileOutputStream(fileDescriptor);
    }
    public FileReader openFileReader(File file, Charset charset) throws IOException {
        return new FileReader(file, charset);
    }
    public FileReader openFileReader(File file) throws IOException {
        return openFileReader(file, null);
    }
    public FileReader openFileReader(File file, Charset charset, int openOptions) throws IOException {
        FileChannel channel = openFileChannel(file, openOptions);
        return new FileReader(getFD(channel), charset) {
            @Override
            public void close() throws IOException {
                super.close();
                channel.close();
            }
        };
    }
    public FileReader openFileReader(File file, int openOptions) throws IOException {
        FileChannel channel = openFileChannel(file, openOptions);
        return new FileReader(getFD(channel)) {
            @Override
            public void close() throws IOException {
                super.close();
                channel.close();
            }
        };
    }
    public FileReader wrapFileReader(FileDescriptor fileDescriptor, Charset charset) {
        return new FileReader(fileDescriptor, charset);
    }
    public FileReader wrapFileReader(FileDescriptor fileDescriptor) {
        return wrapFileReader(fileDescriptor, null);
    }
    public FileWriter openFileWriter(File file, Charset charset) throws IOException {
        return new FileWriter(file, charset);
    }
    public FileWriter openFileWriter(File file) throws IOException {
        return openFileWriter(file, null);
    }
    public FileWriter openFileWriter(File file, Charset charset, int openOptions) throws IOException {
        FileChannel channel = openFileChannel(file, openOptions);
        return new FileWriter(getFD(channel), charset) {
            @Override
            public void close() throws IOException {
                super.close();
                channel.close();
            }
        };
    }
    public FileWriter openFileWriter(File file, int openOptions) throws IOException {
        FileChannel channel = openFileChannel(file, openOptions);
        return new FileWriter(getFD(channel)) {
            @Override
            public void close() throws IOException {
                super.close();
                channel.close();
            }
        };
    }
    public FileWriter wrapFileWriter(FileDescriptor fileDescriptor, Charset charset) {
        return new FileWriter(fileDescriptor, charset);
    }
    public FileWriter wrapFileWriter(FileDescriptor fileDescriptor) {
        return wrapFileWriter(fileDescriptor, null);
    }

    public abstract URI toURI(File file);
    public abstract URL toURL(File file) throws MalformedURLException;

    public abstract String getOwner(File file) throws IOException;
    public abstract void setOwner(File file, String user) throws IOException;

    public abstract List<File> listChildren(File directory) throws IOException;
    public abstract List<File> listChildren(File directory, FileFilter filter) throws IOException;
    public abstract List<File> listChildren(File directory, FilenameFilter filter) throws IOException;
    public abstract List<File> listChildren(File directory, String glob) throws IOException;
    public abstract List<File> listSiblings(File file) throws IOException;
    public abstract List<File> listSiblings(File file, FileFilter filter) throws IOException;
    public abstract List<File> listSiblings(File file, FilenameFilter filter) throws IOException;
    public abstract List<File> listSiblings(File file, String glob) throws IOException;
    public abstract ScopedIterable<File> lazyListChildren(File directory) throws IOException;
    public abstract ScopedIterable<File> lazyListChildren(File directory, FileFilter filter) throws IOException;
    public abstract ScopedIterable<File> lazyListChildren(File directory, FilenameFilter filter) throws IOException;
    public abstract ScopedIterable<File> lazyListChildren(File directory, String glob) throws IOException;
    public abstract ScopedIterable<File> lazyListSiblings(File file) throws IOException;
    public abstract ScopedIterable<File> lazyListSiblings(File file, FileFilter filter) throws IOException;
    public abstract ScopedIterable<File> lazyListSiblings(File file, FilenameFilter filter) throws IOException;
    public abstract ScopedIterable<File> lazyListSiblings(File file, String glob) throws IOException;

    public abstract void walkTree(File root, int maxDepth, FileVisitor visitor) throws IOException;
    public void walkTree(File root, FileVisitor visitor) throws IOException {
        walkTree(root, Integer.MAX_VALUE, visitor);
    }
    public abstract ScopedIterable<File> lazyWalkTree(File root, int maxDepth) throws IOException;
    public ScopedIterable<File> lazyWalkTree(File root) throws IOException {
        return lazyWalkTree(root, Integer.MAX_VALUE);
    }

    public boolean isSamePath(File a, File b) {
        return a.getAbsolutePath().equals(b.getAbsolutePath());
    }
    public int compare(File a, File b) {
        return a.compareTo(b);
    }

    public abstract int getFileno(FileDescriptor descriptor);
    public abstract long getHandle(FileDescriptor descriptor);
    public abstract long transfer(FileDescriptor in, FileDescriptor out) throws IOException;

    public abstract Set<FileStore> listFileStores();
    public abstract Iterable<FileStore> lazyListFileStores();

    public abstract FileStore getFileStore(File file) throws IOException;

    public abstract FileFilter createFileFilter(String glob);
    public abstract FilenameFilter createFilenameFilter(String glob);

    public abstract FileDescriptor getFD(FileChannel channel);
    public abstract FileDescriptor getFD(AsynchronousFileChannel channel);

}
