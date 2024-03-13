package org.example.application.snake.desktop;

import org.example.application.snake.Snake;
import unrefined.runtime.DesktopContainer;
import unrefined.runtime.DesktopRuntime;

import java.awt.Frame;

public class Main {

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);

        DesktopContainer container = new DesktopContainer(new Snake());

        Frame frame = container.getFrame();
        frame.setTitle("Snake");
        frame.setResizable(false);

        container.requestLaunch(() -> frame.setLocationRelativeTo(null));
    }

}
