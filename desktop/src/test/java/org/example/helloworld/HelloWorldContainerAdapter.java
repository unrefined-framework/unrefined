package org.example.helloworld;

import unrefined.app.Runtime;
import unrefined.context.Container;
import unrefined.context.ContainerAdapter;
import unrefined.context.Context;
import unrefined.media.graphics.Color;
import unrefined.media.graphics.Insets;

/**
 * This is the 'container' of canvases, on desktop is the application's root window.
 */
public class HelloWorldContainerAdapter extends ContainerAdapter {

    private static final int MINIMUM_WIDTH = 64;
    private static final int MINIMUM_HEIGHT = 48;
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    private Insets insets;
    private Context context;

    @Override
    public void onCreate(Container container) {
        insets = new Insets();

        context = Context.of(container, new HelloWorldContextAdapter());
        context.setBackgroundColor(Color.WHITE);
        context.setSize(WIDTH, HEIGHT);
        container.addContext(context);
    }

    @Override
    public void onDispose(Container container) {
        insets = null;

        Runtime.getInstance().exit(0);
    }

    @Override
    public void onStart(Container container) {
        container.setClientMinimumSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
    }

    @Override
    public void onResize(Container container, int width, int height) {
        container.getInsets(insets);
        context.setPosition(insets.getLeft(), insets.getTop());
        context.setSize(
                container.getWidth() - insets.getLeft() - insets.getRight(),
                container.getHeight() - insets.getTop() - insets.getBottom());
    }

}
