package unrefined.desktop;

import unrefined.desktop.X11.X11AWTSupport;
import unrefined.desktop.macos.MacAWTSupport;
import unrefined.desktop.windows.WindowsAWTSupport;
import unrefined.media.graphics.Brush;
import unrefined.media.graphics.Graphics;
import unrefined.media.graphics.RectangleF;
import unrefined.media.graphics.Text;
import unrefined.runtime.DesktopBitmap;
import unrefined.runtime.DesktopBrush;
import unrefined.runtime.DesktopFont;
import unrefined.runtime.DesktopGraphics;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.font.GraphicAttribute;
import java.awt.font.ImageGraphicAttribute;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

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

    public static Rectangle2D.Float floatRectangle(Rectangle2D rectangle2D) {
        if (rectangle2D instanceof Rectangle2D.Float) return (Rectangle2D.Float) rectangle2D;
        else return new Rectangle2D.Float((float) rectangle2D.getX(), (float) rectangle2D.getY(),
                (float) rectangle2D.getWidth(), (float) rectangle2D.getHeight());
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

    public static void awtLock() {
        try {
            ReflectionSupport.invokeVoidMethod(null, awtLock);
        } catch (InvocationTargetException e) {
            throw new UnexpectedError(e);
        }
    }

    public static void awtUnlock() {
        try {
            ReflectionSupport.invokeVoidMethod(null, awtUnlock);
        } catch (InvocationTargetException e) {
            throw new UnexpectedError(e);
        }
    }

    static {
        System.setProperty("sun.java2d.uiScale.enabled", "false");
    }

    public static void patch() {
        if (OSInfo.IS_WINDOWS) WindowsAWTSupport.patch();
        else if (OSInfo.IS_MAC) MacAWTSupport.patch();
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

    private static final Field pushPopLockField;
    private static final Field nextQueueField;
    private static final Field dispatchThreadField;
    static {
        try {
            pushPopLockField = EventQueue.class.getDeclaredField("pushPopLock");
            nextQueueField = EventQueue.class.getDeclaredField("nextQueue");
            dispatchThreadField = EventQueue.class.getDeclaredField("dispatchThread");
        }
        catch (NoSuchFieldException e) {
            throw new UnexpectedError(e);
        }
    }

    public static boolean isDispatchThread(Thread thread) {
        EventQueue eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
        Lock pushPopLock = (Lock) ReflectionSupport.getObjectField(eq, pushPopLockField);
        pushPopLock.lock();
        try {
            EventQueue next = (EventQueue) ReflectionSupport.getObjectField(eq, nextQueueField);
            while (next != null) {
                eq = next;
                next = (EventQueue) ReflectionSupport.getObjectField(eq, nextQueueField);
            }
            //if (eq.fwDispatcher != null) {
            //    return eq.fwDispatcher.isDispatchThread();
            //}
            return (thread == ReflectionSupport.getObjectField(eq, dispatchThreadField));
        } finally {
            pushPopLock.unlock();
        }
    }

    public static Shape intersectByArea(Shape s1, Shape s2, boolean keep1, boolean keep2) {
        Area a1, a2;

        // First see if we can find an overwritable source shape
        // to use as our destination area to avoid duplication.
        if (!keep1 && (s1 instanceof Area)) {
            a1 = (Area) s1;
        } else if (!keep2 && (s2 instanceof Area)) {
            a1 = (Area) s2;
            s2 = s1;
        } else {
            a1 = new Area(s1);
        }

        if (s2 instanceof Area) {
            a2 = (Area) s2;
        } else {
            a2 = new Area(s2);
        }

        a1.intersect(a2);
        if (a1.isRectangular()) {
            return a1.getBounds();
        }

        return a1;
    }

    public static Shape intersectShapes(Shape s1, Shape s2, boolean keep1, boolean keep2) {
        if (s1 instanceof Rectangle && s2 instanceof Rectangle) {
            return ((Rectangle) s1).intersection((Rectangle) s2);
        }
        if (s1 instanceof Rectangle2D) {
            return intersectRectShape((Rectangle2D) s1, s2, keep1, keep2);
        } else if (s2 instanceof Rectangle2D) {
            return intersectRectShape((Rectangle2D) s2, s1, keep2, keep1);
        }
        return intersectByArea(s1, s2, keep1, keep2);
    }

    public static Shape intersectRectShape(Rectangle2D r, Shape s,
                                           boolean keep1, boolean keep2) {
        if (s instanceof Rectangle2D) {
            Rectangle2D r2 = (Rectangle2D) s;
            Rectangle2D outrect;
            if (!keep1) {
                outrect = r;
            } else if (!keep2) {
                outrect = r2;
            } else {
                outrect = new Rectangle2D.Float();
            }
            double x1 = Math.max(r.getX(), r2.getX());
            double x2 = Math.min(r.getX()  + r.getWidth(),
                    r2.getX() + r2.getWidth());
            double y1 = Math.max(r.getY(), r2.getY());
            double y2 = Math.min(r.getY()  + r.getHeight(),
                    r2.getY() + r2.getHeight());

            if (((x2 - x1) < 0) || ((y2 - y1) < 0))
                // Width or height is negative. No intersection.
                outrect.setFrameFromDiagonal(0, 0, 0, 0);
            else
                outrect.setFrameFromDiagonal(x1, y1, x2, y2);
            return outrect;
        }
        if (r.contains(s.getBounds2D())) {
            if (keep2) {
                s = new GeneralPath(s);
            }
            return s;
        }
        return intersectByArea(r, s, keep1, keep2);
    }

    public static AttributedCharSequence toAttributedCharSequence(Text text, DesktopGraphics graphics) {
        return toAttributedCharSequence(text, 0, text.length(), graphics);
    }

    public static AttributedCharSequence toAttributedCharSequence(Text text, int start, int end, DesktopGraphics graphics) {
        return toAttributedCharSequence(text, start, end, graphics, null, null);
    }

    public static AttributedCharSequence toAttributedCharSequence(Text text, int start, int end, DesktopGraphics graphics, List<Object[]> backgroundList, List<Object[]> foregroundList) {

        Graphics2D graphics2D = graphics.getGraphics2D();

        if (!text.spanMarks().isEmpty()) {

            Graphics.Info info = graphics.getInfo();

            AttributedCharSequence sequence = new AttributedCharSequence(text.subSequence(start, end), graphics2D.getFont().getAttributes());
            if (info.getStyle() == Graphics.Style.FILL || info.getTextBackground() != null)
                sequence.addAttribute(TextAttribute.BACKGROUND, ((DesktopBrush) info.getTextBackground()).getPaint());

            for (Text.SpanMark mark : text.spanMarks()) {
                Float scaleX, scaleY, skewX, skewY;
                scaleX = scaleY = skewX = skewY = null;
                BufferedImage replacement = null;
                int baseline = 0;
                for (Map.Entry<Text.Attribute, Object> entry : mark.getAttributes().entrySet()) {
                    Object value = entry.getValue();
                    if (value == null) continue;
                    Text.Attribute attribute = entry.getKey();
                    switch (attribute) {
                        case BRUSH:
                            sequence.addAttribute(TextAttribute.FOREGROUND, ((DesktopBrush) value).getPaint(), mark.getStart(), mark.getEnd());
                            break;
                        case FONT:
                            sequence.addAttribute(TextAttribute.FONT, ((DesktopFont) value).getFont(), mark.getStart(), mark.getEnd());
                            break;
                        case BACKGROUND:
                            if (info.getStyle() == Graphics.Style.FILL) sequence.addAttribute(TextAttribute.BACKGROUND, ((DesktopBrush) value).getPaint(), mark.getStart(), mark.getEnd());
                            else if (backgroundList != null) backgroundList.add(new Object[] { ((DesktopBrush) value).getPaint(), mark.getStart(), mark.getEnd() });
                            break;
                        case FOREGROUND:
                            if (foregroundList != null) foregroundList.add(new Object[] { ((DesktopBrush) value).getPaint(), mark.getStart(), mark.getEnd() });
                            break;
                        case SIZE:
                            sequence.addAttribute(TextAttribute.SIZE, value, mark.getStart(), mark.getEnd());
                            break;
                        case DIRECTION:
                            sequence.addAttribute(TextAttribute.RUN_DIRECTION, TextHints.toRunDirection((Integer) value), mark.getStart(), mark.getEnd());
                            break;
                        case UNDERLINE:
                            sequence.addAttribute(TextAttribute.UNDERLINE, (Boolean) value ? TextAttribute.UNDERLINE_ON : TextHints.UNDERLINE_OFF, mark.getStart(), mark.getEnd());
                            break;
                        case STRIKETHROUGH:
                            sequence.addAttribute(TextAttribute.STRIKETHROUGH, (Boolean) value ? TextAttribute.STRIKETHROUGH_ON : TextHints.STRIKETHROUGH_OFF, mark.getStart(), mark.getEnd());
                            break;
                        case KERNING:
                            sequence.addAttribute(TextAttribute.KERNING, (Boolean) value ? TextAttribute.KERNING_ON : TextHints.KERNING_OFF, mark.getStart(), mark.getEnd());
                            break;
                        case VARIANT_LIGATURES:
                            sequence.addAttribute(TextAttribute.LIGATURES, (Boolean) value ? TextAttribute.LIGATURES_ON : TextHints.LIGATURES_OFF, mark.getStart(), mark.getEnd());
                            break;
                        case SUPERSCRIPT:
                            sequence.addAttribute(TextAttribute.SUPERSCRIPT, value, mark.getStart(), mark.getEnd());
                            break;
                        case LETTER_SPACING:
                            sequence.addAttribute(TextAttribute.TRACKING, value, mark.getStart(), mark.getEnd());
                            break;
                        case SCALE_X:
                            scaleX = (Float) value;
                            break;
                        case SCALE_Y:
                            scaleY = (Float) value;
                            break;
                        case SKEW_X:
                            skewX = (Float) value;
                            break;
                        case SKEW_Y:
                            skewY = (Float) value;
                            break;
                        case REPLACEMENT:
                            replacement = ((DesktopBitmap) value).getBufferedImage();
                            break;
                        case REPLACEMENT_BASELINE:
                            baseline = FontSupport.toFontBaseline((Integer) value);
                            break;
                    }
                    if (skewX != null || skewY != null || scaleX != null || scaleY != null) {
                        AffineTransform textTransform = AffineTransform.getShearInstance(skewX == null ? 0 : skewX, skewY == null ? 0 : skewY);
                        textTransform.scale(scaleX == null ? 1 : scaleX, scaleY == null ? 1 : scaleY);
                        sequence.addAttribute(TextAttribute.TRANSFORM, textTransform, mark.getStart(), mark.getEnd());
                    }
                    if (replacement != null) {
                        if (baseline == -3) {
                            baseline = GraphicAttribute.TOP_ALIGNMENT;
                            sequence.addAttribute(TextAttribute.CHAR_REPLACEMENT, new ImageGraphicAttribute(replacement, baseline, 0, replacement.getHeight() * 0.5f), mark.getStart(), mark.getEnd());
                        }
                        else sequence.addAttribute(TextAttribute.CHAR_REPLACEMENT, new ImageGraphicAttribute(replacement, baseline), mark.getStart(), mark.getEnd());
                    }
                }
            }
            return sequence;
        }
        else return new AttributedCharSequence(text.subSequence(start, end), graphics2D.getFont().getAttributes());
    }

}
