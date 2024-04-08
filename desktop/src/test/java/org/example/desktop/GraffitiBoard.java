package org.example.desktop;

import unrefined.context.Container;
import unrefined.context.ContainerAdapter;
import unrefined.context.Context;
import unrefined.context.ContextAdapter;
import unrefined.media.graphics.Bitmap;
import unrefined.media.graphics.Brush;
import unrefined.media.graphics.Color;
import unrefined.media.graphics.Cursor;
import unrefined.media.graphics.CursorNotFoundException;
import unrefined.media.graphics.Drawing;
import unrefined.media.graphics.Graphics;
import unrefined.media.graphics.Insets;
import unrefined.media.graphics.Path;
import unrefined.runtime.DesktopContainer;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.UnexpectedError;
import unrefined.util.signal.Dispatcher;

import java.awt.Dimension;

/**
 * This class creates a simple graffiti board, with a white background and a "plain black" brush.
 */
public class GraffitiBoard {

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);             // Initialize the Unrefined runtime environment

        // Initialize the Unrefined container (on desktop it's a window)
        DesktopContainer container = new DesktopContainer(new ContainerAdapter() { // The lifecycle listener, it's fully platform-independent

            private Context canvas;
            private Brush black;
            private Insets insets;
            private Path path;
            private Bitmap buffer;

            @Override
            public void onCreate(Container container) {
                // The container is created, but not visible
                // GUI-related attributes, such as window insets, not correct here
                // You can set the container's attributes, or allocate resources here

                insets = new Insets();

                Drawing drawing = Drawing.getInstance();  // Get the platform-dependent 2D factory instance
                black = drawing.createBrush(Color.BLACK); // Create the "plain black" brush

                Cursor pointing;
                try {
                    pointing = drawing.getCursor(Cursor.Type.POINTING_HAND); // Get the "pointing hand" cursor
                } catch (CursorNotFoundException e) {
                    throw new UnexpectedError(e);
                }
                container.setCursor(pointing);

                path = drawing.createPath();              // Create the path

                canvas = container.createContext(new ContextAdapter() { // The lifecycle listener, it's fully platform-independent
                    @Override
                    public void onCreate(Context context) {
                        context.setBackgroundColor(Color.WHITE);
                    }
                    @Override
                    public void onResize(Context context, int width, int height) {
                        if (buffer == null || width > buffer.getWidth() || height > buffer.getHeight()) { // Refresh the buffer
                            if (width < 1 || height < 1) return;
                            Bitmap extended = drawing.createBitmap(width, height, Bitmap.Type.RGBA_8888);
                            if (buffer != null) {
                                extended.getGraphics().drawBitmap(buffer);
                                buffer.dispose();
                            }
                            buffer = extended;
                        }
                    }
                    @Override
                    public void onPaint(Context context, Graphics graphics, boolean snapshot) {
                        if (buffer == null) return;
                        graphics.drawBitmap(buffer, 0, 0);
                        graphics.setBrush(black);                 // Use the "plain black" brush
                        graphics.setStyle(Graphics.Style.STROKE); // Set the style to STROKE
                        graphics.setStrokeWidth(2f);              // Set stroke width to 2 px
                        graphics.drawPath(path);                  // Draw the path
                    }
                    @Override
                    public boolean onPointerDown(Context context, float xOffset, float yOffset, int modifiers, int id, int button) {
                        path.moveTo(xOffset, yOffset);
                        return true;
                    }
                    @Override
                    public boolean onPointerDrag(Context context, float xOffset, float yOffset, int modifiers, int id) {
                        path.lineTo(xOffset, yOffset);
                        context.requestPaint();                   // Call onPaint()
                        return true;
                    }
                    @Override
                    public boolean onPointerUp(Context context, float xOffset, float yOffset, int modifiers, int id, int button) {
                        Graphics graphics = buffer.getGraphics();
                        graphics.setBrush(black);                 // Use the "plain black" brush
                        graphics.setStyle(Graphics.Style.STROKE); // Set the style to STROKE
                        graphics.setStrokeWidth(2f);              // Set stroke width to 2 px
                        graphics.drawPath(path);                  // Draw the path
                        path.reset();
                        context.requestPaint();                   // Call onPaint()
                        return true;
                    }
                });
                canvas.setSize(640, 480);

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

                buffer.dispose();
            }

            @Override
            public boolean onCloseRequest(Container container) {
                return true; // Whether close the window when the user clicks the close button, false is not close
            }
        });

        Dispatcher.defaultInstance().invokeLater(container); // Launch the container
    }

}
