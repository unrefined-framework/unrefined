package org.example.desktop.foreign;

import unrefined.nio.Allocator;
import unrefined.nio.Pointer;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.UnexpectedError;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Library;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * You can also retrieve the global variable as well as get the function symbol!
 */
public class GlobalVariables {

    public interface CLibrary extends Library {
        void fprintf(long fp, long format, int... args); // Varargs supported!
    }

    public static void main(String[] args) {
        DesktopRuntime.setup(args);              // Initialize the UXGL runtime environment
        Foreign foreign = Foreign.getInstance(); // Get the platform-dependent FFI factory

        long stdout = Allocator.defaultInstance().getAddress(foreign.getSymbolAddress("stdout")); // Get stdout FILE*

        Random random = ThreadLocalRandom.current();

        int a = random.nextInt();
        int b = random.nextInt();

        CLibrary c = foreign.downcallProxy(CLibrary.class);
        try (Pointer format = Pointer.allocateDirect("SUM (%d, %d) = %d")) {
            c.fprintf(stdout, format.address(), a, b, a + b);
        }
        catch (IOException e) {
            throw new UnexpectedError(e);
        }
    }

}
