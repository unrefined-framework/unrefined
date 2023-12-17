package unrefinedx.game2d;

import unrefined.math.FastMath;
import unrefined.media.graphics.Path;
import unrefined.media.graphics.Transform;

public abstract class Viewport {

    public static Viewport ofFixed(float x, float y, float width, float height) {
        return new Fixed(x, y, width, height);
    }

    private static final class Fixed extends Viewport {
        private final float x, y, width, height;
        public Fixed(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        @Override
        public void update(int width, int height) {}
        @Override
        public void mapViewport(Camera camera, Path clip, Transform transform) {
            clip.addRectangle(0, 0, width, height);
            transform.setTranslate(x, y);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Fixed fixed = (Fixed) o;

            if (Float.compare(x, fixed.x) != 0) return false;
            if (Float.compare(y, fixed.y) != 0) return false;
            if (Float.compare(width, fixed.width) != 0) return false;
            return Float.compare(height, fixed.height) == 0;
        }
        @Override
        public int hashCode() {
            int result = (x != 0.0f ? Float.floatToIntBits(x) : 0);
            result = 31 * result + (y != 0.0f ? Float.floatToIntBits(y) : 0);
            result = 31 * result + (width != 0.0f ? Float.floatToIntBits(width) : 0);
            result = 31 * result + (height != 0.0f ? Float.floatToIntBits(height) : 0);
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

    private static abstract class Dynamic extends Viewport {
        protected int width, height;
        @Override
        public void update(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    private static class Alignment extends Dynamic {
        protected final int alignmentX, alignmentY;
        public Alignment(int alignmentX, int alignmentY) {
            this.alignmentX = unrefinedx.game2d.Alignment.X.checkValid(alignmentX);
            this.alignmentY = unrefinedx.game2d.Alignment.Y.checkValid(alignmentY);
        }
        @Override
        public void mapViewport(Camera camera, Path clip, Transform transform) {
            int cameraWidth = camera.getWidth();
            int cameraHeight = camera.getHeight();
            clip.addRectangle(0, 0, cameraWidth, cameraHeight);
            float dx, dy;
            switch (alignmentX) {
                case unrefinedx.game2d.Alignment.X.LEFT: dx = 0; break;
                case unrefinedx.game2d.Alignment.X.CENTER: dx = (width - cameraWidth) / 2f; break;
                case unrefinedx.game2d.Alignment.X.RIGHT: dx = width - cameraWidth; break;
                default: throw new IllegalArgumentException("Illegal alignment: " + alignmentX);
            }
            switch (alignmentY) {
                case unrefinedx.game2d.Alignment.Y.TOP: dy = 0; break;
                case unrefinedx.game2d.Alignment.Y.CENTER: dy = (height - cameraHeight) / 2f; break;
                case unrefinedx.game2d.Alignment.Y.BOTTOM: dy = height - cameraHeight; break;
                default: throw new IllegalArgumentException("Illegal alignment: " + alignmentY);
            }
            transform.translate(dx, dy);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Alignment alignment = (Alignment) o;

            if (alignmentX != alignment.alignmentX) return false;
            return alignmentY == alignment.alignmentY;
        }
        @Override
        public int hashCode() {
            int result = alignmentX;
            result = 31 * result + alignmentY;
            return result;
        }
        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "alignmentX=" + unrefinedx.game2d.Alignment.X.toString(alignmentX) +
                    ", alignmentY=" + unrefinedx.game2d.Alignment.Y.toString(alignmentY) +
                    '}';
        }
    }

    public static Viewport ofAlignment(int alignmentX, int alignmentY) {
        return new Alignment(alignmentX, alignmentY);
    }

    private static final class Stretch extends Alignment {
        private final int stretch;
        public Stretch(int stretch, int alignmentX, int alignmentY) {
            super(alignmentX, alignmentY);
            this.stretch = stretch;
        }
        @Override
        public void mapViewport(Camera camera, Path clip, Transform transform) {
            int cameraWidth = camera.getWidth();
            int cameraHeight = camera.getHeight();
            clip.addRectangle(0, 0, cameraWidth, cameraHeight);
            float dx, dy;
            float sx, sy;
            if ((stretch & unrefinedx.game2d.Stretch.HORIZONTAL) == unrefinedx.game2d.Stretch.HORIZONTAL) {
                dx = 0;
                sx = (float) width / cameraWidth;
            }
            else {
                sx = 1;
                switch (alignmentX) {
                    case unrefinedx.game2d.Alignment.X.LEFT: dx = 0; break;
                    case unrefinedx.game2d.Alignment.X.CENTER: dx = (width - cameraWidth) / 2f; break;
                    case unrefinedx.game2d.Alignment.X.RIGHT: dx = width - cameraWidth; break;
                    default: throw new IllegalArgumentException("Illegal alignment: " + alignmentX);
                }
            }
            if ((stretch & unrefinedx.game2d.Stretch.VERTICAL) == unrefinedx.game2d.Stretch.VERTICAL) {
                dy = 0;
                sy = (float) height / cameraHeight;
            }
            else {
                sy = 1;
                switch (alignmentY) {
                    case unrefinedx.game2d.Alignment.Y.TOP: dy = 0; break;
                    case unrefinedx.game2d.Alignment.Y.CENTER: dy = (height - cameraHeight) / 2f; break;
                    case unrefinedx.game2d.Alignment.Y.BOTTOM: dy = height - cameraHeight; break;
                    default: throw new IllegalArgumentException("Illegal alignment: " + alignmentY);
                }
            }
            transform.translate(dx, dy);
            transform.scale(sx, sy);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            Stretch stretch1 = (Stretch) o;

            return stretch == stretch1.stretch;
        }
        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + stretch;
            return result;
        }
        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "stretch=" + unrefinedx.game2d.Stretch.toString(stretch) +
                    ", alignmentX=" + unrefinedx.game2d.Alignment.X.toString(alignmentX) +
                    ", alignmentY=" + unrefinedx.game2d.Alignment.Y.toString(alignmentY) +
                    '}';
        }
    }

    public static Viewport ofStretch(int stretch, int alignmentX, int alignmentY) {
        stretch = unrefinedx.game2d.Stretch.removeUnusedBits(stretch);
        if (stretch == unrefinedx.game2d.Stretch.NONE) return ofAlignment(alignmentX, alignmentY);
        else return new Stretch(stretch, alignmentX, alignmentY);
    }

    private static final class Scaling extends Alignment {
        private final int scaling;
        public Scaling(int scaling, int alignmentX, int alignmentY) {
            super(alignmentX, alignmentY);
            this.scaling = scaling;
        }
        @Override
        public void mapViewport(Camera camera, Path clip, Transform transform) {
            int cameraWidth = camera.getWidth();
            int cameraHeight = camera.getHeight();
            clip.addRectangle(0, 0, cameraWidth, cameraHeight);
            float dx, dy;
            float scale;
            switch (scaling) {
                case unrefinedx.game2d.Scaling.NONE:
                    scale = 1;
                    switch (alignmentX) {
                        case unrefinedx.game2d.Alignment.X.LEFT: dx = 0; break;
                        case unrefinedx.game2d.Alignment.X.CENTER: dx = (width - cameraWidth * scale) / 2f; break;
                        case unrefinedx.game2d.Alignment.X.RIGHT: dx = width - cameraWidth * scale; break;
                        default: throw new IllegalArgumentException("Illegal alignment: " + alignmentX);
                    }
                    switch (alignmentY) {
                        case unrefinedx.game2d.Alignment.Y.TOP: dy = 0; break;
                        case unrefinedx.game2d.Alignment.Y.CENTER: dy = (height - cameraHeight * scale) / 2f; break;
                        case unrefinedx.game2d.Alignment.Y.BOTTOM: dy = height - cameraHeight * scale; break;
                        default: throw new IllegalArgumentException("Illegal alignment: " + alignmentY);
                    }
                    break;
                case unrefinedx.game2d.Scaling.HORIZONTAL:
                    scale = (float) width / cameraWidth;
                    dx = 0;
                    switch (alignmentY) {
                        case unrefinedx.game2d.Alignment.Y.TOP: dy = 0; break;
                        case unrefinedx.game2d.Alignment.Y.CENTER: dy = (height - cameraHeight * scale) / 2f; break;
                        case unrefinedx.game2d.Alignment.Y.BOTTOM: dy = height - cameraHeight * scale; break;
                        default: throw new IllegalArgumentException("Illegal alignment: " + alignmentY);
                    }
                    break;
                case unrefinedx.game2d.Scaling.VERTICAL:
                    scale = (float) height / cameraHeight;
                    switch (alignmentX) {
                        case unrefinedx.game2d.Alignment.X.LEFT: dx = 0; break;
                        case unrefinedx.game2d.Alignment.X.CENTER: dx = (width - cameraWidth * scale) / 2f; break;
                        case unrefinedx.game2d.Alignment.X.RIGHT: dx = width - cameraWidth * scale; break;
                        default: throw new IllegalArgumentException("Illegal alignment: " + alignmentX);
                    }
                    dy = 0;
                    break;
                case unrefinedx.game2d.Scaling.AUTO:
                    float sx = (float) width / cameraWidth;
                    float sy = (float) height / cameraHeight;
                    if (sx > sy) {
                        scale = sy;
                        switch (alignmentX) {
                            case unrefinedx.game2d.Alignment.X.LEFT: dx = 0; break;
                            case unrefinedx.game2d.Alignment.X.CENTER: dx = (width - cameraWidth * scale) / 2f; break;
                            case unrefinedx.game2d.Alignment.X.RIGHT: dx = width - cameraWidth * scale; break;
                            default: throw new IllegalArgumentException("Illegal alignment: " + alignmentX);
                        }
                        dy = 0;
                    }
                    else {
                        scale = sx;
                        dx = 0;
                        switch (alignmentY) {
                            case unrefinedx.game2d.Alignment.Y.TOP: dy = 0; break;
                            case unrefinedx.game2d.Alignment.Y.CENTER: dy = (height - cameraHeight * scale) / 2f; break;
                            case unrefinedx.game2d.Alignment.Y.BOTTOM: dy = height - cameraHeight * scale; break;
                            default: throw new IllegalArgumentException("Illegal alignment: " + alignmentY);
                        }
                    }
                    break;
                default: throw new IllegalArgumentException("Illegal scaling: " + scaling);
            }
            transform.translate(dx, dy);
            transform.scale(scale, scale);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            Scaling scaling1 = (Scaling) o;

            return scaling == scaling1.scaling;
        }
        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + scaling;
            return result;
        }
        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "scaling=" + unrefinedx.game2d.Scaling.toString(scaling) +
                    ", alignmentX=" + unrefinedx.game2d.Alignment.X.toString(alignmentX) +
                    ", alignmentY=" + unrefinedx.game2d.Alignment.Y.toString(alignmentY) +
                    '}';
        }
    }

    public static Viewport ofScaling(int scaling, int alignmentX, int alignmentY) {
        if (unrefinedx.game2d.Scaling.checkValid(scaling) == unrefinedx.game2d.Scaling.NONE) return ofAlignment(alignmentX, alignmentY);
        else return new Scaling(scaling, alignmentX, alignmentY);
    }

    private static final class Relative extends Dynamic {
        private final float xPercent, yPercent, widthPercent, heightPercent;
        public Relative(float xPercent, float yPercent, float widthPercent, float heightPercent) {
            this.xPercent = FastMath.clamp(xPercent, 0, 1);
            this.yPercent = FastMath.clamp(yPercent, 0, 1);
            this.widthPercent = FastMath.clamp(widthPercent, 0, 1);
            this.heightPercent = FastMath.clamp(heightPercent, 0, 1);
        }
        @Override
        public void mapViewport(Camera camera, Path clip, Transform transform) {
            clip.addRectangle(0, 0, camera.getWidth() * widthPercent, camera.getHeight() * heightPercent);
            transform.translate(width * xPercent, height * yPercent);
            transform.scale((float) width / camera.getWidth(), (float) height / camera.getHeight());
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Relative relative = (Relative) o;

            if (Float.compare(xPercent, relative.xPercent) != 0) return false;
            if (Float.compare(yPercent, relative.yPercent) != 0) return false;
            if (Float.compare(widthPercent, relative.widthPercent) != 0) return false;
            return Float.compare(heightPercent, relative.heightPercent) == 0;
        }
        @Override
        public int hashCode() {
            int result = (xPercent != 0.0f ? Float.floatToIntBits(xPercent) : 0);
            result = 31 * result + (yPercent != 0.0f ? Float.floatToIntBits(yPercent) : 0);
            result = 31 * result + (widthPercent != 0.0f ? Float.floatToIntBits(widthPercent) : 0);
            result = 31 * result + (heightPercent != 0.0f ? Float.floatToIntBits(heightPercent) : 0);
            return result;
        }
        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "xPercent=" + xPercent +
                    ", yPercent=" + yPercent +
                    ", widthPercent=" + widthPercent +
                    ", heightPercent=" + heightPercent +
                    '}';
        }
    }

    public static Viewport ofRelative(float xPercent, float yPercent, float widthPercent, float heightPercent) {
        return new Relative(xPercent, yPercent, widthPercent, heightPercent);
    }

    public abstract void update(int width, int height);
    public abstract void mapViewport(Camera camera, Path clip, Transform transform);

}
