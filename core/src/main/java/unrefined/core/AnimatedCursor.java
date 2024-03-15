package unrefined.core;

import unrefined.media.graphics.Cursor;

import java.util.Arrays;

public class AnimatedCursor extends Cursor {

    private final Cursor[] cursors;
    private final long[] durations;

    public AnimatedCursor(Cursor[] cursors, int cursorsOffset, long[] durations, int durationsOffset, int length) {
        if (length < 1) throw new IllegalArgumentException("not an animated cursor");
        cursors = Arrays.copyOfRange(cursors, cursorsOffset, cursorsOffset + length);
        for (int i = 0; i < length; i ++) {
            if (cursors[i] instanceof AnimatedCursor) throw new IllegalArgumentException("Illegal cursor type; expected non-animated");
            else if (cursors[i].getType() != Type.CUSTOM) throw new IllegalArgumentException("Illegal cursor type; expected CUSTOM");
        }
        this.cursors = cursors;
        this.durations = Arrays.copyOfRange(durations, durationsOffset, durationsOffset + length);
    }

    public Cursor[] getCursors() {
        return cursors;
    }

    public long[] getDurations() {
        return durations;
    }

    @Override
    public int getType() {
        return Type.CUSTOM;
    }

    @Override
    public int getHotSpotX() {
        return -1;
    }

    @Override
    public int getHotSpotY() {
        return -1;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "type=CUSTOM" +
                '}';
    }

}
