package unrefined.runtime;

import unrefined.app.Logger;
import unrefined.desktop.Log;

public class DesktopLogger extends Logger {

    @Override
    public int verbose(String tag, String message) {
        return Log.v(tag, message);
    }

    @Override
    public int verbose(String tag, String message, Throwable throwable) {
        return Log.v(tag, message, throwable);
    }

    @Override
    public int verbose(String tag, Throwable throwable) {
        return Log.v(tag, throwable);
    }

    @Override
    public int debug(String tag, String message) {
        return Log.d(tag, message);
    }

    @Override
    public int debug(String tag, String message, Throwable throwable) {
        return Log.d(tag, message, throwable);
    }

    @Override
    public int debug(String tag, Throwable throwable) {
        return Log.d(tag, throwable);
    }

    @Override
    public int info(String tag, String message) {
        return Log.i(tag, message);
    }

    @Override
    public int info(String tag, String message, Throwable throwable) {
        return Log.i(tag, message, throwable);
    }

    @Override
    public int info(String tag, Throwable throwable) {
        return Log.i(tag, throwable);
    }

    @Override
    public int warn(String tag, String message) {
        return Log.w(tag, message);
    }

    @Override
    public int warn(String tag, String message, Throwable throwable) {
        return Log.w(tag, message, throwable);
    }

    @Override
    public int warn(String tag, Throwable throwable) {
        return Log.w(tag, throwable);
    }

    @Override
    public int error(String tag, String message) {
        return Log.e(tag, message);
    }

    @Override
    public int error(String tag, String message, Throwable throwable) {
        return Log.e(tag, message, throwable);
    }

    @Override
    public int error(String tag, Throwable throwable) {
        return Log.e(tag, throwable);
    }

    @Override
    public int assertion(String tag, String message) {
        return Log.wtf(tag, message);
    }

    @Override
    public int assertion(String tag, String message, Throwable throwable) {
        return Log.wtf(tag, message, throwable);
    }

    @Override
    public int assertion(String tag, Throwable throwable) {
        return Log.wtf(tag, throwable);
    }

    @Override
    public String getStackTraceString(Throwable throwable) {
        return Log.getStackTraceString(throwable);
    }

    @Override
    public int println(int priority, String tag, String message) {
        return Log.println(priority, tag, message);
    }

    @Override
    public void setPriority(int priority) {
        Log.setPriority(priority);
    }

    @Override
    public int getPriority() {
        return Log.getPriority();
    }

}
