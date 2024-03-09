package org.example.application.breakout;

import unrefined.media.graphics.Brush;
import unrefined.media.graphics.Color;
import unrefined.media.graphics.Graphics;

public class Brick extends Sprite {

    private static final Brush COLOR = Brush.ofColor(Color.FIRE_BRICK);

    private boolean destroyed;

    public Brick(int x, int y) {
        initBrick(x, y);
    }
    
    private void initBrick(int x, int y) {
        this.x = x;
        this.y = y;
        
        destroyed = false;

        setSize();
    }
    
    private void setSize() {
        setWidth(40);
        setHeight(10);
    }

    @Override
    protected void onPaint(Graphics graphics) {
        graphics.setBrush(COLOR);
        graphics.setStyle(Graphics.Style.FILL);
        graphics.drawRectangle(x + 0.5f, y + 0.5f, width - 1, height - 1);
    }

    boolean isDestroyed() {
        return destroyed;
    }

    void setDestroyed(boolean val) {
        destroyed = val;
    }

}
