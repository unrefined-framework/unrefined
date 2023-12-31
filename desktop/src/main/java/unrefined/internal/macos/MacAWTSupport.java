package unrefined.internal.macos;

import unrefined.util.NotInstantiableError;

public final class MacAWTSupport {

    private MacAWTSupport() {
        throw new NotInstantiableError(MacAWTSupport.class);
    }

    static {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.awt.application.name", System.getProperty("unrefined.app.name"));
    }

    public static void patch() {
    }

}
