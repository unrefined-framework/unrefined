package unrefined.runtime;

import unrefined.app.Log;
import unrefined.app.Preferences;
import unrefined.core.AnimatedCursor;
import unrefined.context.ContainerListener;
import unrefined.context.Context;
import unrefined.context.ContextListener;
import unrefined.context.Environment;
import unrefined.desktop.AWTSupport;
import unrefined.desktop.CursorAnimator;
import unrefined.desktop.DefaultIcon;
import unrefined.desktop.KeyEventParser;
import unrefined.desktop.OSInfo;
import unrefined.desktop.StandardDirectories;
import unrefined.internal.X11.X11FontSupport;
import unrefined.internal.macos.MacFontSupport;
import unrefined.internal.macos.MacPreferences;
import unrefined.internal.posix.PosixPreferences;
import unrefined.internal.windows.WindowsAWTSupport;
import unrefined.internal.windows.WindowsFontSupport;
import unrefined.internal.windows.WindowsPreferences;
import unrefined.io.asset.AssetLoader;
import unrefined.media.graphics.Cursor;
import unrefined.media.graphics.Dimension;
import unrefined.media.graphics.Insets;
import unrefined.media.graphics.Point;
import unrefined.media.graphics.Rectangle;
import unrefined.media.input.Input;
import unrefined.util.TextManager;
import unrefined.util.event.EventBus;
import unrefined.util.function.Slot;
import unrefined.util.signal.Dispatcher;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.TextField;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.AttributedCharacterIterator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class DesktopContainer extends unrefined.context.Container implements
        ComponentListener,
        KeyListener, MouseListener, MouseMotionListener, MouseWheelListener,
        WindowListener, WindowFocusListener {

    static final boolean BUFFERED = !GraphicsEnvironment.isHeadless() &&
            Boolean.getBoolean("unrefined.desktop.graphics.buffered");

    private volatile BufferedImage buffer = null;
    private final Object bufferLock = new Object();

    private volatile DisplayMode cachedDisplayMode = null;

    public void lockBufferAndPost(Runnable runnable) {
        if (runnable == null) return;
        synchronized (bufferLock) {
            runnable.run();
        }
    }

    public void lockBufferAndPost(Slot<BufferedImage> consumer) {
        if (consumer == null) return;
        synchronized (bufferLock) {
            consumer.accept(buffer);
        }
    }

    private final Container container;
    private final InputMethodIndicator inputMethod;

    public DesktopContainer(ContainerListener containerListener) {
        super(containerListener);

        local.put("unrefined.runtime.textManager", TextManager.defaultInstance());
        local.put("unrefined.runtime.eventBus", EventBus.defaultInstance());

        local.put("unrefined.runtime.dispatcher", Dispatcher.defaultInstance());
        local.put("unrefined.runtime.log", Log.defaultInstance());
        local.put("unrefined.runtime.assetLoader", AssetLoader.defaultInstance());

        properties.setProperty("unrefined.app.vendor", Environment.properties.getProperty("unrefined.app.vendor"));
        properties.setProperty("unrefined.app.name", Environment.properties.getProperty("unrefined.app.name"));
        properties.setProperty("unrefined.app.version.name", Environment.properties.getProperty("unrefined.app.version.name"));
        properties.setProperty("unrefined.app.version.code", Environment.properties.getProperty("unrefined.app.version.code"));
        properties.setProperty("unrefined.app.package", Environment.properties.getProperty("unrefined.app.package"));

        Frame frame;
        java.awt.Container container;
        InputMethodIndicator inputMethod;
        try {
            container = null;
            frame = new Frame() {
                @Override
                public void addNotify() {
                    super.addNotify();
                    ContainerListener listener = getContainerListener();
                    if (listener != null) listener.onCreate(DesktopContainer.this);
                }
            };
            if (BUFFERED) updateBuffer(frame.getGraphicsConfiguration().getDevice().getDisplayMode());
            frame.setIconImage(DefaultIcon.ICON);
            //frame.setLocationByPlatform(true);
            frame.addWindowListener(this);
            frame.addWindowFocusListener(this);
            inputMethod = new InputMethodIndicator();
            inputMethod.setVisible(false);
            inputMethod.addKeyListener(this);
            frame.add(inputMethod);
        }
        catch (HeadlessException e) {
            frame = null;
            container = new java.awt.Container();
            inputMethod = null;
        }
        this.inputMethod = inputMethod;

        this.container = frame == null ? container : frame;
        //this.container.setLayout(null);
        this.container.setIgnoreRepaint(true);
        this.container.setBackground(Color.BLACK);
        this.container.enableInputMethods(false);
        this.container.setMinimumSize(null);
        this.container.setSize(640, 480);

        this.container.addComponentListener(this);
        this.container.addKeyListener(this);
        this.container.addMouseListener(this);
        this.container.addMouseMotionListener(this);
        this.container.addMouseWheelListener(this);
    }

    public DesktopContainer() {
        this(null);
    }

    @Override
    public void run() {
        run(null);
    }

    public void run(Runnable beforeVisible) {
        if (container instanceof Frame) {
            try {
                ((Frame) container).pack();
                ((Frame) container).setLocationByPlatform(true);
            }
            finally {
                container.setLayout(null);
            }
        }
        try {
            if (beforeVisible != null)
                beforeVisible.run();
        }
        finally {
            container.setVisible(true);
        }
    }

    public void requestLaunch(Runnable beforeVisible) {
        Dispatcher.defaultInstance().invokeLater(() -> DesktopContainer.this.run(beforeVisible));
    }

    private class InputMethodIndicator extends TextField implements InputMethodListener, FocusListener {
        public InputMethodIndicator() {
            super();
            setBackground(AWTSupport.TRANSPARENT);
            setForeground(AWTSupport.TRANSPARENT);
            setSize(0, 0);
            addInputMethodListener(this);
            addFocusListener(this);
        }
        @Override
        public void inputMethodTextChanged(InputMethodEvent event) {
            event.consume();
            AttributedCharacterIterator text = event.getText();
            if (text != null) {
                for (int i = text.getBeginIndex(); i < text.getEndIndex(); i ++) {
                    ContainerListener listener = getContainerListener();
                    if (listener != null) listener.onKeyTyped(DesktopContainer.this, text.current(), false);
                    text.next();
                }
            }
        }
        @Override
        public void caretPositionChanged(InputMethodEvent event) {
            event.consume();
        }
        @Override
        public void focusGained(FocusEvent e) {}
        @Override
        public void focusLost(FocusEvent e) {
            pressedCode.clear();
        }
    }

    @Override
    public void invokeLater(Runnable runnable) {
        EventQueue.invokeLater(runnable);
    }

    @Override
    public void invokeAndWait(Runnable runnable) throws InterruptedException {
        try {
            EventQueue.invokeAndWait(runnable);
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) throw (RuntimeException) t;
            else if (t instanceof Error) throw (Error) t;
            else throw new RuntimeException(t);
        }
    }

    @Override
    public boolean isDispatchThread() {
        return EventQueue.isDispatchThread();
    }

    public Frame getFrame() {
        return container instanceof Frame ? (Frame) container : null;
    }

    public Container getContainer() {
        return container;
    }

    @Override
    public void addContext(Context context) {
        super.addContext(context);
        container.add(((DesktopEmbeddedContext) context).getComponent());
        EventQueue.invokeLater(() -> {
            ContainerListener listener = getContainerListener();
            if (listener != null) listener.onContextAdd(DesktopContainer.this, context);
        });
    }

    @Override
    public void removeContext(Context context) {
        super.removeContext(context);
        container.remove(((DesktopEmbeddedContext) context).getComponent());
        EventQueue.invokeLater(() -> {
            ContainerListener listener = getContainerListener();
            if (listener != null) listener.onContextRemove(DesktopContainer.this, context);
        });
    }

    private volatile String NORMALIZED_APP_NAME;
    private final Object NORMALIZED_APP_NAME_LOCK = new Object();

    private String getNormalizedApplicationName() {
        if (NORMALIZED_APP_NAME == null) synchronized (NORMALIZED_APP_NAME_LOCK) {
            if (NORMALIZED_APP_NAME == null) NORMALIZED_APP_NAME = OSInfo.normalize(getApplicationName());
        }
        return NORMALIZED_APP_NAME;
    }

    @Override
    public Preferences getPreferences(String name) {
        Objects.requireNonNull(name);
        if (OSInfo.IS_WINDOWS) return WindowsPreferences.get("Software" + "\\" + getApplicationImplementer() + "\\" + getApplicationName(), name);
        else if (OSInfo.IS_MAC) {
            return MacPreferences.get(
                    (getApplicationPackage() + "." + getApplicationName())
                            .replace(" ", "-")
                            .replace(".", "/"), name);
        }
        else return PosixPreferences.get(StandardDirectories.CONFIG_HOME,
                    getApplicationName().toLowerCase(Locale.ENGLISH).replace(" ", ""), name);
        //else return DesktopPreferences.get(getConfigDirectory(), name.toLowerCase(Locale.ENGLISH).replace(" ", "-"));
    }

    @Override
    public boolean deletePreferences(String name) {
        Objects.requireNonNull(name);
        if (OSInfo.IS_WINDOWS) return WindowsPreferences.delete("Software" + "\\" + getApplicationImplementer() + "\\" + getApplicationName(), name);
        else if (OSInfo.IS_MAC) {
            return MacPreferences.delete(
                    (getApplicationPackage() + "." + getApplicationName())
                            .replace(" ", "-")
                            .replace(".", "/"), name);
        }
        else return PosixPreferences.delete(StandardDirectories.CONFIG_HOME,
                    getApplicationName().toLowerCase(Locale.ENGLISH).replace(" ", ""), name);
        //else return DesktopPreferences.delete(getConfigDirectory(), name.toLowerCase(Locale.ENGLISH).replace(" ", "-"));
    }

    private volatile File CONFIG_DIR;
    private final Object CONFIG_DIR_LOCK = new Object();
    @Override
    public File getConfigDirectory() {
        if (CONFIG_DIR == null) synchronized (CONFIG_DIR_LOCK) {
            if (CONFIG_DIR == null) CONFIG_DIR = new File(StandardDirectories.CONFIG_HOME, getNormalizedApplicationName());
        }
        return CONFIG_DIR;
    }

    private volatile File CACHE_DIR;
    private final Object CACHE_DIR_LOCK = new Object();
    @Override
    public File getCacheDirectory() {
        if (CACHE_DIR == null) synchronized (CACHE_DIR_LOCK) {
            if (CACHE_DIR == null) CACHE_DIR = new File(StandardDirectories.CACHE_HOME, getNormalizedApplicationName());
        }
        return CACHE_DIR;
    }

    private volatile File FILES_DIR;
    private final Object FILES_DIR_LOCK = new Object();
    @Override
    public File getFilesDirectory(String type) {
        if (FILES_DIR == null) synchronized (FILES_DIR_LOCK) {
            if (FILES_DIR == null) FILES_DIR = new File(StandardDirectories.DATA_HOME, getNormalizedApplicationName());
        }
        return new File(FILES_DIR, type == null ? "" : type);
    }

    @Override
    public File getHomeDirectory() {
        return StandardDirectories.USER_HOME;
    }

    @Override
    public File getTempDirectory() {
        return StandardDirectories.TEMP_HOME;
    }

    @Override
    public File getCurrentDirectory() {
        return new File(System.getProperty("user.dir"));
    }

    @Override
    public int getDotsPerInch() {
        if (GraphicsEnvironment.isHeadless()) return 96;
        else if (OSInfo.IS_WINDOWS) return WindowsAWTSupport.getDotsPerInch(container);
        else if (OSInfo.IS_MAC) return 96 * MacFontSupport.getScaleFactor(container.getGraphicsConfiguration().getDevice());
        else return X11FontSupport.getDPI();
    }

    @Override
    public float getFontScale() {
        if (GraphicsEnvironment.isHeadless()) return 1;
        else if (OSInfo.IS_WINDOWS) return WindowsFontSupport.getFontScale();
        else if (OSInfo.IS_MAC) return MacFontSupport.FONT_SCALE;
        else return X11FontSupport.getFontScale();
    }

    @Override
    public float getDensity() {
        if (GraphicsEnvironment.isHeadless()) return 1;
        else if (OSInfo.IS_WINDOWS) return WindowsAWTSupport.getDotsPerInch(container) / 96f;
        else if (OSInfo.IS_MAC) return MacFontSupport.getScaleFactor(container.getGraphicsConfiguration().getDevice());
        else return X11FontSupport.getDensity();
    }

    @Override
    public float getScaledDensity() {
        if (GraphicsEnvironment.isHeadless()) return 1;
        else if (OSInfo.IS_WINDOWS) return WindowsAWTSupport.getDotsPerInch(container) / 96f * WindowsFontSupport.getFontScale();
        else if (OSInfo.IS_MAC) return MacFontSupport.FONT_SCALE * MacFontSupport.getScaleFactor(container.getGraphicsConfiguration().getDevice());
        else return X11FontSupport.getScaledDensity();
    }

    @Override
    public void setX(int x) {
        container.setLocation(x, container.getY());
    }

    @Override
    public void setY(int y) {
        container.setLocation(container.getX(), y);
    }

    @Override
    public void setPosition(int x, int y) {
        container.setLocation(x, y);
    }

    @Override
    public void setWidth(int width) {
        container.setSize(width, container.getHeight());
        container.setPreferredSize(new java.awt.Dimension(width, container.getHeight()));
    }

    @Override
    public void setHeight(int height) {
        container.setSize(container.getWidth(), height);
        container.setPreferredSize(new java.awt.Dimension(container.getWidth(), height));
    }

    @Override
    public void setSize(int width, int height) {
        container.setSize(width, height);
        container.setPreferredSize(new java.awt.Dimension(width, height));
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        container.setLocation(x, y);
        container.setSize(width, height);
        container.setPreferredSize(new java.awt.Dimension(width, height));
    }

    @Override
    public int getX() {
        return container.getX();
    }

    @Override
    public int getY() {
        return container.getY();
    }

    @Override
    public void getPosition(Point position) {
        position.setPoint(container.getX(), container.getY());
    }

    @Override
    public int getWidth() {
        return container.getWidth();
    }

    @Override
    public int getHeight() {
        return container.getHeight();
    }

    @Override
    public void getBounds(Rectangle bounds) {
        bounds.setRectangle(container.getX(), container.getY(), container.getWidth(), container.getHeight());
    }

    @Override
    public void setMinimumWidth(int width) {
        container.setMinimumSize(new java.awt.Dimension(width, container.getHeight()));
    }

    @Override
    public void setMinimumHeight(int height) {
        container.setMinimumSize(new java.awt.Dimension(container.getWidth(), height));
    }

    @Override
    public void setMinimumSize(int width, int height) {
        container.setMinimumSize(new java.awt.Dimension(width, height));
    }

    @Override
    public int getMinimumWidth() {
        return container.getMinimumSize().width;
    }

    @Override
    public int getMinimumHeight() {
        return container.getMinimumSize().height;
    }

    @Override
    public void getMinimumSize(Dimension size) {
        java.awt.Dimension dimension = container.getMinimumSize();
        size.setDimension(dimension.width, dimension.height);
    }

    @Override
    public void setBackgroundColor(int color) {
        if (container instanceof Window) AWTSupport.setBackgroundColor((Window) container, new Color(color, true));
        else container.setBackground(new Color(color, true));
    }

    @Override
    public int getBackgroundColor() {
        return container.getBackground().getRGB();
    }

    @Override
    public void requestPaint() {
        for (Context context : getContexts()) {
            context.requestPaint();
        }
    }

    @Override
    public void requestSnapshot() {
        for (Context context : getContexts()) {
            context.requestSnapshot();
        }
    }

    @Override
    public void setClientX(int x) {
        java.awt.Insets insets = container.getInsets();
        container.setLocation(x - insets.left, container.getY());
    }

    @Override
    public void setClientY(int y) {
        java.awt.Insets insets = container.getInsets();
        container.setLocation(container.getX(), y - insets.top);
    }

    @Override
    public void setClientPosition(int x, int y) {
        java.awt.Insets insets = container.getInsets();
        container.setLocation(x - insets.left, y - insets.top);
    }

    @Override
    public void setClientWidth(int width) {
        java.awt.Insets insets = container.getInsets();
        container.setSize(width + insets.left + insets.right, container.getHeight());
    }

    @Override
    public void setClientHeight(int height) {
        java.awt.Insets insets = container.getInsets();
        container.setSize(container.getWidth(), height + insets.top + insets.bottom);
    }

    @Override
    public void setClientSize(int width, int height) {
        java.awt.Insets insets = container.getInsets();
        container.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
    }

    @Override
    public int getClientX() {
        java.awt.Insets insets = container.getInsets();
        return container.getX() + insets.left;
    }

    @Override
    public int getClientY() {
        java.awt.Insets insets = container.getInsets();
        return container.getY() + insets.top;
    }

    @Override
    public void getClientPosition(Point position) {
        java.awt.Insets insets = container.getInsets();
        container.setLocation(container.getX() + insets.left, container.getY() + insets.top);
    }

    @Override
    public int getClientWidth() {
        java.awt.Insets insets = container.getInsets();
        return container.getWidth() - insets.left - insets.right;
    }

    @Override
    public int getClientHeight() {
        java.awt.Insets insets = container.getInsets();
        return container.getHeight() - insets.top - insets.bottom;
    }

    @Override
    public void getClientSize(Dimension size) {
        java.awt.Insets insets = container.getInsets();
        size.setDimension(container.getWidth() - insets.left - insets.right,
                container.getHeight() - insets.top - insets.bottom);
    }

    @Override
    public void getClientBounds(Rectangle bounds) {
        java.awt.Insets insets = container.getInsets();
        bounds.setRectangle(container.getX() + insets.left, container.getY() + insets.top,
                container.getWidth() - insets.left - insets.right,
                container.getHeight() - insets.top - insets.bottom);
    }

    @Override
    public void setClientMinimumWidth(int width) {
        java.awt.Insets insets = container.getInsets();
        java.awt.Dimension dimension = container.getMinimumSize();
        dimension.width = width + insets.left + insets.right;
        container.setMinimumSize(dimension);
    }

    @Override
    public void setClientMinimumHeight(int height) {
        java.awt.Insets insets = container.getInsets();
        java.awt.Dimension dimension = container.getMinimumSize();
        dimension.height = height + insets.top + insets.bottom;
        container.setMinimumSize(dimension);
    }

    @Override
    public void setClientMinimumSize(int width, int height) {
        java.awt.Insets insets = container.getInsets();
        container.setMinimumSize(new java.awt.Dimension(width + insets.left + insets.right,
                height + insets.top + insets.bottom));
    }

    @Override
    public void setClientBounds(int x, int y, int width, int height) {
        java.awt.Insets insets = container.getInsets();
        container.setBounds(x - insets.left, y - insets.top,
                width + insets.left + insets.right,
                height + insets.top + insets.bottom);
    }

    @Override
    public int getClientMinimumWidth() {
        java.awt.Insets insets = container.getInsets();
        return container.getMinimumSize().width - insets.left - insets.right;
    }

    @Override
    public int getClientMinimumHeight() {
        java.awt.Insets insets = container.getInsets();
        return container.getMinimumSize().height - insets.top - insets.bottom;
    }

    @Override
    public void getClientMinimumSize(Dimension size) {
        java.awt.Insets insets = container.getInsets();
        java.awt.Dimension dimension = container.getMinimumSize();
        size.setDimension(dimension.width - insets.left - insets.right,
                dimension.height - insets.top - insets.bottom);
    }

    @Override
    public int getLeftInset() {
        return container.getInsets().left;
    }

    @Override
    public int getTopInset() {
        return container.getInsets().top;
    }

    @Override
    public int getRightInset() {
        return container.getInsets().right;
    }

    @Override
    public int getBottomInset() {
        return container.getInsets().bottom;
    }

    @Override
    public void getInsets(Insets insets) {
        java.awt.Insets containerInsets = container.getInsets();
        insets.setInsets(containerInsets.left, containerInsets.top, containerInsets.right, containerInsets.bottom);
    }

    private volatile Cursor cursor = DesktopCursor.getDefaultCursor();
    @Override
    public void setCursor(Cursor cursor) {
        this.cursor = cursor == null ? DesktopCursor.getDefaultCursor() : cursor;
        if (!GraphicsEnvironment.isHeadless() && cursor instanceof AnimatedCursor)
            EventQueue.invokeLater(() -> CursorAnimator.register(container, (AnimatedCursor) cursor));
        else container.setCursor(((DesktopCursor) this.cursor).getCursor());
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public void setInputMethodEnabled(boolean enabled) {
        inputMethod.setEnabled(enabled);
    }

    @Override
    public boolean isInputMethodEnabled() {
        return inputMethod.isEnabled();
    }

    @Override
    public void requestInputMethod() {
        inputMethod.setVisible(true);
        try {
            Component component = container.findComponentAt(inputMethod.getLocation());
            if (component != null) inputMethod.setCursor(component.getCursor());
            synchronized (container.getTreeLock()) {
                int count = container.getComponentCount();
                if (count > 0) container.setComponentZOrder(inputMethod, count - 1);
            }
        }
        finally {
            inputMethod.requestFocus();
        }
    }

    @Override
    public void setInputMethodX(int xOffset) {
        inputMethod.setLocation(xOffset, inputMethod.getY());
    }

    @Override
    public void setInputMethodY(int yOffset) {
        inputMethod.setLocation(inputMethod.getX(), yOffset);
    }

    @Override
    public void setInputMethodPosition(int xOffset, int yOffset) {
        inputMethod.setLocation(xOffset, yOffset);
    }

    @Override
    public int getInputMethodX() {
        return inputMethod.getX();
    }

    @Override
    public int getInputMethodY() {
        return inputMethod.getY();
    }

    @Override
    public void getInputMethodPosition(Point position) {
        position.setPoint(inputMethod.getX(), inputMethod.getY());
    }

    @Override
    public int getInputMethodHeight() {
        return 0;
    }

    @Override
    public Context createContext(ContextListener contextListener) {
        return new DesktopContext(this, contextListener);
    }

    @Override
    public int getScreenWidth() {
        return container.getGraphicsConfiguration().getBounds().width;
    }

    @Override
    public int getScreenHeight() {
        return container.getGraphicsConfiguration().getBounds().height;
    }

    @Override
    public void getScreenSize(Dimension size) {
        java.awt.Rectangle bounds = container.getGraphicsConfiguration().getBounds();
        size.setDimension(bounds.width, bounds.height);
    }

    @Override
    public void setFullscreen(boolean fullscreen) {
        if (container instanceof Window) {
            Window window = (Window) container;
            GraphicsDevice graphicsDevice = window.getGraphicsConfiguration().getDevice();
            if (fullscreen) graphicsDevice.setFullScreenWindow(window);
            else if (graphicsDevice.getFullScreenWindow() == window) graphicsDevice.setFullScreenWindow(null);
        }
    }

    @Override
    public boolean isFullscreen() {
        if (container instanceof Window) {
            Window window = (Window) container;
            return window.getGraphicsConfiguration().getDevice().getFullScreenWindow() == window;
        }
        else return false;
    }

    private void updateBuffer(DisplayMode displayMode) {
        if (!Objects.equals(cachedDisplayMode, displayMode)) {
            cachedDisplayMode = displayMode;
            int width = displayMode.getWidth();
            int height = displayMode.getHeight();
            synchronized (bufferLock) {
                if (width > 0 && height > 0) {
                    if (buffer == null) buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    else if (width != buffer.getWidth() || height != buffer.getHeight()) {
                        buffer.flush();
                        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                        System.gc();
                    }
                }
            }
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (BUFFERED) updateBuffer(e.getComponent().getGraphicsConfiguration().getDevice().getDisplayMode());
        EventQueue.invokeLater(() -> {
            ContainerListener listener = getContainerListener();
            if (listener != null) listener.onResize(DesktopContainer.this, e.getComponent().getWidth(), e.getComponent().getHeight());
        });
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        EventQueue.invokeLater(() -> {
            ContainerListener listener = getContainerListener();
            if (listener != null) listener.onMove(DesktopContainer.this, e.getComponent().getX(), e.getComponent().getY());
        });
    }

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void windowGainedFocus(WindowEvent e) {
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onResume(this);
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        pressedCode.clear();
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onPause(this);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        ContainerListener listener = getContainerListener();
        if (listener == null) e.getWindow().dispose();
        else if (listener.onCloseRequest(this)) e.getWindow().dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onDispose(this);
    }

    @Override
    public void windowIconified(WindowEvent e) {
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onHide(this);
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onShow(this);
    }

    @Override
    public void windowActivated(WindowEvent e) {
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onStart(this);
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onStop(this);
    }

    private final Set<String> pressedCode = new HashSet<>();

    @Override
    public void keyTyped(KeyEvent e) {
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onKeyTyped(this, e.getKeyChar(),
                pressedCode.contains(KeyEventParser.parseCode(e)));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        String code = KeyEventParser.parseCode(e);
        boolean repeat = pressedCode.contains(code);
        pressedCode.add(code);
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onKeyDown(this,
                KeyEventParser.parseKey(e),
                code,
                e.getKeyLocation() - 1,
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6), repeat);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        String code = KeyEventParser.parseCode(e);
        if (pressedCode.contains(code)) {
            ContainerListener listener = getContainerListener();
            if (listener != null) listener.onKeyUp(this,
                    KeyEventParser.parseKey(e),
                    KeyEventParser.parseCode(e),
                    e.getKeyLocation() - 1,
                    Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6));
        }
        pressedCode.remove(code);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onPointerDown(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6),
                e.getClickCount(), e.getButton() - 1);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onPointerUp(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6),
                e.getClickCount(), e.getButton() - 1);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!GraphicsEnvironment.isHeadless() && cursor instanceof AnimatedCursor)
            EventQueue.invokeLater(() -> CursorAnimator.register(container, (AnimatedCursor) cursor));
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onMouseEnter(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!GraphicsEnvironment.isHeadless()) EventQueue.invokeLater(() -> CursorAnimator.unregister(container));
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onMouseExit(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onPointerDrag(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6),
                e.getClickCount());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onMouseMove(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6));
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int scrollType = e.getScrollType();
        float amount;
        switch (scrollType) {
            case MouseWheelEvent.WHEEL_UNIT_SCROLL: amount = (float) (e.getScrollAmount() * e.getPreciseWheelRotation()); break;
            case MouseWheelEvent.WHEEL_BLOCK_SCROLL: amount = e.getWheelRotation() > 0 ? 1 : -1; break;
            default: amount = 0; break;
        }
        if (amount == 0) return;
        float amountX, amountY;
        if (e.getComponent().getComponentOrientation().isHorizontal()) {
            amountX = amount;
            amountY = 0;
        } else {
            amountX = 0;
            amountY = amount;
        }
        ContainerListener listener = getContainerListener();
        if (listener != null) listener.onScroll(this, amountX, amountY,
                scrollType == MouseWheelEvent.WHEEL_BLOCK_SCROLL ? 2 : 0);
    }

}
