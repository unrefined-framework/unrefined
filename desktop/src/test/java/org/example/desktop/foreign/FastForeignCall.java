package org.example.desktop.foreign;

import unrefined.app.Logger;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.foreign.Foreign;

/**
 * With some limitations, you can dynamically call some native functions as fast as hand-writing JNI. (Okay, a bit slower than JNI indeed)
 * Works similar with JNA Direct Mapping, that's why I named it UXGL "Direct Mapping"!
 */
public class FastForeignCall {

    public static void main(String[] args) {
        DesktopRuntime.setup(args);             // Initialize the UXGL runtime environment

        Foreign foreign = Foreign.getInstance(); // Get the platform-dependent FFI factory
        foreign.register(FastForeignCall.class); // Register all the native functions in specified class

        final double x = 1;

        long time;

        time = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i ++) {
            Math.sin(x);
        }
        time = System.currentTimeMillis() - time;
        Logger.defaultInstance().info("UXGL FFI", "java.lang.Math#sin (10m call) time: " + time);
        time = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i ++) {
            sin(x);
        }
        time = System.currentTimeMillis() - time;
        Logger.defaultInstance().info("UXGL FFI", "<cmath> sin (UXGL FFI Direct Mapping, 10m call) time: " + time);
    }

    // Only Java primitive types allowed! The pointer type and varargs are unsupported!
    // Java void -> void
    // Java boolean -> uint8_t
    // Java byte -> int8_t
    // Java char -> uint16_t
    // Java short -> int16_t
    // Java int -> int32_t
    // Java long -> int64_t
    // Java float -> float
    // Java double -> double
    public static native double sin(double x);

}
