package unrefined.runtime;

import unrefined.media.graphics.Cursor;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnimatedCursor extends Cursor {

    private volatile Cursor[] cursors;
    private final long[] durations;

    public AnimatedCursor(Cursor[] cursors, int cursorsOffset, long[] durations, int durationsOffset, int length) {
        if (length < 1) throw new IllegalArgumentException("Not an animated cursor");
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

    private final AtomicBoolean disposed = new AtomicBoolean(false);

    @Override
    public void dispose() {
        if (disposed.compareAndSet(false, true)) {
            for (Cursor cursor : cursors) {
                cursor.dispose();
            }
            cursors = null;
        }
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
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
        if (isDisposed()) return getClass().getName() + "@" + Integer.toHexString(hashCode())
                + '{' +
                "disposed=true" +
                '}';
        else return getClass().getName()
                + '{' +
                "disposed=false" +
                ", type=CUSTOM" +
                '}';
    }

}
