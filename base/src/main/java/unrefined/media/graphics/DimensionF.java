package unrefined.media.graphics;

import unrefined.io.BinaryInput;
import unrefined.io.BinaryOutput;
import unrefined.io.BundleInput;
import unrefined.io.BundleOutput;
import unrefined.io.Savable;

import java.io.IOException;

public class DimensionF implements Savable {

    private float width;
    private float height;

    public DimensionF() {
    }

    public DimensionF(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public DimensionF(DimensionF dimension) {
        this.width = dimension.width;
        this.height = dimension.height;
    }

    public void setDimension(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void setDimension(DimensionF point) {
        this.width = point.width;
        this.height = point.height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public void writePortable(BinaryOutput out) throws IOException {
        out.writeFloat(width);
        out.writeFloat(height);
    }

    @Override
    public void readPortable(BinaryInput in) throws IOException {
        width = in.readFloat();
        height = in.readFloat();
    }

    @Override
    public void to(Object dst) {
        ((DimensionF) dst).setDimension(this);
    }

    @Override
    public void from(Object src) {
        setDimension((DimensionF) src);
    }

    @Override
    public void swap(Object o) {
        DimensionF that = (DimensionF) o;
        float x = that.width;
        float y = that.height;
        that.setDimension(this);
        setDimension(x, y);
    }

    @Override
    public DimensionF clone() {
        try {
            return (DimensionF) super.clone();
        }
        catch (CloneNotSupportedException e) {
            return copy();
        }
    }

    @Override
    public DimensionF copy() {
        return new DimensionF(this);
    }

    @Override
    public void reset() {
        width = height = 0;
    }

    @Override
    public boolean isIdentity() {
        return width == 0 && height == 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DimensionF that = (DimensionF) object;

        if (Float.compare(width, that.width) != 0) return false;
        return Float.compare(height, that.height) == 0;
    }

    @Override
    public int hashCode() {
        int result = (width != 0.0f ? Float.floatToIntBits(width) : 0);
        result = 31 * result + (height != 0.0f ? Float.floatToIntBits(height) : 0);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "width=" + width +
                ", height=" + height +
                '}';
    }

    @Override
    public void writeToBundle(BundleOutput out) throws IOException {
        out.putFloat("width", width);
        out.putFloat("height", height);
    }

    @Override
    public void readFromBundle(BundleInput in) throws IOException {
        width = in.getFloat("width", 0);
        height = in.getFloat("height", 0);
    }

}
