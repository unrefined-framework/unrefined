package org.example.application.breakout;

import unrefined.media.graphics.Graphics;
import unrefined.media.graphics.Rectangle;

public abstract class Sprite {

    int x;
    int y;
    int width;
    int height;
    
    protected void setX(int x) {
        this.x = x;
    }

    int getX() {
        return x;
    }

    protected void setY(int y) {
        this.y = y;
    }

    int getY() {
        return y;
    }

    protected void setWidth(int width) {
        this.width = width;
    }

    int getWidth() {
        return width;
    }

    protected void setHeight(int height) {
        this.height = height;
    }

    int getHeight() {
        return height;
    }

    Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }

    protected abstract void onPaint(Graphics graphics);

}
