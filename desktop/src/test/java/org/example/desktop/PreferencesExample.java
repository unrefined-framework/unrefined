package org.example.desktop;

import unrefined.app.Logger;
import unrefined.app.Preferences;
import unrefined.app.Runtime;
import unrefined.runtime.DesktopRuntime;

public class PreferencesExample {

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);

        Runtime runtime = Runtime.getInstance();

        Logger logger = Logger.defaultInstance();

        Preferences profile = runtime.getPreferences("Profile");
        profile.edit().putInt("Int", 1).putLong("Long", 1).commit();

        logger.info("Unrefined Preferences", "Profile[Int] == 1 ? " + (profile.getInt("Int", -1) == 1));
        logger.info("Unrefined Preferences", "Profile[Long] == 1 ? " + (profile.getLong("Long", -1) == 1));

        Preferences test = runtime.getPreferences("Test");
        test.edit().putBoolean("Boolean", true).putChar("Char", 'C').commit();

        logger.info("Unrefined Preferences", "Test[Boolean] == true ? " + test.getBoolean("Boolean", false));
        logger.info("Unrefined Preferences", "Test[Char] == C ? " + (test.getChar("Char", '\0') == 'C'));
    }

}
