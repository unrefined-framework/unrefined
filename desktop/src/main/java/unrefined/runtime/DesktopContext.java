package unrefined.runtime;

import unrefined.context.Container;
import unrefined.context.ContextListener;
import unrefined.desktop.BitmapSupport;
import unrefined.media.graphics.Bitmap;
import unrefined.util.function.Slot;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;

import static unrefined.runtime.DesktopContainer.BUFFERED;

public class DesktopContext extends DesktopEmbeddedContext {

    private final Object graphicsLock = BUFFERED ? null : new Object();
    private volatile DesktopGraphics graphics = null;

    public DesktopContext(Container container) {
        this(container, null);
    }

    public DesktopContext(Container container, ContextListener contextListener) {
        super(container, contextListener, new GraphicsComponent());
        GraphicsComponent component = (GraphicsComponent) getComponent();
        component.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = e.getComponent().getWidth();
                int height = e.getComponent().getHeight();
                if (width > 0 && height > 0) {
                    if (BUFFERED) {
                        if (graphics != null) ((DesktopContainer) container).lockBufferAndPost(() -> {
                            if (graphics != null) graphics.setSize(width, height);
                        });
                    }
                    else if (graphics != null) synchronized (graphicsLock) {
                        if (graphics != null) graphics.setSize(width, height);
                    }
                }
                EventQueue.invokeLater(() -> {
                    ContextListener listener = listener();
                    if (listener != null) listener.onResize(DesktopContext.this, width, height);
                });
            }
            @Override
            public void componentMoved(ComponentEvent e) {
                EventQueue.invokeLater(() -> {
                    ContextListener listener = listener();
                    if (listener != null) listener.onMove(DesktopContext.this, e.getComponent().getX(), e.getComponent().getY());
                });
            }
            @Override
            public void componentShown(ComponentEvent e) {
                listener().onShow(DesktopContext.this);
                e.getComponent().requestFocus();
            }
            @Override
            public void componentHidden(ComponentEvent e) {
                listener().onHide(DesktopContext.this);
            }
        });
        component.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                listener().onFocusGain(DesktopContext.this);
            }
            @Override
            public void focusLost(FocusEvent e) {
                listener().onFocusLost(DesktopContext.this);
            }
        });
        component.onCreate = () -> {
            if (BUFFERED) ((DesktopContainer) container).lockBufferAndPost(() -> graphics = new DesktopGraphics());
            else synchronized (graphicsLock) {
                graphics = new DesktopGraphics();
            }
            EventQueue.invokeLater(() -> {
                listener().onCreate(DesktopContext.this);
            });
        };
        component.onDispose = () -> {
            if (BUFFERED) ((DesktopContainer) container).lockBufferAndPost(() -> {
                graphics.cleanup();
                graphics = null;
            });
            else synchronized (graphicsLock) {
                graphics.cleanup();
                graphics = null;
            }
            //EventQueue.invokeLater(() -> {
                listener().onDispose(DesktopContext.this);
            //});
        };
        component.onPaint = graphics2D -> {
            if (graphics == null) return;
            if (BUFFERED) ((DesktopContainer) container).lockBufferAndPost(buffer -> {
                if (graphics == null) return;
                ContextListener listener = listener();
                if (listener != null) {
                    int width = getWidth();
                    int height = getHeight();
                    if (width > 0 && height > 0) {
                        graphics.setGraphics2D(buffer.createGraphics());
                        graphics.clearBackground(component.getBackground());
                        graphics.reset();
                        try {
                            listener.onPaint(DesktopContext.this, graphics, false);
                        }
                        finally {
                            try {
                                graphics2D.setComposite(AlphaComposite.Src);
                                graphics2D.clipRect(0, 0, width, height);
                                graphics2D.drawImage(buffer, 0, 0, null);
                            }
                            finally {
                                Toolkit.getDefaultToolkit().sync();
                            }
                        }
                    }
                }
            });
            else synchronized (graphicsLock) {
                if (graphics == null) return;
                ContextListener listener = listener();
                if (listener != null) {
                    int width = getWidth();
                    int height = getHeight();
                    if (width > 0 && height > 0) {
                        graphics.setGraphics2D((Graphics2D) graphics2D.create());
                        graphics.clearBackground(component.getBackground());
                        graphics.reset();
                        try {
                            listener.onPaint(this, graphics, false);
                        }
                        finally {
                            Toolkit.getDefaultToolkit().sync();
                        }
                    }
                }
            }
        };
    }

    @Override
    public void paint() {
        getGraphicsComponent().paint();
    }

    private void doSnapshot() {
        if (graphics == null) return;
        ContextListener listener = listener();
        if (listener != null) {
            int width = getWidth();
            int height = getHeight();
            if (width > 0 && height > 0) {
                BufferedImage image = BitmapSupport.createImage(getWidth(), getHeight(), Bitmap.Type.RGBA_8888);
                try {
                    graphics.setGraphics2D(image.createGraphics());
                    graphics.reset();
                    graphics.clearBackground(getComponent().getBackground());
                    listener.onPaint(this, graphics, true);
                }
                finally {
                    graphics.cleanup();
                }
                EventQueue.invokeLater(() -> listener.onSnapshot(DesktopContext.this, new DesktopBitmap(image)));
            }
        }
    }

    @Override
    public void snapshot() {
        if (graphics == null) return;
        if (BUFFERED) ((DesktopContainer) getContainer()).lockBufferAndPost(this::doSnapshot);
        else synchronized (graphicsLock) {
            doSnapshot();
        }
    }

    @Override
    public void requestPaint() {
        if (graphics == null) return;
        EventQueue.invokeLater(this::paint);
    }

    @Override
    public void requestSnapshot() {
        if (graphics == null) return;
        EventQueue.invokeLater(this::snapshot);
    }

    private static final class GraphicsComponent extends Canvas {

        private volatile boolean created = false;
        private volatile Runnable onCreate;
        private volatile Runnable onDispose;
        private volatile Slot<Graphics2D> onPaint;

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
            EventQueue.invokeLater(() -> created = true);
        }
        @Override
        public void removeNotify() {
            created = false;
            onDispose.run();
            super.removeNotify();
        }

        @Override
        public void paint(Graphics g) {
            if (g == null || !created) return;
            onPaint.accept((Graphics2D) g);
        }

        public void paint() {
            Graphics g = getGraphics();
            if (g == null) return;
            onPaint.accept((Graphics2D) g);
        }

    }

    public Canvas getCanvas() {
        return (Canvas) getComponent();
    }

    private GraphicsComponent getGraphicsComponent() {
        return (GraphicsComponent) getComponent();
    }

}
