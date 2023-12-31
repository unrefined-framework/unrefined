package unrefined.internal.X11;

import com.kenai.jffi.Function;
import com.kenai.jffi.HeapInvocationBuffer;
import com.kenai.jffi.Invoker;
import com.kenai.jffi.Library;
import com.kenai.jffi.Type;
import unrefined.desktop.ABI;
import unrefined.desktop.AWTSupport;
import unrefined.desktop.ForeignSupport;
import unrefined.desktop.ReflectionSupport;
import unrefined.internal.OperatingSystem;
import unrefined.util.NotInstantiableError;
import unrefined.util.UnexpectedError;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static unrefined.desktop.ForeignSupport.MEMORY_IO;
import static unrefined.internal.X11.X11Library.X11;

public final class X11CursorSupport {

    private X11CursorSupport() {
        throw new NotInstantiableError(X11CursorSupport.class);
    }

    public static final int XC_num_glyphs = 154;
    public static final int XC_X_cursor = 0;
    public static final int XC_arrow = 2;
    public static final int XC_based_arrow_down = 4;
    public static final int XC_based_arrow_up = 6;
    public static final int XC_boat = 8;
    public static final int XC_bogosity = 10;
    public static final int XC_bottom_left_corner = 12;
    public static final int XC_bottom_right_corner = 14;
    public static final int XC_bottom_side = 16;
    public static final int XC_bottom_tee = 18;
    public static final int XC_box_spiral = 20;
    public static final int XC_center_ptr = 22;
    public static final int XC_circle = 24;
    public static final int XC_clock = 26;
    public static final int XC_coffee_mug = 28;
    public static final int XC_cross = 30;
    public static final int XC_cross_reverse = 32;
    public static final int XC_crosshair = 34;
    public static final int XC_diamond_cross = 36;
    public static final int XC_dot = 38;
    public static final int XC_dotbox = 40;
    public static final int XC_double_arrow = 42;
    public static final int XC_draft_large = 44;
    public static final int XC_draft_small = 46;
    public static final int XC_draped_box = 48;
    public static final int XC_exchange = 50;
    public static final int XC_fleur = 52;
    public static final int XC_gobbler = 54;
    public static final int XC_gumby = 56;
    public static final int XC_hand1 = 58;
    public static final int XC_hand2 = 60;
    public static final int XC_heart = 62;
    public static final int XC_icon = 64;
    public static final int XC_iron_cross = 66;
    public static final int XC_left_ptr = 68;
    public static final int XC_left_side = 70;
    public static final int XC_left_tee = 72;
    public static final int XC_leftbutton = 74;
    public static final int XC_ll_angle = 76;
    public static final int XC_lr_angle = 78;
    public static final int XC_man = 80;
    public static final int XC_middlebutton = 82;
    public static final int XC_mouse = 84;
    public static final int XC_pencil = 86;
    public static final int XC_pirate = 88;
    public static final int XC_plus = 90;
    public static final int XC_question_arrow = 92;
    public static final int XC_right_ptr = 94;
    public static final int XC_right_side = 96;
    public static final int XC_right_tee = 98;
    public static final int XC_rightbutton = 100;
    public static final int XC_rtl_logo = 102;
    public static final int XC_sailboat = 104;
    public static final int XC_sb_down_arrow = 106;
    public static final int XC_sb_h_double_arrow = 108;
    public static final int XC_sb_left_arrow = 110;
    public static final int XC_sb_right_arrow = 112;
    public static final int XC_sb_up_arrow = 114;
    public static final int XC_sb_v_double_arrow = 116;
    public static final int XC_shuttle = 118;
    public static final int XC_sizing = 120;
    public static final int XC_spider = 122;
    public static final int XC_spraycan = 124;
    public static final int XC_star = 126;
    public static final int XC_target = 128;
    public static final int XC_tcross = 130;
    public static final int XC_top_left_arrow = 132;
    public static final int XC_top_left_corner = 134;
    public static final int XC_top_right_corner = 136;
    public static final int XC_top_side = 138;
    public static final int XC_top_tee = 140;
    public static final int XC_trek = 142;
    public static final int XC_ul_angle = 144;
    public static final int XC_umbrella = 146;
    public static final int XC_ur_angle = 148;
    public static final int XC_watch = 150;
    public static final int XC_xterm = 152;

    private static final Invoker INVOKER = Invoker.getInstance();

    static final Library Xcursor;

    private static final Function XCreateFontCursor;
    private static final Function XcursorLibraryLoadCursor;
    private static final Function XcursorImageLoadCursor;
    private static final Function XcursorImageCreate;
    private static final Function XcursorImageDestroy;
    private static final Function XcursorSupportsARGB;
    private static final Function XcursorSetDefaultSize;
    static {
        if (OperatingSystem.IS_X11 && !GraphicsEnvironment.isHeadless()) {
            XCreateFontCursor = new Function(X11.getSymbolAddress("XCreateFontCursor"), Type.POINTER, Type.POINTER, Type.UINT);
            Xcursor = Library.getCachedInstance(System.mapLibraryName("Xcursor"), Library.GLOBAL | Library.LAZY);
            if (Xcursor == null) {
                XcursorLibraryLoadCursor = null;
                XcursorImageLoadCursor = null;
                XcursorImageCreate = null;
                XcursorImageDestroy = null;
                XcursorSupportsARGB = null;
                XcursorSetDefaultSize = null;
            }
            else {
                XcursorLibraryLoadCursor = new Function(Xcursor.getSymbolAddress("XcursorLibraryLoadCursor"),
                        Type.POINTER, Type.POINTER, Type.POINTER);
                XcursorImageLoadCursor = new Function(Xcursor.getSymbolAddress("XcursorImageLoadCursor"),
                        Type.POINTER, Type.POINTER, Type.POINTER);
                XcursorImageCreate = new Function(Xcursor.getSymbolAddress("XcursorImageCreate"),
                        Type.POINTER, Type.SINT, Type.SINT);
                XcursorImageDestroy = new Function(Xcursor.getSymbolAddress("XcursorImageDestroy"),
                        Type.VOID, Type.POINTER);
                XcursorSupportsARGB = new Function(Xcursor.getSymbolAddress("XcursorSupportsARGB"),
                        Type.SINT, Type.POINTER);
                XcursorSetDefaultSize = new Function(Xcursor.getSymbolAddress("XcursorSetDefaultSize"),
                        Type.SINT, Type.POINTER, Type.SINT);
            }
        }
        else {
            XCreateFontCursor = null;
            Xcursor = null;
            XcursorLibraryLoadCursor = null;
            XcursorImageLoadCursor = null;
            XcursorImageCreate = null;
            XcursorImageDestroy = null;
            XcursorSupportsARGB = null;
            XcursorSetDefaultSize = null;
        }
    }

    private static String mapCursorName(int type) {
        switch (type) {
            case 0: return "X_cursor";
            case 2: return "arrow";
            case 4: return "based_arrow_down";
            case 6: return "based_arrow_up";
            case 8: return "boat";
            case 10: return "bogosity";
            case 12: return "bottom_left_corner";
            case 14: return "bottom_right_corner";
            case 16: return "bottom_side";
            case 18: return "bottom_tee";
            case 20: return "box_spiral";
            case 22: return "center_ptr";
            case 24: return "circle";
            case 26: return "clock";
            case 28: return "coffee_mug";
            case 30: return "cross";
            case 32: return "cross_reverse";
            case 34: return "crosshair";
            case 36: return "diamond_cross";
            case 38: return "dot";
            case 40: return "dotbox";
            case 42: return "double_arrow";
            case 44: return "draft_large";
            case 46: return "draft_small";
            case 48: return "draped_box";
            case 50: return "exchange";
            case 52: return "fleur";
            case 54: return "gobbler";
            case 56: return "gumby";
            case 58: return "hand1";
            case 60: return "hand2";
            case 62: return "heart";
            case 64: return "icon";
            case 66: return "iron_cross";
            case 68: return "left_ptr";
            case 70: return "left_side";
            case 72: return "left_tee";
            case 74: return "leftbutton";
            case 76: return "ll_angle";
            case 78: return "lr_angle";
            case 80: return "man";
            case 82: return "middlebutton";
            case 84: return "mouse";
            case 86: return "pencil";
            case 88: return "pirate";
            case 90: return "plus";
            case 92: return "question_arrow";
            case 94: return "right_ptr";
            case 96: return "right_side";
            case 98: return "right_tee";
            case 100: return "rightbutton";
            case 102: return "rtl_logo";
            case 104: return "sailboat";
            case 106: return "sb_down_arrow";
            case 108: return "sb_h_double_arrow";
            case 110: return "sb_left_arrow";
            case 112: return "sb_right_arrow";
            case 114: return "sb_up_arrow";
            case 116: return "sb_v_double_arrow";
            case 118: return "shuttle";
            case 120: return "sizing";
            case 122: return "spider";
            case 124: return "spraycan";
            case 126: return "star";
            case 128: return "target";
            case 130: return "tcross";
            case 132: return "top_left_arrow";
            case 134: return "top_left_corner";
            case 136: return "top_right_corner";
            case 138: return "top_side";
            case 140: return "top_tee";
            case 142: return "trek";
            case 144: return "ul_angle";
            case 146: return "umbrella";
            case 148: return "ur_angle";
            case 150: return "watch";
            case 152: return "xterm";
            default: throw new IllegalArgumentException("Illegal X cursor type: " + type);
        }
    }

    private static int mapCursorType(int type) {
        switch (type) {
            case XC_left_ptr: return Cursor.DEFAULT_CURSOR;
            case XC_crosshair: return Cursor.CROSSHAIR_CURSOR;
            case XC_xterm: return Cursor.TEXT_CURSOR;
            case XC_watch: return Cursor.WAIT_CURSOR;
            case XC_bottom_left_corner: return Cursor.SW_RESIZE_CURSOR;
            case XC_top_left_corner: return Cursor.NW_RESIZE_CURSOR;
            case XC_bottom_right_corner: return Cursor.SE_RESIZE_CURSOR;
            case XC_top_right_corner: return Cursor.NE_RESIZE_CURSOR;
            case XC_bottom_side: return Cursor.S_RESIZE_CURSOR;
            case XC_top_side: return Cursor.N_RESIZE_CURSOR;
            case XC_left_side: return Cursor.W_RESIZE_CURSOR;
            case XC_right_side: return Cursor.E_RESIZE_CURSOR;
            case XC_hand2: return Cursor.HAND_CURSOR;
            case XC_fleur: return Cursor.MOVE_CURSOR;
            default: return Cursor.CUSTOM_CURSOR;
        }
    }

    private static final Map<String, Cursor> SYSTEM_CURSORS = new HashMap<>();

    public static Cursor getSystemCursor(int type) throws IllegalArgumentException, HeadlessException {
        if (GraphicsEnvironment.isHeadless()) throw new HeadlessException();
        int cursorType = mapCursorType(type);
        if (cursorType != Cursor.CUSTOM_CURSOR) return Cursor.getPredefinedCursor(cursorType);
        long display = X11AWTSupport.getDisplay();
        if (display == 0) return Cursor.getDefaultCursor();
        String name = mapCursorName(type);
        if (!SYSTEM_CURSORS.containsKey(name)) {
            synchronized (SYSTEM_CURSORS) {
                if (SYSTEM_CURSORS.containsKey(name)) return SYSTEM_CURSORS.get(name);
                try {
                    HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(XCreateFontCursor);
                    heapInvocationBuffer.putAddress(display);
                    heapInvocationBuffer.putInt(type);
                    long pData = INVOKER.invokeAddress(XCreateFontCursor, heapInvocationBuffer);
                    if (pData == 0) return Cursor.getDefaultCursor();
                    else SYSTEM_CURSORS.put(name, new XExtendedCursor(name, pData, false));
                } catch (Throwable e) {
                    throw new UnexpectedError(e);
                }
            }
        }
        return SYSTEM_CURSORS.get(name);
    }

    public static Cursor getSystemCursor(String name) throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) throw new HeadlessException();
        long display = X11AWTSupport.getDisplay();
        if (display == 0 || Xcursor == null) return Cursor.getDefaultCursor();
        if (!SYSTEM_CURSORS.containsKey(name)) {
            if (SYSTEM_CURSORS.containsKey(name)) return SYSTEM_CURSORS.get(name);
            synchronized (SYSTEM_CURSORS) {
                long pData = XcursorLibraryLoadCursor(display, name);
                if (pData == 0) return Cursor.getDefaultCursor();
                else SYSTEM_CURSORS.put(name, new XExtendedCursor(name, pData, true));
            }
        }
        return SYSTEM_CURSORS.get(name);
    }

    private static void refreshLibraryCursors() throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) throw new HeadlessException();
        long display = X11AWTSupport.getDisplay();
        if (display == 0 || Xcursor == null) return;
        synchronized (SYSTEM_CURSORS) {
            Set<String> toRemove = new HashSet<>();
            for (Map.Entry<String, Cursor> entry : SYSTEM_CURSORS.entrySet()) {
                String name = entry.getKey();
                Cursor cursor = entry.getValue();
                if (cursor instanceof XExtendedCursor && ((XExtendedCursor) cursor).isLibraryCursor()) {
                    long pData = XcursorLibraryLoadCursor(display, name);
                    if (pData == 0) toRemove.add(name);
                    else {
                        long prev = ((XExtendedCursor) cursor).getPData();
                        try {
                            ((XExtendedCursor) cursor).setPData(pData);
                        }
                        finally {
                            finalizeCursorPData(prev);
                        }
                    }
                }
            }
            SYSTEM_CURSORS.keySet().removeAll(toRemove);
        }
    }

    private static long XcursorLibraryLoadCursor(long display, String name) {
        if (display == 0) return 0;
        long string = ForeignSupport.allocateString(name);
        try {
            HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(XcursorLibraryLoadCursor);
            heapInvocationBuffer.putAddress(display);
            heapInvocationBuffer.putAddress(string);
            return INVOKER.invokeAddress(XcursorLibraryLoadCursor, heapInvocationBuffer);
        } finally {
            MEMORY_IO.freeMemory(string);
        }
    }

    public static boolean isTrueColorCursorSupported() {
        long display = X11AWTSupport.getDisplay();
        if (display == 0 || Xcursor == null) return false;
        else {
            HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(XcursorSupportsARGB);
            heapInvocationBuffer.putAddress(display);
            return INVOKER.invokeInt(XcursorSupportsARGB, heapInvocationBuffer) != 0;
        }
    }

    public static int getMaximumCursorColors() {
        if (isTrueColorCursorSupported()) return 16777216; // true color
        else return Toolkit.getDefaultToolkit().getMaximumCursorColors();
    }

    private static boolean setCursorDefaultSize(int size) {
        long display = X11AWTSupport.getDisplay();
        if (display == 0 || Xcursor == null) return false;
        else {
            HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(XcursorSetDefaultSize);
            heapInvocationBuffer.putAddress(display);
            if (ABI.I == 8) heapInvocationBuffer.putLong(size);
            else heapInvocationBuffer.putInt(size);
            return INVOKER.invokeInt(XcursorSetDefaultSize, heapInvocationBuffer) != 0;
        }
    }

    public static Cursor createCustomCursor(Image cursor, Point hotSpot, String name) throws IndexOutOfBoundsException, HeadlessException {
        if (GraphicsEnvironment.isHeadless()) throw new HeadlessException();
        else if (isTrueColorCursorSupported()) return new XExtendedCursor(cursor, hotSpot, name);
        else return Toolkit.getDefaultToolkit().createCustomCursor(cursor, hotSpot, name);
    }

    private static final Method setPDataMethod;
    private static final Method finalizeImplMethod;
    static {
        Method method;
        try {
            method = Cursor.class.getDeclaredMethod("setPData", long.class);
        }
        catch (NoSuchMethodException e) {
            method = null;
        }
        setPDataMethod = method;
        try {
            method = Cursor.class.getDeclaredMethod("finalizeImpl", long.class);
        }
        catch (NoSuchMethodException e) {
            method = null;
        }
        finalizeImplMethod = method;
    }

    private static boolean setCursorPData(Cursor cursor, long pData) {
        if (setPDataMethod == null) return false;
        else {
            try {
                ReflectionSupport.invokeVoidMethod(cursor, setPDataMethod, pData);
                return true;
            } catch (InvocationTargetException e) {
                return false;
            }
        }
    }

    private static boolean finalizeCursorPData(long pData) {
        if (finalizeImplMethod == null) return false;
        else {
            try {
                ReflectionSupport.invokeVoidMethod(null, finalizeImplMethod, pData);
                return true;
            } catch (InvocationTargetException e) {
                return false;
            }
        }
    }

    private static final class XExtendedCursor extends Cursor {

        private static final long serialVersionUID = -4852254763316324280L;

        private final boolean isLibraryCursor;
        private final boolean isCustomCursor;

        private volatile long pData;

        public XExtendedCursor(String name, long pData, boolean isLibraryCursor) {
            super(name);
            this.isLibraryCursor = isLibraryCursor;
            this.isCustomCursor = false;
            setPData(pData);
        }

        public long getPData() {
            return pData;
        }

        public void setPData(long pData) {
            boolean locked = AWTSupport.awtLock();
            try {
                setCursorPData(this, this.pData = pData);
            } finally {
                if (locked) AWTSupport.awtUnlock();
            }
        }

        public boolean isLibraryCursor() {
            return isLibraryCursor;
        }

        public boolean isCustomCursor() {
            return isCustomCursor;
        }

        // see sun.awt.CustomCursor.java
        public XExtendedCursor(Image cursor, Point hotSpot, String name) throws IndexOutOfBoundsException {
            super(name);
            this.isLibraryCursor = false;
            this.isCustomCursor = true;

            long display = X11AWTSupport.getDisplay();
            if (display == 0 || Xcursor == null) {
                setCursorPData(this, 0);
                return;
            }

            Toolkit toolkit = Toolkit.getDefaultToolkit();

            // Make sure image is fully loaded.
            Canvas c = new Canvas(); // for its imageUpdate method
            MediaTracker tracker = new MediaTracker(c);
            tracker.addImage(cursor, 0);
            try {
                tracker.waitForAll();
            } catch (InterruptedException ignored) {
            }
            int width = cursor.getWidth(c);
            int height = cursor.getHeight(c);

            // Fix for bug 4212593 The Toolkit.createCustomCursor does not
            //                     check absence of the image of cursor
            // If the image is invalid, the cursor will be hidden (made completely
            // transparent). In this case, getBestCursorSize() will adjust negative w and h,
            // but we need to set the hotspot inside the image here.
            if (tracker.isErrorAny() || width < 0 || height < 0) {
                hotSpot.x = hotSpot.y = 0;
            }

            // Scale image to nearest supported size.
            Dimension nativeSize = toolkit.getBestCursorSize(width, height);
            if ((nativeSize.width != width || nativeSize.height != height) &&
                    (nativeSize.width != 0 && nativeSize.height != 0)) {
                cursor = cursor.getScaledInstance(nativeSize.width,
                        nativeSize.height,
                        Image.SCALE_DEFAULT);
                width = nativeSize.width;
                height = nativeSize.height;
            }

            // Verify that the hotspot is within cursor bounds.
            if (hotSpot.x >= width || hotSpot.y >= height || hotSpot.x < 0 || hotSpot.y < 0) {
                throw new IndexOutOfBoundsException("invalid hotSpot");
            }

            /* Extract ARGB array from image.
             *
             * A transparency mask can be created in native code by checking
             * each pixel's top byte -- a 0 value means the pixel's transparent.
             * Since each platform's format of the bitmap and mask are likely to
             * be different, their creation shouldn't be here.
             */
            int[] pixels = new int[width * height];
            ImageProducer ip = cursor.getSource();
            PixelGrabber pg = new PixelGrabber(ip, 0, 0, width, height,
                    pixels, 0, width);
            try {
                pg.grabPixels();
            } catch (InterruptedException ignored) {
            }

            boolean locked = AWTSupport.awtLock();
            try {
                long nativePixels = MEMORY_IO.allocateMemory(pixels.length * 4L, false);
                try {
                    MEMORY_IO.putIntArray(nativePixels, pixels, 0, pixels.length);
                    HeapInvocationBuffer heapInvocationBuffer = new HeapInvocationBuffer(XcursorImageCreate);
                    if (ABI.I == 8) {
                        heapInvocationBuffer.putLong(width);
                        heapInvocationBuffer.putLong(height);
                    }
                    else {
                        heapInvocationBuffer.putInt(width);
                        heapInvocationBuffer.putInt(height);
                    }
                    long nativeCursorImage = INVOKER.invokeAddress(XcursorImageCreate, heapInvocationBuffer);
                    MEMORY_IO.putInt(nativeCursorImage + 16, hotSpot.x);
                    MEMORY_IO.putInt(nativeCursorImage + 20, hotSpot.y);
                    MEMORY_IO.putAddress(nativeCursorImage + 32, nativePixels);
                    try {
                        HeapInvocationBuffer heapInvocationBuffer1 = new HeapInvocationBuffer(XcursorImageLoadCursor);
                        heapInvocationBuffer1.putAddress(display);
                        heapInvocationBuffer1.putAddress(nativeCursorImage);
                        long pData = INVOKER.invokeAddress(XcursorImageLoadCursor, heapInvocationBuffer1);
                        if (pData == 0) return;
                        setCursorPData(this, pData);
                    }
                    finally {
                        HeapInvocationBuffer heapInvocationBuffer1 = new HeapInvocationBuffer(XcursorImageDestroy);
                        heapInvocationBuffer1.putAddress(nativeCursorImage);
                        INVOKER.invokeInt(XcursorImageDestroy, heapInvocationBuffer1);
                    }
                }
                finally {
                    MEMORY_IO.freeMemory(nativePixels);
                }
            }
            finally {
                if (locked) AWTSupport.awtUnlock();
            }
        }

    }

    static {
        if (OperatingSystem.IS_X11 && Xcursor != null) {
            AWTSupport.setDesktopProperty("DnD.Cursor.CopyDrop", getSystemCursor("copy"));
            AWTSupport.setDesktopProperty("DnD.Cursor.MoveDrop", getSystemCursor("move"));
            AWTSupport.setDesktopProperty("DnD.Cursor.LinkDrop", getSystemCursor("alias"));
            AWTSupport.setDesktopProperty("DnD.Cursor.CopyNoDrop", getSystemCursor("grabbing"));
            AWTSupport.setDesktopProperty("DnD.Cursor.MoveNoDrop", getSystemCursor("grabbing"));
            AWTSupport.setDesktopProperty("DnD.Cursor.LinkNoDrop", getSystemCursor("grabbing"));
            Object property = XSettings.getProperty("Gtk/CursorThemeSize");
            if (property instanceof Integer) setCursorDefaultSize((Integer) property);
            XSettings.addPropertyChangeListener("Gtk/CursorThemeSize", evt -> {
                Object value = evt.getNewValue();
                if (value instanceof Integer) {
                    try {
                        setCursorDefaultSize((Integer) value);
                    }
                    finally {
                        refreshLibraryCursors();
                    }
                }
            });
        }
    }

    public static void patch() {
    }

}
