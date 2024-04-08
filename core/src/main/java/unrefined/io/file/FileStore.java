package unrefined.io.file;

import java.io.IOException;

public abstract class FileStore {

    public abstract String name();
    public abstract String type();
    public abstract boolean isReadOnly();
    public abstract long totalSpace() throws IOException;
    public abstract long usableSpace() throws IOException;
    public abstract long freeSpace() throws IOException;
    public abstract long blockSize() throws IOException;

}