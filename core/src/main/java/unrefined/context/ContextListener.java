package unrefined.context;

import unrefined.media.graphics.Bitmap;
import unrefined.media.graphics.Graphics;
import unrefined.media.opengl.GL;

public interface ContextListener {

    void onCreate(Context context);
    void onDispose(Context context);
    void onShow(Context context);
    void onHide(Context context);
    void onFocusGain(Context context);
    void onFocusLost(Context context);
    void onResize(Context context, int width, int height);
    void onMove(Context context, int xOnParent, int yOnParent);

    default void onPaint(Context context, Object canvas, boolean snapshot) {
        if (canvas instanceof Graphics) onPaint(context, (Graphics) canvas, snapshot);
        else if (canvas instanceof GL) onPaint(context, (GL) canvas, snapshot);
        else throw new IllegalArgumentException("Illegal canvas object: " + canvas);
    }

    void onPaint(Context context, Graphics graphics, boolean snapshot);
    void onPaint(Context context, GL gl, boolean snapshot);

    void onSnapshot(Context context, Bitmap snapshot);

    boolean onKeyDown(Context context, String key, String code, int location, int modifiers, boolean repeat);
    boolean onKeyUp(Context context, String key, String code, int location, int modifiers);
    boolean onKeyTyped(Context context, char key, boolean repeat);

    boolean onPointerClick(Context context, float xOffset, float yOffset, int modifiers, int id, int button);
    boolean onPointerDown(Context context, float xOffset, float yOffset, int modifiers, int id, int button);
    boolean onPointerUp(Context context, float xOffset, float yOffset, int modifiers, int id, int button);
    boolean onPointerDrag(Context context, float xOffset, float yOffset, int modifiers, int id);

    boolean onTouchLongPress(Context context, float xOffset, float yOffset, int modifiers, int button);

    boolean onMouseMove(Context context, float xOffset, float yOffset, int modifiers);
    boolean onMouseEnter(Context context, float xOffset, float yOffset, int modifiers);
    boolean onMouseExit(Context context, float xOffset, float yOffset, int modifiers);

    boolean onScroll(Context context, float amountX, float amountY, int type);

}
