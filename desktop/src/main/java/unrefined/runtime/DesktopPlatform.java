package unrefined.runtime;

import unrefined.app.Platform;
import unrefined.context.Environment;
import unrefined.desktop.ABI;
import unrefined.desktop.StandardDirectories;
import unrefined.desktop.OSInfo;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class DesktopPlatform extends Platform {

    static {
        // FIXME complete these
        /*
        Environment.properties.putProperty("unrefined.platform.vendor", );
        Environment.properties.putProperty("unrefined.platform.implementer", );
        Environment.properties.putProperty("unrefined.platform.name", );
        Environment.properties.putProperty("unrefined.platform.version.name", );
        Environment.properties.putProperty("unrefined.platform.version.code", );
        Environment.properties.putProperty("unrefined.os.vendor", );
        Environment.properties.putProperty("unrefined.os.implementer", );
        Environment.properties.putProperty("unrefined.os.name", );
        Environment.properties.putProperty("unrefined.os.version.name", );
        Environment.properties.putProperty("unrefined.os.version.code", );
         */
        Environment.properties.putProperty("unrefined.runtime.vendor", System.getProperty("java.vendor"));
        Environment.properties.putProperty("unrefined.runtime.implementer", System.getProperty("java.vm.vendor"));
        Environment.properties.putProperty("unrefined.runtime.name", System.getProperty("java.runtime.name"));
        Environment.properties.putProperty("unrefined.runtime.version.name",
                System.getProperty("java.specification.name") + " " + System.getProperty("java.specification.version"));
        Environment.properties.putProperty("unrefined.runtime.version.code", System.getProperty("java.runtime.version"));
        Environment.properties.putProperty("unrefined.os.arch", System.getProperty("os.arch"));
        Environment.properties.putIntProperty("unrefined.os.arch.bitmode.int", ABI.I);
        Environment.properties.putIntProperty("unrefined.os.arch.bitmode.long", ABI.L);
        Environment.properties.putIntProperty("unrefined.os.arch.bitmode.address", ABI.P);
        Environment.properties.putProperty("unrefined.os.arch.abi", ABI.IDENTIFIER);
        Environment.properties.putProperty("unrefined.os.endian", ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? "big" : "little");
        Environment.properties.putProperty("unrefined.environment.charset.default", Charset.defaultCharset().displayName());
        Environment.properties.putProperty("unrefined.environment.charset.system", OSInfo.NATIVE_CHARSET.displayName());
        Environment.properties.putProperty("unrefined.environment.charset.wide", OSInfo.WIDE_CHARSET.displayName());
        Environment.properties.putProperty("unrefined.environment.charset.console", OSInfo.CONSOLE_CHARSET.displayName());
        Environment.properties.putBooleanProperty("unrefined.environment.headless", Boolean.parseBoolean(System.getProperty("java.awt.headless")));
        Environment.properties.putProperty("unrefined.os.separator.line", System.lineSeparator());
        Environment.properties.putProperty("unrefined.os.separator.file", File.separator);
        Environment.properties.putProperty("unrefined.os.separator.path", File.pathSeparator);
        Environment.properties.putProperty("unrefined.os.directory.tmp", StandardDirectories.TEMP_HOME.getAbsolutePath());
        Environment.properties.putProperty("unrefined.os.directory.home", StandardDirectories.USER_HOME.getAbsolutePath());
        Environment.properties.putProperty("unrefined.os.directory.config", StandardDirectories.CONFIG_HOME.getAbsolutePath());
        Environment.properties.putProperty("unrefined.os.directory.data", StandardDirectories.DATA_HOME.getAbsolutePath());
        Environment.properties.putProperty("unrefined.os.directory.cache", StandardDirectories.CACHE_HOME.getAbsolutePath());
        Environment.properties.putProperty("unrefined.os.file.null", OSInfo.NULL_FILE.getAbsolutePath());
    }

}
