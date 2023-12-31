package org.example.desktop.foreign;

import unrefined.app.Logger;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Symbol;

/**
 * UXGL provides a set of simple and clear API ("UXGL FFI") to easily integrate native code.
 * Let's begin with the basis, UXGL "Handle Mapping"!.
 */
public class ForeignFunction {

    public static void main(String[] args) throws NoSuchMethodException {
        DesktopRuntime.setup(args);             // Initialize the UXGL runtime environment

        // Type mapping:
        // Java void -> void
        // Java boolean -> uint8_t
        // Java byte -> int8_t
        // Java char -> uint16_t
        // Java short -> int16_t
        // Java int -> int32_t
        // Java long -> int64_t
        // Java float -> float
        // Java double -> double
        // Java unrefined.nio.Pointer -> pointer
        // Note that unrefined.nio.Pointer is used just as a "marker class",
        // for pointer types, the method parameter type in Java side should be long.class

        // Varargs supported for the downcall handle, but the upcall stub not

        Foreign foreign = Foreign.getInstance(); // Get the platform-dependent FFI factory
        Symbol upcallStub = foreign.upcallStub(ForeignFunction.class.getDeclaredMethod("helloFFI"),
                void.class); // Create a upcall stub, which a closure/callback will call Java method from a corresponding C function pointer
        Symbol downcallHandle = foreign.downcallHandle(upcallStub.address(), void.class); // Create a downcall handle, which calls C function from Java
        downcallHandle.invokeVoid(); // Java -> downcall -> C -> upcall -> Java

        //upcallStub.invokeVoid();     // In fact, you can directly invoke the upcall stub without create a new downcall handle
    }

    public static void helloFFI() {
        Logger.defaultInstance().info("UXGL FFI", "C call Java from C from Java");
    }

}
