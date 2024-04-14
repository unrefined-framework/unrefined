package org.example.application.breakout.desktop;

import org.example.application.breakout.Breakout;
import unrefined.Lifecycle;
import unrefined.runtime.DesktopContainer;

import java.awt.Frame;

public class Main {

    public static void main(String[] args) {
        Lifecycle.onMain(args);

        DesktopContainer container = new DesktopContainer(new Breakout());

        Frame frame = container.getFrame();
        frame.setTitle("Breakout");
        frame.setResizable(false);

        container.requestLaunch(() -> frame.setLocationRelativeTo(null));
    }

}
