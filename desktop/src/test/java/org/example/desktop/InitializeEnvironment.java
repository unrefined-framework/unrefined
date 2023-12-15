package org.example.desktop;

import unrefined.runtime.DesktopRuntime;

/**
 * First, you need to initialize the UXGL runtime environment for your application.
 */
public class InitializeEnvironment {

    public static void main(String[] args) {
        DesktopRuntime.setup(args); // Initialize the UXGL runtime environment
                                    // Make sure the first invocation of this method
                                    // running in your application's main() method!
    }

}
