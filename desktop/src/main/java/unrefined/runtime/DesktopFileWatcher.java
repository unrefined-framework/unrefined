package unrefined.runtime;

import unrefined.desktop.FileWatcherSupport;
import unrefined.io.file.FileWatcher;

import java.io.File;
import java.io.IOException;

public class DesktopFileWatcher extends FileWatcher {

    {
        FileWatcherSupport.addListener((path, kind) -> onFileWatch().emit(new FileWatchEvent(this, path.toFile(), kind.name().substring(6))));
    }

    @Override
    public void watch(File file) throws IOException {
        FileWatcherSupport.register(file);
    }

    @Override
    public void unwatch(File file) {
        FileWatcherSupport.unregister(file);
    }

    @Override
    public void clear() {
        FileWatcherSupport.clear();
    }

}
