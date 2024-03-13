package org.example.application.breakout;

import unrefined.app.Runtime;
import unrefined.context.Container;
import unrefined.context.ContainerAdapter;
import unrefined.context.Context;
import unrefined.media.graphics.Color;
import unrefined.media.graphics.Insets;

public class Breakout extends ContainerAdapter {

    private Context canvas;
    private Insets insets;

    @Override
    public void onCreate(Container container) {
        container.setBackgroundColor(Color.WHITE);
        insets = new Insets();

        canvas = container.createContext(new Board());

        container.addContext(canvas);
        canvas.setSize(Constant.WIDTH, Constant.HEIGHT);
    }

    @Override
    public void onDispose(Container container) {
        Runtime.getInstance().exit(0);
    }

    private void resizeCanvas(Container container) {
        container.getInsets(insets);
        canvas.setPosition(insets.getLeft(), insets.getTop());
        canvas.setSize(
                container.getWidth() - insets.getLeft() - insets.getRight(),
                container.getHeight() - insets.getTop() - insets.getBottom());
    }

    @Override
    public void onStart(Container container) {
        canvas.requestFocus();
    }

    @Override
    public void onResize(Container container, int width, int height) {
        resizeCanvas(container);
    }

}
