package unrefined.context;

import unrefined.media.graphics.Bitmap;
import unrefined.media.graphics.Graphics;

public abstract class ContextAdapter implements ContextListener {

    @Override
    public void onCreate(Context context) {

    }

    @Override
    public void onDispose(Context context) {

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

    }

    @Override
    public void onMove(Context context, int xOnParent, int yOnParent) {

    }

    @Override
    public void onPaint(Context context, Graphics graphics, boolean snapshot) {

    }

    @Override
    public void onSnapshot(Context context, Bitmap snapshot) {

    }

    @Override
    public boolean onKeyDown(Context context, String key, String code, int location, int modifiers, boolean repeat) {
        return false;
    }

    @Override
    public boolean onKeyUp(Context context, String key, String code, int location, int modifiers) {
        return false;
    }

    @Override
    public boolean onKeyTyped(Context context, char key, boolean repeat) {
        return false;
    }

    @Override
    public boolean onPointerClick(Context context, float xOffset, float yOffset, int modifiers, int id, int button) {
        return false;
    }

    @Override
    public boolean onPointerDown(Context context, float xOffset, float yOffset, int modifiers, int id, int button) {
        return false;
    }

    @Override
    public boolean onPointerUp(Context context, float xOffset, float yOffset, int modifiers, int id, int button) {
        return false;
    }

    @Override
    public boolean onPointerDrag(Context context, float xOffset, float yOffset, int modifiers, int id) {
        return false;
    }

    @Override
    public boolean onTouchLongPress(Context context, float xOffset, float yOffset, int modifiers, int button) {
        return false;
    }

    @Override
    public boolean onMouseMove(Context context, float xOffset, float yOffset, int modifiers) {
        return false;
    }

    @Override
    public boolean onMouseEnter(Context context, float xOffset, float yOffset, int modifiers) {
        return false;
    }

    @Override
    public boolean onMouseExit(Context context, float xOffset, float yOffset, int modifiers) {
        return false;
    }

    @Override
    public boolean onScroll(Context context, float amountX, float amountY, int type) {
        return false;
    }

}
