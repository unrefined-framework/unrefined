package unrefined.app;

import unrefined.context.Environment;

import java.nio.ByteOrder;

public abstract class Platform {

    private static volatile Platform INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Platform getPlatform() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global().get("unrefined.runtime.platform", Platform.class);
        }
        return INSTANCE;
    }

    public String getBackendVendor() {
        return Environment.properties().getProperty("unrefined.backend.vendor");
    }
    public String getPlatformVendor() {
        return Environment.properties().getProperty("unrefined.platform.vendor");
    }
    public String getSystemVendor() {
        return Environment.properties().getProperty("unrefined.os.vendor");
    }
    public String getRuntimeVendor() {
        return Environment.properties().getProperty("unrefined.runtime.vendor");
    }
    /**
     * Gets the name of UXGL backend. e.g. <code>Java Standard Edition</code>
     * @return the name of UXGL backend
     */
    public String getBackendName() {
        return Environment.properties().getProperty("unrefined.backend.name");
    }
    /**
     * Gets the name of underlying platform. e.g. <code>Android</code>
     * @return the name of underlying platform
     */
    public String getPlatformName() {
        return Environment.properties().getProperty("unrefined.platform.name");
    }
    /**
     * Gets the name of running operating system. e.g. <code>Linux</code>
     * @return the name of running operating system
     */
    public String getSystemName() {
        return Environment.properties().getProperty("unrefined.os.name");
    }
    /**
     * Gets the name of application runtime. e.g. <code>OpenJDK Runtime Environment</code>
     * @return the name of application runtime
     */
    public String getRuntimeName() {
        return Environment.properties().getProperty("unrefined.runtime.name");
    }
    /**
     * Gets the version name of UXGL backend. e.g. <code>0.4.0</code>
     * @return the version name of UXGL backend
     */
    public String getBackendVersionName() {
        return Environment.properties().getProperty("unrefined.backend.version.name");
    }
    public String getPlatformVersionName() {
        return Environment.properties().getProperty("unrefined.platform.version.name");
    }
    public String getSystemVersionName() {
        return Environment.properties().getProperty("unrefined.os.version.name");
    }
    public String getRuntimeVersionName() {
        return Environment.properties().getProperty("unrefined.runtime.version.name");
    }
    public String getBackendVersionCode() {
        return Environment.properties().getProperty("unrefined.backend.version.code");
    }
    public String getPlatformVersionCode() {
        return Environment.properties().getProperty("unrefined.platform.version.code");
    }
    public String getSystemVersionCode() {
        return Environment.properties().getProperty("unrefined.os.version.code");
    }
    public String getRuntimeVersionCode() {
        return Environment.properties().getProperty("unrefined.runtime.version.code");
    }
    public String getArchitecture() {
        return Environment.properties().getProperty("unrefined.os.arch.name");
    }
    public int getIntModelBits() {
        return Environment.properties().parseIntProperty("unrefined.os.arch.bitmode.int");
    }
    public int getLongModelBits() {
        return Environment.properties().parseIntProperty("unrefined.os.arch.bitmode.long");
    }
    public int getAddressModelBits() {
        return Environment.properties().parseIntProperty("unrefined.os.arch.bitmode.address");
    }
    public String getBinaryInterface() {
        return Environment.properties().getProperty("unrefined.os.arch.abi");
    }
    public ByteOrder getEndianness() {
        return Environment.properties().getProperty("unrefined.os.endian").equalsIgnoreCase("big") ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }
    public boolean isHeadless() {
        return Environment.properties().parseBooleanProperty("unrefined.environment.headless");
    }

}
