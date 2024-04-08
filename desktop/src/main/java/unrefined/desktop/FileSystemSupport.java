package unrefined.desktop;

import unrefined.internal.windows.WindowsFileSystemSupport;
import unrefined.io.file.FileSystem;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class FileSystemSupport {

    private FileSystemSupport() {
        throw new NotInstantiableError(FileSystemSupport.class);
    }

    private static Runnable asUncheckedRunnable(Closeable c) {
        return () -> {
            try {
                c.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    public static Stream<Path> list(DirectoryStream<Path> ds) {
        try {
            final Iterator<Path> delegate = ds.iterator();

            // Re-wrap DirectoryIteratorException to UncheckedIOException
            Iterator<Path> iterator = new Iterator<Path>() {
                @Override
                public boolean hasNext() {
                    try {
                        return delegate.hasNext();
                    } catch (DirectoryIteratorException e) {
                        throw new UncheckedIOException(e.getCause());
                    }
                }
                @Override
                public Path next() {
                    try {
                        return delegate.next();
                    } catch (DirectoryIteratorException e) {
                        throw new UncheckedIOException(e.getCause());
                    }
                }
            };

            Spliterator<Path> spliterator =
                    Spliterators.spliteratorUnknownSize(iterator, Spliterator.DISTINCT);
            return StreamSupport.stream(spliterator, false)
                    .onClose(asUncheckedRunnable(ds));
        } catch (Error|RuntimeException e) {
            try {
                ds.close();
            } catch (IOException ex) {
                try {
                    e.addSuppressed(ex);
                } catch (Throwable ignore) {}
            }
            throw e;
        }
    }

    public static FileVisitResult castResult(unrefined.io.file.FileVisitor.Result result) {
        switch (result) {
            case CONTINUE: return FileVisitResult.CONTINUE;
            case TERMINATE: return FileVisitResult.TERMINATE;
            case SKIP_SUBTREE: return FileVisitResult.SKIP_SUBTREE;
            case SKIP_SIBLINGS: return FileVisitResult.SKIP_SIBLINGS;
            default: throw new IllegalArgumentException("Illegal result: " + result);
        }
    }

    public static void setHidden(Path path, boolean hidden, LinkOption... options) throws IOException {
        DosFileAttributeView view = Files.getFileAttributeView(path, DosFileAttributeView.class, options);
        if (view == null) throw new IOException(new UnsupportedOperationException("set file hidden"));
        else view.setHidden(hidden);
    }

    public static int toMode(Set<PosixFilePermission> permissions) {
        int mode = 0;
        if (permissions.contains(PosixFilePermission.OWNER_READ)) mode |= FileSystem.Mode.OWNER_READ;
        if (permissions.contains(PosixFilePermission.OWNER_WRITE)) mode |= FileSystem.Mode.OWNER_WRITE;
        if (permissions.contains(PosixFilePermission.OWNER_EXECUTE)) mode |= FileSystem.Mode.OWNER_EXECUTE;
        if (permissions.contains(PosixFilePermission.GROUP_READ)) mode |= FileSystem.Mode.GROUP_READ;
        if (permissions.contains(PosixFilePermission.GROUP_WRITE)) mode |= FileSystem.Mode.GROUP_WRITE;
        if (permissions.contains(PosixFilePermission.GROUP_EXECUTE)) mode |= FileSystem.Mode.GROUP_EXECUTE;
        if (permissions.contains(PosixFilePermission.OTHERS_READ)) mode |= FileSystem.Mode.OTHERS_READ;
        if (permissions.contains(PosixFilePermission.OTHERS_WRITE)) mode |= FileSystem.Mode.OTHERS_WRITE;
        if (permissions.contains(PosixFilePermission.OTHERS_EXECUTE)) mode |= FileSystem.Mode.OTHERS_EXECUTE;
        return mode;
    }

    public static Set<PosixFilePermission> toPosixFilePermissions(int mode) {
        mode = FileSystem.Mode.removeUnusedBits(mode);
        Set<PosixFilePermission> perms = new HashSet<>(9);
        if ((mode & FileSystem.Mode.OWNER_READ) != 0) perms.add(PosixFilePermission.OWNER_READ);
        if ((mode & FileSystem.Mode.OWNER_WRITE) != 0) perms.add(PosixFilePermission.OWNER_WRITE);
        if ((mode & FileSystem.Mode.OWNER_EXECUTE) != 0) perms.add(PosixFilePermission.OWNER_EXECUTE);
        if ((mode & FileSystem.Mode.OTHERS_READ) != 0) perms.add(PosixFilePermission.OTHERS_READ);
        if ((mode & FileSystem.Mode.OTHERS_WRITE) != 0) perms.add(PosixFilePermission.OTHERS_WRITE);
        if ((mode & FileSystem.Mode.OTHERS_EXECUTE) != 0) perms.add(PosixFilePermission.OTHERS_EXECUTE);
        if ((mode & FileSystem.Mode.GROUP_READ) != 0) perms.add(PosixFilePermission.GROUP_READ);
        if ((mode & FileSystem.Mode.GROUP_WRITE) != 0) perms.add(PosixFilePermission.GROUP_WRITE);
        if ((mode & FileSystem.Mode.GROUP_EXECUTE) != 0) perms.add(PosixFilePermission.GROUP_EXECUTE);
        return Collections.unmodifiableSet(perms);
    }

    private static boolean containsRead(Set<AclEntryPermission> permissions) {
        return permissions.contains(AclEntryPermission.READ_ACL) && permissions.contains(AclEntryPermission.READ_ATTRIBUTES) &&
                permissions.contains(AclEntryPermission.READ_DATA) && permissions.contains(AclEntryPermission.READ_NAMED_ATTRS) &&
                permissions.contains(AclEntryPermission.SYNCHRONIZE);
    }

    private static boolean containsWrite(Set<AclEntryPermission> permissions) {
        return permissions.contains(AclEntryPermission.WRITE_ACL) && permissions.contains(AclEntryPermission.WRITE_ATTRIBUTES) &&
                permissions.contains(AclEntryPermission.WRITE_DATA) && permissions.contains(AclEntryPermission.WRITE_NAMED_ATTRS) &&
                permissions.contains(AclEntryPermission.SYNCHRONIZE) && permissions.contains(AclEntryPermission.WRITE_OWNER) &&
                permissions.contains(AclEntryPermission.APPEND_DATA) && permissions.contains(AclEntryPermission.DELETE) &&
                permissions.contains(AclEntryPermission.DELETE_CHILD);
    }

    private static void getAclEntryPermissions(Set<AclEntryPermission> allow, Set<AclEntryPermission> deny,
                                                                 boolean r, boolean w, boolean x) {
        if (r) {
            allow.add(AclEntryPermission.READ_ACL);
            allow.add(AclEntryPermission.READ_ATTRIBUTES);
            allow.add(AclEntryPermission.READ_DATA);
            allow.add(AclEntryPermission.READ_NAMED_ATTRS);
            allow.add(AclEntryPermission.SYNCHRONIZE);
        }
        else {
            deny.add(AclEntryPermission.READ_ACL);
            deny.add(AclEntryPermission.READ_ATTRIBUTES);
            deny.add(AclEntryPermission.READ_DATA);
            deny.add(AclEntryPermission.READ_NAMED_ATTRS);
            deny.add(AclEntryPermission.SYNCHRONIZE);
        }
        if (w) {
            allow.add(AclEntryPermission.WRITE_ACL);
            allow.add(AclEntryPermission.WRITE_ATTRIBUTES);
            allow.add(AclEntryPermission.WRITE_DATA);
            allow.add(AclEntryPermission.WRITE_NAMED_ATTRS);
            allow.add(AclEntryPermission.WRITE_OWNER);
            allow.add(AclEntryPermission.APPEND_DATA);
            allow.add(AclEntryPermission.DELETE);
            allow.add(AclEntryPermission.DELETE_CHILD);
            allow.add(AclEntryPermission.SYNCHRONIZE);
        }
        else {
            deny.add(AclEntryPermission.WRITE_ACL);
            deny.add(AclEntryPermission.WRITE_ATTRIBUTES);
            deny.add(AclEntryPermission.WRITE_DATA);
            deny.add(AclEntryPermission.WRITE_NAMED_ATTRS);
            deny.add(AclEntryPermission.WRITE_OWNER);
            deny.add(AclEntryPermission.APPEND_DATA);
            deny.add(AclEntryPermission.DELETE);
            deny.add(AclEntryPermission.DELETE_CHILD);
            deny.add(AclEntryPermission.SYNCHRONIZE);
        }
        if (x) allow.add(AclEntryPermission.EXECUTE);
        else deny.add(AclEntryPermission.EXECUTE);
    }

    private static List<AclEntry> replaceAclEntries(UserPrincipal owner, GroupPrincipal group, GroupPrincipal everyone,
                                                    List<AclEntry> original, Set<PosixFilePermission> perms) {
        List<AclEntry> entries = new ArrayList<>(original.size() + 2);
        for (AclEntry entry : original) {
            if (!entry.principal().equals(owner) && !entry.principal().equals(group) && !entry.principal().equals(everyone))
                entries.add(entry);
        }
        Set<AclEntryPermission> allow = new HashSet<>(14);
        Set<AclEntryPermission> deny = new HashSet<>(14);
        boolean r = perms.contains(PosixFilePermission.OWNER_READ);
        boolean w = perms.contains(PosixFilePermission.OWNER_WRITE);
        boolean x = perms.contains(PosixFilePermission.OWNER_EXECUTE);
        getAclEntryPermissions(allow, deny, r, w, x);
        if (!allow.isEmpty()) {
            AclEntry.Builder builder = AclEntry.newBuilder();
            builder.setPrincipal(owner);
            builder.setType(AclEntryType.ALLOW);
            builder.setPermissions(allow);
            entries.add(builder.build());
        }
        if (!deny.isEmpty()) {
            AclEntry.Builder builder = AclEntry.newBuilder();
            builder.setPrincipal(owner);
            builder.setType(AclEntryType.DENY);
            builder.setPermissions(deny);
            entries.add(builder.build());
        }
        if (group != null) {
            allow.clear();
            deny.clear();
            r = perms.contains(PosixFilePermission.GROUP_READ);
            w = perms.contains(PosixFilePermission.GROUP_WRITE);
            x = perms.contains(PosixFilePermission.GROUP_EXECUTE);
            getAclEntryPermissions(allow, deny, r, w, x);
            if (!allow.isEmpty()) {
                AclEntry.Builder builder = AclEntry.newBuilder();
                builder.setPrincipal(group);
                builder.setType(AclEntryType.ALLOW);
                builder.setPermissions(allow);
                entries.add(builder.build());
            }
            if (!deny.isEmpty()) {
                AclEntry.Builder builder = AclEntry.newBuilder();
                builder.setPrincipal(group);
                builder.setType(AclEntryType.DENY);
                builder.setPermissions(deny);
                entries.add(builder.build());
            }
        }
        if (everyone != null) {
            allow.clear();
            deny.clear();
            r = perms.contains(PosixFilePermission.OTHERS_READ);
            w = perms.contains(PosixFilePermission.OTHERS_WRITE);
            x = perms.contains(PosixFilePermission.OTHERS_EXECUTE);
            getAclEntryPermissions(allow, deny, r, w, x);
            if (!allow.isEmpty()) {
                AclEntry.Builder builder = AclEntry.newBuilder();
                builder.setPrincipal(everyone);
                builder.setType(AclEntryType.ALLOW);
                builder.setPermissions(allow);
                entries.add(builder.build());
            }
            if (!deny.isEmpty()) {
                AclEntry.Builder builder = AclEntry.newBuilder();
                builder.setPrincipal(everyone);
                builder.setType(AclEntryType.DENY);
                builder.setPermissions(deny);
                entries.add(builder.build());
            }
        }
        return entries;
    }

    public static Path setPosixFilePermissions(Path path, Set<PosixFilePermission> perms, LinkOption... options) throws IOException {
        PosixFileAttributeView view = Files.getFileAttributeView(path, PosixFileAttributeView.class, options);
        if (view == null) {
            AclFileAttributeView acl = Files.getFileAttributeView(path, AclFileAttributeView.class, options);
            if (acl == null) throw new UnsupportedOperationException();
            else {
                GroupPrincipal group, everyone;
                try {
                    group = path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByGroupName("Creator Group");
                }
                catch (UserPrincipalNotFoundException e) {
                    group = null;
                }
                try {
                    everyone = path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByGroupName("Everyone");
                }
                catch (UserPrincipalNotFoundException e) {
                    everyone = null;
                }
                if (group == null) group = everyone;
                acl.setAcl(replaceAclEntries(Files.getOwner(path, options), group, everyone, acl.getAcl(), perms));
            }
        }
        else view.setPermissions(perms);
        return path;
    }

    private static Set<PosixFilePermission> toPosixFilePermissions(UserPrincipal owner, GroupPrincipal group, GroupPrincipal everyone, List<AclEntry> entries) {
        Set<PosixFilePermission> permissions = new HashSet<>(9);
        Set<AclEntryPermission> ownerAcl = new HashSet<>(14);
        Set<AclEntryPermission> groupAcl = new HashSet<>(14);
        Set<AclEntryPermission> everyoneAcl = new HashSet<>(14);
        for (AclEntry entry : entries) {
            if (entry.type() == AclEntryType.ALLOW) {
                if (entry.principal().equals(owner)) {
                    ownerAcl.addAll(entry.permissions());
                }
                else if (entry.principal().equals(group)) {
                    groupAcl.addAll(entry.permissions());
                }
                else if (entry.principal().equals(everyone)) {
                    everyoneAcl.addAll(entry.permissions());
                }
            }
            else if (entry.type() == AclEntryType.DENY) {
                if (entry.principal().equals(owner)) {
                    ownerAcl.removeAll(entry.permissions());
                }
                else if (entry.principal().equals(group)) {
                    groupAcl.removeAll(entry.permissions());
                }
                else if (entry.principal().equals(everyone)) {
                    everyoneAcl.removeAll(entry.permissions());
                }
            }
        }
        if (ownerAcl.contains(AclEntryPermission.EXECUTE)) permissions.add(PosixFilePermission.OWNER_EXECUTE);
        if (containsRead(ownerAcl)) permissions.add(PosixFilePermission.OWNER_READ);
        if (containsWrite(ownerAcl)) permissions.add(PosixFilePermission.OWNER_WRITE);
        if (groupAcl.contains(AclEntryPermission.EXECUTE)) permissions.add(PosixFilePermission.GROUP_EXECUTE);
        if (containsRead(groupAcl)) permissions.add(PosixFilePermission.GROUP_READ);
        if (containsWrite(groupAcl)) permissions.add(PosixFilePermission.GROUP_WRITE);
        if (everyoneAcl.contains(AclEntryPermission.EXECUTE)) permissions.add(PosixFilePermission.OTHERS_EXECUTE);
        if (containsRead(everyoneAcl)) permissions.add(PosixFilePermission.OTHERS_READ);
        if (containsWrite(everyoneAcl)) permissions.add(PosixFilePermission.OTHERS_WRITE);
        return permissions;
    }

    public static Set<PosixFilePermission> getPosixFilePermissions(Path path, LinkOption... options) throws IOException {
        PosixFileAttributeView view = Files.getFileAttributeView(path, PosixFileAttributeView.class, options);
        if (view == null) {
            AclFileAttributeView acl = Files.getFileAttributeView(path, AclFileAttributeView.class, options);
            if (acl == null) throw new UnsupportedOperationException();
            else {
                GroupPrincipal group, everyone;
                try {
                    group = path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByGroupName("Creator Group");
                }
                catch (UserPrincipalNotFoundException e) {
                    group = null;
                }
                try {
                    everyone = path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByGroupName("Everyone");
                }
                catch (UserPrincipalNotFoundException e) {
                    everyone = null;
                }
                if (group == null) group = everyone;
                return toPosixFilePermissions(Files.getOwner(path, options), group, everyone, acl.getAcl());
            }
        }
        else return view.readAttributes().permissions();
    }

    public static boolean isReadOnly(Path path, LinkOption... options) throws IOException {
        DosFileAttributeView view = Files.getFileAttributeView(path, DosFileAttributeView.class, options);
        if (view != null) return view.readAttributes().isReadOnly();
        else {
            Set<PosixFilePermission> permissions = getPosixFilePermissions(path, options);
            return !permissions.contains(PosixFilePermission.OWNER_WRITE) && !permissions.contains(PosixFilePermission.GROUP_WRITE)
                    && !permissions.contains(PosixFilePermission.OTHERS_WRITE);
        }
    }

    public static Path setReadOnly(Path path, boolean readOnly, LinkOption... options) throws IOException {
        DosFileAttributeView view = Files.getFileAttributeView(path, DosFileAttributeView.class, options);
        if (view != null) view.setReadOnly(readOnly);
        else {
            Set<PosixFilePermission> permissions = getPosixFilePermissions(path, options);
            Set<PosixFilePermission> readOnlyPermissions = new HashSet<>(permissions);
            readOnlyPermissions.remove(PosixFilePermission.OWNER_WRITE);
            readOnlyPermissions.remove(PosixFilePermission.GROUP_WRITE);
            readOnlyPermissions.remove(PosixFilePermission.OTHERS_WRITE);
            setPosixFilePermissions(path, readOnlyPermissions, options);
        }
        return path;
    }
    
    public interface HardLinkProcess {
        int getHardLinkCount(Path path) throws IOException;
    }
    public static final HardLinkProcess HARD_LINK_PROCESS;
    static {
        if (OSInfo.IS_WINDOWS) HARD_LINK_PROCESS = WindowsFileSystemSupport::getNumberOfLinks;
        else HARD_LINK_PROCESS = path -> (Integer) Files.getAttribute(path, "unix:nlink", LinkOption.NOFOLLOW_LINKS);
    }

    public static Set<StandardCopyOption> toStandardCopyOptions(int options) {
        options = FileSystem.CopyOption.removeUnusedBits(options);
        List<StandardCopyOption> copyOptions = new ArrayList<>(3);
        if ((options & FileSystem.CopyOption.REPLACE_EXISTING) != 0) copyOptions.add(StandardCopyOption.REPLACE_EXISTING);
        if ((options & FileSystem.CopyOption.COPY_ATTRIBUTES) != 0) copyOptions.add(StandardCopyOption.COPY_ATTRIBUTES);
        if ((options & FileSystem.CopyOption.ATOMIC_MOVE) != 0) copyOptions.add(StandardCopyOption.ATOMIC_MOVE);
        return Collections.unmodifiableSet(new HashSet<>(copyOptions));
    }

    public static Set<StandardOpenOption> toStandardOpenOptions(int options) {
        options = FileSystem.OpenOption.removeUnusedBits(options);
        List<StandardOpenOption> openOptions = new ArrayList<>(9);
        openOptions.add(StandardOpenOption.READ);
        if ((options & FileSystem.OpenOption.WRITE) != 0) openOptions.add(StandardOpenOption.WRITE);
        if ((options & FileSystem.OpenOption.APPEND) != 0) openOptions.add(StandardOpenOption.APPEND);
        if ((options & FileSystem.OpenOption.TRUNCATE_EXISTING) != 0) openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
        if ((options & FileSystem.OpenOption.CREATE) != 0) openOptions.add(StandardOpenOption.CREATE);
        if ((options & FileSystem.OpenOption.CREATE_NEW) != 0) openOptions.add(StandardOpenOption.CREATE_NEW);
        if ((options & FileSystem.OpenOption.DELETE_ON_CLOSE) != 0) openOptions.add(StandardOpenOption.DELETE_ON_CLOSE);
        if ((options & FileSystem.OpenOption.SYNC) != 0) openOptions.add(StandardOpenOption.SYNC);
        if ((options & FileSystem.OpenOption.DSYNC) != 0) openOptions.add(StandardOpenOption.DSYNC);
        return Collections.unmodifiableSet(new HashSet<>(openOptions));
    }

    public static FileTime getLastAccessTime(Path path, LinkOption... options) throws IOException {
        return Files.readAttributes(path, BasicFileAttributes.class, options).lastAccessTime();
    }

    public static Path setLastAccessTime(Path path, FileTime time) throws IOException {
        Files.getFileAttributeView(path, BasicFileAttributeView.class)
                .setTimes(null, Objects.requireNonNull(time), null);
        return path;
    }

    public static FileTime getCreationTime(Path path, LinkOption... options) throws IOException {
        return Files.readAttributes(path, BasicFileAttributes.class, options).creationTime();
    }

    public static Path setCreationTime(Path path, FileTime time) throws IOException {
        Files.getFileAttributeView(path, BasicFileAttributeView.class)
                .setTimes(null, null, Objects.requireNonNull(time));
        return path;
    }

    public static FileDescriptor getFileDescriptor(FileChannel fileChannel) {
        try {
            return (FileDescriptor) ReflectionSupport.getObjectField(fileChannel, fileChannel.getClass().getDeclaredField("fd"));
        } catch (NoSuchFieldException e) {
            throw new UnexpectedError(e);
        }
    }

    public interface FDProcess {
        int toFD(FileDescriptor descriptor);
        long toHANDLE(FileDescriptor descriptor);
    }

    public static final FDProcess FD_PROCESS;
    static {
        if (OSInfo.IS_WINDOWS) {
            try {
                Field fdField = FileDescriptor.class.getDeclaredField("fd");
                Field handleField = FileDescriptor.class.getDeclaredField("handle");
                FD_PROCESS = new FDProcess() {
                    @Override
                    public int toFD(FileDescriptor descriptor) {
                        return ReflectionSupport.getIntField(descriptor, fdField);
                    }
                    @Override
                    public long toHANDLE(FileDescriptor descriptor) {
                        long hFile = ReflectionSupport.getLongField(descriptor, handleField);
                        if (hFile == -1) {
                            int fd = ReflectionSupport.getIntField(descriptor, fdField);
                            if (fd == -1) hFile = 0;
                            else hFile = WindowsFileSystemSupport._get_osfhandle(fd);
                        }
                        return hFile;
                    }
                };
            } catch (NoSuchFieldException e) {
                throw new UnexpectedError(e);
            }
        }
        else {
            try {
                Field field = FileDescriptor.class.getDeclaredField("fd");
                FD_PROCESS = new FDProcess() {
                    @Override
                    public int toFD(FileDescriptor descriptor) {
                        return ReflectionSupport.getIntField(descriptor, field);
                    }
                    @Override
                    public long toHANDLE(FileDescriptor descriptor) {
                        return 0;
                    }
                };
            } catch (NoSuchFieldException e) {
                throw new UnexpectedError(e);
            }
        }
    }

    private static final Method getBlockSizeMethod;
    static {
        Method method;
        try {
            method = FileStore.class.getDeclaredMethod("getBlockSize");
        } catch (NoSuchMethodException e) {
            method = null;
        }
        getBlockSizeMethod = method;
    }

    public static long getBlockSize(FileStore fileStore) throws IOException {
        if (getBlockSizeMethod == null) return -1;
        else {
            try {
                return ReflectionSupport.invokeLongMethod(fileStore, getBlockSizeMethod);
            } catch (InvocationTargetException e) {
                Throwable target = e.getTargetException();
                if (target instanceof IOException) throw (IOException) target;
                else if (target instanceof RuntimeException) throw (RuntimeException) target;
                else if (target instanceof Error) throw (Error) target;
                else throw new UnexpectedError(target);
            }
        }
    }

}
