package unrefined.internal.X11;

import unrefined.internal.AWTUtils;
import unrefined.internal.CursorUtils;
import unrefined.internal.SystemUtils;
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
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;

import static java.lang.foreign.ValueLayout.*;
import static unrefined.internal.MemoryLayoutUtils.NATIVE_INT;
import static unrefined.internal.X11.X11Library.X11_LOOKUP;

public final class X11CursorUtils {

    private X11CursorUtils() {
        throw new NotInstantiableError(X11CursorUtils.class);
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

    private static final Linker LINKER = Linker.nativeLinker();
    static final SymbolLookup XCURSOR_LOOKUP;
    private static final MethodHandle XCreateFontCursorMethodHandle;
    private static final MethodHandle XcursorLibraryLoadCursorMethodHandle;
    private static final StructLayout XcursorImageLayout;
    private static final MethodHandle XcursorImageLoadCursorMethodHandle;
    private static final MethodHandle XcursorImageCreateMethodHandle;
    private static final MethodHandle XcursorImageDestroyMethodHandle;
    private static final MethodHandle XcursorSupportsARGBMethodHandle;
    private static final MethodHandle XcursorSetDefaultSizeMethodHandle;
    static {
        if (SystemUtils.IS_X11 && !GraphicsEnvironment.isHeadless()) {
            XCreateFontCursorMethodHandle = X11_LOOKUP.find("XCreateFontCursor").map(address ->
                    LINKER.downcallHandle(address, FunctionDescriptor.of(ADDRESS, ADDRESS, NATIVE_INT))).orElseThrow(UnexpectedError::new);
            SymbolLookup lookup;
            try {
                lookup = SymbolLookup.libraryLookup(System.mapLibraryName("Xcursor"), Arena.global());
            }
            catch (IllegalArgumentException e) {
                lookup = null;
            }
            XCURSOR_LOOKUP = lookup;
            if (XCURSOR_LOOKUP == null) {
                XcursorLibraryLoadCursorMethodHandle = null;
                XcursorImageLayout = null;
                XcursorImageLoadCursorMethodHandle = null;
                XcursorImageCreateMethodHandle = null;
                XcursorImageDestroyMethodHandle = null;
                XcursorSupportsARGBMethodHandle = null;
                XcursorSetDefaultSizeMethodHandle = null;
            }
            else {
                XcursorLibraryLoadCursorMethodHandle = XCURSOR_LOOKUP.find("XcursorLibraryLoadCursor").map(address ->
                        LINKER.downcallHandle(address, FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS))).orElseThrow(UnexpectedError::new);
                XcursorImageLayout = MemoryLayout.structLayout(
                        JAVA_INT.withName("version"),
                        JAVA_INT.withName("size"),
                        JAVA_INT.withName("width"),
                        JAVA_INT.withName("height"),
                        JAVA_INT.withName("xhot"),
                        JAVA_INT.withName("yhot"),
                        JAVA_INT.withName("delay"),
                        MemoryLayout.paddingLayout(JAVA_INT.byteSize()),
                        ADDRESS.withName("pixels")
                ).withName("XcursorImage");
                XcursorImageLoadCursorMethodHandle = XCURSOR_LOOKUP.find("XcursorImageLoadCursor").map(address ->
                        LINKER.downcallHandle(address, FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS))).orElseThrow(UnexpectedError::new);
                XcursorImageCreateMethodHandle = XCURSOR_LOOKUP.find("XcursorImageCreate").map(address ->
                        LINKER.downcallHandle(address, FunctionDescriptor.of(ADDRESS.withTargetLayout(XcursorImageLayout),
                                NATIVE_INT, NATIVE_INT))).orElseThrow(UnexpectedError::new);
                XcursorImageDestroyMethodHandle = XCURSOR_LOOKUP.find("XcursorImageDestroy").map(address ->
                        LINKER.downcallHandle(address, FunctionDescriptor.ofVoid(ADDRESS))).orElseThrow(UnexpectedError::new);
                XcursorSupportsARGBMethodHandle = XCURSOR_LOOKUP.find("XcursorSupportsARGB").map(address ->
                        LINKER.downcallHandle(address, FunctionDescriptor.of(NATIVE_INT, ADDRESS))).orElseThrow(UnexpectedError::new);
                XcursorSetDefaultSizeMethodHandle = XCURSOR_LOOKUP.find("XcursorSetDefaultSize").map(address ->
                        LINKER.downcallHandle(address, FunctionDescriptor.of(NATIVE_INT, ADDRESS, NATIVE_INT))).orElseThrow(UnexpectedError::new);
            }
        }
        else {
            XCreateFontCursorMethodHandle = null;
            XCURSOR_LOOKUP = null;
            XcursorLibraryLoadCursorMethodHandle = null;
            XcursorImageLayout = null;
            XcursorImageLoadCursorMethodHandle = null;
            XcursorImageCreateMethodHandle = null;
            XcursorImageDestroyMethodHandle = null;
            XcursorSupportsARGBMethodHandle = null;
            XcursorSetDefaultSizeMethodHandle = null;
        }
    }

    private static String mapCursorName(int type) {
        return switch (type) {
            case 0 -> "X_cursor";
            case 2 -> "arrow";
            case 4 -> "based_arrow_down";
            case 6 -> "based_arrow_up";
            case 8 -> "boat";
            case 10 -> "bogosity";
            case 12 -> "bottom_left_corner";
            case 14 -> "bottom_right_corner";
            case 16 -> "bottom_side";
            case 18 -> "bottom_tee";
            case 20 -> "box_spiral";
            case 22 -> "center_ptr";
            case 24 -> "circle";
            case 26 -> "clock";
            case 28 -> "coffee_mug";
            case 30 -> "cross";
            case 32 -> "cross_reverse";
            case 34 -> "crosshair";
            case 36 -> "diamond_cross";
            case 38 -> "dot";
            case 40 -> "dotbox";
            case 42 -> "double_arrow";
            case 44 -> "draft_large";
            case 46 -> "draft_small";
            case 48 -> "draped_box";
            case 50 -> "exchange";
            case 52 -> "fleur";
            case 54 -> "gobbler";
            case 56 -> "gumby";
            case 58 -> "hand1";
            case 60 -> "hand2";
            case 62 -> "heart";
            case 64 -> "icon";
            case 66 -> "iron_cross";
            case 68 -> "left_ptr";
            case 70 -> "left_side";
            case 72 -> "left_tee";
            case 74 -> "leftbutton";
            case 76 -> "ll_angle";
            case 78 -> "lr_angle";
            case 80 -> "man";
            case 82 -> "middlebutton";
            case 84 -> "mouse";
            case 86 -> "pencil";
            case 88 -> "pirate";
            case 90 -> "plus";
            case 92 -> "question_arrow";
            case 94 -> "right_ptr";
            case 96 -> "right_side";
            case 98 -> "right_tee";
            case 100 -> "rightbutton";
            case 102 -> "rtl_logo";
            case 104 -> "sailboat";
            case 106 -> "sb_down_arrow";
            case 108 -> "sb_h_double_arrow";
            case 110 -> "sb_left_arrow";
            case 112 -> "sb_right_arrow";
            case 114 -> "sb_up_arrow";
            case 116 -> "sb_v_double_arrow";
            case 118 -> "shuttle";
            case 120 -> "sizing";
            case 122 -> "spider";
            case 124 -> "spraycan";
            case 126 -> "star";
            case 128 -> "target";
            case 130 -> "tcross";
            case 132 -> "top_left_arrow";
            case 134 -> "top_left_corner";
            case 136 -> "top_right_corner";
            case 138 -> "top_side";
            case 140 -> "top_tee";
            case 142 -> "trek";
            case 144 -> "ul_angle";
            case 146 -> "umbrella";
            case 148 -> "ur_angle";
            case 150 -> "watch";
            case 152 -> "xterm";
            default -> throw new IllegalArgumentException("Illegal X cursor type: " + type);
        };
    }

    private static int mapCursorType(int type) {
        return switch (type) {
            case XC_left_ptr -> Cursor.DEFAULT_CURSOR;
            case XC_crosshair -> Cursor.CROSSHAIR_CURSOR;
            case XC_xterm -> Cursor.TEXT_CURSOR;
            case XC_watch -> Cursor.WAIT_CURSOR;
            case XC_bottom_left_corner -> Cursor.SW_RESIZE_CURSOR;
            case XC_top_left_corner -> Cursor.NW_RESIZE_CURSOR;
            case XC_bottom_right_corner -> Cursor.SE_RESIZE_CURSOR;
            case XC_top_right_corner -> Cursor.NE_RESIZE_CURSOR;
            case XC_bottom_side -> Cursor.S_RESIZE_CURSOR;
            case XC_top_side -> Cursor.N_RESIZE_CURSOR;
            case XC_left_side -> Cursor.W_RESIZE_CURSOR;
            case XC_right_side -> Cursor.E_RESIZE_CURSOR;
            case XC_hand2 -> Cursor.HAND_CURSOR;
            case XC_fleur -> Cursor.MOVE_CURSOR;
            default -> Cursor.CUSTOM_CURSOR;
        };
    }

    private static final Map<String, Cursor> SYSTEM_CURSORS = new HashMap<>();

    public static Cursor getSystemCursor(int type) throws IllegalArgumentException, HeadlessException {
        if (GraphicsEnvironment.isHeadless()) throw new HeadlessException();
        int cursorType = mapCursorType(type);
        if (cursorType != Cursor.CUSTOM_CURSOR) return Cursor.getPredefinedCursor(cursorType);
        long display = X11AWTUtils.getDisplay();
        if (display == 0) return Cursor.getDefaultCursor();
        String name = mapCursorName(type);
        if (!SYSTEM_CURSORS.containsKey(name)) {
            synchronized (SYSTEM_CURSORS) {
                try {
                    long pData = ((MemorySegment) XCreateFontCursorMethodHandle.invoke(MemorySegment.ofAddress(display))).address();
                    if (pData == 0) return Cursor.getDefaultCursor();
                    else SYSTEM_CURSORS.put(name, new XExtendedCursor(name, pData));
                } catch (Throwable e) {
                    throw new UnexpectedError(e);
                }
            }
        }
        return SYSTEM_CURSORS.get(name);
    }

    public static Cursor getSystemCursor(String name) throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) throw new HeadlessException();
        long display = X11AWTUtils.getDisplay();
        if (display == 0 || XCURSOR_LOOKUP == null) return Cursor.getDefaultCursor();
        if (!SYSTEM_CURSORS.containsKey(name)) {
            synchronized (SYSTEM_CURSORS) {
                try (Arena arena = Arena.ofConfined()) {
                    long pData = ((MemorySegment) XcursorLibraryLoadCursorMethodHandle
                            .invoke(MemorySegment.ofAddress(display), arena.allocateUtf8String(name))).address();
                    if (pData == 0) return Cursor.getDefaultCursor();
                    else SYSTEM_CURSORS.put(name, new XExtendedCursor(name, pData));
                } catch (Throwable e) {
                    throw new UnexpectedError(e);
                }
            }
        }
        return SYSTEM_CURSORS.get(name);
    }

    public static boolean isTrueColorCursorSupported() {
        try {
            long display = X11AWTUtils.getDisplay();
            return display != 0 && XCURSOR_LOOKUP != null &&
                    (int) XcursorSupportsARGBMethodHandle.invoke(MemorySegment.ofAddress(display)) == 1;
        } catch (Throwable e) {
            return false;
        }
    }

    public static int getMaximumCursorColors() {
        if (isTrueColorCursorSupported()) return 16777216; // true color
        else return Toolkit.getDefaultToolkit().getMaximumCursorColors();
    }

    private static boolean setCursorDefaultSize(int size) {
        try {
            long display = X11AWTUtils.getDisplay();
            return display != 0 && XCURSOR_LOOKUP != null &&
                    (int) XcursorSetDefaultSizeMethodHandle.invoke(MemorySegment.ofAddress(display), size) == 1;
        }
        catch (Throwable e) {
            return false;
        }
    }

    public static Cursor createCustomCursor(Image cursor, Point hotSpot, String name) throws IndexOutOfBoundsException, HeadlessException {
        if (GraphicsEnvironment.isHeadless()) throw new HeadlessException();
        else if (isTrueColorCursorSupported()) return new XExtendedCursor(cursor, hotSpot, name);
        else return Toolkit.getDefaultToolkit().createCustomCursor(cursor, hotSpot, name);
    }

    private static final class XExtendedCursor extends Cursor {

        private static final long serialVersionUID = -4852254763316324280L;

        public XExtendedCursor(String name, long pData) {
            super(name);
            boolean locked = AWTUtils.awtLock();
            try {
                CursorUtils.setCursorPData(this, pData);
            } finally {
                if (locked) AWTUtils.awtUnlock();
            }
        }

        // see sun.awt.CustomCursor.java
        public XExtendedCursor(Image cursor, Point hotSpot, String name) throws IndexOutOfBoundsException {
            super(name);

            long display = X11AWTUtils.getDisplay();
            if (display == 0 || XCURSOR_LOOKUP == null) {
                CursorUtils.setCursorPData(this, 0);
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

            boolean locked = AWTUtils.awtLock();
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment nativePixels = arena.allocateArray(JAVA_INT, pixels);
                if (nativePixels.equals(MemorySegment.NULL)) return;
                MemorySegment nativeCursorImage = (MemorySegment) XcursorImageCreateMethodHandle.invoke(width, height);
                XcursorImageLayout.varHandle(PathElement.groupElement("xhot")).set(nativeCursorImage, hotSpot.x);
                XcursorImageLayout.varHandle(PathElement.groupElement("yhot")).set(nativeCursorImage, hotSpot.y);
                XcursorImageLayout.varHandle(PathElement.groupElement("pixels")).set(nativeCursorImage, nativePixels);
                try {
                    long pData = ((MemorySegment) XcursorImageLoadCursorMethodHandle.invoke(MemorySegment.ofAddress(X11AWTUtils.getDisplay()), nativeCursorImage)).address();
                    if (pData == 0) return;
                    CursorUtils.setCursorPData(this, pData);
                }
                finally {
                    XcursorImageDestroyMethodHandle.invoke(nativeCursorImage);
                }
            } catch (Throwable e) {
                throw new UnexpectedError(e);
            } finally {
                if (locked) AWTUtils.awtUnlock();
            }
        }

    }

    static {
        if (SystemUtils.IS_X11 && XCURSOR_LOOKUP != null) {
            AWTUtils.setDesktopProperty("DnD.Cursor.CopyDrop", getSystemCursor("copy"));
            AWTUtils.setDesktopProperty("DnD.Cursor.MoveDrop", getSystemCursor("move"));
            AWTUtils.setDesktopProperty("DnD.Cursor.LinkDrop", getSystemCursor("alias"));
            AWTUtils.setDesktopProperty("DnD.Cursor.CopyNoDrop", getSystemCursor("grabbing"));
            AWTUtils.setDesktopProperty("DnD.Cursor.MoveNoDrop", getSystemCursor("grabbing"));
            AWTUtils.setDesktopProperty("DnD.Cursor.LinkNoDrop", getSystemCursor("grabbing"));
            Object property = XSettings.getProperty("Gtk/CursorThemeSize");
            if (property instanceof Integer) setCursorDefaultSize((Integer) property);
            XSettings.addPropertyChangeListener("Gtk/CursorThemeSize", evt -> {
                Object value = evt.getNewValue();
                if (value instanceof Integer) setCursorDefaultSize((Integer) value);
            });
        }
    }

    public static void patch() {
    }

}
