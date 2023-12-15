package unrefined.internal;

import unrefined.desktop.CursorFactory;
import unrefined.desktop.ReflectionSupport;
import unrefined.runtime.AnimatedCursor;
import unrefined.runtime.DesktopCursor;
import unrefined.util.NotInstantiableError;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public final class CursorUtils {

    private CursorUtils() {
        throw new NotInstantiableError(CursorUtils.class);
    }

    private static final Method setPDataMethod;
    static {
        Method method;
        try {
            method = Cursor.class.getDeclaredMethod("setPData", long.class);
        }
        catch (NoSuchMethodException e) {
            method = null;
        }
        setPDataMethod = method;
    }

    public static boolean setCursorPData(Cursor cursor, long pData) {
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

    private static volatile Timer TIMER;
    private static final Object TIMER_LOCK = new Object();

    private static unrefined.media.graphics.Cursor[] CURSORS;
    private static long[] DURATIONS;
    private static int INDEX;
    private static Component COMPONENT;
    public static void registerAnimated(Component component, AnimatedCursor cursor) {
        if (GraphicsEnvironment.isHeadless()) return;
        if (cursor.getCursors().length == 0) component.setCursor(CursorFactory.NONE_CURSOR);
        else {
            synchronized (TIMER_LOCK) {
                CursorUtils.COMPONENT = component;
                if (TIMER != null) TIMER.cancel();
                TIMER = new Timer("UXGL Cursor Animator", true);
                CURSORS = cursor.getCursors().clone();
                DURATIONS = cursor.getDurations().clone();
                INDEX = 0;
                component.setCursor(((DesktopCursor) CURSORS[INDEX]).getCursor());
                schedule(component);
            }
        }
    }

    public static void unregisterAnimated(Component component) {
        if (GraphicsEnvironment.isHeadless()) return;
        synchronized (TIMER_LOCK) {
            if (CursorUtils.COMPONENT != component) return;
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
