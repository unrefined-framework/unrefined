package unrefined.runtime;

import unrefined.app.Platform;
import unrefined.context.Environment;
import unrefined.internal.MemoryLayoutUtils;

import java.awt.GraphicsEnvironment;
import java.nio.ByteOrder;

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

        Environment.properties().putIntProperty("unrefined.os.arch.bitmode.address", MemoryLayoutUtils.ADDRESS_SIZE);
        Environment.properties().putProperty("unrefined.os.arch.abi", MemoryLayoutUtils.ABI);
        Environment.properties().putProperty("unrefined.os.endian", ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? "big" : "little");
        Environment.properties().putBooleanProperty("unrefined.environment.headless", GraphicsEnvironment.isHeadless());
    }

}
