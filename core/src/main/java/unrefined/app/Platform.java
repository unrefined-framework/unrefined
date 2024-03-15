package unrefined.app;

import unrefined.context.Environment;

import java.io.File;
import java.nio.ByteOrder;

public abstract class Platform {

    private static volatile Platform INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Platform getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.platform", Platform.class);
        }
        return INSTANCE;
    }

    public String getBackendVendor() {
        return Environment.properties.getProperty("unrefined.backend.vendor");
    }
    public String getPlatformVendor() {
        return Environment.properties.getProperty("unrefined.platform.vendor");
    }
    public String getSystemVendor() {
        return Environment.properties.getProperty("unrefined.os.vendor");
    }
    public String getRuntimeVendor() {
        return Environment.properties.getProperty("unrefined.runtime.vendor");
    }
    public String getBackendImplementer() {
        return Environment.properties.getProperty("unrefined.backend.implementer");
    }
    public String getPlatformImplementer() {
        return Environment.properties.getProperty("unrefined.platform.implementer");
    }
    public String getSystemImplementer() {
        return Environment.properties.getProperty("unrefined.os.implementer");
    }
    public String getRuntimeImplementer() {
        return Environment.properties.getProperty("unrefined.runtime.implementer");
    }
    /**
     * Gets the name of Unrefined backend. e.g. <code>Unrefined For Java Standard Edition</code>
     * @return the name of Unrefined backend
     */
    public String getBackendName() {
        return Environment.properties.getProperty("unrefined.backend.name");
    }
    /**
     * Gets the name of underlying platform. e.g. <code>Android</code>
     * @return the name of underlying platform
     */
    public String getPlatformName() {
        return Environment.properties.getProperty("unrefined.platform.name");
    }
    /**
     * Gets the name of running operating system. e.g. <code>Linux</code>
     * @return the name of running operating system
     */
    public String getSystemName() {
        return Environment.properties.getProperty("unrefined.os.name");
    }
    /**
     * Gets the name of application runtime. e.g. <code>OpenJDK Runtime Environment</code>
     * @return the name of application runtime
     */
    public String getRuntimeName() {
        return Environment.properties.getProperty("unrefined.runtime.name");
    }
    /**
     * Gets the version name of Unrefined backend. e.g. <code>0.4.0</code>
     * @return the version name of Unrefined backend
     */
    public String getBackendVersionName() {
        return Environment.properties.getProperty("unrefined.backend.version.name");
    }
    public String getPlatformVersionName() {
        return Environment.properties.getProperty("unrefined.platform.version.name");
    }
    public String getSystemVersionName() {
        return Environment.properties.getProperty("unrefined.os.version.name");
    }
    public String getRuntimeVersionName() {
        return Environment.properties.getProperty("unrefined.runtime.version.name");
    }
    public String getBackendVersionCode() {
        return Environment.properties.getProperty("unrefined.backend.version.code");
    }
    public String getPlatformVersionCode() {
        return Environment.properties.getProperty("unrefined.platform.version.code");
    }
    public String getSystemVersionCode() {
        return Environment.properties.getProperty("unrefined.os.version.code");
    }
    public String getRuntimeVersionCode() {
        return Environment.properties.getProperty("unrefined.runtime.version.code");
    }
    public String getArchitecture() {
        return Environment.properties.getProperty("unrefined.os.arch.name");
    }
    public int getIntModelBits() {
        return Environment.properties.parseIntProperty("unrefined.os.arch.bitmode.int");
    }
    public int getLongModelBits() {
        return Environment.properties.parseIntProperty("unrefined.os.arch.bitmode.long");
    }
    public int getAddressModelBits() {
        return Environment.properties.parseIntProperty("unrefined.os.arch.bitmode.address");
    }
    public String getBinaryInterface() {
        return Environment.properties.getProperty("unrefined.os.arch.abi");
    }
    public ByteOrder getEndianness() {
        return Environment.properties.getProperty("unrefined.os.endian").equalsIgnoreCase("big") ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }
    public boolean isHeadless() {
        return Environment.properties.parseBooleanProperty("unrefined.environment.headless");
    }
    public String getLineSeparator() {
        return Environment.properties.getProperty("unrefined.os.separator.line");
    }
    public String getFileSeparator() {
        return Environment.properties.getProperty("unrefined.os.separator.file");
    }
    public String getPathSeparator() {
        return Environment.properties.getProperty("unrefined.os.separator.path");
    }
    public File getTempDirectory() {
        return new File(Environment.properties.getProperty("unrefined.os.directory.tmp"));
    }
    public File getHomeDirectory() {
        return new File(Environment.properties.getProperty("unrefined.os.directory.home"));
    }
    public File getConfigDirectory() {
        return new File(Environment.properties.getProperty("unrefined.os.directory.config"));
    }
    public File getDataDirectory() {
        return new File(Environment.properties.getProperty("unrefined.os.directory.data"));
    }
    public File getCacheDirectory() {
        return new File(Environment.properties.getProperty("unrefined.os.directory.cache"));
    }
    public File getNullFile() {
        return new File(Environment.properties.getProperty("unrefined.os.file.null"));
    }

}