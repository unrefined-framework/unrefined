/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package unrefined.desktop;

import unrefined.util.AutoReleaseReference;
import unrefined.util.concurrent.Producer;
import unrefined.util.function.Assert;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Paints drop shadow effect.
 */
public class DropShadow {

    private float dx, dy, radius, spread;
    private final GaussianBlur gb = new GaussianBlur();

    public DropShadow(float dx, float dy, float radius, float spread) {
        this.dx = dx;
        this.dy = dy;
        this.radius = radius;
        gb.setRadius(radius);
        this.spread = spread;
    }

    public float getX() {
        return dx;
    }

    public void setX(float x) {
        this.dx = x;
    }

    public float getY() {
        return dy;
    }

    public void setY(float y) {
        this.dy = y;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        gb.setRadius(radius);
    }

    public float getSpread() {
        return spread;
    }

    public void setSpread(float spread) {
        this.spread = spread;
    }

    public void fill(Shape shape, Graphics2D g) {
        paint(shape, g, false);
    }

    public void draw(Shape shape, Graphics2D g) {
        paint(shape, g, true);
    }

    private static final AutoReleaseReference<BufferedImage> cached = new AutoReleaseReference<>();

    private void paint(Shape shape, Graphics2D g, boolean stroke) {

        if (stroke) {
            Stroke s = g.getStroke();
            if (s != null) shape = s.createStrokedShape(shape);
        }

        Area area = new Area(shape);

        // Bounds of outer shadow shape. Blur size is not taken in consideration.
        Rectangle2D r = area.getBounds2D();

        // Outer shadow must contain shape itself.
        area.add(new Area(area));
        area.transform(AffineTransform.getTranslateInstance(-r.getX(), -r.getY()));

        // Create shadow shape considering spread.
        double sx = 1 + spread / r.getWidth();
        double sy = 1 + spread / r.getHeight();
        if (Double.compare(spread, 0d) != 0) {
            area = new Area(AffineTransform.getScaleInstance(sx, sy).createTransformedShape(area));
        }

        // Create shadow image.
        int width = (int) (r.getWidth() * sx + 4 * radius) + /*magic 2 pixels to avoid redundant color lines*/2;
        int height = (int) (r.getHeight() * sy + 4 * radius) + 2;
        BufferedImage bi = cached.setIfAndGet(
                image -> image == null || image.getWidth() < width || image.getHeight() < height,
                () -> new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));

        Graphics2D g2d = bi.createGraphics();
        g2d.setBackground(AWTSupport.TRANSPARENT);
        g2d.clearRect(0, 0, bi.getWidth(), bi.getHeight());
        g2d.setRenderingHints(g.getRenderingHints());
        g2d.setColor(g.getColor());
        g2d.translate(2 * radius, 2 * radius);
        g2d.fill(area);

        Rectangle2D rct = new Rectangle2D.Double(dx + r.getX() - 2 * radius,
                dy + r.getY() - 2 * radius,
                bi.getWidth(),
                bi.getHeight());


        gb.filter(bi, bi);

        g.setPaint(new TexturePaint(bi, rct));

        Area rect = new Area(rct);
        g.fill(rect);
    }

}

