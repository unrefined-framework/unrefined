package org.example.application.breakout;

import unrefined.media.graphics.Brush;
import unrefined.media.graphics.Color;
import unrefined.media.graphics.Graphics;

public class Ball extends Sprite {

    private static final Brush COLOR = Brush.ofColor(Color.BLACK);

    private int xdir;
    private int ydir;

    public Ball() {
        initBall();
    }

    private void initBall() {
        xdir = 1;
        ydir = -1;

        setSize();
        resetState();
    }

    private void setSize() {
        setWidth(5);
        setHeight(5);
    }

    @Override
    protected void onPaint(Graphics graphics) {
        graphics.setBrush(COLOR);
        graphics.setStyle(Graphics.Style.FILL);
        graphics.drawEllipse(x, y, width, height);
    }

    void move( Board board ) {

        x += xdir;
        y += ydir;

        if (x == 0) {
            board.collision();

            setXDir(1);
        }

        if (x == Constant.WIDTH - width) {
            board.collision();

            //System.out.println(bitmapWidth);
            setXDir(-1);
        }

        if (y == 0) {
            board.collision();

            setYDir(1);
        }

    }

    private void resetState() {
        x = Constant.INIT_BALL_X;
        y = Constant.INIT_BALL_Y;
    }

    void setXDir(int x) {
        xdir = x;
    }

    void setYDir(int y) {
        ydir = y;
    }

    int getYDir() {
        return ydir;
    }

}
