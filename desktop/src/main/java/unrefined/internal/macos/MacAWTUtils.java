package unrefined.internal.macos;

import unrefined.util.NotInstantiableError;

public final class MacAWTUtils {

    private MacAWTUtils() {
        throw new NotInstantiableError(MacAWTUtils.class);
    }

    static {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.awt.application.name", System.getProperty("unrefined.app.name"));
    }

    public static void patch() {
    }

}
