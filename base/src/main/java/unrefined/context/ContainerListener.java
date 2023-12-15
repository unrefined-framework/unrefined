package unrefined.context;

public interface ContainerListener {

    void onCreate(Container container);
    void onDispose(Container container);
    void onStart(Container container);
    void onStop(Container container);
    void onResume(Container container);
    void onPause(Container container);
    void onShow(Container container);
    void onHide(Container container);
    void onResize(Container container, int width, int height);
    void onMove(Container container, int xOnScreen, int yOnScreen);

    void onKeyDown(Container container, String key, String code, int location, int modifiers, boolean repeat);
    void onKeyUp(Container container, String key, String code, int location, int modifiers);
    void onKeyTyped(Container container, char key, boolean repeat);

    void onPointerDown(Container container, float xOffset, float yOffset, int modifiers, int id, int button);
    void onPointerUp(Container container, float xOffset, float yOffset, int modifiers, int id, int button);
    void onPointerDrag(Container container, float xOffset, float yOffset, int modifiers, int id);

    void onMouseMove(Container container, float xOffset, float yOffset, int modifiers);
    void onMouseEnter(Container container, float xOffset, float yOffset, int modifiers);
    void onMouseExit(Container container, float xOffset, float yOffset, int modifiers);

    void onScroll(Container container, float amountX, float amountY, int type);

    void onContextAdd(Container container, Context context);
    void onContextRemove(Container container, Context context);

    boolean onCloseRequest(Container container);

}
