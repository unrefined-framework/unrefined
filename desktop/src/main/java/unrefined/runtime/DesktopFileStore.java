package unrefined.runtime;

import unrefined.desktop.FileSystemSupport;
import unrefined.io.file.FileStore;

import java.io.IOException;
import java.util.Objects;

public class DesktopFileStore extends FileStore {

    private final java.nio.file.FileStore fileStore;
    public DesktopFileStore(java.nio.file.FileStore fileStore) {
        this.fileStore = Objects.requireNonNull(fileStore);
    }

    @Override
    public String name() {
        return fileStore.name();
    }

    @Override
    public String type() {
        return fileStore.type();
    }

    @Override
    public boolean isReadOnly() {
        return fileStore.isReadOnly();
    }

    @Override
    public long totalSpace() throws IOException {
        return fileStore.getTotalSpace();
    }

    @Override
    public long usableSpace() throws IOException {
        return fileStore.getUsableSpace();
    }

    @Override
    public long freeSpace() throws IOException {
        return fileStore.getUnallocatedSpace();
    }

    @Override
    public long blockSize() throws IOException {
        return FileSystemSupport.getBlockSize(fileStore);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopFileStore that = (DesktopFileStore) object;

        return fileStore.equals(that.fileStore);
    }

    @Override
    public int hashCode() {
        return fileStore.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + '@' + Integer.toHexString(hashCode())
                + '{' +
                "name=" + name() +
                ", type=" + type() +
                ", readOnly=" + isReadOnly() +
                '}';
    }

}
