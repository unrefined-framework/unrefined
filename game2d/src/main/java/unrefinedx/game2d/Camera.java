package unrefinedx.game2d;

import unrefined.media.graphics.Dimension;
import unrefined.media.graphics.Rectangle;

public class Camera {

    private int x;
    private int y;
    private int width;
    private int height;

    public Camera(int width, int height) {
        this.width = width;
        this.height = height;
        lookAt(0, 0);
    }

    public Camera(Camera camera) {
        this.x = camera.x;
        this.y = camera.y;
        this.width = camera.width;
        this.height = camera.height;
    }

    public void getSize(Dimension size) {
        size.setDimension(width, height);
    }

    public void setCamera(Camera camera) {
        this.x = camera.x;
        this.y = camera.y;
        this.width = camera.width;
        this.height = camera.height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void lookAt(int x, int y) {
        this.x = x - width / 2;
        this.y = y - height / 2;
    }

    public void lookAt(int x, int y, int alignmentX, int alignmentY) {
        switch (alignmentX) {
            case Alignment.X.LEFT: this.x = x; break;
            case Alignment.X.CENTER: this.x = x - width / 2; break;
            case Alignment.X.RIGHT: this.x = x - width; break;
            default: throw new IllegalArgumentException("Illegal alignment: " + alignmentX);
        }
        switch (alignmentY) {
            case Alignment.Y.TOP: this.y = y; break;
            case Alignment.Y.CENTER: this.y = y - width / 2; break;
            case Alignment.Y.BOTTOM: this.y = y - width; break;
            default: throw new IllegalArgumentException("Illegal alignment: " + alignmentY);
        }
    }

    public void getBounds(Rectangle bounds) {
        bounds.setRectangle(x, y, width, height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Camera camera = (Camera) o;

        if (x != camera.x) return false;
        if (y != camera.y) return false;
        if (width != camera.width) return false;
        return height == camera.height;
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
                "x=" + (x + width / 2) +
                ", y=" + (y + width / 2) +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

}
