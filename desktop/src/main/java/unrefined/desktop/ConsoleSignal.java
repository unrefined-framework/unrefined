package unrefined.desktop;

import sun.misc.Signal;
import sun.misc.SignalHandler;
import unrefined.io.AlreadyUsedException;
import unrefined.io.console.SignalNotFoundException;
import unrefined.io.console.UnhandledSignalException;
import unrefined.util.NotInstantiableError;
import unrefined.util.concurrent.ConcurrentHashSet;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ConsoleSignal {

    private ConsoleSignal() {
        throw new NotInstantiableError(ConsoleSignal.class);
    }

    private static final Set<Handler> CONSOLE_SIGNAL_HANDLERS = new ConcurrentHashSet<>();

    private static final SignalHandler NATIVE_HANDLER = sig -> {
        synchronized (CONSOLE_SIGNAL_HANDLERS) {
            for (Handler handler : CONSOLE_SIGNAL_HANDLERS) {
                handler.handle("SIG" + sig.getName(), sig.getNumber());
            }
        }
    };

    public static void addConsoleSignalHandler(Handler handler) {
        CONSOLE_SIGNAL_HANDLERS.add(handler);
    }

    public static void removeConsoleSignalHandler(Handler handler) {
        CONSOLE_SIGNAL_HANDLERS.remove(handler);
    }

    public static void clearConsoleSignalHandlers() {
        CONSOLE_SIGNAL_HANDLERS.clear();
    }

    private static final Map<String, Signal> SIGNALS = new HashMap<>();

    private static void handle0(String signal, SignalHandler handler) throws SignalNotFoundException, AlreadyUsedException {
        Objects.requireNonNull(signal);
        signal = signal.toUpperCase(Locale.ENGLISH);
        if (signal.startsWith("SIG")) signal = signal.substring(3);
        synchronized (SIGNALS) {
            if (!SIGNALS.containsKey(signal)) {
                final Signal sig;
                try {
                    sig = new Signal(signal);
                }
                catch (IllegalArgumentException e) {
                    if (!signal.startsWith("SIG")) signal = "SIG" + signal;
                    throw new SignalNotFoundException(signal);
                }
                SIGNALS.put(signal, sig);
                try {
                    Signal.handle(sig, handler);
                }
                catch (IllegalArgumentException e) {
                    if (!signal.startsWith("SIG")) signal = "SIG" + signal;
                    throw new AlreadyUsedException("Signal already used by VM or OS: " + signal + " (" + sig.getNumber() + ")");
                }
            }
        }
    }

    public static void handle(String signal) throws AlreadyUsedException, SignalNotFoundException {
        handle0(signal, NATIVE_HANDLER);
    }

    public static void reset(String signal) throws AlreadyUsedException, SignalNotFoundException {
        handle0(signal, SignalHandler.SIG_DFL);
    }

    public static void ignore(String signal) throws AlreadyUsedException, SignalNotFoundException {
        handle0(signal, SignalHandler.SIG_IGN);
    }

    public static void raise(String signal) throws UnhandledSignalException {
        Objects.requireNonNull(signal);
        signal = signal.toUpperCase(Locale.ENGLISH);
        if (signal.startsWith("SIG")) signal = signal.substring(3);
        synchronized (SIGNALS) {
            Signal sig = SIGNALS.get(signal);
            if (sig == null) {
                if (!signal.startsWith("SIG")) signal = "SIG" + signal;
                throw new UnhandledSignalException(signal);
            }
            else {
                try {
                    Signal.raise(sig);
                }
                catch (IllegalArgumentException e) {
                    if (!signal.startsWith("SIG")) signal = "SIG" + signal;
                    throw new UnhandledSignalException(signal);
                }
            }
        }
    }

    public interface Handler {
        void handle(String signal, int identifier);
    }

}
