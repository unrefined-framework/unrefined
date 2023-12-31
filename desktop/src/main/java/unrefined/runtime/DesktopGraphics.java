package unrefined.runtime;

import unrefined.desktop.AWTSupport;
import unrefined.desktop.AttributedCharSequence;
import unrefined.desktop.CharArrayIterator;
import unrefined.desktop.FontSupport;
import unrefined.desktop.TextHints;
import unrefined.desktop.TextPathLayout;
import unrefined.media.graphics.Bitmap;
import unrefined.media.graphics.Brush;
import unrefined.media.graphics.Composite;
import unrefined.media.graphics.Font;
import unrefined.media.graphics.Graphics;
import unrefined.media.graphics.Path;
import unrefined.media.graphics.RectangleF;
import unrefined.media.graphics.Text;
import unrefined.media.graphics.Transform;
import unrefined.util.AlreadyDisposedException;
import unrefined.util.PhantomString;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class DesktopGraphics extends Graphics {

    private volatile Graphics2D graphics2D;
    private volatile Graphics2D background;
    private volatile int width, height;
    private volatile Info info;

    DesktopGraphics() {
        info = new Info();
    }

    void setGraphics2D(Graphics2D graphics2D) {
        this.graphics2D = graphics2D;
        this.background = (Graphics2D) graphics2D.create();
        this.background.setComposite(AlphaComposite.Src);
    }

    public DesktopGraphics(Graphics2D graphics2D, int width, int height) {
        this.graphics2D = Objects.requireNonNull(graphics2D);
        this.background = (Graphics2D) graphics2D.create();
        this.background.setComposite(AlphaComposite.Src);
        this.width = width;
        this.height = height;
        info = new Info();
        reset();
    }

    public DesktopGraphics(Graphics2D graphics2D) {
        this(graphics2D, 0, 0);
    }

    public DesktopGraphics(BufferedImage image) {
        this(image.createGraphics(), image.getWidth(), image.getHeight());
    }

    public Graphics2D getGraphics2D() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return graphics2D;
    }

    public void setWidth(int width) {
        if (isDisposed()) throw new AlreadyDisposedException();
        this.width = width;
    }

    public void setHeight(int height) {
        if (isDisposed()) throw new AlreadyDisposedException();
        this.height = height;
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    @Override
    public int getWidth() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return width;
    }

    @Override
    public int getHeight() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return height;
    }

    @Override
    public void getSize(unrefined.media.graphics.Dimension size) {
        if (isDisposed()) throw new AlreadyDisposedException();
        size.setDimension(width, height);
    }

    public void getSize(Dimension dimension) {
        if (isDisposed()) throw new AlreadyDisposedException();
        dimension.setSize(width, height);
    }

    public void drawShape(Shape shape) {
        if (isDisposed()) throw new AlreadyDisposedException();
        switch (info.getStyle()) {
            case Style.STROKE: graphics2D.draw(shape); break;
            case Style.FILL: graphics2D.fill(shape); break;
        }
    }

    @Override
    public void clearColor() {
        if (isDisposed()) throw new AlreadyDisposedException();
        background.setColor(graphics2D.getBackground());
        background.fillRect(0, 0, width, height);
    }
    @Override
    public void clearRectangle(int x, int y, int width, int height) {
        if (isDisposed()) throw new AlreadyDisposedException();
        background.setColor(graphics2D.getBackground());
        background.fillRect(x, y, width, height);
    }

    @Override
    public void drawColor() {
        if (isDisposed()) throw new AlreadyDisposedException();
        graphics2D.fillRect(0, 0, width, height);
    }

    @Override
    public void drawPoint(float x, float y) {
        drawShape(new Line2D.Float(x, y, x, y));
    }

    @Override
    public void drawLine(float x1, float y1, float x2, float y2) {
        drawShape(new Line2D.Float(x1, y1, x2, y2));
    }

    @Override
    public void drawQuadLine(float x1, float y1, float cx1, float cy1, float x2, float y2) {
        drawShape(new QuadCurve2D.Float(x1, y1, cx1, cy1, x2, y2));
    }

    @Override
    public void drawCubicLine(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2) {
        drawShape(new CubicCurve2D.Float(x1, y1, cx1, cy1, cx2, cy2, x2, y2));
    }

    @Override
    public void drawArc(float x, float y, float width, float height, float startAngle, float sweepAngle) {
        drawShape(new Arc2D.Float(x, y, width, height, startAngle, sweepAngle, Arc2D.OPEN));
    }

    @Override
    public void drawCircle(float x, float y, float radius) {
        drawShape(new Ellipse2D.Float(x - radius, y - radius, x + radius, y + radius));
    }

    @Override
    public void drawEllipse(float x, float y, float width, float height) {
        drawShape(new Ellipse2D.Float(x, y, width, height));
    }

    @Override
    public void drawRectangle(float x, float y, float width, float height) {
        drawShape(new Rectangle2D.Float(x, y, width, height));
    }

    @Override
    public void drawRoundRectangle(float x, float y, float width, float height, float rx, float ry) {
        drawShape(new RoundRectangle2D.Float(x, y, width, height, rx, ry));
    }

    @Override
    public void drawBitmap(Bitmap bitmap, Transform transform) {
        if (isDisposed()) throw new AlreadyDisposedException();
        if (transform == null) graphics2D.drawImage(((DesktopBitmap) bitmap).getBufferedImage(), 0, 0, null);
        else graphics2D.drawImage(((DesktopBitmap) bitmap).getBufferedImage(), ((DesktopTransform) transform).getAffineTransform(), null);
    }

    @Override
    public void drawBitmap(Bitmap bitmap, float x, float y) {
        if (isDisposed()) throw new AlreadyDisposedException();
        graphics2D.drawRenderedImage(((DesktopBitmap) bitmap).getBufferedImage(), AffineTransform.getTranslateInstance(x, y));
    }

    @Override
    public void drawBitmapFrame(Bitmap.Frame frame, Transform transform) {
        if (isDisposed()) throw new AlreadyDisposedException();
        if (transform == null) graphics2D.drawImage(((DesktopBitmap) frame.getBitmap()).getBufferedImage(), frame.getHotSpotX(), frame.getHotSpotY(), null);
        else graphics2D.drawImage(((DesktopBitmap) frame.getBitmap()).getBufferedImage(), ((DesktopTransform) transform).getAffineTransform(), null);
    }

    @Override
    public void drawBitmapFrame(Bitmap.Frame frame, float x, float y) {
        if (isDisposed()) throw new AlreadyDisposedException();
        graphics2D.drawRenderedImage(((DesktopBitmap) frame.getBitmap()).getBufferedImage(), AffineTransform.getTranslateInstance(x, y));
    }

    @Override
    public void drawPath(Path path, Transform transform) {
        if (transform == null) drawShape(((DesktopPath) path).getPath2D());
        else drawShape(((DesktopTransform) transform).getAffineTransform().createTransformedShape(((DesktopPath) path).getPath2D()));
    }

    @Override
    public void drawPath(Path path, float x, float y) {
        drawShape(AffineTransform.getTranslateInstance(x, y).createTransformedShape(((DesktopPath) path).getPath2D()));
    }

    @Override
    public void drawText(CharSequence text, int start, int end, float x, float y) {
        if (isDisposed()) throw new AlreadyDisposedException();
        int textAlignment = getTextAlignment();
        TextLayout textLayout;
        if (text instanceof String) {
            String string = (String) text;
            if (start == 0 && end == string.length() && graphics2D.getFont().hasLayoutAttributes() && getStyle() == Style.FILL &&
                    textAlignment == Text.Alignment.START) {
                graphics2D.drawString(string, x, y);
                return;
            }
            else textLayout = new TextLayout(
                    new AttributedString(string.substring(start, end), graphics2D.getFont().getAttributes()).getIterator(),
                    graphics2D.getFontRenderContext());
        }
        else textLayout = new TextLayout(
                    new AttributedCharSequence(text.subSequence(start, end), graphics2D.getFont().getAttributes()).getIterator(),
                    graphics2D.getFontRenderContext());
        float advance = textLayout.getAdvance();
        switch (textAlignment) {
            case Text.Alignment.END: x -= advance; break;
            case Text.Alignment.MIDDLE: x -= advance / 2; break;
        }
        if (getStyle() == Style.STROKE) graphics2D.draw(textLayout.getOutline(AffineTransform.getTranslateInstance(x, y)));
        else textLayout.draw(graphics2D, x, y);
    }

    @Override
    public void drawText(CharSequence text, int start, int end, Transform transform) {
        if (isDisposed()) throw new AlreadyDisposedException();
        TextLayout textLayout = new TextLayout(
                new AttributedCharSequence(text.subSequence(start, end), graphics2D.getFont().getAttributes()).getIterator(),
                graphics2D.getFontRenderContext());
        int textAlignment = getTextAlignment();
        if (getStyle() == Style.STROKE && textAlignment == Text.Alignment.START)
            graphics2D.draw(textLayout.getOutline(((DesktopTransform) transform).getAffineTransform()));
        else {
            AffineTransform affineTransform = graphics2D.getTransform();
            graphics2D.transform(((DesktopTransform) transform).getAffineTransform());
            try {
                float x = 0;
                float advance = textLayout.getAdvance();
                switch (textAlignment) {
                    case Text.Alignment.END: x -= advance; break;
                    case Text.Alignment.MIDDLE: x -= advance / 2; break;
                }
                textLayout.draw(graphics2D, x, 0);
            }
            finally {
                graphics2D.setTransform(affineTransform);
            }
        }
    }

    @Override
    public void drawText(char[] text, int offset, int length, float x, float y) {
        drawText(new PhantomString(text, offset, length), x, y);
    }

    @Override
    public void drawText(char[] text, int offset, int length, Transform transform) {
        drawText(new PhantomString(text, offset, length), transform);
    }

    @Override
    public void drawTextOnPath(CharSequence text, int start, int end, Path path, float startOffset, int x, int y) {
        drawTextOnPath(Text.toCharArray(text, start, end), path, startOffset, x, y);
    }

    @Override
    public void drawTextOnPath(char[] text, int offset, int length, Path path, float startOffset, int x, int y) {
        if (isDisposed()) throw new AlreadyDisposedException();
        int direction = info.getTextDirection();
        final GlyphVector glyphVector;
        if (direction == Text.Direction.AUTO) glyphVector = graphics2D.getFont()
                .createGlyphVector(graphics2D.getFontRenderContext(), new CharArrayIterator(text, offset, length));
        else glyphVector = graphics2D.getFont()
                .layoutGlyphVector(graphics2D.getFontRenderContext(), text, offset, offset + length,
                        FontSupport.toFontFlag(info.getTextDirection()));
        Shape textOnPath = TextPathLayout.layoutGlyphVector(glyphVector, ((DesktopPath) path).getPath2D(),
                info.getTextAlignment(), startOffset, (float) glyphVector.getVisualBounds().getWidth(), TextPathLayout.ADJUST_SPACING);
        drawShape(AffineTransform.getTranslateInstance(x, y).createTransformedShape(textOnPath));
    }

    @Override
    public void drawTextOnPath(CharSequence text, int start, int end, Path path, float startOffset, Transform transform) {
        drawTextOnPath(Text.toCharArray(text, start, end), path, startOffset, transform);
    }

    @Override
    public void drawTextOnPath(char[] text, int offset, int length, Path path, float startOffset, Transform transform) {
        if (isDisposed()) throw new AlreadyDisposedException();
        int direction = info.getTextDirection();
        final GlyphVector glyphVector;
        if (direction == Text.Direction.AUTO) glyphVector = graphics2D.getFont()
                .createGlyphVector(graphics2D.getFontRenderContext(), new CharArrayIterator(text, offset, length));
        else glyphVector = graphics2D.getFont()
                .layoutGlyphVector(graphics2D.getFontRenderContext(), text, offset, offset + length,
                        FontSupport.toFontFlag(info.getTextDirection()));
        Shape textOnPath = TextPathLayout.layoutGlyphVector(glyphVector, ((DesktopPath) path).getPath2D(),
                info.getTextAlignment(), startOffset, (float) glyphVector.getVisualBounds().getWidth(), TextPathLayout.ADJUST_SPACING);
        if (transform == null) drawShape(textOnPath);
        else drawShape(((DesktopTransform) transform).getAffineTransform().createTransformedShape(textOnPath));
    }

    @Override
    public float measureText(CharSequence text, int start, int end) {
        if (isDisposed()) throw new AlreadyDisposedException();
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        Rectangle2D bounds2D;
        if (text instanceof String) {
            String string = (String) text;
            bounds2D = fontMetrics.getStringBounds(string, start, end, graphics2D);
        }
        else if (text instanceof PhantomString) {
            PhantomString string = (PhantomString) text;
            bounds2D = fontMetrics.getStringBounds(string.array(), start, end, graphics2D);
        }
        else {
            AttributedCharacterIterator iterator = new AttributedCharSequence(text).getIterator();
            bounds2D = fontMetrics.getStringBounds(iterator, start, end, graphics2D);
        }
        return (float) bounds2D.getWidth();
    }

    @Override
    public float measureText(char[] text, int offset, int length) {
        if (isDisposed()) throw new AlreadyDisposedException();
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        Rectangle2D bounds2D = fontMetrics.getStringBounds(text, offset, length, graphics2D);
        return (float) bounds2D.getWidth();
    }

    @Override
    public void measureText(CharSequence text, int start, int end, Text.Metrics metrics, RectangleF bounds) {
        if (isDisposed()) throw new AlreadyDisposedException();
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        LineMetrics lineMetrics;
        Rectangle2D bounds2D;
        if (text instanceof String) {
            String string = (String) text;
            lineMetrics = fontMetrics.getLineMetrics(string, start, end, graphics2D);
            bounds2D = fontMetrics.getStringBounds(string, start, end, graphics2D);
        }
        else if (text instanceof PhantomString) {
            PhantomString string = (PhantomString) text;
            lineMetrics = fontMetrics.getLineMetrics(string.array(), start, end, graphics2D);
            bounds2D = fontMetrics.getStringBounds(string.array(), start, end, graphics2D);
        }
        else {
            AttributedCharacterIterator iterator = new AttributedCharSequence(text).getIterator();
            lineMetrics = fontMetrics.getLineMetrics(iterator, start, end, graphics2D);
            bounds2D = fontMetrics.getStringBounds(iterator, start, end, graphics2D);
        }
        if (metrics != null) metrics.setMetrics(lineMetrics.getBaselineOffsets()[lineMetrics.getBaselineIndex()],
                lineMetrics.getAscent(), lineMetrics.getDescent(), lineMetrics.getLeading(),
                fontMetrics.getMaxAscent(), fontMetrics.getMaxDescent(), fontMetrics.getMaxAdvance());
        if (bounds != null) AWTSupport.floatRectangle(bounds2D, bounds);
    }

    @Override
    public void measureText(CharSequence text, int start, int end, Text.Metrics metrics) {
        if (isDisposed()) throw new AlreadyDisposedException();
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        LineMetrics lineMetrics;
        if (text instanceof String) {
            String string = (String) text;
            lineMetrics = fontMetrics.getLineMetrics(string, start, end, graphics2D);
        }
        else if (text instanceof PhantomString) {
            PhantomString string = (PhantomString) text;
            lineMetrics = fontMetrics.getLineMetrics(string.array(), start, end, graphics2D);
        }
        else {
            AttributedCharacterIterator iterator = new AttributedCharSequence(text).getIterator();
            lineMetrics = fontMetrics.getLineMetrics(iterator, start, end, graphics2D);
        }
        metrics.setMetrics(lineMetrics.getBaselineOffsets()[lineMetrics.getBaselineIndex()],
                lineMetrics.getAscent(), lineMetrics.getDescent(), lineMetrics.getLeading(),
                fontMetrics.getMaxAscent(), fontMetrics.getMaxDescent(), fontMetrics.getMaxAdvance());
    }

    @Override
    public void measureText(CharSequence text, int start, int end, RectangleF bounds) {
        if (isDisposed()) throw new AlreadyDisposedException();
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        Rectangle2D bounds2D;
        if (text instanceof String) {
            String string = (String) text;
            bounds2D = fontMetrics.getStringBounds(string, start, end, graphics2D);
        }
        else if (text instanceof PhantomString) {
            PhantomString string = (PhantomString) text;
            bounds2D = fontMetrics.getStringBounds(string.array(), start, end, graphics2D);
        }
        else {
            AttributedCharacterIterator iterator = new AttributedCharSequence(text).getIterator();
            bounds2D = fontMetrics.getStringBounds(iterator, start, end, graphics2D);
        }
        AWTSupport.floatRectangle(bounds2D, bounds);
    }

    @Override
    public void measureText(char[] text, int offset, int length, Text.Metrics metrics, RectangleF bounds) {
        if (isDisposed()) throw new AlreadyDisposedException();
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        LineMetrics lineMetrics = fontMetrics.getLineMetrics(text, offset, length, graphics2D);
        Rectangle2D bounds2D = fontMetrics.getStringBounds(text, offset, length, graphics2D);
        if (metrics != null) metrics.setMetrics(lineMetrics.getBaselineOffsets()[lineMetrics.getBaselineIndex()],
                lineMetrics.getAscent(), lineMetrics.getDescent(), lineMetrics.getLeading(),
                fontMetrics.getMaxAscent(), fontMetrics.getMaxDescent(), fontMetrics.getMaxAdvance());
        if (bounds != null) AWTSupport.floatRectangle(bounds2D, bounds);
    }

    @Override
    public void measureText(char[] text, int offset, int length, Text.Metrics metrics) {
        if (isDisposed()) throw new AlreadyDisposedException();
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        LineMetrics lineMetrics = fontMetrics.getLineMetrics(text, offset, length, graphics2D);
        metrics.setMetrics(lineMetrics.getBaselineOffsets()[lineMetrics.getBaselineIndex()],
                lineMetrics.getAscent(), lineMetrics.getDescent(), lineMetrics.getLeading(),
                fontMetrics.getMaxAscent(), fontMetrics.getMaxDescent(), fontMetrics.getMaxAdvance());
    }

    @Override
    public void measureText(char[] text, int offset, int length, RectangleF bounds) {
        if (isDisposed()) throw new AlreadyDisposedException();
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        Rectangle2D bounds2D = fontMetrics.getStringBounds(text, offset, length, graphics2D);
        AWTSupport.floatRectangle(bounds2D, bounds);
    }

    @Override
    public void hitText(CharSequence text, int start, int end, float xOffset, Text.HitInfo hitInfo) {
        if (isDisposed()) throw new AlreadyDisposedException();
        TextLayout textLayout = new TextLayout(
                new AttributedCharSequence(text.subSequence(start, end), graphics2D.getFont().getAttributes()).getIterator(),
                graphics2D.getFontRenderContext());
        TextHitInfo textHitInfo = textLayout.hitTestChar(xOffset, 0);
        int insertionIndex = textHitInfo.getInsertionIndex();
        Rectangle2D bounds = textLayout.getBounds();
        boolean outBounds = xOffset < bounds.getX() || xOffset >= bounds.getWidth();
        int charIndex = outBounds ? -1 : textHitInfo.getCharIndex();
        boolean leadingEdge = textHitInfo.isLeadingEdge() || outBounds;
        hitInfo.setHitInfo(charIndex, insertionIndex, leadingEdge);
    }

    @Override
    public void hitText(char[] text, int offset, int length, float xOffset, Text.HitInfo hitInfo) {
        hitText(new PhantomString(text, offset, length), xOffset, hitInfo);
    }

    @Override
    public void getInfo(Info info) {
        if (isDisposed()) throw new AlreadyDisposedException();
        this.info.to(info);
    }

    @Override
    public Path getClip() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getClip();
    }

    @Override
    public void setClip(Path clip) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setClip(clip);
        graphics2D.setClip(clip == null ? null : ((DesktopPath) clip).getPath2D());
    }

    @Override
    public Transform getTransform() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getTransform();
    }

    @Override
    public void setTransform(Transform transform) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setTransform(transform);
        graphics2D.setTransform(transform == null ? AWTSupport.getDefaultTransform(graphics2D) : ((DesktopTransform) transform).getAffineTransform());
    }

    @Override
    public Composite getComposite() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getComposite();
    }

    @Override
    public void setComposite(Composite composite) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setComposite(composite);
        graphics2D.setComposite(composite == null ? AlphaComposite.SrcOver : ((DesktopComposite) composite).getComposite());
    }

    @Override
    public Brush getBrush() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getBrush();
    }

    @Override
    public void setBrush(Brush brush) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setBrush(brush);
        graphics2D.setPaint(brush == null ? Color.BLACK : ((DesktopBrush) brush).getPaint());
    }

    @Override
    public int getStyle() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getStyle();
    }

    @Override
    public void setStyle(int style) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setStyle(style);
    }

    @Override
    public float getStrokeWidth() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getStrokeWidth();
    }

    @Override
    public void setStrokeWidth(float width) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setStrokeWidth(width);
        graphics2D.setStroke(new BasicStroke(width, info.getStrokeCap(), info.getStrokeJoin(), info.getStrokeMiterLimit()));
    }

    @Override
    public int getStrokeJoin() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getStrokeJoin();
    }

    @Override
    public void setStrokeJoin(int join) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setStrokeJoin(join);
        graphics2D.setStroke(new BasicStroke(info.getStrokeWidth(), info.getStrokeCap(), join, info.getStrokeMiterLimit()));
    }

    @Override
    public int getStrokeCap() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getStrokeCap();
    }

    @Override
    public void setStrokeCap(int cap) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setStrokeCap(cap);
        graphics2D.setStroke(new BasicStroke(info.getStrokeWidth(), cap, info.getStrokeJoin(), info.getStrokeMiterLimit()));
    }

    @Override
    public float getStrokeMiterLimit() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getStrokeMiterLimit();
    }

    @Override
    public void setStrokeMiterLimit(float miterLimit) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setStrokeMiterLimit(miterLimit);
        graphics2D.setStroke(new BasicStroke(info.getStrokeWidth(), info.getStrokeCap(), info.getStrokeJoin(), miterLimit));
    }

    @Override
    public float[] getStrokeDash() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getStrokeDash().clone();
    }

    @Override
    public void setStrokeDash(float... dash) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setStrokeDash(dash);
        graphics2D.setStroke(new BasicStroke(info.getStrokeWidth(), info.getStrokeCap(), info.getStrokeJoin(), info.getStrokeMiterLimit(),
                info.getStrokeDash(), info.getStrokeDashPhase()));
    }

    @Override
    public float getStrokeDashPhase() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getStrokeDashPhase();
    }

    @Override
    public void setStrokeDashPhase(float phase) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setStrokeDashPhase(phase);
        graphics2D.setStroke(new BasicStroke(info.getStrokeWidth(), info.getStrokeCap(), info.getStrokeJoin(), info.getStrokeMiterLimit(),
                info.getStrokeDash(), phase));
    }

    @Override
    public void setStroke(float width, int join, int cap, float miterLimit, float[] dash, float dashPhase) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setStroke(width, join, cap, miterLimit, dash, dashPhase);
        graphics2D.setStroke(new BasicStroke(width, join, cap, miterLimit, dash, dashPhase));
    }

    @Override
    public boolean isAntiAlias() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.isAntiAlias();
    }

    @Override
    public void setAntiAlias(boolean antiAlias) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setAntiAlias(antiAlias);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                antiAlias ? TextHints.VALUE_TEXT_ANTIALIAS_SYSTEM_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    @Override
    public boolean isImageSmoothing() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.isImageSmoothing();
    }

    @Override
    public void setImageSmoothing(boolean imageSmoothing) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setImageSmoothing(imageSmoothing);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, imageSmoothing ? RenderingHints.VALUE_INTERPOLATION_BILINEAR : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, imageSmoothing ? RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY : RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
    }

    @Override
    public boolean isDither() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.isDither();
    }

    @Override
    public void setDither(boolean dither) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setDither(dither);
        graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, dither ? RenderingHints.VALUE_DITHER_ENABLE : RenderingHints.VALUE_DITHER_DISABLE);
    }

    @Override
    public Font getFont() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getFont();
    }

    @Override
    public void setFont(Font font) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setFont(font);
        graphics2D.setFont((font == null ? FontSupport.getDefaultFont() : ((DesktopFont) font).getFont()).deriveFont(graphics2D.getFont().getAttributes()));
    }

    @Override
    public float getTextSize() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getTextSize();
    }

    @Override
    public void setTextSize(float size) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setTextSize(size);
        graphics2D.setFont(graphics2D.getFont().deriveFont(size));
    }

    @Override
    public int getTextDirection() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getTextDirection();
    }

    @Override
    public void setTextDirection(int direction) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setTextDirection(direction);
        Map<TextAttribute, Object> attributes = new HashMap<>(1);
        attributes.put(TextAttribute.RUN_DIRECTION, TextHints.toRunDirection(direction));
        graphics2D.setFont(graphics2D.getFont().deriveFont(attributes));
    }

    @Override
    public int getTextAlignment() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getTextAlignment();
    }

    @Override
    public void setTextAlignment(int align) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setTextAlignment(align);
    }

    @Override
    public boolean isSubpixelText() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.isSubpixelText();
    }

    @Override
    public void setSubpixelText(boolean subpixelText) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setSubpixelText(subpixelText);
        graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                subpixelText ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    }

    @Override
    public boolean isUnderlineText() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.isUnderlineText();
    }

    @Override
    public void setUnderlineText(boolean underlineText) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setUnderlineText(underlineText);
        graphics2D.setFont(graphics2D.getFont().deriveFont(Collections.singletonMap(TextAttribute.UNDERLINE, TextHints.UNDERLINE_OFF)));
    }

    @Override
    public boolean isStrikeThroughText() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.isStrikeThroughText();
    }

    @Override
    public void setStrikeThroughText(boolean strikeThroughText) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setStrikeThroughText(strikeThroughText);
        graphics2D.setFont(graphics2D.getFont().deriveFont(Collections.singletonMap(TextAttribute.STRIKETHROUGH, TextHints.STRIKETHROUGH_OFF)));
    }

    @Override
    public void setTextScaleX(float scaleX) {
        setTextScale(scaleX, info.getTextScaleY());
    }

    @Override
    public void setTextScaleY(float scaleY) {
        setTextScale(info.getTextScaleX(), scaleY);
    }

    @Override
    public void setTextScale(float scaleX, float scaleY) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setTextScale(scaleX, scaleY);
        java.awt.Font font = graphics2D.getFont();
        AffineTransform transform = font.getTransform();
        transform.setToScale(scaleX, scaleY);
        graphics2D.setFont(font.deriveFont(transform));
    }

    @Override
    public float getTextScaleX() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getTextScaleX();
    }

    @Override
    public float getTextScaleY() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getTextScaleY();
    }

    @Override
    public void getTextScale(float[] scale, int offset) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.getTextScale(scale, offset);
    }

    @Override
    public void setTextSkewX(float skewX) {
        setTextSkew(skewX, info.getTextSkewY());
    }

    @Override
    public void setTextSkewY(float skewY) {
        setTextSkew(info.getTextSkewX(), skewY);
    }

    @Override
    public void setTextSkew(float skewX, float skewY) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setTextSkew(skewX, skewY);
        java.awt.Font font = graphics2D.getFont();
        AffineTransform transform = font.getTransform();
        transform.setToShear(skewX, skewY);
        graphics2D.setFont(font.deriveFont(transform));
    }

    @Override
    public float getTextSkewX() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getTextSkewX();
    }

    @Override
    public float getTextSkewY() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getTextSkewY();
    }

    @Override
    public void getTextSkew(float[] skew, int offset) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.getTextSkew(skew, offset);
    }

    private final AtomicBoolean disposed = new AtomicBoolean(false);

    @Override
    public void dispose() {
        if (disposed.compareAndSet(false, true)) {
            graphics2D.dispose();
            graphics2D = null;
            background.dispose();
            background = null;
            info = null;
        }
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopGraphics graphics = (DesktopGraphics) object;

        if (width != graphics.width) return false;
        if (height != graphics.height) return false;
        if (!Objects.equals(graphics2D, graphics.graphics2D)) return false;
        return Objects.equals(info, graphics.info);
    }

    @Override
    public int hashCode() {
        int result = graphics2D != null ? graphics2D.hashCode() : 0;
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + (info != null ? info.hashCode() : 0);
        return result;
    }

}
