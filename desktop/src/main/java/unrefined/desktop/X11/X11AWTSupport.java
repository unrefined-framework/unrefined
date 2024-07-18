package unrefined.desktop.X11;

import unrefined.desktop.AWTSupport;
import unrefined.desktop.ReflectionSupport;
import unrefined.desktop.OSInfo;
import unrefined.util.NotInstantiableError;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class X11AWTSupport {

    private X11AWTSupport() {
        throw new NotInstantiableError(X11AWTSupport.class);
    }

    private static final Method getDisplayMethod;
    private static final Method getWindowMethod;
    static {
        if (OSInfo.IS_X11) {
            Method method;
            try {
                method = Class.forName("sun.awt.X11.XToolkit").getDeclaredMethod("getDisplay");
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                method = null;
            }
            getDisplayMethod = method;
            try {
                method = Class.forName("sun.awt.X11ComponentPeer").getDeclaredMethod("getWindow");
            }
            catch (ClassNotFoundException | NoSuchMethodException e) {
                method = null;
            }
            getWindowMethod = method;
        }
        else {
            getDisplayMethod = null;
            getWindowMethod = null;
        }
    }

    public static long getDisplay() {
        if (getDisplayMethod == null) return 0;
        else {
            try {
                return ReflectionSupport.invokeLongMethod(null, getDisplayMethod);
            } catch (InvocationTargetException e) {
                return 0;
            }
        }
    }

    public static long getWindow(Component component) {
        if (getWindowMethod == null || component == null) return 0;
        else {
            try {
                Object peer = AWTSupport.getPeer(component);
                if (peer == null) return 0;
                else return ReflectionSupport.invokeLongMethod(peer, getWindowMethod);
            }
            catch (InvocationTargetException e) {
                return 0;
            }
        }
    }

    public static void patch() {
        X11CursorSupport.patch();
    }

}
