package unrefined.desktop;

import unrefined.core.AnimatedCursor;
import unrefined.runtime.DesktopCursor;
import unrefined.util.NotInstantiableError;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.util.Timer;
import java.util.TimerTask;

public final class CursorAnimator {

    private CursorAnimator() {
        throw new NotInstantiableError(CursorAnimator.class);
    }

    private static volatile Timer TIMER;
    private static final Object TIMER_LOCK = new Object();

    private static unrefined.media.graphics.Cursor[] CURSORS;
    private static long[] DURATIONS;
    private static int INDEX;
    private static Component COMPONENT;
    public static void register(Component component, AnimatedCursor cursor) {
        if (GraphicsEnvironment.isHeadless()) return;
        if (cursor.getCursors().length == 0) component.setCursor(CursorSupport.NONE_CURSOR);
        else {
            synchronized (TIMER_LOCK) {
                CursorAnimator.COMPONENT = component;
                if (TIMER != null) TIMER.cancel();
                TIMER = new Timer("Unrefined Cursor Animator", true);
                CURSORS = cursor.getCursors().clone();
                DURATIONS = cursor.getDurations().clone();
                INDEX = 0;
                component.setCursor(((DesktopCursor) CURSORS[INDEX]).getCursor());
                schedule(component);
            }
        }
    }

    public static void unregister(Component component) {
        if (GraphicsEnvironment.isHeadless()) return;
        synchronized (TIMER_LOCK) {
            if (CursorAnimator.COMPONENT != component) return;
            if (TIMER != null) {
                TIMER.cancel();
                TIMER = null;
            }
        }
    }

    private static void schedule(Component component) {
        if (TIMER == null) return;
        TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                INDEX ++;
                if (INDEX >= CURSORS.length) INDEX = 0;
                component.setCursor(((DesktopCursor) CURSORS[INDEX]).getCursor());
                synchronized (TIMER_LOCK) {
                    schedule(component);
                }
            }
        }, DURATIONS[INDEX]);
    }

}
