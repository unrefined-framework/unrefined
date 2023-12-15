package unrefined.runtime;

import unrefined.context.Container;
import unrefined.context.ContextListener;
import unrefined.desktop.BitmapImageFactory;
import unrefined.media.graphics.Bitmap;

import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class DesktopContext extends DesktopEmbeddedContext {

    private volatile DesktopGraphics graphics = null;
    private final Object graphicsLock = new Object();

    public DesktopContext(Container container, ContextListener contextListener) {
        super(container, contextListener, new GraphicsComponent());
        GraphicsComponent component = (GraphicsComponent) getComponent();
        component.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = e.getComponent().getWidth();
                int height = e.getComponent().getHeight();
                if (graphics != null) synchronized (graphicsLock) {
                    if (graphics != null) graphics.setSize(width, height);
                }
                getContextListener().onResize(DesktopContext.this, width, height);
            }
            @Override
            public void componentMoved(ComponentEvent e) {
                getContextListener().onMove(DesktopContext.this, e.getComponent().getX(), e.getComponent().getY());
            }
            @Override
            public void componentShown(ComponentEvent e) {
                getContextListener().onShow(DesktopContext.this);
                e.getComponent().requestFocus();
            }
            @Override
            public void componentHidden(ComponentEvent e) {
                getContextListener().onHide(DesktopContext.this);
            }
        });
        component.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                getContextListener().onFocusGain(DesktopContext.this);
            }
            @Override
            public void focusLost(FocusEvent e) {
                getContextListener().onFocusLost(DesktopContext.this);
            }
        });
        component.onCreate = () -> {
            component.createBufferStrategy(2);
            synchronized (graphicsLock) {
                graphics = new DesktopGraphics();
            }
            EventQueue.invokeLater(() -> getContextListener().onCreate(DesktopContext.this));
        };
        component.onDispose = () -> {
            synchronized (graphicsLock) {
                graphics = null;
            }
            EventQueue.invokeLater(() -> getContextListener().onDispose(DesktopContext.this));
        };
    }

    @Override
    public void requestPaint() {
        if (graphics == null) return;
        EventQueue.invokeLater(() -> {
            synchronized (graphicsLock) {
                if (graphics == null) return;
                GraphicsComponent canvas = (GraphicsComponent) getCanvas();
                BufferStrategy bufferStrategy = canvas.getBufferStrategy();
                if (bufferStrategy == null) return;
                // Render single frame
                do {
                    // The following loop ensures that the contents of the drawing buffer
                    // are consistent in case the underlying surface was recreated
                    do {
                        // Get a new graphics context every time through the loop
                        // to make sure the strategy is validated
                        Graphics2D graphics2D = (Graphics2D) bufferStrategy.getDrawGraphics();

                        // Render to graphics
                        try {
                            graphics.setGraphics2D(graphics2D);
                            graphics.reset();
                            getContextListener().onPaint(this, graphics, false);
                        }
                        finally { // Dispose the graphics
                            graphics2D.dispose();
                        }

                        // Repeat the rendering if the drawing buffer contents
                        // were restored
                    } while (bufferStrategy.contentsRestored());

                    // Display the buffer
                    bufferStrategy.show();

                    // Repeat the rendering if the drawing buffer was lost
                } while (bufferStrategy.contentsLost());
            }
        });
    }

    @Override
    public void requestSnapshot() {
        if (graphics == null) return;
        EventQueue.invokeLater(() -> {
            synchronized (graphicsLock) {
                if (graphics == null) return;
                BufferedImage image = BitmapImageFactory.createImage(graphics.getWidth(), graphics.getHeight(), Bitmap.Type.RGBA_8888);
                Graphics2D graphics2D = image.createGraphics();
                try {
                    graphics2D.setBackground(getComponent().getBackground());
                    graphics2D.clearRect(0, 0, image.getWidth(), image.getHeight());
                    graphics.setGraphics2D(graphics2D);
                    graphics.reset();
                    getContextListener().onPaint(this, graphics, true);
                }
                finally {
                    graphics2D.dispose();
                }
                EventQueue.invokeLater(() -> getContextListener().onSnapshot(DesktopContext.this, new DesktopBitmap(image)));
            }
        });
    }

    private static final class GraphicsComponent extends Canvas {
        private volatile Runnable onCreate;
        private volatile Runnable onDispose;
        public GraphicsComponent() {
            super();
        }
        public GraphicsComponent(GraphicsConfiguration config) {
            super(config);
        }
        @Override
        public void addNotify() {
            super.addNotify();
            onCreate.run();
        }
        @Override
        public void removeNotify() {
            super.removeNotify();
            onDispose.run();
        }
    }

    public Canvas getCanvas() {
        return (Canvas) getComponent();
    }

}
