package org.example.desktop;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.nio.charset.Charsets;
import unrefined.util.Base64;

/**
 * Also, you can use Unrefined as a utility toolkit without create any context. Just like the following code.
 */
public class UseAsUtilityToolkit {

    public static void main(String[] args) {
        Lifecycle.onMain(args);             // Initialize the Unrefined runtime environment

        Log log = Log.defaultInstance(); // Get the platform-dependent log
        Base64 base64 = Base64.getInstance();     // Get the platform-dependent base64 coder

        byte[] base64Encoded = base64.encode("Hello Unrefined".getBytes(Charsets.UTF_8)); // Encode to base64
        byte[] base64Decoded = base64.decode(base64Encoded);                              // Decode from base64

        // stdout: yyyy-MM-dd HH:mm:ss:SSS pid@hostname I/Hello World: Hello Unrefined
        log.info("Hello World", new String(base64Decoded, Charsets.UTF_8));
    }

}
