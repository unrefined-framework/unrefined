package unrefined.runtime;

import unrefined.core.AnimatedCursor;
import unrefined.context.Container;
import unrefined.context.Context;
import unrefined.context.ContextListener;
import unrefined.desktop.AWTSupport;
import unrefined.desktop.CursorAnimator;
import unrefined.desktop.KeyEventParser;
import unrefined.media.graphics.Cursor;
import unrefined.media.graphics.Dimension;
import unrefined.media.graphics.Point;
import unrefined.media.graphics.Rectangle;
import unrefined.media.input.Input;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class DesktopEmbeddedContext extends Context implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, FocusListener {

    private final Component component;

    public DesktopEmbeddedContext(Container container, Component component) {
        this(container, null, component);
    }

    public DesktopEmbeddedContext(Container container, ContextListener contextListener, Component component) {
        super(container, contextListener);
        this.component = Objects.requireNonNull(component);

        component.setIgnoreRepaint(true);
        component.setBackground(AWTSupport.TRANSPARENT);
        component.enableInputMethods(false);

        component.addKeyListener(this);
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addMouseWheelListener(this);
        component.addFocusListener(this);
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
        component.setPreferredSize(new java.awt.Dimension(width, component.getHeight()));
    }

    @Override
    public void setHeight(int height) {
        component.setSize(component.getWidth(), height);
        component.setPreferredSize(new java.awt.Dimension(component.getWidth(), height));
    }

    @Override
    public void setSize(int width, int height) {
        component.setSize(width, height);
        component.setPreferredSize(new java.awt.Dimension(width, height));
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

    @Override
    public void focusGained(FocusEvent e) {}

    @Override
    public void focusLost(FocusEvent e) {
        pressedCode.clear();
    }

    private volatile Cursor cursor = DesktopCursor.getDefaultCursor();
    @Override
    public void setCursor(Cursor cursor) {
        this.cursor = cursor == null ? DesktopCursor.getDefaultCursor() : cursor;
        if (!GraphicsEnvironment.isHeadless() && cursor instanceof AnimatedCursor)
            EventQueue.invokeLater(() -> CursorAnimator.register(component, (AnimatedCursor) cursor));
        else component.setCursor(((DesktopCursor) this.cursor).getCursor());
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    private final Set<String> pressedCode = new HashSet<>();
    private volatile boolean repeat = false;

    @Override
    public void keyTyped(KeyEvent e) {
        if (listener().onKeyTyped(this, e.getKeyChar(), repeat)) e.consume();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        String code = KeyEventParser.parseCode(e);
        repeat = pressedCode.contains(code);
        pressedCode.add(code);
        if (listener().onKeyDown(this,
                KeyEventParser.parseKey(e),
                KeyEventParser.parseCode(e),
                e.getKeyLocation() - 1,
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6), repeat)) e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        String code = KeyEventParser.parseCode(e);
        if (pressedCode.contains(code)) {
            if (listener().onKeyUp(this,
                    KeyEventParser.parseKey(e),
                    KeyEventParser.parseCode(e),
                    e.getKeyLocation() - 1,
                    Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6))) e.consume();
        }
        pressedCode.remove(code);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        ContextListener listener = listener();
        if (listener != null && listener.onPointerClick(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6),
                e.getClickCount(), e.getButton() - 1)) e.consume();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ContextListener listener = listener();
        if (listener != null && listener.onPointerDown(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6),
                e.getClickCount(), e.getButton() - 1)) e.consume();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ContextListener listener = listener();
        if (listener != null && listener.onPointerUp(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6),
                e.getClickCount(), e.getButton() - 1)) e.consume();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!GraphicsEnvironment.isHeadless() && cursor instanceof AnimatedCursor)
            EventQueue.invokeLater(() -> CursorAnimator.register(component, (AnimatedCursor) cursor));
        ContextListener listener = listener();
        if (listener != null && listener.onMouseEnter(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6))) e.consume();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!GraphicsEnvironment.isHeadless()) EventQueue.invokeLater(() -> CursorAnimator.unregister(component));
        ContextListener listener = listener();
        if (listener != null && listener.onMouseExit(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6))) e.consume();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        ContextListener listener = listener();
        if (listener != null && listener.onPointerDrag(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6),
                e.getClickCount())) e.consume();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        ContextListener listener = listener();
        if (listener != null && listener.onMouseMove(this, e.getX(), e.getY(),
                Input.KeyModifier.removeUnusedBits(e.getModifiersEx() >>> 6))) e.consume();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int scrollType = e.getScrollType();
        float amount;
        switch (scrollType) {
            case MouseWheelEvent.WHEEL_UNIT_SCROLL: amount = (float) (e.getScrollAmount() * e.getPreciseWheelRotation()); break;
            case MouseWheelEvent.WHEEL_BLOCK_SCROLL: amount = e.getWheelRotation() > 0 ? 1 : -1; break;
            default: amount = 0; break;
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
        ContextListener listener = listener();
        if (listener != null && listener.onScroll(this, amountX, amountY,
                scrollType == MouseWheelEvent.WHEEL_BLOCK_SCROLL ? 2 : 0)) e.consume();
    }

}
