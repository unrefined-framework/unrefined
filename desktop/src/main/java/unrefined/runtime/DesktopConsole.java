package unrefined.runtime;

import unrefined.desktop.ConsoleSignal;
import unrefined.desktop.ConsoleSupport;
import unrefined.io.AlreadyUsedException;
import unrefined.io.console.Console;
import unrefined.io.console.SignalNotFoundException;
import unrefined.io.console.UnhandledSignalException;
import unrefined.media.graphics.Dimension;

import java.nio.charset.Charset;

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

    @Override
    public boolean isTerminal() {
        return ConsoleSupport.IS_TERMINAL;
    }

    @Override
    public int getTerminalWidth() {
        return ConsoleSupport.SIZE_PRODUCER.width();
    }

    @Override
    public int getTerminalHeight() {
        return ConsoleSupport.SIZE_PRODUCER.height();
    }

    @Override
    public void getTerminalSize(Dimension dimension) {
        ConsoleSupport.SIZE_PRODUCER.size(dimension);
    }

    @Override
    public int getAnsiType() {
        return ConsoleSupport.ANSI_TYPE;
    }

    @Override
    public int getMaximumColors() {
        return ConsoleSupport.MAX_COLORS;
    }

    @Override
    public Charset getCharset() {
        return ConsoleSupport.CHARSET;
    }

    @Override
    public void setAnsiMode(int mode) {
        ConsoleSupport.setAnsiMode(mode);
    }

    @Override
    public int getAnsiMode() {
        return ConsoleSupport.getAnsiMode();
    }

}
