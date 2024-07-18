package unrefined.desktop;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public class TransformedGraphics2D extends FilteredGraphics2D {

    private final AffineTransform transform = new AffineTransform();
    private final AffineTransform originalTransform;
    private final AffineTransform applyTransform = new AffineTransform();
    private Shape clip = null;
    private final Shape originalClip;

    public TransformedGraphics2D(Graphics2D graphics2D, AffineTransform transform, Shape clip) {
        super(graphics2D);
        originalTransform = transform == null ? new AffineTransform() : new AffineTransform(transform);
        originalClip = clip;
    }

    public TransformedGraphics2D(Graphics2D graphics2D, int x, int y, int width, int height) {
        this(graphics2D, AffineTransform.getTranslateInstance(x, y), new Rectangle(0, 0, width, height));
    }

    private void applyClip() {
        if (clip == null) graphics2D.setClip(null);
        else graphics2D.clip(transform.createTransformedShape(clip));
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        clip(new Rectangle(x, y, width, height));
    }

    @Override
    public void clip(Shape s) {
        if (clip == null) clip = s;
        else clip = AWTSupport.intersectShapes(clip, s, true, true);
        applyClip();
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        setClip(new Rectangle(x, y, width, height));
    }

    @Override
    public void setClip(Shape clip) {
        this.clip = clip;
        applyClip();
    }

    @Override
    public Rectangle getClipBounds() {
        return clip == null ? null : getClipBounds(new Rectangle());
    }

    @Override
    public Rectangle getClipBounds(Rectangle r) {
        if (clip != null) {
            if (clip instanceof Rectangle) r.setBounds((Rectangle) clip);
            else r.setFrame(clip.getBounds2D());
        }
        return r;
    }

    @Override
    public Shape getClip() {
        return clip;
    }

    private void applyTransform() {
        applyTransform.setTransform(originalTransform);
        applyTransform.concatenate(transform);
        graphics2D.setTransform(applyTransform);
    }

    @Override
    public void translate(int x, int y) {
        transform.translate(x, y);
        applyTransform();
    }

    @Override
    public void translate(double tx, double ty) {
        transform.translate(tx, ty);
        applyTransform();
    }

    @Override
    public void rotate(double theta) {
        transform.rotate(theta);
        applyTransform();
    }

    @Override
    public void rotate(double theta, double x, double y) {
        transform.rotate(theta, x, y);
        applyTransform();
    }

    @Override
    public void scale(double sx, double sy) {
        transform.scale(sx, sy);
        applyTransform();
    }

    @Override
    public void shear(double shx, double shy) {
        transform.shear(shx, shy);
        applyTransform();
    }

    @Override
    public void transform(AffineTransform Tx) {
        transform.concatenate(Tx);
        applyTransform();
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        transform.setTransform(Tx);
        applyTransform();
    }

    @Override
    public AffineTransform getTransform() {
        return new AffineTransform(transform);
    }

    @Override
    public Graphics create(int x, int y, int width, int height) {
        return new TransformedGraphics2D((Graphics2D) graphics2D.create(),0, 0, width, height);
    }

}
