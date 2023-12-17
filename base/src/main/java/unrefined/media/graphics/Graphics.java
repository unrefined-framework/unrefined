package unrefined.media.graphics;

import unrefined.io.Disposable;
import unrefined.math.FastMath;
import unrefined.util.Copyable;
import unrefined.util.NotInstantiableError;
import unrefined.util.Resettable;
import unrefined.util.Swappable;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;

public abstract class Graphics implements Disposable, Resettable {

    public static final class Style {
        private Style() {
            throw new NotInstantiableError(Style.class);
        }
        public static final int STROKE = 0;
        public static final int FILL = 1;
        public static boolean isValid(int style) {
            return style >= STROKE && style <= FILL;
        }
        public static int checkValid(int style) {
            if (style < STROKE || style > FILL) throw new IllegalArgumentException("Illegal graphics style: " + style);
            else return style;
        }
        public static String toString(int style) {
            switch (style) {
                case STROKE: return "STROKE";
                case FILL: return "FILL";
                default: throw new IllegalArgumentException("Illegal graphics style: " + style);
            }
        }
    }

    public static class Info implements Copyable, Swappable, Resettable {

        private transient Path clip;
        private transient Transform transform;
        private transient Composite composite;

        private transient Brush brush;
        private int style;

        private float strokeWidth;
        private int strokeJoin;
        private int strokeCap;
        private float strokeMiterLimit;
        private float[] strokeDash;
        private float strokeDashPhase;

        private boolean antiAlias;
        private boolean imageSmoothing;
        private boolean dither;

        private transient Font font;
        private float textSize;
        private int textDirection;
        private int textAlignment;
        private boolean subpixelText;
        private boolean underlineText;
        private boolean strikeThroughText;
        private float textScaleX, textScaleY;
        private float textSkewX, textSkewY;

        public Info() {
            reset();
        }

        public Info(Path clip, Transform transform, Composite composite,
                    Brush brush, int style,
                    float strokeWidth, int strokeJoin, int strokeCap, float strokeMiterLimit, float[] strokeDash, float strokeDashPhase,
                    boolean antiAlias, boolean imageSmoothing, boolean dither,
                    Font font, float textSize, int textDirection, int textAlignment, boolean subpixelText, boolean underlineText, boolean strikeThroughText,
                    float textScaleX, float textScaleY, float textSkewX, float textSkewY) {
            setClip(clip);
            setTransform(transform);
            setComposite(composite);
            setBrush(brush);
            setStyle(style);
            setStroke(strokeWidth, strokeJoin, strokeCap, strokeMiterLimit, strokeDash, strokeDashPhase);
            setAntiAlias(antiAlias, imageSmoothing, dither);
            setTextAttributes(font, textSize, textDirection, textAlignment, subpixelText, underlineText, strikeThroughText);
            setTextTransform(textScaleX, textScaleY, textSkewX, textSkewY);
        }

        public Info(Path clip, Transform transform, Composite composite,
                    Brush brush, int style,
                    float strokeWidth, int strokeJoin, int strokeCap, float strokeMiterLimit, float[] strokeDash, int strokeDashOffset, int strokeDashLength, float strokeDashPhase,
                    boolean antiAlias, boolean imageSmoothing, boolean dither,
                    Font font, float textSize, int textDirection, int textAlignment, boolean subpixelText, boolean underlineText, boolean strikeThroughText,
                    float textScaleX, float textScaleY, float textSkewX, float textSkewY) {
            setClip(clip);
            setTransform(transform);
            setComposite(composite);
            setBrush(brush);
            setStyle(style);
            setStroke(strokeWidth, strokeJoin, strokeCap, strokeMiterLimit, strokeDash, strokeDashOffset, strokeDashLength, strokeDashPhase);
            setAntiAlias(antiAlias, imageSmoothing, dither);
            setTextAttributes(font, textSize, textDirection, textAlignment, subpixelText, underlineText, strikeThroughText);
            setTextTransform(textScaleX, textScaleY, textSkewX, textSkewY);
        }

        public Info(Info info) {
            this(info.getClip(), info.getTransform(), info.getComposite(),
                    info.getBrush(), info.getStyle(),
                    info.getStrokeWidth(), info.getStrokeJoin(), info.getStrokeCap(), info.getStrokeMiterLimit(), info.getStrokeDash(), info.getStrokeDashPhase(),
                    info.isAntiAlias(), info.isImageSmoothing(), info.isDither(),
                    info.getFont(), info.getTextSize(), info.getTextDirection(), info.getTextAlignment(), info.isSubpixelText(), info.isUnderlineText(), info.isStrikeThroughText(),
                    info.getTextScaleX(), info.getTextScaleY(), info.getTextSkewX(), info.getTextSkewY());
        }

        public Path getClip() {
            return clip;
        }

        public void setClip(Path clip) {
            this.clip = clip;
        }

        public Transform getTransform() {
            return transform;
        }

        public void setTransform(Transform transform) {
            this.transform = transform;
        }

        public Composite getComposite() {
            return composite;
        }

        public void setComposite(Composite composite) {
            this.composite = composite;
        }

        public Brush getBrush() {
            return brush;
        }

        public void setBrush(Brush brush) {
            this.brush = brush;
        }

        public int getStyle() {
            return style;
        }

        public void setStyle(int type) {
            this.style = type;
        }

        public float getStrokeWidth() {
            return strokeWidth;
        }

        public void setStrokeWidth(float width) {
            this.strokeWidth = width;
        }

        public int getStrokeJoin() {
            return strokeJoin;
        }

        public void setStrokeJoin(int join) {
            this.strokeJoin = join;
        }

        public int getStrokeCap() {
            return strokeCap;
        }

        public void setStrokeCap(int cap) {
            this.strokeCap = cap;
        }

        public float getStrokeMiterLimit() {
            return strokeMiterLimit;
        }

        public void setStrokeMiterLimit(float miterLimit) {
            this.strokeMiterLimit = miterLimit;
        }

        public float[] getStrokeDash() {
            return strokeDash;
        }

        public void setStrokeDash(float... dash) {
            this.strokeDash = dash == null ? null : dash.clone();
        }

        public void setStrokeDash(float[] dash, int offset, int length) {
            this.strokeDash = dash == null ? null : Arrays.copyOfRange(dash, offset, offset + length);
        }

        public float getStrokeDashPhase() {
            return strokeDashPhase;
        }

        public void setStrokeDashPhase(float dashPhase) {
            this.strokeDashPhase = dashPhase;
        }

        public boolean isAntiAlias() {
            return antiAlias;
        }

        public void setAntiAlias(boolean antiAlias) {
            this.antiAlias = antiAlias;
        }

        public boolean isImageSmoothing() {
            return imageSmoothing;
        }

        public void setImageSmoothing(boolean imageSmoothing) {
            this.imageSmoothing = imageSmoothing;
        }

        public boolean isDither() {
            return dither;
        }

        public void setDither(boolean dither) {
            this.dither = dither;
        }

        public Font getFont() {
            return font;
        }

        public void setFont(Font font) {
            this.font = font;
        }

        public float getTextSize() {
            return textSize;
        }

        public void setTextSize(float size) {
            this.textSize = size;
        }

        public int getTextDirection() {
            return textDirection;
        }

        public void setTextDirection(int direction) {
            this.textDirection = direction;
        }

        public int getTextAlignment() {
            return textAlignment;
        }

        public void setTextAlignment(int alignment) {
            this.textAlignment = alignment;
        }

        public boolean isSubpixelText() {
            return subpixelText;
        }

        public void setSubpixelText(boolean subpixelText) {
            this.subpixelText = subpixelText;
        }

        public boolean isUnderlineText() {
            return underlineText;
        }

        public void setUnderlineText(boolean underlineText) {
            this.underlineText = underlineText;
        }

        public boolean isStrikeThroughText() {
            return strikeThroughText;
        }

        public void setStrikeThroughText(boolean strikeThroughText) {
            this.strikeThroughText = strikeThroughText;
        }

        public void setTextScaleX(float scaleX) {
            this.textScaleX = scaleX;
        }

        public void setTextScaleY(float scaleY) {
            this.textScaleY = scaleY;
        }

        public void setTextScale(float scaleX, float scaleY) {
            setTextScaleX(scaleX);
            setTextScaleY(scaleY);
        }

        public float getTextScaleX() {
            return textScaleX;
        }

        public float getTextScaleY() {
            return textScaleY;
        }

        public void getTextScale(float[] scale, int offset) {
            scale[offset] = getTextScaleX();
            scale[offset + 1] = getTextScaleY();
        }

        public void getTextScale(float[] scale) {
            getTextScale(scale, 0);
        }

        public void setTextSkewX(float skewX) {
            this.textSkewX = skewX;
        }

        public void setTextSkewY(float skewY) {
            this.textSkewY = skewY;
        }

        public void setTextSkew(float skewX, float skewY) {
            setTextSkewX(skewX);
            setTextSkewY(skewY);
        }

        public float getTextSkewX() {
            return textSkewX;
        }

        public float getTextSkewY() {
            return textSkewY;
        }

        public void getTextSkew(float[] skew, int offset) {
            skew[offset] = getTextSkewX();
            skew[offset + 1] = getTextSkewY();
        }

        public void getTextSkew(float[] skew) {
            getTextSkew(skew, 0);
        }

        public void setStroke(float width, int join, int cap, float miterLimit, float[] dash, float dashPhase) {
            setStrokeWidth(width);
            setStrokeJoin(join);
            setStrokeCap(cap);
            setStrokeMiterLimit(miterLimit);
            setStrokeDash(dash);
            setStrokeDashPhase(dashPhase);
        }

        public void setStroke(float width, int join, int cap, float miterLimit, float[] dash, int dashOffset, int dashLength, float dashPhase) {
            setStrokeWidth(width);
            setStrokeJoin(join);
            setStrokeCap(cap);
            setStrokeMiterLimit(miterLimit);
            setStrokeDash(dash, dashOffset, dashLength);
            setStrokeDashPhase(dashPhase);
        }

        public void setAntiAlias(boolean antiAlias, boolean imageSmoothing, boolean dither) {
            setAntiAlias(antiAlias);
            setImageSmoothing(imageSmoothing);
            setDither(dither);
        }

        public void setTextAttributes(Font font, float size, int direction, int alignment, boolean subpixel, boolean underline, boolean strikeThrough) {
            setFont(font);
            setTextSize(size);
            setTextDirection(direction);
            setTextAlignment(alignment);
            setSubpixelText(subpixel);
            setUnderlineText(underline);
            setStrikeThroughText(strikeThrough);
        }

        public void setTextTransform(float scaleX, float scaleY, float skewX, float skewY) {
            setTextScale(scaleX, scaleY);
            setTextSkew(skewX, skewY);
        }

        public void setInfo(Path clip, Transform transform, Composite composite,
                    Brush brush, int style,
                    float strokeWidth, int strokeJoin, int strokeCap, float strokeMiterLimit, float[] strokeDash, float strokeDashPhase,
                    boolean antiAlias, boolean imageSmoothing, boolean dither,
                    Font font, float textSize, int textDirection, int textAlignment, boolean subpixelText, boolean underlineText, boolean strikeThroughText,
                    float textScaleX, float textScaleY, float textSkewX, float textSkewY) {
            setClip(clip);
            setTransform(transform);
            setComposite(composite);
            setBrush(brush);
            setStyle(style);
            setStroke(strokeWidth, strokeJoin, strokeCap, strokeMiterLimit, strokeDash, strokeDashPhase);
            setAntiAlias(antiAlias, imageSmoothing, dither);
            setTextAttributes(font, textSize, textDirection, textAlignment, subpixelText, underlineText, strikeThroughText);
            setTextTransform(textScaleX, textScaleY, textSkewX, textSkewY);
        }

        public void setInfo(Path clip, Transform transform, Composite composite,
                    Brush brush, int style,
                    float strokeWidth, int strokeJoin, int strokeCap, float strokeMiterLimit, float[] strokeDash, int strokeDashOffset, int strokeDashLength, float strokeDashPhase,
                    boolean antiAlias, boolean imageSmoothing, boolean dither,
                    Font font, float textSize, int textDirection, int textAlignment, boolean subpixelText, boolean underlineText, boolean strikeThroughText,
                    float textScaleX, float textScaleY, float textSkewX, float textSkewY) {
            setClip(clip);
            setTransform(transform);
            setComposite(composite);
            setBrush(brush);
            setStyle(style);
            setStroke(strokeWidth, strokeJoin, strokeCap, strokeMiterLimit, strokeDash, strokeDashOffset, strokeDashLength, strokeDashPhase);
            setAntiAlias(antiAlias, imageSmoothing, dither);
            setTextAttributes(font, textSize, textDirection, textAlignment, subpixelText, underlineText, strikeThroughText);
            setTextTransform(textScaleX, textScaleY, textSkewX, textSkewY);
        }

        public void setInfo(Info info) {
            setInfo(info.getClip(), info.getTransform(), info.getComposite(),
                    info.getBrush(), info.getStyle(),
                    info.getStrokeWidth(), info.getStrokeJoin(), info.getStrokeCap(), info.getStrokeMiterLimit(), info.getStrokeDash(), info.getStrokeDashPhase(),
                    info.isAntiAlias(), info.isImageSmoothing(), info.isDither(),
                    info.getFont(), info.getTextSize(), info.getTextDirection(), info.getTextAlignment(), info.isSubpixelText(), info.isUnderlineText(), info.isStrikeThroughText(),
                    info.getTextScaleX(), info.getTextScaleY(), info.getTextSkewX(), info.getTextSkewY());
        }

        @Override
        public void to(Object dst) {
            ((Info) dst).setInfo(this);
        }

        @Override
        public void from(Object src) {
            setInfo((Info) src);
        }

        @Override
        public void swap(Object o) {
            Info that = (Info) o;
            Path clip = that.clip;
            Transform transform = that.transform;
            Composite composite = that.composite;
            Brush brush = that.brush;
            int style = that.style;
            float strokeWidth = that.strokeWidth;
            int strokeJoin = that.strokeJoin;
            int strokeCap = that.strokeCap;
            float strokeMiterLimit = that.strokeMiterLimit;
            float[] strokeDash = that.strokeDash;
            float strokeDashPhase = that.strokeDashPhase;
            boolean antiAlias = that.antiAlias;
            boolean imageSmoothing = that.imageSmoothing;
            boolean dither = that.dither;
            Font font = that.font;
            float textSize = that.textSize;
            int textDirection = that.textDirection;
            int textAlignment = that.textAlignment;
            boolean subpixelText = that.subpixelText;
            boolean underlineText = that.underlineText;
            boolean strikeThroughText = that.strikeThroughText;
            float textScaleX = that.textScaleX;
            float textScaleY = that.textScaleY;
            float textSkewX = that.textSkewX;
            float textSkewY = that.textSkewY;
            that.setInfo(this);
            setInfo(clip, transform, composite,
                    brush, style,
                    strokeWidth, strokeJoin, strokeCap, strokeMiterLimit, strokeDash, strokeDashPhase,
                    antiAlias, imageSmoothing, dither,
                    font, textSize, textDirection, textAlignment, subpixelText, underlineText, strikeThroughText,
                    textScaleX, textScaleY, textSkewX, textSkewY);
        }

        @Override
        public Info clone() {
            try {
                Info clone = (Info) super.clone();
                clone.strokeDash = strokeDash == null ? null : strokeDash.clone();
                return clone;
            }
            catch (CloneNotSupportedException e) {
                return copy();
            }
        }

        @Override
        public Info copy() {
            return new Info(this);
        }

        @Override
        public void reset() {
            setInfo(null, null, null,
                    null, Style.FILL,
                    16, Stroke.Join.MITER, Stroke.Cap.BUTT, 10, null, 0,
                    true, false, false,
                    null, 12, Text.Direction.AUTO, Text.Alignment.START, true, false, false,
                    1, 1, 0, 0);
        }

        @Override
        public boolean isIdentity() {
            return getClip() == null && getTransform() == null && getComposite() == null &&
                    getBrush() == null && getStyle() == Style.FILL &&
                    getStrokeWidth() == 16 && getStrokeJoin() == Stroke.Join.MITER && getStrokeCap() == Stroke.Cap.BUTT && getStrokeMiterLimit() == 10 && getStrokeDash() == null && getStrokeDashPhase() == 0 &&
                    isAntiAlias() && !isImageSmoothing() && !isDither() &&
                    getFont() == null && getTextSize() == 12 && getTextDirection() == Text.Direction.AUTO && getTextAlignment() == Text.Alignment.START && isSubpixelText() && !isUnderlineText() && !isStrikeThroughText() &&
                    getTextScaleX() == 1 && getTextScaleY() == 1 && getTextSkewX() == 0 && getTextSkewY() == 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Info info = (Info) o;

            if (style != info.style) return false;
            if (Float.compare(strokeWidth, info.strokeWidth) != 0) return false;
            if (strokeJoin != info.strokeJoin) return false;
            if (strokeCap != info.strokeCap) return false;
            if (Float.compare(strokeMiterLimit, info.strokeMiterLimit) != 0) return false;
            if (Float.compare(strokeDashPhase, info.strokeDashPhase) != 0) return false;
            if (antiAlias != info.antiAlias) return false;
            if (imageSmoothing != info.imageSmoothing) return false;
            if (dither != info.dither) return false;
            if (Float.compare(textSize, info.textSize) != 0) return false;
            if (textDirection != info.textDirection) return false;
            if (textAlignment != info.textAlignment) return false;
            if (subpixelText != info.subpixelText) return false;
            if (underlineText != info.underlineText) return false;
            if (strikeThroughText != info.strikeThroughText) return false;
            if (Float.compare(textScaleX, info.textScaleX) != 0) return false;
            if (Float.compare(textScaleY, info.textScaleY) != 0) return false;
            if (Float.compare(textSkewX, info.textSkewX) != 0) return false;
            if (Float.compare(textSkewY, info.textSkewY) != 0) return false;
            if (!Objects.equals(clip, info.clip)) return false;
            if (!Objects.equals(transform, info.transform)) return false;
            if (!Objects.equals(composite, info.composite)) return false;
            if (!Objects.equals(brush, info.brush)) return false;
            if (!Arrays.equals(strokeDash, info.strokeDash)) return false;
            return Objects.equals(font, info.font);
        }

        @Override
        public int hashCode() {
            int result = clip != null ? clip.hashCode() : 0;
            result = 31 * result + (transform != null ? transform.hashCode() : 0);
            result = 31 * result + (composite != null ? composite.hashCode() : 0);
            result = 31 * result + (brush != null ? brush.hashCode() : 0);
            result = 31 * result + style;
            result = 31 * result + (strokeWidth != 0.0f ? Float.floatToIntBits(strokeWidth) : 0);
            result = 31 * result + strokeJoin;
            result = 31 * result + strokeCap;
            result = 31 * result + (strokeMiterLimit != 0.0f ? Float.floatToIntBits(strokeMiterLimit) : 0);
            result = 31 * result + Arrays.hashCode(strokeDash);
            result = 31 * result + (strokeDashPhase != 0.0f ? Float.floatToIntBits(strokeDashPhase) : 0);
            result = 31 * result + (antiAlias ? 1 : 0);
            result = 31 * result + (imageSmoothing ? 1 : 0);
            result = 31 * result + (dither ? 1 : 0);
            result = 31 * result + (font != null ? font.hashCode() : 0);
            result = 31 * result + (textSize != 0.0f ? Float.floatToIntBits(textSize) : 0);
            result = 31 * result + textDirection;
            result = 31 * result + textAlignment;
            result = 31 * result + (subpixelText ? 1 : 0);
            result = 31 * result + (underlineText ? 1 : 0);
            result = 31 * result + (strikeThroughText ? 1 : 0);
            result = 31 * result + (textScaleX != 0.0f ? Float.floatToIntBits(textScaleX) : 0);
            result = 31 * result + (textScaleY != 0.0f ? Float.floatToIntBits(textScaleY) : 0);
            result = 31 * result + (textSkewX != 0.0f ? Float.floatToIntBits(textSkewX) : 0);
            result = 31 * result + (textSkewY != 0.0f ? Float.floatToIntBits(textSkewY) : 0);
            return result;
        }

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "clip=" + clip +
                    ", transform=" + transform +
                    ", composite=" + composite +
                    ", brush=" + brush +
                    ", style=" + Style.toString(style) +
                    ", strokeWidth=" + strokeWidth +
                    ", strokeJoin=" + Stroke.Join.toString(strokeJoin) +
                    ", strokeCap=" + Stroke.Cap.toString(strokeCap) +
                    ", strokeMiterLimit=" + strokeMiterLimit +
                    ", strokeDash=" + Arrays.toString(strokeDash) +
                    ", strokeDashPhase=" + strokeDashPhase +
                    ", antiAlias=" + antiAlias +
                    ", imageSmoothing=" + imageSmoothing +
                    ", dither=" + dither +
                    ", font=" + font +
                    ", textSize=" + textSize +
                    ", textDirection=" + Text.Direction.toString(textDirection) +
                    ", textAlignment=" + Text.Alignment.toString(textAlignment) +
                    ", subpixelText=" + subpixelText +
                    ", underlineText=" + underlineText +
                    ", strikeThroughText=" + strikeThroughText +
                    ", textScaleX=" + textScaleX +
                    ", textScaleY=" + textScaleY +
                    ", textSkewX=" + textSkewX +
                    ", textSkewY=" + textSkewY +
                    '}';
        }

    }

    public abstract int getWidth();
    public abstract int getHeight();
    public void getSize(Dimension size) {
        size.setDimension(getWidth(), getHeight());
    }

    public abstract void clearColor();
    public abstract void clearRectangle(int x, int y, int width, int height);
    public abstract void drawColor();
    public abstract void drawPoint(float x, float y);
    public abstract void drawLine(float x1, float y1, float x2, float y2);
    public abstract void drawQuadLine(float x1, float y1, float cx1, float cy1, float x2, float y2);
    public abstract void drawCubicLine(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2);
    public abstract void drawArc(float x, float y, float width, float height, float startAngle, float sweepAngle);
    public abstract void drawCircle(float x, float y, float radius);
    public abstract void drawEllipse(float x, float y, float width, float height);
    public abstract void drawRectangle(float x, float y, float width, float height);
    public abstract void drawRoundRectangle(float x, float y, float width, float height, float rx, float ry);
    public void drawRoundRectangle(float x, float y, float width, float height, float r) {
        drawRoundRectangle(x, y, width, height, r, r);
    }

    public abstract void drawBitmap(Bitmap bitmap, Transform transform);
    public void drawBitmap(Bitmap bitmap) {
        drawBitmap(bitmap, null);
    }
    public abstract void drawBitmap(Bitmap bitmap, float x, float y);
    public abstract void drawBitmapFrame(Bitmap.Frame frame, Transform transform);
    public void drawBitmapFrame(Bitmap.Frame frame) {
        drawBitmapFrame(frame, null);
    }
    public abstract void drawBitmapFrame(Bitmap.Frame frame, float x, float y);

    public abstract void drawPath(Path path, Transform transform);
    public void drawPath(Path path) {
        drawPath(path, null);
    }
    public abstract void drawPath(Path path, float x, float y);

    public void drawText(CharSequence text, float x, float y) {
        drawText(text, 0, text.length(), x, y);
    }
    public abstract void drawText(CharSequence text, int start, int end, float x, float y);
    public void drawText(CharSequence text, Transform transform) {
        drawText(text, 0, text.length(), transform);
    }
    public abstract void drawText(CharSequence text, int start, int end, Transform transform);
    public void drawText(char[] text, float x, float y) {
        drawText(text, 0, text.length, x, y);
    }
    public abstract void drawText(char[] text, int offset, int length, float x, float y);
    public void drawText(char[] text, Transform transform) {
        drawText(text, 0, text.length, transform);
    }
    public abstract void drawText(char[] text, int offset, int length, Transform transform);
    public void drawTextOnPath(CharSequence text, Path path, float startOffset, int x, int y) {
        drawTextOnPath(text, 0, text.length(), path, startOffset, x, y);
    }
    public abstract void drawTextOnPath(CharSequence text, int start, int end, Path path, float startOffset, int x, int y);
    public void drawTextOnPath(char[] text, Path path, float startOffset, int x, int y) {
        drawTextOnPath(text, 0, text.length, path, startOffset, x, y);
    }
    public abstract void drawTextOnPath(char[] text, int offset, int length, Path path, float startOffset, int x, int y);
    public void drawTextOnPath(CharSequence text, Path path, float startOffset, Transform transform) {
        drawTextOnPath(text, 0, text.length(), path, startOffset, transform);
    }
    public abstract void drawTextOnPath(CharSequence text, int start, int end, Path path, float startOffset, Transform transform);
    public void drawTextOnPath(char[] text, Path path, float startOffset, Transform transform) {
        drawTextOnPath(text, 0, text.length, path, startOffset, transform);
    }
    public abstract void drawTextOnPath(char[] text, int offset, int length, Path path, float startOffset, Transform transform);

    public abstract void measureText(CharSequence text, int start, int end, Text.Metrics metrics, RectangleF bounds);
    public void measureText(CharSequence text, Text.Metrics metrics, RectangleF bounds) {
        measureText(text, 0, text.length(), metrics, bounds);
    }
    public abstract void measureText(char[] text, int offset, int length, Text.Metrics metrics, RectangleF bounds);
    public void measureText(char[] text, Text.Metrics metrics, RectangleF bounds) {
        measureText(text, 0, text.length, metrics, bounds);
    }

    public abstract void getInfo(Info info);

    public abstract Path getClip();
    public abstract void setClip(Path clip);
    public abstract Transform getTransform();
    public abstract void setTransform(Transform transform);
    public abstract Composite getComposite();
    public abstract void setComposite(Composite composite);
    public abstract Brush getBrush();
    public abstract void setBrush(Brush brush);
    public abstract int getStyle();
    public abstract void setStyle(int style);
    public abstract float getStrokeWidth();
    public abstract void setStrokeWidth(float width);
    public abstract int getStrokeJoin();
    public abstract void setStrokeJoin(int join);
    public abstract int getStrokeCap();
    public abstract void setStrokeCap(int cap);
    public abstract float getStrokeMiterLimit();
    public abstract void setStrokeMiterLimit(float miterLimit);
    public abstract float[] getStrokeDash();
    public abstract void setStrokeDash(float... dash);
    public abstract float getStrokeDashPhase();
    public abstract void setStrokeDashPhase(float dashPhase);
    public abstract boolean isAntiAlias();
    public abstract void setAntiAlias(boolean antiAlias);
    public abstract boolean isImageSmoothing();
    public abstract void setImageSmoothing(boolean imageSmoothing);
    public abstract boolean isDither();
    public abstract void setDither(boolean dither);
    public abstract Font getFont();
    public abstract void setFont(Font font);
    public abstract float getTextSize();
    public abstract void setTextSize(float size);
    public abstract int getTextDirection();
    public abstract void setTextDirection(int direction);
    public abstract int getTextAlignment();
    public abstract void setTextAlignment(int alignment);
    public abstract boolean isSubpixelText();
    public abstract void setSubpixelText(boolean subpixelText);
    public abstract boolean isUnderlineText();
    public abstract void setUnderlineText(boolean underlineText);
    public abstract boolean isStrikeThroughText();
    public abstract void setStrikeThroughText(boolean strikeThroughText);
    public abstract void setTextScaleX(float scaleX);
    public abstract void setTextScaleY(float scaleY);
    public void setTextScale(float scaleX, float scaleY) {
        setTextScaleX(scaleX);
        setTextScaleY(scaleY);
    }
    public abstract float getTextScaleX();
    public abstract float getTextScaleY();
    public abstract void getTextScale(float[] scale, int offset);
    public void getTextScale(float[] scale) {
        getTextScale(scale, 0);
    }
    public abstract void setTextSkewX(float skewX);
    public abstract void setTextSkewY(float skewY);
    public void setTextSkew(float skewX, float skewY) {
        setTextSkewX(skewX);
        setTextSkewY(skewY);
    }
    public abstract float getTextSkewX();
    public abstract float getTextSkewY();
    public abstract void getTextSkew(float[] skew, int offset);
    public void getTextSkew(float[] skew) {
        getTextSkew(skew, 0);
    }

    public void setStroke(float width, int join, int cap, float miterLimit, float[] dash, float dashPhase) {
        setStrokeWidth(width);
        setStrokeJoin(join);
        setStrokeCap(cap);
        setStrokeMiterLimit(miterLimit);
        setStrokeDash(dash);
        setStrokeDashPhase(dashPhase);
    }

    public void setAntiAlias(boolean antiAlias, boolean imageSmoothing, boolean dither) {
        setAntiAlias(antiAlias);
        setImageSmoothing(imageSmoothing);
        setDither(dither);
    }

    public void setTextAttributes(Font font, float size, int direction, int alignment, boolean subpixel, boolean underline, boolean strikeThrough) {
        setFont(font);
        setTextSize(size);
        setTextDirection(direction);
        setTextAlignment(alignment);
        setSubpixelText(subpixel);
        setUnderlineText(underline);
        setStrikeThroughText(strikeThrough);
    }

    public void setTextTransform(float scaleX, float scaleY, float skewX, float skewY) {
        setTextScale(scaleX, scaleY);
        setTextSkew(skewX, skewY);
    }

    public void setInfo(Path clip, Transform transform, Composite composite,
                        Brush brush, int style,
                        float strokeWidth, int strokeJoin, int strokeCap, float strokeMiterLimit, float[] strokeDash, float strokeDashPhase,
                        boolean antiAlias, boolean imageSmoothing, boolean dither,
                        Font font, float textSize, int textDirection, int textAlignment, boolean subpixelText, boolean underlineText, boolean strikeThroughText,
                        float textScaleX, float textScaleY, float textSkewX, float textSkewY) {
        setClip(clip);
        setTransform(transform);
        setComposite(composite);
        setBrush(brush);
        setStyle(style);
        setStroke(strokeWidth, strokeJoin, strokeCap, strokeMiterLimit, strokeDash, strokeDashPhase);
        setAntiAlias(antiAlias, imageSmoothing, dither);
        setTextAttributes(font, textSize, textDirection, textAlignment, subpixelText, underlineText, strikeThroughText);
        setTextTransform(textScaleX, textScaleY, textSkewX, textSkewY);
    }

    public void setInfo(Info info) {
        setInfo(info.getClip(), info.getTransform(), info.getComposite(),
                info.getBrush(), info.getStyle(),
                info.getStrokeWidth(), info.getStrokeJoin(), info.getStrokeCap(), info.getStrokeMiterLimit(), info.getStrokeDash(), info.getStrokeDashPhase(),
                info.isAntiAlias(), info.isImageSmoothing(), info.isDither(),
                info.getFont(), info.getTextSize(), info.getTextDirection(), info.getTextAlignment(), info.isSubpixelText(), info.isUnderlineText(), info.isStrikeThroughText(),
                info.getTextScaleX(), info.getTextScaleY(), info.getTextSkewX(), info.getTextSkewY());
    }

    private final Queue<Info> infoQueue = new ArrayDeque<>();
    public int getInfoQueueSize() {
        return infoQueue.size();
    }

    public void save() {
        Info info = new Info();
        getInfo(info);
        infoQueue.add(info);
    }
    public void restore() {
        setInfo(infoQueue.poll());
    }
    public void restore(int depth) {
        depth = Math.min(depth, getInfoQueueSize());
        for (int i = 0; i < depth; i ++) {
            restore();
        }
    }
    public void restoreAll() {
        int depth = getInfoQueueSize();
        for (int i = 0; i < depth; i ++) {
            restore();
        }
    }
    public void restoreTo(int index) {
        int max = getInfoQueueSize();
        index = FastMath.clamp(index, 0, max);
        for (int i = index; i < max; i ++) {
            restore();
        }
    }

    @Override
    public void reset() {
        setInfo(null, null, null,
                null, Style.FILL,
                16, Stroke.Join.MITER, Stroke.Cap.BUTT, 10, null, 0,
                true, false, false,
                null, 12, Text.Direction.AUTO, Text.Alignment.START, true, false, false,
                1, 1, 0, 0);
    }

    @Override
    public boolean isIdentity() {
        return getClip() == null && getTransform() == null && getComposite() == null &&
                getBrush() == null && getStyle() == Style.FILL &&
                getStrokeWidth() == 16 && getStrokeJoin() == Stroke.Join.MITER && getStrokeCap() == Stroke.Cap.BUTT && getStrokeMiterLimit() == 10 && getStrokeDash() == null && getStrokeDashPhase() == 0 &&
                isAntiAlias() && !isImageSmoothing() && !isDither() &&
                getFont() == null && getTextSize() == 12 && getTextDirection() == Text.Direction.AUTO && getTextAlignment() == Text.Alignment.START && isSubpixelText() && !isUnderlineText() && !isStrikeThroughText() &&
                getTextScaleX() == 1 && getTextScaleY() == 1 && getTextSkewX() == 0 && getTextSkewY() == 0;
    }

}
