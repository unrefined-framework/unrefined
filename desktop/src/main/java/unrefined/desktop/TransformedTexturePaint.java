package unrefined.desktop;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.Objects;

public class TransformedTexturePaint implements Paint {

    private final BufferedImage image;
    private final AffineTransform transform;
    private final TexturePaint paint;

    public TransformedTexturePaint(BufferedImage image, AffineTransform transform) {
        paint = new TexturePaint(this.image = Objects.requireNonNull(image), new Rectangle(0, 0, image.getWidth(), image.getHeight()));
        this.transform = Objects.requireNonNull(transform);
    }

    public BufferedImage getImage() {
        return image;
    }

    public AffineTransform getTransform() {
        return transform;
    }

    @Override
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        if (xform == null) xform = new AffineTransform(transform);
        else {
            xform = (AffineTransform) xform.clone();
            xform.concatenate(transform);
        }
        return paint.createContext(cm, deviceBounds, userBounds, xform, hints);
    }

    @Override
    public int getTransparency() {
        return image.getColorModel().getTransparency();
    }

}
