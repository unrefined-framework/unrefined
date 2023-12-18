package unrefinedx.game2d;

import unrefined.context.Container;
import unrefined.context.ContainerListener;
import unrefined.context.Context;
import unrefined.media.graphics.Bitmap;
import unrefined.media.graphics.Drawing;
import unrefined.media.graphics.Insets;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Stage implements ContainerListener {

    private volatile Context context;
    private volatile Scene scene;
    private volatile Insets insets;
    private volatile Bitmap frameBuffer;

    public Bitmap adjustFrameBuffer(Camera camera) {
        if (frameBuffer != null && (frameBuffer.getWidth() < camera.getWidth() || frameBuffer.getHeight() < camera.getHeight())) frameBuffer.dispose();
        if (frameBuffer == null || frameBuffer.isDisposed())
            frameBuffer = Drawing.getDrawing().createBitmap(camera.getWidth(), camera.getHeight(), Bitmap.Type.RGBA_8888);
        return frameBuffer;
    }

    public Bitmap trimFrameBuffer(Camera camera) {
        if (frameBuffer != null && (frameBuffer.getWidth() != camera.getWidth() || frameBuffer.getHeight() != camera.getHeight())) frameBuffer.dispose();
        if (frameBuffer == null || frameBuffer.isDisposed()) frameBuffer = Drawing.getDrawing().createBitmap(camera.getWidth(), camera.getHeight(), Bitmap.Type.RGBA_8888);
        return frameBuffer;
    }

    public Stage() {
    }

    public Stage(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        context.setContextListener(this.scene = scene);
    }

    @Override
    public void onCreate(Container container) {
        insets = new Insets();
        context = container.createContext();
        container.addContext(context);
        if (scene != null && context.getContextListener() != scene) context.setContextListener(scene);
    }

    @Override
    public void onDispose(Container container) {
        insets = null;
        if (frameBuffer != null) frameBuffer.dispose();
    }

    @Override
    public void onStart(Container container) {
        Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("UXGL Game2D Main Loop Thread");
            return thread;
        }).scheduleAtFixedRate(() -> {
            if (scene != null) container.requestPaint();
        }, 0, 1000_000_000L / 60, TimeUnit.NANOSECONDS);
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
        if (context == null) return;
        container.getInsets(insets);
        context.setPosition(insets.getLeft(), insets.getTop());
        context.setSize(width - insets.getLeft() - insets.getRight(),
                height - insets.getTop() - insets.getBottom());
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
    public void onTouchLongPress(Container container, float xOffset, float yOffset, int modifiers, int button) {

    }

    @Override
    public void onMouseMove(Container container, float xOffset, float yOffset, int modifiers) {

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
