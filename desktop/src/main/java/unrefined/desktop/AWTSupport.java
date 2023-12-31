package unrefined.desktop;

import unrefined.internal.OperatingSystem;
import unrefined.internal.X11.X11AWTSupport;
import unrefined.internal.macos.MacAWTSupport;
import unrefined.internal.windows.WindowsAWTSupport;
import unrefined.media.graphics.Brush;
import unrefined.media.graphics.RectangleF;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.MultipleGradientPaint;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

public final class AWTSupport {

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

    private AWTSupport() {
        throw new NotInstantiableError(AWTSupport.class);
    }

    public static final Color TRANSPARENT = new Color(0x00000000, true);

    private static final Field numCoordsField;
    static {
        try {
            numCoordsField = Path2D.class.getDeclaredField("numCoords");
        } catch (NoSuchFieldException e) {
            throw new UnexpectedError(e);
        }
    }

    public static int getNumCoords(Path2D path2D) {
        return ReflectionSupport.getIntField(path2D, numCoordsField);
    }

    private static final Method trimToSizeMethod;
    static {
        Method method;
        try {
            method = Path2D.class.getDeclaredMethod("trimToSize");
        } catch (NoSuchMethodException e) {
            method = null;
        }
        trimToSizeMethod = method;
    }

    public static void reset(Path2D path2D) {
        path2D.reset();
        if (trimToSizeMethod != null) {
            try {
                trimToSizeMethod.invoke(path2D);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new UnexpectedError(e);
            }
        }
        path2D.setWindingRule(Path2D.WIND_NON_ZERO);
    }

    private static final Field propertiesField;
    static {
        try {
            propertiesField = BufferedImage.class.getDeclaredField("properties");
        } catch (NoSuchFieldException e) {
            throw new UnexpectedError(e);
        }
    }

    public static Hashtable<?, ?> getProperties(BufferedImage bufferedImage) {
        return (Hashtable<?, ?>) ReflectionSupport.getObjectField(bufferedImage, propertiesField);
    }

    public static Color[] toColors(int... colors) {
        if (colors == null) return null;
        Color[] result = new Color[colors.length];
        for (int i = 0; i < colors.length; i ++) {
            result[i] = new Color(colors[i], true);
        }
        return result;
    }

    public static Color[] toColors(int[] colors, int offset, int length) {
        if (colors == null) return null;
        Color[] result = new Color[length];
        for (int i = 0; i < length; i ++) {
            result[i] = new Color(colors[offset + i], true);
        }
        return result;
    }

    public static MultipleGradientPaint.CycleMethod toCycleMethod(int tileMode) {
        switch (tileMode) {
            case Brush.TileMode.CLAMP: return MultipleGradientPaint.CycleMethod.NO_CYCLE;
            case Brush.TileMode.MIRROR: return MultipleGradientPaint.CycleMethod.REFLECT;
            case Brush.TileMode.REPEAT: return MultipleGradientPaint.CycleMethod.REPEAT;
            default: throw new IllegalArgumentException("Illegal tile mode: " + tileMode);
        }
    }

    public static void floatRectangle(Rectangle2D rectangle2D, RectangleF rectangle) {
        if (rectangle2D instanceof Rectangle2D.Float) {
            Rectangle2D.Float rectangle2F = (Rectangle2D.Float) rectangle2D;
            rectangle.setRectangle(rectangle2F.x, rectangle2F.y, rectangle2F.width, rectangle2F.height);
        }
        else rectangle.setRectangle((float) rectangle2D.getX(), (float) rectangle2D.getY(),
                (float) rectangle2D.getWidth(), (float) rectangle2D.getHeight());
    }

    public static AffineTransform getDefaultTransform(Graphics2D graphics2D) {
        if (graphics2D == null) return new AffineTransform();
        else {
            GraphicsConfiguration configuration = graphics2D.getDeviceConfiguration();
            return configuration == null ? new AffineTransform() : configuration.getDefaultTransform();
        }
    }

    private static final Method getComponentAccessorMethod;
    private static final Method getPeerMethod;
    static {
        Method method;
        try {
            method = Class.forName("sun.awt.AWTAccessor").getDeclaredMethod("getComponentAccessor");
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            method = null;
        }
        getComponentAccessorMethod = method;
        try {
            method = Class.forName("sun.awt.AWTAccessor$ComponentAccessor").getDeclaredMethod("getPeer", Component.class);
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            method = null;
        }
        getPeerMethod = method;
    }

    public static Object getPeer(Component component) {
        if (component == null) return null;
        else {
            try {
                return ReflectionSupport.invokeObjectMethod(
                        ReflectionSupport.invokeObjectMethod(null, getComponentAccessorMethod),
                        getPeerMethod, component);
            }
            catch (InvocationTargetException e) {
                return null;
            }
        }
    }

    private static final Method setDesktopPropertyMethod;
    static {
        Method method;
        try {
            method = Toolkit.class.getDeclaredMethod("setDesktopProperty", String.class, Object.class);
        }
        catch (NoSuchMethodException e) {
            method = null;
        }
        setDesktopPropertyMethod = method;
    }

    public static boolean setDesktopProperty(String name, Object newValue) {
        if (setDesktopPropertyMethod == null) return false;
        else {
            try {
                ReflectionSupport.invokeVoidMethod(Toolkit.getDefaultToolkit(), setDesktopPropertyMethod, name, newValue);
                return true;
            } catch (InvocationTargetException e) {
                return false;
            }
        }
    }

    private static final Method awtLock;
    private static final Method awtUnlock;
    static {
        Class<?> clazz;
        try {
            clazz = Class.forName("sun.awt.SunToolkit");
        } catch (ClassNotFoundException e) {
            clazz = null;
        }
        if (clazz == null) {
            awtLock = null;
            awtUnlock = null;
        }
        else {
            Method method;
            try {
                method = clazz.getDeclaredMethod("awtLock");
            } catch (NoSuchMethodException e) {
                method = null;
            }
            awtLock = method;
            try {
                method = clazz.getDeclaredMethod("awtUnlock");
            } catch (NoSuchMethodException e) {
                method = null;
            }
            awtUnlock = method;
        }
    }

    public static boolean awtLock() {
        try {
            awtLock.invoke(null);
            return true;
        } catch (InvocationTargetException | IllegalAccessException e) {
            return false;
        }
    }

    public static boolean awtUnlock() {
        try {
            awtUnlock.invoke(null);
            return true;
        } catch (InvocationTargetException | IllegalAccessException e) {
            return false;
        }
    }

    static {
        System.setProperty("sun.java2d.uiScale.enabled", "false");
    }

    public static void patch() {
        if (OperatingSystem.IS_WINDOWS) WindowsAWTSupport.patch();
        else if (OperatingSystem.IS_MAC) MacAWTSupport.patch();
        else X11AWTSupport.patch();
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
