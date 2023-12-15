package unrefined.internal;

import unrefined.desktop.ReflectionSupport;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class GraphicsUtils {

    private GraphicsUtils() {
        throw new NotInstantiableError(GraphicsUtils.class);
    }

    private static final Method setBackgroundColorMethod;
    static {
        Method method;
        try {
            method = Window.class.getDeclaredMethod("setBackground", Color.class);
        } catch (NoSuchMethodException e) {
            method = null;
        }
        setBackgroundColorMethod = method;
    }

    public static void setBackgroundColor(Window window, Color color) {
        Color bg = window.getBackground();
        if (bg != null && bg.equals(color)) return;
        int bgAlpha = bg != null ? bg.getAlpha() : 255;
        int alpha = color != null ? color.getAlpha() : 255;
        if (bgAlpha == 255 && alpha < 255) {
            GraphicsConfiguration gc = window.getGraphicsConfiguration();
            GraphicsDevice gd = gc.getDevice();
            if (gd.getFullScreenWindow() == window) color = new Color(color.getRGB(), false);
            else if (!gc.isTranslucencyCapable() && getTranslucencyCapableGC(gd) == null)
                color = new Color(color.getRGB(), false);
        }
        if ((window instanceof Frame || window instanceof Dialog)) {
            if (setBackgroundColorMethod == null) color = color == null ? null : new Color(color.getRGB(), false);
            else {
                synchronized (window.getTreeLock()) {
                    try {
                        ReflectionSupport.invokeNonVirtualVoidMethod(window, setBackgroundColorMethod, color);
                        return;
                    } catch (InvocationTargetException e) {
                        throw new UnexpectedError(e);
                    }
                }
            }
        }
        window.setBackground(color);
    }

    public static GraphicsConfiguration getTranslucencyCapableGC(GraphicsDevice gd) {
        // If the default GC supports translucency return true.
        // It is important to optimize the verification this way,
        // see CR 6661196 for more details.
        GraphicsConfiguration defaultGC = gd.getDefaultConfiguration();
        if (defaultGC.isTranslucencyCapable()) return defaultGC;

        // ... otherwise iterate through all the GCs.
        GraphicsConfiguration[] configs = gd.getConfigurations();
        for (GraphicsConfiguration config : configs) {
            if (config.isTranslucencyCapable()) return config;
        }

        return null;
    }

}
