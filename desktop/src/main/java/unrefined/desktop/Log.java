/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package unrefined.desktop;

import unrefined.context.Environment;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import static unrefined.app.Log.Priority.*;

public final class Log {

    public static int v(String tag, String msg) {
        if (getPriority() > VERBOSE) return 0;
        else return println(VERBOSE, tag, msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (getPriority() > VERBOSE) return 0;
        else return println(VERBOSE, tag, msg + System.lineSeparator() + getStackTraceString(tr));
    }

    public static int v(String tag, Throwable tr) {
        if (getPriority() > VERBOSE) return 0;
        else return println(VERBOSE, tag, getStackTraceString(tr));
    }

    public static int d(String tag, String msg) {
        if (getPriority() > DEBUG) return 0;
        else return println(DEBUG, tag, msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (getPriority() > DEBUG) return 0;
        else return println(DEBUG, tag, msg + System.lineSeparator() + getStackTraceString(tr));
    }

    public static int d(String tag, Throwable tr) {
        if (getPriority() > DEBUG) return 0;
        else return println(DEBUG, tag, getStackTraceString(tr));
    }

    public static int i(String tag, String msg) {
        if (getPriority() > INFO) return 0;
        else return println(INFO, tag, msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (getPriority() > INFO) return 0;
        else return println(INFO, tag, msg + System.lineSeparator() + getStackTraceString(tr));
    }

    public static int i(String tag, Throwable tr) {
        if (getPriority() > INFO) return 0;
        else return println(INFO, tag, getStackTraceString(tr));
    }

    public static int w(String tag, String msg) {
        if (getPriority() > WARN) return 0;
        else return println(WARN, tag, msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (getPriority() > WARN) return 0;
        else return println(WARN, tag, msg + System.lineSeparator() + getStackTraceString(tr));
    }

    public static int w(String tag, Throwable tr) {
        if (getPriority() > WARN) return 0;
        else return println(WARN, tag, getStackTraceString(tr));
    }

    public static int e(String tag, String msg) {
        if (getPriority() > ERROR) return 0;
        else return println(ERROR, tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (getPriority() > ERROR) return 0;
        else return println(ERROR, tag, msg + System.lineSeparator() + getStackTraceString(tr));
    }

    public static int e(String tag, Throwable tr) {
        if (getPriority() > ERROR) return 0;
        else return println(ERROR, tag, getStackTraceString(tr));
    }

    public static int wtf(String tag, String msg) {
        return println(ASSERT, tag, msg);
    }

    public static int wtf(String tag, String msg, Throwable tr) {
        return println(ASSERT, tag, msg + System.lineSeparator() + getStackTraceString(tr));
    }

    public static int wtf(String tag, Throwable tr) {
        return println(ASSERT, tag, getStackTraceString(tr));
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) return "";
        else {
            StringWriter buffer = new StringWriter();
            PrintWriter out = new PrintWriter(buffer);
            tr.printStackTrace(out);
            out.flush();
            return buffer.toString();
        }
    }

    private static final String IDENTIFIER;
    static {
        StringBuilder builder = new StringBuilder();
        builder.append(RuntimeSupport.PID);
        builder.append('@');
        try {
            builder.append(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            builder.append("localhost");
        }
        IDENTIFIER = builder.toString();
    }

    private static final DateTimeFormatter LOG_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
    public static int println(int priority, String tag, String msg) {
        StringBuilder builder = new StringBuilder(LOG_DATE_TIME.format(LocalDateTime.now())).append(' ');
        builder.append(IDENTIFIER);
        builder.append(' ');
        switch (priority) {
            case VERBOSE: builder.append('V'); break;
            case DEBUG: builder.append('D'); break;
            case INFO: builder.append('I'); break;
            case WARN: builder.append('W'); break;
            case ERROR: builder.append('E'); break;
            case ASSERT: builder.append('A'); break;
            default: throw new IllegalArgumentException("Illegal priority: " + priority);
        }
        builder.append('/');
        builder.append(tag);
        builder.append(':').append(' ');
        builder.append(msg);
        String log = builder.toString();
        if (priority == ASSERT && Environment.properties.parseBooleanProperty("unrefined.desktop.log.assert"))
            throw new AssertionError(log);
        //else System.err.println(log);
        else if (priority > WARN) System.err.println(log);
        else System.out.println(log);
        return log.length();
    }

    public static final int DEFAULT_PRIORITY = INFO;

    private static final AtomicInteger PRIORITY = new AtomicInteger(DEFAULT_PRIORITY);
    public static void setPriority(int priority) {
        PRIORITY.set(checkValid(priority));
    }

    public static int getPriority() {
        try {
            int priority = Environment.properties.parseIntProperty("unrefined.desktop.log.priority", PRIORITY.get());
            if (isValid(priority)) return priority;
        }
        catch (NumberFormatException ignored) {
        }
        return PRIORITY.get();
    }

}

