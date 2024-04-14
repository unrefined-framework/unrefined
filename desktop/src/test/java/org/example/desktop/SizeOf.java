package org.example.desktop;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.app.Runtime;
import unrefined.util.reflect.Reflection;

public class SizeOf {

    public static void main(String[] args) {
        Lifecycle.onMain(args);
        boolean[] booleans = new boolean[] {false, false, false, false};
        int[] ints = new int[] {0, 0, 0, 0};
        Reflection.getInstance().setArrayBooleanElement(booleans, 0, true);
        Log.defaultInstance().info("Unrefined Runtime", "sizeof(booleans = boolean[4]) = " +
                Runtime.getInstance().sizeOf(booleans));
        Log.defaultInstance().info("Unrefined Runtime", "sizeof(ints = int[4]) = " +
                Runtime.getInstance().sizeOf(ints));
        Log.defaultInstance().info("Unrefined Runtime", "booleans[0] = " + booleans[0]);
        Log.defaultInstance().info("Unrefined Runtime", "booleans[1] = " + booleans[1]);
    }

}
