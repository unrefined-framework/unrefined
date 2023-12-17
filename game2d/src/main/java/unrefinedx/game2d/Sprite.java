package unrefinedx.game2d;

import unrefined.media.graphics.Graphics;

public abstract class Sprite {

    private float x;
    private float y;

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

    public abstract void onAnimate(Scene scene);
    public abstract void onPaint(Scene scene, Graphics graphics, boolean snapshot);

    public boolean onKeyDown(Scene scene, String key, String code, int location, int modifiers) {
        return false;
    }
    
    public boolean onKeyUp(Scene scene, String key, String code, int location, int modifiers) {
        return false;
    }
    
    public boolean onKeyTyped(Scene scene, char key) {
        return false;
    }
    
    public boolean onPointerDown(Scene scene, float xOffset, float yOffset, int modifiers, int id, int button) {
        return false;
    }
    
    public boolean onPointerUp(Scene scene, float xOffset, float yOffset, int modifiers, int id, int button) {
        return false;
    }
    
    public boolean onPointerDrag(Scene scene, float xOffset, float yOffset, int modifiers, int id) {
        return false;
    }
    
    public boolean onMouseMove(Scene scene, float xOffset, float yOffset, int modifiers) {
        return false;
    }

    public boolean onMouseEnter(Scene scene, float xOffset, float yOffset, int modifiers) {
        return false;
    }

    public boolean onMouseExit(Scene scene, float xOffset, float yOffset, int modifiers) {
        return false;
    }
    
    public boolean onScroll(Scene scene, float amountX, float amountY, int type) {
        return false;
    }
    
}
