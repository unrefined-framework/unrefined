package org.example.desktop;

import unrefined.Lifecycle;
import unrefined.io.console.Console;

public class ConsoleTest {

    public static void main(String[] args) {
        Lifecycle.onMain(args);

        Console console = Console.getInstance();
        console.stdout().println(console.getTerminalWidth());
        console.stdout().println(console.getTerminalHeight());
    }

}
