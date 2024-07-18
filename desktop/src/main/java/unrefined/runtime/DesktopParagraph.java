package unrefined.runtime;

import unrefined.desktop.AWTSupport;
import unrefined.desktop.AttributedCharSequence;
import unrefined.media.graphics.Paragraph;
import unrefined.media.graphics.RectangleF;
import unrefined.media.graphics.Text;
import unrefined.media.graphics.Transform;
import unrefined.util.PhantomString;

import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class DesktopParagraph extends Paragraph {

    private final LineBreakMeasurer measurer;
    private final AttributedCharSequence sequence;
    private TextLayout textLayout = null;
    private int offset = 0;
    private boolean first = true;
    private final DesktopGraphics graphics;
    private final List<Object[]> backgrounds = new ArrayList<>();
    private final List<Object[]> foregrounds = new ArrayList<>();

    public DesktopParagraph(DesktopGraphics graphics, char[] array, int offset, int length) {
        this.sequence = new AttributedCharSequence(new PhantomString(array, offset, length));
        this.measurer = new LineBreakMeasurer(sequence.getIterator(), graphics.getGraphics2D().getFontRenderContext());
        this.graphics = graphics;
    }

    public DesktopParagraph(DesktopGraphics graphics, char[] array) {
        this(graphics, array, 0, array.length);
    }

    public DesktopParagraph(DesktopGraphics graphics, CharSequence sequence, int start, int end) {
        if (sequence instanceof Text) this.sequence = AWTSupport.toAttributedCharSequence((Text) sequence, start, end, graphics, backgrounds, foregrounds);
        else this.sequence = new AttributedCharSequence(sequence.subSequence(start, end));
        this.measurer = new LineBreakMeasurer(this.sequence.getIterator(), graphics.getGraphics2D().getFontRenderContext());
        this.graphics = graphics;
    }

    public DesktopParagraph(DesktopGraphics graphics, CharSequence sequence) {
        this(graphics, sequence, 0, sequence.length());
    }

    public DesktopGraphics getGraphics() {
        return graphics;
    }

    public LineBreakMeasurer getMeasurer() {
        return measurer;
    }

    public TextLayout getTextLayout() {
        return textLayout;
    }

    @Override
    public boolean hasNext() {
        return first || textLayout != null;
    }

    @Override
    public void nextLine(float wrappingWidth) {
        first = false;
        offset = measurer.nextOffset(wrappingWidth);
        textLayout = measurer.nextLayout(wrappingWidth);
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public float measureText() {
        return textLayout.getAdvance();
    }

    @Override
    public void measureText(Text.Metrics metrics, RectangleF bounds) {
        if (metrics != null || bounds != null) {
            if (textLayout == null) {
                if (metrics != null) metrics.reset();
                if (bounds != null) bounds.reset();
            }
            else {
                if (metrics != null) metrics.setMetrics(textLayout.getBaseline(), textLayout.getAscent(), textLayout.getDescent(),
                        textLayout.getLeading(), textLayout.getAscent(), textLayout.getDescent(), textLayout.getAdvance());
                if (bounds != null) AWTSupport.floatRectangle(textLayout.getBounds(), bounds);
            }
        }
    }

    @Override
    public void hitText(float xOffset, float yOffset, Text.HitInfo hitInfo) {
        if (hitInfo != null) {
            if (textLayout == null) hitInfo.reset();
            else {
                TextHitInfo textHitInfo = textLayout.hitTestChar(xOffset, yOffset);
                int insertionIndex = textHitInfo.getInsertionIndex();
                Rectangle2D bounds = textLayout.getBounds();
                boolean outBounds = xOffset < bounds.getX() || xOffset >= bounds.getWidth();
                int charIndex = outBounds ? -1 : textHitInfo.getCharIndex();
                boolean leadingEdge = textHitInfo.isLeadingEdge() || outBounds;
                hitInfo.setHitInfo(charIndex, insertionIndex, leadingEdge);
            }
        }
    }

    @Override
    public void drawText(float x, float y) {
        if (textLayout != null) {
            if (sequence.getText() instanceof Text && !((Text) sequence.getText()).spanMarks().isEmpty())
                graphics.drawText(sequence, textLayout, AffineTransform.getTranslateInstance(x, y), backgrounds, foregrounds);
            else graphics.drawText(textLayout, AffineTransform.getTranslateInstance(x, y));
        }
    }

    @Override
    public void drawText(Transform transform) {
        if (textLayout != null) {
            if (sequence.getText() instanceof Text && !((Text) sequence.getText()).spanMarks().isEmpty())
                graphics.drawText(sequence, textLayout, ((DesktopTransform) transform).getAffineTransform(), backgrounds, foregrounds);
            else graphics.drawText(textLayout, ((DesktopTransform) transform).getAffineTransform());
        }
    }

}
