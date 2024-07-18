package org.example.desktop;

import unrefined.Lifecycle;

/**
 * First, you need to initialize the Unrefined runtime environment for your application.
 */
public class InitializeEnvironment {

    public static void main(String[] args) {
        Lifecycle.onMain(args); // Initialize the Unrefined runtime environment
                                // Make sure the first invocation of this method
                                // running in your application's main() method!
    }

}
