package org.example.application.breakout;

import unrefined.media.graphics.Brush;
import unrefined.media.graphics.Color;
import unrefined.media.graphics.Graphics;

public class Paddle extends Sprite {

    private static final Brush COLOR = Brush.ofColor(Color.DARK_BLUE);

    private int dx;

    public Paddle() {
        initPaddle();        
    }
    
    private void initPaddle() {
        setSize();

        resetState();
    }

    private void setSize() {
        setWidth(40);
        setHeight(10);
    }

    @Override
    protected void onPaint(Graphics graphics) {
        graphics.setBrush(COLOR);
        graphics.setStyle(Graphics.Style.FILL);
        graphics.drawRoundRectangle(x, y, width, height, 10);
    }

    void move() {

        x += dx;

        if (x <= 0) {
            x = 0;
        }

        if (x >= Constant.WIDTH - width) {
            x = Constant.WIDTH - width;
        }

    }

    boolean onKeyDown(String code) {

        switch (code) {
            case "KeyA":
            case "ArrowLeft":
                dx = -1;
                return true;
            case "KeyD":
            case "ArrowRight":
                dx = 1;
                return true;
        }

        return false;

    }

    boolean onKeyUp(String code) {

        switch (code) {
            case "KeyA":
            case "ArrowLeft":
                dx = 0;
                return true;
            case "KeyD":
            case "ArrowRight":
                dx = 0;
                return true;
        }

        return false;

    }

    private void resetState() {
        x = Constant.INIT_PADDLE_X;
        y = Constant.INIT_PADDLE_Y;
    }

}
