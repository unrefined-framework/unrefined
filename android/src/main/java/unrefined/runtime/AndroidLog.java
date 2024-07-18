package unrefined.runtime;

import unrefined.app.Log;

public class AndroidLog extends Log {

    @Override
    public int verbose(String tag, String message) {
        return android.util.Log.v(tag, message);
    }

    @Override
    public int verbose(String tag, String message, Throwable throwable) {
        return android.util.Log.v(tag, message, throwable);
    }

    @Override
    public int verbose(String tag, Throwable throwable) {
        return android.util.Log.v(tag, android.util.Log.getStackTraceString(throwable));
    }

    @Override
    public int debug(String tag, String message) {
        return android.util.Log.d(tag, message);
    }

    @Override
    public int debug(String tag, String message, Throwable throwable) {
        return android.util.Log.d(tag, message, throwable);
    }

    @Override
    public int debug(String tag, Throwable throwable) {
        return android.util.Log.d(tag, android.util.Log.getStackTraceString(throwable));
    }

    @Override
    public int info(String tag, String message) {
        return android.util.Log.i(tag, message);
    }

    @Override
    public int info(String tag, String message, Throwable throwable) {
        return android.util.Log.i(tag, message, throwable);
    }

    @Override
    public int info(String tag, Throwable throwable) {
        return android.util.Log.i(tag, android.util.Log.getStackTraceString(throwable));
    }

    @Override
    public int warn(String tag, String message) {
        return android.util.Log.w(tag, message);
    }

    @Override
    public int warn(String tag, String message, Throwable throwable) {
        return android.util.Log.w(tag, message, throwable);
    }

    @Override
    public int warn(String tag, Throwable throwable) {
        return android.util.Log.w(tag, throwable);
    }

    @Override
    public int error(String tag, String message) {
        return android.util.Log.e(tag, message);
    }

    @Override
    public int error(String tag, String message, Throwable throwable) {
        return android.util.Log.e(tag, message, throwable);
    }

    @Override
    public int error(String tag, Throwable throwable) {
        return android.util.Log.e(tag, android.util.Log.getStackTraceString(throwable));
    }

    @Override
    public int assertion(String tag, String message) {
        return android.util.Log.wtf(tag, message);
    }

    @Override
    public int assertion(String tag, String message, Throwable throwable) {
        return android.util.Log.wtf(tag, message, throwable);
    }

    @Override
    public int assertion(String tag, Throwable throwable) {
        return android.util.Log.wtf(tag, throwable);
    }

    @Override
    public String getStackTraceString(Throwable throwable) {
        return android.util.Log.getStackTraceString(throwable);
    }

    @Override
    public int println(int priority, String tag, String message) {
        return android.util.Log.println(priority, tag, message);
    }

    @Override
    public void setPriority(int priority) {
    }

    @Override
    public int getPriority() {
        return Priority.VERBOSE;
    }

}
