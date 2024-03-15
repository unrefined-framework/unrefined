package unrefined.context;

public abstract class ContainerAdapter implements ContainerListener {

    @Override
    public void onCreate(Container container) {

    }

    @Override
    public void onDispose(Container container) {

    }

    @Override
    public void onStart(Container container) {

    }

    @Override
    public void onStop(Container container) {

    }

    @Override
    public void onResume(Container container) {

    }

    @Override
    public void onPause(Container container) {

    }

    @Override
    public void onShow(Container container) {

    }

    @Override
    public void onHide(Container container) {

    }

    @Override
    public void onResize(Container container, int width, int height) {

    }

    @Override
    public void onMove(Container container, int xOnScreen, int yOnScreen) {

    }

    @Override
    public void onKeyDown(Container container, String key, String code, int location, int modifiers, boolean repeat) {

    }

    @Override
    public void onKeyUp(Container container, String key, String code, int location, int modifiers) {

    }

    @Override
    public void onKeyTyped(Container container, char key, boolean repeat) {

    }

    @Override
    public void onPointerClick(Container container, float xOffset, float yOffset, int modifiers, int id, int button) {

    }

    @Override
    public void onPointerDown(Container container, float xOffset, float yOffset, int modifiers, int id, int button) {

    }

    @Override
    public void onPointerUp(Container container, float xOffset, float yOffset, int modifiers, int id, int button) {

    }

    @Override
    public void onPointerDrag(Container container, float xOffset, float yOffset, int modifiers, int id) {

    }

    @Override
    public void onMouseMove(Container container, float xOffset, float yOffset, int modifiers) {

    }

    @Override
    public void onTouchLongPress(Container container, float xOffset, float yOffset, int modifiers, int button) {

    }

    @Override
    public void onMouseEnter(Container container, float xOffset, float yOffset, int modifiers) {

    }

    @Override
    public void onMouseExit(Container container, float xOffset, float yOffset, int modifiers) {

    }

    @Override
    public void onScroll(Container container, float amountX, float amountY, int type) {

    }

    @Override
    public void onContextAdd(Container container, Context context) {

    }

    @Override
    public void onContextRemove(Container container, Context context) {

    }

    @Override
    public boolean onCloseRequest(Container container) {
        return true;
    }

}
