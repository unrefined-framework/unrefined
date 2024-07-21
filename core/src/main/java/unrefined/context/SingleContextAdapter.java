package unrefined.context;

import unrefined.media.graphics.Insets;

import java.util.Objects;

public abstract class SingleContextAdapter extends ContainerAdapter {

    private Context context;
    private Insets insets;

    private final String type;
    private final ContextListener contextListener;

    public SingleContextAdapter(String type, ContextListener contextListener) {
        this.type = type;
        this.contextListener = Objects.requireNonNull(contextListener);
    }

    public SingleContextAdapter(ContextListener contextListener) {
        this.type = null;
        this.contextListener = Objects.requireNonNull(contextListener);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onCreate(Container container) {
        context = container.createContext(type, contextListener);
        insets = new Insets();

        container.addContext(context);
    }

    @Override
    public void onDispose(Container container) {
        context = null;
        insets = null;
    }

    @Override
    public void onResize(Container container, int width, int height) {
        container.getInsets(insets);
        context.setPosition(insets.getLeft(), insets.getTop());
        context.setSize(
                container.getWidth() - insets.getLeft() - insets.getRight(),
                container.getHeight() - insets.getTop() - insets.getBottom());
    }

    @Override
    public void onResume(Container container) {
        context.requestFocus();
    }

}
