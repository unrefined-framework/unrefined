package unrefined.io.console;

import unrefined.context.Environment;
import unrefined.io.AlreadyUsedException;
import unrefined.util.event.Event;
import unrefined.util.event.EventSlot;
import unrefined.util.signal.Signal;

import java.util.Objects;

public abstract class Console {

    private static volatile Console INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Console getConsole() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global().get("unrefined.runtime.console", Console.class);
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
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;

            SignalEvent that = (SignalEvent) object;

            if (identifier != that.identifier) return false;
            return signal.equals(that.signal);
        }
        @Override
        public int hashCode() {
            int result = signal.hashCode();
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
