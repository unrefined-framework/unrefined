package unrefined.runtime;

import unrefined.internal.AWTUtils;
import unrefined.media.graphics.PointF;
import unrefined.media.graphics.RectangleF;
import unrefined.media.graphics.Transform;
import unrefined.media.graphics.Path;
import unrefined.util.AlreadyDisposedException;

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.awt.geom.Path2D.WIND_NON_ZERO;

public class DesktopPath extends Path {

    private volatile Path2D.Float path2D;

    public DesktopPath(Path2D.Float path2D) {
        this.path2D = path2D == null ? new Path2D.Float() : path2D;
    }

    public DesktopPath() {
        this.path2D = new Path2D.Float();
    }

    public Path2D.Float getPath2D() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return path2D;
    }

    @Override
    public void addPath(Path path, Transform transform) {
        if (isDisposed()) throw new AlreadyDisposedException();
        if (transform == null) path2D.append(((DesktopPath) path).path2D, false);
        else path2D.append(((DesktopTransform) transform).getAffineTransform().createTransformedShape(((DesktopPath) path).path2D), false);
    }

    @Override
    public void addPoint(float x, float y) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.append(new Line2D.Float(x, y, x, y), false);
    }

    @Override
    public void addLine(float x1, float y1, float x2, float y2) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.append(new Line2D.Float(x1, y1, x2, y2), false);
    }

    @Override
    public void addQuadLine(float x1, float y1, float cx, float cy, float x2, float y2) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.append(new QuadCurve2D.Float(x1, y1, cx, cy, x2, y2), false);
    }

    @Override
    public void addCubicLine(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.append(new CubicCurve2D.Float(x1, y1, cx1, cy1, cx2, cy2, x2, y2), false);
    }

    @Override
    public void addArc(float x, float y, float width, float height, float startAngle, float sweepAngle) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.append(new Arc2D.Float(x, y, width, height, startAngle, sweepAngle, Arc2D.OPEN), false);
    }

    @Override
    public void addCircle(float x, float y, float radius) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.append(new Ellipse2D.Float(x - radius, y - radius, radius * 2, radius * 2), false);
    }

    @Override
    public void addEllipse(float x, float y, float width, float height) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.append(new Ellipse2D.Float(x, y, width, height), false);
    }

    @Override
    public void addRectangle(float x, float y, float width, float height) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.append(new Rectangle2D.Float(x, y, width, height), false);
    }

    @Override
    public void addRoundRectangle(float x, float y, float width, float height, float rx, float ry) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.append(new RoundRectangle2D.Float(x, y, width, height, rx, ry), false);
    }

    @Override
    public void moveTo(float x, float y) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.lineTo(x, y);
    }

    @Override
    public void quadTo(float cx, float cy, float x, float y) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.quadTo(cx, cy, x, y);
    }

    @Override
    public void cubicTo(float cx1, float cy1, float cx2, float cy2, float x, float y) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.curveTo(cx1, cy1, cx2, cy2, x, y);
    }

    @Override
    public void arcTo(float x, float y, float width, float height, float startAngle, float sweepAngle) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.lineTo(x, y);
        path2D.append(new Arc2D.Float(x, y, width, height, startAngle, sweepAngle, Arc2D.OPEN), false);
    }

    @Override
    public void rMoveTo(float dx, float dy) {
        if (isDisposed()) throw new AlreadyDisposedException();
        Point2D point = path2D.getCurrentPoint();
        path2D.moveTo(point.getX() + dx, point.getY() + dy);
    }

    @Override
    public void rLineTo(float dx, float dy) {
        if (isDisposed()) throw new AlreadyDisposedException();
        Point2D point = path2D.getCurrentPoint();
        path2D.lineTo(point.getX() + dx, point.getY() + dy);
    }

    @Override
    public void rQuadTo(float dx1, float dy1, float dx2, float dy2) {
        if (isDisposed()) throw new AlreadyDisposedException();
        Point2D point = path2D.getCurrentPoint();
        path2D.quadTo(point.getX() + dx1, point.getY() + dy1, dx2, dy2);
    }

    @Override
    public void rCubicTo(float dx1, float dy1, float dx2, float dy2, float dx3, float dy3) {
        if (isDisposed()) throw new AlreadyDisposedException();
        Point2D point = path2D.getCurrentPoint();
        path2D.curveTo(point.getX() + dx1, point.getY() + dy1, dx2, dy2, dx3, dy3);
    }

    @Override
    public void rArcTo(float dx, float dy, float width, float height, float startAngle, float sweepAngle) {
        if (isDisposed()) throw new AlreadyDisposedException();
        Point2D point = path2D.getCurrentPoint();
        path2D.append(AffineTransform.getTranslateInstance(point.getX(), point.getY())
                .createTransformedShape(new Arc2D.Float(dx, dy, width, height, startAngle, sweepAngle, Arc2D.OPEN)), false);
    }

    @Override
    public void getLastPoint(PointF point) {
        if (isDisposed()) throw new AlreadyDisposedException();
        Point2D point2D = path2D.getCurrentPoint();
        point.setPoint((float) point2D.getX(), (float) point2D.getY());
    }

    @Override
    public void setFillRule(int rule) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.setWindingRule(FillRule.checkValid(rule));
    }

    @Override
    public int getFillRule() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return path2D.getWindingRule();
    }

    @Override
    public void transform(Transform transform) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.transform(((DesktopTransform) transform).getAffineTransform());
    }

    @Override
    public void transform(Transform transform, Path dst) {
        if (isDisposed() || dst.isDisposed()) throw new AlreadyDisposedException();
        (((DesktopPath) dst).path2D =
                new Path2D.Float(path2D)).transform(((DesktopTransform) transform).getAffineTransform());
    }

    @Override
    public boolean isEmpty() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return AWTUtils.getNumCoords(path2D) == 0;
    }

    @Override
    public void close() {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.closePath();
    }

    @Override
    public void getBounds(RectangleF bounds) {
        if (isDisposed()) throw new AlreadyDisposedException();
        Rectangle2D bounds2D = path2D.getBounds2D();
        bounds.setRectangle(
                (float) bounds2D.getX(),
                (float) bounds2D.getY(),
                (float) bounds2D.getWidth(),
                (float) bounds2D.getHeight());
    }

    private final AtomicBoolean disposed = new AtomicBoolean(false);

    @Override
    public void dispose() {
        if (disposed.compareAndSet(false, true)) path2D = null;
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }

    @Override
    public boolean contains(float x, float y) {
        if (isDisposed()) throw new AlreadyDisposedException();
        return path2D.contains(x, y);
    }

    @Override
    public boolean contains(float x, float y, float width, float height) {
        if (isDisposed()) throw new AlreadyDisposedException();
        return path2D.contains(x, y, width, height);
    }

    @Override
    public void outRectangle(float x, float y, float width, float height, Path dst) {
        if (isDisposed() || dst.isDisposed()) throw new AlreadyDisposedException();
        Area area = new Area(new Rectangle2D.Float(x, y, width, height));
        area.subtract(new Area(path2D));
        ((DesktopPath) dst).path2D = new Path2D.Float(area);
    }

    @Override
    public void outPath(Path other, Path dst) {
        if (isDisposed() || dst.isDisposed()) throw new AlreadyDisposedException();
        Area area = new Area(((DesktopPath) other).path2D);
        area.subtract(new Area(path2D));
        ((DesktopPath) dst).path2D = new Path2D.Float(area);
    }

    @Override
    public Path clone() {
        if (isDisposed()) throw new AlreadyDisposedException();
        DesktopPath clone = (DesktopPath) super.clone();
        if (clone == null) return copy();
        else {
            clone.path2D = (Path2D.Float) path2D.clone();
            return clone;
        }
    }

    @Override
    public Path copy() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return new DesktopPath((Path2D.Float) path2D.clone());
    }

    @Override
    public void to(Object dst) {
        if (isDisposed()) throw new AlreadyDisposedException();
        ((DesktopPath) dst).path2D = (Path2D.Float) path2D.clone();
    }

    @Override
    public void from(Object src) {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D = (Path2D.Float) ((DesktopPath) src).path2D.clone();
    }

    @Override
    public void swap(Object o) {
        if (isDisposed()) throw new AlreadyDisposedException();
        DesktopPath that = (DesktopPath) o;
        Path2D.Float path2D = that.path2D;
        that.path2D = this.path2D;
        this.path2D = path2D;
    }

    @Override
    public void reset() {
        if (isDisposed()) throw new AlreadyDisposedException();
        path2D.reset();
        path2D.trimToSize();
        path2D.setWindingRule(WIND_NON_ZERO);
    }

    @Override
    public boolean isIdentity() {
        return isEmpty() && path2D.getWindingRule() == WIND_NON_ZERO;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopPath path = (DesktopPath) object;

        return Objects.equals(path2D, path.path2D);
    }

    @Override
    public int hashCode() {
        return path2D != null ? path2D.hashCode() : 0;
    }

}
