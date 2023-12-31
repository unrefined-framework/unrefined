package org.example.desktop.foreign;

import unrefined.nio.Pointer;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.UnexpectedError;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Library;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * UXGL "Library Mapping"! Dynamic proxy objects, working just like JNA!
 * Note that type mapping as the same of UXGL "Handle Mapping", different from JNA.
 */
public class LibraryMapping {

    public interface CLibrary extends Library {
        void printf(long format, int... args); // Varargs supported!
    }

    public static void main(String[] args) {
        DesktopRuntime.setup(args);              // Initialize the UXGL runtime environment
        Foreign foreign = Foreign.getInstance(); // Get the platform-dependent FFI factory

        Random random = ThreadLocalRandom.current();

        int a = random.nextInt();
        int b = random.nextInt();

        CLibrary c = foreign.downcallProxy(CLibrary.class);
        try (Pointer format = Pointer.allocateDirect("SUM (%d, %d) = %d")) {
            c.printf(format.address(), a, b, a + b);
        }
        catch (IOException e) {
            throw new UnexpectedError(e);
        }
    }

}
