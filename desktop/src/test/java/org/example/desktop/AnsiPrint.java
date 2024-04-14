package org.example.desktop;

import unrefined.Lifecycle;
import unrefined.io.console.Ansi;
import unrefined.io.console.Console;

public class AnsiPrint {

    public static void main(String[] args) {
        Lifecycle.onMain(args);

        Console console = Console.getInstance();

        console.stdout().println(new Ansi().fgBlue().append("Hello Ansi").reset());
    }

}
