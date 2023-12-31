package unrefined.runtime;

import unrefined.context.Environment;
import unrefined.desktop.AWTSupport;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public final class DesktopRuntime {

    private DesktopRuntime() {
        throw new NotInstantiableError(DesktopRuntime.class);
    }

    private static final AtomicBoolean setup = new AtomicBoolean(false);
    public static void setup(String[] args) {
        if (setup.compareAndSet(false, true)) {
            try {
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                Class<?> mainClazz = Class.forName(stackTraceElements[stackTraceElements.length - 1].getClassName());
                Package mainPackage = mainClazz.getPackage();
                Environment.properties().setProperty("unrefined.app.vendor", mainPackage.getSpecificationVendor());
                Environment.properties().setProperty("unrefined.app.name", mainPackage.getSpecificationTitle());
                Environment.properties().setProperty("unrefined.app.version.name", mainPackage.getSpecificationVersion());
                Environment.properties().setProperty("unrefined.app.version.code", mainPackage.getImplementationVersion());
                Environment.properties().setProperty("unrefined.app.package", mainPackage.getName());
                try {
                    Attributes attributes = new Manifest(mainClazz.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF")).getMainAttributes();
                    Environment.properties().setProperty("unrefined.backend.vendor", attributes.getValue("UXGL-Implementation-Vendor"));
                    Environment.properties().setProperty("unrefined.backend.name", attributes.getValue("UXGL-Implementation-Title"));
                    Environment.properties().setProperty("unrefined.backend.version.name", attributes.getValue("UXGL-Specification-Version"));
                    Environment.properties().setProperty("unrefined.backend.version.code", attributes.getValue("UXGL-Implementation-Version"));
                }
                catch (IOException ignored) {
                }
            } catch (ClassNotFoundException e) {
                throw new UnexpectedError(e);
            }

            AWTSupport.patch();

            Environment.global().put("unrefined.runtime.platform", new DesktopPlatform());
            Environment.global().put("unrefined.runtime.base64", new DesktopBase64());
            Environment.global().put("unrefined.runtime.reflection", new DesktopReflection());
            Environment.global().put("unrefined.runtime.cleaner", new DesktopCleaner());
            Environment.global().put("unrefined.runtime.console", new DesktopConsole());
            Environment.global().put("unrefined.runtime.drawing", new DesktopDrawing());
            Environment.global().put("unrefined.runtime.foreign", new DesktopForeign());

            Environment.global().put("unrefined.runtime.textManager", new BaseTextManager());
            Environment.global().put("unrefined.runtime.eventBus", new BaseEventBus());

            Environment.global().put("unrefined.runtime.dispatcher", new DesktopDispatcher());
            Environment.global().put("unrefined.runtime.logger", new DesktopLogger());
            Environment.global().put("unrefined.runtime.assetLoader", new DesktopAssetLoader());
            Environment.global().put("unrefined.runtime.allocator", new DesktopAllocator());
        }
    }

}
