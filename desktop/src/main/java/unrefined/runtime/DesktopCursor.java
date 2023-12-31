package unrefined.runtime;

import unrefined.desktop.CursorSupport;
import unrefined.media.graphics.Bitmap;
import unrefined.media.graphics.CursorNotFoundException;
import unrefined.util.UnexpectedError;

import java.awt.Cursor;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DesktopCursor extends unrefined.media.graphics.Cursor {

    private final Cursor cursor;
    private final int type;
    private final int hotSpotX;
    private final int hotSpotY;

    private static final Map<Integer, DesktopCursor> SYSTEM_CURSORS = new HashMap<>();
    public static DesktopCursor getSystemCursor(int type) throws CursorNotFoundException {
        if (!SYSTEM_CURSORS.containsKey(type)) {
            synchronized (SYSTEM_CURSORS) {
                if (!SYSTEM_CURSORS.containsKey(type)) {
                    try {
                        SYSTEM_CURSORS.put(type, new DesktopCursor(type));
                    } catch (CursorNotFoundException e) {
                        throw e;
                    } catch (Throwable e) {
                        throw new UnexpectedError(e);
                    }
                }
            }
        }
        return SYSTEM_CURSORS.get(type);
    }

    private static final DesktopCursor DEFAULT_CURSOR;
    static {
        try {
            DEFAULT_CURSOR = getSystemCursor(Type.ARROW);
        } catch (CursorNotFoundException e) {
            throw new UnexpectedError(e);
        }
    }

    public static DesktopCursor getDefaultCursor() {
        return DEFAULT_CURSOR;
    }
    
    private DesktopCursor(int type) throws CursorNotFoundException {
        this.cursor = CursorSupport.getSystemCursor(type);
        this.type = type;
        this.hotSpotX = -1;
        this.hotSpotY = -1;
    }

    public DesktopCursor(Bitmap bitmap, Point hotSpot) {
        this.cursor = CursorSupport.createCustomCursor(((DesktopBitmap) bitmap).getBufferedImage(), hotSpot, "UXGL Image Cursor");
        this.type = Type.CUSTOM;
        this.hotSpotX = hotSpot.x;
        this.hotSpotY = hotSpot.y;
    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getHotSpotX() {
        return hotSpotX;
    }

    @Override
    public int getHotSpotY() {
        return hotSpotY;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopCursor that = (DesktopCursor) object;

        if (type != that.type) return false;
        if (hotSpotX != that.hotSpotX) return false;
        if (hotSpotY != that.hotSpotY) return false;
        return Objects.equals(cursor, that.cursor);
    }

    @Override
    public int hashCode() {
        int result = cursor != null ? cursor.hashCode() : 0;
        result = 31 * result + type;
        result = 31 * result + hotSpotX;
        result = 31 * result + hotSpotY;
        return result;
    }

}
