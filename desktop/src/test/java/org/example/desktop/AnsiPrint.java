package org.example.desktop;

import unrefined.Lifecycle;
import unrefined.io.console.Ansi;
import unrefined.io.console.Console;

import java.io.IOException;

public class AnsiPrint {

    public static void main(String[] args) throws IOException {
        Lifecycle.onMain(args);

        Console console = Console.getInstance();
        console.stdout().println("Ansi type: " + Ansi.Type.toString(console.getAnsiType()));
        console.stdout().println("Ansi mode: " + Ansi.Mode.toString(console.getAnsiMode()));
        console.stdout().println(new Ansi().fgBlue().append("Hello Ansi").reset());
    }

}
