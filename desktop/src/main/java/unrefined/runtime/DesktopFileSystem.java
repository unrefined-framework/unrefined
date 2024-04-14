package unrefined.runtime;

import unrefined.desktop.FileSystemSupport;
import unrefined.io.file.FileStore;
import unrefined.io.file.FileSystem;
import unrefined.io.file.FileVisitor;
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
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DesktopFileSystem extends FileSystem {

    @Override
    public String getPermissions(File file) throws IOException {
        return PosixFilePermissions.toString(FileSystemSupport.getPosixFilePermissions(file.toPath(), LinkOption.NOFOLLOW_LINKS));
    }

    @Override
    public void setPermissions(File file, String permissions) throws IOException {
        if (permissions != null)
            FileSystemSupport.setPosixFilePermissions(file.toPath(), PosixFilePermissions.fromString(permissions), LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public int getMode(File file) throws IOException {
        return FileSystemSupport.toMode(FileSystemSupport.getPosixFilePermissions(file.toPath(), LinkOption.NOFOLLOW_LINKS));
    }

    @Override
    public void setMode(File file, int mode) throws IOException {
        FileSystemSupport.setPosixFilePermissions(file.toPath(), FileSystemSupport.toPosixFilePermissions(mode), LinkOption.NOFOLLOW_LINKS);
    }

    private static final FileAttribute<?>[] NO_ATTRIBUTES = new FileAttribute[0];

    @Override
    public void createFile(File file, String permissions) throws IOException {
        Files.createFile(file.toPath(), permissions == null ? NO_ATTRIBUTES :
                new FileAttribute[] { PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(permissions)) });
    }

    @Override
    public void createDirectory(File directory, String permissions) throws IOException {
        Files.createDirectory(directory.toPath(), permissions == null ? NO_ATTRIBUTES :
                new FileAttribute[] { PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(permissions)) });
    }

    @Override
    public void createDirectories(File directory, String permissions) throws IOException {
        Files.createDirectories(directory.toPath(), permissions == null ? NO_ATTRIBUTES :
                new FileAttribute[] { PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(permissions)) });
    }

    @Override
    public void createSymbolicLink(File link, File target, String permissions) throws IOException {
        Files.createSymbolicLink(link.toPath(), target.toPath(), permissions == null ? NO_ATTRIBUTES :
                new FileAttribute[] { PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(permissions)) });
    }

    @Override
    public void createFile(File file, int mode) throws IOException {
        Files.createFile(file.toPath(), PosixFilePermissions.asFileAttribute(FileSystemSupport.toPosixFilePermissions(mode)));
    }

    @Override
    public void createDirectory(File directory, int mode) throws IOException {
        Files.createDirectory(directory.toPath(), PosixFilePermissions.asFileAttribute(FileSystemSupport.toPosixFilePermissions(mode)));
    }

    @Override
    public void createDirectories(File directory, int mode) throws IOException {
        Files.createDirectories(directory.toPath(), PosixFilePermissions.asFileAttribute(FileSystemSupport.toPosixFilePermissions(mode)));
    }

    @Override
    public void createSymbolicLink(File link, File target, int mode) throws IOException {
        Files.createSymbolicLink(link.toPath(), target.toPath(), PosixFilePermissions.asFileAttribute(FileSystemSupport.toPosixFilePermissions(mode)));
    }

    @Override
    public void createHardLink(File link, File target) throws IOException {
        Files.createLink(link.toPath(), target.toPath());
    }

    @Override
    public File createTempFile(File directory, String prefix, String suffix, String permissions) throws IOException {
        if (directory == null) return Files.createTempFile(prefix, suffix, permissions == null ? NO_ATTRIBUTES :
                new FileAttribute[] { PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(permissions)) }).toFile();
        return Files.createTempFile(directory.toPath(), prefix, suffix, permissions == null ? NO_ATTRIBUTES :
                new FileAttribute[] { PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(permissions)) }).toFile();
    }

    @Override
    public File createTempDirectory(File directory, String prefix, String permissions) throws IOException {
        if (directory == null) return Files.createTempDirectory(prefix, permissions == null ? NO_ATTRIBUTES :
                new FileAttribute[] { PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(permissions)) }).toFile();
        return Files.createTempDirectory(directory.toPath(), prefix, permissions == null ? NO_ATTRIBUTES :
                new FileAttribute[] { PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(permissions)) }).toFile();
    }

    @Override
    public File createTempFile(File directory, String prefix, String suffix, int mode) throws IOException {
        if (directory == null) return Files.createTempFile(prefix, suffix,
                PosixFilePermissions.asFileAttribute(FileSystemSupport.toPosixFilePermissions(mode))).toFile();
        return Files.createTempFile(directory.toPath(), prefix, suffix,
                PosixFilePermissions.asFileAttribute(FileSystemSupport.toPosixFilePermissions(mode))).toFile();
    }

    @Override
    public File createTempDirectory(File directory, String prefix, int mode) throws IOException {
        if (directory == null) return Files.createTempDirectory(prefix,
                PosixFilePermissions.asFileAttribute(FileSystemSupport.toPosixFilePermissions(mode))).toFile();
        return Files.createTempDirectory(directory.toPath(), prefix,
                PosixFilePermissions.asFileAttribute(FileSystemSupport.toPosixFilePermissions(mode))).toFile();
    }

    @Override
    public boolean isFile(File file) {
        return Files.isRegularFile(file.toPath(), LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public boolean isDirectory(File file) {
        return Files.isDirectory(file.toPath(), LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public boolean isReadable(File file) {
        return Files.isReadable(file.toPath());
    }

    @Override
    public boolean isWritable(File file) {
        return Files.isWritable(file.toPath());
    }

    @Override
    public boolean isExecutable(File file) {
        return Files.isExecutable(file.toPath());
    }

    @Override
    public boolean isSymbolicLink(File file) {
        return Files.isSymbolicLink(file.toPath());
    }

    @Override
    public File getSymbolicLinkTarget(File link) throws IOException {
        return Files.readSymbolicLink(link.toPath()).toFile();
    }

    @Override
    public int getHardLinkCount(File file) throws IOException {
        return FileSystemSupport.HARD_LINK_PROCESS.getHardLinkCount(file.toPath());
    }

    @Override
    public boolean isSameFile(File a, File b) throws IOException {
        return Files.isSameFile(a.toPath(), b.toPath());
    }

    @Override
    public boolean isHidden(File file) throws IOException {
        return Files.isHidden(file.toPath());
    }

    @Override
    public void setHidden(File file, boolean hidden) throws IOException {
        FileSystemSupport.setHidden(file.toPath(), hidden, LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public boolean isReadOnly(File file) throws IOException {
        return FileSystemSupport.isReadOnly(file.toPath(), LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public void setReadOnly(File file, boolean readOnly) throws IOException {
        FileSystemSupport.setReadOnly(file.toPath(), readOnly, LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public void delete(File file) throws IOException {
        Files.delete(file.toPath());
    }

    @Override
    public boolean deleteIfExists(File file) throws IOException {
        return Files.deleteIfExists(file.toPath());
    }

    @Override
    public boolean exists(File file) {
        return Files.exists(file.toPath(), LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public boolean notExists(File file) {
        return Files.notExists(file.toPath(), LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public boolean isValid(File file) {
        Path path = file.toPath();
        return Files.exists(path, LinkOption.NOFOLLOW_LINKS) || Files.notExists(path, LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public long length(File file) throws IOException {
        return Files.size(file.toPath());
    }

    private static java.nio.file.CopyOption[] toCopyOptions(int options) {
        options = FileSystem.CopyOption.removeUnusedBits(options);
        List<java.nio.file.CopyOption> copyOptions = new ArrayList<>(4);
        if ((options & FileSystem.CopyOption.REPLACE_EXISTING) != 0) copyOptions.add(StandardCopyOption.REPLACE_EXISTING);
        if ((options & FileSystem.CopyOption.COPY_ATTRIBUTES) != 0) copyOptions.add(StandardCopyOption.COPY_ATTRIBUTES);
        if ((options & FileSystem.CopyOption.ATOMIC_MOVE) != 0) copyOptions.add(StandardCopyOption.ATOMIC_MOVE);
        copyOptions.add(LinkOption.NOFOLLOW_LINKS);
        return copyOptions.toArray(new java.nio.file.CopyOption[0]);
    }

    @Override
    public void move(File source, File target, int copyOptions) throws IOException {
        Files.move(source.toPath(), target.toPath(), toCopyOptions(copyOptions));
    }

    @Override
    public Set<File> listRootDirectories() {
        Set<File> set = new HashSet<>();
        for (Path path : FileSystems.getDefault().getRootDirectories()) {
            set.add(path.toFile());
        }
        return Collections.unmodifiableSet(set);
    }

    @Override
    public long getLastModifiedTime(File file) throws IOException {
        return Files.getLastModifiedTime(file.toPath(), LinkOption.NOFOLLOW_LINKS).toMillis();
    }

    @Override
    public void setLastModifiedTime(File file, long timestamp) throws IOException {
        Files.setLastModifiedTime(file.toPath(), FileTime.fromMillis(timestamp));
    }

    @Override
    public long getLastAccessTime(File file) throws IOException {
        return FileSystemSupport.getLastAccessTime(file.toPath(), LinkOption.NOFOLLOW_LINKS).toMillis();
    }

    @Override
    public void setLastAccessTime(File file, long timestamp) throws IOException {
        FileSystemSupport.setLastAccessTime(file.toPath(), FileTime.fromMillis(timestamp));
    }

    @Override
    public long getCreationTime(File file) throws IOException {
        return FileSystemSupport.getCreationTime(file.toPath(), LinkOption.NOFOLLOW_LINKS).toMillis();
    }

    @Override
    public void setCreationTime(File file, long timestamp) throws IOException {
        FileSystemSupport.setCreationTime(file.toPath(), FileTime.fromMillis(timestamp));
    }

    @Override
    public FileChannel openFileChannel(File file, int openOptions) throws IOException {
        return FileChannel.open(file.toPath(), FileSystemSupport.toStandardOpenOptions(openOptions));
    }

    @Override
    public FileChannel openFileChannel(File file) throws IOException {
        return FileChannel.open(file.toPath());
    }

    @Override
    public URI toURI(File file) {
        return file.toPath().toUri();
    }

    @Override
    public URL toURL(File file) throws MalformedURLException {
        return file.toPath().toUri().toURL();
    }

    @Override
    public String getOwner(File file) throws IOException {
        return Files.getOwner(file.toPath(), LinkOption.NOFOLLOW_LINKS).getName();
    }

    @Override
    public void setOwner(File file, String user) throws IOException {
        try {
            Files.setOwner(file.toPath(), file.toPath().getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(user));
        }
        catch (UserPrincipalNotFoundException e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<File> listChildren(File directory) throws IOException {
        try (Stream<Path> stream = Files.list(directory.toPath())) {
            return stream.map(Path::toFile).collect(Collectors.toList());
        }
    }

    @Override
    public List<File> listChildren(File directory, FileFilter filter) throws IOException {
        if (filter == null) return listChildren(directory);
        else try (Stream<Path> stream = Files.list(directory.toPath())) {
            return stream.map(Path::toFile).filter(filter::accept).collect(Collectors.toList());
        }
    }

    @Override
    public List<File> listChildren(File directory, FilenameFilter filter) throws IOException {
        if (filter == null) return listChildren(directory);
        else try (Stream<Path> stream = Files.list(directory.toPath())) {
            return stream.map(Path::toFile).filter(file -> filter.accept(directory, file.getName()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<File> listChildren(File directory, String glob) throws IOException {
        try (Stream<Path> stream = FileSystemSupport.list(Files.newDirectoryStream(directory.toPath(), glob))) {
            return stream.map(Path::toFile).collect(Collectors.toList());
        }
    }

    @Override
    public List<File> listSiblings(File file) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent == null) return Collections.emptyList();
        else try (Stream<Path> stream = Files.list(parent.toPath())) {
            return stream.map(Path::toFile)
                    .filter(f -> !f.getAbsolutePath().equals(file.getAbsolutePath()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<File> listSiblings(File file, FileFilter filter) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent == null) return Collections.emptyList();
        else try (Stream<Path> stream = Files.list(parent.toPath())) {
            return stream.map(Path::toFile)
                    .filter(f -> !f.getAbsolutePath().equals(file.getAbsolutePath()))
                    .filter(filter::accept)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<File> listSiblings(File file, FilenameFilter filter) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent == null) return Collections.emptyList();
        else try (Stream<Path> stream = Files.list(parent.toPath())) {
            return stream.map(Path::toFile)
                    .filter(f -> !f.getAbsolutePath().equals(file.getAbsolutePath()))
                    .filter(f -> filter.accept(parent, f.getName()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<File> listSiblings(File file, String glob) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent == null) return Collections.emptyList();
        else try (Stream<Path> stream = FileSystemSupport.list(Files.newDirectoryStream(parent.toPath(), glob))) {
            return stream.map(Path::toFile)
                    .filter(f -> !f.getAbsolutePath().equals(file.getAbsolutePath())).collect(Collectors.toList());
        }
    }

    private static <T> Iterable<T> wrap(Stream<T> stream) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return stream.iterator();
            }
            @Override
            public Spliterator<T> spliterator() {
                return stream.spliterator();
            }
            @Override
            public void forEach(Consumer<? super T> action) {
                stream.forEach(action);
            }
        };
    }

    @Override
    public ScopedIterable<File> lazyListChildren(File directory) throws IOException {
        Stream<Path> stream = Files.list(directory.toPath());
        return ScopedIterable.wrap(wrap(stream.map(Path::toFile)), stream);
    }

    @Override
    public ScopedIterable<File> lazyListChildren(File directory, FileFilter filter) throws IOException {
        Stream<Path> stream = Files.list(directory.toPath());
        return ScopedIterable.wrap(wrap(stream.map(Path::toFile).filter(filter::accept)), stream);
    }

    @Override
    public ScopedIterable<File> lazyListChildren(File directory, FilenameFilter filter) throws IOException {
        Stream<Path> stream = Files.list(directory.toPath());
        return ScopedIterable.wrap(wrap(stream.map(Path::toFile).filter(file -> filter.accept(directory, file.getName()))), stream);
    }

    @Override
    public ScopedIterable<File> lazyListChildren(File directory, String glob) throws IOException {
        Stream<Path> stream = FileSystemSupport.list(Files.newDirectoryStream(directory.toPath(), glob));
        return ScopedIterable.wrap(wrap(stream.map(Path::toFile)), stream);
    }

    @Override
    public ScopedIterable<File> lazyListSiblings(File file) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent == null) return ScopedIterable.wrap(Collections.emptySet(), null);
        else {
            Stream<Path> stream = Files.list(parent.toPath());
            return ScopedIterable.wrap(wrap(stream.map(Path::toFile)
                    .filter(f -> !f.getAbsolutePath().equals(file.getAbsolutePath()))), stream);
        }
    }

    @Override
    public ScopedIterable<File> lazyListSiblings(File file, FileFilter filter) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent == null) return ScopedIterable.wrap(Collections.emptySet(), null);
        else {
            Stream<Path> stream = Files.list(parent.toPath());
            return ScopedIterable.wrap(wrap(stream.map(Path::toFile)
                    .filter(f -> !f.getAbsolutePath().equals(file.getAbsolutePath()))
                    .filter(filter::accept)), stream);
        }
    }

    @Override
    public ScopedIterable<File> lazyListSiblings(File file, FilenameFilter filter) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent == null) return ScopedIterable.wrap(Collections.emptySet(), null);
        else {
            Stream<Path> stream = Files.list(parent.toPath());
            return ScopedIterable.wrap(wrap(stream.map(Path::toFile)
                    .filter(f -> !f.getAbsolutePath().equals(file.getAbsolutePath()))
                    .filter(f -> filter.accept(parent, f.getName()))), stream);
        }
    }

    @Override
    public ScopedIterable<File> lazyListSiblings(File file, String glob) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent == null) return ScopedIterable.wrap(Collections.emptySet(), null);
        else {
            Stream<Path> stream = FileSystemSupport.list(Files.newDirectoryStream(parent.toPath(), glob));
            return ScopedIterable.wrap(wrap(stream.map(Path::toFile)
                    .filter(f -> !f.getAbsolutePath().equals(file.getAbsolutePath()))), stream);
        }
    }

    @Override
    public void walkTree(File root, int maxDepth, FileVisitor visitor) throws IOException {
        Files.walkFileTree(root.toPath(), EnumSet.noneOf(FileVisitOption.class), maxDepth, new java.nio.file.FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileSystemSupport.castResult(visitor.preVisitDirectory(dir.toFile()));
            }
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                return FileSystemSupport.castResult(visitor.visitFile(file.toFile(), null));
            }
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileSystemSupport.castResult(visitor.visitFile(file.toFile(), exc));
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileSystemSupport.castResult(visitor.visitFile(dir.toFile(), exc));
            }
        });
    }

    @Override
    public ScopedIterable<File> lazyWalkTree(File root, int maxDepth) throws IOException {
        Stream<Path> stream = Files.walk(root.toPath(), maxDepth);
        return ScopedIterable.wrap(wrap(stream.map(Path::toFile)), stream);
    }

    @Override
    public int getFileno(FileDescriptor descriptor) {
        return FileSystemSupport.FD_PROCESS.toFD(descriptor);
    }

    @Override
    public long getHandle(FileDescriptor descriptor) {
        return FileSystemSupport.FD_PROCESS.toHANDLE(descriptor);
    }

    @Override
    public long transfer(FileDescriptor in, FileDescriptor out) throws IOException {
        return new FileInputStream(in).transferTo(new FileOutputStream(out));
    }

    @Override
    public Set<FileStore> listFileStores() {
        Set<FileStore> set = new HashSet<>();
        for (java.nio.file.FileStore fileStore : FileSystems.getDefault().getFileStores()) {
            set.add(new DesktopFileStore(fileStore));
        }
        return Collections.unmodifiableSet(set);
    }

    @Override
    public FileStore getFileStore(File file) throws IOException {
        return new DesktopFileStore(Files.getFileStore(file.toPath()));
    }

    @Override
    public FileFilter createFileFilter(String glob) {
        if (glob.equals("*")) return pathname -> true;
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
        return pathname -> pathMatcher.matches(pathname.toPath());
    }

    @Override
    public FilenameFilter createFilenameFilter(String glob) {
        if (glob.equals("*")) return (dir, name) -> true;
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
        return (dir, name) -> pathMatcher.matches(Paths.get(dir.getAbsolutePath(), name));
    }

    @Override
    public FileDescriptor getFD(FileChannel channel) {
        return FileSystemSupport.getFD(channel);
    }

}
