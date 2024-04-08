package unrefined.io.console;

import unrefined.context.Environment;
import unrefined.io.AlreadyUsedException;
import unrefined.media.graphics.Dimension;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.signal.Signal;

import java.io.FileDescriptor;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

public abstract class Console {

    public static final String SIGINT = "SIGINT";
    public static final String SIGTERM = "SIGTERM";
    public static final String SIGABRT = "SIGABRT";

    private static volatile Console INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Console getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.console", Console.class);
        }
        return INSTANCE;
    }

    private final Signal<EventSlot<SignalEvent>> onSignal = Signal.ofSlot();
    public Signal<EventSlot<SignalEvent>> onSignal() {
        return onSignal;
    }

    public abstract void handle(String signal) throws AlreadyUsedException, SignalNotFoundException;
    public abstract void ignore(String signal) throws AlreadyUsedException, SignalNotFoundException;
    public abstract void reset(String signal) throws AlreadyUsedException, SignalNotFoundException;
    public abstract void raise(String signal) throws UnhandledSignalException;

    public InputStream stdin() {
        return System.in;
    }

    private static final ConsoleStream stdout = new ConsoleStream(FileDescriptor.out);

    public ConsoleStream stdout() {
        return stdout;
    }

    private static final ConsoleStream stderr = new ConsoleStream(FileDescriptor.err);

    public ConsoleStream stderr() {
        return stderr;
    }

    public abstract boolean isTerminal();
    public abstract int getTerminalWidth();
    public abstract int getTerminalHeight();
    public abstract void getTerminalSize(Dimension dimension);

    public abstract int getAnsiType();
    public abstract int getMaximumColors();
    public abstract Charset getCharset();

    public abstract void setAnsiMode(int mode);
    public abstract int getAnsiMode();

    public static class SignalEvent extends Event<Console> {
        private final String signal;
        private final int identifier;
        public SignalEvent(Console source, String signal, int identifier) {
            super(source);
            this.signal = Objects.requireNonNull(signal);
            this.identifier = identifier;
        }
        public String getSignal() {
            return signal;
        }
        public int getIdentifier() {
            return identifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            SignalEvent that = (SignalEvent) o;

            if (identifier != that.identifier) return false;
            return signal.equals(that.signal);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + signal.hashCode();
            result = 31 * result + identifier;
            return result;
        }

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "signal='" + signal + '\'' +
                    ", identifier=" + identifier +
                    '}';
        }

    }

}
