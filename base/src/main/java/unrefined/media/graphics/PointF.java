package unrefined.media.graphics;

import unrefined.io.BinaryInput;
import unrefined.io.BinaryOutput;
import unrefined.io.BundleInput;
import unrefined.io.BundleOutput;
import unrefined.io.Savable;

import java.io.IOException;

public class PointF implements Savable {

    private float x;
    private float y;

    public PointF() {
    }

    public PointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointF(PointF point) {
        this.x = point.x;
        this.y = point.y;
    }

    public void setPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setPoint(PointF point) {
        this.x = point.x;
        this.y = point.y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void writePortable(BinaryOutput out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
    }

    @Override
    public void readPortable(BinaryInput in) throws IOException {
        x = in.readFloat();
        y = in.readFloat();
    }

    @Override
    public void to(Object dst) {
        ((PointF) dst).setPoint(this);
    }

    @Override
    public void from(Object src) {
        setPoint((PointF) src);
    }

    @Override
    public void swap(Object o) {
        PointF that = (PointF) o;
        float x = that.x;
        float y = that.y;
        that.setPoint(this);
        setPoint(x, y);
    }

    @Override
    public PointF clone() {
        try {
            return (PointF) super.clone();
        }
        catch (CloneNotSupportedException e) {
            return copy();
        }
    }

    @Override
    public PointF copy() {
        return new PointF(this);
    }

    @Override
    public void reset() {
        x = y = 0;
    }

    @Override
    public boolean isIdentity() {
        return x == 0 && y == 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        PointF that = (PointF) object;

        if (Float.compare(x, that.x) != 0) return false;
        return Float.compare(y, that.y) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != 0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != 0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public void writeToBundle(BundleOutput out) throws IOException {
        out.putFloat("x", x);
        out.putFloat("y", y);
    }

    @Override
    public void readFromBundle(BundleInput in) throws IOException {
        x = in.getFloat("x", 0);
        y = in.getFloat("y", 0);
    }

}
