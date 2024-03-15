package org.example.helloworld.desktop;

import org.example.helloworld.HelloWorldContainer;
import unrefined.runtime.DesktopContainer;
import unrefined.runtime.DesktopRuntime;

import java.awt.Frame;

/**
 * This is the main entry point of the application.
 */
public class Main {

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);

        DesktopContainer container = new DesktopContainer(new HelloWorldContainer());

        Frame frame = container.getFrame();
        frame.setTitle("Unrefined Application");

        container.requestLaunch(() -> frame.setLocationRelativeTo(null));
    }

}
