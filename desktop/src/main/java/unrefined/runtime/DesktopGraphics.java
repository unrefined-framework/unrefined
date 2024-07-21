package unrefined.runtime;

import unrefined.desktop.AWTSupport;
import unrefined.desktop.AttributedCharSequence;
import unrefined.media.graphics.Paragraph;
import unrefined.text.CharArrayCharacterIterator;
import unrefined.desktop.CleanerSupport;
import unrefined.desktop.DropShadow;
import unrefined.desktop.FontSupport;
import unrefined.desktop.TextHints;
import unrefined.desktop.TextPathLayout;
import unrefined.desktop.TransformedGraphics2D;
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
import unrefined.util.CharSequences;
import unrefined.util.PhantomString;
import unrefined.util.concurrent.ConcurrentHashSet;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class DesktopGraphics extends Graphics {

    private volatile Graphics2D graphics2D;
    private volatile int width, height;
    private volatile Info info;

    DesktopGraphics() {
        info = new Info();
    }

    void cleanup() {
        if (this.graphics2D != null) {
            this.graphics2D.dispose();
            this.graphics2D = null;
        }
    }

    void clearBackground(Color color) {
        graphics2D.setBackground(color);
        graphics2D.setColor(color);
        graphics2D.fillRect(0, 0, width, height);
    }

    void setGraphics2D(Graphics2D graphics2D) {
        cleanup();
        this.graphics2D = graphics2D;
        graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    public DesktopGraphics(Graphics2D graphics2D, int width, int height) {
        this.graphics2D = Objects.requireNonNull(graphics2D);
        graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        this.width = width;
        this.height = height;
        info = new Info();
        reset();
        CleanerSupport.register(this, this::dispose);
    }

    public DesktopGraphics(BufferedImage image) {
        this(image.createGraphics(), image.getWidth(), image.getHeight());
    }

    private final Set<DesktopGraphics> subgraphics = new ConcurrentHashSet<>();
    private volatile DesktopGraphics attachment;

    protected DesktopGraphics(DesktopGraphics attachment, int x, int y, int width, int height) {
        this(new TransformedGraphics2D((Graphics2D) Objects.requireNonNull(attachment).graphics2D.create(), x, y, width, height), width, height);
        this.attachment = attachment;
        attachment.subgraphics.add(this);
    }

    protected DesktopGraphics(DesktopGraphics attachment) {
        this((Graphics2D) Objects.requireNonNull(attachment).graphics2D.create(), attachment.width, attachment.height);
        this.attachment = attachment;
        attachment.subgraphics.add(this);
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
            case Style.STROKE:
                if (info.getShadowColor() != unrefined.media.graphics.Color.TRANSPARENT) {
                    Paint originalPaint = graphics2D.getPaint();
                    graphics2D.setColor(shadowColor);
                    try {
                        if (info.getShadowBlur() == 0)
                            graphics2D.draw(AffineTransform.getTranslateInstance(info.getShadowOffsetX(), info.getShadowOffsetY()).createTransformedShape(shape));
                        else new DropShadow(getShadowOffsetX(), getShadowOffsetY(), getShadowBlur(), 0).draw(shape, graphics2D);
                    }
                    finally {
                        graphics2D.setPaint(originalPaint);
                    }
                }
                graphics2D.draw(shape);
                break;
            case Style.FILL:
                if (info.getShadowColor() != unrefined.media.graphics.Color.TRANSPARENT) {
                    Paint originalPaint = graphics2D.getPaint();
                    graphics2D.setColor(shadowColor);
                    try {
                        if (getShadowBlur() == 0)
                            graphics2D.fill(AffineTransform.getTranslateInstance(getShadowOffsetX(), getShadowOffsetY()).createTransformedShape(shape));
                        else new DropShadow(getShadowOffsetX(), getShadowOffsetY(), getShadowBlur(), 0).fill(shape, graphics2D);
                    }
                    finally {
                        graphics2D.setPaint(originalPaint);
                    }
                }
                graphics2D.fill(shape);
                break;
        }
    }

    @Override
    public void clearColor() {
        if (isDisposed()) throw new AlreadyDisposedException();
        graphics2D.clearRect(0, 0, width, height);
    }
    @Override
    public void clearRectangle(int x, int y, int width, int height) {
        if (isDisposed()) throw new AlreadyDisposedException();
        graphics2D.clearRect(x, y, width, height);
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
        drawShape(new Ellipse2D.Float(x - radius, y - radius, radius * 2, radius * 2));
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
        drawText(text, start, end, AffineTransform.getTranslateInstance(x, y));
    }

    @Override
    public void drawText(CharSequence text, int start, int end, Transform transform) {
        drawText(text, start, end, transform == null ? null : ((DesktopTransform) transform).getAffineTransform());
    }

    public void drawText(AttributedCharSequence sequence, TextLayout textLayout, AffineTransform transform, List<Object[]> backgroundList, List<Object[]> foregroundList) {
        AffineTransform originalTransform = graphics2D.getTransform();
        graphics2D.transform(transform);
        try {
            float baseX = 0;
            int textAlignment = info.getTextAlignment();
            switch (textAlignment) {
                case Text.Alignment.END: baseX -= textLayout.getAdvance(); break;
                case Text.Alignment.MIDDLE: baseX -= textLayout.getAdvance() * 0.5f; break;
            }
            boolean drawBackground = info.getTextBackground() != null || !backgroundList.isEmpty() && info.getStyle() != Style.FILL;
            boolean drawForeground = info.getTextForeground() != null || !foregroundList.isEmpty();
            if (!drawBackground && !drawForeground) {
                drawTextWithShadow(textLayout, baseX);
            }
            else {
                Paint originalPaint = graphics2D.getPaint();
                if (drawBackground) {
                    graphics2D.setColor(AWTSupport.TRANSPARENT);
                    AttributedCharSequence backgroundSequence = sequence.clone();
                    if (info.getTextBackground() != null) backgroundSequence.addAttribute(TextAttribute.BACKGROUND, ((DesktopBrush) info.getTextBackground()).getPaint());
                    try {
                        for (Object[] backgroundEntry : backgroundList) {
                            backgroundSequence.addAttribute(TextAttribute.BACKGROUND, backgroundEntry[0], (Integer) backgroundEntry[1], (Integer) backgroundEntry[2]);
                        }
                        TextLayout backgroundTextLayout = new TextLayout(backgroundSequence.getIterator(), graphics2D.getFontRenderContext());
                        backgroundTextLayout.draw(graphics2D, baseX, 0);
                    }
                    finally {
                        graphics2D.setPaint(originalPaint);
                    }
                }
                drawTextWithShadow(textLayout, baseX);
                if (drawForeground) {
                    graphics2D.setColor(AWTSupport.TRANSPARENT);
                    AttributedCharSequence foregroundSequence = sequence.clone();
                    if (info.getTextForeground() != null) foregroundSequence.addAttribute(TextAttribute.BACKGROUND, ((DesktopBrush) info.getTextForeground()).getPaint());
                    try {
                        for (Object[] foregroundEntry : foregroundList) {
                            foregroundSequence.addAttribute(TextAttribute.BACKGROUND, foregroundEntry[0], (Integer) foregroundEntry[1], (Integer) foregroundEntry[2]);
                        }
                        TextLayout foregroundTextLayout = new TextLayout(foregroundSequence.getIterator(), graphics2D.getFontRenderContext());
                        foregroundTextLayout.draw(graphics2D, baseX, 0);
                    }
                    finally {
                        graphics2D.setPaint(originalPaint);
                    }
                }
            }
        }
        finally {
            graphics2D.setTransform(originalTransform);
        }
    }

    public void drawText(TextLayout textLayout, AffineTransform transform) {
        AffineTransform originalTransform = graphics2D.getTransform();
        graphics2D.transform(transform);
        try {
            float baseX = 0;
            switch (info.getTextAlignment()) {
                case Text.Alignment.END: baseX -= textLayout.getAdvance(); break;
                case Text.Alignment.MIDDLE: baseX -= textLayout.getAdvance() / 2; break;
            }
            if (info.getTextBackground() != null) {
                Paint originalPaint = graphics2D.getPaint();
                try {
                    try {
                        graphics2D.setPaint(((DesktopBrush) info.getTextBackground()).getPaint());
                    }
                    finally {
                        graphics2D.fill(new Rectangle2D.Float(baseX, - textLayout.getAscent(), textLayout.getAdvance(),
                                textLayout.getAscent() + textLayout.getDescent()));
                    }
                }
                finally {
                    graphics2D.setPaint(originalPaint);
                }
            }
            drawTextWithShadow(textLayout, baseX);
            if (info.getTextForeground() != null) {
                Paint originalPaint = graphics2D.getPaint();
                try {
                    try {
                        graphics2D.setPaint(((DesktopBrush) info.getTextForeground()).getPaint());
                    }
                    finally {
                        graphics2D.fill(new Rectangle2D.Float(baseX, - textLayout.getAscent(), textLayout.getAdvance(),
                                textLayout.getAscent() + textLayout.getDescent()));
                    }
                }
                finally {
                    graphics2D.setPaint(originalPaint);
                }
            }
        }
        finally {
            graphics2D.setTransform(originalTransform);
        }
    }

    public void drawText(CharSequence text, int start, int end, AffineTransform transform) {
        if (isDisposed()) throw new AlreadyDisposedException();
        if (transform == null) transform = new AffineTransform();

        if (text instanceof Text && !((Text) text).spanMarks().isEmpty()) {

            List<Object[]> backgroundList = new ArrayList<>();
            List<Object[]> foregroundList = new ArrayList<>();

            AttributedCharSequence sequence = AWTSupport.toAttributedCharSequence((Text) text, start, end, this, backgroundList, foregroundList);

            AttributedCharacterIterator iterator = sequence.getIterator();
            TextLayout textLayout = new TextLayout(iterator, graphics2D.getFontRenderContext());
            drawText(sequence, textLayout, transform, backgroundList, foregroundList);
        }
        else {

            TextLayout textLayout = new TextLayout(
                    new AttributedCharSequence(text.subSequence(start, end), graphics2D.getFont().getAttributes()).getIterator(),
                    graphics2D.getFontRenderContext());
            drawText(textLayout, transform);

        }
    }

    private void drawTextWithShadow(TextLayout textLayout, float baseX) {
        if (info.getShadowColor() == unrefined.media.graphics.Color.TRANSPARENT) {
            if (info.getStyle() == Style.STROKE) graphics2D.draw(textLayout.getOutline(AffineTransform.getTranslateInstance(baseX, 0)));
            else textLayout.draw(graphics2D, baseX, 0);
        }
        else {
            Shape outline = textLayout.getOutline(AffineTransform.getTranslateInstance(baseX, 0));
            if (getStyle() == Style.STROKE) {
                Paint originalPaint = graphics2D.getPaint();
                graphics2D.setColor(shadowColor);
                try {
                    if (getShadowBlur() == 0) graphics2D.draw(AffineTransform.getTranslateInstance(getShadowOffsetX(), getShadowOffsetY()).createTransformedShape(outline));
                    else new DropShadow(getShadowOffsetX(), getShadowOffsetY(), getShadowBlur(), 0)
                            .draw(outline, graphics2D);
                }
                finally {
                    graphics2D.setPaint(originalPaint);
                }
                graphics2D.draw(outline);
            }
            else {
                Paint originalPaint = graphics2D.getPaint();
                graphics2D.setColor(shadowColor);
                try {
                    if (getShadowBlur() == 0) graphics2D.fill(AffineTransform.getTranslateInstance(getShadowOffsetX(), getShadowOffsetY()).createTransformedShape(outline));
                    else new DropShadow(getShadowOffsetX(), getShadowOffsetY(), getShadowBlur(), 0)
                            .fill(outline, graphics2D);
                }
                finally {
                    graphics2D.setPaint(originalPaint);
                }
                graphics2D.fill(outline);
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
    public void drawTextOnPath(String text, int start, int end, Path path, float startOffset, float x, float y) {
        drawTextOnPath(CharSequences.toCharArray(text, start, end), path, startOffset, x, y);
    }

    @Override
    public void drawTextOnPath(char[] text, int offset, int length, Path path, float startOffset, float x, float y) {
        drawTextOnPath(text, offset, length, path, startOffset, AffineTransform.getTranslateInstance(x, y));
    }

    @Override
    public void drawTextOnPath(String text, int start, int end, Path path, float startOffset, Transform transform) {
        drawTextOnPath(CharSequences.toCharArray(text, start, end), path, startOffset, transform);
    }

    @Override
    public void drawTextOnPath(char[] text, int offset, int length, Path path, float startOffset, Transform transform) {
        drawTextOnPath(text, offset, length, path, startOffset, transform == null ? null : ((DesktopTransform) transform).getAffineTransform());
    }

    public void drawTextOnPath(char[] text, int offset, int length, Path path, float startOffset, AffineTransform transform) {
        if (isDisposed()) throw new AlreadyDisposedException();
        int direction = info.getTextDirection();
        final GlyphVector glyphVector;
        if (direction == Text.Direction.AUTO) glyphVector = graphics2D.getFont()
                .createGlyphVector(graphics2D.getFontRenderContext(), new CharArrayCharacterIterator(text, offset, length));
        else glyphVector = graphics2D.getFont()
                .layoutGlyphVector(graphics2D.getFontRenderContext(), text, offset, offset + length,
                        FontSupport.toFontLayout(info.getTextDirection()));
        Shape textOnPath = TextPathLayout.layoutGlyphVector(glyphVector, ((DesktopPath) path).getPath2D(),
                info.getTextAlignment(), startOffset, (float) glyphVector.getVisualBounds().getWidth(), TextPathLayout.ADJUST_SPACING);
        if (transform == null) drawShape(textOnPath);
        else drawShape(transform.createTransformedShape(textOnPath));
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
        else if (text instanceof Text) {
            bounds2D = fontMetrics.getStringBounds(AWTSupport.toAttributedCharSequence((Text) text, this).getIterator(), start, end, graphics2D);
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
        else if (text instanceof Text) {
            AttributedCharacterIterator iterator = AWTSupport.toAttributedCharSequence((Text) text, this).getIterator();
            lineMetrics = fontMetrics.getLineMetrics(iterator, start, end, graphics2D);
            bounds2D = fontMetrics.getStringBounds(iterator, start, end, graphics2D);
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
        else if (text instanceof Text) {
            lineMetrics = fontMetrics.getLineMetrics(AWTSupport.toAttributedCharSequence((Text) text, this).getIterator(), start, end, graphics2D);
        }
        else {
            AttributedCharacterIterator iterator = new AttributedCharSequence(text).getIterator();
            lineMetrics = fontMetrics.getLineMetrics(iterator, start, end, graphics2D);
        }
        if (metrics != null) metrics.setMetrics(lineMetrics.getBaselineOffsets()[lineMetrics.getBaselineIndex()],
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
        else if (text instanceof Text) {
            bounds2D = fontMetrics.getStringBounds(AWTSupport.toAttributedCharSequence((Text) text, this).getIterator(), start, end, graphics2D);
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
        if (metrics != null) metrics.setMetrics(lineMetrics.getBaselineOffsets()[lineMetrics.getBaselineIndex()],
                lineMetrics.getAscent(), lineMetrics.getDescent(), lineMetrics.getLeading(),
                fontMetrics.getMaxAscent(), fontMetrics.getMaxDescent(), fontMetrics.getMaxAdvance());
    }

    @Override
    public void measureText(char[] text, int offset, int length, RectangleF bounds) {
        if (isDisposed()) throw new AlreadyDisposedException();
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        Rectangle2D bounds2D = fontMetrics.getStringBounds(text, offset, length, graphics2D);
        if (bounds != null) AWTSupport.floatRectangle(bounds2D, bounds);
    }

    @Override
    public void hitText(CharSequence text, int start, int end, float xOffset, float yOffset, Text.HitInfo hitInfo) {
        if (isDisposed()) throw new AlreadyDisposedException();
        TextLayout textLayout = new TextLayout(
                new AttributedCharSequence(text.subSequence(start, end), graphics2D.getFont().getAttributes()).getIterator(),
                graphics2D.getFontRenderContext());
        TextHitInfo textHitInfo = textLayout.hitTestChar(xOffset, yOffset);
        int insertionIndex = textHitInfo.getInsertionIndex();
        Rectangle2D bounds = textLayout.getBounds();
        boolean outBounds = xOffset < bounds.getX() || xOffset >= bounds.getWidth();
        int charIndex = outBounds ? -1 : textHitInfo.getCharIndex();
        boolean leadingEdge = textHitInfo.isLeadingEdge() || outBounds;
        hitInfo.setHitInfo(charIndex, insertionIndex, leadingEdge);
    }

    @Override
    public void hitText(char[] text, int offset, int length, float xOffset, float yOffset, Text.HitInfo hitInfo) {
        hitText(new PhantomString(text, offset, length), xOffset, yOffset, hitInfo);
    }

    @Override
    public Paragraph multilineText(CharSequence text, int start, int end) {
        return new DesktopParagraph(this, text, start, end);
    }

    @Override
    public Paragraph multilineText(char[] text, int offset, int length) {
        return new DesktopParagraph(this, text, offset, length);
    }

    @Override
    public void getInfo(Info info) {
        if (isDisposed()) throw new AlreadyDisposedException();
        this.info.to(info);
    }

    public Info getInfo() {
        return info;
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
    public int getShadowColor() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getShadowColor();
    }

    private volatile Color shadowColor = AWTSupport.TRANSPARENT;
    @Override
    public void setShadowColor(int color) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setShadowColor(color);
        shadowColor = new Color(color, false);
    }

    @Override
    public float getShadowBlur() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getShadowBlur();
    }

    @Override
    public void setShadowBlur(float blur) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setShadowBlur(blur);
    }

    @Override
    public void setShadowOffsetX(float offsetX) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setShadowOffsetX(offsetX);
    }

    @Override
    public void setShadowOffsetY(float offsetY) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setShadowOffsetY(offsetY);
    }

    @Override
    public float getShadowOffsetX() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getShadowOffsetX();
    }

    @Override
    public float getShadowOffsetY() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getShadowOffsetY();
    }

    @Override
    public Graphics slice(int x, int y, int width, int height) {
        return new DesktopGraphics(this, x, y, width, height);
    }

    @Override
    public Graphics duplicate() {
        return new DesktopGraphics(this);
    }

    @Override
    public Graphics attachment() {
        return attachment;
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
    public Brush getTextBackground() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getTextBackground();
    }

    @Override
    public void setTextBackground(Brush textBackground) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setTextBackground(textBackground);
    }

    @Override
    public Brush getTextForeground() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getTextForeground();
    }

    @Override
    public void setTextForeground(Brush textForeground) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setTextForeground(textForeground);
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
        if (info.getSuperscriptText() != Text.Superscript.NONE) size *= 1.5f;
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
        graphics2D.setFont(graphics2D.getFont().deriveFont(Collections.singletonMap(TextAttribute.RUN_DIRECTION, TextHints.toRunDirection(direction))));
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
    public boolean isKerningText() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.isKerningText();
    }

    @Override
    public void setKerningText(boolean kerningText) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setKerningText(kerningText);
        graphics2D.setFont(graphics2D.getFont().deriveFont(Collections.singletonMap(TextAttribute.KERNING, TextAttribute.KERNING_ON)));
    }

    @Override
    public boolean isVariantLigaturesText() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.isVariantLigaturesText();
    }

    @Override
    public void setVariantLigaturesText(boolean variantLigaturesText) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setVariantLigaturesText(variantLigaturesText);
        graphics2D.setFont(graphics2D.getFont().deriveFont(Collections.singletonMap(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON)));
    }

    @Override
    public int getSuperscriptText() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getSuperscriptText();
    }

    @Override
    public void setSuperscriptText(int superscriptText) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setSuperscriptText(superscriptText);
        float textSize = info.getTextSize();
        if (superscriptText != Text.Superscript.NONE) textSize *= 1.5f;
        Map<TextAttribute, Object> attributes = new HashMap<>(1);
        attributes.put(TextAttribute.SIZE, textSize);
        attributes.put(TextAttribute.SUPERSCRIPT, superscriptText);
        graphics2D.setFont(graphics2D.getFont().deriveFont(attributes));
    }

    @Override
    public float getLetterSpacing() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return info.getLetterSpacing();
    }

    @Override
    public void setLetterSpacing(float letterSpacing) {
        if (isDisposed()) throw new AlreadyDisposedException();
        info.setLetterSpacing(letterSpacing);
        graphics2D.setFont(graphics2D.getFont().deriveFont(Collections.singletonMap(TextAttribute.TRACKING, letterSpacing)));
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
        transform.scale(scaleX, scaleY);
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
        transform.shear(skewX, skewY);
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
            synchronized (subgraphics) {
                for (DesktopGraphics graphics : subgraphics) {
                    graphics.dispose();
                }
            }
            subgraphics.clear();
            if (attachment != null) attachment.subgraphics.remove(this);
            graphics2D.dispose();
            graphics2D = null;
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
