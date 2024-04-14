package org.example.application.snake.desktop;

import org.example.application.snake.Snake;
import unrefined.Lifecycle;
import unrefined.runtime.DesktopContainer;

import java.awt.Frame;

public class Main {

    public static void main(String[] args) {
        Lifecycle.onMain(args);

        DesktopContainer container = new DesktopContainer(new Snake());

        Frame frame = container.getFrame();
        frame.setTitle("Snake");
        frame.setResizable(false);

        container.requestLaunch(() -> frame.setLocationRelativeTo(null));
    }

}
