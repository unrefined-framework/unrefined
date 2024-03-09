package org.example.desktop;

import unrefined.context.Container;
import unrefined.context.ContainerAdapter;
import unrefined.media.graphics.Color;
import unrefined.media.graphics.Cursor;
import unrefined.media.graphics.CursorNotFoundException;
import unrefined.media.graphics.Drawing;
import unrefined.runtime.DesktopContainer;
import unrefined.runtime.DesktopRuntime;
import unrefined.util.UnexpectedError;
import unrefined.util.signal.Dispatcher;

/**
 * One of the main features of Unrefined is creating "canvas" and draw on it. Before doing any drawing, you need to create a container to show the "canvas".
 * Let's create and change its cursor to the system "wait" cursor.
 */
public class CreateContainer {

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);             // Initialize the Unrefined runtime environment

        // Initialize the Unrefined container (on desktop it's a window)
        DesktopContainer container = new DesktopContainer(new ContainerAdapter() { // The lifecycle listener, it's fully platform-independent

            @Override
            public void onCreate(Container container) {
                // The container is created, but not visible
                // GUI-related attributes, such as window insets, not correct here
                // You can set the container's attributes, or allocate resources here

                Drawing drawing = Drawing.getInstance();        // Get the platform-dependent 2D factory instance
                Cursor wait;
                try {
                    wait = drawing.getCursor(Cursor.Type.WAIT); // Get the system "wait" cursor
                } catch (CursorNotFoundException e) {
                    throw new UnexpectedError(e);               // Unexpected
                }

                container.setBackgroundColor(Color.BLUE);       // Set the background color
                container.setCursor(wait);                      // Set the cursor
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

        Dispatcher.defaultInstance().invokeLater(container); // Launch the container
    }

}
