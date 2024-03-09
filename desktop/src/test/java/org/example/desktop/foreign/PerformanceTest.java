package org.example.desktop.foreign;

import unrefined.app.Logger;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Library;
import unrefined.util.foreign.Symbol;

public class PerformanceTest {

    public interface Math extends Library {
        double sin(double x);
    }

    public static final String TAG = "Unrefined FFI";

    public static void main(String[] args) {

        DesktopRuntime.initialize(args);

        Logger logger = Logger.defaultInstance();

        Foreign foreign = Foreign.getInstance();

        Math m = foreign.downcallProxy(Math.class);
        Symbol sin = foreign.downcallHandle(foreign.getSymbolAddress("sin"), double.class, double.class);

        final int count = 10000000;

        long time;

        time = System.currentTimeMillis();

        for (int i = 0; i < count; i ++) {
            m.sin(1);
        }

        time = System.currentTimeMillis() - time;

        logger.info(TAG, "Library Mapping CALL sin(1) " + count + " times: " + time + " ms");

        time = System.currentTimeMillis();

        for (int i = 0; i < count; i ++) {
            sin.invokeDouble(1);
        }

        time = System.currentTimeMillis() - time;

        logger.info(TAG, "Handle Mapping CALL sin(1) " + count + " times: " + time + " ms");
    }

}
