package unrefined.runtime;

import unrefined.io.PortableInput;
import unrefined.io.PortableOutput;
import unrefined.media.graphics.Transform;
import unrefined.util.AlreadyDisposedException;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class DesktopTransform extends Transform {

    private volatile AffineTransform transform;

    public DesktopTransform(AffineTransform transform) {
        this.transform = transform == null ? new AffineTransform() : transform;
    }

    public DesktopTransform() {
        this.transform = new AffineTransform();
    }

    public AffineTransform getAffineTransform() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return transform;
    }

    @Override
    public void writePortable(PortableOutput out) throws IOException {
        if (isDisposed()) throw new AlreadyDisposedException();
        out.writeFloat((float) transform.getScaleX());
        out.writeFloat((float) transform.getShearX());
        out.writeFloat((float) transform.getTranslateX());
        out.writeFloat((float) transform.getShearY());
        out.writeFloat((float) transform.getScaleY());
        out.writeFloat((float) transform.getTranslateY());
    }

    @Override
    public void readPortable(PortableInput in) throws IOException {
        if (isDisposed()) throw new AlreadyDisposedException();
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
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.setToScale(sx, sy);
    }

    @Override
    public void setSkew(float kx, float ky) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.setToShear(kx, ky);
    }

    @Override
    public void preSkew(float kx, float ky) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.preConcatenate(AffineTransform.getShearInstance(kx, ky));
    }

    @Override
    public void rSkew(float kx, float ky) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.shear(kx, ky);
    }

    @Override
    public void postSkew(float kx, float ky) {
        if (isDisposed()) throw new AlreadyDisposedException();
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
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.setToTranslation(dx, dy);
    }

    @Override
    public void preTranslate(float dx, float dy) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
    }

    @Override
    public void rTranslate(float dx, float dy) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.translate(dx, dy);
    }

    @Override
    public void postTranslate(float dx, float dy) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.concatenate(AffineTransform.getTranslateInstance(dx, dy));
    }

    @Override
    public void setTransform(Transform transform) {
        if (isDisposed() || transform.isDisposed()) throw new AlreadyDisposedException();
        this.transform.setTransform(((DesktopTransform) transform).getAffineTransform());
    }

    @Override
    public boolean invert() {
        if (isDisposed()) throw new AlreadyDisposedException();
        try {
            transform.invert();
            return true;
        } catch (NoninvertibleTransformException e) {
            return false;
        }
    }

    @Override
    public void concatenate(Transform a, Transform b) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.setTransform(((DesktopTransform) a).transform);
        transform.concatenate(((DesktopTransform) b).transform);
    }

    @Override
    public void preConcatenate(Transform other) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.preConcatenate(((DesktopTransform) other).transform);
    }

    @Override
    public void postConcatenate(Transform other) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.concatenate(((DesktopTransform) other).transform);
    }

    @Override
    public void preScale(float sx, float sy) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.preConcatenate(AffineTransform.getScaleInstance(sx, sy));
    }

    @Override
    public void rScale(float sx, float sy) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.scale(sx, sy);
    }

    @Override
    public void postScale(float sx, float sy) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.concatenate(AffineTransform.getScaleInstance(sx, sy));
    }

    @Override
    public void setRotate(float degrees, float px, float py) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.setToRotation(degrees, px, py);
    }

    @Override
    public void setRotate(float degrees) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.setToRotation(degrees);
    }

    @Override
    public void preRotate(float degrees, float px, float py) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.preConcatenate(AffineTransform.getRotateInstance(degrees, px, py));
    }

    @Override
    public void preRotate(float degrees) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.preConcatenate(AffineTransform.getRotateInstance(degrees));
    }

    @Override
    public void rRotate(float degrees, float px, float py) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.rotate(degrees, px, py);
    }

    @Override
    public void rRotate(float degrees) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.rotate(degrees);
    }

    @Override
    public void postRotate(float degrees, float px, float py) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.concatenate(AffineTransform.getRotateInstance(degrees, px, py));
    }

    @Override
    public void postRotate(float degrees) {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.concatenate(AffineTransform.getRotateInstance(degrees));
    }

    @Override
    public void getValues(float[] values, int offset) {
        if (isDisposed()) throw new AlreadyDisposedException();
        values[offset] = (float) transform.getScaleX();
        values[offset + 1] = (float) transform.getShearX();
        values[offset + 2] = (float) transform.getTranslateX();
        values[offset + 3] = (float) transform.getShearY();
        values[offset + 4] = (float) transform.getScaleY();
        values[offset + 5] = (float) transform.getTranslateY();
    }

    @Override
    public void getScale(float[] scale, int offset) {
        if (isDisposed()) throw new AlreadyDisposedException();
        scale[offset] = (float) transform.getScaleX();
        scale[offset + 1] = (float) transform.getScaleY();
    }

    @Override
    public void getSkew(float[] skew, int offset) {
        if (isDisposed()) throw new AlreadyDisposedException();
        skew[offset] = (float) transform.getShearX();
        skew[offset + 1] = (float) transform.getShearY();
    }

    @Override
    public void getTranslate(float[] translate, int offset) {
        if (isDisposed()) throw new AlreadyDisposedException();
        translate[offset] = (float) transform.getTranslateX();
        translate[offset + 1] = (float) transform.getTranslateY();
    }

    @Override
    public Transform clone() {
        if (isDisposed()) throw new AlreadyDisposedException();
        DesktopTransform clone = (DesktopTransform) super.clone();
        if (clone == null) return copy();
        else return clone;
    }

    @Override
    public Transform copy() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return new DesktopTransform((AffineTransform) transform.clone());
    }

    private final AtomicBoolean disposed = new AtomicBoolean(false);

    @Override
    public void dispose() {
        if (disposed.compareAndSet(false, true)) transform = null;
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }

    @Override
    public void reset() {
        if (isDisposed()) throw new AlreadyDisposedException();
        transform.setToIdentity();
    }

    @Override
    public boolean isIdentity() {
        return transform.isIdentity();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DesktopTransform that)) return false;

        return Objects.equals(transform, that.transform);
    }

    @Override
    public int hashCode() {
        return transform != null ? transform.hashCode() : 0;
    }

}
