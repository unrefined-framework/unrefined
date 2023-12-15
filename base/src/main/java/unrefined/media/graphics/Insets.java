package unrefined.media.graphics;

import unrefined.io.Portable;
import unrefined.io.PortableInput;
import unrefined.io.PortableOutput;
import unrefined.util.Copyable;
import unrefined.util.Resettable;
import unrefined.util.Swappable;

import java.io.IOException;

public class Insets implements Portable, Copyable, Swappable, Resettable {

    private int left;
    private int top;
    private int right;
    private int bottom;

    public Insets() {
    }

    public Insets(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public Insets(Insets insets) {
        this.left = insets.left;
        this.top = insets.top;
        this.right = insets.right;
        this.bottom = insets.bottom;
    }

    public void setInsets(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void setInsets(Insets insets) {
        this.left = insets.left;
        this.top = insets.top;
        this.right = insets.right;
        this.bottom = insets.bottom;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    @Override
    public void writePortable(PortableOutput out) throws IOException {
        out.writeInt(left);
        out.writeInt(top);
        out.writeInt(right);
        out.writeInt(bottom);
    }

    @Override
    public void readPortable(PortableInput in) throws IOException {
        left = in.readInt();
        top = in.readInt();
        right = in.readInt();
        bottom = in.readInt();
    }

    @Override
    public void to(Object dst) {
        ((Insets) dst).setInsets(this);
    }

    @Override
    public void from(Object src) {
        setInsets((Insets) src);
    }

    @Override
    public void swap(Object o) {
        Insets that = (Insets) o;
        int left = that.left;
        int top = that.top;
        int right = that.right;
        int bottom = that.bottom;
        that.setInsets(this);
        setInsets(left, top, right, bottom);
    }

    @Override
    public Insets clone() {
        try {
            return (Insets) super.clone();
        }
        catch (CloneNotSupportedException e) {
            return copy();
        }
    }

    @Override
    public Insets copy() {
        return new Insets(this);
    }

    @Override
    public void reset() {
        left = top = right = bottom = 0;
    }

    @Override
    public boolean isIdentity() {
        return left == 0 && top == 0 && right == 0 && bottom == 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Insets that = (Insets) object;

        if (left != that.left) return false;
        if (top != that.top) return false;
        if (right != that.right) return false;
        return bottom == that.bottom;
    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + top;
        result = 31 * result + right;
        result = 31 * result + bottom;
        return result;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + '{' +
                "left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                '}';
    }

}
