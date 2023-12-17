package unrefined.context;

import unrefined.media.graphics.Cursor;
import unrefined.media.graphics.Dimension;
import unrefined.media.graphics.Point;
import unrefined.media.graphics.Rectangle;

import java.util.Objects;

public abstract class Context {

    private final Container container;
    private volatile ContextListener contextListener;

    public Context(Container container) {
        this(container, null);
    }

    public Context(Container container, ContextListener contextListener) {
        this.container = Objects.requireNonNull(container);
        this.contextListener = contextListener;
    }

    public Container getContainer() {
        return container;
    }

    public ContextListener getContextListener() {
        return contextListener;
    }

    public void setContextListener(ContextListener contextListener) {
        this.contextListener = contextListener;
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
    public abstract void requestFocus();

    public abstract void setCursor(Cursor cursor);
    public abstract Cursor getCursor();

}
