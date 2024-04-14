package org.example.helloworld.desktop;

import org.example.helloworld.HelloWorldContainerAdapter;
import unrefined.Lifecycle;
import unrefined.runtime.DesktopContainer;

import java.awt.Frame;

/**
 * This is the main entry point of the application.
 */
public class Main {

    public static void main(String[] args) {
        Lifecycle.onMain(args);

        DesktopContainer container = new DesktopContainer(new HelloWorldContainerAdapter());

        Frame frame = container.getFrame();
        frame.setTitle("Unrefined Application");

        container.requestLaunch(() -> frame.setLocationRelativeTo(null));
    }

}
