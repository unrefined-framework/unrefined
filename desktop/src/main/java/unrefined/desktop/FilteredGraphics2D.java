package unrefined.desktop;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.Objects;

public abstract class FilteredGraphics2D extends Graphics2D {

    protected final Graphics2D graphics2D;

    public FilteredGraphics2D(Graphics2D graphics2D) {
        this.graphics2D = Objects.requireNonNull(graphics2D);
    }

    @Override
    public void draw(Shape s) {
        graphics2D.draw(s);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        return graphics2D.drawImage(img, xform, obs);
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        graphics2D.drawImage(img, op, x, y);
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        graphics2D.drawRenderedImage(img, xform);
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        graphics2D.drawRenderableImage(img, xform);
    }

    @Override
    public void drawString(String str, int x, int y) {
        graphics2D.drawString(str, x, y);
    }

    @Override
    public void drawString(String str, float x, float y) {
        graphics2D.drawString(str, x, y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        graphics2D.drawString(iterator, x, y);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return graphics2D.drawImage(img, x, y, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return graphics2D.drawImage(img, x, y, width, height, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return graphics2D.drawImage(img, x, y, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        return graphics2D.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return graphics2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        return graphics2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    @Override
    public void dispose() {
        graphics2D.dispose();
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        graphics2D.drawString(iterator, x, y);
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        graphics2D.drawGlyphVector(g, x, y);
    }

    @Override
    public void fill(Shape s) {
        graphics2D.fill(s);
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return graphics2D.hit(rect, s, onStroke);
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return graphics2D.getDeviceConfiguration();
    }

    @Override
    public void setComposite(Composite comp) {
        graphics2D.setComposite(comp);
    }

    @Override
    public void setPaint(Paint paint) {
        graphics2D.setPaint(paint);
    }

    @Override
    public void setStroke(Stroke s) {
        graphics2D.setStroke(s);
    }

    @Override
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        graphics2D.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return graphics2D.getRenderingHint(hintKey);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        graphics2D.setRenderingHints(hints);
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        graphics2D.addRenderingHints(hints);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return graphics2D.getRenderingHints();
    }

    @Override
    public Graphics create() {
        return graphics2D.create();
    }

    @Override
    public void translate(int x, int y) {
        graphics2D.translate(x, y);
    }

    @Override
    public Color getColor() {
        return graphics2D.getColor();
    }

    @Override
    public void setColor(Color c) {
        graphics2D.setColor(c);
    }

    @Override
    public void setPaintMode() {
        graphics2D.setPaintMode();
    }

    @Override
    public void setXORMode(Color c1) {
        graphics2D.setXORMode(c1);
    }

    @Override
    public Font getFont() {
        return graphics2D.getFont();
    }

    @Override
    public void setFont(Font font) {
        graphics2D.setFont(font);
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return graphics2D.getFontMetrics(f);
    }

    @Override
    public Rectangle getClipBounds() {
        return graphics2D.getClipBounds();
    }

    @Override
    public Rectangle getClipBounds(Rectangle r) {
        return graphics2D.getClipBounds(r);
    }

    @Override
    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        graphics2D.draw3DRect(x, y, width, height, raised);
    }

    @Override
    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
        graphics2D.fill3DRect(x, y, width, height, raised);
    }

    @Override
    public Graphics create(int x, int y, int width, int height) {
        return graphics2D.create(x, y, width, height);
    }

    @Override
    public FontMetrics getFontMetrics() {
        return graphics2D.getFontMetrics();
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        graphics2D.drawRect(x, y, width, height);
    }

    @Override
    public void drawPolygon(Polygon p) {
        graphics2D.drawPolygon(p);
    }

    @Override
    public void fillPolygon(Polygon p) {
        graphics2D.fillPolygon(p);
    }

    @Override
    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        graphics2D.drawBytes(data, offset, length, x, y);
    }

    @Override
    public void drawChars(char[] data, int offset, int length, int x, int y) {
        graphics2D.drawChars(data, offset, length, x, y);
    }

    @Override
    public boolean hitClip(int x, int y, int width, int height) {
        return graphics2D.hitClip(x, y, width, height);
    }

    @Override
    public Rectangle getClipRect() {
        return graphics2D.getClipRect();
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        graphics2D.clipRect(x, y, width, height);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        graphics2D.setClip(x, y, width, height);
    }

    @Override
    public Shape getClip() {
        return graphics2D.getClip();
    }

    @Override
    public void setClip(Shape clip) {
        graphics2D.setClip(clip);
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        graphics2D.copyArea(x, y, width, height, dx, dy);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        graphics2D.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        graphics2D.fillRect(x, y, width, height);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        graphics2D.clearRect(x, y, width, height);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        graphics2D.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        graphics2D.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        graphics2D.drawOval(x, y, width, height);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        graphics2D.fillOval(x, y, width, height);
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        graphics2D.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        graphics2D.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        graphics2D.drawPolyline(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        graphics2D.drawPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        graphics2D.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void translate(double tx, double ty) {
        graphics2D.translate(tx, ty);
    }

    @Override
    public void rotate(double theta) {
        graphics2D.rotate(theta);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        graphics2D.rotate(theta, x, y);
    }

    @Override
    public void scale(double sx, double sy) {
        graphics2D.scale(sx, sy);
    }

    @Override
    public void shear(double shx, double shy) {
        graphics2D.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform Tx) {
        graphics2D.transform(Tx);
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        graphics2D.setTransform(Tx);
    }

    @Override
    public AffineTransform getTransform() {
        return graphics2D.getTransform();
    }

    @Override
    public Paint getPaint() {
        return graphics2D.getPaint();
    }

    @Override
    public Composite getComposite() {
        return graphics2D.getComposite();
    }

    @Override
    public void setBackground(Color color) {
        graphics2D.setBackground(color);
    }

    @Override
    public Color getBackground() {
        return graphics2D.getBackground();
    }

    @Override
    public Stroke getStroke() {
        return graphics2D.getStroke();
    }

    @Override
    public void clip(Shape s) {
        graphics2D.clip(s);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return graphics2D.getFontRenderContext();
    }

}
