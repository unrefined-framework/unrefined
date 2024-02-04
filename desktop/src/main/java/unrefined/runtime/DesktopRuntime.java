package unrefined.runtime;

import unrefined.app.Runtime;
import unrefined.context.Environment;
import unrefined.desktop.RuntimeSupport;
import unrefined.desktop.ShutdownGuard;
import unrefined.desktop.SizeOfSupport;
import unrefined.desktop.VMInfo;
import unrefined.util.UnexpectedError;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static unrefined.app.Runtime.Flag.ALL;
import static unrefined.app.Runtime.Flag.MEDIA;
import static unrefined.desktop.UnsafeSupport.UNSAFE;

public final class DesktopRuntime extends Runtime {

    {
        ShutdownGuard.register(() -> DesktopRuntime.this.onShutdown().emit());
    }

    private static final AtomicBoolean setup = new AtomicBoolean(false);

    private static volatile int flags;

    public static int getFlags() {
        return flags;
    }

    public static void setup(String[] args) {
        if (setup.compareAndSet(false, true)) {
            try {
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                Class<?> mainClazz = Class.forName(stackTraceElements[stackTraceElements.length - 1].getClassName());
                Package mainPackage = mainClazz.getPackage();
                Environment.properties.setProperty("unrefined.app.vendor", mainPackage.getSpecificationVendor());
                Environment.properties.setProperty("unrefined.app.name", mainPackage.getSpecificationTitle());
                Environment.properties.setProperty("unrefined.app.version.name", mainPackage.getSpecificationVersion());
                Environment.properties.setProperty("unrefined.app.version.code", mainPackage.getImplementationVersion());
                Environment.properties.setProperty("unrefined.app.package", mainPackage.getName());
                try {
                    Attributes attributes = new Manifest(mainClazz.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF")).getMainAttributes();
                    Environment.properties.setProperty("unrefined.backend.vendor", attributes.getValue("Unrefined-Implementation-Vendor"));
                    Environment.properties.setProperty("unrefined.backend.name", attributes.getValue("Unrefined-Implementation-Title"));
                    Environment.properties.setProperty("unrefined.backend.version.name", attributes.getValue("Unrefined-Specification-Version"));
                    Environment.properties.setProperty("unrefined.backend.version.code", attributes.getValue("Unrefined-Implementation-Version"));
                }
                catch (IOException ignored) {
                }
            } catch (ClassNotFoundException e) {
                throw new UnexpectedError(e);
            }

            flags = Integer.parseInt(System.getProperty("unrefined.runtime.flags", Integer.toString(ALL)));

            if ((flags & MEDIA) != 0) {
                try {
                    Class.forName("unrefined.desktop.AWTSupport").getDeclaredMethod("patch").invoke(null);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                         ClassNotFoundException e) {
                    throw new UnexpectedError(e);
                }
                finally {
                    Environment.global.put("unrefined.runtime.drawing", new DesktopDrawing());

                    Environment.global.put("unrefined.runtime.dispatcher", new DesktopDispatcher());
                }
            }
            else {
                Environment.global.put("unrefined.runtime.dispatcher", new BaseDispatcher("Unrefined Default Dispatcher"));
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
