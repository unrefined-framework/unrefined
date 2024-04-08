package unrefined.desktop;

import unrefined.util.NotInstantiableError;
import unrefined.util.concurrent.ConcurrentHashSet;
import unrefined.util.function.BiSlot;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class FileWatcherSupport {

    private FileWatcherSupport() {
        throw new NotInstantiableError(FileWatcherSupport.class);
    }

    private static volatile WatchService WATCH_SERVICE = null;
    private static final Object WATCH_SERVICE_LOCK = new Object();

    private static final Map<File, WatchKey> REGISTERED = new HashMap<>();
    private static final Set<File> DIRECTORIES = new HashSet<>();
    private static final Map<File, File> FILES = new HashMap<>();
    private static volatile Thread WATCH_THREAD;

    private static WatchService getWatchService() throws IOException {
        if (WATCH_SERVICE == null) synchronized (WATCH_SERVICE_LOCK) {
            if (WATCH_SERVICE == null) {
                WATCH_SERVICE = FileSystems.getDefault().newWatchService();
                WATCH_THREAD = new Thread(() -> {
                    try {
                        WatchKey key;
                        while ((key = WATCH_SERVICE.take()) != null) {
                            for (WatchEvent<?> event : key.pollEvents()) {
                                WatchEvent.Kind<?> kind = event.kind();
                                if (kind == OVERFLOW) continue;
                                @SuppressWarnings("unchecked")
                                WatchEvent<Path> cast = (WatchEvent<Path>) event;
                                Path path = cast.context().toAbsolutePath();
                                Path parent = path.getParent();
                                boolean emit = false;
                                for (File directories : DIRECTORIES) {
                                    if (parent.toString().equals(directories.getAbsolutePath())) {
                                        emit = true;
                                        break;
                                    }
                                }
                                if (!emit) {
                                    for (File file : FILES.keySet()) {
                                        if (path.toString().equals(file.getAbsolutePath())) {
                                            emit = true;
                                            break;
                                        }
                                    }
                                }
                                if (emit) {
                                    synchronized (LISTENERS) {
                                        for (BiSlot<Path, WatchEvent.Kind<Path>> listener : LISTENERS) {
                                            listener.accept(path, cast.kind());
                                        }
                                    }
                                }
                            }
                            if (!key.reset()) break;
                        }
                    }
                    catch (InterruptedException | ClosedWatchServiceException ignored) {
                    }
                }, "Unrefined FileWatcher Daemon");
                WATCH_THREAD.setDaemon(true);
                WATCH_THREAD.start();
                ShutdownHook.register(() -> {
                    try {
                        if (WATCH_SERVICE != null) WATCH_SERVICE.close();
                    } catch (IOException ignored) {
                    }
                });
            }
        }
        return WATCH_SERVICE;
    }

    public static void register(File file) throws IOException {
        file = file.getAbsoluteFile();
        if (file.isFile()) {
            if (!FILES.containsKey(file)) synchronized (FILES) {
                FILES.put(file, file.getParentFile());
                file = FILES.get(file);
            }
        }
        else {
            if (!DIRECTORIES.contains(file)) synchronized (DIRECTORIES) {
                DIRECTORIES.add(file);
            }
        }
        if (!REGISTERED.containsKey(file)) synchronized (REGISTERED) {
            if (!REGISTERED.containsKey(file)) {
                WatchKey watchKey = file.toPath().register(getWatchService(),
                        StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                REGISTERED.put(file, watchKey);
            }
        }
    }

    private static void cleanup() {
        try {
            WATCH_SERVICE.close();
            WATCH_SERVICE = null;
        } catch (IOException ignored) {
        }
    }

    public static void unregister(File file) {
        file = file.getAbsoluteFile();
        if (file.isFile()) {
            if (FILES.containsKey(file)) synchronized (FILES) {
                File parent = FILES.remove(file);
                if (FILES.containsValue(parent)) return;
                else file = parent;
            }
        }
        else if (DIRECTORIES.contains(file)) synchronized (DIRECTORIES) {
            DIRECTORIES.remove(file);
        }
        if (REGISTERED.containsKey(file)) synchronized (REGISTERED) {
            if (REGISTERED.containsKey(file)) {
                REGISTERED.remove(file).cancel();
            }
        }
        if (REGISTERED.isEmpty()) {
            cleanup();
        }
    }

    public static void clear() {
        synchronized (FILES) {
            FILES.clear();
        }
        synchronized (DIRECTORIES) {
            DIRECTORIES.clear();
        }
        synchronized (REGISTERED) {
            for (WatchKey watchKey : REGISTERED.values()) {
                watchKey.cancel();
            }
            REGISTERED.clear();
            cleanup();
        }
    }

    private static final Set<BiSlot<Path, WatchEvent.Kind<Path>>> LISTENERS = new ConcurrentHashSet<>();

    public static void addListener(BiSlot<Path, WatchEvent.Kind<Path>> listener) {
        LISTENERS.add(listener);
    }

    public static void removeListener(BiSlot<Path, WatchEvent.Kind<Path>> listener) {
        LISTENERS.remove(listener);
    }

    public static void clearListeners() {
        LISTENERS.clear();
    }

}
