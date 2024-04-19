package org.example.desktop;

import unrefined.Lifecycle;
import unrefined.context.Container;
import unrefined.context.ContainerAdapter;
import unrefined.context.Context;
import unrefined.context.ContextAdapter;
import unrefined.media.graphics.Brush;
import unrefined.media.graphics.Color;
import unrefined.media.graphics.Drawing;
import unrefined.media.graphics.Graphics;
import unrefined.media.graphics.Insets;
import unrefined.runtime.DesktopContainer;
import unrefined.util.signal.Dispatcher;

public class DropShadow {

    public static void main(String[] args) {
        Lifecycle.onMain(args);             // Initialize the Unrefined runtime environment

        // Initialize the Unrefined container (on desktop it's a window)
        DesktopContainer container = new DesktopContainer(new ContainerAdapter() { // The lifecycle listener, it's fully platform-independent

            private Context canvas;
            private Brush blue;
            private Insets insets;

            @Override
            public void onCreate(Container container) {
                // The container is created, but not visible
                // GUI-related attributes, such as window insets, not correct here
                // You can set the container's attributes, or allocate resources here

                insets = new Insets();

                Drawing drawing = Drawing.getInstance();  // Get the platform-dependent 2D factory instance
                blue = drawing.createBrush(Color.BLUE);   // Create the "plain blue" brush

                canvas = container.createContext(new ContextAdapter() { // The lifecycle listener, it's fully platform-independent
                    @Override
                    public void onCreate(Context context) {
                        context.setBackgroundColor(Color.WHITE);
                    }
                    @Override
                    public void onPaint(Context context, Graphics graphics, boolean snapshot) {
                        graphics.setShadowColor(Color.RED);
                        graphics.setShadowBlur(10);
                        graphics.setShadowOffset(20, 20);

                        graphics.setBrush(blue);

                        graphics.setStyle(Graphics.Style.STROKE);
                        graphics.drawRectangle(20, 20, 150, 100);

                        graphics.setStyle(Graphics.Style.FILL);
                        graphics.drawRectangle(210, 20, 150, 100);

                        graphics.setTextSize(64);
                        graphics.setShadowBlur(2);

                        graphics.setStyle(Graphics.Style.STROKE);
                        graphics.setStrokeWidth(1);
                        graphics.drawText("Text", 100, 300);

                        graphics.setStyle(Graphics.Style.FILL);
                        graphics.drawText("Text", 290, 300);

                    }
                });
                canvas.setSize(640, 480);

                container.setBackgroundColor(Color.WHITE); // Set the background color
                container.addContext(canvas);
            }

            @Override
            public void onStart(Container container) {
                container.getInsets(insets);                           // Get the window insets
                canvas.setPosition(insets.getLeft(), insets.getTop()); // Make the canvas fill the window's "client area"
                canvas.setSize(
                        container.getWidth() - insets.getLeft() - insets.getRight(),
                        container.getHeight() - insets.getTop() - insets.getBottom());
                canvas.requestPaint();                                 // Call onPaint()
            }

            @Override
            public void onResize(Container container, int width, int height) {
                container.getInsets(insets);                           // Get the window insets
                canvas.setPosition(insets.getLeft(), insets.getTop()); // Make the canvas fill the window's "client area"
                canvas.setSize(
                        container.getWidth() - insets.getLeft() - insets.getRight(),
                        container.getHeight() - insets.getTop() - insets.getBottom());

                canvas.requestPaint();                                 // Call onPaint()
            }

            @Override
            public void onDispose(Container container) {
                // The container is disposed
                // You can free resources here

                blue = null;
                insets = null;
            }

            @Override
            public boolean onCloseRequest(Container container) {
                return true; // Whether close the window when the user clicks the close button, false is not close
            }
        });

        Dispatcher.defaultInstance().invokeLater(container); // Launch the container
    }

}
