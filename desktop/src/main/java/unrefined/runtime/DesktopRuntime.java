package unrefined.runtime;

import unrefined.app.Preferences;
import unrefined.app.Runtime;
import unrefined.context.Container;
import unrefined.context.ContainerListener;
import unrefined.desktop.OSInfo;
import unrefined.desktop.RuntimeSupport;
import unrefined.desktop.ShutdownHook;
import unrefined.desktop.SizeOfSupport;
import unrefined.desktop.StandardDirectories;
import unrefined.desktop.VMInfo;
import unrefined.desktop.macos.MacPreferences;
import unrefined.desktop.posix.PosixPreferences;
import unrefined.desktop.windows.WindowsPreferences;

import java.io.File;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static unrefined.desktop.UnsafeSupport.UNSAFE;

public class DesktopRuntime extends Runtime {

    {
        ShutdownHook.register(() -> DesktopRuntime.this.onShutdown().emit());
    }

    @Override
    public Container createContainer(ContainerListener containerListener) {
        return new DesktopContainer(containerListener);
    }

    private volatile String NORMALIZED_APP_NAME;
    private final Object NORMALIZED_APP_NAME_LOCK = new Object();

    private String getNormalizedApplicationName() {
        if (NORMALIZED_APP_NAME == null) synchronized (NORMALIZED_APP_NAME_LOCK) {
            if (NORMALIZED_APP_NAME == null) NORMALIZED_APP_NAME = OSInfo.normalize(getApplicationName());
        }
        return NORMALIZED_APP_NAME;
    }

    @Override
    public Preferences getPreferences(String name) {
        Objects.requireNonNull(name);
        if (OSInfo.IS_WINDOWS) return WindowsPreferences.get("Software" + "\\" + getApplicationImplementer() + "\\" + getApplicationName(), name);
        else if (OSInfo.IS_MAC) {
            return MacPreferences.get(
                    (getApplicationPackage() + "." + getApplicationName())
                            .replace(" ", "-")
                            .replace(".", "/"), name);
        }
        else return PosixPreferences.get(StandardDirectories.CONFIG_HOME,
                    getApplicationName().toLowerCase(Locale.ENGLISH).replace(" ", ""), name);
        //else return DesktopPreferences.get(getConfigDirectory(), name.toLowerCase(Locale.ENGLISH).replace(" ", "-"));
    }

    @Override
    public boolean deletePreferences(String name) {
        Objects.requireNonNull(name);
        if (OSInfo.IS_WINDOWS) return WindowsPreferences.delete("Software" + "\\" + getApplicationImplementer() + "\\" + getApplicationName(), name);
        else if (OSInfo.IS_MAC) {
            return MacPreferences.delete(
                    (getApplicationPackage() + "." + getApplicationName())
                            .replace(" ", "-")
                            .replace(".", "/"), name);
        }
        else return PosixPreferences.delete(StandardDirectories.CONFIG_HOME,
                    getApplicationName().toLowerCase(Locale.ENGLISH).replace(" ", ""), name);
        //else return DesktopPreferences.delete(getConfigDirectory(), name.toLowerCase(Locale.ENGLISH).replace(" ", "-"));
    }

    private volatile File CONFIG_DIR;
    private final Object CONFIG_DIR_LOCK = new Object();
    @Override
    public File getConfigDirectory() {
        if (CONFIG_DIR == null) synchronized (CONFIG_DIR_LOCK) {
            if (CONFIG_DIR == null) CONFIG_DIR = new File(StandardDirectories.CONFIG_HOME, getNormalizedApplicationName());
        }
        return CONFIG_DIR;
    }

    private volatile File CACHE_DIR;
    private final Object CACHE_DIR_LOCK = new Object();
    @Override
    public File getCacheDirectory() {
        if (CACHE_DIR == null) synchronized (CACHE_DIR_LOCK) {
            if (CACHE_DIR == null) CACHE_DIR = new File(StandardDirectories.CACHE_HOME, getNormalizedApplicationName());
        }
        return CACHE_DIR;
    }

    private volatile File FILES_DIR;
    private final Object FILES_DIR_LOCK = new Object();
    @Override
    public File getFilesDirectory(String type) {
        if (FILES_DIR == null) synchronized (FILES_DIR_LOCK) {
            if (FILES_DIR == null) FILES_DIR = new File(StandardDirectories.DATA_HOME, getNormalizedApplicationName());
        }
        return new File(FILES_DIR, type == null ? "" : type);
    }

    @Override
    public File getHomeDirectory() {
        return StandardDirectories.USER_HOME;
    }

    @Override
    public File getTempDirectory() {
        return StandardDirectories.TEMP_HOME;
    }

    @Override
    public File getCurrentDirectory() {
        return new File(System.getProperty("user.dir"));
    }

    @Override
    public boolean isShutdownThread(Thread thread) {
        return ShutdownHook.isShutdownThread(thread);
    }

    @Override
    public int memoryPageSize() {
        return UNSAFE.pageSize();
    }

    @Override
    public long processId() {
        return RuntimeSupport.PID;
    }

    private static final AtomicBoolean gc = new AtomicBoolean(false);

    /**
     * This method guarantees that garbage collection is
     * done after the JVM shutdown is initialized
     */
    @Override
    public void collectOnExit() {
        if (gc.compareAndSet(false, true)) {
            //java.lang.Runtime.getRuntime().addShutdownHook(new Thread(this::garbageCollection, "Unrefined GC Performer"));
            ShutdownHook.register(this::collect);
        }
    }

    @Override
    public int arrayIndexScale(Class<?> clazz) {
        return UNSAFE.arrayIndexScale(clazz);
    }

    @Override
    public void sneakyThrows(Throwable throwable) {
        UNSAFE.throwException(throwable);
    }

    @Override
    public long sizeOf(Object object) {
        return SizeOfSupport.sizeOf(object);
    }

    @Override
    public long sizeOfType(Class<?> clazz) {
        return SizeOfSupport.sizeOfType(clazz);
    }

    @Override
    public int referenceSize() {
        return VMInfo.REFERENCE_SIZE;
    }

}
