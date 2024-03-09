package org.example.desktop;

import unrefined.io.console.Ansi;
import unrefined.io.console.Console;
import unrefined.runtime.DesktopRuntime;

public class AnsiPrint {

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);

        Console console = Console.getInstance();

        console.stdout().println(new Ansi().fgBlue().append("Hello Ansi").reset());
    }

}
