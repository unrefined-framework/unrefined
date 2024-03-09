package unrefined.media.graphics;

import unrefined.io.Savable;
import unrefined.util.NotInstantiableError;

import static unrefined.media.graphics.Transform.Index.*;

public abstract class Transform implements Savable {

    public static Transform of() {
        return Drawing.getInstance().createTransform();
    }

    public static Transform of(float sx, float kx, float dx, float ky, float sy, float dy) {
        return Drawing.getInstance().createTransform(sx, kx, dx, ky, sy, dy);
    }

    public static Transform ofValues(float... values) {
        return Drawing.getInstance().createTransform(values);
    }

    public static Transform ofScale(float sx, float sy) {
        return Drawing.getInstance().createScaleTransform(sx, sy);
    }

    public static Transform ofSkew(float kx, float ky) {
        return Drawing.getInstance().createSkewTransform(kx, ky);
    }

    public static Transform ofTranslate(float dx, float dy) {
        return Drawing.getInstance().createTranslateTransform(dx, dy);
    }

    public static Transform ofRotate(float radians, float px, float py) {
        return Drawing.getInstance().createRotateTransform(radians, px, py);
    }

    public static Transform ofRotate(float radians) {
        return Drawing.getInstance().createRotateTransform(radians);
    }

    public static final class Index {
        private Index() {
            throw new NotInstantiableError(Index.class);
        }
        public static final int SCALE_X = 0;
        public static final int SKEW_X = 1;
        public static final int TRANSLATE_X = 2;
        public static final int SKEW_Y = 3;
        public static final int SCALE_Y = 4;
        public static final int TRANSLATE_Y = 5;
        public static boolean isValid(int index) {
            return index >= SCALE_X && index <= TRANSLATE_Y;
        }
        public static int checkValid(int index) {
            if (index < SCALE_X || index > TRANSLATE_Y) throw new IndexOutOfBoundsException("Index out of range: " + index);
            else return index;
        }
        public static String toString(int index) {
            switch (index) {
                case SCALE_X: return "SCALE_X";
                case SKEW_X: return "SKEW_X";
                case TRANSLATE_X: return "TRANSLATE_X";
                case SKEW_Y: return "SKEW_Y";
                case SCALE_Y: return "SCALE_Y";
                case TRANSLATE_Y: return "TRANSLATE_Y";
                default: throw new IndexOutOfBoundsException("Index out of range: " + index);
            }
        }
    }

    public void setTransform(float sx, float kx, float dx, float ky, float sy, float dy) {
        setScale(sx, sy);
        setSkew(kx, ky);
        setTranslate(dx, dy);
    }

    public abstract void setTransform(Transform transform);
    
    public void preTransform(float sx, float kx, float dx, float ky, float sy, float dy) {
        preScale(sx, sy);
        preSkew(kx, ky);
        preTranslate(dx, dy);
    }
    
    public void transform(float sx, float kx, float dx, float ky, float sy, float dy) {
        scale(sx, sy);
        skew(kx, ky);
        translate(dx, dy);
    }
    
    public void postTransform(float sx, float kx, float dx, float ky, float sy, float dy) {
        postScale(sx, sy);
        postSkew(kx, ky);
        postTranslate(dx, dy);
    }

    public void setValues(float... values) {
        setTransform(values[SCALE_X], values[SKEW_X], values[TRANSLATE_X], values[SKEW_Y], values[SCALE_Y], values[TRANSLATE_Y]);
    }
    
    public void preValues(float... values) {
        preTransform(values[SCALE_X], values[SKEW_X], values[TRANSLATE_X], values[SKEW_Y], values[SCALE_Y], values[TRANSLATE_Y]);
    }
    
    public void values(float... values) {
        transform(values[SCALE_X], values[SKEW_X], values[TRANSLATE_X], values[SKEW_Y], values[SCALE_Y], values[TRANSLATE_Y]);
    }
    
    public void postValues(float... values) {
        postTransform(values[SCALE_X], values[SKEW_X], values[TRANSLATE_X], values[SKEW_Y], values[SCALE_Y], values[TRANSLATE_Y]);
    }

    public abstract boolean invert();

    public abstract void concatenate(Transform a, Transform b);
    public abstract void preConcatenate(Transform other);
    public abstract void postConcatenate(Transform other);

    public abstract void setScale(float sx, float sy);
    public abstract void preScale(float sx, float sy);
    public abstract void scale(float sx, float sy);
    public abstract void postScale(float sx, float sy);
    
    public abstract void setSkew(float kx, float ky);
    public abstract void preSkew(float kx, float ky);
    public abstract void skew(float kx, float ky);
    public abstract void postSkew(float kx, float ky);

    public abstract void setTranslate(float dx, float dy);
    public abstract void preTranslate(float dx, float dy);
    public abstract void translate(float dx, float dy);
    public abstract void postTranslate(float dx, float dy);

    public abstract void setRotate(float radians, float px, float py);
    public abstract void setRotate(float radians);
    public abstract void preRotate(float radians, float px, float py);
    public abstract void preRotate(float radians);
    public abstract void rotate(float radians, float px, float py);
    public abstract void rotate(float radians);
    public abstract void postRotate(float radians, float px, float py);
    public abstract void postRotate(float radians);

    public abstract void getValues(float[] values, int offset);
    public void getValues(float[] values) {
        getValues(values, 0);
    }
    public abstract void getScale(float[] scale, int offset);
    public void getScale(float[] scale) {
        getScale(scale, 0);
    }
    public abstract void getSkew(float[] skew, int offset);
    public void getSkew(float[] skew) {
        getSkew(skew, 0);
    }
    public abstract void getTranslate(float[] translate, int offset);
    public void getTranslate(float[] translate) {
        getTranslate(translate, 0);
    }

    public abstract void transformPoints(float[] src, int srcOffset, float[] dst, int dstOffset, int pointCount);
    public void transformPoints(float[] src, int srcOffset, float[] dst, int dstOffset) {
        if (src.length != dst.length || (src.length - srcOffset) % 2 != 0) throw new IndexOutOfBoundsException("Array length mismatch");
        else transformPoints(src, srcOffset, dst, dstOffset, (src.length - srcOffset) / 2);
    }
    public void transformPoints(float[] src, int srcOffset, PointF[] dst, int dstOffset) {
        int pointCount = dst.length - dstOffset;
        if (src.length - srcOffset != pointCount * 2) throw new IndexOutOfBoundsException("Array length mismatch");
        transformPoints(src, srcOffset, dst, dstOffset, pointCount);
    }
    public void transformPoints(PointF[] src, int srcOffset, float[] dst, int dstOffset) {
        int pointCount = src.length - srcOffset;
        if (dst.length - dstOffset != pointCount * 2) throw new IndexOutOfBoundsException("Array length mismatch");
        transformPoints(src, srcOffset, dst, dstOffset, pointCount);
    }
    public void transformPoints(float[] points, int offset, int pointCount) {
        transformPoints(points, offset, points, offset, pointCount);
    }
    public void transformPoints(float[] src, float[] dst) {
        if (src.length != dst.length || src.length % 2 != 0) throw new IndexOutOfBoundsException("Array length mismatch");
        else transformPoints(src, 0, dst, 0, src.length / 2);
    }
    public void transformPoints(float[] src, float[] dst, int pointCount) {
        transformPoints(src, 0, dst, 0, pointCount);
    }
    public void transformPoints(float[] src, PointF[] dst, int pointCount) {
        transformPoints(src, 0, dst, 0, pointCount);
    }
    public void transformPoints(PointF[] src, float[] dst, int pointCount) {
        transformPoints(src, 0, dst, 0, pointCount);
    }
    public void transformPoints(float[] points) {
        transformPoints(points, points);
    }
    public void transformPoints(float[] points, int offset) {
        transformPoints(points, offset, points, offset);
    }
    public abstract void transformPoints(PointF[] src, int srcOffset, float[] dst, int dstOffset, int pointCount);
    public void transformPoints(PointF[] src, float[] dst) {
        if (src.length * 2 != dst.length) throw new IndexOutOfBoundsException("Array length mismatch");
        else transformPoints(src, 0, dst, 0, src.length);
    }
    public abstract void transformPoints(float[] src, int srcOffset, PointF[] dst, int dstOffset, int pointCount);
    public void transformPoints(float[] src, PointF[] dst) {
        if (src.length != dst.length * 2) throw new IndexOutOfBoundsException("Array length mismatch");
        else transformPoints(src, 0, dst, 0, dst.length);
    }
    public abstract void transformPoints(PointF[] src, int srcOffset, PointF[] dst, int dstOffset, int pointCount);
    public void transformPoints(PointF[] src, int srcOffset, PointF[] dst, int dstOffset) {
        if (src.length != dst.length) throw new IndexOutOfBoundsException("Array length mismatch");
        transformPoints(src, srcOffset, dst, dstOffset, src.length - srcOffset);
    }
    public void transformPoints(PointF[] src, PointF[] dst, int pointCount) {
        transformPoints(src, 0, dst, 0, pointCount);
    }
    public void transformPoints(PointF[] points, int offset, int pointCount) {
        transformPoints(points, offset, points, offset, pointCount);
    }
    public void transformPoints(PointF[] points, int offset) {
        transformPoints(points, offset, points, offset);
    }
    public void transformPoints(PointF[] src, PointF[] dst) {
        if (src.length != dst.length) throw new IndexOutOfBoundsException("Array length mismatch");
        else transformPoints(src, 0, dst, 0, src.length);
    }
    public void transformPoints(PointF[] points) {
        transformPoints(points, points);
    }
    public abstract void transformPoint(float x, float y, float[] dst, int dstOffset);
    public void transformPoint(float x, float y, float[] dst) {
        transformPoint(x, y, dst, 0);
    }
    public abstract void transformPoint(float x, float y, PointF dst);
    public void transformPoint(PointF src, PointF dst) {
        transformPoint(src.getX(), src.getY(), dst);
    }
    public void transformPoint(PointF point) {
        transformPoint(point, point);
    }

    @Override
    public Transform clone() {
        try {
            return (Transform) super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public abstract Transform copy();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        float[] values = new float[6];
        getValues(values);
        builder.append("[[").append(values[0]).append(", ").append(values[1]).append(", ").append(values[2]).append("], ");
        builder.append('[').append(values[3]).append(", ").append(values[4]).append(", ").append(values[5]).append("]]");
        return getClass().getName()
                + '{' +
                "values=" + builder +
                '}';
    }

}
