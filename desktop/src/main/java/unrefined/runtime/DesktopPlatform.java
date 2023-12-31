package unrefined.runtime;

import unrefined.app.Platform;
import unrefined.context.Environment;
import unrefined.desktop.ABI;
import unrefined.internal.OperatingSystem;

import java.awt.GraphicsEnvironment;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class DesktopPlatform extends Platform {

    static {
        // FIXME complete these
        /*
        Environment.properties().setProperty("unrefined.platform.vendor", );
        Environment.properties().setProperty("unrefined.platform.name", );
        Environment.properties().setProperty("unrefined.platform.version.name", );
        Environment.properties().setProperty("unrefined.platform.version.code", );
        Environment.properties().setProperty("unrefined.os.vendor", );
        Environment.properties().setProperty("unrefined.os.name", );
        Environment.properties().setProperty("unrefined.os.version.name", );
        Environment.properties().setProperty("unrefined.os.version.code", );
         */
        Environment.properties().setProperty("unrefined.runtime.vendor", System.getProperty("java.vendor"));
        Environment.properties().setProperty("unrefined.runtime.name", System.getProperty("java.runtime.name"));
        Environment.properties().setProperty("unrefined.runtime.version.name",
                System.getProperty("java.specification.name") + " " + System.getProperty("java.specification.version"));
        Environment.properties().setProperty("unrefined.runtime.version.code", System.getProperty("java.runtime.version"));

        Environment.properties().setProperty("unrefined.os.arch", System.getProperty("os.arch"));
        Environment.properties().putIntProperty("unrefined.os.arch.bitmode.int", ABI.I);
        Environment.properties().putIntProperty("unrefined.os.arch.bitmode.long", ABI.L);
        Environment.properties().putIntProperty("unrefined.os.arch.bitmode.address", ABI.P);
        Environment.properties().setProperty("unrefined.os.arch.abi", ABI.IDENTIFIER);
        Environment.properties().setProperty("unrefined.os.endian", ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? "big" : "little");
        Environment.properties().setProperty("unrefined.environment.charset.default", Charset.defaultCharset().displayName());
        Environment.properties().setProperty("unrefined.environment.charset.system", OperatingSystem.NATIVE_CHARSET.displayName());
        Environment.properties().setProperty("unrefined.environment.charset.wide", OperatingSystem.WIDE_CHARSET.displayName());
        Environment.properties().putBooleanProperty("unrefined.environment.headless", GraphicsEnvironment.isHeadless());
    }

}
