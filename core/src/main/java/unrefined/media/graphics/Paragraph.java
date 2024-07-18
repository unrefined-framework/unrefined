package unrefined.media.graphics;

public abstract class Paragraph {

    public abstract boolean hasNext();

    public abstract void nextLine(float wrappingWidth);

    public abstract int offset();

    public abstract float measureText();

    public abstract void measureText(Text.Metrics metrics, RectangleF bounds);

    public void measureText(Text.Metrics metrics) {
        measureText(metrics, null);
    }

    public void measureText(RectangleF bounds) {
        measureText(null, bounds);
    }

    public abstract void hitText(float xOffset, float yOffset, Text.HitInfo hitInfo);

    public abstract void drawText(float x, float y);
    public abstract void drawText(Transform transform);

}
