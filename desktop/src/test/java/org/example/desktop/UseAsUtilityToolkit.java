package org.example.desktop;

import unrefined.app.Logger;
import unrefined.nio.charset.Charsets;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.Base64;

/**
 * Also, you can use UXGL as a utility toolkit without create any context. Just like the following code.
 */
public class UseAsUtilityToolkit {

    public static void main(String[] args) {
        DesktopRuntime.setup(args);             // Initialize the UXGL runtime environment

        Logger logger = Logger.defaultInstance(); // Get the platform-dependent logger
        Base64 base64 = Base64.getInstance();     // Get the platform-dependent base64 coder

        byte[] base64Encoded = base64.encode("Hello UXGL".getBytes(Charsets.UTF_8)); // Encode to base64
        byte[] base64Decoded = base64.decode(base64Encoded);                         // Decode from base64

        // stdout: yyyy-MM-dd HH:mm:ss:SSS pid@hostname I/Hello World: Hello UXGL
        logger.info("Hello World", new String(base64Decoded, Charsets.UTF_8));
    }

}
