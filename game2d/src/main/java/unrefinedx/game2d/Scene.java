package unrefinedx.game2d;

import unrefined.context.Context;
import unrefined.context.ContextListener;
import unrefined.media.graphics.Bitmap;
import unrefined.media.graphics.Drawing;
import unrefined.media.graphics.Graphics;
import unrefined.media.graphics.Path;
import unrefined.media.graphics.Rectangle;
import unrefined.media.graphics.Transform;
import unrefined.util.UnexpectedError;

import java.util.ArrayList;
import java.util.List;

public class Scene implements ContextListener {

    private final List<Sprite> sprites = new ArrayList<>();

    public List<Sprite> sprites() {
        return sprites;
    }

    private volatile Viewport viewport;
    private volatile Camera camera;
    private volatile boolean enter = false;
    private volatile boolean down = false;
    private volatile Bitmap buffer;

    private volatile Rectangle bounds;
    private volatile float[] point;
    private volatile Transform invert;
    private volatile Path area;
    private volatile Path clip;
    private volatile Transform transform;

    public Scene() {
    }

    public Scene(Camera camera, Viewport viewport) {
        setCamera(camera);
        setViewport(viewport);
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        if (buffer != null && (buffer.getWidth() < camera.getWidth() || buffer.getHeight() < camera.getHeight())) buffer.dispose();
        if (buffer == null || buffer.isDisposed())
            buffer = Drawing.getDrawing().createBitmap(camera.getWidth(), camera.getHeight(), Bitmap.Type.RGBA_8888);
    }

    @Override
    public void onCreate(Context context) {
        bounds = new Rectangle();
        point = new float[2];
        invert = Drawing.getDrawing().createTransform();
        area = Drawing.getDrawing().createPath();
        clip = Drawing.getDrawing().createPath();
        transform = Drawing.getDrawing().createTransform();
    }

    @Override
    public void onDispose(Context context) {
        if (buffer != null) buffer.dispose();
        invert.dispose();
        area.dispose();
        clip.dispose();
        transform.dispose();
    }

    @Override
    public void onShow(Context context) {

    }

    @Override
    public void onHide(Context context) {

    }

    @Override
    public void onFocusGain(Context context) {

    }

    @Override
    public void onFocusLost(Context context) {

    }

    @Override
    public void onResize(Context context, int width, int height) {
        if (viewport != null) {
            viewport.update(width, height);
            clip.reset();
            transform.reset();
            viewport.mapViewport(camera, clip, transform);
        }
    }

    @Override
    public void onMove(Context context, int xOnParent, int yOnParent) {

    }

    @Override
    public void onPaint(Context context, Graphics graphics, boolean snapshot) {
        if (camera != null) {
            graphics.clearColor();
            area.reset();
            camera.getBounds(bounds);
            area.addRectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
            Graphics canvas = buffer.getGraphics();
            canvas.clearColor();
            canvas.setClip(area);
            for (Sprite sprite : sprites) {
                if (sprite != null) {
                    canvas.save();
                    sprite.onPaint(this, canvas, snapshot);
                    canvas.restore();
                }
            }
            if (viewport != null) {
                graphics.setTransform(transform);
                graphics.setClip(clip);
            }
            graphics.drawBitmap(buffer);
        }
    }

    @Override
    public void onSnapshot(Context context, Bitmap snapshot) {

    }

    @Override
    public boolean onKeyDown(Context context, String key, String code, int location, int modifiers) {
        if (camera == null || !enter) return false;
        for (Sprite sprite : sprites) {
            if (sprite != null && sprite.onKeyDown(this, key, code, location, modifiers)) return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(Context context, String key, String code, int location, int modifiers) {
        if (camera == null || !enter) return false;
        for (Sprite sprite : sprites) {
            if (sprite != null && sprite.onKeyUp(this, key, code, location, modifiers)) return true;
        }
        return false;
    }

    @Override
    public boolean onKeyTyped(Context context, char key) {
        if (camera == null || !enter) return false;
        for (Sprite sprite : sprites) {
            if (sprite != null && sprite.onKeyTyped(this, key)) return true;
        }
        return false;
    }

    @Override
    public boolean onPointerDown(Context context, float xOffset, float yOffset, int modifiers, int id, int button) {
        if (camera == null || !enter) return false;
        boolean transform = viewport != null;
        if (transform) {
            invert.from(this.transform);
            if (!invert.invert()) throw new UnexpectedError("Failed to invert the viewport transform: " + this.transform);
        }
        for (Sprite sprite : sprites) {
            if (sprite != null) {
                float x, y;
                if (transform) {
                    invert.transformPoint(xOffset - sprite.getX(), yOffset - sprite.getY(), point);
                    x = point[0];
                    y = point[1];
                }
                else {
                    x = xOffset - sprite.getX();
                    y = yOffset - sprite.getY();
                }
                if (sprite.onPointerDown(this, x, y, modifiers, id, button)) return true;
            }
        }
        down = true;
        return false;
    }

    @Override
    public boolean onPointerUp(Context context, float xOffset, float yOffset, int modifiers, int id, int button) {
        if (camera == null || !down) return false;
        boolean transform = viewport != null;
        if (transform) {
            invert.from(this.transform);
            if (!invert.invert()) throw new UnexpectedError("Failed to invert the viewport transform: " + this.transform);
        }
        for (Sprite sprite : sprites) {
            if (sprite != null) {
                float x, y;
                if (transform) {
                    invert.transformPoint(xOffset - sprite.getX(), yOffset - sprite.getY(), point);
                    x = point[0];
                    y = point[1];
                }
                else {
                    x = xOffset - sprite.getX();
                    y = yOffset - sprite.getY();
                }
                if (sprite.onPointerUp(this, x, y, modifiers, id, button)) return true;
            }
        }
        down = false;
        return false;
    }

    @Override
    public boolean onPointerDrag(Context context, float xOffset, float yOffset, int modifiers, int id) {
        if (camera == null) return false;
        else if (!down) return onMouseMove(context, xOffset, yOffset, modifiers);
        boolean transform = viewport != null;
        for (Sprite sprite : sprites) {
            if (sprite != null) {
                float x, y;
                if (transform) {
                    invert.transformPoint(xOffset - sprite.getX(), yOffset - sprite.getY(), point);
                    x = point[0];
                    y = point[1];
                }
                else {
                    x = xOffset - sprite.getX();
                    y = yOffset - sprite.getY();
                }
                if (sprite.onPointerDrag(this, x, y, modifiers, id)) return true;
            }
        }
        onMouseMove(context, xOffset, yOffset, modifiers);
        return false;
    }

    @Override
    public boolean onMouseMove(Context context, float xOffset, float yOffset, int modifiers) {
        if (camera == null) return false;
        camera.getBounds(bounds);
        boolean transform = viewport != null;
        if (transform) {
            invert.from(this.transform);
            if (!invert.invert()) throw new UnexpectedError("Failed to invert the viewport transform: " + this.transform);
        }
        for (Sprite sprite : sprites) {
            if (sprite != null) {
                float x, y;
                if (transform) {
                    invert.transformPoint(xOffset - sprite.getX(), yOffset - sprite.getY(), point);
                    x = point[0];
                    y = point[1];
                }
                else {
                    x = xOffset - sprite.getX();
                    y = yOffset - sprite.getY();
                }
                boolean enter = bounds.contains(x, y);
                if (this.enter) {
                    if (!enter) {
                        this.enter = false;
                        if (sprite.onMouseExit(this, x, y, modifiers)) return false;
                    }
                }
                else if (enter) {
                    this.enter = true;
                    if (sprite.onMouseEnter(this, x, y, modifiers)) return false;
                }
                if (this.enter && sprite.onMouseMove(this, x, y, modifiers)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMouseEnter(Context context, float xOffset, float yOffset, int modifiers) {
        if (camera == null) return false;
        camera.getBounds(bounds);
        boolean transform = viewport != null;
        if (transform) {
            invert.from(this.transform);
            if (!invert.invert()) throw new UnexpectedError("Failed to invert the viewport transform: " + this.transform);
        }
        for (Sprite sprite : sprites) {
            if (sprite != null) {
                float x, y;
                if (transform) {
                    invert.transformPoint(xOffset - sprite.getX(), yOffset - sprite.getY(), point);
                    x = point[0];
                    y = point[1];
                }
                else {
                    x = xOffset - sprite.getX();
                    y = yOffset - sprite.getY();
                }
                if (bounds.contains(x, y)) enter = true;
                if (enter && sprite.onMouseEnter(this, x, y, modifiers)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMouseExit(Context context, float xOffset, float yOffset, int modifiers) {
        if (camera == null) return false;
        camera.getBounds(bounds);
        boolean transform = viewport != null;
        if (transform) {
            invert.from(this.transform);
            if (!invert.invert()) throw new UnexpectedError("Failed to invert the viewport transform: " + this.transform);
        }
        for (Sprite sprite : sprites) {
            if (sprite != null) {
                float x, y;
                if (transform) {
                    invert.transformPoint(xOffset - sprite.getX(), yOffset - sprite.getY(), point);
                    x = point[0];
                    y = point[1];
                }
                else {
                    x = xOffset - sprite.getX();
                    y = yOffset - sprite.getY();
                }
                if (!bounds.contains(x, y)) enter = false;
                if (!enter && sprite.onMouseExit(this, x, y, modifiers)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean onScroll(Context context, float amountX, float amountY, int type) {
        if (camera == null || !enter) return false;
        for (Sprite sprite : sprites) {
            if (sprite != null && sprite.onScroll(this, amountX, amountY, type)) return true;
        }
        return false;
    }

}
