package unrefined.io.file;

import unrefined.context.Environment;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.signal.Signal;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class FileWatcher {

    public static final String CREATE = "CREATE";
    public static final String DELETE = "DELETE";
    public static final String MODIFY = "MODIFY";

    private static volatile FileWatcher INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static FileWatcher getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.fileWatcher", FileWatcher.class);
        }
        return INSTANCE;
    }

    public abstract void watch(File file) throws IOException;
    public abstract void unwatch(File file);
    public void watch(File file, File... files) throws IOException {
        watch(file);
        for (File f : files) {
            watch(f);
        }
    }
    public void unwatch(File file, File... files) {
        unwatch(file);
        for (File f : files) {
            unwatch(f);
        }
    }
    public abstract void clear();

    private final Signal<EventSlot<FileWatchEvent>> onFileWatch = Signal.ofSlot();
    public Signal<EventSlot<FileWatchEvent>> onFileWatch() {
        return onFileWatch;
    }

    public static class FileWatchEvent extends Event<FileWatcher> {

        private final File entry;
        private final String kind;
        public FileWatchEvent(FileWatcher source, File entry, String kind) {
            super(source);
            this.entry = Objects.requireNonNull(entry);
            this.kind = Objects.requireNonNull(kind);
        }

        public File getEntry() {
            return entry;
        }

        public String getKind() {
            return kind;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            if (!super.equals(object)) return false;

            FileWatchEvent that = (FileWatchEvent) object;

            if (!entry.equals(that.entry)) return false;
            return kind.equals(that.kind);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + entry.hashCode();
            result = 31 * result + kind.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "entry=" + entry +
                    ", kind='" + kind + '\'' +
                    '}';
        }

    }

}
