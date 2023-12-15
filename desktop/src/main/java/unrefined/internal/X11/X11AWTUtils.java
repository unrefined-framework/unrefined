package unrefined.internal.X11;

import unrefined.desktop.ReflectionSupport;
import unrefined.internal.AWTUtils;
import unrefined.internal.SystemUtils;
import unrefined.util.NotInstantiableError;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class X11AWTUtils {

    private X11AWTUtils() {
        throw new NotInstantiableError(X11AWTUtils.class);
    }

    private static final Method getDisplayMethod;
    private static final Method getWindowMethod;
    static {
        if (SystemUtils.IS_X11) {
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
                Object peer = AWTUtils.getPeer(component);
                if (peer == null) return 0;
                else return ReflectionSupport.invokeLongMethod(peer, getWindowMethod);
            }
            catch (InvocationTargetException e) {
                return 0;
            }
        }
    }

    public static void patch() {
        X11CursorUtils.patch();
    }

}
