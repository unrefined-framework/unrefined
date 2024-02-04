package unrefined.internal.macos;

import unrefined.desktop.ReflectionSupport;
import unrefined.desktop.OSInfo;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.awt.Font;
import java.awt.GraphicsDevice;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MacFontSupport {

    private MacFontSupport() {
        throw new NotInstantiableError(MacFontSupport.class);
    }

    public static final Font SYSTEM_FONT;
    public static final float FONT_SCALE = 13f / 12f;
    static {
        if (OSInfo.IS_MAC) {
            String fontName;
            if (OSInfo.checkOSVersion(10, 15, 0, 0)) fontName = "Helvetica Neue";
            else if (OSInfo.checkOSVersion(10, 11, 0, 0)) fontName = ".SF NS Text";
            else fontName = "Lucida Grande";
            SYSTEM_FONT = new Font(fontName, Font.PLAIN, 13);
        }
        else SYSTEM_FONT = null;
    }

    private static final Method getScaleFactorMethod;
    static {
        if (OSInfo.IS_MAC) {
            Method method;
            try {
                method = Class.forName("sun.awt.CGraphicsDevice").getDeclaredMethod("getScaleFactor");
            }
            catch (NoSuchMethodException | ClassNotFoundException e) {
                method = null;
            }
            getScaleFactorMethod = method;
        }
        else getScaleFactorMethod = null;
    }

    public static int getScaleFactor(GraphicsDevice graphicsDevice) {
        try {
            return ReflectionSupport.invokeIntMethod(graphicsDevice, getScaleFactorMethod);
        } catch (InvocationTargetException e) {
            throw new UnexpectedError(e);
        }
    }

}
