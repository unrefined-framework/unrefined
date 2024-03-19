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

    private volatile String BACKEND_VENDOR;
    private final Object BACKEND_VENDOR_LOCK = new Object();
    public String getBackendVendor() {
        if (BACKEND_VENDOR == null) synchronized (BACKEND_VENDOR_LOCK) {
            if (BACKEND_VENDOR == null) BACKEND_VENDOR = Environment.properties.getProperty("unrefined.backend.vendor");
        }
        return BACKEND_VENDOR;
    }
    private volatile String PLATFORM_VENDOR;
    private final Object PLATFORM_VENDOR_LOCK = new Object();
    public String getPlatformVendor() {
        if (PLATFORM_VENDOR == null) synchronized (PLATFORM_VENDOR_LOCK) {
            if (PLATFORM_VENDOR == null) PLATFORM_VENDOR = Environment.properties.getProperty("unrefined.platform.vendor");
        }
        return PLATFORM_VENDOR;
    }
    private volatile String SYSTEM_VENDOR;
    private final Object SYSTEM_VENDOR_LOCK = new Object();
    public String getSystemVendor() {
        if (SYSTEM_VENDOR == null) synchronized (SYSTEM_VENDOR_LOCK) {
            if (SYSTEM_VENDOR == null) SYSTEM_VENDOR = Environment.properties.getProperty("unrefined.os.vendor");
        }
        return SYSTEM_VENDOR;
    }
    private volatile String RUNTIME_VENDOR;
    private final Object RUNTIME_VENDOR_LOCK = new Object();
    public String getRuntimeVendor() {
        if (RUNTIME_VENDOR == null) synchronized (RUNTIME_VENDOR_LOCK) {
            if (RUNTIME_VENDOR == null) RUNTIME_VENDOR = Environment.properties.getProperty("unrefined.runtime.vendor");
        }
        return RUNTIME_VENDOR;
    }
    private volatile String BACKEND_IMPLEMENTER;
    private final Object BACKEND_IMPLEMENTER_LOCK = new Object();
    public String getBackendImplementer() {
        if (BACKEND_IMPLEMENTER == null) synchronized (BACKEND_IMPLEMENTER_LOCK) {
            if (BACKEND_IMPLEMENTER == null) BACKEND_IMPLEMENTER = Environment.properties.getProperty("unrefined.backend.implementer");
        }
        return BACKEND_IMPLEMENTER;
    }
    private volatile String PLATFORM_IMPLEMENTER;
    private final Object PLATFORM_IMPLEMENTER_LOCK = new Object();
    public String getPlatformImplementer() {
        if (PLATFORM_IMPLEMENTER == null) synchronized (PLATFORM_IMPLEMENTER_LOCK) {
            if (PLATFORM_IMPLEMENTER == null) PLATFORM_IMPLEMENTER = Environment.properties.getProperty("unrefined.platform.implementer");
        }
        return PLATFORM_IMPLEMENTER;
    }
    private volatile String SYSTEM_IMPLEMENTER;
    private final Object SYSTEM_IMPLEMENTER_LOCK = new Object();
    public String getSystemImplementer() {
        if (SYSTEM_IMPLEMENTER == null) synchronized (SYSTEM_IMPLEMENTER_LOCK) {
            if (SYSTEM_IMPLEMENTER == null) SYSTEM_IMPLEMENTER = Environment.properties.getProperty("unrefined.os.implementer");
        }
        return SYSTEM_IMPLEMENTER;
    }
    private volatile String RUNTIME_IMPLEMENTER;
    private final Object RUNTIME_IMPLEMENTER_LOCK = new Object();
    public String getRuntimeImplementer() {
        if (RUNTIME_IMPLEMENTER == null) synchronized (RUNTIME_IMPLEMENTER_LOCK) {
            if (RUNTIME_IMPLEMENTER == null) RUNTIME_IMPLEMENTER = Environment.properties.getProperty("unrefined.runtime.implementer");
        }
        return RUNTIME_IMPLEMENTER;
    }
    private volatile String BACKEND_NAME;
    private final Object BACKEND_NAME_LOCK = new Object();
    /**
     * Gets the name of Unrefined backend. e.g. <code>Unrefined For Java Standard Edition</code>
     * @return the name of Unrefined backend
     */
    public String getBackendName() {
        if (BACKEND_NAME == null) synchronized (BACKEND_NAME_LOCK) {
            if (BACKEND_NAME == null) BACKEND_NAME = Environment.properties.getProperty("unrefined.backend.name");
        }
        return BACKEND_NAME;
    }
    private volatile String PLATFORM_NAME;
    private final Object PLATFORM_NAME_LOCK = new Object();
    /**
     * Gets the name of underlying platform. e.g. <code>Android</code>
     * @return the name of underlying platform
     */
    public String getPlatformName() {
        if (PLATFORM_NAME == null) synchronized (PLATFORM_NAME_LOCK) {
            if (PLATFORM_NAME == null) PLATFORM_NAME = Environment.properties.getProperty("unrefined.platform.name");
        }
        return PLATFORM_NAME;
    }
    private volatile String SYSTEM_NAME;
    private final Object SYSTEM_NAME_LOCK = new Object();
    /**
     * Gets the name of running operating system. e.g. <code>Linux</code>
     * @return the name of running operating system
     */
    public String getSystemName() {
        if (SYSTEM_NAME == null) synchronized (SYSTEM_NAME_LOCK) {
            if (SYSTEM_NAME == null) SYSTEM_NAME = Environment.properties.getProperty("unrefined.os.name");
        }
        return SYSTEM_NAME;
    }
    private volatile String RUNTIME_NAME;
    private final Object RUNTIME_NAME_LOCK = new Object();
    /**
     * Gets the name of application runtime. e.g. <code>OpenJDK Runtime Environment</code>
     * @return the name of application runtime
     */
    public String getRuntimeName() {
        if (RUNTIME_NAME == null) synchronized (RUNTIME_NAME_LOCK) {
            if (RUNTIME_NAME == null) RUNTIME_NAME = Environment.properties.getProperty("unrefined.runtime.name");
        }
        return RUNTIME_NAME;
    }
    private volatile String BACKEND_VERSION_NAME;
    private final Object BACKEND_VERSION_NAME_LOCK = new Object();
    /**
     * Gets the version name of Unrefined backend. e.g. <code>0.4.0</code>
     * @return the version name of Unrefined backend
     */
    public String getBackendVersionName() {
        if (BACKEND_VERSION_NAME == null) synchronized (BACKEND_VERSION_NAME_LOCK) {
            if (BACKEND_VERSION_NAME == null) BACKEND_VERSION_NAME = Environment.properties.getProperty("unrefined.backend.version.name");
        }
        return BACKEND_VERSION_NAME;
    }
    private volatile String PLATFORM_VERSION_NAME;
    private final Object PLATFORM_VERSION_NAME_LOCK = new Object();
    public String getPlatformVersionName() {
        if (PLATFORM_VERSION_NAME == null) synchronized (PLATFORM_VERSION_NAME_LOCK) {
            if (PLATFORM_VERSION_NAME == null) PLATFORM_VERSION_NAME = Environment.properties.getProperty("unrefined.platform.version.name");
        }
        return PLATFORM_VERSION_NAME;
    }
    private volatile String SYSTEM_VERSION_NAME;
    private final Object SYSTEM_VERSION_NAME_LOCK = new Object();
    public String getSystemVersionName() {
        if (SYSTEM_VERSION_NAME == null) synchronized (SYSTEM_VERSION_NAME_LOCK) {
            if (SYSTEM_VERSION_NAME == null) SYSTEM_VERSION_NAME = Environment.properties.getProperty("unrefined.os.version.name");
        }
        return SYSTEM_VERSION_NAME;
    }
    private volatile String RUNTIME_VERSION_NAME;
    private final Object RUNTIME_VERSION_NAME_LOCK = new Object();
    public String getRuntimeVersionName() {
        if (RUNTIME_VERSION_NAME == null) synchronized (RUNTIME_VERSION_NAME_LOCK) {
            if (RUNTIME_VERSION_NAME == null) RUNTIME_VERSION_NAME = Environment.properties.getProperty("unrefined.runtime.version.name");
        }
        return RUNTIME_VERSION_NAME;
    }
    private volatile String BACKEND_VERSION_CODE;
    private final Object BACKEND_VERSION_CODE_LOCK = new Object();
    public String getBackendVersionCode() {
        if (BACKEND_VERSION_CODE == null) synchronized (BACKEND_VERSION_CODE_LOCK) {
            if (BACKEND_VERSION_CODE == null) BACKEND_VERSION_CODE = Environment.properties.getProperty("unrefined.backend.version.code");
        }
        return BACKEND_VERSION_CODE;
    }
    private volatile String PLATFORM_VERSION_CODE;
    private final Object PLATFORM_VERSION_CODE_LOCK = new Object();
    public String getPlatformVersionCode() {
        if (PLATFORM_VERSION_CODE == null) synchronized (PLATFORM_VERSION_CODE_LOCK) {
            if (PLATFORM_VERSION_CODE == null) PLATFORM_VERSION_CODE = Environment.properties.getProperty("unrefined.platform.version.code");
        }
        return PLATFORM_VERSION_CODE;
    }
    private volatile String SYSTEM_VERSION_CODE;
    private final Object SYSTEM_VERSION_CODE_LOCK = new Object();
    public String getSystemVersionCode() {
        if (SYSTEM_VERSION_CODE == null) synchronized (SYSTEM_VERSION_CODE_LOCK) {
            if (SYSTEM_VERSION_CODE == null) SYSTEM_VERSION_CODE = Environment.properties.getProperty("unrefined.os.version.code");
        }
        return SYSTEM_VERSION_CODE;
    }
    private volatile String RUNTIME_VERSION_CODE;
    private final Object RUNTIME_VERSION_CODE_LOCK = new Object();
    public String getRuntimeVersionCode() {
        if (RUNTIME_VERSION_CODE == null) synchronized (RUNTIME_VERSION_CODE_LOCK) {
            if (RUNTIME_VERSION_CODE == null) RUNTIME_VERSION_CODE = Environment.properties.getProperty("unrefined.runtime.version.code");
        }
        return RUNTIME_VERSION_CODE;
    }
    private volatile String ARCHITECTURE;
    private final Object ARCHITECTURE_LOCK = new Object();
    public String getArchitecture() {
        if (ARCHITECTURE == null) synchronized (ARCHITECTURE_LOCK) {
            if (ARCHITECTURE == null) ARCHITECTURE = Environment.properties.getProperty("unrefined.os.arch.name");
        }
        return ARCHITECTURE;
    }
    private volatile Integer INT_MODEL_BITS;
    private final Object INT_MODEL_BITS_LOCK = new Object();
    public int getIntModelBits() {
        if (INT_MODEL_BITS == null) synchronized (INT_MODEL_BITS_LOCK) {
            if (INT_MODEL_BITS == null) INT_MODEL_BITS = Environment.properties.parseIntProperty("unrefined.os.arch.bitmode.int");
        }
        return INT_MODEL_BITS;
    }
    private volatile Integer LONG_MODEL_BITS;
    private final Object LONG_MODEL_BITS_LOCK = new Object();
    public int getLongModelBits() {
        if (LONG_MODEL_BITS == null) synchronized (LONG_MODEL_BITS_LOCK) {
            if (LONG_MODEL_BITS == null) LONG_MODEL_BITS = Environment.properties.parseIntProperty("unrefined.os.arch.bitmode.long");
        }
        return LONG_MODEL_BITS;
    }
    private volatile Integer ADDRESS_MODEL_BITS;
    private final Object ADDRESS_MODEL_BITS_LOCK = new Object();
    public int getAddressModelBits() {
        if (ADDRESS_MODEL_BITS == null) synchronized (ADDRESS_MODEL_BITS_LOCK) {
            if (ADDRESS_MODEL_BITS == null) ADDRESS_MODEL_BITS = Environment.properties.parseIntProperty("unrefined.os.arch.bitmode.address");
        }
        return ADDRESS_MODEL_BITS;
    }
    private volatile String BINARY_INTERFACE;
    private final Object BINARY_INTERFACE_LOCK = new Object();
    public String getBinaryInterface() {
        if (BINARY_INTERFACE == null) synchronized (BINARY_INTERFACE_LOCK) {
            if (BINARY_INTERFACE == null) BINARY_INTERFACE = Environment.properties.getProperty("unrefined.os.arch.abi");
        }
        return BINARY_INTERFACE;
    }
    private volatile ByteOrder ENDIANNESS;
    private final Object ENDIANNESS_LOCK = new Object();
    public ByteOrder getEndianness() {
        if (ENDIANNESS == null) synchronized (ENDIANNESS_LOCK) {
            if (ENDIANNESS == null) ENDIANNESS =
                    Environment.properties.getProperty("unrefined.os.endian").equalsIgnoreCase("big") ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        }
        return ENDIANNESS;
    }
    private volatile Boolean HEADLESS;
    private final Object HEADLESS_LOCK = new Object();
    public boolean isHeadless() {
        if (HEADLESS == null) synchronized (HEADLESS_LOCK) {
            if (HEADLESS == null) HEADLESS = Environment.properties.parseBooleanProperty("unrefined.environment.headless");
        }
        return HEADLESS;
    }
    private volatile String LINE_SEPARATOR;
    private final Object LINE_SEPARATOR_LOCK = new Object();
    public String getLineSeparator() {
        if (LINE_SEPARATOR == null) synchronized (LINE_SEPARATOR_LOCK) {
            if (LINE_SEPARATOR == null) LINE_SEPARATOR = Environment.properties.getProperty("unrefined.os.separator.line");
        }
        return LINE_SEPARATOR;
    }
    private volatile String FILE_SEPARATOR;
    private final Object FILE_SEPARATOR_LOCK = new Object();
    public String getFileSeparator() {
        if (FILE_SEPARATOR == null) synchronized (FILE_SEPARATOR_LOCK) {
            if (FILE_SEPARATOR == null) FILE_SEPARATOR = Environment.properties.getProperty("unrefined.os.separator.file");
        }
        return FILE_SEPARATOR;
    }
    private volatile String PATH_SEPARATOR;
    private final Object PATH_SEPARATOR_LOCK = new Object();
    public String getPathSeparator() {
        if (PATH_SEPARATOR == null) synchronized (PATH_SEPARATOR_LOCK) {
            if (PATH_SEPARATOR == null) PATH_SEPARATOR = Environment.properties.getProperty("unrefined.os.separator.path");
        }
        return PATH_SEPARATOR;
    }
    private volatile File TEMP_DIRECTORY;
    private final Object TEMP_DIRECTORY_LOCK = new Object();
    public File getTempDirectory() {
        if (TEMP_DIRECTORY == null) synchronized (TEMP_DIRECTORY_LOCK) {
            TEMP_DIRECTORY = new File(Environment.properties.getProperty("unrefined.os.directory.tmp"));
        }
        return TEMP_DIRECTORY;
    }
    private volatile File HOME_DIRECTORY;
    private final Object HOME_DIRECTORY_LOCK = new Object();
    public File getHomeDirectory() {
        if (HOME_DIRECTORY == null) synchronized (HOME_DIRECTORY_LOCK) {
            HOME_DIRECTORY = new File(Environment.properties.getProperty("unrefined.os.directory.home"));
        }
        return HOME_DIRECTORY;
    }
    private volatile File CONFIG_DIRECTORY;
    private final Object CONFIG_DIRECTORY_LOCK = new Object();
    public File getConfigDirectory() {
        if (CONFIG_DIRECTORY == null) synchronized (CONFIG_DIRECTORY_LOCK) {
            if (CONFIG_DIRECTORY == null) CONFIG_DIRECTORY = new File(Environment.properties.getProperty("unrefined.os.directory.config"));
        }
        return CONFIG_DIRECTORY;
    }
    private volatile File DATA_DIRECTORY;
    private final Object DATA_DIRECTORY_LOCK = new Object();
    public File getDataDirectory() {
        if (DATA_DIRECTORY == null) synchronized (DATA_DIRECTORY_LOCK) {
            if (DATA_DIRECTORY == null) DATA_DIRECTORY = new File(Environment.properties.getProperty("unrefined.os.directory.data"));
        }
        return DATA_DIRECTORY;
    }
    private volatile File CACHE_DIRECTORY;
    private final Object CACHE_DIRECTORY_LOCK = new Object();
    public File getCacheDirectory() {
        if (CACHE_DIRECTORY == null) synchronized (CACHE_DIRECTORY_LOCK) {
            if (CACHE_DIRECTORY == null) CACHE_DIRECTORY = new File(Environment.properties.getProperty("unrefined.os.directory.cache"));
        }
        return CACHE_DIRECTORY;
    }
    private volatile File NULL_FILE;
    private final Object NULL_FILE_LOCK = new Object();
    public File getNullFile() {
        if (NULL_FILE == null) synchronized (NULL_FILE_LOCK) {
            if (NULL_FILE == null) NULL_FILE = new File(Environment.properties.getProperty("unrefined.os.file.null"));
        }
        return NULL_FILE;
    }

}
