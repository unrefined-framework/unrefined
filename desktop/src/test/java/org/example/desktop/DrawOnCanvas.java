package org.example.desktop;

import unrefined.context.Container;
import unrefined.context.ContainerAdapter;
import unrefined.context.Context;
import unrefined.context.ContextAdapter;
import unrefined.media.graphics.Brush;
import unrefined.media.graphics.Color;
import unrefined.media.graphics.Drawing;
import unrefined.media.graphics.Graphics;
import unrefined.media.graphics.Insets;
import unrefined.runtime.DesktopApplication;
import unrefined.runtime.DesktopRuntime;

import java.awt.EventQueue;

/**
 * After create and add, then you can draw content to the canvas, and it will show to user.
 * Let's fill the canvas with "plain black".
 */
public class DrawOnCanvas {

    public static void main(String[] args) {
        DesktopRuntime.setup(args);             // Initialize the UXGL runtime environment

        // Initialize the UXGL container (on desktop it's a window)
        DesktopApplication application = new DesktopApplication(new ContainerAdapter() { // The lifecycle listener, it's fully platform-independent

            private Context canvas;
            private Brush black;
            private Insets insets;

            @Override
            public void onCreate(Container container) {
                // The container is created, but not visible
                // GUI-related attributes, such as window insets, not correct here
                // You can set the container's attributes, or allocate resources here

                insets = new Insets();

                Drawing drawing = Drawing.getInstance();  // Get the platform-dependent 2D factory instance
                black = drawing.createBrush(Color.BLACK); // Create the "plain black" brush

                canvas = container.createContext(new ContextAdapter() { // The lifecycle listener, it's fully platform-independent
                    @Override
                    public void onPaint(Context context, Graphics graphics, boolean snapshot) {
                        graphics.setBrush(black);         // Use the "plain black" brush
                        graphics.drawColor();             // Draw the full canvas area
                    }
                });

                container.setBackgroundColor(Color.BLUE); // Set the background color
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
            }

            @Override
            public boolean onCloseRequest(Container container) {
                return true; // Whether close the window when the user clicks the close button, false is not close
            }
        });

        EventQueue.invokeLater(application); // Launch the container
    }

}
