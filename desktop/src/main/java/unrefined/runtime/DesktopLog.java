package unrefined.runtime;

import unrefined.app.Log;

public class DesktopLog extends Log {

    @Override
    public int verbose(String tag, String message) {
        return unrefined.desktop.Log.v(tag, message);
    }

    @Override
    public int verbose(String tag, String message, Throwable throwable) {
        return unrefined.desktop.Log.v(tag, message, throwable);
    }

    @Override
    public int verbose(String tag, Throwable throwable) {
        return unrefined.desktop.Log.v(tag, throwable);
    }

    @Override
    public int debug(String tag, String message) {
        return unrefined.desktop.Log.d(tag, message);
    }

    @Override
    public int debug(String tag, String message, Throwable throwable) {
        return unrefined.desktop.Log.d(tag, message, throwable);
    }

    @Override
    public int debug(String tag, Throwable throwable) {
        return unrefined.desktop.Log.d(tag, throwable);
    }

    @Override
    public int info(String tag, String message) {
        return unrefined.desktop.Log.i(tag, message);
    }

    @Override
    public int info(String tag, String message, Throwable throwable) {
        return unrefined.desktop.Log.i(tag, message, throwable);
    }

    @Override
    public int info(String tag, Throwable throwable) {
        return unrefined.desktop.Log.i(tag, throwable);
    }

    @Override
    public int warn(String tag, String message) {
        return unrefined.desktop.Log.w(tag, message);
    }

    @Override
    public int warn(String tag, String message, Throwable throwable) {
        return unrefined.desktop.Log.w(tag, message, throwable);
    }

    @Override
    public int warn(String tag, Throwable throwable) {
        return unrefined.desktop.Log.w(tag, throwable);
    }

    @Override
    public int error(String tag, String message) {
        return unrefined.desktop.Log.e(tag, message);
    }

    @Override
    public int error(String tag, String message, Throwable throwable) {
        return unrefined.desktop.Log.e(tag, message, throwable);
    }

    @Override
    public int error(String tag, Throwable throwable) {
        return unrefined.desktop.Log.e(tag, throwable);
    }

    @Override
    public int assertion(String tag, String message) {
        return unrefined.desktop.Log.wtf(tag, message);
    }

    @Override
    public int assertion(String tag, String message, Throwable throwable) {
        return unrefined.desktop.Log.wtf(tag, message, throwable);
    }

    @Override
    public int assertion(String tag, Throwable throwable) {
        return unrefined.desktop.Log.wtf(tag, throwable);
    }

    @Override
    public String getStackTraceString(Throwable throwable) {
        return unrefined.desktop.Log.getStackTraceString(throwable);
    }

    @Override
    public int println(int priority, String tag, String message) {
        return unrefined.desktop.Log.println(priority, tag, message);
    }

    @Override
    public void setPriority(int priority) {
        unrefined.desktop.Log.setPriority(priority);
    }

    @Override
    public int getPriority() {
        return unrefined.desktop.Log.getPriority();
    }

}
