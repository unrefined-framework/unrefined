package org.example.application.snake.desktop;

import org.example.application.snake.Constant;
import org.example.application.snake.Snake;
import unrefined.runtime.DesktopContainer;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.signal.Dispatcher;

import java.awt.Frame;

public class Main {

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);

        DesktopContainer container = new DesktopContainer(new Snake());
        Frame frame = container.getFrame();
        frame.setTitle("Snake");
        frame.setMinimumSize(null);
        frame.setSize(Constant.WIDTH, Constant.HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        Dispatcher.defaultInstance().invokeLater(container);
    }

}
