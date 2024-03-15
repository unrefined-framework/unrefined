package unrefined.runtime;

import unrefined.app.Preferences;
import unrefined.app.Runtime;
import unrefined.context.Container;
import unrefined.context.ContainerListener;
import unrefined.context.Environment;
import unrefined.desktop.AWTSupport;
import unrefined.desktop.OSInfo;
import unrefined.desktop.RuntimeSupport;
import unrefined.desktop.ShutdownGuard;
import unrefined.desktop.SizeOfSupport;
import unrefined.desktop.StandardDirectories;
import unrefined.desktop.VMInfo;
import unrefined.internal.macos.MacPreferences;
import unrefined.internal.posix.PosixPreferences;
import unrefined.internal.windows.WindowsPreferences;
import unrefined.util.FastArray;
import unrefined.util.UnexpectedError;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static unrefined.app.Runtime.Flag.*;
import static unrefined.desktop.UnsafeSupport.UNSAFE;

public final class DesktopRuntime extends Runtime {

    {
        ShutdownGuard.register(() -> DesktopRuntime.this.onShutdown().emit());
    }

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    private static volatile int flags;

    public static int getFlags() {
        return flags;
    }

    public static void initialize(int flags, String... args) {
        Environment.properties.putIntProperty("unrefined.runtime.flags", Flag.removeUnusedBits(flags));
        initialize(args);
    }

    private static String getDomain(Package p) {
        String text = p.getName();
        String[] split = text.split("\\.");
        FastArray.reverse(split);
        StringBuilder builder = new StringBuilder();
        int last = split.length - 1;
        for (int i = 0; i < last; i ++) {
            builder.append(split[i]).append(".");
        }
        if (last >= 0) builder.append(split[last]);
        return builder.toString();
    }

    public static void initialize(String... args) {
        if (initialized.compareAndSet(false, true)) {
            try {
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                Class<?> mainClazz = Class.forName(stackTraceElements[stackTraceElements.length - 1].getClassName());
                Package mainPackage = mainClazz.getPackage();
                Environment.properties.setProperty("unrefined.app.vendor",
                        mainPackage.getSpecificationVendor() == null ? getDomain(mainPackage) : mainPackage.getSpecificationVendor());
                Environment.properties.setProperty("unrefined.app.name",
                        mainPackage.getSpecificationTitle() == null ? mainClazz.getSimpleName() : mainPackage.getSpecificationTitle());
                Environment.properties.setProperty("unrefined.app.version.name",
                        mainPackage.getSpecificationVersion() == null ? "1.0-SNAPSHOT" : mainPackage.getSpecificationVersion());
                Environment.properties.setProperty("unrefined.app.implementer",
                        mainPackage.getImplementationVendor() == null ? getDomain(mainPackage) : mainPackage.getImplementationVendor());
                Environment.properties.setProperty("unrefined.app.package",
                        mainPackage.getImplementationTitle() == null ? mainPackage.getName() : mainPackage.getImplementationTitle());
                Environment.properties.setProperty("unrefined.app.version.code",
                        mainPackage.getImplementationVersion() == null ? "1" : mainPackage.getImplementationVersion());
                try {
                    Attributes attributes = new Manifest(mainClazz.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF")).getMainAttributes();
                    Environment.properties.setProperty("unrefined.backend.vendor", attributes.getValue("Unrefined-Specification-Vendor"));
                    Environment.properties.setProperty("unrefined.backend.name", attributes.getValue("Unrefined-Specification-Title"));
                    Environment.properties.setProperty("unrefined.backend.version.name", attributes.getValue("Unrefined-Specification-Version"));
                    Environment.properties.setProperty("unrefined.backend.implementer", attributes.getValue("Unrefined-Implementation-Vendor"));
                    Environment.properties.setProperty("unrefined.backend.package", attributes.getValue("Unrefined-Implementation-Title"));
                    Environment.properties.setProperty("unrefined.backend.version.code", attributes.getValue("Unrefined-Implementation-Version"));
                }
                catch (IOException ignored) {
                }
            } catch (ClassNotFoundException e) {
                throw new UnexpectedError(e);
            }

            flags = Integer.parseInt(System.getProperty("unrefined.runtime.flags", Integer.toString(ALL)));

            if ((flags & GRAPHICS) != 0) {
                Environment.properties.put("unrefined.desktop.graphics.buffered", "true");
                AWTSupport.patch();

                Environment.global.put("unrefined.runtime.drawing", new DesktopDrawing());

                Environment.global.put("unrefined.runtime.dispatcher", new DesktopDispatcher());
            }
            else {
                Environment.global.put("unrefined.runtime.dispatcher", new BaseDispatcher("Unrefined Default Dispatcher"));
            }
            if ((flags & AUDIO) != 0) {
                Environment.global.put("unrefined.runtime.sampled", new DesktopSampled());
            }

            Environment.global.put("unrefined.runtime.reflection", new DesktopReflection());
            Environment.global.put("unrefined.runtime.arithmetic", new DesktopArithmetic());
            Environment.global.put("unrefined.runtime.runtime", new DesktopRuntime());
            Environment.global.put("unrefined.runtime.platform", new DesktopPlatform());
            Environment.global.put("unrefined.runtime.threading", new DesktopThreading());
            Environment.global.put("unrefined.runtime.base64", new DesktopBase64());
            Environment.global.put("unrefined.runtime.atomic", new DesktopAtomic());
            Environment.global.put("unrefined.runtime.cleaner", new DesktopCleaner());
            Environment.global.put("unrefined.runtime.console", new DesktopConsole());
            Environment.global.put("unrefined.runtime.foreign", new DesktopForeign());
            Environment.global.put("unrefined.runtime.allocator", new DesktopAllocator());

            Environment.global.put("unrefined.runtime.textManager", new BaseTextManager());
            Environment.global.put("unrefined.runtime.eventBus", new BaseEventBus());

            Environment.global.put("unrefined.runtime.logger", new DesktopLogger());
            Environment.global.put("unrefined.runtime.assetLoader", new DesktopAssetLoader());
        }
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
        return new File("");
    }

    @Override
    public boolean isShutdownThread(Thread thread) {
        return ShutdownGuard.isShutdownThread(thread);
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
            ShutdownGuard.register(this::collect);
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
