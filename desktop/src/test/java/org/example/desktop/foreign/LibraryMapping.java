package org.example.desktop.foreign;

import unrefined.app.Logger;
import unrefined.nio.Pointer;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.UnexpectedError;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Library;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * "Library Mapping": Dynamic proxy objects, working just like JNA.
 * Note that type mapping as the same of "Handle Mapping", which is different from JNA.
 */
public class LibraryMapping {

    public interface CLibrary extends Library {
        @Marshal("int") long printf(@Marshal("size_t") long format, Object... args); // Varargs supported!
    }

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);              // Initialize the Unrefined runtime environment
        Foreign foreign = Foreign.getInstance(); // Get the platform-dependent FFI factory

        Random random = ThreadLocalRandom.current();

        int a = random.nextInt();
        int b = random.nextInt();

        CLibrary c = foreign.downcallProxy(CLibrary.class);
        try (Pointer format = Pointer.allocateDirect("SUM (%d, %d) = %d")) {
            Logger.defaultInstance().info("Unrefined FFI", "characters: " +
                    c.printf(format.address(), a, b, a + b));
        }
        catch (IOException e) {
            throw new UnexpectedError(e);
        }
    }

}
