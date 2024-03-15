package org.example.helloworld;

import unrefined.context.Context;
import unrefined.context.ContextAdapter;
import unrefined.media.graphics.Brush;
import unrefined.media.graphics.Color;
import unrefined.media.graphics.Graphics;
import unrefined.media.graphics.RectangleF;
import unrefined.media.graphics.Text;

/**
 * This is the 'canvas', all drawing logic are here.
 */
public class HelloWorldContext extends ContextAdapter {

    private static final String TXT_HELLO_WORLD = "Hello, World!";

    private Text.Metrics metrics;
    private RectangleF bounds;
    private Brush brush;

    @Override
    public void onCreate(Context context) {
        metrics = new Text.Metrics();
        bounds = new RectangleF();
        brush = Brush.ofColor(Color.BLACK);
    }

    @Override
    public void onDispose(Context context) {
        metrics = null;
        bounds = null;
        brush = null;
    }

    @Override
    public void onPaint(Context context, Graphics graphics, boolean snapshot) {
        graphics.save();
        try {
            graphics.setStyle(Graphics.Style.FILL);
            graphics.setBrush(brush);
            graphics.setTextSize(context.getContainer().sppt(12));
            graphics.measureText(TXT_HELLO_WORLD, metrics, bounds);
            graphics.drawText(TXT_HELLO_WORLD,
                    (graphics.getWidth() - bounds.getWidth()) / 2,
                    (graphics.getHeight() - bounds.getHeight()) / 2 + metrics.getAscent());
        }
        finally {
            graphics.restore();
        }
    }

}
