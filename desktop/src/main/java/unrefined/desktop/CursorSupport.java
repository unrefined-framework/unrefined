package unrefined.desktop;

import unrefined.desktop.X11.X11CursorSupport;
import unrefined.media.graphics.CursorNotFoundException;
import unrefined.util.NotInstantiableError;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import static unrefined.media.graphics.Cursor.Type.*;

public final class CursorSupport {

    public static final Cursor NONE_CURSOR;
    static {
        Cursor cursor;
        try {
            cursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    Toolkit.getDefaultToolkit().getImage(CursorSupport.class.getResource("")),
                    new Point(0, 0), "Unrefined None Cursor");
        }
        catch (HeadlessException e) {
            cursor = null;
        }
        NONE_CURSOR = cursor;
    }

    private CursorSupport() {
        throw new NotInstantiableError(CursorSupport.class);
    }

    public static Cursor getSystemCursor(int type) throws CursorNotFoundException {
        switch (type) {
            case ARROW: return Cursor.getDefaultCursor();
            case CROSSHAIR: return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
            case IBEAM: return Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
            case WAIT:
                if (OSInfo.IS_MAC) return Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR); // FIXME Cocoa
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
                if (OSInfo.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR); // Windows unsupported
                else if (OSInfo.IS_MAC) return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor(X11CursorSupport.XC_sb_v_double_arrow);
            case RESIZE_WE:
                if (OSInfo.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR); // Windows unsupported
                else if (OSInfo.IS_MAC) return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor(X11CursorSupport.XC_sb_h_double_arrow);
            case RESIZE_NWSE:
                if (OSInfo.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR); // Windows unsupported
                else if (OSInfo.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor("bd_double_arrow");
            case RESIZE_NESW:
                if (OSInfo.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR); // Windows unsupported
                else if (OSInfo.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor("fd_double_arrow");
            case RESIZE_COL:
                if (OSInfo.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR); // FIXME extract ole32.dll and bundle image
                else if (OSInfo.IS_MAC) return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor("col-resize");
            case RESIZE_ROW:
                if (OSInfo.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR); // FIXME extract ole32.dll and bundle image
                else if (OSInfo.IS_MAC) return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor("row-resize");
            case CELL:
                if (OSInfo.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME extract ole32.dll and bundle image
                else if (OSInfo.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor("plus");
            case HELP:
                if (OSInfo.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME read cursor file
                else if (OSInfo.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor(X11CursorSupport.XC_question_arrow);
            case ZOOM_IN:
                if (OSInfo.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME extract ole32.dll and bundle image
                else if (OSInfo.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor("zoom-in");
            case ZOOM_OUT:
                if (OSInfo.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME extract ole32.dll and bundle image
                else if (OSInfo.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor("zoom-out");
            case NO:
                if (OSInfo.IS_WINDOWS) return getSystemCustomCursor("Invalid.32x32"); // FIXME read cursor file
                else if (OSInfo.IS_MAC) return getDesktopPropertyCursor("DnD.Cursor.MoveNoDrop");
                else return X11CursorSupport.getSystemCursor("crossed_circle");
            case GRAB:
                if (OSInfo.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME extract ole32.dll and bundle image
                else if (OSInfo.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor(X11CursorSupport.XC_hand1);
            case GRABBING:
                if (OSInfo.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME extract ole32.dll and bundle image
                else if (OSInfo.IS_MAC) return getDesktopPropertyCursor("DnD.Cursor.MoveDrop");
                else return X11CursorSupport.getSystemCursor("grabbing");
            case COPY_DROP:
                if (OSInfo.IS_WINDOWS) return getSystemCustomCursor("CopyDrop.32x32"); // FIXME extract ole32.dll and bundle image
                else if (OSInfo.IS_MAC) return getDesktopPropertyCursor("DnD.Cursor.CopyDrop");
                else return X11CursorSupport.getSystemCursor("dnd-copy");
            case LINK_DROP:
                if (OSInfo.IS_WINDOWS) return getSystemCustomCursor("LinkDrop.32x32"); // FIXME extract ole32.dll and bundle image
                else if (OSInfo.IS_MAC) return getDesktopPropertyCursor("DnD.Cursor.LinkDrop");
                else return X11CursorSupport.getSystemCursor("dnd-link");
            case MOVE_DROP:
                if (OSInfo.IS_WINDOWS) return getSystemCustomCursor("MoveDrop.32x32"); // FIXME extract ole32.dll and bundle image
                else if (OSInfo.IS_MAC) return getDesktopPropertyCursor("DnD.Cursor.MoveDrop");
                else return X11CursorSupport.getSystemCursor("dnd-move");
            case NO_DROP:
                if (OSInfo.IS_WINDOWS) return getSystemCustomCursor("Invalid.32x32"); // FIXME read cursor file
                else if (OSInfo.IS_MAC) return getDesktopPropertyCursor("DnD.Cursor.MoveNoDrop");
                else return X11CursorSupport.getSystemCursor("dnd-no-drop");
            case UP_ARROW:
                if (OSInfo.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME read cursor file
                else if (OSInfo.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor(X11CursorSupport.XC_sb_up_arrow);
            case VERTICAL_IBEAM:
                if (OSInfo.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME read cursor file
                else if (OSInfo.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor("vertical-text");
            case CONTEXT_MENU:
                if (OSInfo.IS_WINDOWS) return Cursor.getDefaultCursor(); // FIXME extract ole32.dll and bundle image
                else if (OSInfo.IS_MAC) return Cursor.getDefaultCursor(); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor("context-menu");
            case PROGRESS:
                if (OSInfo.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR); // FIXME read cursor file
                else if (OSInfo.IS_MAC) return Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor("left_ptr_watch");
            case FLEUR:
                if (OSInfo.IS_WINDOWS) return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR); // Windows unsupported
                else if (OSInfo.IS_MAC) return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR); // FIXME Cocoa
                else return X11CursorSupport.getSystemCursor(X11CursorSupport.XC_fleur);
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
        if (OSInfo.IS_X11) return X11CursorSupport.createCustomCursor(cursor, hotSpot, name);
        else return Toolkit.getDefaultToolkit().createCustomCursor(cursor, hotSpot, name);
    }

    public static int getMaximumCursorColors() {
        if (OSInfo.IS_X11) return X11CursorSupport.getMaximumCursorColors();
        else return Toolkit.getDefaultToolkit().getMaximumCursorColors();
    }

}
