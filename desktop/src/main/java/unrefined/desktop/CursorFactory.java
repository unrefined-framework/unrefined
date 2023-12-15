package unrefined.desktop;

import unrefined.internal.SystemUtils;
import unrefined.internal.X11.X11CursorUtils;
import unrefined.media.graphics.CursorNotFoundException;
import unrefined.util.NotInstantiableError;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import static unrefined.media.graphics.Cursor.Type.*;

public final class CursorFactory {

    public static final Cursor NONE_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(
            Toolkit.getDefaultToolkit().getImage(CursorFactory.class.getResource("")),
            new Point(0, 0), "UXGL None Cursor");

    private CursorFactory() {
        throw new NotInstantiableError(CursorFactory.class);
    }

    public static Cursor getSystemCursor(int type) throws CursorNotFoundException {
        switch (type) {
            case ARROW: return Cursor.getDefaultCursor();
            case CROSSHAIR: return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
            case IBEAM: return Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
            case WAIT:
                if (SystemUtils.IS_MAC) return Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR); // FIXME Cocoa
                else return Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
            case POINTING_HAND: return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            case MOVE: return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
            case RESIZE_N: return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
            case RESIZE_S: return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
            case RESIZE_W: return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
            case RESIZE_E: return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
            case RESIZE_SW: return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
            case RESIZE_SE: return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
            case RESIZE_NW: return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
            case RESIZE_NE: return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
            case RESIZE_NS:
                if (SystemUtils.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR); // Windows unsupported
                else if (SystemUtils.IS_MAC) return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor(X11CursorUtils.XC_sb_v_double_arrow);
            case RESIZE_WE:
                if (SystemUtils.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR); // Windows unsupported
                else if (SystemUtils.IS_MAC) return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor(X11CursorUtils.XC_sb_h_double_arrow);
            case RESIZE_NWSE:
                if (SystemUtils.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR); // Windows unsupported
                else if (SystemUtils.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor("bd_double_arrow");
            case RESIZE_NESW:
                if (SystemUtils.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR); // Windows unsupported
                else if (SystemUtils.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor("fd_double_arrow");
            case RESIZE_COL:
                if (SystemUtils.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR); // FIXME extract ole32.dll and bundle image
                else if (SystemUtils.IS_MAC) return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor("col-resize");
            case RESIZE_ROW:
                if (SystemUtils.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR); // FIXME extract ole32.dll and bundle image
                else if (SystemUtils.IS_MAC) return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor("row-resize");
            case CELL:
                if (SystemUtils.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME extract ole32.dll and bundle image
                else if (SystemUtils.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor("plus");
            case HELP:
                if (SystemUtils.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME read cursor file
                else if (SystemUtils.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor(X11CursorUtils.XC_question_arrow);
            case ZOOM_IN:
                if (SystemUtils.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME extract ole32.dll and bundle image
                else if (SystemUtils.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor("zoom-in");
            case ZOOM_OUT:
                if (SystemUtils.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME extract ole32.dll and bundle image
                else if (SystemUtils.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor("zoom-out");
            case NO:
                if (SystemUtils.IS_WINDOWS) return getSystemCustomCursor("Invalid.32x32"); // FIXME read cursor file
                else if (SystemUtils.IS_MAC) return getDesktopPropertyCursor("DnD.Cursor.MoveNoDrop");
                else return X11CursorUtils.getSystemCursor("crossed_circle");
            case GRAB:
                if (SystemUtils.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME extract ole32.dll and bundle image
                else if (SystemUtils.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor(X11CursorUtils.XC_hand1);
            case GRABBING:
                if (SystemUtils.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME extract ole32.dll and bundle image
                else if (SystemUtils.IS_MAC) return getDesktopPropertyCursor("DnD.Cursor.MoveDrop");
                else return X11CursorUtils.getSystemCursor("grabbing");
            case COPY_DROP:
                if (SystemUtils.IS_WINDOWS) return getSystemCustomCursor("CopyDrop.32x32"); // FIXME extract ole32.dll and bundle image
                else if (SystemUtils.IS_MAC) return getDesktopPropertyCursor("DnD.Cursor.CopyDrop");
                else return X11CursorUtils.getSystemCursor("dnd-copy");
            case LINK_DROP:
                if (SystemUtils.IS_WINDOWS) return getSystemCustomCursor("LinkDrop.32x32"); // FIXME extract ole32.dll and bundle image
                else if (SystemUtils.IS_MAC) return getDesktopPropertyCursor("DnD.Cursor.LinkDrop");
                else return X11CursorUtils.getSystemCursor("dnd-link");
            case MOVE_DROP:
                if (SystemUtils.IS_WINDOWS) return getSystemCustomCursor("MoveDrop.32x32"); // FIXME extract ole32.dll and bundle image
                else if (SystemUtils.IS_MAC) return getDesktopPropertyCursor("DnD.Cursor.MoveDrop");
                else return X11CursorUtils.getSystemCursor("dnd-move");
            case NO_DROP:
                if (SystemUtils.IS_WINDOWS) return getSystemCustomCursor("Invalid.32x32"); // FIXME read cursor file
                else if (SystemUtils.IS_MAC) return getDesktopPropertyCursor("DnD.Cursor.MoveNoDrop");
                else return X11CursorUtils.getSystemCursor("dnd-no-drop");
            case UP_ARROW:
                if (SystemUtils.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME read cursor file
                else if (SystemUtils.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor(X11CursorUtils.XC_sb_up_arrow);
            case VERTICAL_IBEAM:
                if (SystemUtils.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME read cursor file
                else if (SystemUtils.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor("vertical-text");
            case CONTEXT_MENU:
                if (SystemUtils.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME extract ole32.dll and bundle image
                else if (SystemUtils.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor("context-menu");
            case PROGRESS:
                if (SystemUtils.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR); // FIXME read cursor file
                else if (SystemUtils.IS_MAC) return Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor("left_ptr_watch");
            case FLEUR:
                if (SystemUtils.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR); // Windows unsupported
                else if (SystemUtils.IS_MAC) return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR); // FIXME Cocoa
                else return X11CursorUtils.getSystemCursor(X11CursorUtils.XC_fleur);
            case NONE: return NONE_CURSOR;
            default: throw new CursorNotFoundException(type);
        }
    }

    public static Cursor getDesktopPropertyCursor(String name) {
        try {
            Cursor result = (Cursor) Toolkit.getDefaultToolkit().getDesktopProperty(name);
            return result == null ? Cursor.getDefaultCursor() : result;
        }
        catch (ClassCastException e) {
            return Cursor.getDefaultCursor();
        }
    }

    public static Cursor getSystemCustomCursor(String name) {
        try {
            Cursor cursor = Cursor.getSystemCustomCursor(name);
            return cursor == null ? Cursor.getDefaultCursor() : cursor;
        }
        catch (AWTException e) {
            return Cursor.getDefaultCursor();
        }
    }

    public static Cursor createCustomCursor(Image cursor, Point hotSpot, String name) {
        if (SystemUtils.IS_X11) return X11CursorUtils.createCustomCursor(cursor, hotSpot, name);
        else return Toolkit.getDefaultToolkit().createCustomCursor(cursor, hotSpot, name);
    }

    public static int getMaximumCursorColors() {
        if (SystemUtils.IS_X11) return X11CursorUtils.getMaximumCursorColors();
        else return Toolkit.getDefaultToolkit().getMaximumCursorColors();
    }

}
