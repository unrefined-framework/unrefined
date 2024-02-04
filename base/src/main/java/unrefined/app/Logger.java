package unrefined.app;

import unrefined.context.Environment;
import unrefined.util.NotInstantiableError;

public abstract class Logger {

    private static volatile Logger DEFAULT_INSTANCE;
    private static final Object DEFAULT_INSTANCE_LOCK = new Object();
    public static Logger defaultInstance() {
        if (DEFAULT_INSTANCE == null) synchronized (DEFAULT_INSTANCE_LOCK) {
            if (DEFAULT_INSTANCE == null) DEFAULT_INSTANCE = Environment.global.get("unrefined.runtime.logger", Logger.class);
        }
        return DEFAULT_INSTANCE;
    }

    public static final class Priority {
        private Priority() {
            throw new NotInstantiableError(Priority.class);
        }
        public static final int VERBOSE = 2;
        public static final int DEBUG = 3;
        public static final int INFO = 4;
        public static final int WARN = 5;
        public static final int ERROR = 6;
        public static final int ASSERT = 7;
        public static int checkValid(int priority) {
            if (priority >= VERBOSE && priority <= ASSERT) return priority;
            else throw new IllegalArgumentException("Illegal priority: " + priority);
        }
        public static boolean isValid(int priority) {
            return priority >= VERBOSE && priority <= ASSERT;
        }
        public static String toString(int priority) {
            switch (priority) {
                case VERBOSE: return "VERBOSE";
                case DEBUG:   return "DEBUG";
                case INFO:    return "INFO";
                case WARN:    return "WARN";
                case ERROR:   return "ERROR";
                default: throw new IllegalArgumentException("Illegal priority: " + priority);
            }
        }
    }

    /**
     * Send a {@link Priority#VERBOSE} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public abstract int verbose(String tag, String message);

    /**
     * Send a {@link Priority#VERBOSE} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     * @param throwable An exception to log
     */
    public abstract int verbose(String tag, String message, Throwable throwable);

    /*
     * Send a {@link Priority#VERBOSE} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param throwable An exception to log
     */
    public abstract int verbose(String tag, Throwable throwable);

    /**
     * Send a {@link Priority#DEBUG} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public abstract int debug(String tag, String message);

    /**
     * Send a {@link Priority#DEBUG} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     * @param throwable An exception to log
     */
    public abstract int debug(String tag, String message, Throwable throwable);

    /**
     * Send a {@link Priority#DEBUG} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param throwable An exception to log
     */
    public abstract int debug(String tag, Throwable throwable);

    /**
     * Send an {@link Priority#INFO} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public abstract int info(String tag, String message);

    /**
     * Send a {@link Priority#INFO} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     * @param throwable An exception to log
     */
    public abstract int info(String tag, String message, Throwable throwable);

    /**
     * Send a {@link Priority#INFO} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param throwable An exception to log
     */
    public abstract int info(String tag, Throwable throwable);

    /**
     * Send a {@link Priority#WARN} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public abstract int warn(String tag, String message);

    /**
     * Send a {@link Priority#WARN} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     * @param throwable An exception to log
     */
    public abstract int warn(String tag, String message, Throwable throwable);

    /**
     * Send a {@link Priority#WARN} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param throwable An exception to log
     */
    public abstract int warn(String tag, Throwable throwable);

    /**
     * Send an {@link Priority#ERROR} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public abstract int error(String tag, String message);

    /**
     * Send a {@link Priority#ERROR} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     * @param throwable An exception to log
     */
    public abstract int error(String tag, String message, Throwable throwable);

    /**
     * Send a {@link Priority#ERROR} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param throwable An exception to log
     */
    public abstract int error(String tag, Throwable throwable);

    /**
     * Send an {@link Priority#ASSERT} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public abstract int assertion(String tag, String message);

    /**
     * Send a {@link Priority#ASSERT} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     * @param throwable An exception to log
     */
    public abstract int assertion(String tag, String message, Throwable throwable);

    /**
     * Send a {@link Priority#ASSERT} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param throwable An exception to log
     */
    public abstract int assertion(String tag, Throwable throwable);

    /**
     * Handy function to get a loggable stack trace from a Throwable
     * @param throwable An exception to log
     */
    public abstract String getStackTraceString(Throwable throwable);

    /**
     * Low-level logging call.
     * @param priority The priority/type of this log message
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     * @return The number of bytes written.
     */
    public abstract int println(int priority, String tag, String message);

    public abstract void setPriority(int priority);

    public abstract int getPriority();

}
