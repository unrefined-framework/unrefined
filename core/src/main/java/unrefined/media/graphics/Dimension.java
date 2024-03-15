package unrefined.media.graphics;

import unrefined.io.BinaryInput;
import unrefined.io.BinaryOutput;
import unrefined.io.BundleInput;
import unrefined.io.BundleOutput;
import unrefined.io.Savable;

import java.io.IOException;

public class Dimension implements Savable {

    private int width;
    private int height;

    public Dimension() {
    }

    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Dimension(Dimension dimension) {
        this.width = dimension.width;
        this.height = dimension.height;
    }

    public void setDimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setDimension(Dimension dimension) {
        this.width = dimension.width;
        this.height = dimension.height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void writePortable(BinaryOutput out) throws IOException {
        out.writeInt(width);
        out.writeInt(height);
    }

    @Override
    public void readPortable(BinaryInput in) throws IOException {
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public void to(Object dst) {
        ((Dimension) dst).setDimension(this);
    }

    @Override
    public void from(Object src) {
        setDimension((Dimension) src);
    }

    @Override
    public void swap(Object o) {
        Dimension that = (Dimension) o;
        int x = that.width;
        int y = that.height;
        that.setDimension(this);
        setDimension(x, y);
    }

    @Override
    public Dimension clone() {
        try {
            return (Dimension) super.clone();
        }
        catch (CloneNotSupportedException e) {
            return copy();
        }
    }

    @Override
    public Dimension copy() {
        return new Dimension(this);
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

        Dimension that = (Dimension) object;

        if (width != that.width) return false;
        return height == that.height;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
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
        out.putInt("width", width);
        out.putInt("height", height);
    }

    @Override
    public void readFromBundle(BundleInput in) throws IOException {
        width = in.getInt("width", 0);
        height = in.getInt("height", 0);
    }

}
