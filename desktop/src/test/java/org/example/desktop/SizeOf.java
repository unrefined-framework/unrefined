package org.example.desktop;

import unrefined.app.Logger;
import unrefined.app.Runtime;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.reflect.Reflection;

public class SizeOf {

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);
        boolean[] booleans = new boolean[] {false, false, false, false};
        int[] ints = new int[] {0, 0, 0, 0};
        Reflection.getInstance().setArrayBooleanElement(booleans, 0, true);
        Logger.defaultInstance().info("Unrefined Runtime", "sizeof(booleans = boolean[4]) = " +
                Runtime.getInstance().sizeOf(booleans));
        Logger.defaultInstance().info("Unrefined Runtime", "sizeof(ints = int[4]) = " +
                Runtime.getInstance().sizeOf(ints));
        Logger.defaultInstance().info("Unrefined Runtime", "booleans[0] = " + booleans[0]);
        Logger.defaultInstance().info("Unrefined Runtime", "booleans[1] = " + booleans[1]);
    }

}
