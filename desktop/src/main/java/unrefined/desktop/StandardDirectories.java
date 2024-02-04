package unrefined.desktop;

import unrefined.util.NotInstantiableError;

import java.io.File;

public final class StandardDirectories {

    private StandardDirectories() {
        throw new NotInstantiableError(StandardDirectories.class);
    }

    public static final File TEMP_HOME = new File(System.getProperty("java.io.tmpdir"));
    public static final File USER_HOME = new File(System.getProperty("user.home"));
    public static final File CONFIG_HOME;
    public static final File DATA_HOME;
    public static final File CACHE_HOME;

    // FIXME hardcoded, not using native functions
    static {
        if (OSInfo.IS_WINDOWS) {
            // for more information, see:
            // https://learn.microsoft.com/en-us/windows/win32/shell/csidl
            // https://learn.microsoft.com/en-us/windows/win32/shell/knownfolderid
            final String APPDATA = System.getenv("APPDATA");
            final String LOCALAPPDATA = System.getenv("LOCALAPPDATA");
            if (LOCALAPPDATA == null) { // Vista or later
                DATA_HOME = new File(USER_HOME, "Local Settings\\Application Data"); // CSIDL_LOCAL_APPDATA
                CACHE_HOME = new File(USER_HOME, "Local Settings\\Temporary Internet Files"); // CSIDL_INTERNET_CACHE
            }
            else { // XP or earlier
                DATA_HOME = new File(LOCALAPPDATA); // FOLDERID_LocalAppData
                CACHE_HOME = new File(LOCALAPPDATA, "Microsoft\\Windows\\Temporary Internet Files"); // FOLDERID_InternetCache
            }
            CONFIG_HOME = new File(APPDATA); // CSIDL_APPDATA or FOLDERID_RoamingAppData
        }
        else if (OSInfo.IS_MAC) {
            // for more information, see:
            // 'The File System' section in
            // https://developer.apple.com/library/archive/documentation/General/Conceptual/MOSXAppProgrammingGuide/AppRuntime/AppRuntime.html
            CONFIG_HOME = new File(USER_HOME, "Library/Preferences");
            DATA_HOME = new File(USER_HOME, "Library/Application Support");
            CACHE_HOME = new File(USER_HOME, "Library/Caches");
        }
        else {
            // For more information, see:
            // https://www.freedesktop.org/wiki/Specifications/basedir-spec/
            final String XDG_CONFIG_HOME = System.getenv("XDG_CONFIG_HOME");
            if (XDG_CONFIG_HOME == null) CONFIG_HOME = new File(USER_HOME, ".config");
            else CONFIG_HOME = new File(XDG_CONFIG_HOME);
            final String XDG_DATA_HOME = System.getenv("XDG_DATA_HOME");
            if (XDG_DATA_HOME == null) DATA_HOME = new File(USER_HOME, ".local/share");
            else DATA_HOME = new File(XDG_DATA_HOME);
            final String XDG_CACHE_HOME = System.getenv("XDG_CACHE_HOME");
            if (XDG_CACHE_HOME == null) CACHE_HOME = new File(USER_HOME, ".cache");
            else CACHE_HOME = new File(XDG_CACHE_HOME);
        }
    }

}
