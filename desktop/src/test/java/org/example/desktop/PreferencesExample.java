package org.example.desktop;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.app.Preferences;
import unrefined.app.Runtime;

public class PreferencesExample {

    public static void main(String[] args) {
        Lifecycle.onMain(args);

        Runtime runtime = Runtime.getInstance();

        Log log = Log.defaultInstance();

        Preferences profile = runtime.getPreferences("Profile");
        profile.edit().putInt("Int", 1).putLong("Long", 1).commit();

        log.info("Unrefined Preferences", "Profile[Int] == 1 ? " + (profile.getInt("Int", -1) == 1));
        log.info("Unrefined Preferences", "Profile[Long] == 1 ? " + (profile.getLong("Long", -1) == 1));

        Preferences test = runtime.getPreferences("Test");
        test.edit().putBoolean("Boolean", true).putChar("Char", 'C').commit();

        log.info("Unrefined Preferences", "Test[Boolean] == true ? " + test.getBoolean("Boolean", false));
        log.info("Unrefined Preferences", "Test[Char] == C ? " + (test.getChar("Char", '\0') == 'C'));
    }

}
