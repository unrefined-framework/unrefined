package org.example.desktop.foreign;

import unrefined.app.Logger;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Symbol;

/**
 * Unrefined provides a set of simple and clear API ("Unrefined FFI") to easily integrate native code.
 * Let's begin with the basis, "Handle Mapping"!
 */
public class ForeignFunction {

    public static void main(String[] args) throws NoSuchMethodException {
        DesktopRuntime.initialize(args);             // Initialize the Unrefined runtime environment

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
        // Java subclasses of unrefined.foreign.Aggregate -> struct / union

        // Varargs supported for the downcall handle, but the upcall stub not

        Foreign foreign = Foreign.getInstance(); // Get the platform-dependent FFI factory
        Symbol upcallStub = foreign.upcallStub(ForeignFunction.class.getDeclaredMethod("helloFFI"),
                void.class); // Create a upcall stub, which a closure/callback will call Java method from a corresponding C function pointer
        Symbol downcallHandle = foreign.downcallHandle(upcallStub.address(), void.class); // Create a downcall handle, which calls C function from Java
        downcallHandle.invoke(); // Java -> downcall -> C -> upcall -> Java

        //upcallStub.invokeVoid();     // In fact, you can directly invoke the upcall stub without create a new downcall handle
    }

    public static void helloFFI() {
        Logger.defaultInstance().info("Unrefined FFI", "C call Java from C from Java");
    }

}
