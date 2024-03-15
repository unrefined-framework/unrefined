package unrefined.context;

import unrefined.app.Logger;
import unrefined.app.Preferences;
import unrefined.app.Runtime;
import unrefined.io.asset.AssetLoader;
import unrefined.media.graphics.Cursor;
import unrefined.media.graphics.Dimension;
import unrefined.media.graphics.Insets;
import unrefined.media.graphics.Point;
import unrefined.media.graphics.Rectangle;
import unrefined.util.TextManager;
import unrefined.util.event.EventBus;
import unrefined.util.signal.Dispatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Container implements Runnable {

    public static Container of() {
        return Runtime.getInstance().createContainer();
    }

    public static Container of(ContainerListener containerListener) {
        return Runtime.getInstance().createContainer(containerListener);
    }

    private volatile ContainerListener containerListener;

    public final Environment local;
    public final Environment properties;

    public Container(ContainerListener containerListener) {
        this.containerListener = containerListener;
        local = new Environment(new ConcurrentHashMap<>(), "LOCAL ENVIRONMENT " + Integer.toHexString(hashCode()));
        properties = new Environment(new ConcurrentHashMap<>(), "LOCAL PROPERTIES " + Integer.toHexString(hashCode()));
    }

    public Container() {
        this(null);
    }

    public abstract void invokeLater(Runnable runnable);
    public abstract void invokeAndWait(Runnable runnable) throws InterruptedException;
    public abstract boolean isDispatchThread();

    public void requestLaunch() {
        Dispatcher.defaultInstance().invokeLater(this);
    }

    private volatile AssetLoader ASSET_LOADER;
    private final Object ASSET_LOADER_LOCK = new Object();
    public AssetLoader getAssetLoader() {
        if (ASSET_LOADER == null) synchronized (ASSET_LOADER_LOCK) {
            if (ASSET_LOADER == null) ASSET_LOADER = local.get("unrefined.runtime.assetLoader", AssetLoader.class);
        }
        return ASSET_LOADER;
    }
    private volatile Dispatcher DISPATCHER;
    private final Object DISPATCHER_LOCK = new Object();
    public Dispatcher getDispatcher() {
        if (DISPATCHER == null) synchronized (DISPATCHER_LOCK) {
            if (DISPATCHER == null) DISPATCHER = local.get("unrefined.runtime.dispatcher", Dispatcher.class);
        }
        return DISPATCHER;
    }
    private volatile Logger LOGGER;
    private final Object LOGGER_LOCK = new Object();
    public Logger getLogger() {
        if (LOGGER == null) synchronized (LOGGER_LOCK) {
            if (LOGGER == null) LOGGER = local.get("unrefined.runtime.logger", Logger.class);
        }
        return LOGGER;
    }
    private volatile TextManager TEXT_MANAGER;
    private final Object TEXT_MANAGER_LOCK = new Object();
    public TextManager getTextManager() {
        if (TEXT_MANAGER == null) synchronized (TEXT_MANAGER_LOCK) {
            if (TEXT_MANAGER == null) TEXT_MANAGER = local.get("unrefined.runtime.textManager", TextManager.class);
        }
        return TEXT_MANAGER;
    }
    private volatile EventBus EVENT_BUS;
    private final Object EVENT_BUS_LOCK = new Object();
    public EventBus getEventBus() {
        if (EVENT_BUS == null) synchronized (EVENT_BUS_LOCK) {
            if (EVENT_BUS == null) EVENT_BUS = local.get("unrefined.runtime.eventBus", EventBus.class);
        }
        return EVENT_BUS;
    }

    public abstract Preferences getPreferences(String name);
    public abstract boolean deletePreferences(String name);

    private volatile String APP_VENDOR;
    private final Object APP_VENDOR_LOCK = new Object();
    public String getApplicationVendor() {
        if (APP_VENDOR == null) synchronized (APP_VENDOR_LOCK) {
            if (APP_VENDOR == null) APP_VENDOR = properties.getProperty("unrefined.app.vendor");
        }
        return APP_VENDOR;
    }
    private volatile String APP_NAME;
    private final Object APP_NAME_LOCK = new Object();
    public String getApplicationName() {
        if (APP_NAME == null) synchronized (APP_NAME_LOCK) {
            if (APP_NAME == null) APP_NAME = properties.getProperty("unrefined.app.name");
        }
        return APP_NAME;
    }
    private volatile String APP_VERSION_NAME;
    private final Object APP_VERSION_NAME_LOCK = new Object();
    public String getApplicationVersionName() {
        if (APP_VERSION_NAME == null) synchronized (APP_VERSION_NAME_LOCK) {
            if (APP_VERSION_NAME == null) APP_VERSION_NAME = properties.getProperty("unrefined.app.version.name");
        }
        return APP_VERSION_NAME;
    }
    private volatile String APP_VERSION_CODE;
    private final Object APP_VERSION_CODE_LOCK = new Object();
    public String getApplicationVersionCode() {
        if (APP_VERSION_CODE == null) synchronized (APP_VERSION_CODE_LOCK) {
            if (APP_VERSION_CODE == null) APP_VERSION_CODE = properties.getProperty("unrefined.app.version.code");
        }
        return APP_VERSION_CODE;
    }
    private volatile String APP_PACKAGE;
    private final Object APP_PACKAGE_LOCK = new Object();
    public String getApplicationPackage() {
        if (APP_PACKAGE == null) synchronized (APP_PACKAGE_LOCK) {
            if (APP_PACKAGE == null) APP_PACKAGE = properties.getProperty("unrefined.app.package");
        }
        return APP_PACKAGE;
    }
    private volatile String APP_IMPLEMENTER;
    private final Object APP_IMPLEMENTER_LOCK = new Object();
    public String getApplicationImplementer() {
        if (APP_IMPLEMENTER == null) synchronized (APP_IMPLEMENTER_LOCK) {
            if (APP_IMPLEMENTER == null) APP_IMPLEMENTER = properties.getProperty("unrefined.app.implementer");
        }
        return APP_IMPLEMENTER;
    }

    public abstract int getDotsPerInch();
    public abstract float getFontScale();
    public abstract float getDensity();
    public abstract float getScaledDensity();
    public float dpsp(float dp) {
        return dp * getFontScale();
    }
    public float dppx(float dp) {
        return dp * getDensity();
    }
    public float dppt(float dp) {
        return dp * getDotsPerInch() / 72f;
    }
    public float spdp(float sp) {
        return sp / getFontScale();
    }
    public float sppx(float sp) {
        return sp * getScaledDensity();
    }
    public float sppt(float sp) {
        return sp * getDotsPerInch() * getFontScale() / 72f;
    }
    public float ptdp(float pt) {
        return pt / getDotsPerInch() * 72f;
    }
    public float ptsp(float pt) {
        return pt / getDotsPerInch() * getFontScale() * 72f;
    }
    public float ptpx(float pt) {
        return pt * 72f;
    }
    public float pxdp(float px) {
        return px / getDensity();
    }
    public float pxsp(float px) {
        return px / getScaledDensity();
    }
    public float pxpt(float px) {
        return px / 72f;
    }

    public abstract File getConfigDirectory();
    public abstract File getCacheDirectory();
    public abstract File getFilesDirectory(String type);
    public abstract File getHomeDirectory();
    public abstract File getTempDirectory();
    public abstract File getCurrentDirectory();

    private final List<Context> contexts = Collections.synchronizedList(new ArrayList<>());
    public void addContext(Context context) {
        contexts.add(context);
    }
    public void removeContext(Context context) {
        contexts.remove(context);
    }
    public List<Context> getContexts() {
        return Collections.unmodifiableList(new ArrayList<>(contexts));
    }

    public ContainerListener getContainerListener() {
        return containerListener;
    }

    public void setContainerListener(ContainerListener containerListener) {
        this.containerListener = containerListener;
    }

    public abstract void setX(int x);
    public abstract void setY(int y);
    public void setPosition(int x, int y) {
        setX(x);
        setY(y);
    }
    public abstract void setWidth(int width);
    public abstract void setHeight(int height);
    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }
    public void setBounds(int x, int y, int width, int height) {
        setPosition(x, y);
        setSize(width, height);
    }

    public abstract int getX();
    public abstract int getY();
    public void getPosition(Point position) {
        position.setPoint(getX(), getY());
    }
    public abstract int getWidth();
    public abstract int getHeight();
    public void getSize(Dimension size) {
        size.setDimension(getWidth(), getHeight());
    }
    public void getBounds(Rectangle bounds) {
        bounds.setRectangle(getX(), getY(), getWidth(), getHeight());
    }

    public abstract void setMinimumWidth(int width);
    public abstract void setMinimumHeight(int height);
    public void setMinimumSize(int width, int height) {
        setMinimumWidth(width);
        setMinimumHeight(height);
    }
    public abstract int getMinimumWidth();
    public abstract int getMinimumHeight();
    public void getMinimumSize(Dimension size) {
        size.setDimension(getMinimumWidth(), getMinimumHeight());
    }

    public abstract void setBackgroundColor(int color);
    public abstract int getBackgroundColor();

    public abstract void requestPaint();
    public abstract void requestSnapshot();
    public void requestPaint(boolean snapshot) {
        if (snapshot) requestSnapshot();
        else requestPaint();
    }

    public abstract void setClientX(int x);
    public abstract void setClientY(int y);
    public void setClientPosition(int x, int y) {
        setClientX(x);
        setClientY(y);
    }
    public abstract void setClientWidth(int width);
    public abstract void setClientHeight(int height);
    public void setClientSize(int width, int height) {
        setClientWidth(width);
        setClientHeight(height);
    }
    public void setClientBounds(int x, int y, int width, int height) {
        setClientPosition(x, y);
        setClientSize(width, height);
    }
    public abstract int getClientX();
    public abstract int getClientY();
    public void getClientPosition(Point position) {
        position.setPoint(getClientX(), getClientY());
    }
    public abstract int getClientWidth();
    public abstract int getClientHeight();
    public void getClientSize(Dimension size) {
        size.setDimension(getClientWidth(), getClientHeight());
    }
    public void getClientBounds(Rectangle bounds) {
        bounds.setRectangle(getClientX(), getClientY(), getClientWidth(), getClientHeight());
    }

    public abstract void setClientMinimumWidth(int width);
    public abstract void setClientMinimumHeight(int height);
    public void setClientMinimumSize(int width, int height) {
        setClientMinimumWidth(width);
        setClientMinimumHeight(height);
    }
    public abstract int getClientMinimumWidth();
    public abstract int getClientMinimumHeight();
    public void getClientMinimumSize(Dimension size) {
        size.setDimension(getClientMinimumWidth(), getClientMinimumHeight());
    }

    public abstract int getLeftInset();
    public abstract int getTopInset();
    public abstract int getRightInset();
    public abstract int getBottomInset();
    public void getInsets(Insets insets) {
        insets.setInsets(getLeftInset(), getTopInset(), getRightInset(), getBottomInset());
    }

    public abstract void setCursor(Cursor cursor);
    public abstract Cursor getCursor();

    public abstract void setInputMethodEnabled(boolean enabled);
    public abstract boolean isInputMethodEnabled();
    public abstract void requestInputMethod();
    public abstract void setInputMethodX(int xOffset);
    public abstract void setInputMethodY(int yOffset);
    public void setInputMethodPosition(int xOffset, int yOffset) {
        setInputMethodX(xOffset);
        setInputMethodY(yOffset);
    }
    public abstract int getInputMethodX();
    public abstract int getInputMethodY();
    public void getInputMethodPosition(Point position) {
        position.setPoint(getInputMethodX(), getInputMethodY());
    }
    public abstract int getInputMethodHeight();

    public abstract Context createContext(ContextListener contextListener);
    public Context createContext() {
        return createContext(null);
    }

    public abstract int getScreenWidth();
    public abstract int getScreenHeight();
    public abstract void getScreenSize(Dimension size);

    public abstract void setFullscreen(boolean fullscreen);
    public abstract boolean isFullscreen();

}
