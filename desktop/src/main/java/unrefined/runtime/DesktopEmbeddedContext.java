package unrefined.runtime;

import unrefined.context.Container;
import unrefined.context.Context;
import unrefined.context.ContextListener;
import unrefined.internal.AWTUtils;
import unrefined.internal.CursorUtils;
import unrefined.internal.KeyUtils;
import unrefined.media.graphics.Cursor;
import unrefined.media.graphics.Dimension;
import unrefined.media.graphics.Point;
import unrefined.media.graphics.Rectangle;
import unrefined.media.input.Input;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Objects;

public abstract class DesktopEmbeddedContext extends Context implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private final Component component;

    public DesktopEmbeddedContext(Container container, Component component) {
        this(container, null, component);
    }

    public DesktopEmbeddedContext(Container container, ContextListener contextListener, Component component) {
        super(container, contextListener);
        this.component = Objects.requireNonNull(component);

        component.setIgnoreRepaint(true);
        component.setBackground(AWTUtils.TRANSPARENT);
        component.enableInputMethods(false);

        component.addKeyListener(this);
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addMouseWheelListener(this);
    }

    public Component getComponent() {
        return component;
    }

    @Override
    public void setX(int x) {
        component.setLocation(x, component.getY());
    }

    @Override
    public void setY(int y) {
        component.setLocation(component.getX(), y);
    }

    @Override
    public void setPosition(int x, int y) {
        component.setLocation(x, y);
    }

    @Override
    public void setWidth(int width) {
        component.setSize(width, component.getHeight());
    }

    @Override
    public void setHeight(int height) {
        component.setSize(component.getWidth(), height);
    }

    @Override
    public void setSize(int width, int height) {
        component.setSize(width, height);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        component.setBounds(x, y, width, height);
    }

    @Override
    public int getX() {
        return component.getX();
    }

    @Override
    public int getY() {
        return component.getY();
    }

    @Override
    public void getPosition(Point position) {
        position.setPoint(component.getX(), component.getY());
    }

    @Override
    public int getWidth() {
        return component.getWidth();
    }

    @Override
    public int getHeight() {
        return component.getHeight();
    }

    @Override
    public void getSize(Dimension size) {
        size.setDimension(component.getWidth(), component.getHeight());
    }

    @Override
    public void getBounds(Rectangle bounds) {
        bounds.setRectangle(component.getX(), component.getY(), component.getWidth(), component.getHeight());
    }

    @Override
    public void setMinimumWidth(int width) {
        component.setMinimumSize(new java.awt.Dimension(width, component.getMinimumSize().height));
    }

    @Override
    public void setMinimumHeight(int height) {
        component.setMinimumSize(new java.awt.Dimension(component.getMinimumSize().width, height));
    }

    @Override
    public void setMinimumSize(int width, int height) {
        component.setMinimumSize(new java.awt.Dimension(width, height));
    }

    @Override
    public int getMinimumWidth() {
        return component.getMinimumSize().width;
    }

    @Override
    public int getMinimumHeight() {
        return component.getMinimumSize().height;
    }

    @Override
    public void getMinimumSize(Dimension size) {
        java.awt.Dimension dimension = component.getMinimumSize();
        size.setDimension(dimension.width, dimension.height);
    }

    @Override
    public void setBackgroundColor(int color) {
        component.setBackground(new Color(color, true));
    }

    @Override
    public int getBackgroundColor() {
        return component.getBackground().getRGB();
    }

    @Override
    public void requestFocus() {
        component.requestFocus();
    }

    private volatile Cursor cursor = DesktopCursor.getDefaultCursor();
    @Override
    public void setCursor(Cursor cursor) {
        this.cursor = cursor == null ? DesktopCursor.getDefaultCursor() : cursor;
        if (!GraphicsEnvironment.isHeadless() && cursor instanceof AnimatedCursor)
            EventQueue.invokeLater(() -> CursorUtils.registerAnimated(component, (AnimatedCursor) cursor));
        else component.setCursor(((DesktopCursor) this.cursor).getCursor());
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        ContextListener listener = getContextListener();
        if (listener != null && listener.onKeyTyped(this, e.getKeyChar())) e.consume();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        ContextListener listener = getContextListener();
        if (listener != null && listener.onKeyDown(this,
                KeyUtils.parseKey(e),
                KeyUtils.parseCode(e),
                e.getKeyLocation() - 1,
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6))) e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        ContextListener listener = getContextListener();
        if (listener != null && listener.onKeyUp(this,
                KeyUtils.parseKey(e),
                KeyUtils.parseCode(e),
                e.getKeyLocation() - 1,
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6))) e.consume();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        ContextListener listener = getContextListener();
        if (listener != null && listener.onPointerDown(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6),
                e.getClickCount(), e.getButton() - 1)) e.consume();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ContextListener listener = getContextListener();
        if (listener != null && listener.onPointerUp(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6),
                e.getClickCount(), e.getButton() - 1)) e.consume();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!GraphicsEnvironment.isHeadless() && cursor instanceof AnimatedCursor)
            EventQueue.invokeLater(() -> CursorUtils.registerAnimated(component, (AnimatedCursor) cursor));
        ContextListener listener = getContextListener();
        if (listener != null && listener.onMouseEnter(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6))) e.consume();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!GraphicsEnvironment.isHeadless()) EventQueue.invokeLater(() -> CursorUtils.unregisterAnimated(component));
        ContextListener listener = getContextListener();
        if (listener != null && listener.onMouseExit(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6))) e.consume();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        ContextListener listener = getContextListener();
        if (listener != null && listener.onPointerDrag(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6),
                e.getClickCount())) e.consume();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        ContextListener listener = getContextListener();
        if (listener != null && listener.onMouseMove(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6))) e.consume();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int scrollType = e.getScrollType();
        float amount = (float) switch (scrollType) {
            case MouseWheelEvent.WHEEL_UNIT_SCROLL -> e.getScrollAmount() * e.getPreciseWheelRotation();
            case MouseWheelEvent.WHEEL_BLOCK_SCROLL -> e.getWheelRotation() > 0 ? 1 : -1;
            default -> 0;
        };
        if (amount == 0) return;
        float amountX, amountY;
        if (e.getComponent().getComponentOrientation().isHorizontal()) {
            amountX = amount;
            amountY = 0;
        } else {
            amountX = 0;
            amountY = amount;
        }
        ContextListener listener = getContextListener();
        if (listener != null && listener.onScroll(this, amountX, amountY,
                scrollType == MouseWheelEvent.WHEEL_BLOCK_SCROLL ? 2 : 0)) e.consume();
    }

}
