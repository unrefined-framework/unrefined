package unrefined.media.graphics;

import unrefined.io.Portable;
import unrefined.io.PortableInput;
import unrefined.io.PortableOutput;
import unrefined.util.Copyable;
import unrefined.util.Resettable;
import unrefined.util.Swappable;

import java.io.IOException;

public class Rectangle implements Portable, Copyable, Swappable, Resettable {

    private int x;
    private int y;
    private int width;
    private int height;

    public Rectangle() {
    }

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle(Rectangle rectangle) {
        this.x = rectangle.x;
        this.y = rectangle.y;
        this.width = rectangle.width;
        this.height = rectangle.height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void getPosition(Point position) {
        position.setPoint(x, y);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void getSize(Dimension size) {
        size.setDimension(width, height);
    }

    public void setRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setRectangle(Rectangle rectangle) {
        this.x = rectangle.x;
        this.y = rectangle.y;
        this.width = rectangle.width;
        this.height = rectangle.height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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

    public boolean contains(int x, int y) {
        return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
    }

    public boolean contains(float x, float y) {
        return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
    }

    @Override
    public void writePortable(PortableOutput out) throws IOException {
        out.writeInt(x);
        out.writeInt(y);
        out.writeInt(width);
        out.writeInt(height);
    }

    @Override
    public void readPortable(PortableInput in) throws IOException {
        x = in.readInt();
        y = in.readInt();
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public void to(Object dst) {
        ((Rectangle) dst).setRectangle(this);
    }

    @Override
    public void from(Object src) {
        setRectangle((Rectangle) src);
    }

    @Override
    public void swap(Object o) {
        Rectangle that = (Rectangle) o;
        int x = that.x;
        int y = that.y;
        int width = that.width;
        int height = that.height;
        that.setRectangle(this);
        setRectangle(x, y, width, height);
    }

    @Override
    public Rectangle clone() {
        try {
            return (Rectangle) super.clone();
        }
        catch (CloneNotSupportedException e) {
            return copy();
        }
    }

    @Override
    public Rectangle copy() {
        return new Rectangle(this);
    }

    @Override
    public void reset() {
        x = y = width = height = 0;
    }

    @Override
    public boolean isIdentity() {
        return x == 0 && y == 0 && width == 0 && height == 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Rectangle rectangle = (Rectangle) object;

        if (x != rectangle.x) return false;
        if (y != rectangle.y) return false;
        if (width != rectangle.width) return false;
        return height == rectangle.height;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

}
