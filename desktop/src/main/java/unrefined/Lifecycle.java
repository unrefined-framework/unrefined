package unrefined;

import unrefined.app.Runtime;
import unrefined.context.Environment;
import unrefined.desktop.AWTSupport;
import unrefined.runtime.BaseDispatcher;
import unrefined.runtime.BaseEventBus;
import unrefined.runtime.BaseTextManager;
import unrefined.runtime.DesktopAllocator;
import unrefined.runtime.DesktopArithmetic;
import unrefined.runtime.DesktopAssetLoader;
import unrefined.runtime.DesktopAtomic;
import unrefined.runtime.DesktopBase64;
import unrefined.runtime.DesktopCleaner;
import unrefined.runtime.DesktopConsole;
import unrefined.runtime.DesktopDateTime;
import unrefined.runtime.DesktopDispatcher;
import unrefined.runtime.DesktopDrawing;
import unrefined.runtime.DesktopFileSystem;
import unrefined.runtime.DesktopFileWatcher;
import unrefined.runtime.DesktopForeign;
import unrefined.runtime.DesktopLog;
import unrefined.runtime.DesktopNet;
import unrefined.runtime.DesktopPlatform;
import unrefined.runtime.DesktopReflection;
import unrefined.runtime.DesktopRuntime;
import unrefined.runtime.DesktopSampled;
import unrefined.runtime.DesktopThreading;
import unrefined.util.FastArray;
import unrefined.util.UnexpectedError;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static unrefined.app.Runtime.Flag.*;

public class Lifecycle {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static volatile int flags;

    public static int getFlags() {
        return flags;
    }

    public static void onMain(int flags, String... args) {
        Environment.properties.putIntProperty("unrefined.runtime.flags", Runtime.Flag.removeUnusedBits(flags));
        onMain(args);
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

    public static void onMain(String... args) {
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
                    Attributes attributes = new Manifest(mainClazz.getClassLoader().getResourceAsStream("META-INF/unrefined/MANIFEST.MF")).getMainAttributes();
                    Environment.properties.setProperty("unrefined.backend.vendor", attributes.getValue("Specification-Vendor"));
                    Environment.properties.setProperty("unrefined.backend.name", attributes.getValue("Specification-Title"));
                    Environment.properties.setProperty("unrefined.backend.version.name", attributes.getValue("Specification-Version"));
                    Environment.properties.setProperty("unrefined.backend.implementer", attributes.getValue("Implementation-Vendor"));
                    Environment.properties.setProperty("unrefined.backend.package", attributes.getValue("Implementation-Title"));
                    Environment.properties.setProperty("unrefined.backend.version.code", attributes.getValue("Implementation-Version"));
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
            Environment.global.put("unrefined.runtime.fileSystem", new DesktopFileSystem());
            Environment.global.put("unrefined.runtime.fileWatcher", new DesktopFileWatcher());
            Environment.global.put("unrefined.runtime.net", new DesktopNet());
            Environment.global.put("unrefined.runtime.dateTime", new DesktopDateTime());

            Environment.global.put("unrefined.runtime.textManager", new BaseTextManager());
            Environment.global.put("unrefined.runtime.eventBus", new BaseEventBus());

            Environment.global.put("unrefined.runtime.log", new DesktopLog());
            Environment.global.put("unrefined.runtime.assetLoader", new DesktopAssetLoader());
        }
    }

}
