package unrefined.runtime;

import unrefined.context.Container;
import unrefined.context.ContextListener;
import unrefined.desktop.AWTSupport;
import unrefined.desktop.BitmapSupport;
import unrefined.desktop.TransformedGraphics2D;
import unrefined.media.graphics.Bitmap;
import unrefined.util.function.Slot;

import javax.swing.JPanel;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

public class DesktopContext extends DesktopEmbeddedContext {

    private final Object graphicsLock = new Object();
    private volatile DesktopGraphics graphics = null;

    public DesktopContext(Container container, ContextListener contextListener) {
        super(container, contextListener, new GraphicsComponent(false));
        GraphicsComponent component = (GraphicsComponent) getComponent();
        component.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = e.getComponent().getWidth();
                int height = e.getComponent().getHeight();
                if (width > 0 && height > 0) {
                    if (graphics != null) synchronized (graphicsLock) {
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
            synchronized (graphicsLock) {
                graphics = new DesktopGraphics();
            }
            EventQueue.invokeLater(() -> {
                listener().onCreate(DesktopContext.this);
            });
        };
        component.onDispose = () -> {
            synchronized (graphicsLock) {
                graphics.cleanup();
                graphics = null;
            }
            //EventQueue.invokeLater(() -> {
                listener().onDispose(DesktopContext.this);
            //});
        };
        component.onPaint = graphics2D -> {
            if (graphics == null) return;
            synchronized (graphicsLock) {
                if (graphics == null) return;
                ContextListener listener = listener();
                if (listener != null) {
                    int width = getWidth();
                    int height = getHeight();
                    if (width > 0 && height > 0) {
                        graphics.setGraphics2D(new TransformedGraphics2D((Graphics2D) graphics2D.create(),
                                graphics2D.getTransform(), graphics2D.getClip()));
                        graphics.clearBackground(component.getBackground());
                        graphics.reset();
                        try {
                            listener.onPaint(this, graphics, component.isPaintingForPrint());
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
    public void requestPaint() {
        getGraphicsComponent().repaint();
    }

    @Override
    public void requestSnapshot() {
        if (graphics == null) return;
        synchronized (graphicsLock) {
            if (graphics == null) return;
            ContextListener listener = listener();
            if (listener != null) {
                int width = getWidth();
                int height = getHeight();
                if (width > 0 && height > 0) {
                    BufferedImage image = BitmapSupport.createImage(getWidth(), getHeight(), Bitmap.Type.RGBA_8888);
                    Graphics graphics = image.getGraphics();
                    try {
                        getGraphicsComponent().print(graphics);
                    }
                    finally {
                        graphics.dispose();
                    }
                    EventQueue.invokeLater(() -> listener.onSnapshot(DesktopContext.this, new DesktopBitmap(image)));
                }
            }
        }
    }

    private static final class GraphicsComponent extends JPanel {

        private volatile Runnable onCreate;
        private volatile Runnable onDispose;
        private volatile Slot<Graphics2D> onPaint;

        public GraphicsComponent(boolean isDoubleBuffered) {
            super(null, isDoubleBuffered);
            JPanel content = new JPanel(null, isDoubleBuffered) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    onPaint.accept((Graphics2D) g);
                }
            };
            content.setFocusable(false);
            content.setOpaque(false);
            //content.setIgnoreRepaint(true);
            content.setBackground(AWTSupport.TRANSPARENT);
            content.enableInputMethods(false);
            content.setLocation(0, 0);
            setLayout(null);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    content.setSize(e.getComponent().getWidth(), e.getComponent().getHeight());
                }
            });
            add(content);
        }

        private final AtomicBoolean created = new AtomicBoolean(false);
        private final AtomicBoolean disposed = new AtomicBoolean(false);

        @Override
        public void addNotify() {
            super.addNotify();
            if (created.compareAndSet(false, true)) {
                onCreate.run();
            }
        }

        @Override
        public void removeNotify() {
            if (disposed.compareAndSet(false, true)) {
                onDispose.run();
            }
            super.removeNotify();
        }

    }

    public JPanel getJPanel() {
        return (JPanel) getComponent();
    }

    private GraphicsComponent getGraphicsComponent() {
        return (GraphicsComponent) getComponent();
    }

}
