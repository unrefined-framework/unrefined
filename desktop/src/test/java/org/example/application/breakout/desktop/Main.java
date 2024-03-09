package org.example.application.breakout.desktop;

import org.example.application.breakout.Breakout;
import org.example.application.breakout.Constant;
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
        frame.setMinimumSize(null);
        frame.setSize(Constant.WIDTH, Constant.HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        Dispatcher.defaultInstance().invokeLater(container);
    }

}
