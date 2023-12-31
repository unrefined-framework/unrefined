package unrefined.runtime;

import unrefined.io.BinaryInput;
import unrefined.io.BinaryOutput;
import unrefined.io.BundleInput;
import unrefined.io.BundleOutput;
import unrefined.media.graphics.PointF;
import unrefined.media.graphics.Transform;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;

public class DesktopTransform extends Transform {

    private final AffineTransform transform;

    public DesktopTransform(AffineTransform transform) {
        this.transform = transform == null ? new AffineTransform() : transform;
    }

    public DesktopTransform() {
        this.transform = new AffineTransform();
    }

    public AffineTransform getAffineTransform() {
        return transform;
    }

    @Override
    public void writePortable(BinaryOutput out) throws IOException {
        out.writeFloat((float) transform.getScaleX());
        out.writeFloat((float) transform.getShearX());
        out.writeFloat((float) transform.getTranslateX());
        out.writeFloat((float) transform.getShearY());
        out.writeFloat((float) transform.getScaleY());
        out.writeFloat((float) transform.getTranslateY());
    }

    @Override
    public void readPortable(BinaryInput in) throws IOException {
        float sx = in.readFloat();
        float kx = in.readFloat();
        float dx = in.readFloat();
        float ky = in.readFloat();
        float sy = in.readFloat();
        float dy = in.readFloat();
        transform.setTransform(sx, ky, kx, sy, dx, dy);
    }

    @Override
    public void setScale(float sx, float sy) {
        transform.setToScale(sx, sy);
    }

    @Override
    public void setSkew(float kx, float ky) {
        transform.setToShear(kx, ky);
    }

    @Override
    public void preSkew(float kx, float ky) {
        transform.preConcatenate(AffineTransform.getShearInstance(kx, ky));
    }

    @Override
    public void skew(float kx, float ky) {
        transform.shear(kx, ky);
    }

    @Override
    public void postSkew(float kx, float ky) {
        transform.concatenate(AffineTransform.getShearInstance(kx, ky));
    }

    @Override
    public void to(Object dst) {
        ((DesktopTransform) dst).transform.setTransform(transform);
    }

    @Override
    public void from(Object src) {
        transform.setTransform(((DesktopTransform) src).transform);
    }

    @Override
    public void swap(Object o) {
        AffineTransform tmp = ((DesktopTransform) o).transform;
        double sx = tmp.getScaleX();
        double kx = tmp.getShearX();
        double dx = tmp.getTranslateX();
        double ky = tmp.getShearY();
        double sy = tmp.getScaleY();
        double dy = tmp.getTranslateY();
        transform.setTransform(tmp);
        tmp.setTransform(sx, ky, kx, sy, dx, dy);
    }

    @Override
    public void setTranslate(float dx, float dy) {
        transform.setToTranslation(dx, dy);
    }

    @Override
    public void preTranslate(float dx, float dy) {
        transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
    }

    @Override
    public void translate(float dx, float dy) {
        transform.translate(dx, dy);
    }

    @Override
    public void postTranslate(float dx, float dy) {
        transform.concatenate(AffineTransform.getTranslateInstance(dx, dy));
    }

    @Override
    public void setTransform(Transform transform) {
        this.transform.setTransform(((DesktopTransform) transform).getAffineTransform());
    }

    @Override
    public boolean invert() {
        try {
            transform.invert();
            return true;
        } catch (NoninvertibleTransformException e) {
            return false;
        }
    }

    @Override
    public void concatenate(Transform a, Transform b) {
        transform.setTransform(((DesktopTransform) a).transform);
        transform.concatenate(((DesktopTransform) b).transform);
    }

    @Override
    public void preConcatenate(Transform other) {
        transform.preConcatenate(((DesktopTransform) other).transform);
    }

    @Override
    public void postConcatenate(Transform other) {
        transform.concatenate(((DesktopTransform) other).transform);
    }

    @Override
    public void preScale(float sx, float sy) {
        transform.preConcatenate(AffineTransform.getScaleInstance(sx, sy));
    }

    @Override
    public void scale(float sx, float sy) {
        transform.scale(sx, sy);
    }

    @Override
    public void postScale(float sx, float sy) {
        transform.concatenate(AffineTransform.getScaleInstance(sx, sy));
    }

    @Override
    public void setRotate(float radians, float px, float py) {
        transform.setToRotation(radians, px, py);
    }

    @Override
    public void setRotate(float radians) {
        transform.setToRotation(radians);
    }

    @Override
    public void preRotate(float radians, float px, float py) {
        transform.preConcatenate(AffineTransform.getRotateInstance(radians, px, py));
    }

    @Override
    public void preRotate(float radians) {
        transform.preConcatenate(AffineTransform.getRotateInstance(radians));
    }

    @Override
    public void rotate(float radians, float px, float py) {
        transform.rotate(radians, px, py);
    }

    @Override
    public void rotate(float radians) {
        transform.rotate(radians);
    }

    @Override
    public void postRotate(float radians, float px, float py) {
        transform.concatenate(AffineTransform.getRotateInstance(radians, px, py));
    }

    @Override
    public void postRotate(float radians) {
        transform.concatenate(AffineTransform.getRotateInstance(radians));
    }

    @Override
    public void getValues(float[] values, int offset) {
        values[offset] = (float) transform.getScaleX();
        values[offset + 1] = (float) transform.getShearX();
        values[offset + 2] = (float) transform.getTranslateX();
        values[offset + 3] = (float) transform.getShearY();
        values[offset + 4] = (float) transform.getScaleY();
        values[offset + 5] = (float) transform.getTranslateY();
    }

    @Override
    public void getScale(float[] scale, int offset) {
        scale[offset] = (float) transform.getScaleX();
        scale[offset + 1] = (float) transform.getScaleY();
    }

    @Override
    public void getSkew(float[] skew, int offset) {
        skew[offset] = (float) transform.getShearX();
        skew[offset + 1] = (float) transform.getShearY();
    }

    @Override
    public void getTranslate(float[] translate, int offset) {
        translate[offset] = (float) transform.getTranslateX();
        translate[offset + 1] = (float) transform.getTranslateY();
    }

    @Override
    public void transformPoints(float[] src, int srcOffset, float[] dst, int dstOffset, int pointCount) {
        transform.transform(src, srcOffset, dst, dstOffset, pointCount);
    }

    @Override
    public void transformPoints(PointF[] src, int srcOffset, float[] dst, int dstOffset, int pointCount) {
        float[] srcArray = new float[pointCount * 2];
        for (int i = 0; i < pointCount; i ++) {
            srcArray[i * 2] = src[i + srcOffset].getX();
            srcArray[i * 2 + 1] = src[i + srcOffset].getY();
        }
        transform.transform(srcArray, srcOffset, dst, dstOffset, pointCount);
    }

    @Override
    public void transformPoints(float[] src, int srcOffset, PointF[] dst, int dstOffset, int pointCount) {
        float[] dstArray = new float[pointCount * 2];
        transform.transform(src, srcOffset, dstArray, dstOffset, pointCount);
        for (int i = 0; i < pointCount; i ++) {
            dst[i + dstOffset].setPoint(dstArray[i * 2], dstArray[i * 2 + 1]);
        }
    }

    @Override
    public void transformPoints(PointF[] src, int srcOffset, PointF[] dst, int dstOffset, int pointCount) {
        float[] srcArray = new float[pointCount * 2];
        for (int i = 0; i < pointCount; i ++) {
            srcArray[i * 2] = src[i + srcOffset].getX();
            srcArray[i * 2 + 1] = src[i + srcOffset].getY();
        }
        float[] dstArray = new float[pointCount * 2];
        transform.transform(srcArray, srcOffset, dstArray, dstOffset, pointCount);
        for (int i = 0; i < pointCount; i ++) {
            dst[i + dstOffset].setPoint(dstArray[i * 2], dstArray[i * 2 + 1]);
        }
    }

    @Override
    public void transformPoint(float x, float y, float[] dst, int dstOffset) {
        transform.transform(new float[] { x, y }, 0, dst, dstOffset, 1);
    }

    @Override
    public void transformPoint(float x, float y, PointF dst) {
        float[] dstArray = new float[2];
        transform.transform(new float[] { x, y }, 0, dstArray, 0, 1);
        dst.setPoint(dstArray[0], dstArray[1]);
    }

    @Override
    public void transformPoint(PointF src, PointF dst) {
        float[] dstArray = new float[2];
        transform.transform(new float[] { src.getX(), src.getY() }, 0, dstArray, 0, 1);
        dst.setPoint(dstArray[0], dstArray[1]);
    }

    @Override
    public void transformPoint(PointF point) {
        float[] array = new float[] { point.getX(), point.getY() };
        transform.transform(array, 0, array, 0, 1);
        point.setPoint(array[0], array[1]);
    }

    @Override
    public Transform clone() {
        DesktopTransform clone = (DesktopTransform) super.clone();
        if (clone == null) return copy();
        else return clone;
    }

    @Override
    public Transform copy() {
        return new DesktopTransform((AffineTransform) transform.clone());
    }

    @Override
    public void reset() {
        transform.setToIdentity();
    }

    @Override
    public boolean isIdentity() {
        return transform.isIdentity();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopTransform that = (DesktopTransform) object;

        return transform.equals(that.transform);
    }

    @Override
    public int hashCode() {
        return transform.hashCode();
    }

    @Override
    public void writeToBundle(BundleOutput out) throws IOException {
        out.putFloat("scaleX", (float) transform.getScaleX());
        out.putFloat("skewY", (float) transform.getShearY());
        out.putFloat("skewX", (float) transform.getShearX());
        out.putFloat("scaleY", (float) transform.getScaleY());
        out.putFloat("translateX", (float) transform.getTranslateX());
        out.putFloat("translateY", (float) transform.getTranslateY());
    }

    @Override
    public void readFromBundle(BundleInput in) throws IOException {
        transform.setTransform(in.getFloat("scaleX", 1),
                in.getFloat("skewY", 0),
                in.getFloat("skewX", 0),
                in.getFloat("scaleY", 1),
                in.getFloat("translateX", 0),
                in.getFloat("translateY", 0));
    }

}
