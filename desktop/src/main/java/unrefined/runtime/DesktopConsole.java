package unrefined.runtime;

import unrefined.desktop.ConsoleSignal;
import unrefined.io.AlreadyUsedException;
import unrefined.io.console.Console;
import unrefined.io.console.SignalNotFoundException;
import unrefined.io.console.UnhandledSignalException;

public class DesktopConsole extends Console {

    public DesktopConsole() {
        ConsoleSignal.addConsoleSignalHandler((signal, identifier) ->
                onSignal().emit(new SignalEvent(DesktopConsole.this, signal, identifier)));
    }

    @Override
    public void handle(String signal) throws AlreadyUsedException, SignalNotFoundException {
        ConsoleSignal.handle(signal);
    }

    @Override
    public void ignore(String signal) throws AlreadyUsedException, SignalNotFoundException {
        ConsoleSignal.ignore(signal);
    }

    @Override
    public void reset(String signal) throws AlreadyUsedException, SignalNotFoundException {
        ConsoleSignal.reset(signal);
    }

    @Override
    public void raise(String signal) throws UnhandledSignalException {
        ConsoleSignal.raise(signal);
    }

}
