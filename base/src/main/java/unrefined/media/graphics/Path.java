package unrefined.media.graphics;

import unrefined.util.Copyable;
import unrefined.io.Disposable;
import unrefined.util.NotInstantiableError;
import unrefined.util.Resettable;
import unrefined.util.Swappable;

import static unrefined.media.graphics.Path.SegmentType.*;

public abstract class Path implements Disposable, Copyable, Swappable, Resettable {

    public static final class FillRule {
        private FillRule() {
            throw new NotInstantiableError(FillRule.class);
        }
        public static final int EVEN_ODD = 0;
        public static final int NON_ZERO = 1;
        public static boolean isValid(int rule) {
            return rule >= EVEN_ODD && rule <= NON_ZERO;
        }
        public static int checkValid(int rule) {
            if (rule < EVEN_ODD || rule > NON_ZERO) throw new IllegalArgumentException("Illegal fill rule: " + rule);
            else return rule;
        }
        public static String toString(int rule) {
            switch (rule) {
                case EVEN_ODD: return "EVEN_ODD";
                case NON_ZERO: return "NON_ZERO";
                default: throw new IllegalArgumentException("Illegal fill rule: " + rule);
            }
        }
    }

    public static final class SegmentType {
        private SegmentType() {
            throw new NotInstantiableError(SegmentType.class);
        }
        public static final int MOVE_TO = 0;
        public static final int LINE_TO = 1;
        public static final int QUAD_TO = 2;
        public static final int CUBIC_TO = 3;
        public static boolean isValid(int type) {
            return type >= MOVE_TO && type <= CUBIC_TO;
        }
        public static int checkValid(int type) {
            if (type < MOVE_TO || type > CUBIC_TO) throw new IllegalArgumentException("Illegal segment type: " + type);
            else return type;
        }
        public static String toString(int type) {
            switch (type) {
                case MOVE_TO: return "MOVE_TO";
                case LINE_TO: return "LINE_TO";
                case QUAD_TO: return "QUAD_TO";
                case CUBIC_TO: return "CUBIC_TO";
                default: throw new IllegalArgumentException("Illegal segment type: " + type);
            }
        }
    }

    public abstract void addPath(Path path, Transform transform);
    public void addPath(Path path) {
        addPath(path, null);
    }

    public abstract void addPoint(float x, float y);
    public abstract void addLine(float x1, float y1, float x2, float y2);
    public abstract void addQuadLine(float x1, float y1, float cx, float cy, float x2, float y2);
    public abstract void addCubicLine(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2);
    public abstract void addArc(float x, float y, float width, float height, float startAngle, float sweepAngle);
    public abstract void addCircle(float x, float y, float radius);
    public abstract void addEllipse(float x, float y, float width, float height);
    public abstract void addRectangle(float x, float y, float width, float height);
    public abstract void addRoundRectangle(float x, float y, float width, float height, float rx, float ry);
    public void addRoundRectangle(float x, float y, float width, float height, float r) {
        addRoundRectangle(x, y, width, height, r, r);
    }

    public void moveTo(float x, float y) {
        segmentTo(MOVE_TO, x, y);
    }
    public void lineTo(float x, float y) {
        segmentTo(LINE_TO, x, y);
    }
    public void quadTo(float cx, float cy, float x, float y) {
        segmentTo(QUAD_TO, cx, cy, x, y);
    }
    public void cubicTo(float cx1, float cy1, float cx2, float cy2, float x, float y) {
        segmentTo(CUBIC_TO, cx1, cy1, cx2, cy2, x, y);
    }
    public void segmentTo(int type, float... coords) {
        switch (type) {
            case MOVE_TO: moveTo(coords[0], coords[1]); break;
            case LINE_TO: lineTo(coords[0], coords[1]); break;
            case QUAD_TO: quadTo(coords[0], coords[1], coords[2], coords[3]); break;
            case CUBIC_TO: cubicTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]); break;
            default: throw new IllegalArgumentException("Illegal segment type: " + type);
        }
    }

    public abstract void arcTo(float x, float y, float width, float height, float startAngle, float sweepAngle);

    public void rMoveTo(float dx, float dy) {
        rSegmentTo(MOVE_TO, dx, dy);
    }
    public void rLineTo(float dx, float dy) {
        rSegmentTo(LINE_TO, dx, dy);
    }
    public void rQuadTo(float dx1, float dy1, float dx2, float dy2) {
        rSegmentTo(QUAD_TO, dx1, dy1, dx2, dy2);
    }
    public void rCubicTo(float dx1, float dy1, float dx2, float dy2, float dx3, float dy3) {
        rSegmentTo(CUBIC_TO, dx1, dy1, dx2, dy2, dx3, dy3);
    }
    public void rSegmentTo(int type, float... coords) {
        switch (type) {
            case MOVE_TO: rMoveTo(coords[0], coords[1]); break;
            case LINE_TO: rLineTo(coords[0], coords[1]); break;
            case QUAD_TO: rQuadTo(coords[0], coords[1], coords[2], coords[3]); break;
            case CUBIC_TO: rCubicTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]); break;
            default: throw new IllegalArgumentException("Illegal segment type: " + type);
        }
    }

    public abstract void rArcTo(float dx, float dy, float width, float height, float startAngle, float sweepAngle);

    public abstract void getLastPoint(PointF point);

    public abstract void setFillRule(int rule);
    public abstract int getFillRule();

    public abstract void transform(Transform transform, Path dst);
    public void transform(Transform transform) {
        transform(transform, this);
    }

    public abstract boolean isEmpty();
    public abstract void close();

    public abstract void getBounds(RectangleF bounds);

    public abstract boolean contains(float x, float y);
    public abstract boolean contains(float x, float y, float width, float height);

    public abstract void outRectangle(float x, float y, float width, float height, Path dst);
    public void outRectangle(float x, float y, float width, float height) {
        outRectangle(x, y, width, height, this);
    }

    public abstract void outPath(Path other, Path dst);
    public void outPath(Path other) {
        outPath(other, this);
    }

    @Override
    public Path clone() {
        try {
            return (Path) super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public abstract Path copy();

    @Override
    public String toString() {
        if (isDisposed()) return getClass().getName() + "@" + Integer.toHexString(hashCode())
                + '{' +
                "disposed=true" +
                '}';
        else return getClass().getName() + "@" + Integer.toHexString(hashCode())
                + '{' +
                "disposed=false" +
                ", fillRule=" + FillRule.toString(getFillRule()) +
                '}';
    }

}
