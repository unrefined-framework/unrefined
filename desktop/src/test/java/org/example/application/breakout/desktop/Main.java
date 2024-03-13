package org.example.application.breakout.desktop;

import org.example.application.breakout.Breakout;
import unrefined.runtime.DesktopContainer;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.signal.Dispatcher;

import java.awt.Frame;

public class Main {

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);

        DesktopContainer container = new DesktopContainer(new Breakout());

        Frame frame = container.getFrame();
        frame.setTitle("Breakout");
        frame.setResizable(false);

        container.requestLaunch(() -> frame.setLocationRelativeTo(null));
    }

}
